package com.woowacourse.moragora.controller;

import com.woowacourse.moragora.entity.user.User;
import com.woowacourse.moragora.service.TestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/clear-data")
    public void clearData() {
        testService.clearData();
    }

    @GetMapping("/init-data")
    public void initData() {
        testService.initData();
    }

    @GetMapping("/get-data")
    public List<User> getData() {
        return testService.getData();
    }
}
