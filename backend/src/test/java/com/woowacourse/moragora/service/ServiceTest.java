package com.woowacourse.moragora.service;

import com.woowacourse.utils.InitialTestDataConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Import(InitialTestDataConfig.class)
public abstract class ServiceTest {

}
