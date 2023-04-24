package iob.logic.users;

import org.springframework.stereotype.Component;

import iob.data.UserEntity;

@Component
public class UserConverter {

	public UserEntity toEntity(UserBoundary boundary) {
		UserEntity entity = new UserEntity();
		entity.setEmail(boundary.getUserId().getEmail());
		entity.setDomain(boundary.getUserId().getDomain());
		entity.setAvatar(boundary.getAvatar());
		entity.setUserName(boundary.getUsername());
		entity.setRole(boundary.getRole());
		return entity;
	}

	public UserBoundary toBoundary(UserEntity entity) {
		UserBoundary boundary = new UserBoundary();
		boundary.setUserId(new UserId(entity.getEmail(), entity.getDomain()));
		boundary.setAvatar(entity.getAvatar());
		boundary.setUsername(entity.getUserName());
		boundary.setRole(entity.getRole());
		return boundary;
	}
}
