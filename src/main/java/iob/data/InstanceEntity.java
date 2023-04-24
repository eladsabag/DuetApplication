package iob.data;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "instances")
public class InstanceEntity {
	private @Id String instanceId;
	private String type;
	private @Indexed String name;
	private @Indexed boolean active;
	private @Indexed Date createdTimestamp;
	private String createdByDomain;
	private String createdByEmail;
	private @GeoSpatialIndexed double[] location;
	private Map<String, Object> attributes;

	public InstanceEntity() {
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
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

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public String getCreatedByDomain() {
		return createdByDomain;
	}

	public void setCreatedByDomain(String createdByDomain) {
		this.createdByDomain = createdByDomain;
	}

	public String getCreatedByEmail() {
		return createdByEmail;
	}

	public void setCreatedByEmail(String createdByEmail) {
		this.createdByEmail = createdByEmail;
	}

	public double[] getLocation() {
		return location;
	}

	public void setLocation(double[] location) {
		this.location = location;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

}
