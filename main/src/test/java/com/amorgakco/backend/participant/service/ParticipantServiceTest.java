package com.amorgakco.backend.participant.service;

import com.amorgakco.backend.fixture.group.TestGroupFactory;
import com.amorgakco.backend.fixture.member.TestMemberFactory;
import com.amorgakco.backend.group.domain.Group;
import com.amorgakco.backend.group.dto.LocationVerificationRequest;
import com.amorgakco.backend.group.repository.GroupRepository;
import com.amorgakco.backend.member.domain.Member;
import com.amorgakco.backend.member.repository.MemberRepository;
import com.amorgakco.backend.notification.infrastructure.NotificationPublisherFacade;
import com.amorgakco.backend.notification.infrastructure.consumer.NotificationRequest;
import com.amorgakco.backend.participant.domain.LocationVerificationStatus;
import com.amorgakco.backend.participant.domain.Participant;
import com.amorgakco.backend.participant.dto.ParticipationHistoryPagingResponse;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@Transactional
class ParticipantServiceTest {
    @Autowired
    private ParticipantService participantService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private GroupRepository groupRepository;
    @MockBean
    private NotificationPublisherFacade notificationPublisherFacade;

    @Test
    @DisplayName("참여자는 모각코를 탈퇴할 수 있다.")
    void withdrawGroup() {
        // given
        Member host = TestMemberFactory.createEntity();
        Member member = TestMemberFactory.createEntity();
        memberRepository.save(member);
        memberRepository.save(host);
        Group group = TestGroupFactory.createActiveGroup(host);
        group.addParticipants(new Participant(member));
        groupRepository.save(group);
        // when
        doNothing().when(notificationPublisherFacade).send(any(NotificationRequest.class));
        participantService.withdraw(group.getId(), member.getId());
        // then
        assertThat(!group.getParticipants().contains(member)).isTrue();
    }

    @Test
    @DisplayName("위치를 인증할 수 있다.")
    void verifyParticipantLocation() {
        // given
        double currentLatitude = 37.57060;
        double currentLongitude = 126.9754;
        Member host = TestMemberFactory.createEntity();
        Member member = TestMemberFactory.createEntity();
        memberRepository.save(member);
        memberRepository.save(host);
        Group group = TestGroupFactory.createActiveGroup(host);
        group.addParticipants(new Participant(member));
        Group savedGroup = groupRepository.save(group);
        LocationVerificationRequest request = new LocationVerificationRequest(savedGroup.getId(), currentLatitude, currentLongitude);
        // when
        participantService.verifyParticipantLocation(request, member.getId());
        // then
        Participant participant = participantService.getParticipant(savedGroup.getId(), member.getId());
        assertThat(participant.getLocationVerificationStatus()).isEqualTo(LocationVerificationStatus.VERIFIED);
    }

    @Test
    @DisplayName("현재 참여중인 그룹 내역을 조회할 수 있다.")
    void currentParticipationHistories() {
        // given
        int currentParticipationGroupSize = 4;
        int pastParticipationGroupSize = 6;
        Member host = TestMemberFactory.createEntity();
        Member member = TestMemberFactory.createEntity();
        memberRepository.save(member);
        memberRepository.save(host);
        createGroupsAndParticipate(currentParticipationGroupSize, pastParticipationGroupSize, host, member);
        // when
        ParticipationHistoryPagingResponse currentParticipationHistories =
                participantService.getCurrentParticipationHistories(member.getId(), 0);
        // then
        assertThat(currentParticipationHistories.histories().size()).isEqualTo(currentParticipationGroupSize);
    }

    @Test
    @DisplayName("과거에 참여했던 그룹 내역을 조회할 수 있다.")
    void pastParticipationHistories() {
        // given
        int currentParticipationGroupSize = 4;
        int pastParticipationGroupSize = 6;
        Member host = TestMemberFactory.createEntity();
        Member member = TestMemberFactory.createEntity();
        memberRepository.save(member);
        memberRepository.save(host);
        createGroupsAndParticipate(currentParticipationGroupSize, pastParticipationGroupSize, host, member);
        // when
        ParticipationHistoryPagingResponse pastParticipationHistories =
                participantService.getPastParticipationHistories(member.getId(), 0);
        // then
        assertThat(pastParticipationHistories.histories().size()).isEqualTo(pastParticipationGroupSize);
    }

    private void createGroupsAndParticipate(int currentGroupSize, int pastGroupSize, Member host, Member member) {
        for (int i = 0; i < currentGroupSize; i++) {
            final Group activeGroup = TestGroupFactory.createActiveGroup(host);
            activeGroup.addParticipants(new Participant(member));
            groupRepository.save(activeGroup);
        }

        for (int i = 0; i < pastGroupSize; i++) {
            final Group inactiveGroup = TestGroupFactory.createInactiveGroup(host);
            inactiveGroup.addParticipants(new Participant(member));
            groupRepository.save(inactiveGroup);
        }
    }
}
