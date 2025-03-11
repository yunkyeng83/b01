package org.zerock.b01.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.Board;
import org.zerock.b01.repository.search.BoardSearch;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {
    // JPA Query Method ----------------------------------------------------
    List<Board> findByTitleAndWriter(String title, String writer);
    List<Board> findByWriterIn(List<String> writers);
    Page<Board> findByTitleContainingOrderByBnoDesc(String keyword, Pageable pageable);

    // JPA @Query Method -> JPQL
    @Query("select b from Board b where title = :title and writer = :writer")
    List<Board> findFromTitleWriter(@Param("title") String title, @Param("writer")  String writer);
    @Query("select b from Board b where writer in :writers")
    List<Board> findFromWriters(@Param("writers") List<String> writers);
    @Query("select b from Board b where b.title like concat('%', :keyword, '%')")
    Page<Board> findKeyword(@Param("keyword") String keyword, Pageable pageable); // findByTitleContainingOrderByBnoDesc

    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select b from Board b where b.bno = :bno")
    Optional<Board> findByIdWithImages(Long bno);
}
