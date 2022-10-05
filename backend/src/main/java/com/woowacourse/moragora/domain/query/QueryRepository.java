package com.woowacourse.moragora.domain.query;

import com.woowacourse.moragora.domain.meeting.Meeting;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface QueryRepository extends Repository<Meeting, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Meeting m where m.id = :id")
    Meeting findMeetingParticipantAttendance(final Long id);

    @Query(
            "select m from Meeting m"
                    + " join fetch m.participants p"
                    + " where m.id = :id"
    )
    Optional<Meeting> findMeetingAndAllChild(@Param("id") final Long id);
}
