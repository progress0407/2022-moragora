package com.woowacourse.moragora.repository;

import com.woowacourse.moragora.entity.Attendance;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceSpringJpaRepository extends JpaRepository<Attendance, Long> {

    // checked
    // TODO 최적화 필요
    //  left join participant
    List<Attendance> findByParticipantId(final Long participantId);

    @Query("select a from Attendance a where a.participant.id = :participantId")
    List<Attendance> findListBy(@Param("participantId") final Long participantId);

    // checked
    // findAttendanceCountById -> countByParticipantId
    long countByParticipantId(final Long participantId);

    // checked
    Optional<Attendance> findByParticipantIdAndAttendanceDate(final Long participantId,
                                                              final LocalDate attendanceDate);

    /**
     * 바로 위 메서드인 findByParticipantIdAndAttendanceDate 의 대체하는 방법 장점: 메서드가 간단함, join 등의 복잡한 쿼리 가능 단점: 처음 작성할때 복잡함
     */
    @Query("select a from Attendance a where a.participant.id = :participantId and a.attendanceDate = :attendanceDate")
    Optional<Attendance> findByIdAndDate(@Param("participantId") final Long participantId,
                                         @Param("attendanceDate") final LocalDate attendanceDate);


    /**
     * 쿼리 메서드명 컨벤션에 따라 이름 변경
     * <p>
     * findByParticipantIds 에서 findByParticipantIdIn 으로 변경
     * <p>
     * 에러 메세지: Failed to create query for method public abstract java.util.List
     * com.woowacourse.moragora.repository.AttendanceSpringJpaRepository.findByParticipantIds(java.util.List)! No
     * property 'ids' found for type 'Participant' Did you mean ''id'' Traversed path: Attendance.participant.
     */
//    List<Attendance> findByParticipantIds(final List<Long> participantIds);
    List<Attendance> findByParticipantIdIn(final List<Long> participantIds);

    // failed by wrong naming

    /**
     * 쿼리 메서드명 컨벤션에 따라 이름 변경
     */
    /*List<Attendance> findByParticipantIdsAndAttendanceDate(final List<Long> participantIds,
                                                           final LocalDate attendanceDate);*/

    List<Attendance> findByParticipantIdInAndAttendanceDate(final List<Long> participantIds,
                                                            final LocalDate attendanceDate);

    // checked
    List<Attendance> findByParticipantIdAndAttendanceDateNot(final Long participantId, final LocalDate today);
}
