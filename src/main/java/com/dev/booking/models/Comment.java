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
public class Comment {

    @Id 
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    
    private String title;
    private String text;
    private Integer rating;
    
    @Field("user_id")
    private ObjectId userId;
    @Field("apartment_id")
    private ObjectId apartmentId;
    
    @CreatedDate
    @Field("created_date")
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Field("modified_date")
    private LocalDateTime modifiedDate;
}