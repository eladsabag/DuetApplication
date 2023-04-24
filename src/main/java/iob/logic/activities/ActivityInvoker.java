package iob.logic.activities;

import iob.logic.users.UserId;

public class ActivityInvoker {
	private UserId userId;

	public ActivityInvoker() {
	}

	public ActivityInvoker(UserId userId) {
		this();
		this.userId = userId;
	}

	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}
}
