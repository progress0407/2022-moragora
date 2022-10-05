package com.woowacourse.moragora.domain.query;

import com.woowacourse.moragora.domain.meeting.Meeting;
import com.woowacourse.moragora.domain.participant.Participant;
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

    /**
     * 가져오는 것 기준으로...
     * <p>
     * Participant를 + Attendace로 1 fetch join으로 where 절을 포함해서 가져와서 다대일로 Meeting을 가져옴
     * <p>
     * Participat
     */

    /**
     * M -> P ->  A
     *
     * P -> A
     * where isEn & Tardy
     *
     * P -> M :
     */
    @Query("select p, count(a)"
            + " from Participant p "
            + " join fetch p.attendances a"
            + " where p.meeting.id = :meetingId "
            + "  and p.id in :participantIds "
            + "  and a.status = 'TARDY'"
            + "  and a.disabled = false"
            + " group by p"
    )
    Optional<Participant> findParticipantAndAll(@Param("meetingId") Long meetingId);

    /**
     * case when m.age <= 10 then '학생요금'
     *  when m.age >= 60 then '경로요금'
     *  else '일반요금'
     *  end
     */
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
