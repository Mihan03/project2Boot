package ru.feoktistov.springcourse.Project2Boot.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.feoktistov.springcourse.Project2Boot.models.Book;
import ru.feoktistov.springcourse.Project2Boot.models.Person;
import ru.feoktistov.springcourse.Project2Boot.service.BookService;
import ru.feoktistov.springcourse.Project2Boot.service.PeopleService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final PeopleService peopleService;

    @Autowired
    public BookController(BookService bookService, PeopleService peopleService) {
        this.bookService = bookService;
        this.peopleService = peopleService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String query, Model model) {
        List<Book> books = new ArrayList<>();

        if (query != null) {
            books = bookService.findByNameStartingWith(query);
        }

        for (Book book : books) {
            book.setOwner(book.getOwner());
        }

        model.addAttribute("books", books);
        model.addAttribute("query", query);

        System.out.println(books);

        return "books/search";
    }

    @GetMapping()
    public String index(@RequestParam(required = false) Integer page,
                        @RequestParam(name = "books_per_page", required = false) Integer itemsPerPage,
                        @RequestParam(name = "sort_by_year", required = false) String sortByYear,
                        Model model) {

        if (page == null || itemsPerPage == null) {
            if (sortByYear != null && sortByYear.equals("true")) {
                model.addAttribute("books", bookService.findAll(true));
            } else {
                model.addAttribute("books", bookService.findAll(false));
            }
        } else {
            if (sortByYear != null && sortByYear.equals("true")) {
                model.addAttribute("books", bookService.findAll(page, itemsPerPage, true));
            } else {
                model.addAttribute("books", bookService.findAll(page, itemsPerPage, false));
            }
        }

        return "books/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
        Book book = bookService.findById(id);
        Person owner = book.getOwner();

        List<Person> people = null;

        if (owner == null) {
            people = peopleService.findAll();
        }

        model.addAttribute("book", book);
        model.addAttribute("owner", owner);
        model.addAttribute("people", people);

        return "books/show";
    }

    @GetMapping("/new")
    public String newPerson(Model model) {
        model.addAttribute("book", new Book());
        return "books/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "books/new";
        }

        bookService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String editPerson(Model model, @PathVariable("id") int id) {
        model.addAttribute("book", bookService.findById(id));

        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult,
                         @PathVariable("id") int id) {

        if (bindingResult.hasErrors()) {
            return "books/edit";
        }

        bookService.update(id, book);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/set")
    public String set(@ModelAttribute("person") Person person,
                         @PathVariable("id") int id) {

        Book book = bookService.findById(id);

        book.setOwner(person);

        book.setRentalTime(new Date());

        bookService.update(id, book);

        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/unset")
    public String unset(@PathVariable("id") int id) {

        Book book = bookService.findById(id);
        book.setOwner(null);
        book.setRentalTime(null);
        bookService.update(id, book);

        return "redirect:/books/" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        bookService.delete(id);

        return "redirect:/books";
    }
}
