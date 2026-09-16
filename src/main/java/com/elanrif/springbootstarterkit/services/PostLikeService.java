package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.PostDto;

public interface PostLikeService {
    PostDto.LikeResponse toggleLike(Long postId);
}
