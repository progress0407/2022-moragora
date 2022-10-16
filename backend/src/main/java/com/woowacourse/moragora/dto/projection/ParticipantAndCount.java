package com.woowacourse.moragora.dto.projection;

import com.woowacourse.moragora.domain.participant.Participant;

public interface ParticipantAndCount {

    Participant getParticipant();

    Integer getTardyCount();
}
