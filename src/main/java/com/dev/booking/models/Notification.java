package com.dev.booking.models;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

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
	@Field("email_info")
	private EmailInfo emailInfo;
	@Field("ui_status")
	private UIStatus uiStatus;
	@JsonSerialize(using = ToStringSerializer.class)
	@Field("user_id")
	private ObjectId userId;
	
	@CreatedDate
	@Field("created_date")
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Field("modified_date")
    private LocalDateTime modifiedDate;
}
