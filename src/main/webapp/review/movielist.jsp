<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="pack.movie.MovieDto" %>
<%@ page import="pack.movie.MovieDao" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    int bpage = 1;
    int pageList = 5;
    try {
        bpage = Integer.parseInt(request.getParameter("page"));
    } catch(Exception e) {
        bpage = 1;
    }
    if (bpage <= 0) bpage = 1;

    String searchWord = request.getParameter("searchWord");
    if (searchWord == null) searchWord = "";

    MovieDao dao = new MovieDao();

    int totalRecord = dao.getMovieCount(searchWord);
    int pageSu = totalRecord / pageList + (totalRecord % pageList > 0 ? 1 : 0);
    if (pageSu == 0) pageSu = 1;

    int start = (bpage - 1) * pageList;

    List<MovieDto> list = dao.getPagedMovies(start, pageList, searchWord);

    request.setAttribute("list", list);
    request.setAttribute("bpage", bpage);
    request.setAttribute("pageSu", pageSu);
    request.setAttribute("totalRecord", totalRecord);
    request.setAttribute("searchWord", searchWord);
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>과제 목록</title>
    <script>
        window.onload = () => {
            document.querySelector("#btnSearch").onclick = function () {
                const word = document.frm.searchWord.value.trim();
                if (word === "") {
                    document.frm.searchWord.placeholder = "검색어를 입력하세요.";
                }
                document.frm.submit();
            }
        }
    </script>
</head>
<body>

<div>
    [ <a href="../index.html">메인으로</a> ]
    [ <a href="movielist.jsp?page=1">최근목록</a> ]
    [ <a href="moviewrite.jsp">새글작성</a> ]
    [ <a href="#" onclick="window.open('admin.jsp','','width=500,height=300,top=200,left=300')">관리자용</a> ]
</div>

<hr>

<div>
    <c:forEach var="movie" items="${list}">
        <div>
            <a href='reviewcontent.jsp?movieId=${movie.id}&page=${bpage}'>
                <img src="${movie.imageUrl}" alt="영화 포스터" width="150"><br>
                <strong>${movie.title}</strong><br>
                장르: ${movie.genre}<br>
                출연: ${movie.actorName}<br>
                개봉일: ${movie.releaseDate}
            </a>
        </div>
        <hr>
    </c:forEach>
</div>

<div>
    <c:forEach begin="1" end="${pageSu}" var="i">
        <c:choose>
            <c:when test="${i == bpage}">
                <b>[${i}]</b>
            </c:when>
            <c:otherwise>
                <a href="movielist.jsp?page=${i}&searchWord=${searchWord}">[${i}]</a>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</div>

<hr>

<div>
    <form action="movielist.jsp" name="frm" method="post">
        <input type="text" name="searchWord" placeholder="영화 제목 입력" value="${searchWord}">
        <input type="button" value="검색" id="btnSearch">
    </form>
</div>

</body>
</html>
