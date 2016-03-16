package com.ryce.frugalist;

import java.util.List;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.NotFoundException;
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

	/**
	 * LIST ALL USERS
	 * @param request
	 * @return
	 */
	@ApiMethod(name = "user.list",
		       path = "user/list",
		       httpMethod = HttpMethod.GET)
	public List<User> listUsers(
			HttpServletRequest request
		) {
		List<User> users = ObjectifyService.ofy()
				.load()
				.type(User.class)
				.list();
		return users;
	}
	
	/**
	 * GET USER BY ID
	 * @param request
	 * @return
	 */
	@ApiMethod(name = "user",
		       path = "user",
		       httpMethod = HttpMethod.GET)
	public User getUser(
			HttpServletRequest request,
			@Named("id") String id
		) {
		Key<User> key = Key.create(User.class, id);
		User user = ObjectifyService.ofy().load().key(key).now();
		return user;
	}
	
	/**
	 * GET USER BY ID
	 * @param request
	 * @param id
	 * @param dealId
	 * @return
	 * @throws NotFoundException 
	 */
	@ApiMethod(name = "user.update.addbookmark",
		       path = "user/update/addbookmark",
		       httpMethod = HttpMethod.PUT)
	public User addBookmark(
			HttpServletRequest request,
			@Named("id") String id,
			@Named("dealId") Long dealId
		) throws NotFoundException {
		
		Key<User> key = Key.create(User.class, id);
		User user = ObjectifyService.ofy().load().key(key).now();
		
		if (user == null)
			throw new NotFoundException("User not found");
		
		// add deal id to bookmarks
		user.getBookmarks().add(dealId);
		
		// update user
		ObjectifyService.ofy().save().entity(user).now();
		
		return user;
	}
	
	/**
	 * ADD A USER
	 * @param request
	 * @param id
	 * @param name
	 * @return
	 */
	@ApiMethod(name = "user.add",
		       path = "user/add",
		       httpMethod = HttpMethod.POST)
	public User addUser(
			HttpServletRequest request,
			@Named("id") String id,
			@Named("name") String name
		) {
		User user = new User(id, name);
		Key<User> userKey = 
				ObjectifyService.ofy()
					.save()
					.entity(user)
					.now();
		return user;
	}
	
}
