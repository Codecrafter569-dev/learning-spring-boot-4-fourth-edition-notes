package com.codecrafter.vikash.codecrafter;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
@Component
public class Orderplace{
 
  private NotificationService notification;
   @Autowired
  public Orderplace(NotificationService notification){
    this.notification = notification;
  }
  
  public void place(){
    notification.notifyUser();
    System.out.println("Order placed successful.");
  }
}