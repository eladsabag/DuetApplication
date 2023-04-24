package iob.logic.users;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import iob.data.UserEntity;
import iob.data.UserRole;
import iob.log.LogMethod;
import iob.logic.ExtendedUsersService;
import iob.logic.customExceptions.BadRequestException;
import iob.logic.customExceptions.EntityNotFoundException;
import iob.logic.customExceptions.UnauthorizedRequestException;
import iob.logic.utility.ConfigProperties;
import iob.mongo_repository.UserRepository;

@Service
public class UsersServiceJPA implements ExtendedUsersService {
	private UserConverter userConverter;
	private String domain;
	private ConfigProperties configProperties;
	private UserRepository userRepo;

	@Autowired
	public UsersServiceJPA(UserConverter userConverter, ConfigProperties configProperties, UserRepository userRepo) {
		this.userConverter = userConverter;
		this.configProperties = configProperties;
		this.domain = this.configProperties.getApplicationDomain();
		this.userRepo = userRepo;
	}

	@Override
	@LogMethod
	public UserBoundary createUser(NewUserBoundary user) {
		try {
			getUserEntityByDomainAndEmail(this.domain, user.getEmail());
			throw new BadRequestException("user already exists");
		} catch (EntityNotFoundException e) {
			if (user.getEmail() == null || user.getEmail().length() == 0)
				throw new BadRequestException("email is missing");

			if (!isValidEmail(user.getEmail()))
				throw new BadRequestException("invalid email");

			if (user.getRole() == null)
				throw new BadRequestException("role is missing");

			if (user.getUsername() == null || user.getUsername().length() == 0)
				throw new BadRequestException("username is missing");

			if (user.getAvatar() == null || user.getAvatar().length() == 0)
				throw new BadRequestException("avatar is missing");

			UserBoundary userBoundary = new UserBoundary(new UserId(user.getEmail(), domain),
					UserRole.valueOf(user.getRole()), user.getUsername(), user.getAvatar());

			UserEntity entity = userConverter.toEntity(userBoundary);
			entity = this.userRepo.save(entity);
			return this.userConverter.toBoundary(entity);
		}
	}

	@Override
	@LogMethod
	public UserBoundary login(String userDomain, String userEmail) {
		UserEntity logged = getUserEntityByDomainAndEmail(userDomain, userEmail);
		return this.userConverter.toBoundary(logged);
	}

	@Override
	@LogMethod
	public UserBoundary updateUser(String userDomain, String userEmail, UserBoundary update) {
		UserEntity entity = getUserEntityByDomainAndEmail(userDomain, userEmail);
		if (update.getAvatar() != null && update.getAvatar().length() != 0) {
			entity.setAvatar(update.getAvatar());
		}
		if (update.getUsername() != null && update.getAvatar().length() != 0) {
			entity.setUserName(update.getUsername());
		}
		if (update.getRole() != null) {
			entity.setRole(update.getRole());
		}

		entity = this.userRepo.save(entity);
		return this.userConverter.toBoundary(entity);
	}

	@Override
	public List<UserBoundary> getAllUsers() {
		throw new RuntimeException("deprecated method - use getAllUsers with pagination instead");
	}

	@Override
	@LogMethod
	public void deleteAllUsers(String userDomain, String userEmail) {
		UserEntity entity = getUserEntityByDomainAndEmail(userDomain, userEmail);
		if (entity.getRole().equals(UserRole.ADMIN)) {
			this.userRepo.deleteAll();
		} else
			throw new UnauthorizedRequestException("user must be an admin to perform this action");
	}

	private UserEntity getUserEntityByDomainAndEmail(String domain, String email) {
		Optional<UserEntity> op = this.userRepo.findByDomainAndEmail(domain, email);

		if (op.isPresent()) {
			UserEntity entity = op.get();
			return entity;
		} else {
			throw new EntityNotFoundException("could not find user with domain=" + domain + " and email=" + email);
		}
	}

	@Override
	@LogMethod
	public List<UserBoundary> getAllUsers(int size, int page, String domain, String email) {
		UserEntity entity = getUserEntityByDomainAndEmail(domain, email);
		if (entity.getRole().equals(UserRole.ADMIN)) {
			return this.userRepo.findAll(PageRequest.of(page, size, Direction.ASC, "userId")).stream() // Stream<MessageEntity>
					.map(this.userConverter::toBoundary) // Stream<Message>
					.collect(Collectors.toList()); // List<Message>
		} else {
			throw new UnauthorizedRequestException("user must be an admin to perform this action");
		}
	}

	@Override
	@LogMethod
	public List<UserBoundary> getUsersByVersion(int version, int size, int page) {
		return this.userRepo.findAllByVersion(version, PageRequest.of(page, size, Direction.ASC, "userId")).stream()
				.map(this.userConverter::toBoundary).collect(Collectors.toList());
	}

	public boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
				+ "A-Z]{2,7}$";

		Pattern pat = Pattern.compile(emailRegex);
		if (email == null)
			return false;
		return pat.matcher(email).matches();
	}
}
