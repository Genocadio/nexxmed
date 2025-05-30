package com.nexxserve.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NexxmedApplication {
	public static void main(String[] args) {
		var ctx = SpringApplication.run(NexxmedApplication.class, args);
		MyFirstClass myFirstClass = ctx.getBean(MyFirstClass.class);
		System.out.println(myFirstClass.sayHello());
	}

}
