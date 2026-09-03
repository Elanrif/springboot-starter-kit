package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.CommentDto;
import com.elanrif.springbootstarterkit.dto.PaginationDto;
import com.elanrif.springbootstarterkit.dto.shared.PageResponse;

public interface CommentService {

    PageResponse<CommentDto.Response> getComments(CommentDto.Filter filter,
                                                  PaginationDto.Pagination pagination);
    CommentDto.Response getCommentById(Long id);

    CommentDto.Response createComment(CommentDto.CreateRequest request);

    CommentDto.Response updateComment(Long id, CommentDto.UpdateRequest request);

    void deleteComment(Long id);
}
