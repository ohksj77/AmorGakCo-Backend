package com.amorgakco.backend.groupapplication.domain;

import com.amorgakco.backend.global.BaseTime;
import com.amorgakco.backend.group.domain.Group;
import com.amorgakco.backend.member.domain.Member;
import com.amorgakco.backend.participant.domain.Participant;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupApplication extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member participant;

    @Enumerated(EnumType.STRING)
    private GroupApplicationStatus groupApplicationStatus;

    @Builder
    public GroupApplication(final Group group, final Member member) {
        this.group = group;
        this.participant = member;
        this.groupApplicationStatus = GroupApplicationStatus.PENDING;
    }

    public void approve(final Member member) {
        group.validateGroupHost(member);
        groupApplicationStatus = GroupApplicationStatus.APPROVED;
        group.addParticipants(new Participant(participant));
    }

    public void reject(final Member member) {
        group.validateGroupHost(member);
        groupApplicationStatus = GroupApplicationStatus.REJECTED;
    }
}
