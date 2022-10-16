package com.woowacourse.moragora.dto.response.meeting;

import com.woowacourse.moragora.domain.meeting.Meeting;
import com.woowacourse.moragora.dto.response.meeting.CoffeeStatResponse.Tuple;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CoffeeStatsResponse {

    private List<CoffeeStatResponse> userCoffeeStats;

    public CoffeeStatsResponse(final List<CoffeeStatResponse> userCoffeeStats) {
        this.userCoffeeStats = List.copyOf(userCoffeeStats);
    }

    public static CoffeeStatsResponse from(Map<Tuple, Long> userToTardyCount) {
        final List<CoffeeStatResponse> coffeeStatResponses = userToTardyCount.entrySet().stream()
                .map(it -> CoffeeStatResponse.of(it.getKey(), it.getValue()))
                .collect(Collectors.toList());
        return new CoffeeStatsResponse(coffeeStatResponses);
    }
}
