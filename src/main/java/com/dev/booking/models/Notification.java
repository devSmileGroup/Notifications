package com.dev.booking.models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class Notification {
	@Id
	private ObjectId id;
	
	private String title;
	private String text;
	
	private Object status;
	
	private ObjectId userId;
	
	private Date dateCreation;
	private Date dateModify;
	
	public Notification() {}
	
	public Notification(ObjectId id, String title, String text,
			Object status, ObjectId userId,
					    Date dateCreation, Date dateModify) {
		this.id = id;
		this.title = title;
		this.text = text;
		this.status = status;
		this.userId = userId;
		this.dateCreation = dateCreation;
		this.dateModify = dateModify;
	}
}
