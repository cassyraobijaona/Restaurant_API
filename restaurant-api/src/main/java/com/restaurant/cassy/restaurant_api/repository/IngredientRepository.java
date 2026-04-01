package com.restaurant.cassy.restaurant_api.repository;

import com.restaurant.cassy.restaurant_api.datasource.DataSource;
import com.restaurant.cassy.restaurant_api.entity.CategoryEnum;
import com.restaurant.cassy.restaurant_api.entity.Ingredient;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class IngredientRepository {

    private final DataSource dataSource;

    public IngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Ingredient mapRow(ResultSet rs) throws SQLException {
        return new Ingredient(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("selling_price"),
                CategoryEnum.valueOf(rs.getString("category")),
                rs.getObject("required_quantity") != null ? rs.getDouble("required_quantity") : null
        );
    }

    public List<Ingredient> findAll() {
        List<Ingredient> list = new ArrayList<>();
        String sql = "SELECT * FROM ingredient ORDER BY id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Ingredient> findAllPaginated(int page, int size) {
        List<Ingredient> list = new ArrayList<>();
        String sql = "SELECT * FROM ingredient ORDER BY id LIMIT ? OFFSET ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public Optional<Ingredient> findById(Integer id) {
        String sql = "SELECT * FROM ingredient WHERE id = ?";
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

    public boolean existsById(Integer id) {
        return findById(id).isPresent();
    }

    public List<Ingredient> findByCriteria(String name, String category,
                                           String dishName, int page, int size) {
        List<Ingredient> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT i.* FROM ingredient i
            LEFT JOIN dish_ingredient di ON di.ingredient_id = i.id
            LEFT JOIN dish d ON d.id = di.dish_id
            WHERE 1=1
            """);

        List<Object> params = new ArrayList<>();
        if (name != null) {
            sql.append(" AND LOWER(i.name) LIKE LOWER(?)");
            params.add("%" + name + "%");
        }
        if (category != null) {
            sql.append(" AND i.category = CAST(? AS VARCHAR)");
            params.add(category);
        }
        if (dishName != null) {
            sql.append(" AND LOWER(d.name) LIKE LOWER(?)");
            params.add("%" + dishName + "%");
        }
        sql.append(" ORDER BY i.id LIMIT ? OFFSET ?");
        params.add(size);
        params.add((page - 1) * size);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Ingredient> saveAll(List<Ingredient> ingredients) {
        List<Ingredient> saved = new ArrayList<>();
        String sql = "INSERT INTO ingredient (name, selling_price, category, required_quantity) " +
                "VALUES (?, ?, ?, ?) RETURNING *";
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (Ingredient i : ingredients) {
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, i.getName());
                    ps.setDouble(2, i.getSellingPrice() != null ? i.getSellingPrice() : 0.0);
                    ps.setString(3, i.getCategory().name());
                    if (i.getRequiredQuantity() != null)
                        ps.setDouble(4, i.getRequiredQuantity());
                    else
                        ps.setNull(4, Types.NUMERIC);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) saved.add(mapRow(rs));
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return saved;
    }

    public Ingredient save(Ingredient ingredient) {
        if (ingredient.getId() == null) {
            String sql = "INSERT INTO ingredient (name, selling_price, category, required_quantity) " +
                    "VALUES (?, ?, ?, ?) RETURNING *";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, ingredient.getName());
                ps.setDouble(2, ingredient.getSellingPrice() != null ? ingredient.getSellingPrice() : 0.0);
                ps.setString(3, ingredient.getCategory().name());
                if (ingredient.getRequiredQuantity() != null)
                    ps.setDouble(4, ingredient.getRequiredQuantity());
                else
                    ps.setNull(4, Types.NUMERIC);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) return mapRow(rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            String sql = "UPDATE ingredient SET name=?, selling_price=?, " +
                    "category=?, required_quantity=? WHERE id=? RETURNING *";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, ingredient.getName());
                ps.setDouble(2, ingredient.getSellingPrice() != null ? ingredient.getSellingPrice() : 0.0);
                ps.setString(3, ingredient.getCategory().name());
                if (ingredient.getRequiredQuantity() != null)
                    ps.setDouble(4, ingredient.getRequiredQuantity());
                else
                    ps.setNull(4, Types.NUMERIC);
                ps.setInt(5, ingredient.getId());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) return mapRow(rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return ingredient;
    }

    public List<Ingredient> findByIds(List<Integer> ids) {
        if (ids.isEmpty()) return new ArrayList<>();
        List<Ingredient> list = new ArrayList<>();
        String placeholders = ids.stream().map(i -> "?").reduce((a, b) -> a + "," + b).orElse("?");
        String sql = "SELECT * FROM ingredient WHERE id IN (" + placeholders + ")";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < ids.size(); i++) ps.setInt(i + 1, ids.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Ingredient> findByDishId(Integer dishId) {
        List<Ingredient> list = new ArrayList<>();
        String sql = """
            SELECT i.* FROM ingredient i
            JOIN dish_ingredient di ON di.ingredient_id = i.id
            WHERE di.dish_id = ?
            ORDER BY i.id
            """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}