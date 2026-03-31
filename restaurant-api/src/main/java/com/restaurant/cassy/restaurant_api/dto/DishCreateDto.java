package com.restaurant.cassy.restaurant_api.dto;

import com.restaurant.cassy.restaurant_api.entity.DishTypeEnum;

public class DishCreateDto {
    private String name;
    private String dishType;
    private Double sellingPrice;

    public DishCreateDto() {}

    public DishCreateDto(String name, String dishType, Double sellingPrice) {
        this.name = name;
        this.dishType = dishType;
        this.sellingPrice = sellingPrice;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DishTypeEnum getDishType() { return dishType; }
    public void setDishType(String dishType) { this.dishType = dishType; }

    public Double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(Double sellingPrice) { this.sellingPrice = sellingPrice; }
}