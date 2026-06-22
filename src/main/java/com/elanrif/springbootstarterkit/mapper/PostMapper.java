package com.elanrif.springbootstarterkit.mapper;

import com.elanrif.springbootstarterkit.dto.PostDto;
import com.elanrif.springbootstarterkit.entity.Post;
import org.mapstruct.*;

import java.util.List;

// uses = {UserMapper.class} : permet à MapStruct de déléguer le mapping des types complexes
// à un autre mapper existant (réutilisation des règles de mapping et gestion des objets imbriqués).
@Mapper(componentModel = "spring", uses = {UserMapper.class, CommentMapper.class})
public interface PostMapper {

    @Mapping(target = "author", source = "author")
    @Mapping(target = "commentCount", expression = "java(post.getComments() != null ? post.getComments().size() : 0)")
    PostDto.Summary toSummary(Post post);

    @Mapping(target = "author", source = "author")
    @Mapping(target = "commentCount", expression = "java(post.getComments() != null ? post.getComments().size() : 0)")
    PostDto.Response toResponse(Post post);

    @Mapping(target = "author", source = "author")
    @Mapping(target = "comments", source = "comments")
    PostDto.DetailResponse toDetailResponse(Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Post toEntity(PostDto.CreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "comments", ignore = true)
    void updateFromRequest(PostDto.UpdateRequest request, @MappingTarget Post entity);
}
