package iob.rest_api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import iob.logic.ExtendedUsersService;
import iob.logic.users.NewUserBoundary;
import iob.logic.users.UserBoundary;



@RestController
public class UsersController {
	private ExtendedUsersService service;
	
	@Autowired
	public void setUserService(ExtendedUsersService userService) {
		this.service = userService;
	}

	@RequestMapping(
			method = RequestMethod.POST, 
			path = "/iob/users", 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary createNewUser(@RequestBody NewUserBoundary newUser) {
		return service.createUser(newUser);
	}

	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/iob/users/login/{userDomain}/{userEmail}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary login(@PathVariable("userDomain") String userDomain, 
			@PathVariable("userEmail") String userEmail) {
		return service.login(userDomain, userEmail);
	}

	@RequestMapping(
			method = RequestMethod.PUT, 
			path = "/iob/users/{userDomain}/{userEmail}",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateUser(@RequestBody UserBoundary user, @PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail) {
		service.updateUser(userDomain, userEmail, user);
	}
}
