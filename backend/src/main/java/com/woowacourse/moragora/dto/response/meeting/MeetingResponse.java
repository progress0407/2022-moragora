package com.woowacourse.moragora.dto.response.meeting;

import com.woowacourse.moragora.domain.meeting.Meeting;
import com.woowacourse.moragora.domain.participant.Participant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MeetingResponse {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final Long id;
    private final String name;
    private final long attendedEventCount;
    private final Boolean isLoginUserMaster;
    private final Boolean isCoffeeTime;
    private final List<ParticipantResponse> users;

    @Builder
    public MeetingResponse(final Long id,
                           final String name,
                           final long attendedEventCount,
                           final Boolean isLoginUserMaster,
                           final Boolean isCoffeeTime,
                           final List<ParticipantResponse> participantResponses) {
        this.id = id;
        this.name = name;
        this.attendedEventCount = attendedEventCount;
        this.isLoginUserMaster = isLoginUserMaster;
        this.isCoffeeTime = isCoffeeTime;
        this.users = participantResponses;
    }

    public static MeetingResponse from(final Meeting meeting,
                                       final List<Participant> participants,
                                       final long attendedEventCount,
                                       final boolean isLoginUserMaster,
                                       final boolean isTardyStackFull) {
        return new MeetingResponse(
                meeting.getId(),
                meeting.getName(),
                attendedEventCount,
                isLoginUserMaster,
                isTardyStackFull,
                participantResponses(participants)
        );
    }

    public static List<ParticipantResponse> participantResponses(final List<Participant> participants) {
        return participants.stream()
                .map(ParticipantResponse::of)
                .collect(Collectors.toUnmodifiableList());
    }
}
