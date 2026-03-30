package com.restaurant.cassy.restaurant_api.controller;

import com.restaurant.cassy.restaurant_api.dto.IngredientResponseDto;
import com.restaurant.cassy.restaurant_api.dto.StockResponseDto;
import com.restaurant.cassy.restaurant_api.entity.Ingredient;
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
    public ResponseEntity<List<IngredientResponseDto>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String dishName,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        if (name == null && category == null && dishName == null
                && page == null && size == null) {
            return ResponseEntity.ok(ingredientService.findAll());
        }

        int p = (page != null) ? page : 1;
        int s = (size != null) ? size : 10;

        if (name == null && category == null && dishName == null) {
            return ResponseEntity.ok(ingredientService.findAllPaginated(p, s));
        }

        return ResponseEntity.ok(
                ingredientService.findByCriteria(name, category, dishName, p, s)
        );
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
                    .body("Invalid date format for `at`. Expected: yyyy-MM-ddTHH:mm:ss");
        }

        double stockValue = ingredientService.getStockValueAt(id, dateTime);
        return ResponseEntity.ok(new StockResponseDto(unit, stockValue));
    }

    @PostMapping
    public ResponseEntity<?> createIngredients(
            @RequestBody(required = false) List<Ingredient> ingredients) {

        if (ingredients == null || ingredients.isEmpty()) {
            return ResponseEntity.status(400)
                    .body("Request body is required and must contain a list of ingredients.");
        }

        try {
            List<IngredientResponseDto> created =
                    ingredientService.createIngredients(ingredients);
            return ResponseEntity.status(201).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateIngredient(
            @PathVariable Integer id,
            @RequestBody(required = false) Ingredient ingredient) {

        if (ingredient == null) {
            return ResponseEntity.status(400)
                    .body("Request body is required.");
        }

        try {
            IngredientResponseDto updated = ingredientService.saveIngredient(id, ingredient);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            if ("NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(404)
                        .body("Ingredient.id=" + id + " is not found");
            }
            throw e;
        }
    }
}
