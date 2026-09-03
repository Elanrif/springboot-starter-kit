package com.elanrif.springbootstarterkit.services.purge;

import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.repository.AddressRepository;
import com.elanrif.springbootstarterkit.repository.CommentRepository;
import com.elanrif.springbootstarterkit.repository.PostRepository;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserPurgeService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public void purgeUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));

        if (user.getDeletedAt() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "User " + id + " must be soft-deleted before it can be purged");
        }

        commentRepository.deleteByAuthorId(id);
        postRepository.deleteByAuthorId(id);
        addressRepository.deleteByUserId(id);
        userRepository.hardDelete(id);
    }
}