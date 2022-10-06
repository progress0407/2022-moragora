package com.woowacourse.moragora.domain.participant;

import com.woowacourse.moragora.domain.attendance.Attendance;
import com.woowacourse.moragora.domain.meeting.Meeting;
import com.woowacourse.moragora.domain.user.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "participant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "boolean default false")
    private Boolean isMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @OneToMany(mappedBy = "participant")
    private final List<Attendance> attendances = new ArrayList<>();

    @Transient
    @Setter
    private Integer tardyCount = null;

    public Participant(final User user, final Meeting meeting, final boolean isMaster) {
        this.user = user;
        this.meeting = meeting;
        this.isMaster = isMaster;
    }

    public Participant(final Participant participant, final long tardyCount) {
        this.id = participant.id;
        this.isMaster = participant.isMaster;
        this.user = participant.user;
        this.meeting = participant.meeting;
        this.tardyCount = (int) tardyCount;
    }

    public void mapMeeting(final Meeting meeting) {
        this.meeting = meeting;

        if (!meeting.getParticipants().value().contains(this)) {
            meeting.getParticipants().value().add(this);
        }
    }

    public void updateIsMaster(final boolean isMaster) {
        this.isMaster = isMaster;
    }

    public void calculateTardy() {
        this.tardyCount = (int) attendances.stream()
                .filter(Attendance::isTardy)
                .filter(Attendance::isEnabled)
                .count();
    }

    public Integer calculateTardyAndGet() {
        return  (int) attendances.stream()
                .filter(Attendance::isTardy)
                .filter(Attendance::isEnabled)
                .count();
    }
}
