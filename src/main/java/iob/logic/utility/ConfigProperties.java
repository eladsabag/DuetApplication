package iob.logic.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigProperties {

	@Value("${spring.application.name}")
	private String applicationDomain;
	
	@Value("${activity.like}")
	private String likedUser;
	
	@Value("${activity.like.userId}")
	private String userId;
	
	@Value("${user.property.domain}")
	private String userDomain;
	
	@Value("${user.property.email}")
	private String userEmail;
	
	@Value("${like.match}")
	private String match;

	public String getApplicationDomain() {
		return applicationDomain;
	}

	public String getUserId() {
		return userId;
	}

	public String getlikedUser() {
		return likedUser;
	}
	
	public String getUserDomain() {
		return userDomain;
	}
	
	public String getUserEmail() {
		return userEmail;
	}

	public String getMatch() {
		return match;
	}
}
