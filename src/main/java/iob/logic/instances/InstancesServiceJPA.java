package iob.logic.instances;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import iob.data.InstanceEntity;
import iob.data.UserRole;
import iob.log.LogMethod;
import iob.logic.ExtendedInstancesService;
import iob.logic.UsersService;
import iob.logic.customExceptions.BadRequestException;
import iob.logic.customExceptions.EntityNotFoundException;
import iob.logic.customExceptions.UnauthorizedRequestException;
import iob.logic.users.UserId;
import iob.logic.utility.ConfigProperties;
import iob.logic.utility.Location;
import iob.mongo_repository.InstanceRepository;

@Service
public class InstancesServiceJPA implements ExtendedInstancesService {
	private InstanceRepository instanceRepository;
	private InstanceConverter instanceConverter;
	private ConfigProperties configProperties;
	private UsersService userService;

	@Autowired
	public InstancesServiceJPA(InstanceRepository instanceRepository, InstanceConverter instanceConverter,
			ConfigProperties configProperties, UsersService userService) {
		this.instanceRepository = instanceRepository;
		this.instanceConverter = instanceConverter;
		this.configProperties = configProperties;
		this.userService = userService;
	}

	@Override
	@LogMethod
	public InstanceBoundary createInstance(InstanceBoundary instance) {

		if (instance.getType() == null)
			throw new BadRequestException("type is missing");
		
		if (instance.getType().isEmpty())
			throw new BadRequestException("type is missing");

		if (instance.getName() == null)
			throw new BadRequestException("name is missing");
		
		if (instance.getName().isEmpty())
			throw new BadRequestException("name is missing");

		if (instance.getCreatedBy() == null)
			throw new BadRequestException("createdBy is missing");

		if (instance.getCreatedBy().containsKey("userId") == false)
			throw new BadRequestException("createdBy.userId is missing");

		UserId userId = instance.getCreatedBy().get("userId");
		String userEmail = userId.getEmail();
		String userDomain = userId.getDomain();

		if (userEmail == null)
			throw new BadRequestException("createdBy.userId.email is missing");
		
		if (userEmail.isEmpty())
			throw new BadRequestException("createdBy.userId.email is missing");
		
		if (userDomain == null)
			throw new BadRequestException("createdBy.userId.domain is missing");
		
		if (userDomain.isEmpty())
			throw new BadRequestException("createdBy.userId.domain is missing");
			

		if (userService.login(userDomain, userEmail).getRole() != UserRole.MANAGER)
			throw new BadRequestException("userId must belong to a manager");

		if (instance.getLocation() == null)
			instance.setLocation(new Location(0., 0.));

		if (instance.getActive() == null)
			instance.setActive(true);

		instance.setCreatedTimestamp(new Date());

		InstanceId instanceId = new InstanceId(configProperties.getApplicationDomain(), UUID.randomUUID().toString());
		instance.setInstanceId(instanceId);

		InstanceEntity entity = instanceConverter.toEntity(instance);
		entity = instanceRepository.save(entity);
		return instanceConverter.toBoundary(entity);
	}

	@Override
	public InstanceBoundary updateInstance(String instanceDomain, String instanceId, InstanceBoundary update) {
		throw new RuntimeException("deprecated method - use updateInstance with user info instead");
	}

	private InstanceEntity getInstanceEntityById(String instanceDomain, String instanceId) {
		Optional<InstanceEntity> op = instanceRepository
				.findById(instanceConverter.toEntity(instanceDomain, instanceId));

		if (op.isPresent()) {
			InstanceEntity entity = op.get();
			return entity;
		} else {
			throw new EntityNotFoundException("could not find instance by id: " + instanceDomain + ", " + instanceId);
		}
	}

	private UserRole getUserRoleById(String userDomain, String userId) {
		return userService.login(userDomain, userId).getRole();
	}

	@Override
	public InstanceBoundary getSpecificInstance(String instanceDomain, String instanceId) {
		throw new RuntimeException("deprecated method - use getSpecificInstance with user info instead");
	}

	@Override
	public List<InstanceBoundary> getAllInstances() {
		throw new RuntimeException("deprecated method - use getAllInstances with user info instead");
	}

	@Override
	@LogMethod
	public void deleteAllInstances() {
		throw new RuntimeException("deprecated method - use deleteAllInstances with user info instead");
	}

	@Override
	@LogMethod
	public List<InstanceBoundary> getInstancesByName(String name, String userDomain, String userEmail, int size,
			int page) {
		UserRole userRole = getUserRoleById(userDomain, userEmail);

		switch (userRole) {
		case MANAGER:
			return instanceRepository.findAllByName(name, PageRequest.of(page, size, Direction.ASC, "instanceId"))
					.stream().map(this.instanceConverter::toBoundary).collect(Collectors.toList());
		case PLAYER:
			return instanceRepository
					.findAllByNameAndActive(name, true, PageRequest.of(page, size, Direction.ASC, "instanceId"))
					.stream().map(this.instanceConverter::toBoundary).collect(Collectors.toList());
		default:
			throw new UnauthorizedRequestException("user must be either a manager or a player to perform this action");
		}
	}

