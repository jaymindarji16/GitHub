<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<html>
<head>
	<title>User Login</title>
	<style type="text/css">
		.tg  {border-collapse:collapse;border-spacing:0;border-color:#ccc;}
		.tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#ccc;color:#333;background-color:#fff;}
		.tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#ccc;color:#333;background-color:#f0f0f0;}
		.tg .tg-4eph{background-color:#f9f9f9}
	</style>
</head>
<body>
<h1>
	User Login
</h1>

<c:url var="addAction" value="/user_login/login" ></c:url>

<form:form action="${addAction}">
 <input type="hidden" id="lockCounter" name="lockCounter" value="${lockCounter}">
	
	<c:if test="${!empty faliureMessage}">  
		<p>"${faliureMessage}"</p>
	</c:if>
	
	<table>
		<tr>
			<td>
				User name:
			</td>
			<td>
				<input type="text" name="username">
			</td> 
		</tr>
		
		<tr>
			<td>
				Password:
			</td>
			<td>
				<input type="text" name="password">
			</td> 
		</tr>
		
		<tr>
			<td>
				<input type="submit" value="Login">
			</td>
		</tr>
	</table>	
</form:form>

</body>
</html>
