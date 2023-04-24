package iob.logic;

import java.util.List;

import iob.logic.users.UserBoundary;

public interface ExtendedUsersService extends UsersService {
	public List<UserBoundary> getAllUsers(int size, int page, String domain, String email);
	public List<UserBoundary> getUsersByVersion(int version, int size, int page);
}
