package iob.rest_api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import iob.logic.ActivitiesService;
import iob.logic.activities.ActivityBoundary;


@RestController
public class ActivityController {
	private ActivitiesService activitiesService;
	
	@Autowired
	public void setActivityService(ActivitiesService activitiesService ) {
		this.activitiesService = activitiesService;
	}

	@RequestMapping(
			method = RequestMethod.POST,
			path = "/iob/activities",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	
	public Object createActivityInstance(
			@RequestBody ActivityBoundary boundary) {
		return this.activitiesService.invokeActivity(boundary);
	}
}
