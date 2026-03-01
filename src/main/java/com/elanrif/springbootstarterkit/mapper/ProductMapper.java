package com.elanrif.springbootstarterkit.mapper;

import com.elanrif.springbootstarterkit.dto.product.ProductCreateDto;
import com.elanrif.springbootstarterkit.dto.product.ProductDto;
import com.elanrif.springbootstarterkit.dto.product.ProductUpdateDto;
import com.elanrif.springbootstarterkit.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * Use @BeanMapping(ignoreByDefault = true) to explicitly map only the fields listed below.
     * This prevents MapStruct from automatically mapping all properties, which can lead to:
     *  - "unmapped target properties" errors if some fields are not mapped
     *  - unwanted or unsafe automatic mapping when DTO/entity structures diverge
     *  - accidental exposure of fields you do not want in the DTO
     *
     * By being explicit, you ensure only the intended fields are mapped, making the mapping safer and easier to maintain.
     */
    ProductDto toDto(Product product);

    // The handling of the category association should be done in the service (e.g., via categoryId)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    void updateFromDto(ProductUpdateDto dto, @MappingTarget Product entity);
}
