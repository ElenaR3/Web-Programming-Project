package com.example.project_web.web.controllers;

import com.example.project_web.model.Author;
import com.example.project_web.model.Book;
import com.example.project_web.model.ShoppingCart;
import com.example.project_web.model.User;
import com.example.project_web.model.dtos.AuthorCreationDto;
import com.example.project_web.model.enumerations.FacultyChoice;
import com.example.project_web.model.exceptions.InvalidBookIdException;
import com.example.project_web.repository.AuthorRepository;
import com.example.project_web.repository.BookRepository;
import com.example.project_web.service.BookService;
import com.example.project_web.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("myBooks")
public class UserPersonalBooksController {

    FacultyChoice facultyChoice;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public UserPersonalBooksController(BookService bookService, BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @GetMapping("/homepage")
    public String getHomepage(Model model){
        model.addAttribute("bodyContentUser", "buy_books");
        return "master-user-template";
    }

    @GetMapping("/sellBooks")
    public String sellBooks (Model model){
        List<FacultyChoice> enumValues = Arrays.asList(FacultyChoice.values());
        AuthorCreationDto authors = new AuthorCreationDto();

        for (int i = 1; i <= 5; i++) {
            authors.addAuthor(new Author());
        }

        model.addAttribute("authors", authors);
        model.addAttribute("facultyChoices", enumValues);
        model.addAttribute("bodyContentUser", "sell_books");
        return "master-user-template";
    }

    @GetMapping("/myBooksForSale")
    public String getBookList(Model model, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        List<Book> books = bookService.findAllByUser(user);
        model.addAttribute("books", books);
        model.addAttribute("bodyContentUser", "my-books-for-sale");
        return "master-user-template";
    }

    @GetMapping("/{id}/edit")
    public String showEdit(@PathVariable Long id, Model model) {
        Book book = this.bookService.findById(id).orElseThrow(InvalidBookIdException::new);
        model.addAttribute("book", book);

        List<FacultyChoice> enumValues = Arrays.asList(FacultyChoice.values());
        model.addAttribute("facultyChoices", enumValues);

        List<Author> authors = book.getAuthors();

       AuthorCreationDto authorCreationDto = new AuthorCreationDto(authors);

        model.addAttribute("authors", authorCreationDto);
        model.addAttribute("bodyContentUser", "sell_books");
        return "master-user-template";
    }

    @GetMapping("/booksFair")
    public String showBooks(Model model, @RequestParam("page") Optional<Integer> page,
                            @RequestParam("size") Optional<Integer> size) {
        List<Book> books = bookService.listAll();
        model.addAttribute("books", books);
        model.addAttribute("bodyContentUser", "buy_books");

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Page<Book> bookPage = bookService.findPaginated(PageRequest.of(currentPage - 1, pageSize), books);

        model.addAttribute("bookPage", bookPage);

        int totalPages = bookPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "master-user-template";
    }

    @GetMapping("/personalReserved")
    public String showBoughtBooks(Model model, @RequestParam("page") Optional<Integer> page,
                            @RequestParam("size") Optional<Integer> size,
                                  Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        List<Book> books = bookService.findAllByUserBuyer(user);
        model.addAttribute("books", books);
        model.addAttribute("bodyContentUser", "wish-list");

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Page<Book> bookPage = bookService.findPaginated(PageRequest.of(currentPage - 1, pageSize), books);

        model.addAttribute("bookPage", bookPage);

        int totalPages = bookPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "master-user-template";
    }

    @GetMapping("/{id}/reservation")
    public String showBook(@PathVariable Long id, Model model) {
        Book book = this.bookService.findById(id).orElseThrow(InvalidBookIdException::new);
        model.addAttribute("book", book);

        List<FacultyChoice> enumValues = Arrays.asList(FacultyChoice.values());
        model.addAttribute("facultyChoices", enumValues);

        List<Author> authors = book.getAuthors();

        AuthorCreationDto authorCreationDto = new AuthorCreationDto(authors);

        model.addAttribute("authors", authorCreationDto);
        model.addAttribute("bodyContentUser", "reserve-book");
        return "master-user-template";
    }

    @Transactional
    @PostMapping("/sellBooks")
    public String saveBooks(@RequestParam String bookName, @RequestParam int yearPublished,
                            @RequestParam FacultyChoice facultyChoice, @ModelAttribute AuthorCreationDto authors,
                            @RequestParam String description, Model model, Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        List<Author> authorList = authors.getAuthors();
        List<Author> authorList1 = new ArrayList<>();

        for (Author author : authorList) {
            if (!author.getAuthorName().isEmpty())
                authorList1.add(author);
        }

        for (Author author : authorList1) {
            this.authorRepository.save(author);
        }

        Book book = new Book(bookName, yearPublished, facultyChoice, authorList1, user, description);
        this.bookRepository.save(book);

        //smeni tuka
        return "redirect:/myBooks/sellBooks";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        this.bookService.delete(id);
        return "redirect:/myBooks/myBooksForSale";
    }

    @Transactional
    @PostMapping("/sellBooks/{id}")
    public String editBooks(@PathVariable Long id, @RequestParam String bookName, @RequestParam int yearPublished,
                            @RequestParam FacultyChoice facultyChoice, @ModelAttribute AuthorCreationDto authors,
                            @RequestParam String description, Model model, Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        List<Author> authorList = authors.getAuthors();
        List<Author> authorList1 = new ArrayList<>();

        for (Author author : authorList) {
            if (!author.getAuthorName().isEmpty())
                authorList1.add(author);
        }

        for (Author author : authorList1) {
            this.authorRepository.save(author);
        }

        this.bookService.update(id, bookName, yearPublished, facultyChoice, authorList1, user, description);

        return "redirect:/myBooks/sellBooks";
    }

    @Transactional
    @PostMapping("/{id}/reservation")
    String makeReservation(@PathVariable Long id, Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        ShoppingCart cart = new ShoppingCart(user);
        this.bookService.updateReserved(id, user, cart);
        return "redirect:/myBooks/booksFair";
    }

}
