package ru.feoktistov.springcourse.Project2Boot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feoktistov.springcourse.Project2Boot.models.Person;

import java.util.List;

public interface PeopleRepository extends JpaRepository<Person, Integer> {
}
