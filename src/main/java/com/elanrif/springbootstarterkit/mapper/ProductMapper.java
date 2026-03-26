package com.elanrif.springbootstarterkit.mapper;

import com.elanrif.springbootstarterkit.dto.ProductDto;
import com.elanrif.springbootstarterkit.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {

    ProductDto.Response toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductDto.CreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    void updateFromRequest(ProductDto.UpdateRequest request, @MappingTarget Product entity);
}
