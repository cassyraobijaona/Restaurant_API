package com.restaurant.cassy.restaurant_api.repository;

import com.restaurant.cassy.restaurant_api.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Integer> {

    List<StockMovement> findByIngredientIdAndMovementDateLessThanEqual(
            Integer ingredientId, LocalDateTime movementDate
    );
}