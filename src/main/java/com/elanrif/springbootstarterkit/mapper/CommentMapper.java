package com.elanrif.springbootstarterkit.mapper;

import com.elanrif.springbootstarterkit.dto.CommentDto;
import com.elanrif.springbootstarterkit.entity.Comment;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "author", source = "author")
    CommentDto.Summary toSummary(Comment comment);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "author", source = "author")
    CommentDto.Response toResponse(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "post", ignore = true)
    Comment toEntity(CommentDto.CreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "post", ignore = true)
    void updateFromRequest(CommentDto.UpdateRequest request, @MappingTarget Comment entity);
}
