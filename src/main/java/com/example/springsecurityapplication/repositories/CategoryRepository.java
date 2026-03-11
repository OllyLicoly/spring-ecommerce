package com.example.springsecurityapplication.repositories;

import com.example.springsecurityapplication.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    //Поиск категорий по наименованию (title)
    Optional<Category> findByName(String name);


}
