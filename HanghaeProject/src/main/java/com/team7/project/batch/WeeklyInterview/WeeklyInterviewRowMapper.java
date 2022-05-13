package com.team7.project.batch.WeeklyInterview;

import com.team7.project.interview.model.Interview;
import com.team7.project.weeklyInterview.model.WeeklyInterview;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WeeklyInterviewRowMapper implements RowMapper<WeeklyInterview> {

    public static final String user_id = "user_id";
    public static final String NAME_COLUMN = "name";
    public static final String CREDIT_COLUMN = "credit";

    @Override
    public WeeklyInterview mapRow(ResultSet rs, int rowNum) throws SQLException {
        return null;
    }

//    public Interview mapRow(ResultSet rs, int rowNum) throws SQLException {
//        WeeklyInterview weeklyInterview = new WeeklyInterview();
//
//        weeklyInterview.setUser(rs.getLong(user_id));
//        weeklyInterview.setName(rs.getString(NAME_COLUMN));
//        weeklyInterview.setCredit(rs.getBigDecimal(CREDIT_COLUMN));
//
//        return customerCredit;
//    }
}