package com.restaurant.cassy.restaurant_api.service;

import com.restaurant.cassy.restaurant_api.dto.*;
import com.restaurant.cassy.restaurant_api.entity.Dish;
import com.restaurant.cassy.restaurant_api.entity.Ingredient;
import com.restaurant.cassy.restaurant_api.repository.DishRepository;
import com.restaurant.cassy.restaurant_api.repository.IngredientRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DishService {

    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;

    public DishService(DishRepository dishRepository,
                       IngredientRepository ingredientRepository) {
        this.dishRepository = dishRepository;
        this.ingredientRepository = ingredientRepository;
    }

    private DishResponseDto toDto(Dish dish) {
        List<IngredientResponseDto> ingredientDtos = new ArrayList<>();
        if (dish.getIngredients() != null) {
            for (Ingredient i : dish.getIngredients()) {
                ingredientDtos.add(new IngredientResponseDto(
                        i.getId(),
                        i.getName(),
                        i.getCategory(),
                        i.getSellingPrice()
                ));
            }
        }
        return new DishResponseDto(
                dish.getId(),
                dish.getName(),
                dish.getSellingPrice(),
                ingredientDtos
        );
    }

    public List<DishResponseDto> findAll() throws SQLException {
        List<Dish> dishes = dishRepository.findAll();
        return dishes.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Optional<DishResponseDto> findById(Integer id) throws SQLException {
        Optional<Dish> dish = dishRepository.findById(id);
        return dish.map(this::toDto);
    }

    public DishResponseDto updateIngredients(Integer dishId, List<Ingredient> requested) throws SQLException {
        Optional<Dish> optionalDish = dishRepository.findById(dishId);
        Dish dish = optionalDish.orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        List<Integer> ids = requested.stream().map(Ingredient::getId).collect(Collectors.toList());
        List<Ingredient> validIngredients = ingredientRepository.findAllByIds(ids);
        dish.setIngredients(validIngredients);

        dishRepository.save(dish);
        return toDto(dish);
    }

    public DishResponseDto saveDish(Dish dishToSave) throws SQLException {
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
            List<Integer> ids = dishToSave.getIngredients().stream()
                    .map(Ingredient::getId)
                    .collect(Collectors.toList());
            dish.setIngredients(ingredientRepository.findAllByIds(ids));
        }

        dishRepository.save(dish);
        return toDto(dish);
    }

    public List<DishResponseDto> findByIngredientName(String ingredientName) throws SQLException {
        List<Dish> dishes = dishRepository.findByIngredientName(ingredientName);
        return dishes.stream().map(this::toDto).collect(Collectors.toList());
    }

    public CostResponseDto getDishCost(Integer id) throws SQLException {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));
        return new CostResponseDto(dish.getName(), dish.getDishCost());
    }

    public MarginResponseDto getGrossMargin(Integer id) throws SQLException {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        Double margin;
        try {
            margin = dish.getGrossMargin();
        } catch (RuntimeException e) {
            if ("SELLING_PRICE_NULL".equals(e.getMessage())) {
                throw new RuntimeException("SELLING_PRICE_NULL");
            }
            throw e;
        }

        return new MarginResponseDto(
                dish.getName(),
                dish.getSellingPrice(),
                dish.getDishCost(),
                margin
        );
    }

    public List<DishResponseDto> createDishes(List<DishCreateDto> dishDtos) throws SQLException {
        List<DishResponseDto> created = new ArrayList<>();
        for (DishCreateDto dto : dishDtos) {
            if (dishRepository.findByName(dto.getName()).isPresent()) {
                throw new RuntimeException("Dish.name=" + dto.getName() + " already exists");
            }

            Dish dish = new Dish();
            dish.setName(dto.getName());
            dish.setDishType(dto.getDishType());
            dish.setSellingPrice(dto.getSellingPrice());

            Dish saved = dishRepository.save(dish);
            created.add(toDto(saved));
        }
        return created;
    }

    public List<DishResponseDto> getDishesFiltered(Double priceOver, Double priceUnder, String name) throws SQLException {
        List<Dish> dishes = dishRepository.findFiltered(priceOver, priceUnder, name);
        return dishes.stream().map(this::toDto).collect(Collectors.toList());
    }
}