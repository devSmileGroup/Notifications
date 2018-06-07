package com.dev.booking.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dev.booking.models.Comment;

public interface CommentRepository extends MongoRepository<Comment, ObjectId> {
	
}
