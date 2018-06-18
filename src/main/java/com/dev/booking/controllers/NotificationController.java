package com.dev.booking.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dev.booking.models.Notification;
import com.dev.booking.repositories.NotificationRepository;
import com.mongodb.MongoException;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
	
	private static final Logger logger = LogManager.getLogger(NotificationController.class);
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> create(@RequestBody Notification notification) {
		try {
			notificationRepository.save(notification);
			
			logger.info("Create notification with id: {}", notification.getId().toString());
			
			return ResponseEntity
					.ok()
					.contentType(MediaType.TEXT_PLAIN)
					.body("Success");
		}
		catch(MongoException ex) {
			logger.error("Error in creating notification: {}", ex);
			
			return ResponseEntity
					.status(500)
					.contentType(MediaType.TEXT_PLAIN)
					.body("Failed: " + ex);
		}
	}
	
	@GetMapping(value="/{id}")
	public ResponseEntity<Notification> read(@PathVariable ObjectId id) {
		Notification notification = null;
		try {
			 notification = notificationRepository.findById(id).get();
			logger.info("Read notification with id: {}", id);
			return ResponseEntity
					.ok()
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.body(notification);
		}
		catch(MongoException ex) {
			logger.error("Error in reading notification: {}", ex);
			
			return ResponseEntity
					.status(500)
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.body(notification);
		}
	}
	
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> update(@RequestBody Notification notification) {
		Notification foundNotification = null;
		try {
			foundNotification = notificationRepository.findById(notification.getId()).get();
			updateNotification(foundNotification, notification);
			
			logger.info("Update notification with id: {}", notification.getId().toString());
			
			return ResponseEntity
					.ok()
					.contentType(MediaType.TEXT_PLAIN)
					.body("Success");
		}
		catch(MongoException ex) {
			logger.error("Error in updating notification: {}", ex);
			
			return ResponseEntity
					.status(500)
					.contentType(MediaType.TEXT_PLAIN)
					.body("Failed: " + ex);
		}
	}
	
	@DeleteMapping(value="/{id}")
	public ResponseEntity<String> delete(@PathVariable ObjectId id) {
		try {
			notificationRepository.deleteById(id);
			logger.info("Delete notification with id: {}", id);
			
			return ResponseEntity
					.ok()
					.contentType(MediaType.TEXT_PLAIN)
					.body("Success");
		}
		catch(MongoException ex) {
			logger.error("Error in deleting notification: {}", ex);
			
			return ResponseEntity
					.status(500)
					.contentType(MediaType.TEXT_PLAIN)
					.body("Failed: " + ex);
		}
	}
	
	private void updateNotification(Notification foundNotification, Notification notification) {
		foundNotification.setEmailInfo(notification.getEmailInfo());
		foundNotification.setUiStatus(notification.getUiStatus());
		foundNotification.setText(notification.getText());
		foundNotification.setTitle(notification.getTitle());
		notificationRepository.save(foundNotification);
	}

}
