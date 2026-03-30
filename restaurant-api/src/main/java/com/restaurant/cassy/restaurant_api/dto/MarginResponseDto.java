package com.restaurant.cassy.restaurant_api.dto;

public class MarginResponseDto {

    private String dishName;
    private Double sellingPrice;
    private Double cost;
    private Double grossMargin;

    public MarginResponseDto(String dishName, Double sellingPrice,
                             Double cost, Double grossMargin) {
        this.dishName = dishName;
        this.sellingPrice = sellingPrice;
        this.cost = cost;
        this.grossMargin = grossMargin;
    }

    public String getDishName() {
        return dishName;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public Double getCost() {
        return cost;
    }

    public Double getGrossMargin() {
        return grossMargin;
    }
}