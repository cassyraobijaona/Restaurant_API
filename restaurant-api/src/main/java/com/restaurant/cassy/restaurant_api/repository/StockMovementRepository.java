package com.restaurant.cassy.restaurant_api.repository;

import com.restaurant.cassy.restaurant_api.datasource.DataSource;
import com.restaurant.cassy.restaurant_api.entity.MovementTypeEnum;
import com.restaurant.cassy.restaurant_api.entity.StockMovement;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StockMovementRepository {

    private final DataSource dataSource;

    public StockMovementRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<StockMovement> findByIngredientIdAndMovementDateLessThanEqual(
            Integer ingredientId, LocalDateTime at) {
        List<StockMovement> list = new ArrayList<>();
        String sql = """
            SELECT * FROM stock_movement
            WHERE ingredient_id = ? AND movement_date <= ?
            ORDER BY movement_date
            """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            ps.setTimestamp(2, Timestamp.valueOf(at));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new StockMovement(
                        rs.getInt("id"),
                        rs.getInt("ingredient_id"),
                        rs.getDouble("quantity"),
                        rs.getString("unit"),
                        MovementTypeEnum.valueOf(rs.getString("movement_type")),
                        rs.getTimestamp("movement_date").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}