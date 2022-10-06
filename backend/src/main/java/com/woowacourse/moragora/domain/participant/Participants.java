package com.woowacourse.moragora.domain.participant;

import com.woowacourse.moragora.domain.meeting.Meeting;
import com.woowacourse.moragora.domain.query.QueryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
        final List<ParticipantRepoCarrier> dtos = queryRepository.findParticipantAndAttendanceCount3(participants);

        final Map<Long, Long> cache = dtos.stream()
                .collect(Collectors.toMap(it -> it.id, it -> it.tardyCount));

        for (final Participant participant : participants) {
            final Integer tardyCount = cache.get(participant.getId()).intValue();
            participant.changeTardyCount(tardyCount);
        }

        this.totalTardyCount = calculateTotalTardyCount();

        if (isTardyStackFullCondition()) {
            this.isTardyStackFull = true;
        }
    }

    public Optional<Participant> findOne(final Long id) {
        return participants.stream()
                .filter(participant -> participant.getId().equals(id))
                .findAny();
    }

    public List<Participant> value() {
        return participants;
    }

    public List<Long> ids() {
        return participants.stream()
                .map(Participant::getId)
                .collect(Collectors.toUnmodifiableList());
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
