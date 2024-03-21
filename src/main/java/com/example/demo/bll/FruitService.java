package com.example.demo.bll;

import com.example.demo.FruitProto;
import com.example.demo.entities.Fruit;
import com.example.demo.repositories.FruitRepository;
import org.hibernate.query.Order;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FruitService {
    @Autowired
    FruitRepository fruitRepository;

    public Optional<Fruit> getFruitById(int id) {
        return fruitRepository.findById(id);
    }

    public List<Fruit> getAllFruits() {
        return fruitRepository.findAll();
    }

    public Fruit save(Fruit fruit) {
        return fruitRepository.save(fruit);
    }

    public boolean delete(int id) {
        Optional<Fruit> fruit = getFruitById(id);
        if (fruit.isPresent()) {
            fruitRepository.deleteById(id);
            return true;
        } else
            return false;
    }

    public List<Fruit> getFruitsPageableByName(int from, int pageSize)
    {
        Pageable fruitsPage = PageRequest.of(from, pageSize, Sort.by("name").descending());

        return fruitRepository.findAll(fruitsPage).stream().toList();
    }

    public List<Fruit> getIntersection(Geometry geometry)
    {
        return fruitRepository.findItemsIntersects(geometry);
    }
}
