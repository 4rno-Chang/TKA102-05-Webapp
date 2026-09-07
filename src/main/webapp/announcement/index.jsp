<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>公告設定</title> 
</head>
<body>
	<h1>公告設定</h1>
	<c:if test="${not empty errorMsg}">
		<p style="color: red;">${errorMsg}</p>
	</c:if>

	<a href="${pageContext.request.contextPath}/ann/ann.do?action=getAll">查看所有公告</a>
	<br>
	<br>
	<form action="${pageContext.request.contextPath}/ann/ann.do"
		method="post">
		<p>
			<label>公告編號查詢：</label>
		</p>
		<input type="text" name="annNo"> <input type="hidden"
			name="action" value="annNoQuery">
		<p>
			<input type="submit" value="查詢">
		</p>
	</form>
	<a
		href="${pageContext.request.contextPath}/ann/ann.do?action=insertAnnPage">新增公告</a>
	<br>
	<br>

</body>
</html>