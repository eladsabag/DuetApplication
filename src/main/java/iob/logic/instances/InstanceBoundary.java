package iob.logic.instances;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import iob.logic.users.UserId;
import iob.logic.utility.Location;

public class InstanceBoundary {

	private InstanceId instanceId;
	private String type;
	private String name;
	private Boolean active;
	private Date createdTimestamp;
	private Map<String, UserId> createdBy;
	private Location location;
	private Map<String, Object> instanceAttributes;

	public InstanceBoundary() {
		this.createdBy = new HashMap<>();
		this.location = new Location(0.,0.);
		this.instanceAttributes = new HashMap<>();
	}

	public InstanceBoundary(InstanceId instanceId, String type, String name, Boolean active,
			Date createdTimestamp, Map<String, UserId> createdBy, Location location,
			Map<String, Object> instanceAttributes) {
		this();
		this.instanceId = instanceId;
		this.type = type;
		this.name = name;
		this.active = active;
		this.createdTimestamp = createdTimestamp;
		this.createdBy = createdBy;
		this.location = location;
		this.instanceAttributes = instanceAttributes;
	}

	public InstanceId getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(InstanceId instanceId) {
		this.instanceId = instanceId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public Map<String, UserId> getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Map<String, UserId> createdBy) {
		this.createdBy = createdBy;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Map<String, Object> getInstanceAttributes() {
		return instanceAttributes;
	}

	public void setInstanceAttributes(Map<String, Object> instanceAttributes) {
		this.instanceAttributes = instanceAttributes;
	}
}
