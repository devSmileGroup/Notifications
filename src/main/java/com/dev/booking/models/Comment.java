package com.dev.booking.models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
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
}