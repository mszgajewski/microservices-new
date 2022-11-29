package com.mszgajewski.inventoryservice;

import com.mszgajewski.inventoryservice.model.Inventory;
import com.mszgajewski.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient

public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {

		return args -> {
		Inventory inventory = new Inventory();
		inventory.setSkuCode("iphone_13");
		inventory.setQuantity(0);
		Inventory inventory1 = new Inventory();
		inventory1.setSkuCode("iphone_12_mini");
		inventory.setQuantity(100);

		inventoryRepository.save(inventory);
		inventoryRepository.save(inventory1);
		};
	}
}
