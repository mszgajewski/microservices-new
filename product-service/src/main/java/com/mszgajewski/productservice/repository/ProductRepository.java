package com.mszgajewski.productservice.repository;

import com.mszgajewski.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product,String> {
}
