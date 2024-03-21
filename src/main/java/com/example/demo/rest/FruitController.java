package com.example.demo.rest;

import com.example.demo.FruitProto;
import com.example.demo.bll.FruitService;
import com.example.demo.bll.ProtobufService;
import com.example.demo.entities.Fruit;
import com.example.demo.repositories.FruitRepository;
import com.google.protobuf.InvalidProtocolBufferException;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class FruitController {
    @Autowired
    FruitService fruitService;

    @Autowired
    ProtobufService protobufService;


    @GetMapping("/json/{id}")
    ResponseEntity<Fruit> getFruitByIdJSON(@PathVariable("id") int id) {
        Optional<Fruit> fruit = fruitService.getFruitById(id);

        if (fruit.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).header("lilit_execution_time", "555").body(fruit.get());
        } else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("lilit_message", "123").build();
    }

    @GetMapping("/{id}")
    ResponseEntity<FruitProto.Fruit> getFruitById(@PathVariable("id") int id) {
        Optional<Fruit> fruit = fruitService.getFruitById(id);

        if (fruit.isPresent()) {
            FruitProto.Fruit fp = protobufService.convert(fruit.get());
            return ResponseEntity.status(HttpStatus.OK).header("lilit_execution_time", "555").body(fp);
        } else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("lilit_message", "123").build();
    }

    @GetMapping("/")
    ResponseEntity<List<Fruit>> getAllFruits() {
        List<Fruit> fruits = fruitService.getAllFruits();

        return ResponseEntity.status(HttpStatus.OK).header("lilit_execution_time", "555").body(fruits);
    }

    @PostMapping("/")
    ResponseEntity<Integer> saveFruit(@RequestBody Fruit fruit) {
        Fruit savedFruit = fruitService.save(fruit);

        return ResponseEntity.status(HttpStatus.OK).header("lilit_execution_time", "555").body(savedFruit.getId());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Integer> deleteFruit(@PathVariable("id") int id) {
        boolean res = fruitService.delete(id);
        if (res)
            return ResponseEntity.status(HttpStatus.OK).header("lilit_execution_time", "555").body(id);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("lilit_message", "123").build();
    }

    @GetMapping("/pages")
    @ResponseBody
    ResponseEntity<List<Fruit>> getPagedFruits(@RequestParam(value = "from") int from, @RequestParam(value = "pageSize") int pageSize) {
        List<Fruit> fruits = fruitService.getFruitsPageableByName(from, pageSize);

        return ResponseEntity.status(HttpStatus.OK).header("lilit_execution_time", "555").body(fruits);
    }

    @PostMapping("/intersect")
    ResponseEntity<FruitProto.Fruits> getIntersection(@RequestBody byte[] filterProtoBin) throws InvalidProtocolBufferException {
        try {
            File outputFile = new File("outputFile.bat");
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(filterProtoBin);
            }
        } catch (Exception e) {

        }

        FruitProto.Geometry filterProto = FruitProto.Geometry.parseFrom(filterProtoBin);
        Geometry geom = protobufService.convert(filterProto);

        List<Fruit> intersectionFruits = fruitService.getIntersection(geom);
        FruitProto.Fruits.Builder fruitsProto = FruitProto.Fruits.newBuilder();
        for (Fruit f : intersectionFruits)
            fruitsProto.addFruits(protobufService.convert(f));

        return ResponseEntity.status(HttpStatus.OK).header("lilit_execution_time", "555").body(fruitsProto.build());
    }

    @PostMapping("/json/intersect")
    ResponseEntity<List<Fruit>> getIntersectionJson(@RequestBody byte[] filterProtoBin) throws InvalidProtocolBufferException {
        FruitProto.Geometry filterProto = FruitProto.Geometry.parseFrom(filterProtoBin);
        Geometry geom = protobufService.convert(filterProto);

        List<Fruit> intersectionFruits = fruitService.getIntersection(geom);

        return ResponseEntity.status(HttpStatus.OK).header("lilit_execution_time", "555").body(intersectionFruits);
    }
}
