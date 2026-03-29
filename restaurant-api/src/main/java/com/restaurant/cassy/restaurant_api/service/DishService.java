package com.restaurant.cassy.restaurant_api.service;

import com.restaurant.cassy.restaurant_api.entity.Dish;
import com.restaurant.cassy.restaurant_api.entity.Ingredient;
import com.restaurant.cassy.restaurant_api.repository.DishRepository;
import com.restaurant.cassy.restaurant_api.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DishService {

    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;

    public DishService(DishRepository dishRepository,
                       IngredientRepository ingredientRepository) {
        this.dishRepository = dishRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    public Optional<Dish> findById(Integer id) {
        return dishRepository.findById(id);
    }

    public Dish updateIngredients(Integer dishId, List<Ingredient> requested) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        List<Integer> ids = requested.stream()
                .map(Ingredient::getId)
                .toList();

        List<Ingredient> validIngredients = ingredientRepository.findAllById(ids);
        dish.setIngredients(validIngredients);

        return dishRepository.save(dish);
    }
}