package com.dev.booking.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dev.booking.models.Notification;
import com.dev.booking.repositories.NotificationRepository;

@Component
public class ScheduledTasks {
	private static final Logger logger = Logger.getLogger(ScheduledTasks.class);
	
	@Autowired
	EmailService emailService;
	
	@Scheduled(cron="*/20 * * * * *")
	public void sendNotification() {
		Query query = new Query();
		query.addCriteria(Criteria
				.where("status.emailStatus.value")
				.is("NEW")
		);
		query.addCriteria(Criteria
				.where("status.emailStatus.value")
				.is("IN_PROCESS")
		);
		/*List<Notification> list = notificationRepository.findAll(query, Notification.class);
		for(Notification el:list) {
			emailService.sendMessage("def_x@ukr.net", el.getTitle(), el.getText());	
			logger.debug("send notification " + el.getTitle() + "to user with id - " + el.getUserId());
		}*/
	}
}
