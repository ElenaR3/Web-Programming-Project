package com.example.project_web.web.controllers;

import com.example.project_web.model.*;
import com.example.project_web.model.dtos.AuthorCreationDto;
import com.example.project_web.model.enumerations.FacultyChoice;
import com.example.project_web.model.exceptions.InvalidBookIdException;
import com.example.project_web.repository.AuthorRepository;
import com.example.project_web.repository.BookRepository;
import com.example.project_web.repository.EventRepository;
import com.example.project_web.repository.ReviewRepository;
import com.example.project_web.service.BookService;
import com.example.project_web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.model.IModel;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("myBooks")
public class UserPersonalBooksController {

    @Autowired
    OAuth2AuthorizedClientService authclientService;

    FacultyChoice facultyChoice;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final UserService userService;
    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;

    public UserPersonalBooksController(BookService bookService, BookRepository bookRepository, AuthorRepository authorRepository, UserService userService, ReviewRepository reviewRepository, EventRepository eventRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.userService = userService;
        this.reviewRepository = reviewRepository;
        this.eventRepository = eventRepository;
    }


    @GetMapping("/homepage")
    public String getHomepage(Model model){
        model.addAttribute("bodyContentUser", "buy_books");
        return "master-user-template";
    }

    @GetMapping("/events")
    public String events(Model model) {
        List<CalendarEvent> events = (List<CalendarEvent>) this.eventRepository.findAll();
        model.addAttribute("events", events);
        model.addAttribute("bodyContentUser", "calendar");
        return "master-user-template";

    }

    @GetMapping("/myProfile")
    public String getProfile(Model model, Authentication authentication ){
        String username = null;

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            DefaultOidcUser usr = (DefaultOidcUser) authentication.getPrincipal();
            username = usr.getEmail();
        }

        User user = (User) this.userService.loadUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("bodyContentUser", "my-profile");
        return "master-user-template";
    }

    @GetMapping("/userProfile/{user}")
    public String getOtherProfile(@PathVariable String user, Model model) {
        User userProfile = null;
        Optional<User> findUserProfile = this.userService.findByUsername(user);
        if(findUserProfile.isPresent()) {
          userProfile = findUserProfile.get();
        }

        model.addAttribute("user", userProfile);
        model.addAttribute("bodyContentUser", "my-profile");
        return "master-user-template";
    }

    @PostMapping("/reviews")
    public String getReviews(@RequestParam(required = false) String file, @RequestParam String username, Model model, Authentication authentication, HttpSession httpSession){
        String reviewer = null;

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            reviewer = ((UserDetails)principal).getUsername();
        } else {
            DefaultOidcUser usr = (DefaultOidcUser) authentication.getPrincipal();
            reviewer = usr.getEmail();
        }

        User userReviewer = (User) this.userService.loadUserByUsername(reviewer);    // user-ot sto ocenuva e ova

        User userReviewed = (User) this.userService.loadUserByUsername(username);

        List<Review> reviews = this.reviewRepository.findAllByReviewed(userReviewed);

        model.addAttribute("reviews", reviews);
        model.addAttribute("userReviewed", userReviewed);
        httpSession.setAttribute("userReviewed", userReviewed);
        model.addAttribute("bodyContentUser", "reviews-form.html");
        return "master-user-template";
    }

    @PostMapping("/reviews/addComment")
    public String addComment(@RequestParam String comment, Model model, Authentication authentication, HttpSession httpSession ){
        String reviewer = null;

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            reviewer = ((UserDetails)principal).getUsername();
        } else {
            DefaultOidcUser usr = (DefaultOidcUser) authentication.getPrincipal();
            reviewer = usr.getEmail();
        }

        User userReviewer = (User) this.userService.loadUserByUsername(reviewer);    // user-ot sto ocenuva e ova

        User userReviewed = (User) httpSession.getAttribute("userReviewed");

        Review review = new Review(comment, userReviewer, userReviewed);

        this.reviewRepository.save(review);

        List<Review> reviews = this.reviewRepository.findAll();

        model.addAttribute("reviews", reviews);
        model.addAttribute("userReviewed", userReviewed);
        model.addAttribute("bodyContentUser", "reviews-form.html");
      //  return "master-user-template";
        return "redirect:/myBooks/sellBooks";
    }

    @GetMapping("/sellBooksAuth")
    public String sellBooksAuth(Model model, @AuthenticationPrincipal OAuth2AuthenticationToken authenticationToken){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        DefaultOidcUser usr = (DefaultOidcUser) authentication.getPrincipal();
        String email = usr.getEmail();
        String name = usr.getGivenName();
        String surname = usr.getFamilyName();

        Optional<User> user = this.userService.findByUsername(email);

        if(user.isEmpty()) {
            String pass = this.userService.randomPassGenerator();
            this.userService.create(name, surname, email, pass, Role.ROLE_USER);
        }

        String accessToken = null;
      if (authentication.getClass().isAssignableFrom(OAuth2AuthenticationToken.class)) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
            if (clientRegistrationId.equals("google")) {
                OAuth2AuthorizedClient client =
                        authclientService.loadAuthorizedClient(clientRegistrationId, oauthToken.getName());
                accessToken = client.getAccessToken().getTokenValue();

            }
        }

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
        String username = null;

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
           username = ((UserDetails)principal).getUsername();
        } else {
            DefaultOidcUser usr = (DefaultOidcUser) authentication.getPrincipal();
            username = usr.getEmail();
        }

        User user = (User) this.userService.loadUserByUsername(username);
       // User user = (User) authentication.getPrincipal();
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
    public String showBooks(Model model,@RequestParam(required = false) String search, @RequestParam("page") Optional<Integer> page,
                            @RequestParam("size") Optional<Integer> size) {
        List<Book> books = new ArrayList<>();
        if(search==null) {
            books = bookService.listAll();
        } else {
            books = bookService.findAllByAuthorOrName(search);
        }
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

    @PostMapping("/booksFair")
    public String showBooksFiltered(Model model,@RequestParam(required = false) String search, @RequestParam("page") Optional<Integer> page,
                            @RequestParam("size") Optional<Integer> size) {
        List<Book> books = new ArrayList<>();
        List<Book> allBooks = bookService.listAll();

        if(search!=null) {
            books = bookService.findAllByAuthorOrName(search);
        }
        if(books.isEmpty()) {
            books = allBooks;
        }

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

        String username = null;

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            DefaultOidcUser usr = (DefaultOidcUser) authentication.getPrincipal();
            username = usr.getEmail();
        }

        User user = (User) this.userService.loadUserByUsername(username);

      //  User user = (User) authentication.getPrincipal();
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

        String username = null;

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            DefaultOidcUser usr = (DefaultOidcUser) authentication.getPrincipal();
            username = usr.getEmail();
        }

        User user = (User) this.userService.loadUserByUsername(username);

    //    User user = (User) authentication.getPrincipal();
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

        String username = null;

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            DefaultOidcUser usr = (DefaultOidcUser) authentication.getPrincipal();
            username = usr.getEmail();
        }

        User user = (User) this.userService.loadUserByUsername(username);

     //   User user = (User) authentication.getPrincipal();
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

        String username = null;

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            DefaultOidcUser usr = (DefaultOidcUser) authentication.getPrincipal();
            username = usr.getEmail();
        }

        User user = (User) this.userService.loadUserByUsername(username);

       // User user = (User) authentication.getPrincipal();
        ShoppingCart cart = new ShoppingCart(user);
        this.bookService.updateReserved(id, user, cart);
        return "redirect:/myBooks/booksFair";
    }

}
