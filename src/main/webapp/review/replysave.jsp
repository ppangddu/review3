<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<% request.setCharacterEncoding("utf-8"); %>
<%@page import="pack.review.ReviewBean" %>
<%@page import="pack.review.ReviewDao" %>
<%@page import="pack.review.ReviewDto" %>
<%@page import="jakarta.servlet.http.Cookie" %>
<%@page import="pack.cookie.CookieManager" %>

<%
  String bpage = request.getParameter("page");

  ReviewBean bean = new ReviewBean();
  ReviewDao reviewDao = new ReviewDao();

  int movieId = Integer.parseInt(request.getParameter("movieId"));
  boolean isReply = (request.getParameter("num") != null);
  int parentOnum = Integer.parseInt(request.getParameter("onum"));
  int parentNested = Integer.parseInt(request.getParameter("nested"));

  if (isReply) {
    int newNested = parentNested >= 2 ? 2 : parentNested + 1;
    bean.setOnum(parentOnum);
    bean.setNested(newNested);
  } else {
    bean.setOnum(0);
    bean.setNested(1);
  }

  bean.setMovieId(movieId);
  bean.setCont(request.getParameter("cont"));
  bean.setUserId((String) session.getAttribute("user_id"));

  String cont = bean.getCont();
  String[] badWords = {"ㅅㅂ", "tq"};
  for (String word : badWords) {
    if (cont != null && cont.contains(word)) {
      out.println("<script>alert('금지된 단어가 포함되어 있습니다.'); history.back();</script>");
      return;
    }
  }

  int rating = 0;
  if (!isReply) {
    try {
      rating = Integer.parseInt(request.getParameter("rating"));
    } catch (Exception e) { rating = 0; }
  }
  bean.setRating(rating);

  int insertedNum = reviewDao.saveReplyData(bean);

  if (!isReply) {
    reviewDao.updateReviewOnum(insertedNum, insertedNum);
  }

  CookieManager cm = CookieManager.getInstance();
  String contName = isReply ? "cont_reply" : "cont_review";
  String ratingName = isReply ? "rating_reply" : "rating_review";

  response.addCookie(cm.deleteCookie(contName));
  response.addCookie(cm.deleteCookie(ratingName));

  response.sendRedirect("reviewcontent.jsp?movieId=" + movieId + "&page=" + bpage);
%>
