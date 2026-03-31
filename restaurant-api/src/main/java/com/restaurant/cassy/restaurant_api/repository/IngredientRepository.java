package com.restaurant.cassy.restaurant_api.repository;

import com.restaurant.cassy.restaurant_api.entity.CategoryEnum;
import com.restaurant.cassy.restaurant_api.entity.Ingredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IngredientRepository {

    private final Connection connection;

    public IngredientRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Ingredient> findAll() throws SQLException {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT id, name, selling_price, category FROM ingredient";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Ingredient ingredient = mapRowToIngredient(rs);
                ingredients.add(ingredient);
            }
        }
        return ingredients;
    }

    public Optional<Ingredient> findById(Integer id) throws SQLException {
        String sql = "SELECT id, name, selling_price, category FROM ingredient WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToIngredient(rs));
                }
            }
        }
        return Optional.empty();
    }
    public List<Ingredient> findAllByIds(List<Integer> ids) throws SQLException {
        List<Ingredient> ingredients = new ArrayList<>();
        if (ids.isEmpty()) return ingredients;

        StringBuilder sql = new StringBuilder("SELECT id, name, selling_price, category FROM ingredient WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            sql.append("?");
            if (i < ids.size() - 1) sql.append(",");
        }
        sql.append(")");

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < ids.size(); i++) {
                stmt.setInt(i + 1, ids.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(mapRowToIngredient(rs));
                }
            }
        }

        return ingredients;
    }

    // Sauvegarder un ingrédient (insert ou update)
    public Ingredient save(Ingredient ingredient) throws SQLException {
        if (ingredient.getId() == null) {
            String sql = "INSERT INTO ingredient (name, selling_price, category) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, ingredient.getName());
                stmt.setDouble(2, ingredient.getSellingPrice());
                stmt.setString(3, ingredient.getCategory().name());
                stmt.executeUpdate();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        ingredient.setId(keys.getInt(1));
                    }
                }
            }
        } else {
            String sql = "UPDATE ingredient SET name = ?, selling_price = ?, category = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, ingredient.getName());
                stmt.setDouble(2, ingredient.getSellingPrice());
                stmt.setString(3, ingredient.getCategory().name());
                stmt.setInt(4, ingredient.getId());
                stmt.executeUpdate();
            }
        }
        return ingredient;
    }

    public boolean existsByName(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ingredient WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private Ingredient mapRowToIngredient(ResultSet rs) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(rs.getInt("id"));
        ingredient.setName(rs.getString("name"));
        ingredient.setSellingPrice(rs.getDouble("selling_price"));
        ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
        return ingredient;
    }
}