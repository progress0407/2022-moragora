package com.woowacourse.moragora.dto.projection;

import com.woowacourse.moragora.domain.attendance.Status;
import com.woowacourse.moragora.domain.event.Event;

public interface UserAndTardyCount {
    Long getId();
    String getNickname();
    Status getStatus();
    Event getEvent();
}
