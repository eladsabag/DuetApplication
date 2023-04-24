package iob.logic;

import java.util.List;

import iob.logic.instances.InstanceBoundary;

public interface ExtendedInstancesService extends InstancesService {
	
	public List<InstanceBoundary> getInstancesByName(String name, String userDomain, String userEmail, int size, int page);

	public List<InstanceBoundary> getInstancesByType(String type, String userDomain, String userEmail, int size, int page);

	public List<InstanceBoundary> getInstancesNear(double lat, double lng, double distance, String userDomain, String userEmail, int size, int page);

	public InstanceBoundary updateInstance(String instanceDomain, String instanceId, InstanceBoundary update, String userDomain, String userEmail);

	public InstanceBoundary getSpecificInstance(String instanceDomain, String instanceId, String userDomain, String userEmail);

	public List<InstanceBoundary> getAllInstances(String userDomain, String userEmail, int size, int page);
	
	public void deleteAllInstances(String userDomain, String userEmail);

}
