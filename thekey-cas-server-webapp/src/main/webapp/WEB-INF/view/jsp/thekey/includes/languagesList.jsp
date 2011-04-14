<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<ul id="languagesList">
	<c:forEach var="language" items="${languages}">
		<li <c:if test="${language.key == locale}">class="selected"</c:if>>
			<a href="login?locale=<c:out value="${language.key}"/>"><span><c:out value="${language.value}"/></span></a>
		</li>
	</c:forEach>
</ul>
