package com.ssafy.ticket_backend.handler;

import com.ssafy.ticket_backend.model.UserGender;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(UserGender.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class UserGenderTypeHandler extends BaseTypeHandler<UserGender> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UserGender parameter,
        JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name()); // DB에 저장할 값
    }

    @Override
    public UserGender getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value != null ? UserGender.valueOf(value) : null;
    }

    @Override
    public UserGender getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value != null ? UserGender.valueOf(value) : null;
    }

    @Override
    public UserGender getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value != null ? UserGender.valueOf(value) : null;
    }
}