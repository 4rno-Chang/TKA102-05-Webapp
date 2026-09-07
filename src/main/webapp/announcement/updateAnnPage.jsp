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
	<h1>修改公告</h1>
	<form action="${pageContext.request.contextPath}/ann/ann.do"
		method="post" enctype="multipart/form-data">
		<input type="hidden" name="action" value="updateAnn"> <input
			type="hidden" name="annNo" value="${ann.annNo}">

		<div>
			<label for="annTitle">公告標題：</label> <input type="text" id="annTitle"
				name="annTitle" value="${ann.annTitle}" required>
		</div>

		<div>
			<label for="annBegin">公告開始時間：</label> <input type="datetime-local"
				id="annBegin" name="annBegin" value="${ann.annBegin}" required>
		</div>

		<div>
			<label>目前公告圖片：</label>

			<c:if test="${not empty ann.annImg}">
				<img
					src="${pageContext.request.contextPath}/ann/annImage.do?annNo=${ann.annNo}"
					alt="${ann.annTitle}" width="200">
			</c:if>
		</div>
		<br>
		<div>
			<label for="annImg">更換公告圖片：</label> <input type="file" id="annImg"
				name="annImg" accept="image/*">
		</div>
		<div>
			<label for="annText">公告內容：</label>

			<textarea id="annText" name="annText" rows="8" cols="50" required>${ann.annText}</textarea>
		</div>

		<button type="submit">確認修改</button>
	</form>
	<br>
	<br>
	<br>

	<a href="${pageContext.request.contextPath}/announcement/index.jsp">回首頁</a>
</body>
</html>