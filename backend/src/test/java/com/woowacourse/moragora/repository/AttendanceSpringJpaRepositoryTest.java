package com.woowacourse.moragora.repository;

import static java.lang.System.out;

import com.woowacourse.moragora.entity.Attendance;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * 문서 참고
 *
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
 */

/**
 * TODO 최적화
 *
 * 추후 Attendance -> Participant
 * FetchType을 Lazy로 지정해야 함 !
 */

@SpringBootTest
@Transactional
class AttendanceSpringJpaRepositoryTest {

    @Autowired
    private AttendanceSpringJpaRepository repository;

    /**
     * TODO 성능최적화 필요
     *
     *     select
     *         attendance0_.id as id1_0_,
     *         attendance0_.attendance_date as attendan2_0_,
     *         attendance0_.participant_id as particip4_0_,
     *         attendance0_.status as status3_0_
     *     from
     *         attendance attendance0_
     *     left outer join
     *         participant participan1_
     *             on attendance0_.participant_id=participan1_.id
     *     where
     *         participan1_.id=1
     *
     *  + 1 Join 쿼리 발생
     */
    @Test
    void findByParticipantId() {
        final List<Attendance> attendances = repository.findByParticipantId(1L);

        for (final Attendance attendance : attendances) {
            out.println("attendance = " + attendance);
        }
    }

    @Test
    void findListBy() {
        final List<Attendance> attendances = repository.findListBy(1L);

        for (final Attendance attendance : attendances) {
            out.println("attendance = " + attendance);
        }
    }

    /**
     *     select
     *         count(attendance0_.id) as col_0_0_
     *     from
     *         attendance attendance0_
     *     left outer join
     *         participant participan1_
     *             on attendance0_.participant_id=participan1_.id
     *     where
     *         participan1_.id=1
     */
    @Test
    void countByParticipantId() {
        final long count = repository.countByParticipantId(1L);
        out.println("count = " + count);
    }

    /**
     *     select
     *         attendance0_.id as id1_0_,
     *         attendance0_.attendance_date as attendan2_0_,
     *         attendance0_.participant_id as particip4_0_,
     *         attendance0_.status as status3_0_
     *     from
     *         attendance attendance0_
     *     left outer join
     *         participant participan1_
     *             on attendance0_.participant_id=participan1_.id
     *     where
     *         participan1_.id=?1
     *         and attendance0_.attendance_date=2022-07-12
     */
    @Test
    void findByParticipantIdAndAttendanceDate() {
        final Optional<Attendance> attendances = repository.findByParticipantIdAndAttendanceDate(
                1L, date(2022, 7, 12));

        out.println("attendances = " + attendances);
    }

    /**
     *     select
     *         attendance0_.id as id1_0_,
     *         attendance0_.attendance_date as attendan2_0_,
     *         attendance0_.participant_id as particip4_0_,
     *         attendance0_.status as status3_0_
     *     from
     *         attendance attendance0_
     *     left outer join
     *         participant participan1_
     *             on attendance0_.participant_id=participan1_.id
     *     where
     *         participan1_.id=1
     *         and attendance0_.attendance_date=2022-07-12
     */
    @Test
    void findByIdAndDate() {
        final Optional<Attendance> attendances = repository.findByParticipantIdAndAttendanceDate(
                1L, date(2022, 7, 12));

        out.println("attendances = " + attendances);
    }

    /**
     * select
     *         attendance0_.id as id1_0_,
     *         attendance0_.attendance_date as attendan2_0_,
     *         attendance0_.participant_id as particip4_0_,
     *         attendance0_.status as status3_0_
     *     from
     *         attendance attendance0_
     *     left outer join
     *         participant participan1_
     *             on attendance0_.participant_id=participan1_.id
     *     where
     *         participan1_.id in (
     *             1 , 2
     *         )
     */
    @Test
    void findByParticipantIdIn() {
        final List<Attendance> attendances = repository.findByParticipantIdIn(List.of(1L, 2L));

        for (final Attendance attendance : attendances) {
            out.println("attendance = " + attendance);
        }
    }

    /**
     * TODO 성능 최적화 필요 (left Join)
     *
     * select
     *         attendance0_.id as id1_0_,
     *         attendance0_.attendance_date as attendan2_0_,
     *         attendance0_.participant_id as particip4_0_,
     *         attendance0_.status as status3_0_
     *     from
     *         attendance attendance0_
     *     left outer join
     *         participant participan1_
     *             on attendance0_.participant_id=participan1_.id
     *     where
     *         (
     *             participan1_.id in (
     *                 1L , 2L
     *             )
     *         )
     *         and attendance0_.attendance_date=?
     */
    @Test
    void findByParticipantIdInAndAttendanceDate() {
        final List<Attendance> attendances = repository.findByParticipantIdInAndAttendanceDate(
                List.of(1L, 2L), date(2022, 7, 12));

        for (final Attendance attendance : attendances) {
            out.println("attendance = " + attendance);
        }
    }

    /**
     *     select
     *         attendance0_.id as id1_0_,
     *         attendance0_.attendance_date as attendan2_0_,
     *         attendance0_.participant_id as particip4_0_,
     *         attendance0_.status as status3_0_
     *     from
     *         attendance attendance0_
     *     left outer join
     *         participant participan1_
     *             on attendance0_.participant_id=participan1_.id
     *     where
     *         participan1_.id=1
     *         and attendance0_.attendance_date<>2022-07-12
     */
    @Test
    void findByParticipantIdAndAttendanceDateNot() {
        final List<Attendance> attendances = repository.findByParticipantIdAndAttendanceDateNot(
                1L, date(2022, 7, 12));

        for (final Attendance attendance : attendances) {
            out.println("attendance = " + attendance);
        }
    }

    private LocalDate date(final int year, final int month, final int day) {
        return LocalDate.of(year, month, day);
    }
}