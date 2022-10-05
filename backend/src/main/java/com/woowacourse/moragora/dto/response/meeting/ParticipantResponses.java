package com.woowacourse.moragora.dto.response.meeting;

import com.woowacourse.moragora.domain.meeting.Meeting;
import java.util.List;
import java.util.stream.Collectors;

public class ParticipantResponses {

    public static List<ParticipantResponse> create(final Meeting meeting) {
        return meeting.getParticipants().stream()
                .map(ParticipantResponse::of)
                .collect(Collectors.toUnmodifiableList());
    }
}
