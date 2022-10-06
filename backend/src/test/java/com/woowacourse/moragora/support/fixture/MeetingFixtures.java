package com.woowacourse.moragora.support.fixture;

import com.woowacourse.moragora.domain.meeting.Meeting;
import com.woowacourse.moragora.domain.participant.Participants;

public enum MeetingFixtures {

    MORAGORA("모라고라"),
    SOKDAKSOKDAK("속닥속닥"),
    F12("F12"),
    TEATIME("티타임");

    private final String name;

    MeetingFixtures(final String name) {
        this.name = name;
    }

    public Meeting create() {
        return Meeting.builder()
                .name(this.name)
                .participants(new Participants())
                .build();
    }
}
