<%@page import="pg.share.ItemType"%>
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
			<p id="successAlert" class="mb-0">
				<strong>Success!</strong> Fabrics Item Name Save Successfully..
			</p>
		</div>
		<div class="alert alert-warning alert-dismissible fade show"
			style="display: none;">
			<p id="warningAlert" class="mb-0">
				<strong>Warning!</strong> Fabrics Item Name Empty.Please Enter
				Fabrics Item Name...
			</p>
		</div>
		<div class="alert alert-danger alert-dismissible fade show"
			style="display: none;">
			<p id="dangerAlert" class="mb-0">
				<strong>Wrong!</strong> Something Wrong...
			</p>
		</div>
		<input type="hidden" id="userId" value="<%=userId%>">
		<input type="hidden" id="itemType" value="<%=ItemType.FABRICS.getType()%>">
		<input type="hidden" id="fabricsItemId" value="0">
		<input type="hidden" id="linkName" value="<%=linkName%>">

		<div class="row">
			<div class="col-sm-12 col-md-12 col-lg-12">
				<div class="card-box">
					<div class="row">
						<div class="col-sm-6 col-md-6 col-lg-6">

							<div class="row ">
								<h2>
									<b>Fabrics Item Create</b>
								</h2>
							</div>
							<hr>

							<div class="form-group">
								<label for="fabricsItemName">Fabrics Item Name:</label> <input
									type="text" class="form-control-sm inputs" id="fabricsItemName"
									name="text">
							</div>
							<div class="form-group">
								<label for="reference">Reference:</label> <input type="text"
									class="form-control-sm inputs" id="reference" name="text">
							</div>
							<div class="row">

								<div class="col-md-6 form-group mb-0 row">

									<label for="unit" class="col-md-3 col-form-label-sm pr-0">Unit</label>
									<select id="unit" class="selectpicker col-md-9 px-0"
										data-live-search="true"
										data-style="btn-light btn-sm border-light-gray inputs">
										<option id="unit" value="0">Select Unit</option>
										<c:forEach items="${unitList}" var="unit">
											<option id="unit" value="${unit.unitId}">${unit.unitName}</option>
										</c:forEach>
									</select>
								</div>



								<div class="col-md-6">
									<div class="input-group input-group-sm my-0">
										<input type="text" class="form-control inputs"
											placeholder="Unit Qty" aria-describedby="addUnit"
											id="unitQty">
										<div class="input-group-append">
											<button class="btn btn-primary" type="button" id="addUnit" onclick="unitAddAction()">
												<i class="fas fa-plus-circle"></i>
											</button>
										</div>
									</div>
									<table class="table table-hover table-bordered table-sm">
										<thead>
											<tr>
												<th scope="col">Unit</th>
												<th scope="col">Minimum Qty</th>
											</tr>
										</thead>
										<tbody id="unitList">
											
										</tbody>
									</table>
								</div>
							</div>


							<button type="button" id="btnSave" accesskey="S" class="btn btn-primary btn-sm inputs"
								onclick="saveAction()"><span style="text-decoration:underline;"> Save</span></button>

							<button type="button" id="btnEdit" class="btn btn-success btn-sm"
								onclick="editAction()" style="display: none;">Edit</button>
							<button type="button" id="btnRefresh"
								class="btn btn-secondary btn-sm" onclick="refreshAction()">Refresh</button>

						</div>
						<div class="col-sm-6 col-md-6 col-lg-6 shadow ">
							<div class="input-group my-2">
								<input type="text" class="form-control"
									placeholder="Search Fabrics Item" aria-describedby="findButton"
									id="search" name="search">
								<div class="input-group-append">
									<button class="btn btn-primary" type="button" id="findButton">
										<i class="fa fa-search"></i>
									</button>
								</div>
							</div>
							<hr>
							<div class="row">
								<div class="col-sm-12 col-md-12 col-lg-12"
									style="overflow: auto; max-height: 600px;">
									<table class="table table-hover table-bordered table-sm">
										<thead>
											<tr>
												<th scope="col">#SL</th>
												<th scope="col">Fabrics Item Name</th>
												<th scope="col">Reference</th>
												<th scope="col">Edit</th>
												<th scope="col">Delete</th>
											</tr>
										</thead>
										<tbody id="dataList">
											<c:forEach items="${fabricsItemList}" var="fabricsItem"
												varStatus="counter">
												<tr>
													<td>${counter.count}</td>
													<td id='fabricsItemName${fabricsItem.fabricsItemId}'>${fabricsItem.fabricsItemName}</td>
													<td id='reference${fabricsItem.fabricsItemId}'>${fabricsItem.reference}</td>
													<td><i class="fa fa-edit" onclick="setData(${fabricsItem.fabricsItemId})" style='cursor:pointer;'> </i></td>
													<td><i class="fa fa-trash" onclick="trashFabricsData(${fabricsItem.fabricsItemId})" style='cursor:pointer;'> </i></td>
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
	src="${pageContext.request.contextPath}/assets/js/register/fabrics-create.js"></script>