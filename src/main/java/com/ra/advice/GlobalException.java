package com.ra.advice;

import com.ra.exception.CustomException;
import com.ra.exception.ForbiddenException;
import com.ra.model.dto.response.DataResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalException {
    // 1  lối không tìm thấy dữ liệu
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<DataResponse<Object>> handleNoSuchElementException(NoSuchElementException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(DataResponse.builder()
                        .status(404)
                        .message("Không tìm thấy dữ liệu" + ex.getMessage())
                        .data(null)
                        .build());
    }
    // 2  Lỗi tùy chỉnh
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<DataResponse<Object>> handleCustomException(CustomException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DataResponse.builder()
                        .status(400)
                        .message(ex.getMessage())
                        .data(null)
                        .build());
    }

    //3 lỗi bị cấm truy cập
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<DataResponse<Object>> handleForbiddenException(ForbiddenException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(DataResponse.builder()
                        .status(403)
                        .message(ex.getMessage())
                        .data(null)
                        .build());

    }
    // 4 lỗi máy chủ
    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public ResponseEntity<DataResponse<Object>> handleHttpServerErrorException(HttpServerErrorException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(DataResponse.builder()
                        .status(500)
                        .message("Lỗi máy chủ: " + ex.getMessage())
                        .data(null)
                        .build());
    }

    //5 loi validator du lieuj dau vaof
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponse<List<String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity.badRequest()
                .body(DataResponse.<List<String>>builder()
                        .status(400)
                        .message("Dữ liệu không hợp lệ")
                        .data(errors)
                        .build());
    }


}
