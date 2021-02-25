package com.example.project_web.repository;

import com.example.project_web.model.Book;
import com.example.project_web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByBookNameLikeOrAuthorsLike(String bookName, String author);
    void deleteByBookName(String name);
    List<Book> findAllByUser(User user);
    List<Book> findAllByUserBuyer(User user);
}
