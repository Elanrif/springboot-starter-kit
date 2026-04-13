package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.PostDto;
import com.elanrif.springbootstarterkit.util.PageResponse;

public interface PostService {

    PageResponse<PostDto.Response> getPosts(PostDto.Filter filter, int page, int size);

    PostDto.DetailResponse getPostById(Long id);

    PostDto.Response createPost(PostDto.CreateRequest request);

    PostDto.Response updatePost(Long id, PostDto.UpdateRequest request);

    void deletePost(Long id);
}
