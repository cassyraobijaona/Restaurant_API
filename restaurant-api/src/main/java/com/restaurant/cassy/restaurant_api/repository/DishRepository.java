package com.restaurant.cassy.restaurant_api.repository;

import com.restaurant.cassy.restaurant_api.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DishRepository extends JpaRepository<Dish, Integer> {
}