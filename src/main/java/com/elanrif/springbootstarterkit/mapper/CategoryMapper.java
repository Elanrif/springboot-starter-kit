package com.elanrif.springbootstarterkit.mapper;

import com.elanrif.springbootstarterkit.dto.category.CategoryCreateDto;
import com.elanrif.springbootstarterkit.dto.category.CategoryDto;
import com.elanrif.springbootstarterkit.entity.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    Category toEntity(CategoryCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(CategoryCreateDto dto, @MappingTarget Category entity);
}
