package com.woowacourse.moragora.dto.response;

import com.woowacourse.moragora.domain.participant.Participant;

public class ParticipantRepoDto {

    public Long id;
    public Integer tardyCount;

    public ParticipantRepoDto(final Long id, final Long tardyCount) {
        this.id = id;
        this.tardyCount = tardyCount.intValue();
    }
}
