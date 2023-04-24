package iob.rest_api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import iob.logic.ExtendedInstancesService;
import iob.logic.instances.InstanceBoundary;

@RestController
public class InstanceController {
	private ExtendedInstancesService instanceService;
	
	@Autowired
	public void setInstanceService(ExtendedInstancesService instanceService) {
		this.instanceService = instanceService;
	}

	@RequestMapping(
			method = RequestMethod.POST, 
			path = "/iob/instances", 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary createInstance(@RequestBody InstanceBoundary boundary) {
		return instanceService.createInstance(boundary);
	}

	@RequestMapping(
			method = RequestMethod.PUT, 
			path = "/iob/instances/{instanceDomain}/{instanceId}", 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateInstance(
			@PathVariable("instanceDomain") String instanceDomain,
			@PathVariable("instanceId") String instanceId,
			@RequestParam(name="userDomain") String userDomain,
			@RequestParam(name="userEmail") String userEmail,
			@RequestBody InstanceBoundary boundary) {
		
		instanceService.updateInstance(instanceDomain, instanceId, boundary, userDomain, userEmail);
	}

	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/iob/instances/{instanceDomain}/{instanceId}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary retrieveInstance(
			@PathVariable("instanceDomain") String instanceDomain,
			@PathVariable("instanceId") String instanceId,
			@RequestParam(name="userDomain") String userDomain,
			@RequestParam(name="userEmail") String userEmail) {
		
		return instanceService.getSpecificInstance(instanceDomain, instanceId, userDomain, userEmail);
	}

	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/iob/instances", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary[] getAllInstances(
			@RequestParam(name="userDomain") String userDomain,
			@RequestParam(name="userEmail") String userEmail,
			@RequestParam(name="size", required = false, defaultValue = "10") int size,
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		
		List<InstanceBoundary> list = instanceService.getAllInstances(userDomain, userEmail, size, page);
		return list.toArray(new InstanceBoundary[0]);
	}
	
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/iob/instances/search/byName/{name}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary[] getInstancesByName(
		@PathVariable("name") String name,
		@RequestParam(name="userDomain") String userDomain,
		@RequestParam(name="userEmail") String userEmail,
		@RequestParam(name="size", required = false, defaultValue = "10") int size,
		@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		
		List<InstanceBoundary> list = instanceService.getInstancesByName(name, userDomain, userEmail, size, page);
		
		return list.toArray(new InstanceBoundary[0]);
	}
	
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/iob/instances/search/byType/{type}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary[] getInstancesByType(
		@PathVariable("type") String type,
		@RequestParam(name="userDomain") String userDomain,
		@RequestParam(name="userEmail") String userEmail,
		@RequestParam(name="size", required = false, defaultValue = "10") int size,
		@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		
		List<InstanceBoundary> list = instanceService.getInstancesByType(type, userDomain, userEmail, size, page);
		
		return list.toArray(new InstanceBoundary[0]);
	}
	
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/iob/instances/search/near/{lat}/{lng}/{distance}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary[] getInstancesNear(
		@PathVariable("lat") double lat,
		@PathVariable("lng") double lng,
		@PathVariable("distance") double distance,
		@RequestParam(name="userDomain") String userDomain,
		@RequestParam(name="userEmail") String userEmail,
		@RequestParam(name="size", required = false, defaultValue = "10") int size,
		@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		
		List<InstanceBoundary> list = instanceService.getInstancesNear(lat, lng, distance, userDomain, userEmail, size, page);
		
		return list.toArray(new InstanceBoundary[0]);
	}
	

}
