package com.restaurant.cassy.restaurant_api.controller;

import com.restaurant.cassy.restaurant_api.dto.DishCreateDto;
import com.restaurant.cassy.restaurant_api.dto.DishResponseDto;
import com.restaurant.cassy.restaurant_api.service.DishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ResponseEntity<List<DishResponseDto>> getAll(@RequestParam(required = false) Double priceOver,
                                                        @RequestParam(required = false) Double priceUnder,
                                                        @RequestParam(required = false) String name) throws SQLException {
        if (priceOver != null || priceUnder != null || name != null) {
            return ResponseEntity.ok(dishService.getDishesFiltered(priceOver, priceUnder, name));
        }
        return ResponseEntity.ok(dishService.findAll());
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateIngredients(@PathVariable Integer id,
                                               @RequestBody List<com.restaurant.cassy.restaurant_api.entity.Ingredient> ingredients) throws SQLException {
        if (ingredients == null) return ResponseEntity.badRequest().body("Request body is required");
        try {
            return ResponseEntity.ok(dishService.updateIngredients(id, ingredients));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Dish.id=" + id + " is not found");
        }
    }

    @PostMapping
    public ResponseEntity<?> createDishes(@RequestBody List<DishCreateDto> dishDtos) {
        try {
            List<DishResponseDto> result = dishService.createDishes(dishDtos);
            return ResponseEntity.status(201).body(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}