	@Override
	@LogMethod
	public List<InstanceBoundary> getInstancesByType(String type, String userDomain, String userEmail, int size,
			int page) {
		UserRole userRole = getUserRoleById(userDomain, userEmail);

		switch (userRole) {
		case MANAGER:
			return instanceRepository.findAllByType(type, PageRequest.of(page, size, Direction.ASC, "instanceId"))
					.stream().map(this.instanceConverter::toBoundary).collect(Collectors.toList());
		case PLAYER:
			return instanceRepository
					.findAllByTypeAndActive(type, true, PageRequest.of(page, size, Direction.ASC, "instanceId"))
					.stream().map(this.instanceConverter::toBoundary).collect(Collectors.toList());
		default:
			throw new UnauthorizedRequestException("user must be either a manager or a player to perform this action");
		}
	}

	@Override
	@LogMethod
	public List<InstanceBoundary> getInstancesNear(double lat, double lng, double distance, String userDomain,
			String userEmail, int size, int page) {
		UserRole userRole = getUserRoleById(userDomain, userEmail);
		Point location = new Point(lat, lng);
		Distance dist = new Distance(distance);

		switch (userRole) {
		case MANAGER:
			return instanceRepository
					.findAllByLocationNear(location, dist, PageRequest.of(page, size, Direction.ASC, "instanceId"))
					.stream().map(this.instanceConverter::toBoundary).collect(Collectors.toList());
		case PLAYER:
			return instanceRepository
					.findAllByLocationNearAndActive(location, dist, true,
							PageRequest.of(page, size, Direction.ASC, "instanceId"))
					.stream().map(this.instanceConverter::toBoundary).collect(Collectors.toList());
		default:
			throw new UnauthorizedRequestException("user must be either a manager or a player to perform this action");
		}

	}

	@Override
	@LogMethod
	public InstanceBoundary updateInstance(String instanceDomain, String instanceId, InstanceBoundary update,
			String userDomain, String userEmail) {
		UserRole userRole = getUserRoleById(userDomain, userEmail);

		if (userRole == UserRole.MANAGER) {
			InstanceEntity entity = getInstanceEntityById(instanceDomain, instanceId);

			if (update.getInstanceId() != null) {
				// do nothing
			}

			if (update.getType() != null) {
				entity.setType(update.getType());
			}

			if (update.getName() != null) {
				entity.setName(update.getName());
			}

			if (update.getActive() != null) {
				entity.setActive(update.getActive());
			}

			if (update.getCreatedTimestamp() != null) {
				// do nothing
			}

			if (update.getCreatedBy() != null) {
				// do nothing
			}

			Location newLocation = update.getLocation();
			if (newLocation != null) {
				double[] location = entity.getLocation();
				Double lat = newLocation.getLat();
				if (lat != null)
					location[0] = lat;
				Double lng = newLocation.getLng();
				if (lng != null)
					location[1] = lng;
				
				entity.setLocation(location);
			}

			if (update.getInstanceAttributes() != null) {
				entity.setAttributes(update.getInstanceAttributes());
			}

			entity = instanceRepository.save(entity);
			return instanceConverter.toBoundary(entity);

		} else {
			throw new UnauthorizedRequestException("user must be a manager to perform this action");
		}

	}

	@Override
	@LogMethod
	public InstanceBoundary getSpecificInstance(String instanceDomain, String instanceId, String userDomain,
			String userEmail) {
		UserRole userRole = getUserRoleById(userDomain, userEmail);

		if (userRole == UserRole.ADMIN)
			throw new UnauthorizedRequestException("user must be either a manager or a player to perform this action");

		InstanceBoundary instance = instanceConverter.toBoundary(getInstanceEntityById(instanceDomain, instanceId));

		if (userRole == UserRole.MANAGER)
			return instance;

		if (instance.getActive() == true)
			return instance;
		else
			throw new EntityNotFoundException(
					"no active instance with domain=" + instanceDomain + " and id=" + instanceId);
	}

	@Override
	@LogMethod
	public List<InstanceBoundary> getAllInstances(String userDomain, String userEmail, int size, int page) {
		UserRole userRole = getUserRoleById(userDomain, userEmail);

		switch (userRole) {
		case MANAGER:
			return instanceRepository.findAll(PageRequest.of(page, size, Direction.ASC, "instanceId")).stream()
					.map(instanceConverter::toBoundary).collect(Collectors.toList());
		case PLAYER:
			return instanceRepository.findAllByActive(true, PageRequest.of(page, size, Direction.ASC, "instanceId"))
					.stream().map(this.instanceConverter::toBoundary).collect(Collectors.toList());
		default:
			throw new UnauthorizedRequestException("user must be either a manager or a player to perform this action");
		}

	}

	@Override
	@LogMethod
	public void deleteAllInstances(String userDomain, String userEmail) {
		UserRole userRole = getUserRoleById(userDomain, userEmail);

		if (userRole == UserRole.ADMIN)
			instanceRepository.deleteAll();
		else
			throw new UnauthorizedRequestException("user must be an admin to perform this action");

	}
}
