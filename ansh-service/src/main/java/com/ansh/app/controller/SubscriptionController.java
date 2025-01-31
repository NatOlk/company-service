package com.ansh.app.controller;

import com.ansh.auth.service.UserProfileService;
import com.ansh.dto.SubscriptionRequest;
import com.ansh.entity.animal.UserProfile.AnimalNotificationSubscriptionStatus;
import com.ansh.entity.subscription.Subscription;
import com.ansh.app.service.notification.subscription.AnimalInfoPendingSubscriptionService;
import com.ansh.app.service.notification.subscription.NotificationSubscriptionService;
import com.ansh.repository.entity.PendingSubscriber;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

@RestController
public class SubscriptionController {
  @Autowired
  @Qualifier("notificationSubscriptionService")
  private NotificationSubscriptionService notificationService;
  @Autowired
  @Qualifier("animalInfoPendingSubscriptionService")
  private AnimalInfoPendingSubscriptionService pendingSubscriptionService;
  @Autowired
  private UserProfileService userProfileService;

  @PostMapping("/animal-notify-approve-subscriber")
  public void approve(@RequestBody SubscriptionRequest subscriptionRequest) {
    if (StringUtils.isEmpty(subscriptionRequest.getApprover()) || StringUtils.isEmpty(
        subscriptionRequest.getEmail())) {
      return;
    }
    pendingSubscriptionService.approveSubscriber(subscriptionRequest.getEmail(),
        subscriptionRequest.getApprover());
  }

  @PostMapping("/animal-notify-reject-subscriber")
  public void reject(@RequestBody SubscriptionRequest subscriptionRequest) {
    if (StringUtils.isEmpty(subscriptionRequest.getEmail())) {
      return;
    }
    pendingSubscriptionService.rejectSubscriber(subscriptionRequest.getEmail());
  }

  @PostMapping(value = "/animal-notify-pending-subscribers")
  public List<PendingSubscriber> getPendingSubscribers(
      @RequestBody SubscriptionRequest subscriptionRequest) {
    if (StringUtils.isEmpty(subscriptionRequest.getApprover())) {
      return Collections.emptyList();
    }
    return pendingSubscriptionService.getSubscribersByApprover(subscriptionRequest.getApprover());
  }

  @GetMapping("/animal-notify-pending-no-approver-subscribers")
  public List<PendingSubscriber> getPendingNoApproverSubscribers() {
    return pendingSubscriptionService.getPendingSubscribersWithoutApprover();
  }

  @PostMapping("/animal-notify-all-approver-subscriptions")
  public List<Subscription> getSubscribers(@RequestBody SubscriptionRequest subscriptionRequest) {
    if (StringUtils.isEmpty(subscriptionRequest.getApprover())) return Collections.emptyList();
    return notificationService.getAllSubscriptionByApprover(subscriptionRequest.getApprover());
  }

  @PostMapping("/animal-notify-approver-status")
  public AnimalNotificationSubscriptionStatus getApproverStatus(
      @RequestBody SubscriptionRequest subscriptionRequest) {
    if (StringUtils.isEmpty(subscriptionRequest.getApprover())) return null;

    AnimalNotificationSubscriptionStatus status = notificationService.getStatusByApprover(
        subscriptionRequest.getApprover());
    userProfileService.updateNotificationStatusOfAuthUser(status);
    return status;
  }
}
