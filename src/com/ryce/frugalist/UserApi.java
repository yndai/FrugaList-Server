package com.ryce.frugalist;

import java.util.List;

import javax.inject.Named;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.Email;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.ryce.frugalist.model.User;

@Api(
	name = "frugalist",
	version = "v1",
	namespace = @ApiNamespace(
			ownerDomain = "frugalist.ryce.com",
	        ownerName = "RYCE",
	        packagePath=""
	),
	title = "Frugalist API",
	description = "FrugaList API",
	canonicalName = "FrugaList",
	transformers = {} // used to transform 1 object into another (useful for formatting json)
)
public class UserApi {

	@ApiMethod(name = "user.list",
		       path = "user/list",
		       httpMethod = HttpMethod.GET)
	public List<User> listUsers() {
		List<User> users = ObjectifyService.ofy()
				.load()
				.type(User.class)
				.list();
		return users;
	}
	
	@ApiMethod(name = "user.add",
		       path = "user/add",
		       httpMethod = HttpMethod.POST)
	public User addUser(
			@Named("id") String id,
			@Named("name") String name,
			@Named("email") String email) 
	{
		User user = new User(id, name, new Email(email));
		Key<User> userKey = 
				ObjectifyService.ofy()
					.save()
					.entity(user)
					.now();
		return user;
	}
	
}
