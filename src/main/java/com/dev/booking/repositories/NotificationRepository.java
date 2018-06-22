package com.dev.booking.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dev.booking.models.Notification;

public interface NotificationRepository extends MongoRepository<Notification, ObjectId> {
	@Query("{'email_info.sending_count':{$gt:?0}}")
	public List<Notification> findBySendingCountGreaterThan(int sendingCount);
	@Query("{'$or':[{'$and':[{'email_info.status':'IN_PROCESS' },{'modified_date':{$gt:?0}}]},{'email_info.status':'NEW'}]}")
	public List<Notification> findByEmailStatusForProcessing(LocalDateTime date, Pageable pageable);
}
