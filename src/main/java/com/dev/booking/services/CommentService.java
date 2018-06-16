package com.dev.booking.services;

import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dev.booking.models.Comment;
import com.dev.booking.repositories.CommentRepository;
import com.mongodb.MongoException;

@Service
public class CommentService {
	private static final Logger logger = LogManager.getLogger(CommentService.class);

	@Autowired
	private CommentRepository commentRepository;
	
	public Comment create(Comment comment) {
		try {
			commentRepository.save(comment);
			logger.info("Create comment with id: {}", comment.getId().toString());
			return comment;
		} catch (MongoException e) {
			logger.error("Error in creating comment: {}", e);
			throw new RuntimeException("Error in creating comment");
		}

	}
	
	public Comment read(ObjectId objectId) {
		try {
			Optional<Comment> optionalComment = commentRepository.findById(objectId);
			
			if(optionalComment.isPresent()) {
				logger.info("Read comment with id: {}", objectId.toString());
	
				return optionalComment.get();
			}
			logger.info("Comment with id: {} not found", objectId.toString());
			return null;
		}
		catch (MongoException e) {
			logger.error("Error in reading comment: {}", e);
			throw new RuntimeException("Error in reading comment");
		}

	}
	
	public Comment update(Comment comment) {
		try {
			Optional<Comment> optionalComment = commentRepository.findById(comment.getId());
			if(optionalComment.isPresent()) {
				logger.info("Update comment with id: {}", comment.getId().toString());
				Comment foundComment = optionalComment.get();
				updateComment(foundComment, comment);
				commentRepository.save(foundComment);
				return foundComment;
			}
			logger.info("Comment with id: {} not found", comment.getId().toString());
			return null;
			
		}
		catch (MongoException e) {
			logger.error("Error in updating comment: {}", e);
			throw new RuntimeException("Error in updating comment");
		}
		
	}
	
	public void delete(ObjectId id) {
		try {
			logger.info("Deleting comment with id: {}", id.toString());
			commentRepository.deleteById(id);
		}
		catch (MongoException e) {
			logger.error("Error in deleting comment: {}", e);
			throw new RuntimeException("Error in deleting comment");
		}
	}
	
	private void updateComment(Comment foundComment, Comment comment) {
		foundComment.setTitle(comment.getTitle());
		foundComment.setText(comment.getText());
		foundComment.setRating(comment.getRating());
	}
}
