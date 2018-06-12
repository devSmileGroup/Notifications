package com.dev.booking.repositories;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dev.booking.models.Notification;

public interface NotificationRepository extends MongoRepository<Notification, ObjectId> {
	@Query("{'$or': [{'status.emailStatus.value' : ?0 }, {'status.emailStatus.value' : ?1 }]}")
	public List<Notification> findByEmailStatus(String value0, String value1);
}
