<%@page import="org.apache.ibatis.reflection.SystemMetaObject"%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*, java.util.ArrayList, com.google.gson.Gson" %>

<%
    // MariaDB 연결 정보
    String dbUrl = "jdbc:mariadb://localhost:3306/cinemap"; // DB URL
    String dbUser = "root"; // DB 사용자 이름
    String dbPassword = "123"; // DB 비밀번호

    ArrayList<String> titles = new ArrayList<>();

    try {
        // JDBC 드라이버 로드
        Class.forName("org.mariadb.jdbc.Driver"); // MariaDB JDBC 드라이버

        // 데이터베이스 연결
        Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

        // SQL 쿼리 실행
        String term = request.getParameter("term");
        if (term == null) term = "";
        String sql = "SELECT title FROM movie WHERE title LIKE ? LIMIT 3";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "%" + term + "%");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            titles.add(rs.getString(1));
        }

        rs.close();
        stmt.close();
        conn.close();
    } catch (Exception e) {
       
    }

    Gson gson = new Gson();
    out.print(gson.toJson(titles));
%>