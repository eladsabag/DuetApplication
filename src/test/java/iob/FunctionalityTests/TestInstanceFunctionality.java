package iob.FunctionalityTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import iob.logic.instances.InstanceBoundary;
import iob.logic.instances.InstanceConverter;
import iob.logic.users.UserBoundary;
import iob.logic.users.UserId;
import iob.logic.utility.Location;
import iob.utility.ApTestRequester;
import iob.utility.TestProperties;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestInstanceFunctionality {
	private @Autowired TestProperties testProperties;
	private UserBoundary admin, player, manager;
	private InstanceBoundary testInstance;
	private @Autowired InstanceConverter instanceConverter;
	private @Autowired ApTestRequester requester;
	private Comparator<InstanceBoundary> instanceComparatorById;

	@PostConstruct
	public void init() {
		testInstance = testProperties.getInstance();

		instanceComparatorById = Comparator.comparing(InstanceBoundary::getInstanceId,
				(id1, id2) -> instanceConverter.toEntity(id1.getDomain(), id1.getId())
						.compareTo(instanceConverter.toEntity(id2.getDomain(), id2.getId())));
	}

	@BeforeEach
	void setUp() {
		player = requester.postNewUserForUser(testProperties.getNewPlayer());
		manager = requester.postNewUserForUser(testProperties.getNewManager());
		admin = requester.postNewUserForUser(testProperties.getNewAdmin());
	}

	@AfterEach
	void tearDown() {
		requester.deleteAll(admin.getUserId().getDomain(), admin.getUserId().getEmail());
	}

	@Test
	void testInstanceCreationUpdateAndRetrieval() {

		InstanceBoundary createdInstance = requester.postInstanceForInstance(testInstance);

		assertThat(requester.getInstance(createdInstance.getInstanceId().getDomain(),
				createdInstance.getInstanceId().getId(), manager.getUserId().getDomain(),
				manager.getUserId().getEmail())).isNotNull();

		createdInstance.setName("updated " + createdInstance.getName());
		createdInstance.setType("updated " + createdInstance.getType());
		createdInstance.setLocation(new Location(1., 1.));
		createdInstance.setInstanceAttributes(Collections.singletonMap("key", "property"));

		requester.putInstance(createdInstance, createdInstance.getInstanceId().getDomain(),
				createdInstance.getInstanceId().getId(), manager.getUserId().getDomain(),
				manager.getUserId().getEmail());

		assertThat(requester.getInstance(createdInstance.getInstanceId().getDomain(),
				createdInstance.getInstanceId().getId(), manager.getUserId().getDomain(),
				manager.getUserId().getEmail())).usingRecursiveComparison().isEqualTo(createdInstance);

		assertThat(requester.getInstance(createdInstance.getInstanceId().getDomain(),
				createdInstance.getInstanceId().getId(), player.getUserId().getDomain(), player.getUserId().getEmail()))
				.usingRecursiveComparison().isEqualTo(createdInstance);

		createdInstance.setActive(false);

		requester.putInstance(createdInstance, createdInstance.getInstanceId().getDomain(),
				createdInstance.getInstanceId().getId(), manager.getUserId().getDomain(),
				manager.getUserId().getEmail());

		assertThat(assertThrows(NotFound.class, () -> requester.getInstance(createdInstance.getInstanceId().getDomain(),
				createdInstance.getInstanceId().getId(), player.getUserId().getDomain(), player.getUserId().getEmail()))
				.getMessage())
				.contains("no active instance with domain=" + createdInstance.getInstanceId().getDomain() + " and id="
						+ createdInstance.getInstanceId().getId());

		assertThat(assertThrows(Unauthorized.class,
				() -> requester.getInstance(createdInstance.getInstanceId().getDomain(),
						createdInstance.getInstanceId().getId(), admin.getUserId().getDomain(),
						admin.getUserId().getEmail()))
				.getMessage()).contains("user must be either a manager or a player to perform this action");
	}

	@Test
	void ifUserIsNotAManager_thenIstanceCreationThrowsBadRequestWithMwssage() {
		InstanceBoundary instanceCreatedByAdmin = new InstanceBoundary(null, "test type", "test name", true, null,
				Collections.singletonMap("userId", admin.getUserId()), null, null);
		InstanceBoundary instanceCreatedByPlayer = new InstanceBoundary(null, "test type", "test name", true, null,
				Collections.singletonMap("userId", player.getUserId()), null, null);

		assertThat(assertThrows(BadRequest.class, () -> requester.postInstanceForInstance(instanceCreatedByAdmin))
				.getMessage()).contains("userId must belong to a manager");

		assertThat(assertThrows(BadRequest.class, () -> requester.postInstanceForInstance(instanceCreatedByPlayer))
				.getMessage()).contains("userId must belong to a manager");

	}

	@Test
	void ifInstanceDoesNotExist_thenGetInstanceThrowsNotFoundWithCouldNotFindInstanceMessage() {
		IntStream.range(0, 5).mapToObj(i -> requester.postInstanceForInstance(testInstance))
				.collect(Collectors.toList());

		assertThat(assertThrows(NotFound.class,
				() -> requester.getInstance("1", "1", manager.getUserId().getDomain(), manager.getUserId().getEmail()))
				.getMessage()).contains("could not find instance by id");
	}

	@Test
	void ifInstanceIsMissingType_thenCreateInstanceThrowsBadRequestWithTypeIsMissingMessage() {
		InstanceBoundary instanceWithoutType = new InstanceBoundary(null, null, "test name", true, null,
				testInstance.getCreatedBy(), null, null);

		assertThat(assertThrows(BadRequest.class, () -> requester.postInstanceForInstance(instanceWithoutType))
				.getMessage()).contains("type is missing");

		instanceWithoutType.setType("");

		assertThat(assertThrows(BadRequest.class, () -> requester.postInstanceForInstance(instanceWithoutType))
				.getMessage()).contains("type is missing");
	}

	@Test
	void ifInstanceIsMissingName_thenCreateInstanceThrowsBadRequestWithNameIsMissingMessage() {
		InstanceBoundary instanceWithoutName = new InstanceBoundary(null, "test type", null, true, null,
				testInstance.getCreatedBy(), null, null);

		assertThat(assertThrows(BadRequest.class, () -> requester.postInstanceForInstance(instanceWithoutName))
				.getMessage()).contains("name is missing");

		instanceWithoutName.setName("");

		assertThat(assertThrows(BadRequest.class, () -> requester.postInstanceForInstance(instanceWithoutName))
				.getMessage()).contains("name is missing");
	}

	@Test
	void ifInstanceIsMissingCreatedBy_thenCreateInstanceThrowsBadRequestWithCreatedByIsMissingMessage() {
		InstanceBoundary instance = new InstanceBoundary(null, "test type", "test name", true, null, null, null, null);

		assertThat(assertThrows(BadRequest.class, () -> requester.postInstanceForInstance(instance)).getMessage())
				.contains("createdBy is missing");

		instance.setCreatedBy(new HashMap<String, UserId>());

		assertThat(assertThrows(BadRequest.class, () -> requester.postInstanceForInstance(instance)).getMessage())
				.contains("createdBy.userId is missing");

		instance.getCreatedBy().put("userId", new UserId("test email", null));

		assertThat(assertThrows(BadRequest.class, () -> requester.postInstanceForInstance(instance)).getMessage())
				.contains("createdBy.userId.domain is missing");

		instance.getCreatedBy().put("userId", new UserId("test email", ""));

		assertThat(assertThrows(BadRequest.class, () -> requester.postInstanceForInstance(instance)).getMessage())
				.contains("createdBy.userId.domain is missing");

		instance.getCreatedBy().put("userId", new UserId(null, "test domain"));

		assertThat(assertThrows(BadRequest.class, () -> requester.postInstanceForInstance(instance)).getMessage())
				.contains("createdBy.userId.email is missing");

		instance.getCreatedBy().put("userId", new UserId("", "test domain"));

		assertThat(assertThrows(BadRequest.class, () -> requester.postInstanceForInstance(instance)).getMessage())
				.contains("createdBy.userId.email is missing");
	}

	@Test
	void testGetAllInstancesWithPagination() {
		InstanceBoundary activeInstance = new InstanceBoundary(null, "test type", "test name", true, null,
				testInstance.getCreatedBy(), null, null);

		InstanceBoundary inactiveInstance = new InstanceBoundary(null, "test type", "test name", false, null,
				testInstance.getCreatedBy(), null, null);

		IntStream.range(0, 15).mapToObj(i -> requester.postInstanceForInstance(activeInstance))
				.collect(Collectors.toList());

		IntStream.range(0, 15).mapToObj(i -> requester.postInstanceForInstance(inactiveInstance))
				.collect(Collectors.toList());

		assertThat(requester.getInstances(player.getUserId().getDomain(), player.getUserId().getEmail()))
				.isSortedAccordingTo(instanceComparatorById);

		assertThat(requester.getInstances(player.getUserId().getDomain(), player.getUserId().getEmail(), 20, 0))
				.hasSize(15);

		assertThat(requester.getInstances(manager.getUserId().getDomain(), manager.getUserId().getEmail(), 20, 0))
				.hasSize(20);

		assertThat(assertThrows(Unauthorized.class,
				() -> requester.getInstances(admin.getUserId().getDomain(), admin.getUserId().getEmail(), 20, 0))
				.getMessage()).contains("user must be either a manager or a player to perform this action");
	}

	@Test
	void ifUserIsNotAnAdmin_thenDeleteAllInstancesThrowsUnauthorizedWithMessage() {
		IntStream.range(0, 5).mapToObj(i -> requester.postInstanceForInstance(testInstance))
				.collect(Collectors.toList());

		assertThat(assertThrows(Unauthorized.class,
				() -> requester.deleteAllInstances(player.getUserId().getDomain(), player.getUserId().getEmail()))
				.getMessage()).contains("user must be an admin to perform this action");

		assertThat(assertThrows(Unauthorized.class,
				() -> requester.deleteAllInstances(manager.getUserId().getDomain(), manager.getUserId().getEmail()))
				.getMessage()).contains("user must be an admin to perform this action");
	}

	@Test
	void testSearchByNameWithPagination() {
		IntStream
				.range(0, 10).mapToObj(i -> requester.postInstanceForInstance(new InstanceBoundary(null, "test type",
						"first name", true, null, testInstance.getCreatedBy(), null, null)))
				.collect(Collectors.toList());

		IntStream
				.range(0, 10).mapToObj(i -> requester.postInstanceForInstance(new InstanceBoundary(null, "test type",
						"first name", false, null, testInstance.getCreatedBy(), null, null)))
				.collect(Collectors.toList());

		IntStream
				.range(0, 10).mapToObj(i -> requester.postInstanceForInstance(new InstanceBoundary(null, "test type",
						"second name", true, null, testInstance.getCreatedBy(), null, null)))
				.collect(Collectors.toList());

		assertThat(requester.getInstancesByName("first name", manager.getUserId().getDomain(),
				manager.getUserId().getEmail())).isSortedAccordingTo(instanceComparatorById);

		assertThat(requester.getInstancesByName("first name", manager.getUserId().getDomain(),
				manager.getUserId().getEmail(), 6, 1)).hasSize(6);

		assertThat(requester.getInstancesByName("first name", player.getUserId().getDomain(),
				player.getUserId().getEmail(), 6, 1)).hasSize(4);

		assertThat(assertThrows(Unauthorized.class,
				() -> requester.getInstancesByName("first name", admin.getUserId().getDomain(),
						admin.getUserId().getEmail(), 6, 1))
				.getMessage()).contains("user must be either a manager or a player to perform this action");
	}

	@Test
	void testSearchByTypeWithPagination() {
		IntStream
				.range(0, 10).mapToObj(i -> requester.postInstanceForInstance(new InstanceBoundary(null, "first type",
						"test name", true, null, testInstance.getCreatedBy(), null, null)))
				.collect(Collectors.toList());

		IntStream
				.range(0, 10).mapToObj(i -> requester.postInstanceForInstance(new InstanceBoundary(null, "first type",
						"test name", false, null, testInstance.getCreatedBy(), null, null)))
				.collect(Collectors.toList());

		IntStream
				.range(0, 10).mapToObj(i -> requester.postInstanceForInstance(new InstanceBoundary(null, "second type",
						"test name", true, null, testInstance.getCreatedBy(), null, null)))
				.collect(Collectors.toList());

		assertThat(requester.getInstancesByType("first type", manager.getUserId().getDomain(),
				manager.getUserId().getEmail())).isSortedAccordingTo(instanceComparatorById);

		assertThat(requester.getInstancesByType("first type", manager.getUserId().getDomain(),
				manager.getUserId().getEmail(), 6, 1)).hasSize(6);

		assertThat(requester.getInstancesByType("first type", player.getUserId().getDomain(),
				player.getUserId().getEmail(), 6, 1)).hasSize(4);

		assertThat(assertThrows(Unauthorized.class,
				() -> requester.getInstancesByType("first type", admin.getUserId().getDomain(),
						admin.getUserId().getEmail(), 6, 1))
				.getMessage()).contains("user must be either a manager or a player to perform this action");
	}

	@Test
	void testSearchNearWithPagination() {
		requester.postInstanceForInstance(new InstanceBoundary(null, "test type", "test name", true, null,
				testInstance.getCreatedBy(), null, null));
		IntStream.range(0, 4)

				.mapToObj(i -> requester.postInstanceForInstance(
						new InstanceBoundary(null, "test type", "test name", false, null, testInstance.getCreatedBy(),
								new Location(Math.cos(i * Math.PI), Math.sin(i * Math.PI)), null)))
				.collect(Collectors.toList());

		IntStream.range(0, 4)
				.mapToObj(i -> requester.postInstanceForInstance(
						new InstanceBoundary(null, "test type", "test name", true, null, testInstance.getCreatedBy(),
								new Location(2 * Math.cos(i * Math.PI), 2 * Math.sin(i * Math.PI)), null)))
				.collect(Collectors.toList());

		assertThat(requester.getInstancesByLocation(0, 0, 2.5, manager.getUserId().getDomain(),
				manager.getUserId().getEmail())).isSortedAccordingTo(instanceComparatorById);

		assertThat(requester.getInstancesByLocation(0, 0, 2.5, manager.getUserId().getDomain(),
				manager.getUserId().getEmail(), 10, 0)).hasSize(9);

		assertThat(requester.getInstancesByLocation(0, 0, 2.5, player.getUserId().getDomain(),
				player.getUserId().getEmail(), 10, 0)).hasSize(5);

		assertThat(requester.getInstancesByLocation(0, 0, 1.5, manager.getUserId().getDomain(),
				manager.getUserId().getEmail())).hasSize(5);

		assertThat(assertThrows(Unauthorized.class,
				() -> requester.getInstancesByLocation(0, 0, 1.5, admin.getUserId().getDomain(),
						admin.getUserId().getEmail()))
				.getMessage()).contains("user must be either a manager or a player to perform this action");
	}

}
