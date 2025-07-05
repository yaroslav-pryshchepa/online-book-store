package book.store.service;

import static book.store.util.BookUtil.createBook;
import static book.store.util.BookUtil.createBookDtoWithoutCategoryIds;
import static book.store.util.CategoryUtil.createCategory;
import static book.store.util.CategoryUtil.createCategoryDto;
import static book.store.util.CategoryUtil.createCategoryRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.mapper.BookMapper;
import book.store.mapper.CategoryMapper;
import book.store.model.Book;
import book.store.model.Category;
import book.store.repository.book.BookRepository;
import book.store.repository.category.CategoryRepository;
import book.store.service.impl.CategoryServiceImpl;
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
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private BookMapper bookMapper;

    @Test
    @DisplayName("Get category by id with existing id returns CategoryDto")
    void getById_withValidId_returnsCategoryDto() {
        Long categoryId = 1L;
        Category category = createCategory(categoryId, "Fiction", "Fiction books");
        CategoryDto expected = createCategoryDto(categoryId);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);
        CategoryDto actual = categoryService.getById(categoryId);
        assertEquals(expected, actual);
        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("Get category by id with invalid id throws EntityNotFoundException")
    void getById_withInvalidId_throwsException() {
        Long categoryId = 999L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> categoryService.getById(categoryId));
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    @DisplayName("Save valid category returns CategoryDto")
    void save_validRequest_returnsCategoryDto() {
        CreateCategoryRequestDto requestDto = createCategoryRequestDto();
        Category category = createCategory(1L, requestDto.getName(),
                requestDto.getDescription());
        CategoryDto expected = createCategoryDto(1L);
        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);
        CategoryDto actual = categoryService.save(requestDto);
        assertEquals(expected, actual);
        verify(categoryMapper).toEntity(requestDto);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("Update valid category returns CategoryDto")
    void update_withValidId_returnsUpdatedCategoryDto() {
        Long categoryId = 1L;
        CategoryDto updateDto = createCategoryDto(categoryId);
        updateDto.setName("Updated Fiction");
        updateDto.setDescription("Updated description");
        Category category = createCategory(categoryId, "Fiction", "Fiction books");
        CategoryDto expected = updateDto;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        doNothing().when(categoryMapper).updateCategoryFromDto(updateDto, category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);
        CategoryDto actual = categoryService.update(categoryId, updateDto);
        assertEquals(expected, actual);
        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper).updateCategoryFromDto(updateDto, category);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("Delete category by id")
    void deleteById_withValidId_success() {
        Long categoryId = 1L;
        doNothing().when(categoryRepository).deleteById(categoryId);
        categoryService.deleteById(categoryId);
        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    @DisplayName("Find all categories returns Page<CategoryDto>")
    void findAll_returnsPageOfCategoryDtos_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Category category = createCategory(1L, "Fiction", "Fiction books");
        CategoryDto categoryDto = createCategoryDto(1L);
        Page<Category> pageOfCategories = new PageImpl<>(List.of(category), pageable, 1);
        when(categoryRepository.findAll(pageable)).thenReturn(pageOfCategories);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);
        Page<CategoryDto> actual = categoryService.findAll(pageable);
        assertNotNull(actual);
        assertEquals(1, actual.getContent().size());
        assertEquals(categoryDto, actual.getContent().get(0));
        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("findAllByCategoryId returns page of BookDtoWithoutCategoryIds")
    void findAllByCategoryId_returnsPageOfBookDtoWithoutCategoryIds() {
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Book book = createBook(1L, Set.of());
        BookDtoWithoutCategoryIds dto = createBookDtoWithoutCategoryIds(1L);
        Page<Book> bookPage = new PageImpl<>(List.of(book));
        when(bookRepository.findAllByCategories_Id(categoryId, pageable)).thenReturn(bookPage);
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(dto);
        Page<BookDtoWithoutCategoryIds> result = categoryService.findAllByCategoryId(categoryId,
                pageable);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(dto, result.getContent().get(0));
        verify(bookRepository, times(1)).findAllByCategories_Id(categoryId, pageable);
        verify(bookMapper, times(1)).toDtoWithoutCategories(book);
    }

}
