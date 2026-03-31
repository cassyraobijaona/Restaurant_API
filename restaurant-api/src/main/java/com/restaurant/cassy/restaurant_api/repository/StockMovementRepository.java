package com.restaurant.cassy.restaurant_api.repository;

import com.restaurant.cassy.restaurant_api.entity.MovementTypeEnum;
import com.restaurant.cassy.restaurant_api.entity.StockMovement;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StockMovementRepository {

    private final Connection connection;

    public StockMovementRepository(Connection connection) {
        this.connection = connection;
    }

    public List<StockMovement> findByIngredientIdAndMovementDateLessThanEqual(
            Integer ingredientId, LocalDateTime movementDate
    ) throws SQLException {

        List<StockMovement> movements = new ArrayList<>();

        String sql = "SELECT * FROM stock_movement " +
                "WHERE ingredient_id = ? AND movement_date <= ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ingredientId);
            stmt.setTimestamp(2, Timestamp.valueOf(movementDate));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                movements.add(mapRowToStockMovement(rs));
            }
        }

        return movements;
    }

    private StockMovement mapRowToStockMovement(ResultSet rs) throws SQLException {
        StockMovement movement = new StockMovement();
        movement.setId(rs.getInt("id"));
        movement.setIngredientId(rs.getInt("ingredient_id"));
        movement.setQuantity(rs.getDouble("quantity"));
        movement.setMovementType(MovementTypeEnum.valueOf(rs.getString("movement_type"))); // adapter si tu as un enum
        movement.setMovementDate(rs.getTimestamp("movement_date").toLocalDateTime());
        return movement;
    }
}