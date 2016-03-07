package com.ryce.frugalist.model;

import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Deal {
	@Id Long id;
	
	Key<User> author;
	@Index String product;
	String imageUrl;
	String address;
	@Index GeoPt location;
	
	String price;
	String unit;
	@Index String store;
	@Index Integer rating;
	
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

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
