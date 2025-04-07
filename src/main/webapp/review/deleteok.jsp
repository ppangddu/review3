<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="pack.movie.MovieDao" %>

<%
    request.setCharacterEncoding("UTF-8");

    int id = Integer.parseInt(request.getParameter("id"));
    String bpage = request.getParameter("page");

    MovieDao dao = new MovieDao();
    dao.deleteMovie(id);

    response.sendRedirect("movielist.jsp?page=" + bpage);
%>
