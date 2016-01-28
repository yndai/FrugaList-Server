package com.ryce.frugalist;

import java.util.List;

import javax.inject.Named;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.appengine.api.datastore.Email;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.ryce.frugalist.model.Deal;
import com.ryce.frugalist.model.User;
import com.google.api.server.spi.config.ApiNamespace;

/** An endpoint class we are exposing */
@Api(name = "frugalist",
     version = "v1",
     namespace = @ApiNamespace(ownerDomain = "frugalist.ryce.com",
                               ownerName = "RYCE",
                               packagePath=""),
     title = "Frugalist API",
     description = "FrugaList API",
	 canonicalName = "FrugaList",
	 transformers = {} // used to transform 1 object into another (useful for formatting json)
	 )
public class FrugaListApi {

	@ApiMethod(name = "user.list",
		       path = "get/user/list",
		       httpMethod = HttpMethod.GET)
	public List<User> listUsers() {
		List<User> users = ObjectifyService.ofy()
				.load().type(User.class).list();
		return users;
	}
	
	@ApiMethod(name = "user.add",
		       path = "post/user/add",
		       httpMethod = HttpMethod.POST)
	public User addUser(
			@Named("id") String id,
			@Named("email") String email) 
	{
		User user = new User(id, new Email(email));
		Key<User> userKey = ObjectifyService.ofy().save().entity(user).now();
		return user;
	}
	
	@ApiMethod(name = "deal.list",
		       path = "get/deal/list",
		       httpMethod = HttpMethod.GET)
	public List<Deal> listDeals() {
		List<Deal> deals = ObjectifyService.ofy()
				.load().type(Deal.class).list();
		return deals;
	}
	
	@ApiMethod(name = "deal.add",
		       path = "post/deal/add",
		       httpMethod = HttpMethod.POST)
	public Deal addDeal(
			@Named("authorId") String id,
			@Named("description") String description) 
	{
		Deal deal = new Deal(id, description);
		Key<Deal> dealKey = ObjectifyService.ofy().save().entity(deal).now();
		return deal;
	}
	
}
