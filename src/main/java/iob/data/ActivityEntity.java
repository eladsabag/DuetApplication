package iob.data;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "activities")
public class ActivityEntity {

	private @Id String activityId;
	private String type;
	private String instanceDomain;
	private String instanceId;
	private @Indexed Date createdTimestamp;
	private String invokedByDomain;
	private String invokedByEmail;
	private Map<String, Object> attributes;
	private @Indexed int version;

	public ActivityEntity() {
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInstanceDomain() {
		return instanceDomain;
	}

	public void setInstanceDomain(String instanceDomain) {
		this.instanceDomain = instanceDomain;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public String getInvokedByDomain() {
		return invokedByDomain;
	}

	public void setInvokedByDomain(String invokedByDomain) {
		this.invokedByDomain = invokedByDomain;
	}

	public String getInvokedByEmail() {
		return invokedByEmail;
	}

	public void setInvokedByEmail(String invokedByEmail) {
		this.invokedByEmail = invokedByEmail;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
