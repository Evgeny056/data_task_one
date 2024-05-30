package com.springdatajdbc.repository;

import com.springdatajdbc.entity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Book> bookRowMapper = (rs, rowNum) -> {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPublicationYear(rs.getInt("publication_year"));
        return book;
    };

    public Book save(Book book) {
        if (book.getId() == null) {
            jdbcTemplate.update(
                    "INSERT INTO book (title, author, publication_year) VALUES (?, ?, ?)",
                    book.getTitle(), book.getAuthor(), book.getPublicationYear()
            );
            Long id = jdbcTemplate.queryForObject("SELECT lastval()", Long.class);
            book.setId(id);
        } else {
            jdbcTemplate.update(
                    "UPDATE book SET title = ?, author = ?, publication_year = ? WHERE id = ?",
                    book.getTitle(), book.getAuthor(), book.getPublicationYear(), book.getId()
            );
        }
        return book;
    }

    public Optional<Book> findById(Long id) {
        List<Book> books = jdbcTemplate.query(
                "SELECT * FROM book WHERE id = ?",
                new Object[]{id},
                bookRowMapper
        );
        if (books.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(books.get(0));
    }

    public List<Book> findAll() {
        return jdbcTemplate.query("SELECT * FROM book", bookRowMapper);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM book WHERE id = ?", id);
    }
}
