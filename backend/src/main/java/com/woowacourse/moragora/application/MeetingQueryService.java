package com.woowacourse.moragora.application;

import com.woowacourse.moragora.domain.attendance.AttendanceRepository;
import com.woowacourse.moragora.domain.event.EventRepository;
import com.woowacourse.moragora.domain.meeting.Meeting;
import com.woowacourse.moragora.domain.meeting.MeetingRepository;
import com.woowacourse.moragora.domain.participant.Participant;
import com.woowacourse.moragora.domain.participant.ParticipantDetailDto;
import com.woowacourse.moragora.domain.participant.ParticipantRepository;
import com.woowacourse.moragora.domain.query.QueryRepository;
import com.woowacourse.moragora.domain.user.UserRepository;
import com.woowacourse.moragora.dto.response.meeting.MeetingResponse;
import com.woowacourse.moragora.exception.participant.ParticipantNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class MeetingQueryService {

    private final MeetingRepository meetingRepository;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final QueryRepository queryRepository;
    private final ServerTimeManager serverTimeManager;

    public MeetingQueryService(final MeetingRepository meetingRepository, final EventRepository eventRepository,
                               final ParticipantRepository participantRepository,
                               final AttendanceRepository attendanceRepository,
                               final UserRepository userRepository, final QueryRepository queryRepository,
                               final ServerTimeManager serverTimeManager) {
        this.meetingRepository = meetingRepository;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
        this.queryRepository = queryRepository;
        this.serverTimeManager = serverTimeManager;
    }

    public MeetingResponse findById(final Long meetingId, final Long loginId) {
        final LocalDate today = serverTimeManager.getDate();
        // q
        final List<ParticipantDetailDto> dtos = queryRepository.findParticipantAndAttendanceCount(meetingId);
        final Meeting meeting = dtos.get(0).getMeeting();

        for (final ParticipantDetailDto dto : dtos) {
            final Participant participant = dto.getParticipant();
            participant.changeTardyCount(dto.getTardyCount());
        }

        final List<Participant> participants = getParticipants(dtos);
        final Participant loginParticipant = getLoginParticipant(loginId, participants);

        // q
        final long attendedEventCount = eventRepository.countByMeetingIdAndDateLessThanEqual(meetingId, today);
        final int totalTardyCount = getTotalTardyCount(participants);

        final boolean isTardyStackFull = totalTardyCount >= participants.size();

        return MeetingResponse.from(
                meeting,
                participants,
                attendedEventCount,
                loginParticipant.getIsMaster(),
                isTardyStackFull);
    }

    private int getTotalTardyCount(final List<Participant> participants) {
        return participants.stream()
                .mapToInt(Participant::getTardyCount)
                .sum();
    }

    private Participant getLoginParticipant(final Long loginId, final List<Participant> participants) {
        return participants
                .stream()
                .filter(it -> it.getId().equals(loginId))
                .findAny()
                .orElseThrow(ParticipantNotFoundException::new);
    }

    private List<Participant> getParticipants(final List<ParticipantDetailDto> dtos) {
        return dtos.stream()
                .map(dto -> {
                    final Participant participant = dto.getParticipant();
                    participant.changeTardyCount(dto.getTardyCount());
                    return participant;
                })
                .collect(Collectors.toUnmodifiableList());
    }
}
