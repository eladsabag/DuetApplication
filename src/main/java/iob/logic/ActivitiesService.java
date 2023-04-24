package iob.logic;

import java.util.List;

import iob.logic.activities.ActivityBoundary;

public interface ActivitiesService {
		
	Object invokeActivity(ActivityBoundary activity);
	@Deprecated
	public List<ActivityBoundary> getAllActivities();
	public void deleteAllActivities();
} 
