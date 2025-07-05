package book.store.util;

import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.model.Category;
import java.util.List;

public class CategoryUtil {

    public static CreateCategoryRequestDto createCategoryRequestDto() {
        return new CreateCategoryRequestDto()
                .setName("Fiction")
                .setDescription("Books about fiction");
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
                .setDescription("Books about fiction");
    }

    public static List<CategoryDto> createListOfCategoryDtos() {
        CategoryDto firstDto = new CategoryDto()
                .setId(1L)
                .setName("Fiction")
                .setDescription("Books about fiction");

        CategoryDto secondDto = new CategoryDto()
                .setId(2L)
                .setName("Science")
                .setDescription("Scientific books");

        CategoryDto thirdDto = new CategoryDto()
                .setId(3L)
                .setName("History")
                .setDescription("Historical books");

        return List.of(firstDto, secondDto, thirdDto);
    }
}
