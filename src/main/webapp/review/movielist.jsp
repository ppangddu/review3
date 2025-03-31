<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="pack.movie.MovieDto" %>
<%@ page import="pack.movie.MovieManager" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    int bpage = 1;
    try {
        bpage = Integer.parseInt(request.getParameter("page"));
    } catch(Exception e) { bpage = 1; }

    if (bpage <= 0) bpage = 1;

    String searchType = request.getParameter("searchType");
    String searchWord = request.getParameter("searchWord");

    MovieManager movieManager = new MovieManager();
    movieManager.totalList();
    int pageSu = movieManager.getPageSu();
    int totalRecord = movieManager.getTotalRecordCount();

    List<MovieDto> list = movieManager.getDataAll(bpage, searchType, searchWord);

    request.setAttribute("list", list);
    request.setAttribute("bpage", bpage);
    request.setAttribute("pageSu", pageSu);
    request.setAttribute("totalRecord", totalRecord);
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>영화 목록</title>
    <link rel="stylesheet" type="text/css" href="../css/board.css">
    <script>
        window.onpageshow = function(event) {
            if (event.persisted || (window.performance && window.performance.navigation.type === 2)) {
                location.reload(true);
            }
        };

        window.onload = () => {
            document.querySelector("#btnSearch").onclick = function() {
                if (frm.searchWord.value === "") {
                    frm.searchWord.placeholder = "검색어를 입력하세요.";
                    return;
                }
                frm.submit();
            }
        };
    </script>
</head>
<body>
<table>
    <tr>
        <td>
            [ <a href="../index.html">메인으로</a> ]&nbsp;
            [ <a href="movielist.jsp?page=1">최근목록</a> ]&nbsp;
            [ <a href="moviewrite.jsp">영화등록</a> ]&nbsp;

            <br><br>

            <table style="width: 100%">
                <tr style="background-color: cyan;">
                    <th>번호</th><th>영화</th><th>장르</th><th>출연</th><th>개봉일</th>
                </tr>
                <c:forEach var="dto" items="${list}" varStatus="status">
                    <tr>
                        <td>${dto.id}</td>
                        <td>
                            <a href="reviewcontent.jsp?id=${dto.id}">
                                <img src="${dto.imageUrl}" alt="영화 포스터"
                                     style="width:120px;height:180px;vertical-align:middle;margin-right:10px;border-radius:6px;">
                                    ${dto.title}
                            </a>
                        </td>
                        <td>${dto.genre}</td>
                        <td>${dto.actorName}</td>
                        <td>${dto.releaseDate}</td>
                    </tr>
                </c:forEach>
            </table>

            <br>
            <table style="width: 100%">
                <tr>
                    <td style="text-align: center;">
                        <c:forEach begin="1" end="${pageSu}" var="i">
                            <c:choose>
                                <c:when test="${i == bpage}">
                                    <b style="font-size:12pt;color:blue">[${i}]</b>
                                </c:when>
                                <c:otherwise>
                                    <a href="movielist.jsp?page=${i}">[${i}]</a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                        <br><br>
                        <form action="movielist.jsp" name="frm" method="post">
                            <select name="searchType">
                                <option value="title" selected="selected">영화제목</option>
                                <option value="actorName">출연</option>
                            </select>
                            <input type="text" name="searchWord">
                            <input type="button" value="검색" id="btnSearch">
                        </form>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>
