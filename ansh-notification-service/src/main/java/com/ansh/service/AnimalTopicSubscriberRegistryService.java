package com.ansh.service;

import static com.ansh.entity.animal.UserProfile.AnimalNotificationSubscriptionStatus.ACTIVE;
import static com.ansh.entity.animal.UserProfile.AnimalNotificationSubscriptionStatus.NONE;
import static com.ansh.entity.animal.UserProfile.AnimalNotificationSubscriptionStatus.PENDING;

import com.ansh.cache.SubscriptionCacheManager;
import com.ansh.entity.animal.UserProfile;
import com.ansh.entity.subscription.Subscription;
import com.ansh.notification.subscription.SubscriberNotificationEventProducer;
import com.ansh.repository.SubscriptionRepository;
import com.ansh.utils.IdentifierMasker;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AnimalTopicSubscriberRegistryService {

  private static final Logger LOG = LoggerFactory.getLogger(
      AnimalTopicSubscriberRegistryService.class);

  @Value("${animalTopicId}")
  private String animalTopicId;

  @Autowired
  private SubscriptionRepository subscriptionRepository;

  @Autowired
  private SubscriptionNotificationEmailService subscriptionNotificationEmailService;

  @Autowired
  private SubscriberNotificationEventProducer subscriberNotificationEventProducer;

  @Autowired
  private SubscriptionCacheManager cacheManager;

  @PostConstruct
  public void initializeCache() {
    cacheManager.initializeCache();
  }

  @Scheduled(cron = "0 0 20 * * ?", zone = "Europe/Berlin")
  public void reloadCache() {
    if (cacheManager.shouldUpdateCache()) {
      cacheManager.reloadCache();
    }
  }

  @Transactional
  public void registerSubscriber(String email, String approver) {
    findSubscriptionByEmail(email).ifPresentOrElse(
        this::handleExistingSubscription,
        () -> createAndRegisterNewSubscription(email, approver)
    );
  }

  @Transactional
  public void unregisterSubscriber(String token) {
    removeSubscriptionFromCacheAndDb(token);
  }

  @Transactional
  public boolean acceptSubscription(String token) {
    Optional<Subscription> subscriptionOpt = findSubscriptionByToken(token);
    subscriptionOpt.ifPresent(subscription -> {
      subscription.setAccepted(true);
      subscriptionRepository.save(subscription);
      cacheManager.addToCache(subscription);
      subscriptionNotificationEmailService.sendSuccessTokenConfirmationEmail(subscription);
    });
    return subscriptionOpt.isEmpty();
  }

  public List<Subscription> getAcceptedAndApprovedSubscribers() {
    return cacheManager.getAllFromCache().stream()
        .toList();
  }

  public List<Subscription> getAllSubscriptions(String approver) {
    return subscriptionRepository.findByApproverAndTopic(approver, animalTopicId);
  }

  public UserProfile.AnimalNotificationSubscriptionStatus getSubscriptionStatus(String approver) {
    return findSubscriptionByEmail(approver)
        .map(subscription -> subscription.isAccepted() ? ACTIVE : PENDING)
        .orElse(NONE);
  }

  @Transactional
  public void handleSubscriptionApproval(String email, String approver, boolean reject) {
    findSubscriptionByEmail(email).ifPresent(subscription -> {
      if (reject) {
        removeSubscriptionFromCacheAndDb(subscription.getToken());
      } else {
        approveSubscription(subscription, approver);
      }
    });
  }

  private void approveSubscription(Subscription subscription, String approver) {
    subscription.setApprover(approver);
    subscription.setApproved(true);
    subscriptionRepository.save(subscription);
    subscriptionNotificationEmailService.sendNeedAcceptSubscriptionEmail(subscription);
  }

  private void removeSubscriptionFromCacheAndDb(String token) {
    subscriptionRepository.deleteByTokenAndTopic(token, animalTopicId);
    cacheManager.removeFromCache(token);
  }

  private void createAndRegisterNewSubscription(String email, String approver) {
    Subscription newSubscription = new Subscription();
    newSubscription.setTopic(animalTopicId);
    newSubscription.setEmail(email);
    newSubscription.setApprover(approver);
    newSubscription.setToken(UUID.randomUUID().toString());
    newSubscription.setAccepted(false);
    newSubscription.setApproved(false);

    subscriptionRepository.save(newSubscription);
    subscriberNotificationEventProducer.sendApproveRequest(email, approver, animalTopicId);
    LOG.debug("Register a new subscriber {}", IdentifierMasker.maskEmail(email));
  }

  private void handleExistingSubscription(Subscription subscription) {
    if (!subscription.isApproved()) {
      return;
    }
    if (subscription.isAccepted()) {
      subscriptionNotificationEmailService.sendRepeatConfirmationEmail(subscription);
    } else {
      subscriptionNotificationEmailService.sendNeedAcceptSubscriptionEmail(subscription);
    }
  }

  private Optional<Subscription> findSubscriptionByEmail(String email) {
    return subscriptionRepository.findByEmailAndTopic(email, animalTopicId);
  }

  private Optional<Subscription> findSubscriptionByToken(String token) {
    return subscriptionRepository.findByTokenAndTopic(token, animalTopicId);
  }

  protected void setAnimalTopicId(String animalTopicId) {
    this.animalTopicId = animalTopicId;
  }
}
