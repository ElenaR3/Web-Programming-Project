package com.example.project_web.service;
import com.example.project_web.model.Author;
import com.example.project_web.model.Book;
import com.example.project_web.model.ShoppingCart;
import com.example.project_web.model.User;
import com.example.project_web.model.enumerations.FacultyChoice;
import com.example.project_web.model.exceptions.InvalidBookIdException;
import com.example.project_web.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class BookService implements BookServiceInterface {

    private final BookRepository bookRepository;
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> searchByNameOrAuthor(String name, String author) {
        // return this.bookRepository.findAllByBookNameLikeOrAuthorsLike(name, )
        return null;
    }

    @Override
    public List<Book> listAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book delete(Long id) {
            Book book = this.bookRepository.findById(id).orElseThrow(InvalidBookIdException::new);
            this.bookRepository.delete(book);
            return book;
        }

    @Override
    public Book update(Long id, String bookName, int yearPublished, FacultyChoice facultyChoice,
                       List<Author> authors, User user, String description) {
        Book book = this.bookRepository.findById(id).orElseThrow(InvalidBookIdException::new);
        book.setAuthors(authors);
        book.setBookName(bookName);
        book.setYearPublished(yearPublished);
        book.setUser(user);
        book.setWhichFaculty(facultyChoice);
        book.setDescription(description);
        return this.bookRepository.save(book);
    }

    public Book updateReserved(Long id, User user_buyer, ShoppingCart cart) {
        Book book = this.bookRepository.findById(id).orElseThrow(InvalidBookIdException::new);
        book.setShoppingCart(cart);
        book.setUserBuyer(user_buyer);
        return this.bookRepository.save(book);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return this.bookRepository.findById(id);
    }

    public List<Book> findAllByUser(User user) {
        return bookRepository.findAllByUser(user);
    }

    @Override
    public List<Book> findAllByUserBuyer(User user) {
        return bookRepository.findAllByUserBuyer(user);
    }

    public Page<Book> findPaginated(Pageable pageable, List<Book> books) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Book> list;

        if (books.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, books.size());
            list = books.subList(startItem, toIndex);
        }

        Page<Book> bookPage = new PageImpl<Book>(list, PageRequest.of(currentPage, pageSize), books.size());

        return bookPage;
    }
}
