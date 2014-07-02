<%@ page pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

											</div> <!-- .mainContent-in -->
										</div> <!-- .mainContent -->

										<div class="minorContent">
											<div class="minorContent-in">
												<div class="logo-wrap"><img class="logo" src="<c:out value="${logoUri}" />" alt="The Key Logo" /></div>

												<c:if test="${showMinorNav}">
													<div class="menu_minorContent-wrap">
														<ul class="menu_minorContent">
															<li><a href="<c:out value="${signupUri}" />"><span><spring:message code="login.noaccountA" /></span></a></li>
															<li><a href="<c:out value="${accountDetailsUri}" />"><span><spring:message code="login.canalsoA" /></span></a></li>
														</ul> <!-- .menu_minorContent -->
													</div> <!-- .menu_minorContent-wrap -->
												</c:if>

											</div> <!-- .minorContent-in -->
										</div> <!-- .minorContent -->

									</div> <!-- .content_body-in -->
								</div> <!-- .content_body -->

							</div> <!-- .content-in -->
						</div> <!-- .content -->

						<c:if test="${showLanguages}">
							<%@ include file="languagesList.jsp" %>
						</c:if>

						<!-- extra_footer is for potential client css style needs -->
						<div class="extra_footer1"></div>
						<div class="extra_footer2"></div>

					</div> <!-- .main-in3 -->
				</div> <!-- .main-in2 -->
			</div> <!-- .main-in1 -->
		</div> <!-- .main -->

		<!-- extra boxes for potential client css style needs -->
		<div class="extra1"></div>
		<div class="extra2"></div>
		<div class="extra3"></div>
		<div class="extra4"></div>
	</body>
</html>
