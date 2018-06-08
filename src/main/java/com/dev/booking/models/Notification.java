package com.dev.booking.models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Data;

@Data
public class Notification {
	
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId id;
	private String title;
	private String text;
	private Object status;
	private ObjectId userId;
	
	@CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date modifiedDate;
	
    
	public Notification() {}
	
	public Notification(ObjectId id, String title, String text,
			Object status, ObjectId userId,
					    Date createdDate, Date modifiedDate) {
		this.id = id;
		this.title = title;
		this.text = text;
		this.status = status;
		this.userId = userId;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}
}
