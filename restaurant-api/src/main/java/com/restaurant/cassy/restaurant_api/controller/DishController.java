package com.restaurant.cassy.restaurant_api.controller;

import com.restaurant.cassy.restaurant_api.dto.CreateDishRequestDto;
import com.restaurant.cassy.restaurant_api.dto.DishResponseDto;
import com.restaurant.cassy.restaurant_api.entity.Dish;
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
    public ResponseEntity<List<DishResponseDto>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double priceOver,
            @RequestParam(required = false) Double priceUnder,
            @RequestParam(required = false) String ingredientName) {

        if (ingredientName != null) {
            return ResponseEntity.ok(dishService.findByIngredientName(ingredientName));
        }

        if (name != null || priceOver != null || priceUnder != null) {
            return ResponseEntity.ok(dishService.findByCriteria(name, priceOver, priceUnder));
        }

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

    @GetMapping("/{id}/cost")
    public ResponseEntity<?> getDishCost(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(dishService.getDishCost(id));
        } catch (RuntimeException e) {
            if ("NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(404)
                        .body("Dish.id=" + id + " is not found");
            }
            throw e;
        }
    }

    @GetMapping("/{id}/margin")
    public ResponseEntity<?> getGrossMargin(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(dishService.getGrossMargin(id));
        } catch (RuntimeException e) {
            if ("NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(404)
                        .body("Dish.id=" + id + " is not found");
            }
            if ("SELLING_PRICE_NULL".equals(e.getMessage())) {
                return ResponseEntity.status(400)
                        .body("Cannot compute gross margin: selling price is null for this dish.");
            }
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<?> createDishes(
            @RequestBody(required = false) List<CreateDishRequestDto> dishes) {

        if (dishes == null || dishes.isEmpty()) {
            return ResponseEntity.status(400)
                    .body("Request body is required and must contain a list of dishes.");
        }

        try {
            List<DishResponseDto> created = dishService.createDishes(dishes);
            return ResponseEntity.status(201).body(created);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("DISH_EXISTS:")) {
                String dishName = e.getMessage().replace("DISH_EXISTS:", "");
                return ResponseEntity.status(400)
                        .body("Dish.name=" + dishName + " already exists");
            }
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}