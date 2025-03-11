package org.zerock.b01.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.zerock.b01.domain.Board;

@Mapper
public interface BoardMapper {
    Long insert(Board board);
}
