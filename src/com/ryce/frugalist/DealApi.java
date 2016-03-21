package com.ryce.frugalist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.NotFoundException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.ryce.frugalist.model.Deal;
import com.ryce.frugalist.model.User;
import com.ryce.frugalist.util.Util;
import com.ryce.frugalist.util.Util.ResponseMsg;

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
public class DealApi {

	/** Enum of sort codes */	
	private static Map<Integer, String> sortTypeMap = new HashMap<Integer, String>();
	static {
		sortTypeMap.put(0, "-created"); // Date descending
		sortTypeMap.put(1, "-rating"); // Rating descending
		sortTypeMap.put(2, "price"); // Price ascending
	}
	
	/**
	 * GET ALL DEALS
	 * - Descending order by create date
	 * @param request
	 * @return
	 */
	@ApiMethod(name = "deal.list",
		       path = "deal/list",
		       httpMethod = HttpMethod.GET)
	public List<Deal> listDeals(
			HttpServletRequest request
		) {
		List<Deal> deals = ObjectifyService.ofy()
				.load()
				.type(Deal.class)
				.order("-created")
				.list();
		return deals;
	}
	
	/**
	 * SEARCH DEALS BY PRODUCT
	 * - Descending order by create date
	 * - Filtered for nearest
	 * @param request
	 * @param product
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @return
	 */
	@ApiMethod(name = "deal.search.product",
		       path = "deal/search/product",
		       httpMethod = HttpMethod.GET)
	public List<Deal> searchDealsByProduct(
			HttpServletRequest request,
			@Named("product") String product,
			@Nullable @Named("latitude") Float latitude,
			@Nullable @Named("longitude") Float longitude,
			@Nullable @Named("radius") Integer radius,
			@Nullable @Named("sortType") Integer sortType
		) {
		
		String order = sortType == null ? "-created" : sortTypeMap.get(sortType);
		
		List<Deal> deals = ObjectifyService.ofy()
				.load()
				.type(Deal.class)
				.order(order)
				.list();
		
		List<Deal> filtered = new LinkedList<Deal>();
		
		// Note: GAE does not support substring filter,
		// so just manually filter
		final String lcProduct = product.toLowerCase();
		for (Deal deal : deals) {
			if (deal.getProduct().toLowerCase().contains(lcProduct)) {
				filtered.add(deal);
			}
		}
		
		if (latitude != null && longitude != null && radius != null) {
			// filter for nearest
			return filterNearest(filtered, latitude, longitude, radius);
		} else {
			return filtered;
		}
	}
	
	/**
	 * SEARCH DEALS BY STORE
	 * - Descending order by create date
	 * - Filtered for nearest
	 * @param request
	 * @param store
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @return
	 */
	@ApiMethod(name = "deal.search.store",
		       path = "deal/search/store",
		       httpMethod = HttpMethod.GET)
	public List<Deal> searchDealsByStore(
			HttpServletRequest request,
			@Named("store") String store,
			@Nullable @Named("latitude") Float latitude,
			@Nullable @Named("longitude") Float longitude,
			@Nullable @Named("radius") Integer radius,
			@Nullable @Named("sortType") Integer sortType
		) {
		
		String order = sortType == null ? "-created" : sortTypeMap.get(sortType);
		
		List<Deal> deals = ObjectifyService.ofy()
				.load()
				.type(Deal.class)
				.order(order)
				.list();
		
		List<Deal> filtered = new LinkedList<Deal>();
		
		// Note: GAE does not support substring filter,
		// so just manually filter
		final String lcStore = store.toLowerCase();
		for (Deal deal : deals) {
			if (deal.getStore().toLowerCase().contains(lcStore)) {
				filtered.add(deal);
			}
		}
		
		if (latitude != null && longitude != null && radius != null) {
			// filter for nearest
			return filterNearest(filtered, latitude, longitude, radius);
		} else {
			return filtered;
		}
	}
	
	/**
	 * GET ALL DEALS BY AUTHOR
	 * - Descending order by create date
	 * @param request
	 * @param authorId
	 * @return
	 */
	@ApiMethod(name = "deal.list.byauthor",
		       path = "deal/list/byauthor",
		       httpMethod = HttpMethod.GET)
	public List<Deal> listDealsByAuthor(
			HttpServletRequest request,
			@Named("authorId") String authorId
		) {
		Key<User> authorKey = Key.create(User.class, authorId);
		
		List<Deal> deals = ObjectifyService.ofy()
				.load()
				.type(Deal.class)
				.filter("author", authorKey)
				.order("-created")
				.list();
		
		return deals;
	}
	
	/**
	 * GET ALL DEALS BOOKMARKED BY USER
	 * - Descending order by create date
	 * @param request
	 * @param authorId
	 * @return
	 * @throws NotFoundException 
	 */
	@ApiMethod(name = "deal.list.bookmarks",
		       path = "deal/list/bookmarks",
		       httpMethod = HttpMethod.GET)
	public Collection<Deal> listBookmarks(
			HttpServletRequest request,
			@Named("userId") String userId
		) throws NotFoundException {
		
		// fetch user
		Key<User> userKey = Key.create(User.class, userId);
		User user = ObjectifyService.ofy().load().key(userKey).now();
		
		if (user == null)
			throw new NotFoundException("User not found");
		
		// verify use actually has bookmarks
		if (!user.getBookmarks().isEmpty()) {
			
			// fetch deals within bookmarks set
			Collection<Deal> deals = ObjectifyService.ofy()
					.load()
					.type(Deal.class)
					.ids(user.getBookmarks())
					.values();
			
			return deals;
			
		} else {	
			// just return an empty list
			return new ArrayList<Deal>(0);
		}
		
	}
	
