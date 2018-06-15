package com.dev.booking.models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

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
	private UIStatus uiStatus;
	private EmailInfo emailInfo;
	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId userId;
	
	@CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date modifiedDate;
}
