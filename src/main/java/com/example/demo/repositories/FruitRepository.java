package com.example.demo.repositories;

import com.example.demo.entities.Fruit;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FruitRepository extends JpaRepository<Fruit, Integer> {
    @Query(value="SELECT f FROM Fruit f WHERE intersects(f.location, :filter) = true")
    List<Fruit> findItemsIntersects(Geometry filter);
}
