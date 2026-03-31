package com.restaurant.cassy.restaurant_api.controller;

import com.restaurant.cassy.restaurant_api.dto.IngredientResponseDto;
import com.restaurant.cassy.restaurant_api.dto.StockResponseDto;
import com.restaurant.cassy.restaurant_api.service.IngredientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public ResponseEntity<List<IngredientResponseDto>> getAll() throws SQLException {
        return ResponseEntity.ok(ingredientService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) throws SQLException {
        return ingredientService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body("Ingredient.id=" + id + " is not found"));
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getStock(@PathVariable Integer id,
                                      @RequestParam(required = false) String at,
                                      @RequestParam(required = false) String unit) throws SQLException {
        if (at == null || unit == null) {
            return ResponseEntity.badRequest().body("Either mandatory query parameter `at` or `unit` is not provided.");
        }

        double stockValue = 100;
        return ResponseEntity.ok(new StockResponseDto(id, unit, stockValue));
    }
}