package com.restaurant.cassy.restaurant_api.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dish")
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "dish_type")
    @Enumerated(EnumType.STRING)
    private DishTypeEnum dishType;

    @Column(name = "selling_price")
    private Double sellingPrice;

    @ManyToMany
    @JoinTable(
            name = "dish_ingredient",
            joinColumns = @JoinColumn(name = "dish_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<Ingredient> ingredients = new ArrayList<>();

    public Double getDishCost() {
        if (ingredients == null || ingredients.isEmpty()) return 0.0;
        return ingredients.stream()
                .mapToDouble(i -> {
                    double price = i.getSellingPrice() != null ? i.getSellingPrice() : 0.0;
                    double qty = i.getRequiredQuantity() != null ? i.getRequiredQuantity() : 1.0;
                    return price * qty;
                })
                .sum();
    }

    public Double getGrossMargin() {
        if (sellingPrice == null) {
            throw new RuntimeException("SELLING_PRICE_NULL");
        }
        return sellingPrice - getDishCost();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DishTypeEnum getDishType() { return dishType; }
    public void setDishType(DishTypeEnum dishType) { this.dishType = dishType; }

    public Double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(Double sellingPrice) { this.sellingPrice = sellingPrice; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }
}