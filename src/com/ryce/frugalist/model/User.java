package com.ryce.frugalist.model;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class User {
	@Id String id;	
	String name;
	
	// A set of bookmarked listings
	Set<Long> bookmarks = new HashSet<Long>();
	
	private User() {
	}
	
	public User(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Set<Long> getBookmarks() {
		return bookmarks;
	}
	public void setBookmarks(Set<Long> bookmarks) {
		this.bookmarks = bookmarks;
	}
	
}
