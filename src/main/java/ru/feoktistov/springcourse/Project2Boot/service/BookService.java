package ru.feoktistov.springcourse.Project2Boot.service;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.feoktistov.springcourse.Project2Boot.models.Book;
import ru.feoktistov.springcourse.Project2Boot.models.Person;
import ru.feoktistov.springcourse.Project2Boot.repositories.BookRepository;
import ru.feoktistov.springcourse.Project2Boot.repositories.PeopleRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll(boolean sort) {
        if (sort) {
            return bookRepository.findAll(Sort.by("year").descending());
        }
        return bookRepository.findAll();
    }

    public List<Book> findAll(int page, int itemsPerPage, boolean sort) {
        if (sort) {
            return bookRepository.findAll(PageRequest.of(page, itemsPerPage, Sort.by("year").descending())).getContent();
        }

        return bookRepository.findAll(PageRequest.of(page, itemsPerPage)).getContent();
    }

    public Book findById(int id) {
        Optional<Book> foundBook = bookRepository.findById(id);
        Hibernate.initialize(foundBook.get().getOwner());

        return foundBook.orElse(null);
    }

    @Transactional
    public void save(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updateBook) {
        updateBook.setId(id);
        bookRepository.save(updateBook);
    }

    @Transactional
    public void delete(int id) {
        bookRepository.deleteById(id);
    }

    public List<Book> findByNameStartingWith(String query) {
        List<Book> books = bookRepository.findByNameStartingWith(query);

        books.forEach(book -> {
            Hibernate.initialize(book.getOwner());
        });

        return books;
    }
}
