package book.store.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import book.store.model.Book;
import book.store.repository.book.BookRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find books by category id returns correct books")
    @Sql(scripts = {
            "classpath:database/delete-books-and-categories-from-db.sql",
            "classpath:database/insert-books-and-categories.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-books-and-categories-from-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void find_AllBooksByCategoryId_returnsCorrectBooks() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Book> booksPage = bookRepository.findAllByCategories_Id(2L, pageable);
        List<String> actual = booksPage.map(Book::getTitle).toList();
        List<String> expected = List.of("Second Book");
        assertEquals(expected, actual);
    }
}
