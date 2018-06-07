package com.dev.booking.controllers;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
	@Autowired
	NotificationRepository notificationRepository;
	
	private static final Logger logger = Logger.getLogger(NotificationController.class);
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public void create(@RequestBody Notification notification) {
		try {
			notificationRepository.save(notification);
			logger.debug("Create notification with id - " + notification.getId().toString());
			
		}
		catch(MongoException ex) {
			logger.error("Error on notification create - " + ex);
		}
	}
	
	@GetMapping(value="/{id}")
	public Optional<Notification> read(@PathVariable ObjectId id) {
		try {
			Optional<Notification> notification = notificationRepository.findById(id);
			
			logger.debug("Read notification with id - " + id);
			return notification;
		}
		catch(MongoException ex) {
			logger.error("Error on notification read - " + ex);
			return null;
		}
	}
	
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public void update(@RequestBody Notification notification) {
		try {
			Optional<Notification> foundNotification = notificationRepository.findById(notification.getId());
			if(foundNotification.isPresent()) {
				foundNotification.get().setStatus(notification.getStatus());
				foundNotification.get().setText(notification.getText());
				foundNotification.get().setTitle(notification.getTitle());
				notificationRepository.save(foundNotification.get());
				logger.debug("Update notification with id - " + notification.getId().toString());
			}
			else {
				logger.debug("Cannot update notification with id - " + notification.getId().toString());
			}
			
		}
		catch(MongoException ex) {
			logger.error("Error on notification update - " + ex);
		}
	}
	
	@DeleteMapping(value="/{id}")
	public void delete(@PathVariable ObjectId id) {
		try {
			notificationRepository.deleteById(id);
			logger.debug("Delete notification with id - " + id);
		}
		catch(MongoException ex) {
			logger.error("Error on notification delete - " + ex);
		}
	}
}
