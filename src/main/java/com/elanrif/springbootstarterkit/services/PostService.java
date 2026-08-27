package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.CommentDto;
import com.elanrif.springbootstarterkit.dto.CommonDto;
import com.elanrif.springbootstarterkit.dto.PostDto;
import com.elanrif.springbootstarterkit.util.PageResponse;

public interface PostService {

    PageResponse<PostDto.Response> getPosts(PostDto.Filter filter, CommonDto.Pagination pageRequest);

    PostDto.CommentsResponse getComments(
            Long postId,
            CommentDto.Filter filter,
            CommonDto.Pagination pagination
    );

    PostDto.Response getPostById(Long id);

    PostDto.Response createPost(PostDto.CreateRequest request);

    PostDto.Response updatePost(Long id, PostDto.UpdateRequest request);

    void deletePost(Long id);
}
