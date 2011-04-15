<%@ taglib uri="http://portals.apache.org/pluto" prefix="pluto" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<pluto:portlet portletId="${portlet}">
  <div class="portlet" id='<c:out value="${portlet}"/>'>
    <div class="body">
      <pluto:render/>
    </div>
  </div>
</pluto:portlet>

