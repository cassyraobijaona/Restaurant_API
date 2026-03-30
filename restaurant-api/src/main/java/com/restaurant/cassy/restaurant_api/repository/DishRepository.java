package com.restaurant.cassy.restaurant_api.repository;

import com.restaurant.cassy.restaurant_api.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Integer> {

    @Query(value = """
            SELECT DISTINCT d.* FROM dish d
            JOIN dish_ingredient di ON di.dish_id = d.id
            JOIN ingredient i ON i.id = di.ingredient_id
            WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :ingredientName, '%'))
            """, nativeQuery = true)
    List<Dish> findByIngredientName(@Param("ingredientName") String ingredientName);
}