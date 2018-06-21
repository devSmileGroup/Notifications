package com.dev.booking.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.dev.booking.models.EmailStatus;
import com.dev.booking.models.Notification;
import com.dev.booking.models.User;
import com.dev.booking.repositories.NotificationRepository;

@Component
public class ScheduledTasks {
	private static final Logger logger = LogManager.getLogger(ScheduledTasks.class);

	@Autowired
	private EmailService emailService;
	@Autowired
	private NotificationRepository notificationRepository;

	@Value("${mailing.time.difference}")
	private long mailingTimeDifference;

	@Value("${mailing.message.quantity}")
	private int mailingMessageQuantity;
	@Value("${mailing.threads.amount}")
	private int mailingThreadsAmount;

	@Scheduled(cron = "${mailing.interval}")
	public void sendNotification() {
		List<Notification> notificationsList = notificationRepository.findByEmailStatus("NEW", "IN_PROCESS",
				LocalDateTime.now().minusMinutes(mailingTimeDifference),
				PageRequest.of(0, mailingMessageQuantity * mailingThreadsAmount));

		Integer threadsAmount = calcNumberOfThreads(notificationsList.size());
		if (threadsAmount != 0) {

			List<Notification> validNotificationsList = notificationsList.stream().map(notification -> {
				notification.getEmailInfo().setSendingCount(notification.getEmailInfo().getSendingCount() + 1);
				notification.getEmailInfo().setEmailStatus(EmailStatus.IN_PROCESS);
				return notification;
			}).collect(Collectors.toList());

			if (validNotificationsList.size() > 0) {
				ExecutorService executorService = Executors.newFixedThreadPool(threadsAmount);
				getBatches(validNotificationsList, mailingMessageQuantity).stream().forEach(batch -> {
					executorService.submit(() -> {
						batch.forEach(this::processNotification);
					});
				});
			}
			notificationRepository.saveAll(validNotificationsList);
		}
	}

	@Scheduled(cron = "${mailing.status.checkout.interval}")
	public void changeStatus() {
		List<Notification> notificationsList = notificationRepository.findByEmailStatus("NEW", "IN_PROCESS",
				LocalDateTime.now(), PageRequest.of(0, mailingMessageQuantity * mailingThreadsAmount));

		notificationsList.forEach(notification -> {
			if (notification.getEmailInfo().getSendingCount() > 2) {
				notification.getEmailInfo().setEmailStatus(EmailStatus.FAILED);
				notificationRepository.save(notification);

				logger.error(String.format("Status of notification with id: %s set to failed, " + " sending count = %s",
						notification.getId(), notification.getEmailInfo().getSendingCount()));
			}
		});
	}

	private int calcNumberOfThreads(int notificationsListSize) {
		return (int) Math.ceil((float) notificationsListSize / mailingMessageQuantity) < mailingThreadsAmount
				? (int) Math.ceil((float) notificationsListSize / mailingMessageQuantity)
				: mailingThreadsAmount;
	}
	
	/**
	 * 
	 * @param source The source list to split on batches
	 * @param size	Size of batch
	 * @return List of batches
	 */
	private List<List<Notification>> getBatches(List<Notification> source, int size) {
		return IntStream.range(0, (source.size() + size - 1) / size)
				.mapToObj(i -> source.subList(i * size, Math.min(source.size(), (i + 1) * size)))
				.collect(Collectors.toList());
	}
	
	/**
	 * Sends email about notification, changes status to PROCESSED if succeed
	 * @param notification The notification to be processed
	 */
	private void processNotification(Notification notification) {
		User testUser = new User("vladmartishevskii@gmail.com"); //def_x@ukr.net
		if (emailService.sendMessage(testUser.getEmail(), notification.getTitle(), notification.getText())) {
			notification.getEmailInfo().setEmailStatus(EmailStatus.PROCESSED);
			logger.info("Notification with id: {} succesfully sended to user with id: {} ThreadID: {}", notification.getId(),
					notification.getUserId(), Thread.currentThread().getId());
		} else {
			logger.error("Notification with id: {} not sended to user with id: {}", notification.getId(),
					notification.getUserId());
		}
	}
}
