package com.dev.booking.models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Comment {

    @Id
    private ObjectId id;
    
    private String title;
    private String text;
    private Integer rating;
    
    private ObjectId userId;
    private ObjectId apartmentId;
    
    private String dateCreation;
    private String dateModify;
    
    public Comment() {}

	public Comment(String title, String text,
				   Integer rating, String userId,
				   String apartmentId, String dateCreation,
				   String dateModify) {
		this.id = new ObjectId();
		this.title = title;
		this.text = text;
		this.rating = rating;
		this.userId = new ObjectId();
		this.apartmentId = new ObjectId();
		this.dateCreation = new Date().toString();
		this.dateModify = new Date().toString();
	}



	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public ObjectId getApartmentId() {
		return apartmentId;
	}

	public void setApartmentId(ObjectId apartmentId) {
		this.apartmentId = apartmentId;
	}

	public String getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(String dateCreation) {
		this.dateCreation = dateCreation;
	}

	public String getDateModify() {
		return dateModify;
	}

	public void setDateModify(String dateModify) {
		this.dateModify = dateModify;
	}

}