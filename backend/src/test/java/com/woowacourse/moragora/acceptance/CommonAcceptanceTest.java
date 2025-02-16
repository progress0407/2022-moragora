package com.woowacourse.moragora.acceptance;

import static org.mockito.BDDMockito.given;

import io.restassured.response.ValidatableResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Disabled
class CommonAcceptanceTest extends AcceptanceTest {

    @DisplayName("서버 시간을 조회하면 서버의 현재 시간과 상태코드 200을 반환한다.")
    @Test
    void showServerTime() {
        // given
        final LocalDateTime now = LocalDateTime.now();
        final Timestamp expected = Timestamp.valueOf(now);

        given(serverTimeManager.getDateAndTime())
                .willReturn(now);

        // when
        final ValidatableResponse response = get("/server-time");

        // then
        response.statusCode(HttpStatus.OK.value())
                .body("serverTime", Matchers.equalTo(expected.getTime()));
    }
}
