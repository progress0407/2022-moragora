package com.woowacourse.moragora.application;

import static com.woowacourse.moragora.support.fixture.EventFixtures.EVENT1;
import static com.woowacourse.moragora.support.fixture.EventFixtures.EVENT2;
import static com.woowacourse.moragora.support.fixture.EventFixtures.EVENT3;
import static com.woowacourse.moragora.support.fixture.MeetingFixtures.MORAGORA;
import static com.woowacourse.moragora.support.fixture.UserFixtures.KUN;
import static com.woowacourse.moragora.support.fixture.UserFixtures.PHILLZ;
import static com.woowacourse.moragora.support.fixture.UserFixtures.WOODY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.moragora.domain.attendance.Status;
import com.woowacourse.moragora.domain.event.Event;
import com.woowacourse.moragora.domain.meeting.Meeting;
import com.woowacourse.moragora.domain.participant.Participant;
import com.woowacourse.moragora.domain.user.User;
import com.woowacourse.moragora.dto.response.meeting.MeetingResponse;
import com.woowacourse.moragora.dto.response.meeting.ParticipantResponse;
import com.woowacourse.moragora.exception.meeting.MeetingNotFoundException;
import com.woowacourse.moragora.support.DataSupport;
import com.woowacourse.moragora.support.DatabaseCleanUp;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MeetingQueryServiceTest {

    @Autowired
    private MeetingQueryService meetingQueryService;

    @Autowired
    private ServerTimeManager serverTimeManager;

    @Autowired
    private DataSupport dataSupport;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void setUp() {
        databaseCleanUp.execute();
    }

    @DisplayName("id로 모임 상세 정보를 조회한다.")
    @Test
    void findById() {
        // given
        final Meeting meeting = dataSupport.saveMeeting(MORAGORA.create());
        final User user = dataSupport.saveUser(KUN.create());
        final Participant participant = dataSupport.saveParticipant(user, meeting, true);
        final Event event1 = dataSupport.saveEvent(EVENT1.create(meeting));
        final Event event2 = dataSupport.saveEvent(EVENT2.create(meeting));
        final Event event3 = dataSupport.saveEvent(EVENT3.create(meeting));
        dataSupport.saveAttendance(participant, event1, Status.TARDY);
        dataSupport.saveAttendance(participant, event2, Status.TARDY);
        dataSupport.saveAttendance(participant, event3, Status.TARDY);

        final MeetingResponse expectedMeetingResponse = MeetingResponse.builder()
                .id(meeting.getId())
                .name(meeting.getName())
                .participantResponses(List.of(ParticipantResponse.of(participant, 3)))
                .attendedEventCount(3)
                .isCoffeeTime(true)
                .isLoginUserMaster(true)
                .build();

        final LocalDateTime dateTime = LocalDateTime.of(2022, 8, 4, 10, 6);
        serverTimeManager.refresh(dateTime);

        // when
        final MeetingResponse response = meetingQueryService.findById(meeting.getId(), user.getId());

        // then
        assertThat(response).usingRecursiveComparison()
                .ignoringFields("users")
                .isEqualTo(expectedMeetingResponse);
    }

    @DisplayName("id로 모임 상세 정보를 조회한다. (case 2)")
    @Test
    void findById_2() {
        // given
        final Meeting meeting = dataSupport.saveMeeting(MORAGORA.create());
        final User user = dataSupport.saveUser(KUN.create());
        final User user2 = dataSupport.saveUser(PHILLZ.create());
        final Participant participant = dataSupport.saveParticipant(user, meeting, true);
        final Participant participant2 = dataSupport.saveParticipant(user2, meeting, false);
        final Event event1 = dataSupport.saveEvent(EVENT1.create(meeting));
        final Event event2 = dataSupport.saveEvent(EVENT2.create(meeting));
        final Event event3 = dataSupport.saveEvent(EVENT3.create(meeting));
        dataSupport.saveAttendance(participant, event1, Status.TARDY);
        dataSupport.saveAttendance(participant, event2, Status.TARDY);
        dataSupport.saveAttendance(participant, event3, Status.TARDY);
        dataSupport.saveAttendance(participant2, event1, Status.PRESENT);
        dataSupport.saveAttendance(participant2, event2, Status.PRESENT);
        dataSupport.saveAttendance(participant2, event3, Status.PRESENT);

        final MeetingResponse expectedMeetingResponse = MeetingResponse.builder()
                .id(meeting.getId())
                .name(meeting.getName())
                .participantResponses(List.of(
                        ParticipantResponse.of(participant, 3),
                        ParticipantResponse.of(participant, 0)))
                .attendedEventCount(3)
                .isCoffeeTime(true)
                .isLoginUserMaster(true)
                .build();

        final LocalDateTime dateTime = LocalDateTime.of(2022, 8, 4, 10, 6);
        serverTimeManager.refresh(dateTime);

        // when
        final MeetingResponse response = meetingQueryService.findById(meeting.getId(), user.getId());

        // then
        assertThat(response).usingRecursiveComparison()
                .ignoringFields("users")
                .isEqualTo(expectedMeetingResponse);
    }

    @DisplayName("id로 모임 상세 정보를 조회한다_당일 출석부가 없는 경우 추가 후 조회한다.")
    @Test
    void findById_putAttendanceIfAbsent() {
        // given
        final Meeting meeting = dataSupport.saveMeeting(MORAGORA.create());
        final User user = dataSupport.saveUser(KUN.create());
        final Participant participant = dataSupport.saveParticipant(user, meeting);

        final Event event1 = dataSupport.saveEvent(EVENT1.create(meeting));
        final Event event2 = dataSupport.saveEvent(EVENT2.create(meeting));
        final Event event3 = dataSupport.saveEvent(EVENT3.create(meeting));
        dataSupport.saveAttendance(participant, event1, Status.TARDY);
        dataSupport.saveAttendance(participant, event2, Status.TARDY);
        dataSupport.saveAttendance(participant, event3, Status.TARDY);

        final MeetingResponse expectedMeetingResponse = MeetingResponse.builder()
                .id(meeting.getId())
                .name(meeting.getName())
                .participantResponses(List.of(ParticipantResponse.of(participant, 3)))
                .attendedEventCount(3)
                .isCoffeeTime(true)
                .isLoginUserMaster(false)
                .build();

        final LocalDateTime dateTime = LocalDateTime.of(2022, 8, 4, 10, 0);
        serverTimeManager.refresh(dateTime);

        // when
        final MeetingResponse response = meetingQueryService.findById(meeting.getId(), user.getId());

        // then
        assertThat(response).usingRecursiveComparison()
                .ignoringFields("users")
                .isEqualTo(expectedMeetingResponse);
    }

    @DisplayName("id로 모임 상세 정보를 조회한다(출석 마감 시간이 지나면 당일 지각 스택도 반영된다.)")
    @Test
    void findById_ifOverClosingTime() {
        // given
        final Meeting meeting = dataSupport.saveMeeting(MORAGORA.create());
        final User user1 = dataSupport.saveUser(KUN.create());
        final Participant participant1 = dataSupport.saveParticipant(user1, meeting, true);
        final Event event1 = dataSupport.saveEvent(EVENT1.create(meeting));
        dataSupport.saveAttendance(participant1, event1, Status.TARDY);

        final User user2 = dataSupport.saveUser(PHILLZ.create());
        final Participant participant2 = dataSupport.saveParticipant(user2, meeting);
        dataSupport.saveAttendance(participant2, event1, Status.TARDY);

        final User user3 = dataSupport.saveUser(WOODY.create());
        final Participant participant3 = dataSupport.saveParticipant(user3, meeting);
        dataSupport.saveAttendance(participant3, event1, Status.TARDY);

        final List<ParticipantResponse> usersResponse = List.of(
                new ParticipantResponse(user1.getId(), user1.getEmail(), user1.getNickname(), 1,
                        participant1.getIsMaster()),
                new ParticipantResponse(user2.getId(), user2.getEmail(), user2.getNickname(), 1,
                        participant2.getIsMaster()),
                new ParticipantResponse(user3.getId(), user3.getEmail(), user3.getNickname(), 1,
                        participant3.getIsMaster())
        );

        final MeetingResponse expectedMeetingResponse = MeetingResponse.builder()
                .id(meeting.getId())
                .name(meeting.getName())
                .participantResponses(usersResponse)
                .attendedEventCount(1)
                .isCoffeeTime(true)
                .isLoginUserMaster(true)
                .build();

        final LocalDateTime dateTime = LocalDateTime.of(2022, 8, 1, 10, 6);
        serverTimeManager.refresh(dateTime);

        // when
        final MeetingResponse response = meetingQueryService.findById(meeting.getId(), user1.getId());

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expectedMeetingResponse);
    }


    @DisplayName("Master인 id로 모임 상세 정보를 조회한다")
    @Test
    void findById_isMaster() {
        // given
        final Meeting meeting = MORAGORA.create();
        final User user = KUN.create();

        dataSupport.saveParticipant(user, meeting, true);

        final LocalDateTime dateTime = LocalDateTime.of(2022, 8, 1, 10, 5);
        serverTimeManager.refresh(dateTime);

        // when
        final MeetingResponse response = meetingQueryService.findById(meeting.getId(), user.getId());

        // then
        assertThat(response.getIsLoginUserMaster()).isTrue();
    }


    @DisplayName("id로 모임 상세 정보를 조회한다(출석 마감 시간 전에는 당일 지각 스택은 반영되지 않는다.)")
    @Test
    void findById_ifNotOverClosingTime() {
        // given
        final Meeting meeting = dataSupport.saveMeeting(MORAGORA.create());
        final User user1 = dataSupport.saveUser(KUN.create());
        final Participant participant1 = dataSupport.saveParticipant(user1, meeting, true);
        final Event event1 = dataSupport.saveEvent(EVENT1.create(meeting));
        dataSupport.saveAttendance(participant1, event1, Status.NONE);

        final User user2 = dataSupport.saveUser(PHILLZ.create());
        final Participant participant2 = dataSupport.saveParticipant(user2, meeting);
        dataSupport.saveAttendance(participant2, event1, Status.NONE);

        final User user3 = dataSupport.saveUser(WOODY.create());
        final Participant participant3 = dataSupport.saveParticipant(user3, meeting);
        dataSupport.saveAttendance(participant3, event1, Status.NONE);

        final List<ParticipantResponse> usersResponse = List.of(
                new ParticipantResponse(user1.getId(), user1.getEmail(), user1.getNickname(), 0,
                        participant1.getIsMaster()),
                new ParticipantResponse(user2.getId(), user2.getEmail(), user2.getNickname(), 0,
                        participant2.getIsMaster()),
                new ParticipantResponse(user3.getId(), user3.getEmail(), user3.getNickname(), 0,
                        participant3.getIsMaster())
        );

        final MeetingResponse expectedMeetingResponse = MeetingResponse.builder()
                .id(meeting.getId())
                .name(meeting.getName())
                .participantResponses(usersResponse)
                .attendedEventCount(1)
                .isCoffeeTime(false)
                .isLoginUserMaster(true)
                .build();

        final LocalDateTime dateTime = LocalDateTime.of(2022, 8, 1, 10, 4);
        serverTimeManager.refresh(dateTime);

        // when
        final MeetingResponse response = meetingQueryService.findById(meeting.getId(), user1.getId());

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expectedMeetingResponse);
    }

    @DisplayName("id로 모임 상세 정보를 조회한다(당일부터의 일정이 없을 경우 기존의 출석 데이터를 응답한다).")
    @Test
    void findById_if_hasNoEvent_and_hasNoUpcomingEvent() {
        // given
        final Meeting meeting = dataSupport.saveMeeting(MORAGORA.create());
        final User user = dataSupport.saveUser(KUN.create());
        final Participant participant = dataSupport.saveParticipant(user, meeting, true);
        final Event event = dataSupport.saveEvent(EVENT1.create(meeting));
        dataSupport.saveAttendance(participant, event, Status.PRESENT);

        final MeetingResponse expectedMeetingResponse = MeetingResponse.builder()
                .id(meeting.getId())
                .name(meeting.getName())
                .attendedEventCount(1)
                .isCoffeeTime(false)
                .isLoginUserMaster(true)
                .build();

        final LocalDateTime dateTime = LocalDateTime.of(2022, 8, 2, 9, 59);
        serverTimeManager.refresh(dateTime);

        // when
        final MeetingResponse response = meetingQueryService.findById(meeting.getId(), user.getId());

        // then
        assertThat(response).usingRecursiveComparison()
                .ignoringFields("users")
                .isEqualTo(expectedMeetingResponse);
    }

    @DisplayName("Master가 아닌 id로 모임 상세 정보를 조회한다")
    @Test
    void findById_NotMaster() {
        // given
        final Meeting meeting = MORAGORA.create();
        final User user = KUN.create();

        dataSupport.saveParticipant(user, meeting, false);

        final LocalDateTime dateTime = LocalDateTime.of(2022, 7, 14, 10, 5);
        serverTimeManager.refresh(dateTime);

        // when
        final MeetingResponse response = meetingQueryService.findById(meeting.getId(), user.getId());

        // then
        assertThat(response.getIsLoginUserMaster()).isFalse();
    }


    @DisplayName("존재하지 않는 미팅을 삭제하려고 하면 예외가 발생한다.")
    @Test
    void deleteMeeting_throwsException_ifNotExistMeeting() {
        // given
        final User user = dataSupport.saveUser(KUN.create());

        // when, then
        assertThatThrownBy(() -> meetingQueryService.findById(99L, user.getId()))
                .isInstanceOf(MeetingNotFoundException.class);
    }


    @DisplayName("id로 모임 상세 정보를 조회한다(당일 일정이 없으면 출석부를 초기화 하지 않고 기존 출석 데이터를 응답한다).")
    @Test
    void findById_if_hasNoEvent_and_hasUpcomingEvent() {
        // given
        final Meeting meeting = dataSupport.saveMeeting(MORAGORA.create());
        final User user = dataSupport.saveUser(KUN.create());
        final Participant participant = dataSupport.saveParticipant(user, meeting, true);
        final Event event = dataSupport.saveEvent(EVENT1.create(meeting));
        dataSupport.saveEvent(EVENT2.create(meeting));
        dataSupport.saveAttendance(participant, event, Status.PRESENT);

        final MeetingResponse expectedMeetingResponse = MeetingResponse.builder()
                .id(meeting.getId())
                .name(meeting.getName())
                .attendedEventCount(1)
                .isCoffeeTime(false)
                .isLoginUserMaster(true)
                .build();

        final LocalDateTime dateTime = LocalDateTime.of(2022, 8, 1, 9, 59);
        serverTimeManager.refresh(dateTime);

        // when
        final MeetingResponse response = meetingQueryService.findById(meeting.getId(), user.getId());

        // then
        assertThat(response).usingRecursiveComparison()
                .ignoringFields("users")
                .isEqualTo(expectedMeetingResponse);
    }

}
