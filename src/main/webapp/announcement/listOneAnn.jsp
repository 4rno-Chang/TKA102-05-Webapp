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
	<h1>公告查詢結果</h1>
	<c:if test="${empty ann}">
		<p>查無資料</p>
	</c:if>

	<c:if test="${not empty ann}">
		<table style="width: 50%; text-align: center;">
			<tr>
				<th>公告編號</th>
				<th>公告標題</th>
				<th>公告時間</th>
				<th>推播圖</th>
				<th>文字內容</th>
			</tr>
			<tr>
				<td>${ann.annNo}</td>
				<td>${ann.annTitle}</td>
				<td>${ann.annDateTimeFormat}</td>
				<td><c:if test="${not empty ann.annImg}">
						<img
							src="${pageContext.request.contextPath}/ann/ann.img?annNo=${ann.annNo}"
							alt="${ann.annTitle}">
					</c:if></td>
				<td>${ann.annText}</td>
			</tr>
		</table>
	</c:if>

	<br>
	<br>
	<br>

	<a href="${pageContext.request.contextPath}/announcement/index.jsp">回首頁</a>
</body>
</html>