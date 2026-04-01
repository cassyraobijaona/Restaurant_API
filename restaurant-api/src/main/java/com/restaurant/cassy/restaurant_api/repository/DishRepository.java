package com.restaurant.cassy.restaurant_api.repository;

import com.restaurant.cassy.restaurant_api.datasource.DataSource;
import com.restaurant.cassy.restaurant_api.entity.Dish;
import com.restaurant.cassy.restaurant_api.entity.DishTypeEnum;
import com.restaurant.cassy.restaurant_api.entity.Ingredient;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DishRepository {

    private final DataSource dataSource;
    private final IngredientRepository ingredientRepository;

    public DishRepository(DataSource dataSource,
                          IngredientRepository ingredientRepository) {
        this.dataSource = dataSource;
        this.ingredientRepository = ingredientRepository;
    }

    private Dish mapRow(ResultSet rs) throws SQLException {
        Dish dish = new Dish(
                rs.getInt("id"),
                rs.getString("name"),
                DishTypeEnum.valueOf(rs.getString("dish_type")),
                rs.getObject("selling_price") != null ? rs.getDouble("selling_price") : null
        );
        dish.setIngredients(ingredientRepository.findByDishId(dish.getId()));
        return dish;
    }

    public List<Dish> findAll() {
        List<Dish> list = new ArrayList<>();
        String sql = "SELECT * FROM dish ORDER BY id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public Optional<Dish> findById(Integer id) {
        String sql = "SELECT * FROM dish WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public List<Dish> findByIngredientName(String ingredientName) {
        List<Dish> list = new ArrayList<>();
        String sql = """
            SELECT DISTINCT d.* FROM dish d
            JOIN dish_ingredient di ON di.dish_id = d.id
            JOIN ingredient i ON i.id = di.ingredient_id
            WHERE LOWER(i.name) LIKE LOWER(?)
            ORDER BY d.id
            """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + ingredientName + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public Dish save(Dish dish) {
        if (dish.getId() == null) {
            String sql = "INSERT INTO dish (name, dish_type, selling_price) " +
                    "VALUES (?, ?, ?) RETURNING *";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, dish.getName());
                ps.setString(2, dish.getDishType().name());
                if (dish.getSellingPrice() != null)
                    ps.setDouble(3, dish.getSellingPrice());
                else
                    ps.setNull(3, Types.NUMERIC);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    dish.setId(rs.getInt("id"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            String sql = "UPDATE dish SET name=?, dish_type=?, selling_price=? WHERE id=?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, dish.getName());
                ps.setString(2, dish.getDishType().name());
                if (dish.getSellingPrice() != null)
                    ps.setDouble(3, dish.getSellingPrice());
                else
                    ps.setNull(3, Types.NUMERIC);
                ps.setInt(4, dish.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        updateDishIngredients(dish.getId(), dish.getIngredients());
        return findById(dish.getId()).orElse(dish);
    }

    private void updateDishIngredients(Integer dishId, List<Ingredient> ingredients) {
        String deleteSql = "DELETE FROM dish_ingredient WHERE dish_id = ?";
        String insertSql = "INSERT INTO dish_ingredient (dish_id, ingredient_id) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement deletePs = conn.prepareStatement(deleteSql);
            deletePs.setInt(1, dishId);
            deletePs.executeUpdate();

            if (ingredients != null && !ingredients.isEmpty()) {
                PreparedStatement insertPs = conn.prepareStatement(insertSql);
                for (Ingredient i : ingredients) {
                    insertPs.setInt(1, dishId);
                    insertPs.setInt(2, i.getId());
                    insertPs.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}