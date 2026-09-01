package com.elanrif.springbootstarterkit.util;

import org.springframework.data.domain.Page;
import java.util.List;

public record PageResponse<T>(
        List<T> data,
        Meta meta  // ← ceci est le TYPE du paramètre
) {

    public record Meta(  // ← ceci définit ce TYPE
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}

    public static <T> PageResponse<T> from(Page<T> page) {
        // create a new PageResponse object
        return new PageResponse<>(
                page.getContent(),
                new Meta( // create a new Meta object
                        page.getNumber() + 1, // 👈 ICI : On ajoute 1 pour commencer à 1 au lieu de 0
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages()
                )
        );
    }
}

