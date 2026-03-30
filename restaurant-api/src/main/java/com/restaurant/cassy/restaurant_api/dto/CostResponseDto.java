package com.restaurant.cassy.restaurant_api.dto;

public class CostResponseDto {

    private String dishName;
    private Double cost;

    public CostResponseDto(String dishName, Double cost) {
        this.dishName = dishName;
        this.cost = cost;
    }

    public String getDishName() { return dishName; }
    public Double getCost() { return cost; }
}