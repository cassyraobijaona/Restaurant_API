package com.restaurant.cassy.restaurant_api.controller;

import com.restaurant.cassy.restaurant_api.entity.Ingredient;
import com.restaurant.cassy.restaurant_api.repository.IngredientRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TestController {

    private final IngredientRepository repo;

    public TestController(IngredientRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/test")
    public List<Ingredient> test() {
        return repo.findAll();
    }
}