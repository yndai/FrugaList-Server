package com.ryce.frugalist;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Named;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.NotFoundException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.ryce.frugalist.model.Deal;
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

	/**
	 * GET ALL DEALS
	 * - Descending order by create date
	 * @return
	 */
	@ApiMethod(name = "deal.list",
		       path = "deal/list",
		       httpMethod = HttpMethod.GET)
	public List<Deal> listDeals() {
		List<Deal> deals = ObjectifyService.ofy()
				.load().type(Deal.class).order("-created").list();
		return deals;
	}
	
	/**
	 * SEARCH DEALS BY PRODUCT
	 * - Descending order by create date
	 * - Filtered for nearest
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
			@Named("product") String product,
			@Named("latitude") Float latitude,
			@Named("longitude") Float longitude,
			@Named("radius") Integer radius
		) {
		List<Deal> deals = ObjectifyService.ofy()
				.load().type(Deal.class).order("-created").list();
		
		List<Deal> filtered = new LinkedList<Deal>();
		
		// Note: GAE does not support substring filter,
		// so just manually filter
		final String lcProduct = product.toLowerCase();
		for (Deal deal : deals) {
			if (deal.getProduct().toLowerCase().contains(lcProduct)) {
				filtered.add(deal);
			}
		}
		
		// filter for nearest
		return filterNearest(filtered, latitude, longitude, radius);
	}
	
	/**
	 * SEARCH DEALS BY STORE
	 * - Descending order by create date
	 * - Filtered for nearest
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
			@Named("store") String store,
			@Named("latitude") Float latitude,
			@Named("longitude") Float longitude,
			@Named("radius") Integer radius
		) {
		List<Deal> deals = ObjectifyService.ofy()
				.load().type(Deal.class).order("-created").list();
		
		List<Deal> filtered = new LinkedList<Deal>();
		
		// Note: GAE does not support substring filter,
		// so just manually filter
		final String lcStore = store.toLowerCase();
		for (Deal deal : deals) {
			if (deal.getStore().toLowerCase().contains(lcStore)) {
				filtered.add(deal);
			}
		}
		
		// filter for nearest
		return filterNearest(filtered, latitude, longitude, radius);
	}
	
	/**
	 * GET NEAREST DEALS
	 * - Descending order by created date
	 * @param latitude
	 * @param longitude
	 * @param radius (in KM)
	 * @return
	 */
	@ApiMethod(name = "deal.list.near",
		       path = "deal/list.near",
		       httpMethod = HttpMethod.GET)
	public List<Deal> nearestDeals(
			@Named("latitude") Float latitude,
			@Named("longitude") Float longitude,
			@Named("radius") Integer radius
		) {
		
		List<Deal> deals = ObjectifyService.ofy()
				.load().type(Deal.class).order("-created").list();
		
		// filter for nearest
		List<Deal> filtered = filterNearest(deals, latitude, longitude, radius);
		
		return filtered;
	}
	
	/**
	 * GET DEAL BY ID
	 * @param id
	 * @return
	 * @throws com.google.api.server.spi.response.NotFoundException 
	 */
	@ApiMethod(name = "deal",
		       path = "deal",
		       httpMethod = HttpMethod.GET)
	public Deal getDeal(
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
	 * @param id
	 * @return
	 */
	@ApiMethod(name = "deal.update.rating",
		       path = "deal/update/rating",
		       httpMethod = HttpMethod.PUT)
	public Deal updateDealRating(
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
	 * @param id
	 * @return
	 */
	@ApiMethod(name = "deal.delete",
		       path = "deal/delete",
		       httpMethod = HttpMethod.DELETE)
	public ResponseMsg deleteDeal(
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
