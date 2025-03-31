<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="pack.movie.MovieBean" %>
<%@ page import="pack.movie.MovieManager" %>

<%
    request.setCharacterEncoding("UTF-8");

    MovieBean bean = new MovieBean();
    MovieManager manager = new MovieManager();

    bean.setTitle(request.getParameter("title"));
    bean.setGenre(request.getParameter("genre"));
    bean.setActorName(request.getParameter("actorName"));  // 출연
    bean.setDescription(request.getParameter("description"));
    bean.setReleaseDate(request.getParameter("releaseDate"));
    bean.setImageUrl(request.getParameter("imageUrl"));

    manager.saveMovie(bean);

    response.sendRedirect("movielist.jsp");
%>
