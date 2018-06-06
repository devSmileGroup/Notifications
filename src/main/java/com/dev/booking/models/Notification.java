package com.dev.booking.models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Notification {
	@Id
	private ObjectId id;
	private String title;
	private String text;
	private String status;
	
	private ObjectId userId;
	
	private Date dateCreation;
	private Date dateModify;
	
	public Notification() {}
	
	public Notification(ObjectId id, String title, String text,
					    String status, ObjectId userId,
					    Date dateCreation, Date dateModify) {
		this.id = id;
		this.title = title;
		this.text = text;
		this.status = status;
		this.userId = userId;
		this.dateCreation = dateCreation;
		this.dateModify = dateModify;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ObjectId getUserId() {
		return userId;
	}
	public void setUserId(ObjectId userId) {
		this.userId = userId;
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
