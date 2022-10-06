package com.woowacourse.moragora.dto.response.meeting;

import com.woowacourse.moragora.domain.meeting.Meeting;
import com.woowacourse.moragora.domain.participant.Participants;
import java.util.List;
import java.util.stream.Collectors;

public class ParticipantResponses {

    public static List<ParticipantResponse> create(final Participants participants) {
        return participants.value().stream()
                .map(ParticipantResponse::of)
                .collect(Collectors.toUnmodifiableList());
    }
}
