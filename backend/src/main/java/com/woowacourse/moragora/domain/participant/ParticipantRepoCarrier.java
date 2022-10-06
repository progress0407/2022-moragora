package com.woowacourse.moragora.domain.participant;

public class ParticipantRepoCarrier {

    public Long id;
    public Long tardyCount;

    public ParticipantRepoCarrier(final Long id, final Long tardyCount) {
        this.id = id;
        this.tardyCount = tardyCount;
    }
}
