package com.woowacourse.moragora.presentation;

import com.woowacourse.moragora.application.AttendanceService;
import com.woowacourse.moragora.application.auth.MasterAuthorization;
import com.woowacourse.moragora.dto.request.meeting.GeolocationAttendanceRequest;
import com.woowacourse.moragora.dto.request.user.UserAttendanceRequest;
import com.woowacourse.moragora.dto.response.attendance.AttendancesResponse;
import com.woowacourse.moragora.dto.response.meeting.CoffeeStatsResponse;
import com.woowacourse.moragora.presentation.auth.Authentication;
import com.woowacourse.moragora.presentation.auth.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Authentication
@RequestMapping("/meetings/{meetingId}")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(final AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @Authentication
    @GetMapping("/attendances/today")
    public ResponseEntity<AttendancesResponse> showAttendances(@PathVariable final Long meetingId) {
        final AttendancesResponse attendancesResponse = attendanceService.findTodayAttendancesByMeeting(meetingId);
        return ResponseEntity.ok(attendancesResponse);
    }

    @MasterAuthorization
    @PostMapping("/users/{userId}/attendances/today")
    public ResponseEntity<Void> markAttendance(@PathVariable final Long meetingId,
                                               @PathVariable final Long userId,
                                               @RequestBody final UserAttendanceRequest request,
                                               @AuthenticationPrincipal final Long loginId) {
        attendanceService.updateAttendance(meetingId, userId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/coffees/use")
    public ResponseEntity<CoffeeStatsResponse> showUserCoffeeStats(@PathVariable final Long meetingId) {
        final CoffeeStatsResponse response = attendanceService.countUsableCoffeeStack(meetingId);
        return ResponseEntity.ok(response);
    }

    @MasterAuthorization
    @PostMapping("/coffees/use")
    public ResponseEntity<Void> useCoffeeStack(@PathVariable final Long meetingId,
                                               @AuthenticationPrincipal final Long loginId) {
        attendanceService.disableUsedTardy(meetingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{userId}/attendances/today/geolocation")
    public ResponseEntity<Void> attendWithBeaconBase(@PathVariable final Long meetingId,
                                                                              @PathVariable final Long userId,
                                                                              @RequestBody final GeolocationAttendanceRequest geoAttendanceRequest) {
        attendanceService.attendWithGeoLocation(meetingId, userId, geoAttendanceRequest);
        return ResponseEntity.noContent().build();
    }
}
