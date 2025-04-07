<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="pack.movie.MovieDto"%>
<%@page import="pack.movie.MovieDao"%>

<%
    request.setCharacterEncoding("UTF-8");

    int id = Integer.parseInt(request.getParameter("id"));
    String bpage = request.getParameter("page");

    MovieDto movie = new MovieDto();
    movie.setId(id);
    movie.setTitle(request.getParameter("title"));
    movie.setGenre(request.getParameter("genre"));
    movie.setActorName(request.getParameter("actorName"));
    movie.setDescription(request.getParameter("description"));
    movie.setReleaseDate(request.getParameter("releaseDate"));
    movie.setImageUrl(request.getParameter("imageUrl"));

    MovieDao dao = new MovieDao();
    dao.updateMovie(movie);  // MyBatis 방식

    response.sendRedirect("movielist.jsp?page=" + bpage);
%>
