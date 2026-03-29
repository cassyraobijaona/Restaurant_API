package com.restaurant.cassy.restaurant_api.service;

import com.restaurant.cassy.restaurant_api.entity.Ingredient;
import com.restaurant.cassy.restaurant_api.entity.StockMovement;
import com.restaurant.cassy.restaurant_api.repository.IngredientRepository;
import com.restaurant.cassy.restaurant_api.repository.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final StockMovementRepository stockMovementRepository;

    public IngredientService(IngredientRepository ingredientRepository,
                             StockMovementRepository stockMovementRepository) {
        this.ingredientRepository = ingredientRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    public List<Ingredient> findAll() {
        return ingredientRepository.findAll();
    }

    public Optional<Ingredient> findById(Integer id) {
        return ingredientRepository.findById(id);
    }

    public double getStockValueAt(Integer ingredientId, LocalDateTime at) {
        List<StockMovement> movements = stockMovementRepository
                .findByIngredientIdAndMovementDateLessThanEqual(ingredientId, at);

        return movements.stream()
                .mapToDouble(StockMovement::getQuantity)
                .sum();
    }
}