package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.model.Game;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CrawlMapper {

    void insertGame(Game game);
}
