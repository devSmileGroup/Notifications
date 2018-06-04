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
    
    private Date dateCreation;
    private Date dateModify;
    
    public Comment() {}

	public Comment(ObjectId id, String title, String text,
				   Integer rating, ObjectId userId, ObjectId apartmentId,
				   Date dateCreation, Date dateModify) {
		this.id = id;
		this.title = title;
		this.text = text;
		this.rating = rating;
		this.userId = userId;
		this.apartmentId = apartmentId;
		this.dateCreation = dateCreation;
		this.dateModify = dateModify;
	}
	
	public Comment(String title, String text, Integer rating,
				   ObjectId userId, ObjectId apartmentId) {
		this(new ObjectId(), title, text, rating, userId, apartmentId, new Date(), new Date());
	}
	
	public Comment(ObjectId id, String title, String text,
			   Integer rating, ObjectId userId, ObjectId apartmentId,
			   Date dateCreation) {
		this(id, title, text, rating, userId, apartmentId, dateCreation, new Date());
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

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Date getDateModify() {
		return dateModify;
	}

	public void setDateModify(Date dateModify) {
		this.dateModify = dateModify;
	}

}