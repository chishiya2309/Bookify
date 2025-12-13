package com.bookstore.service;

import com.bookstore.dao.PublisherDAO;
import com.bookstore.model.Publisher;

import java.util.List;

public class PublisherService {
    public List<Publisher> getAllPublishers() {
        return PublisherDAO.getAllPublishers();
    }
    public Publisher findById(Integer id) {
        return PublisherDAO.getPublisherById(id);
    }
    public void createPublisher(Publisher publisher) {
        PublisherDAO.createPublisher(publisher);
    }
    public void updatePublisher(Publisher publisher) {
        PublisherDAO.updatePublisher(publisher);
    }
    public void deletePublisher(Integer id) {
        PublisherDAO.deletePublisher(id);
    }

}
