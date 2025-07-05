package book.store.service;

import static book.store.util.BookUtil.createBook;
import static book.store.util.BookUtil.createBookDto;
import static book.store.util.BookUtil.createBookRequestDto;
import static book.store.util.BookUtil.createCategory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import book.store.dto.book.BookDto;
import book.store.dto.book.CreateBookRequestDto;
import book.store.mapper.BookMapper;
import book.store.model.Book;
import book.store.model.Category;
import book.store.repository.book.BookRepository;
import book.store.repository.book.BookSpecificationBuilder;
import book.store.repository.category.CategoryRepository;
import book.store.service.impl.BookServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("save should save and return BookDto")
    void save_returnsSavedBookDto() {
        CreateBookRequestDto requestDto = createBookRequestDto(List.of(1L));
        Book book = createBook(1L, Set.of());
        BookDto bookDto = createBookDto(1L, List.of(1L));
        List<Category> categories = List.of(createCategory(1L, "Test"));
        when(bookMapper.toEntity(requestDto)).thenReturn(book);
        when(categoryRepository.findAllById(requestDto.getCategoryIds())).thenReturn(categories);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        BookDto result = bookService.save(requestDto);
        assertNotNull(result);
        assertEquals(bookDto, result);
        verify(bookMapper).toEntity(requestDto);
        verify(categoryRepository).findAllById(requestDto.getCategoryIds());
        verify(bookRepository).save(book);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("findAll should return page of BookDto")
    void findAll_returnsPageOfBookDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Book book = createBook(1L, Set.of());
        BookDto bookDto = createBookDto(1L, List.of(1L));
        Page<Book> bookPage = new PageImpl<>(List.of(book));
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        Page<BookDto> result = bookService.findAll(pageable);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(bookDto, result.getContent().get(0));
        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("findById should return BookDto when book exists")
    void findById_existingBook_returnsBookDto() {
        Long id = 1L;
        Book book = createBook(id, Set.of());
        BookDto bookDto = createBookDto(id, List.of(1L));
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        BookDto result = bookService.findById(id);
        assertNotNull(result);
        assertEquals(bookDto, result);
        verify(bookRepository).findById(id);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("findById should throw if book not found")
    void findById_bookNotFound_throwsException() {
        Long id = 1L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(id));
        assertEquals("Can't find book by id: " + id, ex.getMessage());
        verify(bookRepository).findById(id);
        verifyNoMoreInteractions(bookMapper);
    }

    @Test
    @DisplayName("update should update and return BookDto")
    void update_existingBook_returnsUpdatedBookDto() {
        Long id = 1L;
        CreateBookRequestDto requestDto = createBookRequestDto(List.of(1L));
        Book book = createBook(id, Set.of());
        BookDto updatedDto = createBookDto(id, List.of(1L));
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        doNothing().when(bookMapper).updateBookFromDto(requestDto, book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(updatedDto);
        BookDto result = bookService.update(id, requestDto);
        assertNotNull(result);
        assertEquals(updatedDto, result);
        verify(bookRepository).findById(id);
        verify(bookMapper).updateBookFromDto(requestDto, book);
        verify(bookRepository).save(book);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("update should throw if book not found")
    void update_bookNotFound_throwsException() {
        Long id = 1L;
        CreateBookRequestDto requestDto = createBookRequestDto(List.of(1L));
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> bookService.update(id, requestDto));
        assertEquals("Can't find book by id: " + id, ex.getMessage());
        verify(bookRepository).findById(id);
        verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    @DisplayName("deleteById should call repository")
    void deleteById_callsRepository() {
        Long id = 1L;
        doNothing().when(bookRepository).deleteById(id);
        bookService.deleteById(id);
        verify(bookRepository).deleteById(id);
    }
}
