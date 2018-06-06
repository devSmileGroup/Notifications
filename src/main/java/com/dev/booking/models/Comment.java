package com.dev.booking.models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Comment {

    @Id 
    private ObjectId id;
    
    private String title;
    private String text;
    private Integer rating;
    
    private ObjectId userId;
    private ObjectId apartmentId;
    
    @CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date modifiedDate;
    
    public Comment() {}

	public Comment(ObjectId id, String title, String text,
				   Integer rating, ObjectId userId, ObjectId apartmentId,
				   Date createdDate, Date modifiedDate) {
		this.id = id;
		this.title = title;
		this.text = text;
		this.rating = rating;
		this.userId = userId;
		this.apartmentId = apartmentId;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}
}