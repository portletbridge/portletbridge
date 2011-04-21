<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Pluto Portal</title>
    <style type="text/css" title="currentStyle" media="screen">
        @import "<c:out value="${pageContext.request.contextPath}"/>/portlet-spec-1.0.css";
    </style>
</head>
<body>
<div id="portal">
    <div id="header">
        <h1>Apache Pluto</h1>
        <p>An Apache Portals Project</p>
    </div>
    <!-- Content block: portlets are divided into two columns/groups -->
		<div id="content">
			<c:forEach var="portlet" varStatus="status"
				items="${currentPage.portletIds}">
				<c:set var="portlet" value="${portlet}" scope="request" />
				<jsp:include page="portlet-skin.jsp" />
			</c:forEach>
		</div>

		<!-- Footer block: copyright -->
    <div id="footer">
       &copy; 2003-2011 Apache Software Foundation
    </div>
</div>
</body>
</html>


