package iob.utility;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import iob.logic.instances.InstanceBoundary;
import iob.logic.users.NewUserBoundary;

@Component
@ConfigurationProperties(prefix = "test-objects")
public class TestProperties {

	NewUserBoundary newAdmin;
	NewUserBoundary newManager;
	NewUserBoundary newPlayer;

	InstanceBoundary instance;

	String testDomain;

	public TestProperties() {

	}

	public NewUserBoundary getNewAdmin() {
		return newAdmin;
	}

	public void setNewAdmin(NewUserBoundary newAdmin) {
		this.newAdmin = newAdmin;
	}

	public NewUserBoundary getNewManager() {
		return newManager;
	}

	public void setNewManager(NewUserBoundary newManager) {
		this.newManager = newManager;
	}

	public NewUserBoundary getNewPlayer() {
		return newPlayer;
	}

	public void setNewPlayer(NewUserBoundary newPlayer) {
		this.newPlayer = newPlayer;
	}

	public InstanceBoundary getInstance() {
		return instance;
	}

	public void setInstance(InstanceBoundary instance) {
		this.instance = instance;
	}

	public void setTestDomain(String testDomain) {
		this.testDomain = testDomain;
	}
}
