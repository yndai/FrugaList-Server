package com.ryce.frugalist.model;

import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Rating;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Deal {
	@Parent Key<User> author;
	@Id Long Id;
	
	// GeoPt location;
	// Rating rating;
	
	String description;
	
	private Deal() {
	}
	
	public Deal(String userId, String description) {
		if (userId != null) {
			this.author = Key.create(User.class, userId);
		} else {
			this.author = Key.create(User.class, "_anonymous");
		}
		this.description = description;
	}

	public String getAuthor() {
		return author.getName();
	}
//	public void setAuthor(Key<User> author) {
//		this.author = author;
//	}
	
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
