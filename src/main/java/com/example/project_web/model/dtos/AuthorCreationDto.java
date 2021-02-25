package com.example.project_web.model.dtos;

import com.example.project_web.model.Author;

import java.util.ArrayList;
import java.util.List;

public class AuthorCreationDto {
    private List<Author> authors;

    public AuthorCreationDto() {
        this.authors = new ArrayList<>();
    }

    public AuthorCreationDto(List<Author> authors) {
        this.authors = authors;
    }

    public void addAuthor(Author author) {
        this.authors.add(author);
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }
}
