<%@ page contentType="text/html; charset=UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%final String queryString = request.getQueryString() == null ? "" : request.getQueryString().replaceAll("(?:^|&)locale=[A-Za-z_]*(?:$|&)", "&").replaceAll("(?:^&|&$)", "");%>
<c:set var='query' value='<%=queryString%>' />
<c:set var="baseUrl" value="?${query}${not empty query ? '&' : ''}locale=" />
<div class="languagesList-wrap">
	<ul id="languagesList" dir="ltr">
		<c:forEach var="language" items="${languages}">
			<li <c:if test="${language.key.equals(locale)}">class="selected"</c:if>>
				<a href="<c:out value="${baseUrl}${language.key}"/>"><span><c:out value="${language.value}"/></span></a>
			</li>
		</c:forEach>
	</ul>
</div> <!-- .languagesList-wrap -->
