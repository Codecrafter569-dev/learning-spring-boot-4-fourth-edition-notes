package com.codecrafter.vikash.codecrafter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class CodecrafterApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodecrafterApplication.class, args);
		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
	//	NotificationService noti = context.getBean(NotificationService.class); 
		Orderplace order = context.getBean(Orderplace.class);
	//	noti.notifyUser();
	
		order.place();
	}

}
