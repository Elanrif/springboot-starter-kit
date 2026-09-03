package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.PaginationDto;
import com.elanrif.springbootstarterkit.dto.PostDto;
import com.elanrif.springbootstarterkit.dto.shared.PageResponse;

public interface PostService {


    PageResponse<PostDto.Response> getPosts(PostDto.Filter filter,
                                            PaginationDto.Pagination pagination);
    PostDto.Response getPostById(Long id);

    PostDto.Response createPost(PostDto.CreateRequest request);

    PostDto.Response updatePost(Long id, PostDto.UpdateRequest request);

    void deletePost(Long id);
}