	/**
	 * GET NEAREST DEALS
	 * - Descending order by created date
	 * @param request
	 * @param latitude
	 * @param longitude
	 * @param radius (in KM)
	 * @return
	 */
	@ApiMethod(name = "deal.list.near",
		       path = "deal/list/near",
		       httpMethod = HttpMethod.GET)
	public List<Deal> nearestDeals(
			HttpServletRequest request,
			@Named("latitude") Float latitude,
			@Named("longitude") Float longitude,
			@Named("radius") Integer radius
		) {
		
		List<Deal> deals = ObjectifyService.ofy()
				.load()
				.type(Deal.class)
				.order("-created")
				.list();
		
		// filter for nearest
		List<Deal> filtered = filterNearest(deals, latitude, longitude, radius);
		
		return filtered;
	}
	
	/**
	 * GET DEAL BY ID
	 * @param request
	 * @param id
	 * @return
	 * @throws com.google.api.server.spi.response.NotFoundException 
	 */
	@ApiMethod(name = "deal",
		       path = "deal",
		       httpMethod = HttpMethod.GET)
	public Deal getDeal(
			HttpServletRequest request,
			@Named("id") Long id
		) throws NotFoundException {
		Key<Deal> key = Key.create(Deal.class, id);
		Deal deal = ObjectifyService.ofy().load().key(key).now();
		if (deal == null) 
			throw new NotFoundException("Deal not found");
		return deal;
	}
	
	/**
	 * ADD DEAL
	 * @param request
	 * @param authorId
	 * @param product
	 * @param imageUrl
	 * @param address
	 * @param latitude
	 * @param longitude
	 * @param price
	 * @param unit
	 * @param store
	 * @param rating
	 * @param description
	 * @return
	 */
	@ApiMethod(name = "deal.add",
		       path = "deal/add",
		       httpMethod = HttpMethod.POST)
	public Deal addDeal(
			HttpServletRequest request,
			@Named("authorId") String authorId,
			@Named("product") String product,
			@Named("imageUrl") String imageUrl,
			@Named("address") String address,
			@Named("latitude") Float latitude,
			@Named("longitude") Float longitude,
			@Named("price") String price,
			@Named("unit") String unit,
			@Named("store") String store,
			@Named("description") String description
		) {
		Deal deal = new Deal(
				authorId,
				product,
				imageUrl,
				address,
				latitude,
				longitude,
				price,
				unit,
				store,
				0,
				new Date(),
				description);
		Key<Deal> dealKey = ObjectifyService.ofy().save().entity(deal).now();
		return deal;
	}
	
	/**
	 * UPDATE DEAL RATING
	 * @param request
	 * @param id
	 * @return
	 */
	@ApiMethod(name = "deal.update.rating",
		       path = "deal/update/rating",
		       httpMethod = HttpMethod.PUT)
	public Deal updateDealRating(
			HttpServletRequest request,
			@Named("id") Long id,
			@Named("userId") String userId,
			@Named("upvote") Boolean upvote
	) throws NotFoundException {
		Key<Deal> dealKey = Key.create(Deal.class, id);
		Deal deal = ObjectifyService.ofy().load().key(dealKey).now();
		
		if (deal == null) 
			throw new NotFoundException("Deal not found");
		
		// determine if user has already voted
		final boolean voted = deal.getVotes().containsKey(userId);
		final boolean upvoted = voted && deal.getVotes().get(userId);
		final boolean downvoted = voted && !deal.getVotes().get(userId);
		
		// update deal
		// Note: not handling integer overflow because if we get 2 billion users
		// we would have better developers
		if (upvote && (!voted || downvoted)) {
			
			// increment rating and add user to upvoted set
			deal.setRating(deal.getRating() + 1);
			deal.getVotes().put(userId, true);
			
			// undo downvote if user previously downvoted
			if (downvoted) {
				deal.setRating(deal.getRating() + 1);
			}
			
			// do update
			ObjectifyService.ofy().save().entity(deal).now();
			
		} else if (!upvote && (!voted || upvoted)) {
			
			// decrement rating and add user to downvoted set
			deal.setRating(deal.getRating() - 1);
			deal.getVotes().put(userId, false);
			
			// undo upvote if user previously upvoted
			if (upvoted) {
				deal.setRating(deal.getRating() - 1);
			}
			
			// do update
			ObjectifyService.ofy().save().entity(deal).now();
			
		}
		
		return deal;
	}
	
	/**
	 * DELETE DEAL
	 * @param request
	 * @param id
	 * @return
	 */
	@ApiMethod(name = "deal.delete",
		       path = "deal/delete",
		       httpMethod = HttpMethod.DELETE)
	public ResponseMsg deleteDeal(
			HttpServletRequest request,
			@Named("id") Long id
	) {
		Key<Deal> key = Key.create(Deal.class, id);
		ObjectifyService.ofy().delete().key(key).now();
		return new ResponseMsg("Deal deleted");
	}
	
	/* ---------- HELPERS ---------- */
	
	/**
	 * Filter out deals that are further than a given radius
	 * @param deals
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @return
	 */
	private List<Deal> filterNearest(
			List<Deal> deals, double latitude, double longitude, int radius) {
		
		List<Deal> filtered = new LinkedList<Deal>();
		
		for (Deal deal : deals) {
			final double dist = Util.getDistanceH(
					deal.getLocation().getLatitude(), 
					deal.getLocation().getLongitude(), 
					latitude, 
					longitude);
			if (dist <= radius) {
				filtered.add(deal);
			}
		}
		
		return filtered;
	}
}
