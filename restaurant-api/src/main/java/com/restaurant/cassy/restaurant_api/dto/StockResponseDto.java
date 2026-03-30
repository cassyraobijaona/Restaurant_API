package com.restaurant.cassy.restaurant_api.dto;

public class StockResponseDto {

    private String unit;
    private Double value;

    public StockResponseDto(String unit, Double value) {
        this.unit = unit;
        this.value = value;
    }

    public String getUnit() { return unit; }
    public Double getValue() { return value; }
}