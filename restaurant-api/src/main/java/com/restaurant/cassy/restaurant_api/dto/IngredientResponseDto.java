package com.restaurant.cassy.restaurant_api.dto;

import com.restaurant.cassy.restaurant_api.entity.CategoryEnum;

public class IngredientResponseDto {

    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;

    public IngredientResponseDto(Integer id, String name, CategoryEnum category, Double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public CategoryEnum getCategory() { return category; }
    public Double getPrice() { return price; }
}