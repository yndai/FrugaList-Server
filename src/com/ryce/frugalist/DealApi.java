package com.ryce.frugalist;

import java.util.List;

import javax.inject.Named;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.GeoRegion;
import com.google.appengine.api.datastore.Query.StContainsFilter;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.ryce.frugalist.model.Deal;

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
	 * 
	 * @return
	 */
	@ApiMethod(name = "deal.list",
		       path = "deal/list",
		       httpMethod = HttpMethod.GET)
	public List<Deal> listDeals() {
		List<Deal> deals = ObjectifyService.ofy()
				.load().type(Deal.class).list();
		return deals;
	}
	
	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	@ApiMethod(name = "deal.list.near",
		       path = "deal/list.near",
		       httpMethod = HttpMethod.GET)
	public List<Deal> nearestDeals(
			@Named("latitude") Float latitude,
			@Named("longitude") Float longitude
		) {
		
		GeoPt center = new GeoPt(latitude, longitude);
		double radiusMeters = 100;
		
		Filter geoFilter = new StContainsFilter("location", new GeoRegion.Circle(center, radiusMeters));
		
		List<Deal> deals = ObjectifyService.ofy()
				.load()
				.type(Deal.class)
				.filter(geoFilter)
				.list();
		return deals;
	}
	
	/**
	 * 
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
			@Named("rating") Integer rating,
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
				rating,
				description);
		Key<Deal> dealKey = ObjectifyService.ofy().save().entity(deal).now();
		return deal;
	}
	
}
