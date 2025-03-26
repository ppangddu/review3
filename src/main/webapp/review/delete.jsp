<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%
  String num = request.getParameter("num");
  String bpage = request.getParameter("page");
  String adminOk = (String) session.getAttribute("adminOk");
  boolean isAdmin = "admin".equals(adminOk);
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>글 삭제</title>
  <link rel="stylesheet" type="text/css" href="../css/board.css">
  <script type="text/javascript">
    function check() {
      <% if (!isAdmin) { %>
      if (frm.pass.value === "") {
        alert("비밀번호를 입력하세요.");
        frm.pass.focus();
        return;
      }
      <% } %>

      if (confirm("정말 삭제할까요?")) {
        frm.submit();
      }
    }
  </script>
</head>
<body>
<h2>글 삭제</h2>

<form action="deleteok.jsp" name="frm" method="get">
  <input type="hidden" name="num" value="<%=num %>">
  <input type="hidden" name="page" value="<%=bpage %>">

  <% if (isAdmin) { %>
  <p style="color: red;">⚠️ 관리자 권한으로 삭제됩니다.</p>
  <input type="hidden" name="pass" value="adminpass"> <%-- dummy 값 --%>
  <% } else { %>
  비밀번호 : <input type="text" name="pass">
  <% } %>

  <br><br>
  <input type="button" onclick="check()" value="삭제확인">
  <input type="button" value="목록보기" onclick="location.href='reviewlist.jsp?page=<%=bpage %>'">
</form>
</body>
</html>
