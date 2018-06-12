package com.dev.booking.services;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dev.booking.models.EmailStatusValue;
import com.dev.booking.models.Notification;
import com.dev.booking.repositories.NotificationRepository;

@Component
public class ScheduledTasks {
	private static final Logger logger = Logger.getLogger(ScheduledTasks.class);
	
	@Autowired
	EmailService emailService;
	@Autowired
	NotificationRepository notificationRepository;
	
	@Scheduled(cron="*/20 * * * * *")
	public void sendNotification() {
		List<Notification> list = notificationRepository.findByEmailStatus("NEW", "IN_PROCESS");
		
		for(Notification el:list) {
			if(diffMinutes(new Date(), el.getCreatedDate()) > 20) {
				if(el.getStatus().getEmailStatus().getValue() != EmailStatusValue.IN_PROCESS) {
					el.getStatus().getEmailStatus().setValue(EmailStatusValue.IN_PROCESS);
					notificationRepository.save(el);
				}
				
				Thread thread = new Thread("NOTIFICATION_THREAD") {
					public void run() {
						int failedCount;
						if(emailService.sendMessage("def_x@ukr.net", el.getTitle(), el.getText())) {
							failedCount = 0;
							el.getStatus().getEmailStatus().setValue(EmailStatusValue.PROCESSED);
							
							logger.debug("send notification "
									+ el.getTitle()
									+ "to user with id - "
									+ el.getUserId());
						}
						else {
							failedCount = el.getStatus().getEmailStatus().getFailedCount() + 1;
							
							logger.error("Failed to send notification "
									+ el.getTitle()
									+ "to user with id - "
									+ el.getUserId());
						}
						el.getStatus().getEmailStatus().setFailedCount(failedCount);
						notificationRepository.save(el);
					}
				};
				thread.start();
			}
		}
	}
	
	@Scheduled(cron="*/25 * * * * *")
	public void changeStatus() {
		List<Notification> list = notificationRepository.findByEmailStatus("NEW", "IN_PROCESS");
		
		for(Notification el:list) {
			if(el.getStatus().getEmailStatus().getFailedCount() > 2) {
				el.getStatus().getEmailStatus().setValue(EmailStatusValue.FAILED);
				notificationRepository.save(el);
			}
		}
	}
	
	private Long diffMinutes(Date current, Date modified) {
		return (current.getTime() - modified.getTime()) / (1000 * 1);
	}
}
