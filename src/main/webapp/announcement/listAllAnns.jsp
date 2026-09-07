<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>List All Announcements</title>
</head>
<body>
	<h1>公告列表</h1>
	<table style="width: 50%; text-align: center;">
		<tr>
			<th>公告編號</th>
			<th>公告標題</th>
			<th>公告時間</th>
			<th>推播圖</th>
			<th>文字內容</th>
		</tr>
		<c:forEach var="ann" items="${annList}">
			<tr>
				<td>${ann.annNo}</td>
				<td>${ann.annTitle}</td>
				<td>${ann.annBegin}</td>
				<td><c:if test="${not empty ann.annImg}">
						<img
							src="${pageContext.request.contextPath}/ann/ann.img?annNo=${ann.annNo}"
							alt="${ann.annTitle}">
					</c:if></td>
				<td>${ann.annText}</td>
				<td>
					<form action="${pageContext.request.contextPath}/ann/ann.do"
						method="post">
						<input type="hidden" name="action" value="updateAnnPage">
						<input type="hidden" name="annNo" value="${ann.annNo}">
						<button type="submit">修改公告</button>
					</form>
				</td>
				<td>
					<form action="${pageContext.request.contextPath}/ann/ann.do"
						method="post">
						<input type="hidden" name="action" value="deleteAnn">
						<input type="hidden" name="annNo" value="${ann.annNo}">
						<button type="submit">刪除公告</button>
					</form>
				</td>
			</tr>
		</c:forEach>
	</table>

	<br>
	<br>
	<br>

	<a href="${pageContext.request.contextPath}/announcement/index.jsp">回首頁</a>
</body>
</html>