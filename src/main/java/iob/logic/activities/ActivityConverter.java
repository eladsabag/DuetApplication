package iob.logic.activities;

import org.springframework.stereotype.Component;

import iob.data.ActivityEntity;
import iob.logic.instances.InstanceId;
import iob.logic.users.UserId;

@Component
public class ActivityConverter {

	public ActivityEntity toEntity(ActivityBoundary boundary) {
		ActivityEntity entity = new ActivityEntity();
		entity.setActivityId(toEntity(boundary.getActivityId().getDomain(), boundary.getActivityId().getId()));
		entity.setType(boundary.getType());
		entity.setCreatedTimestamp(boundary.getCreatedTimestamp());
		UserId userId = boundary.getInvokedBy().getUserId();

		entity.setInvokedByDomain(userId.getDomain());
		entity.setInvokedByEmail(userId.getEmail());
		entity.setAttributes(boundary.getActivityAttributes());

		return entity;
	}

	public String toEntity(String domain, String id) {
		return domain + "/" + id;
	}

	public ActivityBoundary toBoundary(ActivityEntity entity) {
		ActivityBoundary boundary = new ActivityBoundary();
		boundary.setActivityId(toActivityIdBoundary(entity.getActivityId()));
		boundary.setType(entity.getType());
		boundary.setInstance(toActivityInstanceBoundary(entity.getInstanceDomain(), entity.getInstanceDomain()));
		boundary.setCreatedTimestamp(entity.getCreatedTimestamp());
		boundary.setInvokedBy(toInvokedByBoundary(entity.getInvokedByDomain(), entity.getInvokedByEmail()));
		boundary.setActivityAttributes(entity.getAttributes());
		return boundary;
	}

	private ActivityId toActivityIdBoundary(String activityId) {
		String[] activityDomainAndId = activityId.split("/");
		return new ActivityId(activityDomainAndId[0], activityDomainAndId[1]);
	}

	private ActivityInstance toActivityInstanceBoundary(String domain, String id) {
		return new ActivityInstance(new InstanceId(domain, id));
	}

	public ActivityId toBoundary(ActivityId activityId) {
		return activityId;
	}

	public ActivityInvoker toInvokedByBoundary(String domain, String email) {
		return new ActivityInvoker(new UserId(email, domain));
	}
}
