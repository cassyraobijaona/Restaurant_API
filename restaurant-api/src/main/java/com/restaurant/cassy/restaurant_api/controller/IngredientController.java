package com.restaurant.cassy.restaurant_api.controller;

import com.restaurant.cassy.restaurant_api.dto.IngredientResponseDto;
import com.restaurant.cassy.restaurant_api.dto.StockResponseDto;
import com.restaurant.cassy.restaurant_api.service.IngredientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public ResponseEntity<List<IngredientResponseDto>> getAll() {
        return ResponseEntity.ok(ingredientService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        Optional<IngredientResponseDto> ingredient = ingredientService.findById(id);
        if (ingredient.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("Ingredient.id=" + id + " is not found");
        }
        return ResponseEntity.ok(ingredient.get());
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getStock(
            @PathVariable Integer id,
            @RequestParam(required = false) String at,
            @RequestParam(required = false) String unit) {

        if (at == null || unit == null) {
            return ResponseEntity.status(400)
                    .body("Either mandatory query parameter `at` or `unit` is not provided.");
        }

        if (!ingredientService.existsById(id)) {
            return ResponseEntity.status(404)
                    .body("Ingredient.id=" + id + " is not found");
        }

        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(at);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(400)
                    .body("Invalid date format for `at`. Expected format: yyyy-MM-ddTHH:mm:ss");
        }

        double stockValue = ingredientService.getStockValueAt(id, dateTime);

        return ResponseEntity.ok(new StockResponseDto(unit, stockValue));
    }
}