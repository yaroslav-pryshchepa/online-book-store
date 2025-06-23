package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.category.CategoryDto;
import book.store.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateCategoryFromDto(CategoryDto categoryDto, @MappingTarget Category category);
}
