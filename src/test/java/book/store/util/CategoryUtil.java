package book.store.util;

import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.model.Category;
import java.util.List;

public class CategoryUtil {

    public static CreateCategoryRequestDto createCategoryRequestDto() {
        return new CreateCategoryRequestDto()
                .setName("Fiction")
                .setDescription("Fiction books");
    }

    public static Category createCategory(Long id, String name, String description) {
        return new Category()
                .setId(id)
                .setName(name)
                .setDescription(description);
    }

    public static CategoryDto createCategoryDto(Long id) {
        return new CategoryDto()
                .setId(id)
                .setName("Fiction")
                .setDescription("Fiction books");
    }

    public static CategoryDto createUpdatedCategoryDto(Long id) {
        return new CategoryDto()
                .setId(id)
                .setName("Updated Fiction")
                .setDescription("Updated fiction description");
    }

    public static List<CategoryDto> createListOfCategoryDtos() {
        CategoryDto firstDto = new CategoryDto()
                .setId(1L)
                .setName("Fiction")
                .setDescription("Fiction books");

        CategoryDto secondDto = new CategoryDto()
                .setId(2L)
                .setName("Science")
                .setDescription("Books about science");

        CategoryDto thirdDto = new CategoryDto()
                .setId(3L)
                .setName("History")
                .setDescription("Historical literature");

        return List.of(firstDto, secondDto, thirdDto);
    }
}
