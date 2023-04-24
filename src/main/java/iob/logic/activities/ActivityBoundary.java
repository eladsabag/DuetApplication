package iob.logic.activities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityBoundary {

	private ActivityId activityId;
	private String type;
	private ActivityInstance instance;
	private Date createdTimestamp;
	private ActivityInvoker invokedBy;
	private Map<String, Object> activityAttributes;

	public ActivityBoundary() {
		this.activityAttributes = new HashMap<>();
	}

	public ActivityBoundary(ActivityId activityId, String type, ActivityInstance instance, Date createdTimestamp,
			ActivityInvoker invokedBy, Map<String, Object> activityAttributes) {
		this();
		this.activityId = activityId;
		this.type = type;
		this.instance = instance;
		this.createdTimestamp = createdTimestamp;
		this.invokedBy = invokedBy;
		this.activityAttributes = activityAttributes;
	}

	public ActivityId getActivityId() {
		return activityId;
	}

	public void setActivityId(ActivityId activityId) {
		this.activityId = activityId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ActivityInstance getInstance() {
		return instance;
	}

	public void setInstance(ActivityInstance instance) {
		this.instance = instance;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public ActivityInvoker getInvokedBy() {
		return invokedBy;
	}

	public void setInvokedBy(ActivityInvoker invokedBy) {
		this.invokedBy = invokedBy;
	}

	public Map<String, Object> getActivityAttributes() {
		return activityAttributes;
	}

	public void setActivityAttributes(Map<String, Object> activityAttributes) {
		this.activityAttributes = activityAttributes;
	}
}
