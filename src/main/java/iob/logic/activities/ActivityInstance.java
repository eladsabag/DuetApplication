package iob.logic.activities;

import iob.logic.instances.InstanceId;

public class ActivityInstance {
	private InstanceId instanceId;

	public ActivityInstance() {
	}

	public ActivityInstance(InstanceId instanceId) {
		this();
		this.instanceId = instanceId;
	}

	public InstanceId getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(InstanceId instanceId) {
		this.instanceId = instanceId;
	}
}
