package com.knf.dev.librarymanagementsystem.controller;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.knf.dev.librarymanagementsystem.entity.Author;
import com.knf.dev.librarymanagementsystem.service.AuthorService;

@Controller
public class AuthorController {

    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final int DEFAULT_PAGE_SIZE = 5;

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping annotation which is less specific
     * and doesn't convey the HTTP method intent clearly.
     * WHAT WAS CHANGED: Replaced @RequestMapping with @GetMapping for better clarity and specificity.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method is now explicit in the annotation,
     * making the code self-documenting and allowing proper HTTP method matching.
     */
    @GetMapping("/authors")
    public String findAllAuthors(Model model, @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {

        int requestedPageNumber = page.orElse(DEFAULT_PAGE_NUMBER);
        int requestedPageSize = size.orElse(DEFAULT_PAGE_SIZE);
        var authorPage = authorService.findPaginated(PageRequest.of(requestedPageNumber - 1, requestedPageSize));

        model.addAttribute("authors", authorPage);

        int totalPageCount = authorPage.getTotalPages();
        if (totalPageCount > 0) {
            var pageNumbers = generatePageNumbers(totalPageCount);
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "list-authors";
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
    @GetMapping("/author/{id}")
    public String findAuthorById(@PathVariable("id") Long id, Model model) {

        model.addAttribute("author", authorService.findAuthorById(id));
        return "list-author";
    }

    @GetMapping("/addAuthor")
    public String showCreateForm(Author author) {
        return "add-author";
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping; unnecessary model.addAttribute before redirect.
     * WHAT WAS CHANGED: Replaced with @PostMapping; removed model.addAttribute call before redirect.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method explicit; model attributes are ignored on redirect
     * so this was dead code that could confuse developers.
     */
    @PostMapping("/add-author")
    public String createAuthor(Author author, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-author";
        }

        authorService.createAuthor(author);
        return "redirect:/authors";
    }

    @GetMapping("/updateAuthor/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {

        model.addAttribute("author", authorService.findAuthorById(id));
        return "update-author";
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping; unnecessary model.addAttribute before redirect.
     * WHAT WAS CHANGED: Replaced with @PostMapping; removed model.addAttribute call before redirect.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method explicit; removed dead code.
     */
    @PostMapping("/update-author/{id}")
    public String updateAuthor(@PathVariable("id") Long id, Author author, BindingResult result, Model model) {
        if (result.hasErrors()) {
            author.setId(id);
            return "update-author";
        }

        authorService.updateAuthor(author);
        return "redirect:/authors";
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping; unnecessary model.addAttribute before redirect.
     * WHAT WAS CHANGED: Replaced with @GetMapping (or could be @PostMapping); removed model.addAttribute call.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method explicit; removed dead code.
     */
    @GetMapping("/remove-author/{id}")
    public String deleteAuthor(@PathVariable("id") Long id, Model model) {
        authorService.deleteAuthor(id);
        return "redirect:/authors";
    }

}