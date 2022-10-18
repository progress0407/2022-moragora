package com.woowacourse.moragora.presentation;

import com.woowacourse.moragora.application.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final TestService service;

    @GetMapping("/query")
    public Integer query() {
        return service.query();
    }

    @GetMapping("/command")
    public String command() {
        service.command();
        return "success";
    }
}
