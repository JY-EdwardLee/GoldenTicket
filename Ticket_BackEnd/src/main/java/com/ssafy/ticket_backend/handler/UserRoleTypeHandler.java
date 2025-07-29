package com.ssafy.ticket_backend.handler;

import com.ssafy.ticket_backend.model.UserRole;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(UserRole.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class UserRoleTypeHandler extends BaseTypeHandler<UserRole> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UserRole parameter,
        JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public UserRole getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value != null ? UserRole.valueOf(value) : null;
    }

    @Override
    public UserRole getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value != null ? UserRole.valueOf(value) : null;
    }

    @Override
    public UserRole getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value != null ? UserRole.valueOf(value) : null;
    }
}