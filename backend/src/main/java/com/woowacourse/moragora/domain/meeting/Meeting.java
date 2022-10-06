package com.woowacourse.moragora.domain.meeting;

import com.woowacourse.moragora.domain.participant.Participant;
import com.woowacourse.moragora.domain.participant.Participants;
import com.woowacourse.moragora.exception.global.InvalidFormatException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meeting")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Meeting {

    private static final int MAX_NAME_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @Embedded
    private Participants participants = new Participants();

    @Builder
    public Meeting(final Long id, final String name) {
        validateName(name);
        this.id = id;
        this.name = name;
    }

    public Meeting(final String name) {
        this(null, name);
    }

    public static Meeting from(final Participants participants) {
        final List<Participant> participantValues = participants.value();
        if (participantValues.isEmpty()) {
            throw new IllegalArgumentException("participants가 존재하지 않을 경우 Meeting을 생성할 수 없습니다.");
        }
        final Participant firstParticipant = participantValues.get(0);
        return firstParticipant.getMeeting();
    }

    public void updateName(final String name) {
        validateName(name);
        this.name = name;
    }

    public List<Long> getParticipantIds() {
        return participants.value().stream()
                .map(Participant::getId)
                .collect(Collectors.toUnmodifiableList());
    }

    private void validateName(final String name) {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new InvalidFormatException();
        }
    }

    public Optional<Participant> findParticipant(final Long id) {
        return participants.value().stream()
                .filter(participant -> participant.getId().equals(id))
                .findAny();
    }

    public void calculateTardy() {
        this.participants.calculateTardy();
    }

    public boolean isTardyStackFull() {
        return this.participants.isTardyStackFull();
    }

    public int getTotalTardyCount() {
        return this.participants.getTotalTardyCount();
    }
}
