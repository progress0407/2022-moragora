package com.woowacourse.moragora.domain.query;

import com.woowacourse.moragora.domain.meeting.Meeting;
import com.woowacourse.moragora.domain.participant.Participant;
import com.woowacourse.moragora.dto.projection.UserAndTardyCount;
import com.woowacourse.moragora.dto.projection.ParticipantAndCount;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface QueryRepository extends Repository<Meeting, Long> {

    @Query("select "
            + " p as participant, "
            +   "( select count(a) "
            +   "  from Attendance a "
            +   "  where a.participant.id = p.id "
            + "      and a.status = 'TARDY' "
            + "      and a.disabled = false"
            + "   ) as tardyCount "
            + " from Participant p "
            + " join fetch p.user "
            + " where p in :participants ")
    List<ParticipantAndCount> countParticipantsTardy(@Param("participants") final List<Participant> participants);

    @Query("select "
            + " u.id as id, "
            + " u.nickname as nickname, "
            + " a.status as status,"
            + " e as event "
            + " from Participant p "
            + " inner join p.user u "
            + " inner join p.attendances a"
            + " inner join a.event e "
            + " where p in :participants "
            + "   and a.status = 'TARDY' "
            + "   and a.disabled = false "
            + "   and e.date <= :today "
            + " order by e.date asc ")
    LinkedList<UserAndTardyCount> findUserAndTardyCount(@Param("participants") final List<Participant> participants,
                                                        @Param("today") final LocalDate today,
                                                        final Pageable pageable);
}
