package com.example.backend.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class CommonApiResponse<T> {

    private String result;

    private T data;

    public static<T> CommonApiResponse<T> success(T data) {
        return new CommonApiResponse<>("success", data);

    }
}
