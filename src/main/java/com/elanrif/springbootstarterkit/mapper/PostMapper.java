package com.elanrif.springbootstarterkit.mapper;

import com.elanrif.springbootstarterkit.dto.PostDto;
import com.elanrif.springbootstarterkit.entity.Post;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

// ℹ️ MapStruct automatically maps properties when source and target property names match,
// including nested properties. ⛔ Non-matching properties are ignored.

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class}
)
public interface PostMapper {

    // source (Post) = the object MapStruct reads data from
    // target (PostDto.Response) = the object MapStruct writes data to
    //
    // ⛔ Use @Mapping(target = "targetProperty", source = "sourceProperty")
    // when source and target property names differ.
    // Example: @Mapping(target = "author", source = "user")
    @Mapping(
            target = "numberOfComments",
            expression = "java(post.getComments() != null ? post.getComments().size() : 0)"
    )
    PostDto.Response toDto(Post post);

    // source (PostDto.CreateRequest) = the object MapStruct reads data from
    // target (Post) = the object MapStruct writes data to
    //
    // "ignore = true" means that this target property must not be mapped.
    @Mapping(target = "id", ignore = true)
    Post toEntity(PostDto.CreateRequest request);

    // source (PostDto.UpdateRequest) = the object MapStruct reads data from
    // target (Post) = the object MapStruct writes data to
    //
    // Null values are ignored, so fields not provided in the PATCH request
    // keep their existing values.
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    void updateEntity(
            PostDto.UpdateRequest request, // The parameter without @MappingTarget is the source.
            @MappingTarget Post existingPost // @MappingTarget identifies the target to be modified.
    );
}
