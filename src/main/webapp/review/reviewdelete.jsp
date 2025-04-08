<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="pack.review.ReviewDao" %>
<%@ page import="pack.review.ReviewDto" %>

<%
    request.setCharacterEncoding("UTF-8");

    // 테스트용 세션 세팅
    if (session.getAttribute("user_id") == null) {
        session.setAttribute("user_id", "testuser");
        session.setAttribute("nickname", "haruka");
    }

    String numStr = request.getParameter("num");
    String movieId = request.getParameter("movieId");
    String bpage = request.getParameter("page");

    if (numStr == null) {
        response.sendRedirect("movielist.jsp");
        return;
    }

    int num = Integer.parseInt(numStr);
    String userId = (String) session.getAttribute("user_id");

    ReviewDao reviewDao = new ReviewDao();
    ReviewDto target = reviewDao.getReplyData(num);

    if (target == null || !userId.equals(target.getUserId())) {
        out.println("<script>alert('삭제 권한이 없습니다.'); history.back();</script>");
        return;
    }

    if (target.getNested() == 1) {
        reviewDao.deleteCascade(target.getGnum(), target.getNested());
    } else {
        reviewDao.deleteByNum(num);
    }

    response.sendRedirect("reviewcontent.jsp?movieId=" + movieId + "&page=" + bpage);
%>
