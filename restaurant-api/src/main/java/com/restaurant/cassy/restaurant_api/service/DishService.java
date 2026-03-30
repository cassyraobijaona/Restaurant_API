package com.restaurant.cassy.restaurant_api.service;

import com.restaurant.cassy.restaurant_api.dto.DishResponseDto;
import com.restaurant.cassy.restaurant_api.dto.IngredientResponseDto;
import com.restaurant.cassy.restaurant_api.entity.Dish;
import com.restaurant.cassy.restaurant_api.entity.Ingredient;
import com.restaurant.cassy.restaurant_api.repository.DishRepository;
import com.restaurant.cassy.restaurant_api.repository.IngredientRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DishService {

    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;

    public DishService(DishRepository dishRepository,
                       IngredientRepository ingredientRepository) {
        this.dishRepository = dishRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<DishResponseDto> findAll() {
        return dishRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public Optional<DishResponseDto> findById(Integer id) {
        return dishRepository.findById(id)
                .map(this::toDto);
    }

    public DishResponseDto updateIngredients(Integer dishId, List<Ingredient> requested) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        List<Integer> ids = requested.stream()
                .map(Ingredient::getId)
                .toList();

        List<Ingredient> validIngredients = ingredientRepository.findAllById(ids);
        dish.setIngredients(validIngredients);

        return toDto(dishRepository.save(dish));
    }

    private DishResponseDto toDto(Dish dish) {
        List<IngredientResponseDto> ingredientDtos = dish.getIngredients() == null
                ? List.of()
                : dish.getIngredients().stream()
                .map(i -> new IngredientResponseDto(
                        i.getId(),
                        i.getName(),
                        i.getCategory(),
                        i.getSellingPrice()
                ))
                .toList();

        return new DishResponseDto(
                dish.getId(),
                dish.getName(),
                dish.getSellingPrice(),
                ingredientDtos
        );
    }

    @Transactional
    public DishResponseDto saveDish(Dish dishToSave) {
        Dish dish;

        if (dishToSave.getId() != null) {
            dish = dishRepository.findById(dishToSave.getId())
                    .orElseThrow(() -> new RuntimeException("NOT_FOUND"));
            dish.setName(dishToSave.getName());
            dish.setDishType(dishToSave.getDishType());
            dish.setSellingPrice(dishToSave.getSellingPrice());
        } else {
            dish = new Dish();
            dish.setName(dishToSave.getName());
            dish.setDishType(dishToSave.getDishType());
            dish.setSellingPrice(dishToSave.getSellingPrice());
        }

        if (dishToSave.getIngredients() != null) {
            List<Integer> ids = dishToSave.getIngredients()
                    .stream()
                    .map(Ingredient::getId)
                    .filter(id -> id != null)
                    .toList();
            List<Ingredient> validIngredients = ingredientRepository.findAllById(ids);
            dish.setIngredients(validIngredients);
        }

        return toDto(dishRepository.save(dish));
    }

    public List<DishResponseDto> findByIngredientName(String ingredientName) {
        return dishRepository.findByIngredientName(ingredientName)
                .stream()
                .map(this::toDto)
                .toList();
    }
}