package com.dev.booking.controllers;

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
import com.dev.booking.models.Notification;
import com.dev.booking.repositories.CommentRepository;
import com.mongodb.MongoException;

@RestController
@RequestMapping("/comments")
public class CommentController {
	
	private static final Logger logger = Logger.getLogger(CommentController.class);
	
	@Autowired
	private CommentRepository commentRepository;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> create(@RequestBody Comment comment) {
		try {
			commentRepository.save(comment);
			logger.debug("Create comment with id - " + comment.getId().toString());
			
			return ResponseEntity
					.ok()
					.contentType(MediaType.TEXT_PLAIN)
					.body("Success");
		} catch (MongoException ex) {
			logger.error("Error on comment create - " + ex);
			
			return ResponseEntity
					.status(500)
					.contentType(MediaType.TEXT_PLAIN)
					.body("Failed: " + ex);
		}
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<Comment> read(@PathVariable ObjectId id) {
		Comment comment = null;
		try {
			comment = commentRepository.findById(id).get();
			logger.debug("Read comment with id - " + id);
			
			return ResponseEntity
					.ok()
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.body(comment);
		} 
		catch (MongoException ex) {
			logger.error("Error on comment read - " + ex);
			
			return ResponseEntity
					.status(500)
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.body(comment);
		}
	}
	
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> update(@RequestBody Comment comment) {
		try {
			Comment foundComment = commentRepository.findById(comment.getId()).get();
			updateComment(foundComment, comment);
			
			logger.debug("Update comment with id - " + comment.getId().toString());
			
			return ResponseEntity
					.ok()
					.contentType(MediaType.TEXT_PLAIN)
					.body("Success");
		}
		catch (NullPointerException | MongoException ex) {
			logger.error("Error on comment update - " + ex);
			
			return ResponseEntity
					.status(500)
					.contentType(MediaType.TEXT_PLAIN)
					.body("Failed: " + ex);
		}
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> delete(@PathVariable ObjectId id) {
		try {
			commentRepository.deleteById(id);

			logger.debug("Delete comment with id - " + id);
			
			return ResponseEntity
					.ok()
					.contentType(MediaType.TEXT_PLAIN)
					.body("Success");
		} 
		catch (MongoException ex) {
			logger.error("Error on comment delete - " + ex);
			
			return ResponseEntity
					.status(500)
					.contentType(MediaType.TEXT_PLAIN)
					.body("Failed: " + ex);
		}
	}
	
	private void updateComment(Comment foundComment, Comment comment) {
		foundComment.setTitle(comment.getTitle());
		foundComment.setText(comment.getText());
		foundComment.setRating(comment.getRating());
		commentRepository.save(foundComment);
	}
}
