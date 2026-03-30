package com.restaurant.cassy.restaurant_api.repository;

import com.restaurant.cassy.restaurant_api.entity.Ingredient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {

    @Query("SELECT i FROM Ingredient i")
    List<Ingredient> findAllPaginated(Pageable pageable);

    @Query(value = """
        SELECT DISTINCT i.* FROM ingredient i
        LEFT JOIN dish_ingredient di ON di.ingredient_id = i.id
        LEFT JOIN dish d ON d.id = di.dish_id
        WHERE (:name IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:category IS NULL OR i.category = CAST(:category AS VARCHAR))
        AND (:dishName IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :dishName, '%')))
        LIMIT :size OFFSET :offset
        """, nativeQuery = true)
    List<Ingredient> findByCriteria(
            @Param("name") String name,
            @Param("category") String category,
            @Param("dishName") String dishName,
            @Param("size") int size,
            @Param("offset") int offset
    );
}