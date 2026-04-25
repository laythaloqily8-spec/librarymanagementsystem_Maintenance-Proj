package com.knf.dev.librarymanagementsystem.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.knf.dev.librarymanagementsystem.entity.Author;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.AuthorRepository;
import com.knf.dev.librarymanagementsystem.service.AuthorService;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Author> findAllAuthors() {
        return authorRepository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Author findAuthorById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Author not found with ID %d", id)));
    }

    @Override
    public void createAuthor(Author author) {
        authorRepository.save(author);
    }

    @Override
    public void updateAuthor(Author author) {
        authorRepository.save(author);
    }

    @Override
    public void deleteAuthor(Long id) {
        var author = authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Author not found with ID %d", id)));

        authorRepository.deleteById(author.getId());
    }

    @Override
    public Page<Author> findPaginated(Pageable pageable) {

        /*
         * WHAT WAS WRONG BEFORE: Variable names were unclear - 'currentPage' was actually the page number
         * (0-indexed), and 'startItem' was the offset into the list.
         * WHAT WAS CHANGED: Renamed to 'pageNumber' and 'offset' for clarity.
         * WHY THIS IMPROVES MAINTAINABILITY: Variable names now accurately reflect their purpose,
         * making the code easier to understand for future developers.
         */
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int offset = pageNumber * pageSize;

        /*
         * WHAT WAS WRONG BEFORE: Calling findAllAuthors() multiple times (4 times in this method),
         * causing redundant database queries and performance issues.
         * WHAT WAS CHANGED: Retrieve the list once and reuse it via 'allAuthors' variable.
         * WHY THIS IMPROVES MAINTAINABILITY: Reduces database calls, improves performance,
         * and makes the code more efficient and easier to follow.
         */
        List<Author> allAuthors = findAllAuthors();
        int totalAuthorCount = allAuthors.size();

        List<Author> authorPageContent;

        /*
         * WHAT WAS WRONG BEFORE: Using unclear variable name 'list' for the paginated content.
         * WHAT WAS CHANGED: Renamed to 'authorPageContent' for clarity.
         * WHY THIS IMPROVES MAINTAINABILITY: Name now clearly indicates the purpose of the variable.
         */
        if (totalAuthorCount < offset) {
            authorPageContent = Collections.emptyList();
        } else {
            int endIndex = Math.min(offset + pageSize, totalAuthorCount);
            authorPageContent = allAuthors.subList(offset, endIndex);
        }

        return new PageImpl<>(authorPageContent, PageRequest.of(pageNumber, pageSize), totalAuthorCount);

    }

}