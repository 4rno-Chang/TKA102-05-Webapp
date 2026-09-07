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
	<h1>新增公告</h1>
	<form action="${pageContext.request.contextPath}/ann/ann.do"
		method="post" enctype="multipart/form-data">
		<input type="hidden" name="action" value="insertAnn">

		<div>
			<label for="annTitle">公告標題：</label> <input type="text" id="annTitle"
				name="annTitle" required>
		</div>
		<br>

		<div>
			<label for="annBegin">公告開始時間：</label> <input type="datetime-local"
				id="annBegin" name="annBegin" required>
		</div>
		<br>

		<div>
			<label for="annImg">公告圖片：</label> <input type="file" id="annImg"
				name="annImg" accept="image/*">
		</div>
		<br>

		<div>
			<label for="annText">公告內容：</label>
			<textarea id="annText" name="annText" rows="8" cols="50" required></textarea>
		</div>
		<br>
		<button type="submit">新增公告</button>

	</form>
	<br>
	<br>
	<br>

	<a href="${pageContext.request.contextPath}/announcement/index.jsp">回首頁</a>
</body>
</html>