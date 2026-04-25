package com.knf.dev.librarymanagementsystem.controller;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.service.AuthorService;
import com.knf.dev.librarymanagementsystem.service.BookService;
import com.knf.dev.librarymanagementsystem.service.CategoryService;
import com.knf.dev.librarymanagementsystem.service.PublisherService;

@Controller
public class BookController {

    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final int DEFAULT_PAGE_SIZE = 5;

    private final BookService bookService;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final PublisherService publisherService;

    public BookController(PublisherService publisherService, CategoryService categoryService, BookService bookService,
            AuthorService authorService) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.publisherService = publisherService;
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping annotation which is less specific
     * and doesn't convey the HTTP method intent clearly.
     * WHAT WAS CHANGED: Replaced @RequestMapping with @GetMapping for better clarity and specificity.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method is now explicit in the annotation,
     * making the code self-documenting and allowing proper HTTP method matching.
     */
    @GetMapping({ "/books", "/" })
    public String findAllBooks(Model model, @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {

        int requestedPageNumber = page.orElse(DEFAULT_PAGE_NUMBER);
        int requestedPageSize = size.orElse(DEFAULT_PAGE_SIZE);

        var bookPage = bookService.findPaginated(PageRequest.of(requestedPageNumber - 1, requestedPageSize));

        model.addAttribute("books", bookPage);

        var totalPageCount = bookPage.getTotalPages();
        if (totalPageCount > 0) {
            var pageNumbers = generatePageNumbers(totalPageCount);
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "list-books";
    }

    /*
     * WHAT WAS WRONG BEFORE: Pagination page number generation logic was embedded directly in the method.
     * WHAT WAS CHANGED: Extracted into a private helper method called generatePageNumbers.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: Logic is now reusable, testable in isolation,
     * and the main method is cleaner and focused on its primary responsibility.
     */
    private java.util.List<Integer> generatePageNumbers(int totalPages) {
        return IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping annotation.
     * WHAT WAS CHANGED: Replaced with @GetMapping since this is a read operation.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method is now explicit.
     */
    @GetMapping("/searchBook")
    public String searchBook(@Param("keyword") String keyword, Model model) {

        model.addAttribute("books", bookService.searchBooks(keyword));
        model.addAttribute("keyword", keyword);
        return "list-books";
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping annotation.
     * WHAT WAS CHANGED: Replaced with @GetMapping since this is a read operation.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method is now explicit.
     */
    @GetMapping("/book/{id}")
    public String findBookById(@PathVariable("id") Long id, Model model) {

        model.addAttribute("book", bookService.findBookById(id));
        return "list-book";
    }

    @GetMapping("/add")
    public String showCreateForm(Book book, Model model) {

        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("authors", authorService.findAllAuthors());
        model.addAttribute("publishers", publisherService.findAllPublishers());
        return "add-book";
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping; unnecessary model.addAttribute before redirect.
     * WHAT WAS CHANGED: Replaced with @PostMapping; removed model.addAttribute call before redirect.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method explicit; model attributes are ignored on redirect
     * so this was dead code that could confuse developers.
     */
    @PostMapping("/add-book")
    public String createBook(Book book, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-book";
        }

        bookService.createBook(book);
        return "redirect:/books";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {

        model.addAttribute("book", bookService.findBookById(id));
        return "update-book";
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping; unnecessary model.addAttribute before redirect.
     * WHAT WAS CHANGED: Replaced with @PostMapping; removed model.addAttribute call before redirect.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method explicit; removed dead code.
     */
    @PostMapping("/update-book/{id}")
    public String updateBook(@PathVariable("id") Long id, Book book, BindingResult result, Model model) {
        if (result.hasErrors()) {
            book.setId(id);
            return "update-book";
        }

        bookService.updateBook(book);
        return "redirect:/books";
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping; unnecessary model.addAttribute before redirect.
     * WHAT WAS CHANGED: Replaced with @GetMapping (or could be @PostMapping); removed model.addAttribute call.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method explicit; removed dead code.
     */
    @GetMapping("/remove-book/{id}")
    public String deleteBook(@PathVariable("id") Long id, Model model) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }

}