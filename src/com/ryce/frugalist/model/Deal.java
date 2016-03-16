package com.ryce.frugalist.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Deal {
	@Id Long id;
	
	@Index Key<User> author;
	String product;
	String imageUrl;
	String address;
	GeoPt location;
	
	String price;
	String unit;
	String store;
	Integer rating;
	
	// store IDs of users who have upvoted/downvoted
	// value is true for an upvote
	Map<String, Boolean> votes = new HashMap<String, Boolean>();
	
	@Index Date created;
	String description;
	
	private Deal() {
	}
	
	public Deal(
			String userId, 
			String product, 
			String imageUrl, 
			String address, 
			Float latitude,
			Float longitude,
			String price,
			String unit, 
			String store, 
			Integer rating,
			Date created,
			String description) {
		this.author = Key.create(User.class, userId);
		this.product = product;
		this.imageUrl = imageUrl;
		this.address = address;
		this.location = new GeoPt(latitude, longitude);
		this.price = price;
		this.unit = unit;
		this.store = store;
		this.rating = rating;
		this.created = created;
		this.description = description;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getAuthor() {
		// output the nameId from the key
		return author.getName();
	}
	//@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setAuthor(Key<User> author) {
		this.author = author;
	}
	
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}

	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public GeoPt getLocation() {
		return location;
	}
	public void setLocation(GeoPt location) {
		this.location = location;
	}

	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}

	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getStore() {
		return store;
	}
	public void setStore(String store) {
		this.store = store;
	}

	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, Boolean> getVotes() {
		return votes;
	}
	
}
