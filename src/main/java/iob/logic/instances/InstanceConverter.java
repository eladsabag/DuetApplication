package iob.logic.instances;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import iob.data.InstanceEntity;
import iob.logic.users.UserId;
import iob.logic.utility.Location;

@Component
public class InstanceConverter {

	public InstanceEntity toEntity(InstanceBoundary boundary) {
		InstanceEntity entity = new InstanceEntity();
		entity.setInstanceId(toEntity(boundary.getInstanceId().getDomain(), boundary.getInstanceId().getId()));
		entity.setType(boundary.getType());
		entity.setName(boundary.getName());
		entity.setActive(boundary.getActive());
		entity.setCreatedTimestamp(boundary.getCreatedTimestamp());
		UserId userId = boundary.getCreatedBy().get("userId");
		entity.setCreatedByDomain(userId.getDomain());
		entity.setCreatedByEmail(userId.getEmail());
		entity.setLocation(toEntity(boundary.getLocation()));
		entity.setAttributes(boundary.getInstanceAttributes());

		return entity;
	}

	public double[] toEntity(Location location) {
		return new double[] { location.getLng(), location.getLat() };
	}

	public String toEntity(String instanceDomain, String instanceId) {
		return instanceDomain + "/" + instanceId;
	}

	public InstanceBoundary toBoundary(InstanceEntity entity) {
		InstanceBoundary boundary = new InstanceBoundary();

		boundary.setInstanceId(toBoundary(entity.getInstanceId()));
		boundary.setType(entity.getType());
		boundary.setName(entity.getName());
		boundary.setActive(entity.getActive());
		boundary.setCreatedTimestamp(entity.getCreatedTimestamp());
		boundary.setCreatedBy(toBoundary(entity.getCreatedByDomain(), entity.getCreatedByEmail()));
		boundary.setLocation(toBoundary(entity.getLocation()));
		if (entity.getAttributes() != null) {
			boundary.setInstanceAttributes(entity.getAttributes());
		}

		return boundary;
	}

	private Location toBoundary(double[] location) {
		return new Location(location[0], location[1]);
	}

	private Map<String, UserId> toBoundary(String createdByDomain, String createdByEmail) {
		Map<String, UserId> userMap = new HashMap<String, UserId>();
		userMap.put("userId", new UserId(createdByEmail, createdByDomain));

		return userMap;
	}

	public InstanceId toBoundary(String instanceId) {
		String[] domainId = instanceId.split("/");
		return new InstanceId(domainId[0], domainId[1]);
	}

}
