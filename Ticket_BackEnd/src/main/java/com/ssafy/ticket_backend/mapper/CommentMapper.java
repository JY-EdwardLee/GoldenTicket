package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.dto.request.CommentRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {

    int insertComment(CommentRequest commentRequest);
}
