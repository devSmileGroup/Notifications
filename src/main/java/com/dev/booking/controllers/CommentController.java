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

import com.dev.booking.models.Comment;
import com.dev.booking.repositories.CommentRepository;
import com.dev.booking.services.ResponseGenerator;
import com.mongodb.MongoException;

@RestController
@RequestMapping("/comments")
public class CommentController {
	@Autowired
	CommentRepository commentRepository;
	
	private static final Logger logger = Logger.getLogger(CommentController.class);
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> create(@RequestBody Comment comment) {
		try {
			comment.setId(new ObjectId());
			comment.setCreatedDate(new Date());
			comment.setModifiedDate(new Date());
			
			commentRepository.save(comment);
			
			logger.debug("Create comment with id - " + comment.getId().toString());
			
			return ResponseGenerator.generateJsonResponse(200, null, null);
		}
		catch(MongoException ex) {
			logger.error("Error on comment create - " + ex);
			
			return ResponseGenerator.generateJsonResponse(500, null, "Internal server error");
		}
	}
	
	@GetMapping(value="/{id}")
	public ResponseEntity<String> read(@PathVariable String id) {
		try {
			Comment comment = commentRepository.findById(id).get();
			
			logger.debug("Read comment with id - " + id);
			
			return ResponseGenerator.generateJsonResponse(200, comment, null);
		}
		catch(MongoException ex) {
			logger.error("Error on comment read - " + ex);
			
			return ResponseGenerator.generateJsonResponse(500, null, "Internal server error");
		}
	}
	
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> update(@RequestBody Comment comment) {
		try {
			comment.setModifiedDate(new Date());
			commentRepository.save(comment);
			
			logger.debug("Update comment with id - " + comment.getId().toString());
		
			return ResponseGenerator.generateJsonResponse(200, null, null);
		}
		catch(MongoException ex) {
			logger.error("Error on comment update - " + ex);
			
			return ResponseGenerator.generateJsonResponse(500, null, "Internal server error");
		}
	}
	
	@DeleteMapping(value="/{id}")
	public ResponseEntity<String> delete(@PathVariable String id) {
		try {
			commentRepository.deleteById(id);
			
			logger.debug("Delete comment with id - " + id);
			
			return ResponseGenerator.generateJsonResponse(200, null, null);
		}
		catch(MongoException ex) {
			logger.error("Error on comment delete - " + ex);
			
			return ResponseGenerator.generateJsonResponse(500, null, "Internal server error");
		}
	}
}
