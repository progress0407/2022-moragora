package com.woowacourse.moragora.application;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import com.woowacourse.moragora.domain.attendance.Attendance;
import com.woowacourse.moragora.domain.attendance.AttendanceRepository;
import com.woowacourse.moragora.domain.attendance.MeetingAttendances;
import com.woowacourse.moragora.domain.attendance.Status;
import com.woowacourse.moragora.domain.event.Event;
import com.woowacourse.moragora.domain.event.EventRepository;
import com.woowacourse.moragora.domain.meeting.Meeting;
import com.woowacourse.moragora.domain.meeting.MeetingRepository;
import com.woowacourse.moragora.domain.participant.Participant;
import com.woowacourse.moragora.domain.participant.ParticipantRepository;
import com.woowacourse.moragora.domain.query.QueryRepository;
import com.woowacourse.moragora.domain.user.User;
import com.woowacourse.moragora.domain.user.UserRepository;
import com.woowacourse.moragora.dto.projection.UserAndTardyCount;
import com.woowacourse.moragora.dto.request.user.UserAttendanceRequest;
import com.woowacourse.moragora.dto.response.attendance.AttendanceResponse;
import com.woowacourse.moragora.dto.response.attendance.AttendancesResponse;
import com.woowacourse.moragora.dto.response.meeting.CoffeeStatResponse;
import com.woowacourse.moragora.dto.response.meeting.CoffeeStatResponse.Tuple;
import com.woowacourse.moragora.dto.response.meeting.CoffeeStatsResponse;
import com.woowacourse.moragora.exception.ClientRuntimeException;
import com.woowacourse.moragora.exception.attendance.InvalidCoffeeTimeException;
import com.woowacourse.moragora.exception.event.EventNotFoundException;
import com.woowacourse.moragora.exception.meeting.AttendanceNotFoundException;
import com.woowacourse.moragora.exception.meeting.MeetingNotFoundException;
import com.woowacourse.moragora.exception.meeting.NotCheckInTimeException;
import com.woowacourse.moragora.exception.participant.ParticipantNotFoundException;
import com.woowacourse.moragora.exception.user.UserNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AttendanceService {

    private final MeetingRepository meetingRepository;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final QueryRepository queryRepository;
    private final ServerTimeManager serverTimeManager;

    public AttendanceService(final MeetingRepository meetingRepository,
                             final EventRepository eventRepository,
                             final ParticipantRepository participantRepository,
                             final AttendanceRepository attendanceRepository,
                             final UserRepository userRepository,
                             final QueryRepository queryRepository, final ServerTimeManager serverTimeManager) {
        this.meetingRepository = meetingRepository;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
        this.queryRepository = queryRepository;
        this.serverTimeManager = serverTimeManager;
    }

    public AttendancesResponse findTodayAttendancesByMeeting(final Long meetingId) {
        final Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(MeetingNotFoundException::new);
        final LocalDate date = serverTimeManager.getDate();
        final Event event = eventRepository.findByMeetingIdAndDate(meetingId, date)
                .orElseThrow(() -> new ClientRuntimeException("오늘의 일정이 존재하지 않아 출석부를 조회할 수 없습니다.",
                        HttpStatus.NOT_FOUND));

        final List<Attendance> attendances = attendanceRepository
                .findByParticipantIdInAndEventId(meeting.getParticipantIds(), event.getId());
        final List<AttendanceResponse> attendanceResponses = attendances.stream()
                .map(AttendanceResponse::from)
                .collect(Collectors.toUnmodifiableList());

        return new AttendancesResponse(attendanceResponses);
    }

    @Transactional
    public void updateAttendance(final Long meetingId,
                                 final Long userId,
                                 final UserAttendanceRequest request) {
        final Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(MeetingNotFoundException::new);
        final User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        final Participant participant = participantRepository.findByMeetingIdAndUserId(meeting.getId(), user.getId())
                .orElseThrow(ParticipantNotFoundException::new);
        final Event event = eventRepository.findByMeetingIdAndDate(meetingId, serverTimeManager.getDate())
                .orElseThrow(EventNotFoundException::new);

        validateAttendanceTime(event);

        final Attendance attendance = attendanceRepository
                .findByParticipantIdAndEventId(participant.getId(), event.getId())
                .orElseThrow(AttendanceNotFoundException::new);

        if (request.getIsPresent()) {
            attendance.changeAttendanceStatus(Status.PRESENT);
            return;
        }
        attendance.changeAttendanceStatus(Status.NONE);
    }

    public CoffeeStatsResponse countUsableCoffeeStack(final Long meetingId) {
        final LocalDate today = serverTimeManager.getDate();
        final Meeting meeting = meetingRepository.findMeetingAndParticipantsByMeetingId(meetingId)
                .orElseThrow(MeetingNotFoundException::new);
        final List<UserAndTardyCount> userAndTardyCounts = queryRepository
                .findUserAndTardyCount(meeting.getParticipants(), today, PageRequest.ofSize(meeting.getParticipants().size()));
        final Map<Tuple, Long> userToTardyCount = userAndTardyCounts.stream()
                .collect(groupingBy(it -> new CoffeeStatResponse.Tuple(it.getId(), it.getNickname()), counting()));

        meeting.allocateUserTardyCount(userToTardyCount);
        validateEnoughTardyCountToDisable(meeting);

        return CoffeeStatsResponse.from(userToTardyCount);
    }


    @Transactional
    public void disableUsedTardy(final Long meetingId) {
        final MeetingAttendances meetingAttendances = findMeetingAttendancesBy(meetingId);
        validateEnoughTardyCountToDisable_old(meetingAttendances);
        meetingAttendances.disableAttendances();
    }

    private void validateAttendanceTime(final Event event) {
        final LocalTime entranceTime = event.getStartTime();

        if (!serverTimeManager.isAttendanceOpen(entranceTime)) {
            throw new NotCheckInTimeException();
        }
    }

    private MeetingAttendances findMeetingAttendancesBy(final Long meetingId) {
        final Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(MeetingNotFoundException::new);
        final List<Long> participantIds = meeting.getParticipantIds();
        final List<Attendance> attendances = attendanceRepository
                .findByParticipantIdInAndEventDateLessThanEqual(participantIds, serverTimeManager.getDate());
        return new MeetingAttendances(attendances, participantIds.size());
    }

    // TODO 제거할 것
    private void validateEnoughTardyCountToDisable_old(final MeetingAttendances attendances) {
        if (!attendances.isTardyStackFull()) {
            throw new InvalidCoffeeTimeException();
        }
    }

    private void validateEnoughTardyCountToDisable(final Meeting meeting) {
        if (!meeting.isTardyStackFull()) {
            throw new InvalidCoffeeTimeException();
        }
    }
}
