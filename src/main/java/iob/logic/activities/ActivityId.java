package iob.logic.activities;

import org.springframework.beans.factory.annotation.Value;

public class ActivityId {

	@Value("${spring.application.name}")
	private String domain;
	private String id;

	public ActivityId() {
	}

	public ActivityId(String domain, String id) {
		this();
		this.domain = domain;
		this.id = id;
	}
	
	public ActivityId(ActivityId other) {
		this(other.domain, other.id);
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
