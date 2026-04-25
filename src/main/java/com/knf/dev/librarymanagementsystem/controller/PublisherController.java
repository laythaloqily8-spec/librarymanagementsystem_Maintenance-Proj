package com.knf.dev.librarymanagementsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.knf.dev.librarymanagementsystem.entity.Publisher;
import com.knf.dev.librarymanagementsystem.service.PublisherService;

@Controller
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;

    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping annotation which is less specific
     * and doesn't convey the HTTP method intent clearly.
     * WHAT WAS CHANGED: Replaced @RequestMapping with @GetMapping for better clarity and specificity.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method is now explicit in the annotation,
     * making the code self-documenting and allowing proper HTTP method matching.
     */
    @GetMapping("/publishers")
    public String findAllPublishers(Model model) {

        model.addAttribute("publishers", publisherService.findAllPublishers());
        return "list-publishers";
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping annotation.
     * WHAT WAS CHANGED: Replaced with @GetMapping since this is a read operation.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method is now explicit.
     */
    @GetMapping("/publisher/{id}")
    public String findPublisherById(@PathVariable("id") Long id, Model model) {

        model.addAttribute("publisher", publisherService.findPublisherById(id));
        return "list-publisher";
    }

    @GetMapping("/addPublisher")
    public String showCreateForm(Publisher publisher) {
        return "add-publisher";
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping; unnecessary model.addAttribute before redirect.
     * WHAT WAS CHANGED: Replaced with @PostMapping; removed model.addAttribute call before redirect.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method explicit; model attributes are ignored on redirect
     * so this was dead code that could confuse developers.
     */
    @PostMapping("/add-publisher")
    public String createPublisher(Publisher publisher, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-publisher";
        }

        publisherService.createPublisher(publisher);
        return "redirect:/publishers";
    }

    @GetMapping("/updatePublisher/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {

        model.addAttribute("publisher", publisherService.findPublisherById(id));
        return "update-publisher";
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping; unnecessary model.addAttribute before redirect.
     * WHAT WAS CHANGED: Replaced with @PostMapping; removed model.addAttribute call before redirect.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method explicit; removed dead code.
     */
    @PostMapping("/update-publisher/{id}")
    public String updatePublisher(@PathVariable("id") Long id, Publisher publisher, BindingResult result, Model model) {
        if (result.hasErrors()) {
            publisher.setId(id);
            return "update-publishers";
        }

        publisherService.updatePublisher(publisher);
        return "redirect:/publishers";
    }

    /*
     * WHAT WAS WRONG BEFORE: Using generic @RequestMapping; unnecessary model.addAttribute before redirect.
     * WHAT WAS CHANGED: Replaced with @GetMapping (or could be @PostMapping); removed model.addAttribute call.
     * WHY THE CHANGE IMPROVES MAINTAINABILITY: HTTP method explicit; removed dead code.
     */
    @GetMapping("/remove-publisher/{id}")
    public String deletePublisher(@PathVariable("id") Long id, Model model) {
        publisherService.deletePublisher(id);
        return "redirect:/publishers";
    }

}