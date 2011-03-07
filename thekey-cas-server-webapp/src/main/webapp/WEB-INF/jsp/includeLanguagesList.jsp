<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ul id="languagesList">
  	<c:forEach var="language" items="${languagelist}" >
  		<c:choose>
  			<c:when test="${currentlocale == language.key}">
  			 <li selected>
  			</c:when>
  			<c:otherwise>
  				<li>
  			</c:otherwise>
  		</c:choose>
        <a href="login.htm?locale=<c:out value="${language.key}"/>"><span><c:out value="${language.value}"/></span></a></li>
	</c:forEach>  	
</ul>