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
import com.dev.booking.services.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
	private static final Logger logger = LogManager.getLogger(NotificationController.class);
	
	@Autowired
	private NotificationService notificationService;
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Notification> create(@RequestBody Notification notification) {
		return ResponseEntity
					.ok()
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.body(notificationService.create(notification));
	}
	
	@GetMapping(value="/{id}")
	public ResponseEntity<Notification> read(@PathVariable ObjectId id) {
		return ResponseEntity
					.ok()
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.body(notificationService.read(id));
	}
	
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Notification> update(@RequestBody Notification notification) {
		return ResponseEntity
					.ok()
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.body(notificationService.update(notification));
	}
	
	@DeleteMapping(value="/{id}")
	public void delete(@PathVariable ObjectId id) {
		notificationService.delete(id);
	}
	
}
