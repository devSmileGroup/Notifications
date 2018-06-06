package com.dev.booking.controllers;

import java.util.Date;

import org.apache.log4j.Logger;
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
import com.dev.booking.services.ResponseGenerator;
import com.mongodb.MongoException;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
	@Autowired
	NotificationRepository notificationRepository;
	
	private static final Logger logger = Logger.getLogger(NotificationController.class);
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> create(@RequestBody Notification notification) {
		try {
			notification.setId(new ObjectId());
			notification.setDateCreation(new Date());
			notification.setDateModify(new Date());
			System.out.println("------------------");
			System.out.println(notification.getStatus());
			System.out.println("------------------");
			
			notificationRepository.save(notification);
			logger.debug("Create notification with id - " + notification.getId().toString());
			
			return ResponseGenerator.generateJsonResponse(200, null, null);
		}
		catch(MongoException ex) {
			logger.error("Error on notification create - " + ex);
			
			return ResponseGenerator.generateJsonResponse(500, null, "Internal server error");
		}
	}
	
	@GetMapping(value="/{id}")
	public ResponseEntity<String> read(@PathVariable String id) {
		try {
			Notification notification = notificationRepository.findById(id).get();
			
			logger.debug("Read notification with id - " + id);
			
			return ResponseGenerator.generateJsonResponse(200, notification, null);
		}
		catch(MongoException ex) {
			logger.error("Error on notification read - " + ex);
			
			return ResponseGenerator.generateJsonResponse(500, null, "Internal server error");
		}
	}
	
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> update(@RequestBody Notification notification) {
		try {
			notification.setDateModify(new Date());
			notificationRepository.save(notification);
			
			logger.debug("Update notification with id - " + notification.getId().toString());
		
			return ResponseGenerator.generateJsonResponse(200, null, null);
		}
		catch(MongoException ex) {
			logger.error("Error on notification update - " + ex);
			
			return ResponseGenerator.generateJsonResponse(500, null, "Internal server error");
		}
	}
	
	@DeleteMapping(value="/{id}")
	public ResponseEntity<String> delete(@PathVariable String id) {
		try {
			notificationRepository.deleteById(id);
			
			logger.debug("Delete notification with id - " + id);
			
			return ResponseGenerator.generateJsonResponse(200, null, null);
		}
		catch(MongoException ex) {
			logger.error("Error on notification delete - " + ex);
			
			return ResponseGenerator.generateJsonResponse(500, null, "Internal server error");
		}
	}
}
