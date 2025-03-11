package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.Todo;

public interface TodoRepository extends JpaRepository<Todo, Integer> {
}
