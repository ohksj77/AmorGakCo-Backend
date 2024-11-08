package com.amorgakco.backend.fixture.groupparticipation;

import com.amorgakco.backend.fixture.group.TestGroupFactory;
import com.amorgakco.backend.fixture.member.TestMemberFactory;
import com.amorgakco.backend.group.domain.Group;
import com.amorgakco.backend.groupapplication.domain.GroupApplication;
import com.amorgakco.backend.member.domain.Member;

public class TestGroupParticipationFactory {

    public static GroupApplication create(final Member host, final Long memberId) {
        final Member member = TestMemberFactory.create(memberId);
        final Group group = TestGroupFactory.createActiveGroup(host);
        return GroupApplication.builder().group(group).member(member).build();
    }
}
