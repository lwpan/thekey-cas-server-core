<%@ page contentType="text/html; charset=UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:theme text="" />
<%final String queryString = request.getQueryString() == null ? "" : request.getQueryString().replaceAll("&locale=([A-Za-z][A-Za-z]_)?[A-Za-z][A-Za-z]|^locale=([A-Za-z][A-Za-z]_)?[A-Za-z][A-Za-z]", "").replaceAll("^&", "");%>
<c:set var='query' value='<%=queryString%>' />
<c:set var="baseUrl" value="?${query}${not empty query ? '&' : ''}locale=" />
<ul id="languagesList">
	<c:forEach var="language" items="${languages}">
		<li <c:if test="${language.key == locale}">class="selected"</c:if>>
			<a href="<c:out value="${baseUrl}${language.key}"/>"><span><c:out value="${language.value}"/></span></a>
		</li>
	</c:forEach>
</ul>
