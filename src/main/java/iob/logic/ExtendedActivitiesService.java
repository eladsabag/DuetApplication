package iob.logic;

import java.util.List;

import iob.logic.activities.ActivityBoundary;

public interface ExtendedActivitiesService extends ActivitiesService{
	public List<ActivityBoundary> getAllActivities(int size, int page, String domain, String email);

	public void deleteAllActivities(String userDomain, String userEmail);

	
}
