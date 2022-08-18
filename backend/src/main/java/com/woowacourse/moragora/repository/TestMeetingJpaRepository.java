package com.woowacourse.moragora.repository;

import com.woowacourse.moragora.entity.Meeting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface TestMeetingJpaRepository extends JpaRepository<Meeting, Long> {

}
