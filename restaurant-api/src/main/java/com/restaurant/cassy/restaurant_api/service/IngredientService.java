package com.restaurant.cassy.restaurant_api.service;

import com.restaurant.cassy.restaurant_api.dto.IngredientResponseDto;
import com.restaurant.cassy.restaurant_api.entity.Ingredient;
import com.restaurant.cassy.restaurant_api.repository.IngredientRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<IngredientResponseDto> findAll() throws SQLException {
        return ingredientRepository.findAll()
                .stream()
                .map(i -> new IngredientResponseDto(i.getId(), i.getName(), i.getCategory(), i.getSellingPrice()))
                .collect(Collectors.toList());
    }

    public Optional<IngredientResponseDto> findById(Integer id) throws SQLException {
        Optional<Ingredient> ingredient = ingredientRepository.findById(id);
        return ingredient.map(i -> new IngredientResponseDto(i.getId(), i.getName(), i.getCategory(), i.getSellingPrice()));
    }
}