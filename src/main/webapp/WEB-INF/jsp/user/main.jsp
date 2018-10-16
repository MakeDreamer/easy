<%--
  Created by IntelliJ IDEA.
  User: YuMing_Huai
  Date: 2018/8/25
  Time: 16:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<%=request.getAttribute("message") %>


<h1>超级管理员专区</h1>
<a href="/user/superControl">超管Controller</a><br>
<a href="/user/superList">超管List</a><br>


<h1>管理员专区</h1>
<a href="/user/adminControl">AdminController</a><br>
<a href="/user/adminList">AdminList</a><br>


<h1>普通用户专区</h1>
<a href="/user/userControl">UserController</a><br>
<a href="/user/userInfo">UserInfo</a><br>

</body>
</html>
