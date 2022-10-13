package com.woowacourse.moragora.dto.response.meeting;

import com.woowacourse.moragora.domain.participant.Participant;
import com.woowacourse.moragora.domain.user.User;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CoffeeStatResponse {

    private final Long id;
    private final String nickname;
    private final Integer coffeeCount;

    public CoffeeStatResponse(final Long id, final String nickname, final Integer coffeeCount) {
        this.id = id;
        this.nickname = nickname;
        this.coffeeCount = coffeeCount;
    }

    public static CoffeeStatResponse of(final User user, final Integer coffeeCount) {
        return new CoffeeStatResponse(
                user.getId(), user.getNickname(), coffeeCount
        );
    }

    public static CoffeeStatResponse of(final Participant participant) {
        final User user = participant.getUser();
        return new CoffeeStatResponse(
                user.getId(),
                user.getNickname(),
                participant.getTardyCount()
        );
    }
}
