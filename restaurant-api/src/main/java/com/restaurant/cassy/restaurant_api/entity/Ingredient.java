package com.restaurant.cassy.restaurant_api.entity;

public class Ingredient {

    private Integer id;
    private String name;
    private Double sellingPrice;
    private CategoryEnum category;
    private Double requiredQuantity;

    public Ingredient() {}

    public Ingredient(Integer id, String name, Double sellingPrice,
                      CategoryEnum category, Double requiredQuantity) {
        this.id = id;
        this.name = name;
        this.sellingPrice = sellingPrice;
        this.category = category;
        this.requiredQuantity = requiredQuantity;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(Double sellingPrice) { this.sellingPrice = sellingPrice; }

    public CategoryEnum getCategory() { return category; }
    public void setCategory(CategoryEnum category) { this.category = category; }

    public Double getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(Double requiredQuantity) { this.requiredQuantity = requiredQuantity; }
}