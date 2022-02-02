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
String linkName=(String) request.getAttribute("linkName");	
%>

<jsp:include page="../include/header.jsp" />

<div class="page-wrapper">
	<div class="content container-fluid">
		<div class="alert alert-success alert-dismissible fade show"
			style="display: none;">
			<p id="successAlert" class="mb-0"><strong>Success!</strong> Particular Item Name Save Successfully..</p>
		</div>
		<div class="alert alert-warning alert-dismissible fade show"
			style="display: none;">
			<p id="warningAlert" class="mb-0"><strong>Warning!</strong> Particular Item Name Empty.Please Enter Particular Item Name...</p>
		</div>
		<div class="alert alert-danger alert-dismissible fade show"
			style="display: none;">
			<p id="dangerAlert" class="mb-0"><strong>Wrong!</strong> Something Wrong...</p>
		</div>
		<input type="hidden" id="userId" value="<%=userId%>">
		<input type="hidden" id="particularItemId" value="0">
		<input type="hidden" id="linkName" value="<%=linkName%>">

		<div class="row">
			<div class="col-sm-12 col-md-12 col-lg-12">
				<div class="card-box">
					<div class="row">
						<div class="col-sm-6 col-md-6 col-lg-6">

							<div class="row ">
								<h2>
									<b>Costing Item Create</b>
								</h2>
							</div>
							<hr>

							<div class="form-group mb-1">
								<label for="particularItemName">Particular Item Name:</label> <input type="text"
									class="form-control-sm inputs" id="particularItemName" name="text">
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
									placeholder="Search Particular Item" aria-describedby="findButton"
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
										<th scope="col">Particular Item Name</th>
										<th scope="col">Edit</th>
										<th scope="col">Delete</th>
									</tr>
								</thead>
								<tbody id="dataList">
									<c:forEach items="${particularItemList}" var="particularItem"
													varStatus="counter">
										<tr>
											<td>${counter.count}</td>
											<td id='particularItemName${particularItem.particularItemId}'>${particularItem.particularItemName}</td>
											<td><i class="fa fa-edit" style="cursor: pointer;" onclick="setData(${particularItem.particularItemId})"> </i></td>
											<td><i class="fa fa-trash" style="cursor: pointer;" onclick="deleteCostingItem(${particularItem.particularItemId})"> </i></td>
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
	src="${pageContext.request.contextPath}/assets/js/register/particular-item-create.js"></script>