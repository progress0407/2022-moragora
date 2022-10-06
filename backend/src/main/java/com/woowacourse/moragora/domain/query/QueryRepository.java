package com.woowacourse.moragora.domain.query;

import com.woowacourse.moragora.domain.meeting.Meeting;
import com.woowacourse.moragora.domain.participant.Participant;
import com.woowacourse.moragora.domain.participant.ParticipantRepoCarrier;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface QueryRepository extends Repository<Meeting, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Meeting m where m.id = :id")
    Meeting findMeetingParticipantAttendance(final Long id);

    @Query("select "
            + " distinct p "
            + " from Participant p "
            + " inner join fetch p.user u "
            + " inner join fetch p.meeting m "
            + " left  join fetch p.attendances a "
            + " where p.meeting.id = :meetingId "
            + " and   a.status = 'TARDY'"
            + " and   a.disabled = false ")
    List<Participant> findParticipantAndAll(@Param("meetingId") Long meetingId);

    @Query("select "
            + " p, count(case when a.status = 'TARDY' then 1 end) "
            + " from Participant p "
            + " inner join p.user u "
            + " inner join p.meeting m "
            + " left join p.attendances a "
            + " where p.meeting.id = :meetingId "
            + "   and a.disabled = false "
            + " group by p "
    )
    List<Object[]> findParticipantAndAll3(@Param("meetingId") Long meetingId);

    @Query("select p.id, count(a) "
            + "  from Participant p "
            + " left join Attendance a "
            + "  where p in :participants "
            + "    and a.status = 'TARDY' "
            + "    and a.disabled = false "
            + " group by p ")
    List<Object[]> findParticipantAndAttendanceCount(@Param("participants") List<Participant> participants);

    @Query("select p.id, count(a) "
            + "  from Participant p "
            + " left join Attendance a "
            + "  where p.id in :participantIds "
            + "    and a.status = 'TARDY' "
            + "    and a.disabled = false "
            + " group by p ")
    List<Object[]> findParticipantAndAttendanceCount2(@Param("participantIds") List<Long> participantIds);

    @Query("select new com.woowacourse.moragora.domain.participant.ParticipantRepoCarrier(p.id, count(a)) "
            + "  from Participant p "
            + " left join p.attendances a "
            + "  where p in :participants "
            + "    and a.status = 'TARDY' "
            + "    and a.disabled = false "
            + " group by p ")
    List<ParticipantRepoCarrier> findParticipantAndAttendanceCount3(@Param("participants") List<Participant> participants);

    @Query("select "
            + " a.participant.id, "
            + " case "
            + "      when count (a) is null then '0' "
            + "      else count(a) "
            + " end"
            + " from Attendance a "
            + "  inner join a.event e"
            + " where a.participant.id in :participantIds "
            + "   and e.date <= :date "
            + "   and a.status = 'TARDY' "
            + "   and a.disabled = false "
            + " group by a.participant "
            + " order by a.participant.id")
    List<Object[]> findTardyCount(
            @Param("participantIds") final List <Long> participantIds,
            @Param("date") final LocalDate date);

    @Query(
            "select m from Meeting m"
                    + " join fetch m.participants p"
                    + " where m.id = :id"
    )
    Optional<Meeting> findMeetingAndAllChild(@Param("id") final Long id);

    @Query("select "
            + "    p.id,  "
            + "    ( select count(a)"
            + "     from Attendance a"
            + "     inner join a.event e "
            + "     where a.participant.id = p.id "
            + "     and a.status = 'TARDY' "
            + "     and a.disabled = false "
            + "     and e.date <= :date "
            + "     ) "
            + " from Participant p "
            + " where p.id in :participantIds")
    List<Object[]> findTardyCount_v2(
            @Param("participantIds") final List <Long> participantIds,
            @Param("date") final LocalDate date);

    @Query("select count(a) from Attendance a where a.status = 'TARDY' and a.disabled = false and a.event.date <= :date and a.participant.id = :id")
    int findTardyCount_single(@Param("id") Long id, @Param("date") LocalDate date);
}
