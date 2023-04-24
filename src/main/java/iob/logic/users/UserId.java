package iob.logic.users;

import org.springframework.beans.factory.annotation.Value;

public class UserId {
	@Value("${spring.application.name}")
	private String domain;
	private String email;

	public UserId() {

	}

	public UserId(String email, String domain) {
		this.email = email;
		this.domain = domain;
	}

	public String getDomain() {
		return domain;
	}

	@Value("{spring.application.name}")
	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
	        return true;
	    if (!(o instanceof UserId))
	        return false;
	    UserId other = (UserId) o;
	    return domain.equals(other.domain) && email.equals(other.email);
	}
}
