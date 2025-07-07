package com.collaboportal.shoho;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.collaboportal"})
public class ShohoApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(ShohoApplication.class, args);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	
	}
	

}
