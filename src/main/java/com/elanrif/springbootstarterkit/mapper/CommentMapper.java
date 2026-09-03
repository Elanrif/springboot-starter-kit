package com.elanrif.springbootstarterkit.mapper;

import com.elanrif.springbootstarterkit.dto.CommentDto;
import com.elanrif.springbootstarterkit.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

// ℹ️ MapStruct automatically maps properties when source and target property names match,
// including nested properties. ⛔ Non-matching properties are ignored.

@Mapper(componentModel = "spring")
public interface CommentMapper {

    // source (Comment) = the object MapStruct reads data from
    // target (CommentDto.Response) = the object MapStruct writes data to
    //
    // ⛔ Use @Mapping(target = "targetProperty", source = "sourceProperty")
    // when source and target property names differ.
    // Example: @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "postId", source = "post.id")
    CommentDto.Response toDto(Comment comment);

    // source (CommentDto.CreateRequest) = the object MapStruct reads data from
    // target (Comment) = the object MapStruct writes data to
    //
    // "ignore = true" means that this target property must not be mapped.
    @Mapping(target = "id", ignore = true)
    Comment toEntity(CommentDto.CreateRequest request);


    // source (CommentDto.UpdateRequest) = the object MapStruct reads data from
    // target (Comment) = the existing object MapStruct writes data to
    //
    // "ignore = true" means that this target property must not be mapped.
    void updateEntity(
            CommentDto.UpdateRequest request, // The parameter without @MappingTarget is the source.
            @MappingTarget Comment comment // @MappingTarget identifies the target to be modified.
    );
}