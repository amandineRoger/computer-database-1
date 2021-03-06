<%@ page session="false" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mylib2" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:include page="top.jsp"/>

<c:set var="context" value="${pageContext.request.contextPath}"/>

<spring:message code="column.name" var="columnName"/>
<spring:message code="column.introduced" var="columnIntroduced"/>
<spring:message code="column.discontinued" var="columnDiscontinued"/>
<spring:message code="column.company" var="columnCompany"/>
<spring:message code="dashboard.filterbyname" var="filterByName"/>
<spring:message code="dashboard.addcomputer" var="addComputer"/>
<spring:message code="dashboard.edit" var="editComputer"/>
<spring:message code="dashboard.found" var="foundComputer"/>
<spring:message code="delete.confirmation" var="deleteConfirmation"/>

<body>
<jsp:include page="header.jsp"/>
<section id="main">
    <div class="container">
        <h1 id="homeTitle">
            <span id="nbComputers">${page.totalCount}</span>&nbsp<span>${foundComputer}</span>
        </h1>
        <div id="actions" class="form-horizontal">
            <div class="pull-left">
                <form id="searchForm" action="#" method="GET" class="form-inline">

                    <input type="search" id="searchbox" name="search" class="form-control" placeholder="Search name"/>
                    <input type="submit" id="searchsubmit" value="${filterByName}" class="btn btn-primary"/>
                </form>
            </div>
            <div class="pull-right">
                <a class="btn btn-success" id="addComputer" href="${context}/computer/add">${addComputer}</a> <a
                    class="btn btn-default" id="editComputer" href="#"
                    onclick="$.fn.toggleEditMode();">${editComputer}</a>
            </div>
        </div>
    </div>

    <form id="deleteForm" action="${context}/computer/delete" method="POST">
        <input type="hidden" name="selection" value="">
    </form>

    <div class="container" style="margin-top: 10px;">
        <table class="table table-striped table-bordered">
            <thead>
            <tr>
                <!-- Variable declarations for passing labels as parameters -->
                <!-- Table header for Computer Name -->

                <th class="editMode" style="width: 60px; height: 22px;"><input type="checkbox"
                                                                               id="selectall"/> <span
                        style="vertical-align: top;"> - <a href="#"
                                                           id="deleteSelected"
                                                           onclick="$.fn.deleteSelected('${deleteConfirmation}');"> <i
                        class="fa fa-trash-o fa-lg"></i>
                            </a>
                        </span></th>
                <th><mylib2:link target="" name="${columnName}" params="${page.params}" order="name"/></th>
                <th><mylib2:link target="" name="${columnIntroduced}" params="${page.params}"
                                 order="introduced"/></th>
                <!-- Table header for Discontinued Date -->
                <th><mylib2:link target="" name="${columnDiscontinued}" params="${page.params}"
                                 order="discontinued"/></th>
                <!-- Table header for Company -->
                <th><mylib2:link target="" name="${columnCompany}" params="${page.params}"
                                 order="company_name"/></th>

            </tr>
            </thead>
            <!-- Browse attribute computers -->
            <tbody id="results">
            <c:forEach items="${page.list}" var="computer">
                <tr>
                    <td class="editMode"><input type="checkbox" name="cb" id="${computer.name}_id"
                                                class="cb" value="${computer.id}"></td>
                    <td><a id="${computer.name}_name" href="${context}/computer/edit?id=${computer.id}"
                           onclick="">${computer.name}</a></td>
                    <td>${computer.introduced}</td>
                    <td>${computer.discontinued}</td>
                    <td>${computer.companyName}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<footer class="navbar-fixed-bottom">
    <div class="container text-center">
        <mylib2:pagination2 current="${page.params.pageNumber}" count="${page.numberOfPages()}"
            psize="${page.params.size}" />
    </div>
</footer>
</body>
<script type="text/javascript">
    $.springMessages = {
        deleteConfirmation: "${deleteConfirmation}"
    };
</script>
</html>