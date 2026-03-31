package com.restaurant.cassy.restaurant_api.repository;

import com.restaurant.cassy.restaurant_api.entity.CategoryEnum;
import com.restaurant.cassy.restaurant_api.entity.Dish;
import com.restaurant.cassy.restaurant_api.entity.DishTypeEnum;
import com.restaurant.cassy.restaurant_api.entity.Ingredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DishRepository {

    private final Connection connection;

    public DishRepository(Connection connection) {
        this.connection = connection;
    }
    public List<Dish> findAll() throws SQLException {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT id, name, selling_price, dish_type FROM dish";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setSellingPrice(rs.getDouble("selling_price"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setIngredients(getIngredientsByDishId(dish.getId()));
                dishes.add(dish);
            }
        }
        return dishes;
    }
    public Optional<Dish> findById(Integer id) throws SQLException {
        String sql = "SELECT id, name, selling_price, dish_type FROM dish WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setSellingPrice(rs.getDouble("selling_price"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setIngredients(getIngredientsByDishId(dish.getId()));
                return Optional.of(dish);
            }
        }
        return Optional.empty();
    }
    public List<Dish> findByIngredientName(String ingredientName) throws SQLException {
        List<Dish> dishes = new ArrayList<>();
        String sql = """
                SELECT DISTINCT d.id, d.name, d.selling_price, d.dish_type
                FROM dish d
                JOIN dish_ingredient di ON di.dish_id = d.id
                JOIN ingredient i ON i.id = di.ingredient_id
                WHERE LOWER(i.name) LIKE LOWER(?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + ingredientName + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setSellingPrice(rs.getDouble("selling_price"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setIngredients(getIngredientsByDishId(dish.getId()));
                dishes.add(dish);
            }
        }
        return dishes;
    }
    private List<Ingredient> getIngredientsByDishId(int dishId) throws SQLException {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = """
                SELECT i.id, i.name, i.selling_price, i.category
                FROM ingredient i
                JOIN dish_ingredient di ON di.ingredient_id = i.id
                WHERE di.dish_id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, dishId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setSellingPrice(rs.getDouble("selling_price"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                ingredients.add(ingredient);
            }
        }
        return ingredients;
    }

    public Dish save(Dish dish) throws SQLException {
        if (dish.getId() == null) {
            String sql = "INSERT INTO dish (name, selling_price, dish_type) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, dish.getName());
                stmt.setDouble(2, dish.getSellingPrice());
                stmt.executeUpdate();
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    dish.setId(keys.getInt(1));
                }
            }
        } else {
            String sql = "UPDATE dish SET name = ?, selling_price = ?, dish_type = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, dish.getName());
                stmt.setDouble(2, dish.getSellingPrice());
                stmt.setInt(4, dish.getId());
                stmt.executeUpdate();
            }
        }
        return dish;
    }

    public boolean existsByName(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM dish WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    public Optional<Dish> findByName(String name) throws SQLException {
        String sql = "SELECT id, name, selling_price, dish_type FROM dish WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setSellingPrice(rs.getDouble("selling_price"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setIngredients(getIngredientsByDishId(dish.getId()));
                return Optional.of(dish);
            }
        }
        return Optional.empty();
    }

    public List<Dish> findFiltered(Double priceOver, Double priceUnder, String name) throws SQLException {
        List<Dish> dishes = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, name, selling_price, dish_type FROM dish WHERE 1=1");
        if (priceOver != null) sql.append(" AND selling_price >= ").append(priceOver);
        if (priceUnder != null) sql.append(" AND selling_price <= ").append(priceUnder);
        if (name != null && !name.isEmpty()) sql.append(" AND LOWER(name) LIKE LOWER('%").append(name).append("%')");
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString());
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setSellingPrice(rs.getDouble("selling_price"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setIngredients(getIngredientsByDishId(dish.getId()));
                dishes.add(dish);
            }
        }
        return dishes;
    }
}