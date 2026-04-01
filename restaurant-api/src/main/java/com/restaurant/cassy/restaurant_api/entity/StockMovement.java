package com.restaurant.cassy.restaurant_api.entity;

import java.time.LocalDateTime;

public class StockMovement {

    private Integer id;
    private Integer ingredientId;
    private Double quantity;
    private String unit;
    private MovementTypeEnum movementType;
    private LocalDateTime movementDate;

    public StockMovement() {}

    public StockMovement(Integer id, Integer ingredientId, Double quantity,
                         String unit, MovementTypeEnum movementType,
                         LocalDateTime movementDate) {
        this.id = id;
        this.ingredientId = ingredientId;
        this.quantity = quantity;
        this.unit = unit;
        this.movementType = movementType;
        this.movementDate = movementDate;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIngredientId() { return ingredientId; }
    public void setIngredientId(Integer ingredientId) { this.ingredientId = ingredientId; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public MovementTypeEnum getMovementType() { return movementType; }
    public void setMovementType(MovementTypeEnum movementType) { this.movementType = movementType; }

    public LocalDateTime getMovementDate() { return movementDate; }
    public void setMovementDate(LocalDateTime movementDate) { this.movementDate = movementDate; }
}