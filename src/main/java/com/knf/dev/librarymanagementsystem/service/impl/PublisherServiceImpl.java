package com.knf.dev.librarymanagementsystem.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.knf.dev.librarymanagementsystem.entity.Publisher;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.PublisherRepository;
import com.knf.dev.librarymanagementsystem.service.PublisherService;

@Service
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherServiceImpl(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Publisher> findAllPublishers() {
        return publisherRepository.findAll();
    }

    /*
     * WHAT WAS WRONG BEFORE: Missing @Transactional annotation on findPublisherById method,
     * while similar methods in other service implementations had it.
     * WHAT WAS CHANGED: Added @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
     * to match the consistency of other service implementations.
     * WHY THIS IMPROVES MAINTAINABILITY: Ensures consistent transaction handling
     * across all service implementations, making the codebase more predictable
     * and easier to understand.
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Publisher findPublisherById(Long id) {
        return publisherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Publisher not found with ID %d", id)));
    }

    @Override
    public void createPublisher(Publisher publisher) {
        publisherRepository.save(publisher);
    }

    @Override
    public void updatePublisher(Publisher publisher) {
        publisherRepository.save(publisher);
    }

    @Override
    public void deletePublisher(Long id) {
        var publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Publisher not found with ID %d", id)));

        publisherRepository.deleteById(publisher.getId());
    }

}