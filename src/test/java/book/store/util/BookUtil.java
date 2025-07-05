package book.store.util;

import book.store.dto.book.BookDto;
import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.book.CreateBookRequestDto;
import book.store.model.Book;
import book.store.model.Category;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookUtil {
    public static CreateBookRequestDto createBookRequestDto(List<Long> categoryIds) {
        return new CreateBookRequestDto()
                .setTitle("Sapiens")
                .setAuthor("Yuval Harari")
                .setIsbn("9780062316097")
                .setPrice(BigDecimal.valueOf(19.99))
                .setDescription("Test description")
                .setCoverImage("http://example.com/test-cover.jpg")
                .setCategoryIds(categoryIds);
    }

    public static Book createBook(Long bookId, Set<Category> categories) {
        return new Book()
                .setId(bookId)
                .setTitle("Sapiens")
                .setAuthor("Yuval Harari")
                .setIsbn("9780062316097")
                .setPrice(BigDecimal.valueOf(19.99))
                .setDescription("Test description")
                .setCoverImage("http://example.com/test-cover.jpg")
                .setCategories(categories != null ? categories : new HashSet<>());
    }

    public static BookDto createBookDto(Long id, List<Long> categoryIds) {
        return new BookDto()
                .setId(id)
                .setTitle("Sapiens")
                .setAuthor("Yuval Harari")
                .setIsbn("9780062316097")
                .setPrice(BigDecimal.valueOf(19.99))
                .setDescription("Test description")
                .setCoverImage("http://example.com/test-cover.jpg")
                .setCategoryIds(categoryIds);
    }

    public static BookDtoWithoutCategoryIds createBookDtoWithoutCategoryIds(Long id) {
        return new BookDtoWithoutCategoryIds()
                .setId(id)
                .setTitle("Sapiens")
                .setAuthor("Yuval Harari")
                .setIsbn("9780062316097")
                .setPrice(BigDecimal.valueOf(19.99))
                .setDescription("Test description")
                .setCoverImage("http://example.com/test-cover.jpg");
    }

    public static Category createCategory(Long id, String name) {
        return new Category()
                .setId(id)
                .setName(name);
    }

    public static List<BookDto> createListOfBookDtos() {
        BookDto firstDto = createBookDto(1L, List.of(1L))
                .setTitle("Sapiens")
                .setAuthor("Yuval Harari")
                .setIsbn("9780062316097")
                .setPrice(BigDecimal.valueOf(10.99));

        BookDto secondDto = createBookDto(2L, List.of(1L, 2L))
                .setTitle("Dune")
                .setAuthor("Frank Herbert")
                .setIsbn("9780441013593")
                .setPrice(BigDecimal.valueOf(15.99));

        BookDto thirdDto = createBookDto(3L, List.of(2L))
                .setTitle("The Hobbit")
                .setAuthor("Yuval Tolkien")
                .setIsbn("9780547928227")
                .setPrice(BigDecimal.valueOf(20.99));

        return List.of(firstDto, secondDto, thirdDto);
    }
}
