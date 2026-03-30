package com.restaurant.cassy.restaurant_api.dto;

import java.util.List;

public class DishResponseDto {

    private Integer id;
    private String name;
    private Double sellingPrice;
    private List<IngredientResponseDto> ingredients;

    public DishResponseDto(Integer id, String name, Double sellingPrice,
                           List<IngredientResponseDto> ingredients) {
        this.id = id;
        this.name = name;
        this.sellingPrice = sellingPrice;
        this.ingredients = ingredients;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public List<IngredientResponseDto> getIngredients() {
        return ingredients;
    }
}