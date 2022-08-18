package com.woowacourse.moragora.service;

import com.woowacourse.moragora.entity.user.EncodedPassword;
import com.woowacourse.moragora.entity.user.User;
import com.woowacourse.moragora.repository.TestAttendanceJpaRepository;
import com.woowacourse.moragora.repository.TestEventJpaRepository;
import com.woowacourse.moragora.repository.TestMeetingJpaRepository;
import com.woowacourse.moragora.repository.TestParticipantJpaRepository;
import com.woowacourse.moragora.repository.TestUserJpaRepository;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TestService {

    private final TestAttendanceJpaRepository attendanceRepository;
    private final TestEventJpaRepository eventRepository;
    private final TestMeetingJpaRepository meetingRepository;
    private final TestParticipantJpaRepository participantRepository;
    private final TestUserJpaRepository userRepository;

    @PersistenceContext
    private EntityManager em;

    public void clearData() {
        attendanceRepository.deleteAll();
        eventRepository.deleteAll();
        meetingRepository.deleteAll();
        participantRepository.deleteAll();
        userRepository.deleteAll();
    }

    /*
    insert into user values (1,'greenblues1190@gmail.com','진짜우디',null,'GOOGLE'),
                        (2,'shindongseok1224@gmail.com','아스피',null,'GOOGLE'),
                        (3,'trulyforky@gmail.com','칙칙포키',null,'GOOGLE'),
                        (4,'progress0407@gmail.com','조성우',null,'GOOGLE'),
                        (5,'h01062983052@gmail.com','홍동건',null,'GOOGLE'),
                        (6,'dndud5548@gmail.com','감우영',null,'GOOGLE'),
                        (7,'syoun602@gmail.com','썬',null,'GOOGLE');
     */

    /*
    id bigint not null auto_increment,
    email varchar(255) not null,
    nickname varchar(255) not null,
    password varchar(255),
    provider varchar(255),
    primary key (id)
     */

    // user - meeting - participant - event - attendance
    public void initData() {
        em.createNativeQuery("insert into user values (1,'greenblues1190@gmail.com','진짜우디',null,'GOOGLE'),\n"
                + "                        (2,'shindongseok1224@gmail.com','아스피',null,'GOOGLE'),\n"
                + "                        (3,'trulyforky@gmail.com','칙칙포키',null,'GOOGLE'),\n"
                + "                        (4,'progress0407@gmail.com','조성우',null,'GOOGLE'),\n"
                + "                        (5,'h01062983052@gmail.com','홍동건',null,'GOOGLE'),\n"
                + "                        (6,'dndud5548@gmail.com','감우영',null,'GOOGLE'),\n"
                + "                        (7,'syoun602@gmail.com','썬',null,'GOOGLE');").executeUpdate();

        em.createNativeQuery("insert into meeting values (1, 'CS 스터디');").executeUpdate();

        em.createNativeQuery("insert into participant values (1, 1, 1, 1),\n"
                + "                               (2, 0, 1, 2),\n"
                + "                               (3, 0, 1, 3),\n"
                + "                               (4, 0, 1, 4),\n"
                + "                               (5, 0, 1, 5),\n"
                + "                               (6, 0, 1, 6),\n"
                + "                               (7, 0, 1, 7);").executeUpdate();

        em.createNativeQuery("insert into event values (1, '2022-08-07', '11:00:00', '09:00:00', 1),\n"
                + "                         (2, '2022-08-08', '11:00:00', '09:00:00', 1),\n"
                + "                         (3, '2022-08-09', '11:00:00', '09:00:00', 1),\n"
                + "                         (4, '2022-08-10', '11:00:00', '09:00:00', 1),\n"
                + "                         (5, '2022-08-11', '11:00:00', '09:00:00', 1),\n"
                + "                         (6, '2022-08-12', '11:00:00', '09:00:00', 1),\n"
                + "                         (7, '2022-08-13', '11:00:00', '09:00:00', 1),\n"
                + "                         (8, '2022-08-14', '11:00:00', '09:00:00', 1),\n"
                + "                         (9, '2022-08-15', '11:00:00', '09:00:00', 1);").executeUpdate();

        em.createNativeQuery("insert into attendance values (1, 0, 'TARDY', 1, 1),\n"
                + "                              (2, 0, 'PRESENT', 1, 2),\n"
                + "                              (3, 0, 'PRESENT', 1, 3),\n"
                + "                              (4, 0, 'PRESENT', 1, 4),\n"
                + "                              (5, 0, 'PRESENT', 1, 5),\n"
                + "                              (6, 0, 'TARDY', 1, 6),\n"
                + "                              (7, 0, 'TARDY', 1, 7),\n"
                + "                              (8, 0, 'PRESENT', 2, 1),\n"
                + "                              (9, 0, 'PRESENT', 2, 2),\n"
                + "                              (10, 0, 'PRESENT', 2, 3),\n"
                + "                              (11, 0, 'PRESENT', 2, 4),\n"
                + "                              (12, 0, 'PRESENT', 2, 5),\n"
                + "                              (13, 0, 'TARDY', 2, 6),\n"
                + "                              (14, 0, 'TARDY', 2, 7),\n"
                + "                              (15, 0, 'TARDY', 3, 1),\n"
                + "                              (16, 0, 'PRESENT', 3, 2),\n"
                + "                              (17, 0, 'PRESENT', 3, 3),\n"
                + "                              (18, 0, 'PRESENT', 3, 4),\n"
                + "                              (19, 0, 'PRESENT', 3, 5),\n"
                + "                              (20, 0, 'TARDY', 3, 6),\n"
                + "                              (21, 0, 'TARDY', 3, 7),\n"
                + "                              (22, 0, 'TARDY', 4, 1),\n"
                + "                              (23, 0, 'TARDY', 4, 2),\n"
                + "                              (24, 0, 'TARDY', 4, 3),\n"
                + "                              (25, 0, 'PRESENT', 4, 4),\n"
                + "                              (26, 0, 'PRESENT', 4, 5),\n"
                + "                              (27, 0, 'PRESENT', 4, 6),\n"
                + "                              (28, 0, 'TARDY', 4, 7),\n"
                + "                              (29, 0, 'TARDY', 5, 1),\n"
                + "                              (30, 0, 'TARDY', 5, 2),\n"
                + "                              (31, 0, 'TARDY', 5, 3),\n"
                + "                              (32, 0, 'TARDY', 5, 4),\n"
                + "                              (33, 0, 'PRESENT', 5, 5),\n"
                + "                              (34, 0, 'PRESENT', 5, 6),\n"
                + "                              (35, 0, 'PRESENT', 5, 7),\n"
                + "                              (36, 0, 'TARDY', 6, 1),\n"
                + "                              (37, 0, 'PRESENT', 6, 2),\n"
                + "                              (38, 0, 'PRESENT', 6, 3),\n"
                + "                              (39, 0, 'TARDY', 6, 4),\n"
                + "                              (40, 0, 'PRESENT', 6, 5),\n"
                + "                              (41, 0, 'PRESENT', 6, 6),\n"
                + "                              (42, 0, 'PRESENT', 6, 7),\n"
                + "                              (43, 0, 'PRESENT', 7, 1),\n"
                + "                              (44, 0, 'TARDY', 7, 2),\n"
                + "                              (45, 0, 'TARDY', 7, 3),\n"
                + "                              (46, 0, 'TARDY', 7, 4),\n"
                + "                              (47, 0, 'PRESENT', 7, 5),\n"
                + "                              (48, 0, 'PRESENT', 7, 6),\n"
                + "                              (49, 0, 'PRESENT', 7, 7),\n"
                + "                              (50, 0, 'PRESENT', 8, 1),\n"
                + "                              (51, 0, 'TARDY', 8, 2),\n"
                + "                              (52, 0, 'TARDY', 8, 3),\n"
                + "                              (53, 0, 'TARDY', 8, 4),\n"
                + "                              (54, 0, 'TARDY', 8, 5),\n"
                + "                              (55, 0, 'PRESENT', 8, 6),\n"
                + "                              (56, 0, 'PRESENT', 8, 7);").executeUpdate();

    }

    public List<User> getData() {
        return userRepository.findAll();
    }
}
