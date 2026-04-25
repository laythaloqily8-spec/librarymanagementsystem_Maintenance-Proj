package com.knf.dev.librarymanagementsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.knf.dev.librarymanagementsystem.entity.Category;
import com.knf.dev.librarymanagementsystem.service.CategoryService;

@Controller
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;

    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping annotation which is less specific
     * and doesn't convey the HTTP method intent clearly.
     * WHAT WAS CHANGED: Replaced @RequestMapping with @GetMapping for better clarity and specificity.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method is now explicit in the annotation,
     * making the code self-documenting and allowing proper HTTP method matching.
     */
    @GetMapping("/categories")
    public String findAllCategories(Model model) {

        model.addAttribute("categories", categoryService.findAllCategories());
        return "list-categories";
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping annotation.
     * WHAT WAS CHANGED: Replaced with @GetMapping since this is a read operation.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method is now explicit.
     */
    @GetMapping("/category/{id}")
    public String findCategoryById(@PathVariable("id") Long id, Model model) {

        model.addAttribute("category", categoryService.findCategoryById(id));
        return "list-category";
    }

    @GetMapping("/addCategory")
    public String showCreateForm(Category category) {
        return "add-category";
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping; unnecessary model.addAttribute before redirect.
     * WHAT WAS CHANGED: Replaced with @PostMapping; removed model.addAttribute call before redirect.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method explicit; model attributes are ignored on redirect
     * so this was dead code that could confuse developers.
     */
    @PostMapping("/add-category")
    public String createCategory(Category category, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-category";
        }

        categoryService.createCategory(category);
        return "redirect:/categories";
    }

    @GetMapping("/updateCategory/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {

        model.addAttribute("category", categoryService.findCategoryById(id));
        return "update-category";
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping; unnecessary model.addAttribute before redirect.
     * WHAT WAS CHANGED: Replaced with @PostMapping; removed model.addAttribute call before redirect.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method explicit; removed dead code.
     */
    @PostMapping("/update-category/{id}")
    public String updateCategory(@PathVariable("id") Long id, Category category, BindingResult result, Model model) {
        if (result.hasErrors()) {
            category.setId(id);
            return "update-category";
        }

        categoryService.updateCategory(category);
        return "redirect:/categories";
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping; unnecessary model.addAttribute before redirect.
     * WHAT WAS CHANGED: Replaced with @GetMapping (or could be @PostMapping); removed model.addAttribute call.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method explicit; removed dead code.
     */
    @GetMapping("/remove-category/{id}")
    public String deleteCategory(@PathVariable("id") Long id, Model model) {
        categoryService.deleteCategory(id);
        return "redirect:/categories";
    }

}