package com.amorgakco.backend.participant.repository;

import com.amorgakco.backend.member.domain.Member;
import com.amorgakco.backend.participant.domain.Participant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    @Query("select p from Participant p join fetch p.group where p.member.id = :memberId and p.group.duration.endAt >= :now")
    Slice<Participant> findCurrentParticipationByMember(Long memberId, LocalDateTime now, Pageable pageable);

    @Query("select p from Participant p join fetch p.group where p.member.id = :memberId and p.group.duration.endAt < :now")
    Slice<Participant> findPastParticipationByMember(Long memberId, LocalDateTime now, Pageable pageable);

    @Query(
            "select p from Participant p join fetch p.group join fetch p.member where p.group.id = :groupId and p.member.id = :memberId")
    Optional<Participant> findByGroupAndMember(final Long groupId, final Long memberId);

    @Query("select count(*) from Participant p where p.member = :member and p.group.duration.endAt > :now")
    Integer countCurrentParticipationByMember(Member member, LocalDateTime now);
}
