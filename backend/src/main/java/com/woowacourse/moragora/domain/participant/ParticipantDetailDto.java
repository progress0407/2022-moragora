package com.woowacourse.moragora.domain.participant;

import com.woowacourse.moragora.domain.meeting.Meeting;
import com.woowacourse.moragora.domain.user.User;
import java.util.List;
import lombok.Getter;

@Getter
public class ParticipantDetailDto {

    private Meeting meeting;
    private Participant participant;
    private Integer tardyCount;
    private User user;

    public ParticipantDetailDto(final Meeting meeting,
                                final Participant participant,
                                final Long tardyCount,
                                final User user) {
        this.meeting = meeting;
        this.participant = participant;
        this.tardyCount = tardyCount.intValue();
        this.user = user;
    }
}
