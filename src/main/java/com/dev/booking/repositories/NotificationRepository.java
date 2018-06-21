package com.dev.booking.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dev.booking.models.Notification;

public interface NotificationRepository extends MongoRepository<Notification, ObjectId> {
	@Query("{'$and':[{'$or':[{'email_info.status':?0 },{'email_info.status':?1 }]},{'modified_date':{$lt:?2}}]}")
	public List<Notification> findByEmailStatus(String status1, String status2, LocalDateTime date, Pageable pageable);
}
