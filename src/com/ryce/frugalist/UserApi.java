package com.ryce.frugalist;

import java.util.List;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;
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
	 * GET USER BY ID OR CREATE
	 * @param request
	 * @return
	 */
	@ApiMethod(name = "user.getOrCreate",
		       path = "user/getOrCreate",
		       httpMethod = HttpMethod.GET)
	public User getOrCreateUser(
			HttpServletRequest request,
			@Named("id") String id,
			@Nullable @Named("name") String name
		) {
		
		// fetch user
		Key<User> key = Key.create(User.class, id);
		User user = ObjectifyService.ofy().load().key(key).now();
		
		// if user does not exist, create and save
		if (user == null) {
			user = new User(id, name);
			ObjectifyService.ofy().save().entity(user).now();
		}
		
		return user;
	}
	
	/**
	 * ADD OR DELETE BOOKMARK
	 * @param request
	 * @param id
	 * @param dealId
	 * @param add
	 * @return
	 * @throws NotFoundException 
	 */
	@ApiMethod(name = "user.update.bookmark",
		       path = "user/update/bookmark",
		       httpMethod = HttpMethod.PUT)
	public User addBookmark(
			HttpServletRequest request,
			@Named("id") String id,
			@Named("dealId") Long dealId,
			@Named("add") Boolean add
		) throws NotFoundException {
		
		Key<User> key = Key.create(User.class, id);
		User user = ObjectifyService.ofy().load().key(key).now();
		
		if (user == null)
			throw new NotFoundException("User not found");
		
		// add or delete the bookmark
		if (add) {
			// add deal id to bookmarks
			user.getBookmarks().add(dealId);
		} else {
			// remove deal id from bookmarks
			user.getBookmarks().remove(dealId);
		}
		
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
