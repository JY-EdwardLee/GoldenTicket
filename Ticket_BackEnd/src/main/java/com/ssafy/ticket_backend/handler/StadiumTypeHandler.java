package com.ssafy.ticket_backend.handler;

import com.ssafy.ticket_backend.model.Stadium;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(Stadium.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class StadiumTypeHandler extends BaseTypeHandler<Stadium> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Stadium parameter,
        JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public Stadium getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value != null ? Stadium.valueOf(value) : null;
    }

    @Override
    public Stadium getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value != null ? Stadium.valueOf(value) : null;
    }

    @Override
    public Stadium getNullableResult(CallableStatement cs, int columnIndex)
        throws SQLException {
        String value = cs.getString(columnIndex);
        return value != null ? Stadium.valueOf(value) : null;
    }
} 