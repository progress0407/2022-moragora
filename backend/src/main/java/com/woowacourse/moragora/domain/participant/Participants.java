package com.woowacourse.moragora.domain.participant;

import com.woowacourse.moragora.domain.meeting.Meeting;
import com.woowacourse.moragora.domain.query.QueryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class Participants {

    @Getter
    private int totalTardyCount;

    @Getter
    private boolean isTardyStackFull = false;

    @OneToMany(mappedBy = "meeting")
    private List<Participant> participants = new ArrayList<>();

    public Participants(final List<Participant> participants) {
        this.participants = participants;
    }

    public Meeting toMeeting() {
        final List<Participant> participantValues = this.participants;
        if (participantValues.isEmpty()) {
            throw new IllegalArgumentException("participants가 존재하지 않을 경우 Meeting을 생성할 수 없습니다.");
        }
        final Participant firstParticipant = participantValues.get(0);
        final Meeting meeting = firstParticipant.getMeeting();
        return Meeting.builder()
                .id(meeting.getId())
                .name(meeting.getName())
                .participants(this)
                .build();
    }

    public void calculateTardy(final QueryRepository queryRepository) {
        final List<Map<Long, Long>> countMap = queryRepository.findParticipantAndAttendanceCount();

        for (final Participant participant : participants) {
            participant.calculateTardy();
        }

        this.totalTardyCount = calculateTotalTardyCount();

        if (isTardyStackFullCondition()) {
            this.isTardyStackFull = true;
        }
    }

    public Optional<Participant> findParticipant(final Long id) {
        return participants.stream()
                .filter(participant -> participant.getId().equals(id))
                .findAny();
    }

    public List<Participant> value() {
        return participants;
    }

    private int calculateTotalTardyCount() {
        return participants.stream()
                .mapToInt(Participant::getTardyCount)
                .sum();
    }

    private boolean isTardyStackFullCondition() {
        return this.totalTardyCount >= participants.size();
    }
}
