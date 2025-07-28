package com.ssafy.ticket_backend.handler;

import com.ssafy.ticket_backend.model.BoardType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(BoardType.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class BoardTypeHandler extends BaseTypeHandler<BoardType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BoardType parameter,
        JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public BoardType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value != null ? BoardType.valueOf(value) : null;
    }

    @Override
    public BoardType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value != null ? BoardType.valueOf(value) : null;
    }

    @Override
    public BoardType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value != null ? BoardType.valueOf(value) : null;
    }
}