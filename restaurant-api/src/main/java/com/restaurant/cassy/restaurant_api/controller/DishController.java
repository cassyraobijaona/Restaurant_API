package com.restaurant.cassy.restaurant_api.controller;

import com.restaurant.cassy.restaurant_api.dto.DishResponseDto;
import com.restaurant.cassy.restaurant_api.entity.Ingredient;
import com.restaurant.cassy.restaurant_api.service.DishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ResponseEntity<List<DishResponseDto>> getAll() {
        return ResponseEntity.ok(dishService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return dishService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body("Dish.id=" + id + " is not found"));
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateIngredients(
            @PathVariable Integer id,
            @RequestBody(required = false) List<Ingredient> ingredients) {

        if (ingredients == null) {
            return ResponseEntity.status(400)
                    .body("Request body is required and must contain a list of ingredients.");
        }

        try {
            DishResponseDto updated = dishService.updateIngredients(id, ingredients);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            if ("NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(404)
                        .body("Dish.id=" + id + " is not found");
            }
            throw e;
        }
    }
}