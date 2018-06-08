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
import com.dev.booking.models.Comment;
import com.dev.booking.repositories.CommentRepository;
import com.mongodb.MongoException;

@RestController
@RequestMapping("/comments")
public class CommentController {
	
	private static final Logger logger = Logger.getLogger(CommentController.class);
	
	@Autowired
	CommentRepository commentRepository;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public void create(@RequestBody Comment comment) {
		try {
			commentRepository.save(comment);
			logger.debug("Create comment with id - " + comment.getId().toString());
		} catch (MongoException ex) {
			logger.error("Error on comment create - " + ex);
		}
	}

	@GetMapping(value = "/{id}")
	public Optional<Comment> read(@PathVariable ObjectId id) {
		try {
			Optional<Comment> comment = commentRepository.findById(id);
			logger.debug("Read comment with id - " + id);
			return comment;
		} catch (MongoException ex) {
			logger.error("Error on comment read - " + ex);
			return null;
		}
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public void update(@RequestBody Comment comment) {
		try {
			Optional<Comment> foundComment = commentRepository.findById(comment.getId());
			if (foundComment.isPresent()) {
				foundComment.get().setTitle(comment.getTitle());
				foundComment.get().setText(comment.getText());
				foundComment.get().setRating(comment.getRating());

				commentRepository.save(foundComment.get());
				logger.debug("Update comment with id - " + comment.getId().toString());
			} else {
				logger.debug("Cannot update comment with id - " + comment.getId().toString());
			}
		} catch (MongoException ex) {
			logger.error("Error on comment update - " + ex);
		}
	}

	@DeleteMapping(value = "/{id}")
	public void delete(@PathVariable ObjectId id) {
		try {
			commentRepository.deleteById(id);

			logger.debug("Delete comment with id - " + id);
		} catch (MongoException ex) {
			logger.error("Error on comment delete - " + ex);
		}
	}
}
