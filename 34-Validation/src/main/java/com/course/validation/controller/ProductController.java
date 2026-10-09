package com.course.validation.controller;

import com.course.validation.dto.ProductRequest;
import com.course.validation.validation.ValidationGroups.OnCreate;
import com.course.validation.validation.ValidationGroups.OnUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/*
 * ProductController - VALIDATION GROUPS demo.
 * --------------------------------------------
 * @Valid (Jakarta) cannot choose a group - it always validates the Default group.
 * @Validated (Spring) CAN: @Validated(OnCreate.class) runs the OnCreate rules
 * (+ Default rules, because OnCreate extends Default).
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@Validated(OnCreate.class) @RequestBody ProductRequest request) {
        // "id" must be null here - the server would assign it.
        return Map.of("message", "Product created", "name", request.name());
    }

    @PutMapping
    public Map<String, Object> update(@Validated(OnUpdate.class) @RequestBody ProductRequest request) {
        // "id" is guaranteed to be present here.
        return Map.of("message", "Product updated", "id", request.id(), "name", request.name());
    }
}
