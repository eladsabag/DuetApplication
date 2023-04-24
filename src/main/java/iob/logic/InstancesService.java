package iob.logic;

import java.util.List;

import iob.logic.instances.InstanceBoundary;

public interface InstancesService {
	
	public InstanceBoundary createInstance(InstanceBoundary insatance);
	
	@Deprecated
	public InstanceBoundary updateInstance(String instanceDomain, String instanceId, InstanceBoundary update);
	
	@Deprecated
	public InstanceBoundary getSpecificInstance(String instanceDomain, String instanceId);
	 
	@Deprecated
	public List<InstanceBoundary> getAllInstances();
	
	@Deprecated
	public void deleteAllInstances();
}
