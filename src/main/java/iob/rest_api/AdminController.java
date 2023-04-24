package iob.rest_api;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import iob.logic.ExtendedActivitiesService;
import iob.logic.ExtendedInstancesService;
import iob.logic.ExtendedUsersService;
import iob.logic.activities.ActivityBoundary;
import iob.logic.users.UserBoundary;

@RestController
public class AdminController {
	private ExtendedInstancesService instanceService;
	private ExtendedUsersService userService;
	private ExtendedActivitiesService activitiesService;
	
	@Autowired
	public void setInstanceService(ExtendedInstancesService instanceService, 
			ExtendedUsersService userService, ExtendedActivitiesService activitiesSrevice ) {
		this.instanceService = instanceService;
		this.userService = userService;
		this.activitiesService = activitiesSrevice;
	}

	@RequestMapping(
			method = RequestMethod.DELETE, 
			path = "/iob/admin/users")
	public void deleteAllUsers(
			@RequestParam("userDomain") String domain,
			@RequestParam("userEmail") String email) {
		this.userService.deleteAllUsers(domain, email);
	}

	@RequestMapping(
			method = RequestMethod.DELETE, 
			path = "/iob/admin/instances")
	public void deleteAllInstances(
			@RequestParam(name = "userDomain") String domain,
			@RequestParam(name = "userEmail") String email) {
		this.instanceService.deleteAllInstances(domain, email);
	}

	@RequestMapping(
			method = RequestMethod.DELETE, 
			path = "/iob/admin/activities")
	public void deleteAllActivities(
			@RequestParam("userDomain") String domain,
			@RequestParam("userEmail") String email) {
		this.activitiesService.deleteAllActivities(domain, email);
	}

	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/iob/admin/users", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public List<UserBoundary> exportAllUsers(
			@RequestParam("userDomain") String domain,
			@RequestParam("userEmail") String email,
			@RequestParam(name="size", required = false, defaultValue = "10") int size,
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		return this.userService.getAllUsers(size,page, domain, email);
	}

	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/iob/admin/activities", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ActivityBoundary> exportAllActivities(
			@RequestParam("userDomain") String domain,
			@RequestParam("userEmail") String email,
			@RequestParam(name="size", required = false, defaultValue = "10") int size,
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		return this.activitiesService.getAllActivities(size, page, domain, email);
	}
}
