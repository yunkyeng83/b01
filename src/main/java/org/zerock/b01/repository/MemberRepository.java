package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    // mid값을 가진 member정보와 role의 정보를 모두 조회
    @EntityGraph(attributePaths = {"roleSet"})
    @Query("select m from Member m where m.mid = :mid and m.social = false")
    Optional<Member> getWithRoles(@Param("mid") String mid);

    @EntityGraph(attributePaths = {"roleSet"})
    @Query("select m from Member m where m.mid = :mid and m.social = true")
    Optional<Member> getWithRolesIsSocial(@Param("mid") String mid);

    @EntityGraph(attributePaths = {"roleSet"})
    Optional<Member> findByEmail(String email);
}
