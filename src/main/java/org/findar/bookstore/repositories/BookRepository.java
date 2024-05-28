package org.findar.bookstore.repositories;

import org.findar.bookstore.entities.Book;
import org.findar.bookstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    public List<Book> findAllByOwner(User owner);
}
