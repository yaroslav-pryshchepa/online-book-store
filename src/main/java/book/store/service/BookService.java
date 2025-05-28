package book.store.service;

import book.store.dto.BookDto;
import book.store.dto.BookSearchParametersDto;
import book.store.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParametersDto params);
}
