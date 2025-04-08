<%@ page import="pack.review.ReviewDao" %>
<%@ page language="java" contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String numStr = request.getParameter("num");
    String userId = (String) session.getAttribute("user_id");

    if (userId == null) {
        out.print("unauthorized");
        return;
    }

    if (numStr != null) {
        int num = Integer.parseInt(numStr);
        String likeKey = "liked_" + num;

        ReviewDao reviewDao = new ReviewDao();
        Boolean alreadyLiked = (Boolean) session.getAttribute(likeKey);
        int newCount;

        if (alreadyLiked != null && alreadyLiked) {
            reviewDao.decreaseLikeCount(num);
            session.removeAttribute(likeKey);
            newCount = reviewDao.getLikeCount(num);  // 좋아요 수 다시 조회
            out.print("unliked:" + newCount);
        } else {
            reviewDao.increaseLikeCount(num);
            session.setAttribute(likeKey, true);
            newCount = reviewDao.getLikeCount(num);  // 좋아요 수 다시 조회
            out.print("liked:" + newCount);
        }
    } else {
        out.print("error");
    }
%>
