package com.dev.booking.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dev.booking.models.Notification;

public interface NotificationRepository extends MongoRepository<Notification, ObjectId> {
	
}
