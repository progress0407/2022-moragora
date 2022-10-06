package com.woowacourse.moragora.domain.participant;

public class ParticipantRepoDto {

    public Long id;
    public Long tardyCount;

    public ParticipantRepoDto(final Long id, final Long tardyCount) {
        this.id = id;
        this.tardyCount = tardyCount;
    }
}
