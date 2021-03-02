package com.example.project_web.service;

import com.example.project_web.model.Author;
import com.example.project_web.model.Book;
import com.example.project_web.model.User;
import com.example.project_web.model.enumerations.FacultyChoice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface BookServiceInterface {
    List<Book> listAll();
    Book delete(Long id);
    Book update(Long id, String bookName, int yearPublished, FacultyChoice facultyChoice, List<Author> authors, User user, String description);
    Optional<Book> findById(Long id);
    List<Book> findAllByUserBuyer(User user);
}
