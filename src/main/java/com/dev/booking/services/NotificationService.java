package com.dev.booking.services;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dev.booking.models.Notification;
import com.dev.booking.repositories.NotificationRepository;
import com.mongodb.MongoException;

@Service
public class NotificationService {
	private static final Logger logger = LogManager.getLogger(NotificationService.class);

	@Autowired
	private NotificationRepository notificationRepository;

	public Notification create(Notification notification) {
		try {
			notificationRepository.save(notification);
			logger.info("Create notification with id: {}", notification.getId().toString());
			return notification;
		} catch (MongoException e) {
			logger.error("Error in creating notification: {}", e);
			throw new RuntimeException("Error in creating notification");
		}

	}
	
	public Notification read(ObjectId objectId) {
		try {
			Optional<Notification> optionalNotification = notificationRepository.findById(objectId);
			
			if(optionalNotification.isPresent()) {
				logger.info("Read notification with id: {}", objectId.toString());
	
				return optionalNotification.get();
			}
			logger.info("Notification with id: {} not found", objectId.toString());
			return null;
		}
		catch (MongoException e) {
			logger.error("Error in reading notification: {}", e);
			throw new RuntimeException("Error in reading notification");
		}

	}
	
	public Notification update(Notification notification) {
		try {
			Optional<Notification> optionalNotification = notificationRepository.findById(notification.getId());
			if(optionalNotification.isPresent()) {
				logger.info("Update notification with id: {}", notification.getId().toString());
				Notification foundNotification = optionalNotification.get();
				updateNotification(foundNotification, notification);
				notificationRepository.save(foundNotification);
				return foundNotification;
			}
			logger.info("Notification with id: {} not found", notification.getId().toString());
			return null;
			
		}
		catch (MongoException e) {
			logger.error("Error in updating notification: {}", e);
			throw new RuntimeException("Error in updating notification");
		}
		
	}
	
	public void delete(ObjectId id) {
		try {
			logger.info("Deleting notification with id: {}", id.toString());
			notificationRepository.deleteById(id);
		}
		catch (MongoException e) {
			logger.error("Error in deleting notification: {}", e);
			throw new RuntimeException("Error in deleting notification");
		}
	}
	
	private void updateNotification(Notification foundNotification, Notification notification) {
		foundNotification.setEmailInfo(notification.getEmailInfo());
		foundNotification.setUiStatus(notification.getUiStatus());
		foundNotification.setText(notification.getText());
		foundNotification.setTitle(notification.getTitle());
	}
}
