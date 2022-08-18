package com.woowacourse.moragora.repository;

import com.woowacourse.moragora.entity.Provider;
import com.woowacourse.moragora.entity.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface TestUserJpaRepository extends JpaRepository<User, Long> {
}
