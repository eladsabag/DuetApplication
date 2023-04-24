package iob.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import iob.logic.activities.ActivityBoundary;
import iob.logic.instances.InstanceBoundary;
import iob.logic.users.NewUserBoundary;
import iob.logic.users.UserBoundary;

@Component
public class ApTestRequester {

	private RestTemplate restTemplate;

	@Autowired
	public ApTestRequester(Environment environment) {
		LocalHostUriTemplateHandler localhostHandler = new LocalHostUriTemplateHandler(environment);
		restTemplate = new RestTemplate();
		restTemplate.setUriTemplateHandler(localhostHandler);
	}

	public UserBoundary postNewUserForUser(NewUserBoundary newUser) {
		return restTemplate.postForObject("/iob/users", newUser, UserBoundary.class);
	}

	public UserBoundary getUser(String userDomain, String userEmail) {
		return restTemplate.getForObject("/iob/users/login/{userDomain}/{userEmail}", UserBoundary.class, userDomain,
				userEmail);
	}

	public UserBoundary[] getUsers(String adminDomain, String adminEmail) {

		return restTemplate.getForObject("/iob/admin/users?userDomain={userDomain}&userEmail={userEmail}",
				UserBoundary[].class, adminDomain, adminEmail);
	}

	public UserBoundary[] getUsers(String adminDomain, String adminEmail, int size, int page) {

		return restTemplate.getForObject(
				"/iob/admin/users?userDomain={userDomain}&userEmail={userEmail}&size={size}&page={page}",
				UserBoundary[].class, adminDomain, adminEmail, size, page);
	}

	public void putUser(UserBoundary user, String userDomain, String userEmail) {
		restTemplate.put("/iob/users/{userDomain}/{userEmail}", user, userDomain, userEmail);
	}

	public void deleteAllUsers(String adminDomain, String adminEmail) {
		restTemplate.delete("/iob/admin/users?userDomain={domain}&userEmail={email}", adminDomain, adminEmail);
	}

	public InstanceBoundary postInstanceForInstance(InstanceBoundary instance) {
		return restTemplate.postForObject("/iob/instances", instance, InstanceBoundary.class);
	}

	public void putInstance(InstanceBoundary instance, String instanceDomain, String instanceId, String userDomain,
			String userEmail) {
		restTemplate.put("/iob/instances/{instanceDomain}/{instanceId}?userDomain={domain}&userEmail={email}", instance,
				instanceDomain, instanceId, userDomain, userEmail);
	}

	public InstanceBoundary getInstance(String instanceDomain, String instanceId, String userDomain, String userEmail) {
		return restTemplate.getForObject(
				"/iob/instances/{instanceDomain}/{instanceId}?userDomain={domain}&userEmail={email}",
				InstanceBoundary.class, instanceDomain, instanceId, userDomain, userEmail);
	}

	public InstanceBoundary[] getInstances(String userDomain, String userEmail, int size, int page) {
		return restTemplate.getForObject("/iob/instances?userDomain={domain}&userEmail={email}&size={size}&page={page}",
				InstanceBoundary[].class, userDomain, userEmail, size, page);
	}

	public InstanceBoundary[] getInstances(String userDomain, String userEmail) {
		return restTemplate.getForObject("/iob/instances?userDomain={domain}&userEmail={email}",
				InstanceBoundary[].class, userDomain, userEmail);
	}

	public InstanceBoundary[] getInstancesByName(String name, String userDomain, String userEmail, int size, int page) {
		return restTemplate.getForObject(
				"/iob/instances/search/byName/{name}?userDomain={domain}&userEmail={email}&size={size}&page={page}",
				InstanceBoundary[].class, name, userDomain, userEmail, size, page);
	}

	public InstanceBoundary[] getInstancesByName(String name, String userDomain, String userEmail) {
		return restTemplate.getForObject("/iob/instances/search/byName/{name}?userDomain={domain}&userEmail={email}",
				InstanceBoundary[].class, name, userDomain, userEmail);
	}

	public InstanceBoundary[] getInstancesByType(String type, String userDomain, String userEmail, int size, int page) {
		return restTemplate.getForObject(
				"/iob/instances/search/byType/{type}?userDomain={domain}&userEmail={email}&size={size}&page={page}",
				InstanceBoundary[].class, type, userDomain, userEmail, size, page);
	}

	public InstanceBoundary[] getInstancesByType(String type, String userDomain, String userEmail) {
		return restTemplate.getForObject("/iob/instances/search/byType/{type}?userDomain={domain}&userEmail={email}",
				InstanceBoundary[].class, type, userDomain, userEmail);
	}

	public InstanceBoundary[] getInstancesByLocation(double lat, double lng, double distance, String userDomain,
			String userEmail, int size, int page) {
		return restTemplate.getForObject(
				"/iob/instances/search/near/{lat}/{lng}/{distance}?userDomain={domain}&userEmail={email}&size={size}&page={page}",
				InstanceBoundary[].class, lat, lng, distance, userDomain, userEmail, size, page);
	}

	public InstanceBoundary[] getInstancesByLocation(double lat, double lng, double distance, String userDomain,
			String userEmail) {
		return restTemplate.getForObject(
				"/iob/instances/search/near/{lat}/{lng}/{distance}?userDomain={domain}&userEmail={email}",
				InstanceBoundary[].class, lat, lng, distance, userDomain, userEmail);
	}

	public void deleteAllInstances(String adminDomain, String adminEmail) {
		restTemplate.delete("/iob/admin/instances?userDomain={domain}&userEmail={email}", adminDomain, adminEmail);

	}

	public Object postActivityForObject(ActivityBoundary activity) {
		return restTemplate.postForObject("/iob/activities", activity, ActivityBoundary.class);
	}

	public ActivityBoundary[] getActivities(String adminDomain, String adminEmail, int size, int page) {
		return restTemplate.getForObject(
				"/iob/admin/activities?userDomain={userDomain}&userEmail={userEmail}&size={size}&page={page}",
				ActivityBoundary[].class, adminDomain, adminEmail, size, page);
	}

	public ActivityBoundary[] getActivities(String adminDomain, String adminEmail) {
		return restTemplate.getForObject("/iob/admin/activities?userDomain={userDomain}&userEmail={userEmail}",
				ActivityBoundary[].class, adminDomain, adminEmail);
	}

	public void deleteAllActivities(String adminDomain, String adminEmail) {
		restTemplate.delete("/iob/admin/activities?userDomain={domain}&userEmail={email}", adminDomain, adminEmail);
	}

	public void deleteAll(String adminDomain, String adminEmail) {
		deleteAllActivities(adminDomain, adminEmail);
		deleteAllInstances(adminDomain, adminEmail);
		deleteAllUsers(adminDomain, adminEmail);

	}
}