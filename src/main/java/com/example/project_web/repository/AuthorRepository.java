package com.example.project_web.repository;

import com.example.project_web.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface AuthorRepository extends JpaRepository<Author,Long> {
}
