package com.marcosmoreiradev.uensbackend.common.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {

    private List<T> items;
    private int page;
    private int size;

    private long totalElements;
    private int totalPages;
    private int numberOfElements;

    private boolean first;
    private boolean last;

    private String sort;
}
