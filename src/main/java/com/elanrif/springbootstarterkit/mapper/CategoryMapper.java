package com.elanrif.springbootstarterkit.mapper;

import com.elanrif.springbootstarterkit.dto.CategoryDto;
import com.elanrif.springbootstarterkit.entity.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto.Response toResponse(Category category);

    CategoryDto.DetailResponse toDetailResponse(Category category);

    Category toEntity(CategoryDto.CreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(CategoryDto.UpdateRequest request, @MappingTarget Category entity);
}
