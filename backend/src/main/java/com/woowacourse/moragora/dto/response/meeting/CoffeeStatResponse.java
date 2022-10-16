package com.woowacourse.moragora.dto.response.meeting;

import com.woowacourse.moragora.domain.participant.Participant;
import com.woowacourse.moragora.domain.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
@EqualsAndHashCode
public class CoffeeStatResponse {

    private final Long id;
    private final String nickname;
    private final Integer coffeeCount;

    public CoffeeStatResponse(final Long id, final String nickname, final Integer coffeeCount) {
        System.out.println("CoffeeStatResponse.CoffeeStatResponse");
        this.id = id;
        this.nickname = nickname;
        this.coffeeCount = coffeeCount;
    }

    public static CoffeeStatResponse of(final Tuple tuple, final Long tardyCount) {
        return new CoffeeStatResponse(tuple.id, tuple.nickname, tardyCount.intValue());
    }

    @Getter
    @EqualsAndHashCode
    public static class Tuple {
        private Long id;
        private String nickname;

        public Tuple(final Long id, final String nickname) {
            this.id = id;
            this.nickname = nickname;
        }
    }
}
