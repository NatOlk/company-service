package com.ansh.repository;

import com.ansh.repository.data.Subscription;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

  @Query(name = "remove from Subscription s where s.email=:email and s.topic=:topic")
  void removeAllByEmailAndTopic(@NonNull String email, @NonNull String topic);

  @Query(name = "select * from Subscription s where s.topic=:topic")
  List<Subscription> getAllSubscriptionsByTopic(String topic);

  @Query(name = "select * from Subscription s where s.email=:email")
  List<Subscription> getSubscriptionByEmail(String email);
}
