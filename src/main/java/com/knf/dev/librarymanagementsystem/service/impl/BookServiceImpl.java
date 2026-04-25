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

import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.service.BookService;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> searchBooks(String keyword) {
        if (keyword != null) {
            return bookRepository.search(keyword);
        }
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Book not found with ID %d", id)));
    }

    @Override
    public void createBook(Book book) {
        bookRepository.save(book);
    }

    @Override
    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    @Override
    public void deleteBook(Long id) {
        var book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Book not found with ID %d", id)));

        bookRepository.deleteById(book.getId());
    }

    @Override
    public Page<Book> findPaginated(Pageable pageable) {

        /*
         * WHAT WAS WRONG BEFORE: Unnecessary timing/profiling code using System.currentTimeMillis() and
         * System.out.println() was embedded in the method - this is debug code that should
         * never be in production.
         * WHAT WAS CHANGED: Removed all timing code (startTime, endTime variables and System.out.println).
         * WHY THIS IMPROVES MAINTAINABILITY: Debug/timing code pollutes production code,
         * creates unnecessary console output, and can be a security/performance concern.
         */

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
         * WHAT WAS WRONG BEFORE: Calling findAllAuthors() multiple times in this method,
         * causing redundant database queries and performance issues.
         * WHAT WAS CHANGED: Retrieve the list once and reuse it via 'allBooks' variable.
         * WHY THIS IMPROVES MAINTAINABILITY: Reduces database calls, improves performance,
         * and makes the code more efficient and easier to follow.
         */
        List<Book> allBooks = findAllBooks();
        int totalBookCount = allBooks.size();

        List<Book> bookPageContent;

        /*
         * WHAT WAS WRONG BEFORE: Using unclear variable name 'list' for the paginated content.
         * WHAT WAS CHANGED: Renamed to 'bookPageContent' for clarity.
         * WHY THIS IMPROVES MAINTAINABILITY: Name now clearly indicates the purpose of the variable.
         */
        if (totalBookCount < offset) {
            bookPageContent = Collections.emptyList();
        } else {
            int endIndex = Math.min(offset + pageSize, totalBookCount);
            bookPageContent = allBooks.subList(offset, endIndex);
        }

        return new PageImpl<>(bookPageContent, PageRequest.of(pageNumber, pageSize), totalBookCount);

    }

}