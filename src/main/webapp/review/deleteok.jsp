<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<jsp:useBean id="bean" class="pack.review.ReviewBean" />
<jsp:setProperty property="*" name="bean" /> <!-- num도 같이 들어옴 -->
<jsp:useBean id="reviewManager" class="pack.review.ReviewManager" />

<%
    String num = request.getParameter("num");
    String bpage = request.getParameter("page");
    String pass = request.getParameter("pass");

    String adminOk = (String) session.getAttribute("adminOk");
    boolean isAdmin = "admin".equals(adminOk);

    boolean b;

    if (isAdmin) {
        b = true; // 관리자면 비번 체크 생략
    } else {
        b = reviewManager.checkPassword(Integer.parseInt(num), pass); // 일반 사용자 비번 체크
    }

    if (b) {
        reviewManager.delData(num);
        response.sendRedirect("reviewlist.jsp?page=" + bpage);
    } else {
%>
<script>
    alert("비밀번호 불일치");
    history.back();
</script>
<%
    }
%>