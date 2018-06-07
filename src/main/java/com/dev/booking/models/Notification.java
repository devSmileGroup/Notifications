package com.dev.booking.models;

import java.util.Date;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;

@Data
public class Notification {
	@Id
	private ObjectId id;
	private String title;
	private String text;
	
	@Inject
	private Status status;
	
	private ObjectId userId;
	
	@CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date modifiedDate;
}
