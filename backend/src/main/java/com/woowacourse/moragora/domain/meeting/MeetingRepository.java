package com.woowacourse.moragora.domain.meeting;

import com.woowacourse.moragora.domain.participant.Participant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface MeetingRepository extends Repository<Meeting, Long> {

    Meeting save(final Meeting meeting);

    List<Meeting> findAll();

    Optional<Meeting> findById(final Long id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Meeting m where m.id = :id")
    void deleteById(@Param("id") final Long id);
}
