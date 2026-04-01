package com.restaurant.cassy.restaurant_api.service;

import com.restaurant.cassy.restaurant_api.dto.IngredientResponseDto;
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

    public List<IngredientResponseDto> findAll() {
        return ingredientRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<IngredientResponseDto> findAllPaginated(int page, int size) {
        return ingredientRepository.findAllPaginated(page, size)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<IngredientResponseDto> findByCriteria(
            String name, String category, String dishName,
            int page, int size) {
        return ingredientRepository.findByCriteria(name, category, dishName, page, size)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public Optional<IngredientResponseDto> findById(Integer id) {
        return ingredientRepository.findById(id).map(this::toDto);
    }

    public boolean existsById(Integer id) {
        return ingredientRepository.existsById(id);
    }

    public List<IngredientResponseDto> createIngredients(List<Ingredient> newIngredients) {
        for (Ingredient ingredient : newIngredients) {
            boolean exists = ingredientRepository.findAll()
                    .stream()
                    .anyMatch(i -> i.getName().equalsIgnoreCase(ingredient.getName()));
            if (exists) {
                throw new RuntimeException(
                        "Ingredient '" + ingredient.getName() + "' already exists"
                );
            }
        }
        return ingredientRepository.saveAll(newIngredients)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public IngredientResponseDto saveIngredient(Integer id, Ingredient toSave) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        if (toSave.getName() != null) ingredient.setName(toSave.getName());
        if (toSave.getSellingPrice() != null) ingredient.setSellingPrice(toSave.getSellingPrice());
        if (toSave.getCategory() != null) ingredient.setCategory(toSave.getCategory());
        if (toSave.getRequiredQuantity() != null) ingredient.setRequiredQuantity(toSave.getRequiredQuantity());

        return toDto(ingredientRepository.save(ingredient));
    }

    public double getStockValueAt(Integer ingredientId, LocalDateTime at) {
        List<StockMovement> movements = stockMovementRepository
                .findByIngredientIdAndMovementDateLessThanEqual(ingredientId, at);
        return movements.stream()
                .mapToDouble(StockMovement::getQuantity)
                .sum();
    }

    private IngredientResponseDto toDto(Ingredient i) {
        return new IngredientResponseDto(
                i.getId(),
                i.getName(),
                i.getCategory(),
                i.getSellingPrice()
        );
    }
}