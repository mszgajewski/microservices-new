package com.mszgajewski.productmicroservice.repository;

import com.mszgajewski.productmicroservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product,String> {
}
