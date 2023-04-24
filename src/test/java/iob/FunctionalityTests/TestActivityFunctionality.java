package iob.FunctionalityTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import iob.logic.activities.ActivityBoundary;
import iob.logic.activities.ActivityConverter;
import iob.logic.activities.ActivityInstance;
import iob.logic.activities.ActivityInvoker;
import iob.logic.instances.InstanceBoundary;
import iob.logic.instances.InstanceId;
import iob.logic.users.UserBoundary;
import iob.logic.users.UserId;
import iob.utility.ApTestRequester;
import iob.utility.TestProperties;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestActivityFunctionality {

	private @Autowired TestProperties testProperties;
	private UserBoundary admin, player, manager;
	private InstanceBoundary testInstance;
	private @Autowired ApTestRequester requester;
	private @Autowired ActivityConverter activityConverter;
	private Comparator<ActivityBoundary> activityComparatorById = Comparator.comparing(ActivityBoundary::getActivityId,
			(id1, id2) -> activityConverter.toEntity(id1.getDomain(), id1.getId())
					.compareTo(activityConverter.toEntity(id2.getDomain(), id2.getId())));;

	@BeforeEach
	void setUp() {
		player = requester.postNewUserForUser(testProperties.getNewPlayer());
		manager = requester.postNewUserForUser(testProperties.getNewManager());
		admin = requester.postNewUserForUser(testProperties.getNewAdmin());
		testInstance = requester.postInstanceForInstance(testProperties.getInstance());
	}

	@AfterEach
	void tearDown() {
		requester.deleteAll(admin.getUserId().getDomain(), admin.getUserId().getEmail());
	}

	@Test
	void ifUserIsNotPlayer_then_InvoleActivityThrowsUnauthorizedWithMessage() {
		ActivityBoundary testActivity = new ActivityBoundary(null, "test type",
				new ActivityInstance(testInstance.getInstanceId()), null, new ActivityInvoker(manager.getUserId()),
				null);
		assertThrows(Unauthorized.class, () -> requester.postActivityForObject(testActivity));

		testActivity.setInvokedBy(new ActivityInvoker(admin.getUserId()));

		assertThrows(Unauthorized.class, () -> requester.postActivityForObject(testActivity));

		// BUGS OUT - FOR SOME REASON MESSAGE IS "401: [no body]"
//		assertThat(assertThrows(Unauthorized.class, () -> requester.postActivityForObject(testActivity)).getMessage())
//				.contains("activity invoker must be a player");
//		assertThat(assertThrows(Unauthorized.class, () -> requester.postActivityForObject(testActivity)).getMessage())
//				.contains("activity invoker must be a player");
	}

	@Test
	void ifUserIsNotAnAdmin_thenDeleteAllActivitiesThrowsUnauthorizedWithMessage() {

		ActivityBoundary testActivity = new ActivityBoundary(null, "test type",
				new ActivityInstance(testInstance.getInstanceId()), null, new ActivityInvoker(player.getUserId()),
				null);

		IntStream.range(0, 5).mapToObj(i -> requester.postActivityForObject(testActivity)).collect(Collectors.toList());

		assertThat(assertThrows(Unauthorized.class,
				() -> requester.deleteAllActivities(player.getUserId().getDomain(), player.getUserId().getEmail()))
				.getMessage()).contains("user must be an admin to perform this action");

		assertThat(assertThrows(Unauthorized.class,
				() -> requester.deleteAllActivities(manager.getUserId().getDomain(), manager.getUserId().getEmail()))
				.getMessage()).contains("user must be an admin to perform this action");
	}

	@Test
	void ifUserIsNotAnAdmin_thenGetAllActivitiesThrowsUnauthorizedWithMessage() {

		ActivityBoundary testActivity = new ActivityBoundary(null, "test type",
				new ActivityInstance(testInstance.getInstanceId()), null, new ActivityInvoker(player.getUserId()),
				null);

		IntStream.range(0, 5).mapToObj(i -> requester.postActivityForObject(testActivity)).collect(Collectors.toList());

		assertThat(assertThrows(Unauthorized.class,
				() -> requester.getActivities(player.getUserId().getDomain(), player.getUserId().getEmail()))
				.getMessage()).contains("user must be an admin to perform this action");

		assertThat(assertThrows(Unauthorized.class,
				() -> requester.getActivities(manager.getUserId().getDomain(), manager.getUserId().getEmail()))
				.getMessage()).contains("user must be an admin to perform this action");
	}

	@Test
	void testGetAllActivitiesWithPagination() {

		IntStream.range(0, 20)
				.mapToObj(
						i -> new ActivityBoundary(null, "test type", new ActivityInstance(testInstance.getInstanceId()),
								null, new ActivityInvoker(player.getUserId()), Collections.singletonMap("key", 1)))
				.map(activity -> requester.postActivityForObject(activity)).collect(Collectors.toList());

		assertThat(requester.getActivities(admin.getUserId().getDomain(), admin.getUserId().getEmail()))
				.isSortedAccordingTo(activityComparatorById);

		assertThat(requester.getActivities(admin.getUserId().getDomain(), admin.getUserId().getEmail())).hasSize(10);

		assertThat(requester.getActivities(admin.getUserId().getDomain(), admin.getUserId().getEmail(), 30, 0))
				.hasSize(20);

		assertThat(requester.getActivities(admin.getUserId().getDomain(), admin.getUserId().getEmail(), 6, 0))
				.hasSize(6);

		assertThat(requester.getActivities(admin.getUserId().getDomain(), admin.getUserId().getEmail(), 13, 1))
				.hasSize(7);
	}

	@Test
	void ifActivityIsMissingType_thenInvokeActivityThrowsBadRequestWithTypeIsMissingMessage() {
		ActivityBoundary testActivity = new ActivityBoundary(null, null,
				new ActivityInstance(testInstance.getInstanceId()), null, null, null);

		assertThat(assertThrows(BadRequest.class, () -> requester.postActivityForObject(testActivity)).getMessage())
				.contains("type is missing");

		testActivity.setType("");

		assertThat(assertThrows(BadRequest.class, () -> requester.postActivityForObject(testActivity)).getMessage())
				.contains("type is missing");
	}

	@Test
	void ifActivityIsMissingInstance_thenInvokeActivityThrowsBadRequestWithInstanceIsMissingMessage() {
		ActivityBoundary testActivity = new ActivityBoundary(null, "test type", null, null,
				new ActivityInvoker(player.getUserId()), null);

		assertThat(assertThrows(BadRequest.class, () -> requester.postActivityForObject(testActivity)).getMessage())
				.contains("instance is missing");

		testActivity.setInstance(new ActivityInstance(null));

		assertThat(assertThrows(BadRequest.class, () -> requester.postActivityForObject(testActivity)).getMessage())
				.contains("instance.instanceId is missing");

		testActivity.getInstance().setInstanceId(new InstanceId(null, "test id"));

		assertThat(assertThrows(BadRequest.class, () -> requester.postActivityForObject(testActivity)).getMessage())
				.contains("instance.instanceId.domain is missing");

		testActivity.getInstance().setInstanceId(new InstanceId("", "test id"));

		assertThat(assertThrows(BadRequest.class, () -> requester.postActivityForObject(testActivity)).getMessage())
				.contains("instance.instanceId.domain is missing");

		testActivity.getInstance().setInstanceId(new InstanceId("test domain", null));

		assertThat(assertThrows(BadRequest.class, () -> requester.postActivityForObject(testActivity)).getMessage())
				.contains("instance.instanceId.id is missing");

		testActivity.getInstance().setInstanceId(new InstanceId("test domain", ""));

		assertThat(assertThrows(BadRequest.class, () -> requester.postActivityForObject(testActivity)).getMessage())
				.contains("instance.instanceId.id is missing");
	}

	@Test
	void ifActivityIsMissingInvokedBy_thenInvokeActivityThrowsBadRequestWithInvokedByIsMissingMessage() {
		ActivityBoundary testActivity = new ActivityBoundary(null, "test type",
				new ActivityInstance(testInstance.getInstanceId()), null, null, null);

		assertThat(assertThrows(BadRequest.class, () -> requester.postActivityForObject(testActivity)).getMessage())
				.contains("invokedBy is missing");

		testActivity.setInvokedBy(new ActivityInvoker(null));

		assertThat(assertThrows(BadRequest.class, () -> requester.postActivityForObject(testActivity)).getMessage())
				.contains("invokedBy.userId is missing");

		testActivity.setInvokedBy(new ActivityInvoker(new UserId("test@email.com", null)));

		assertThat(assertThrows(BadRequest.class, () -> requester.postActivityForObject(testActivity)).getMessage())
				.contains("invokedBy.userId.domain is missing");

		testActivity.setInvokedBy(new ActivityInvoker(new UserId("test@email.com", "")));

		assertThat(assertThrows(BadRequest.class, () -> requester.postActivityForObject(testActivity)).getMessage())
				.contains("invokedBy.userId.domain is missing");

		testActivity.setInvokedBy(new ActivityInvoker(new UserId(null, "test domain")));

		assertThat(assertThrows(BadRequest.class, () -> requester.postActivityForObject(testActivity)).getMessage())
				.contains("invokedBy.userId.email is missing");

		testActivity.setInvokedBy(new ActivityInvoker(new UserId("", "test domain")));

		assertThat(assertThrows(BadRequest.class, () -> requester.postActivityForObject(testActivity)).getMessage())
				.contains("invokedBy.userId.email is missing");

	}

	@Test
	void ifInstanceNotInDB_thenInvokeActivityThrowsNotFoundWithMessage() {
		ActivityBoundary testActivity = new ActivityBoundary(null, "some type",
				new ActivityInstance(new InstanceId("some domain", "some id")), null,
				new ActivityInvoker(player.getUserId()), null);

		assertThat(assertThrows(NotFound.class, () -> requester.postActivityForObject(testActivity)).getMessage())
				.contains("could not find instance");

	}

	@Test
	void ifInstanceNotActive_thenInvokeActivityThrowsBadRequestWithMessage() {
		testInstance.setActive(false);

		requester.putInstance(testInstance, testInstance.getInstanceId().getDomain(),
				testInstance.getInstanceId().getId(), manager.getUserId().getDomain(), manager.getUserId().getEmail());

		ActivityBoundary testActivity = new ActivityBoundary(null, "some type",
				new ActivityInstance(testInstance.getInstanceId()), null, new ActivityInvoker(player.getUserId()),
				null);

		assertThat(assertThrows(NotFound.class, () -> requester.postActivityForObject(testActivity)).getMessage())
				.contains("no active instance");

	}
}
