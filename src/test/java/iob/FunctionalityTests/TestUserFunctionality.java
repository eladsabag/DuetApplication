package iob.FunctionalityTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import iob.data.UserEntity;
import iob.data.UserRole;
import iob.logic.users.NewUserBoundary;
import iob.logic.users.UserBoundary;
import iob.logic.users.UserConverter;
import iob.logic.users.UserId;
import iob.mongo_repository.UserRepository;
import iob.utility.ApTestRequester;
import iob.utility.TestProperties;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestUserFunctionality {

	private @Autowired TestProperties testProperties;
	private @Autowired UserConverter userConverter;
	private UserBoundary admin, player;
	private NewUserBoundary newPlayer;
	private @Autowired UserRepository userRepository;
	private @Autowired ApTestRequester requester;

	@PostConstruct
	public void init() {
		newPlayer = testProperties.getNewPlayer();
	}

	@BeforeEach
	void setUp() {
		admin = requester.postNewUserForUser(testProperties.getNewAdmin());
		player = requester.postNewUserForUser(newPlayer);
	}

	@AfterEach
	void tearDown() {
		requester.deleteAllUsers(admin.getUserId().getDomain(), admin.getUserId().getEmail());
	}

	@Test
	void testLogin() {
		IntStream.range(0, 5).mapToObj(i -> {
			NewUserBoundary newUser = new NewUserBoundary("entity" + i + "@test.com", newPlayer.getRole(),
					newPlayer.getUsername(), newPlayer.getAvatar());
			return newUser;
		}).map(newUser -> requester.postNewUserForUser(newUser)).collect(Collectors.toList());

		UserBoundary rv = requester.getUser("test-domain", "entity" + 3 + "@test.com");
		UserBoundary expected = new UserBoundary(new UserId("entity" + 3 + "@test.com", "test-domain"),
				player.getRole(), player.getUsername(), player.getAvatar());
		assertThat(rv).isNotNull().usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void ifUserDoesNotExist_thenLoginThrowsNotFoundWithCouldNotFindUserMessage() {
		IntStream.range(0, 5).mapToObj(i -> {
			NewUserBoundary newUser = new NewUserBoundary("entity" + i + "@test.com", newPlayer.getRole(),
					newPlayer.getUsername(), newPlayer.getAvatar());
			return newUser;
		}).map(newUser -> requester.postNewUserForUser(newUser)).collect(Collectors.toList());

		assertThat(assertThrows(NotFound.class, () -> requester.getUser("test-domain", "entity" + 10 + "@test.com"))
				.getMessage()).contains("could not find user");
	}

	@Test
	void ifUserDoesNotExist_thenUpdateThrowsNotFoundWithCouldNotFindUserMessage() {
		IntStream.range(0, 5).mapToObj(i -> {
			NewUserBoundary newUser = new NewUserBoundary("entity" + i + "@test.com", newPlayer.getRole(),
					newPlayer.getUsername(), newPlayer.getAvatar());
			return newUser;
		}).map(newUser -> requester.postNewUserForUser(newUser)).collect(Collectors.toList());

		assertThat(assertThrows(NotFound.class,
				() -> requester.putUser(new UserBoundary(), "test-domain", "entity" + 10 + "@test.com")).getMessage())
				.contains("could not find user");
	}

	@Test
	void ifUserAlreadyExists_thenCreateThrowsBadRequestWithUserAlreadyExistsMessage() {
		assertThat(assertThrows(BadRequest.class, () -> requester.postNewUserForUser(newPlayer)).getMessage())
				.contains("user already exists");
	}

	@Test
	void testUpdateUser() {
		assertThat(userRepository.findAll()).hasSize(2);
		UserEntity updatedPlayerEntity = userRepository
				.findByDomainAndEmail(player.getUserId().getDomain(), player.getUserId().getEmail()).get();

		updatedPlayerEntity.setRole(UserRole.MANAGER);
		updatedPlayerEntity.setUsername("updated " + updatedPlayerEntity.getUsername());
		updatedPlayerEntity.setAvatar("updated " + updatedPlayerEntity.getAvatar());

		UserBoundary updatedPlayer = userConverter.toBoundary(updatedPlayerEntity);

		requester.putUser(updatedPlayer, player.getUserId().getDomain(), player.getUserId().getEmail());

		assertThat(userRepository.findByDomainAndEmail(player.getUserId().getDomain(), player.getUserId().getEmail()))
				.isPresent().get().usingRecursiveComparison().isEqualTo(updatedPlayerEntity);

		assertThat(userRepository.findAll()).hasSize(2);
	}

	@Test
	void testGetAllUsers() {
		IntStream.range(0, 5)
				.mapToObj(i -> new NewUserBoundary("entity" + i + "@test.com", newPlayer.getRole(),
						newPlayer.getUsername() + i, newPlayer.getAvatar() + i))
				.map(user -> requester.postNewUserForUser(user)).collect(Collectors.toList());

		UserBoundary[] actual = requester.getUsers(admin.getUserId().getDomain(), admin.getUserId().getEmail(), 10, 0);
		assertThat(actual).hasSize(5 + 2); // 5 inserted users + admin + player
	}

	@Test
	void testFindAllByVersion() {
		IntStream.range(0, 5).mapToObj(i -> {
			UserEntity userEntity = new UserEntity();
			userEntity.setDomain("test-domain");
			userEntity.setEmail("entity" + i + "@test.com");
			userEntity.setVersion(1);
			return userEntity;
		}).map(userEntity -> userRepository.save(userEntity)).collect(Collectors.toList());

		IntStream.range(5, 10).mapToObj(i -> {
			UserEntity userEntity = new UserEntity();
			userEntity.setDomain("test-domain");
			userEntity.setEmail("entity" + i + "@test.com");
			userEntity.setVersion(2);
			return userEntity;
		}).map(userEntity -> userRepository.save(userEntity)).collect(Collectors.toList());

		List<UserEntity> actual = userRepository.findAllByVersion(1, PageRequest.of(0, 20, Direction.ASC, "userId"));

		assertThat(actual).hasSize(5);
	}

	@Test
	void testFindAllByVersionGreaterThan() {
		IntStream.range(0, 10).mapToObj(i -> {
			UserEntity userEntity = new UserEntity();
			userEntity.setDomain("test-domain");
			userEntity.setEmail("entity" + i + "@test.com");
			userEntity.setVersion(i);
			return userEntity;
		}).map(userEntity -> userRepository.save(userEntity)).collect(Collectors.toList());

		List<UserEntity> actual = userRepository.findAllByVersionGreaterThan(4,
				PageRequest.of(0, 20, Direction.ASC, "userId"));

		assertThat(actual).hasSize(5);
	}

	@Test
	void testFindAllByVersionBetween() {
		IntStream.range(0, 10).mapToObj(i -> {
			UserEntity userEntity = new UserEntity();
			userEntity.setDomain("test-domain");
			userEntity.setEmail("entity" + i + "@test.com");
			userEntity.setVersion(i);
			return userEntity;
		}).map(userEntity -> userRepository.save(userEntity)).collect(Collectors.toList());

		List<UserBoundary> actual = userRepository
				.findAllByVersionBetween(2, 8, PageRequest.of(0, 20, Direction.ASC, "userId")).stream()
				.map(userConverter::toBoundary).collect(Collectors.toList());
		assertThat(actual).hasSize(5);
	}

	@Test
	void ifNewUserIsMissingEmailThenPostThrowsBadRequestWithEmailIsMissingMessage() {
		NewUserBoundary playerWithoutEmail = new NewUserBoundary(null, newPlayer.getRole(), newPlayer.getUsername(),
				newPlayer.getAvatar());

		assertThat(assertThrows(BadRequest.class, () -> requester.postNewUserForUser(playerWithoutEmail)).getMessage())
				.contains("email is missing");
	}

	@Test
	void ifNewUserHasInvalidEmailThenPostThrowsBadRequestWithIncorrectEmailAddressMessage() {
		NewUserBoundary playerWithBadEmail = new NewUserBoundary("bad email", newPlayer.getRole(),
				newPlayer.getUsername(), newPlayer.getAvatar());

		assertThat(assertThrows(BadRequest.class, () -> requester.postNewUserForUser(playerWithBadEmail)).getMessage())
				.contains("invalid email");
	}

	@Test
	void ifNewUserIsMissingRoleThenPostThrowsBadRequestWithRoleIsMissingMessage() {
		NewUserBoundary playerWithoutRole = new NewUserBoundary("no-role-" + newPlayer.getEmail(), null,
				newPlayer.getUsername(), newPlayer.getAvatar());

		assertThat(assertThrows(BadRequest.class, () -> requester.postNewUserForUser(playerWithoutRole)).getMessage())
				.contains("role is missing");
	}

	@Test
	void ifNewUserIsMissingUsernameThenPostThrowsBadRequestWithUsernameIsMissingMessage() {
		NewUserBoundary playerWithoutUsername = new NewUserBoundary("no-username-" + newPlayer.getEmail(),
				newPlayer.getRole(), null, newPlayer.getAvatar());

		assertThat(
				assertThrows(BadRequest.class, () -> requester.postNewUserForUser(playerWithoutUsername)).getMessage())
				.contains("username is missing");
	}

	@Test
	void ifNewUserIsMissingAvatarThenPostThrowsBadRequestWithAvatarIsMissingMessage() {
		NewUserBoundary playerWithoutAvatar = new NewUserBoundary("no-avatar-" + newPlayer.getEmail(),
				newPlayer.getRole(), newPlayer.getUsername(), null);

		assertThat(assertThrows(BadRequest.class, () -> requester.postNewUserForUser(playerWithoutAvatar)).getMessage())
				.contains("avatar is missing");
	}

	@Test
	void ifUserIsNotAnAdmin_thenDeleteAllUsersThrowsUnauthorizedWithMessage() {
		UserBoundary manager = requester.postNewUserForUser(testProperties.getNewManager());

		assertThat(assertThrows(Unauthorized.class,
				() -> requester.deleteAllUsers(player.getUserId().getDomain(), player.getUserId().getEmail()))
				.getMessage()).contains("user must be an admin to perform this action");

		assertThat(assertThrows(Unauthorized.class,
				() -> requester.deleteAllUsers(manager.getUserId().getDomain(), manager.getUserId().getEmail()))
				.getMessage()).contains("user must be an admin to perform this action");
	}

	@Test
	void ifUserIsNotAnAdmin_thenGetAllUsersThrowsUnauthorizedWithMessage() {
		UserBoundary manager = requester.postNewUserForUser(testProperties.getNewManager());

		assertThat(assertThrows(Unauthorized.class,
				() -> requester.getUsers(player.getUserId().getDomain(), player.getUserId().getEmail(), 10, 0))
				.getMessage()).contains("user must be an admin to perform this action");

		assertThat(assertThrows(Unauthorized.class,
				() -> requester.getUsers(manager.getUserId().getDomain(), manager.getUserId().getEmail(), 10, 0))
				.getMessage()).contains("user must be an admin to perform this action");
	}
}
