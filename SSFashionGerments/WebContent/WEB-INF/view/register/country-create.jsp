<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="f"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="pg.model.WareInfo"%>
<%@page import="pg.model.Module"%>
<%@page import="pg.model.Login"%>
<%@page import="java.util.List"%>

<%
String userId=(String) request.getAttribute("userId");	
String userName=(String) request.getAttribute("userName");	
%>

<jsp:include page="../include/header.jsp" />

<div class="page-wrapper">
	<div class="content container-fluid">
		<div class="alert alert-success alert-dismissible fade show"
			style="display: none;">
			<p id="successAlert" class="mb-0"><strong>Success!</strong> Country Name Save Successfully..</p>
		</div>
		<div class="alert alert-warning alert-dismissible fade show"
			style="display: none;">
			<p id="warningAlert" class="mb-0"><strong>Warning!</strong> Country Name Empty.Please Enter Country Name...</p>
		</div>
		<div class="alert alert-danger alert-dismissible fade show"
			style="display: none;">
			<p id="dangerAlert" class="mb-0"><strong>Wrong!</strong> Something Wrong...</p>
		</div>
		<input type="hidden" id="userId" value="<%=userId%>">
		<input type="hidden" id="countryId" value="0">

		<div class="row">
			<div class="col-sm-12 col-md-12 col-lg-12">
				<div class="card-box">
					<div class="row">
						<div class="col-sm-6 col-md-6 col-lg-6">

							<div class="row ">
								<h2>
									<b>Country Create</b>
								</h2>
							</div>
							<hr>

							<div class="form-group">
								<label for="countryName">Country Name:</label> <input type="text"
									class="form-control-sm inputs" id="countryName" name="text">
							</div>
							<button type="button" id="btnSave" accesskey="S" class="btn btn-primary btn-sm inputs"
								onclick="saveAction()"><span style="text-decoration:underline;"> Save</span></button>

							<button type="button" id="btnEdit" class="btn btn-success btn-sm" onclick="editAction()"
								style="display: none;">Edit</button>
							<button type="button" id="btnRefresh"
								class="btn btn-secondary btn-sm" onclick="refreshAction()">Refresh</button>

						</div>
						<div class="col-sm-6 col-md-6 col-lg-6 shadow ">
							<div class="input-group my-2">
								<input type="text" class="form-control"
									placeholder="Search Country" aria-describedby="findButton"
									id="search" name="search">
								<div class="input-group-append">
									<button class="btn btn-primary" type="button" id="findButton">
										<i class="fa fa-search"></i>
									</button>
								</div>
							</div>
							<hr>
							<div class="row" >
								<div class="col-sm-12 col-md-12 col-lg-12" style="overflow: auto; max-height: 600px;">
								<table class="table table-hover table-bordered table-sm" >
								<thead>
									<tr>
										<th scope="col">#</th>
										<th scope="col">Country Name</th>
										<th scope="col">edit</th>
									</tr>
								</thead>
								<tbody id="dataList">
									<c:forEach items="${countryList}" var="country"
													varStatus="counter">
										<tr>
											<td>${country.countryId}</td>
											<td id='countryName${country.countryId}'>${country.countryName}</td>
											<td><i class="fa fa-edit" onclick="setData(${country.countryId})" style="cursor: pointer;"> </i></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
								</div>
								
							</div>
							
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<jsp:include page="../include/footer.jsp" />

<script
	src="${pageContext.request.contextPath}/assets/js/register/country-create.js"></script>