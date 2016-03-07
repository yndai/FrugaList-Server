package com.ryce.frugalist.model;

import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Freebie {
	@Id Long id;
	
	Key<User> author;
	@Index String product;
	String imageUrl;
	String address;
	@Index GeoPt location;
	
	String description;
	
	private Freebie() {
	}
	
	public Freebie(
			String userId, 
			String product, 
			String imageUrl, 
			String address, 
			Float latitude,
			Float longitude, 
			String description) {
		this.author = Key.create(User.class, userId);
		this.product = product;
		this.imageUrl = imageUrl;
		this.address = address;
		this.location = new GeoPt(latitude, longitude);
		this.description = description;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getAuthor() {
		return author.getName();
	}
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

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
