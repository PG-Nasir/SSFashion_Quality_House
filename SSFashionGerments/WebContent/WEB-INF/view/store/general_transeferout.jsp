<%@page import="pg.share.Currency"%>
<%@page import="pg.share.PaymentType"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="f"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="pg.model.WareInfo"%>
<%@page import="pg.model.Module"%>
<%@page import="pg.model.Login"%>
<%@page import="java.util.List"%>

<%
String userId=(String)session.getAttribute("userId");
String userName=(String)session.getAttribute("userName");
String departmentId=(String)session.getAttribute("departmentId");
%>

<jsp:include page="../include/header.jsp" />

<div class="page-wrapper">
	<div class="alert alert-success alert-dismissible fade show"
		style="display: none;">
		<p id="successAlert" class="mb-0">
			<strong>Success!</strong> Unit Name Save Successfully..
		</p>
	</div>
	<div class="alert alert-warning alert-dismissible fade show"
		style="display: none;">
		<p id="warningAlert" class="mb-0">
			<strong>Warning!</strong> Unit Name Empty.Please Enter Unit Name...
		</p>
	</div>
	<div class="alert alert-danger alert-dismissible fade show"
		style="display: none;">
		<p id="dangerAlert" class="mb-0">
			<strong>Wrong!</strong> Something Wrong...
		</p>
	</div>
	<input type="hidden" id="userId" value="<%=userId%>"> <input
		type="hidden" id="departmentId" value="<%=departmentId%>"> <input
		type="hidden" id="poNo" value="0">

	<div class="card-box">
		<header class="d-flex justify-content-between">
			<h5 class="text-center" style="display: inline;">General Store
				Transfer Out</h5>
		</header>
		<hr class="my-1">
		<div class="row">
			<div class="col-md-4">
				<div class="form-group mb-0  row">
					<label for="transferInId"
						class="col-md-3 col-form-label-sm pr-0 mb-1 pb-1">Transfer
						ID</label>
					<div class="input-group col-md-9 px-0">
						<div class="input-group-append width-100">
							<input id="transferOutTransactionId" type="text"
								class=" form-control-sm" readonly>
							<button id="newFabricsTransferOutBtn" type="button"
								class="btn btn-outline-dark btn-sm form-control-sm">
								<i class="fa fa-file-text-o"></i>
							</button>
							<button id="findFabricsTransferOutBtn" type="button"
								class="btn btn-outline-dark btn-sm form-control-sm"
								data-toggle="modal" data-target="#transferInSearchModal">
								<i class="fa fa-search"></i>
							</button>

						</div>
					</div>
				</div>
				<div class="form-group mb-0  row">
					<label for="treansferDate"
						class="col-md-3 col-form-label-sm pr-0 mb-1 pb-1">Transfer
						Date:</label> <input id="transferOutDate" type="date"
						class="col-md-9 form-control-sm">
				</div>

			</div>
			<div class="col-md-4">
				<div class="form-group mb-0  row">
					<label for="department"
						class="col-md-3 col-form-label-sm pr-0 mb-1 pb-1">Department</label>


					<select id="department" class="selectpicker col-md-9 px-0"
						data-live-search="true"
						data-style="btn-light btn-sm border-light-gray">
						<option id="department" value="0">--- Select ---</option>
						<c:forEach items="${departmentList}" var="department">
							<option id="department" value="${department.departmentId}">${department.departmentName}
								(${department.factoryName})</option>
						</c:forEach>
					</select>
				</div>
				<div class="form-group mb-0  row">
					<label for="receiveFrom"
						class="col-md-3 col-form-label-sm pr-0 mb-1 pb-1">Receive
						From</label> <input id="receiveBy" type="text"
						class="col-md-9 form-control-sm">
				</div>

			</div>
			<div class="col-md-4">
				<div class="form-group mb-0  row">
					<label for="remarks"
						class="col-md-3 col-form-label-sm pr-0 mb-1 pb-1">Remarks</label>
					<textarea id="remarks" class="col-md-9 form-control-sm"></textarea>

				</div>
				<button id="itemsearch" type="button"
					class="btn btn-info btn-sm " data-toggle="modal"
					placeholder="Search Fabrics Roll">
					<i class="fa fa-search"></i> Items Search
				</button>
			</div>
		</div>

		<hr class="my-1">
		<div class="row mt-1">
			<div style="overflow: auto; max-height: 300px;"
				class="col-sm-12 px-1 table-responsive">
				<table id="ptable" 
					class="table table-hover table-bordered table-sm mb-0 small-font table-expandable">
					<thead class="no-wrap-text">
						<tr>
							<th>Item Name</th>
							<th>Unit</th>
							<th>Receive Qty</th>
							<th>Transfer In Qty</th>
							<th>Transfer Out Qty</th>
							<th>Issue Qty</th>
							<th>Issue Return Qty</th>
							<th>Balance Qty</th>
							<th>Transfer Qty</th>
						</tr>
					</thead>
					<tbody id="itemlist">

					</tbody>
				</table>
			</div>
		</div>

		<div class="row mt-1">
			<div class="col-md-12 d-flex justify-content-end">
				<button id="btnSubmit" type="button" class="btn btn-primary btn-sm"
					onclick="submitAction()" accesskey="S">
					<i class="fas fa-save"></i><span
						style="text-decoration: underline;"> Submit</span>
				</button>
				<button id="btnEdit" type="button"
					class="btn btn-primary btn-sm ml-1" onclick="submitAction()" disabled>
					<i class="fa fa-pencil-square"></i> Edit
				</button>
				<button id="btnRefresh" type="button"
					class="btn btn-primary btn-sm ml-1" onclick="refreshAction()">
					<i class="fa fa-refresh"></i> Refresh
				</button>
				<button id="btnPreview" type="button"
					class="btn btn-primary btn-sm ml-1" disabled>
					<i class="fa fa-print"></i> Preview
				</button>
			</div>
		</div>
	</div>
</div>
<!--QC search modal -->
<div class="modal fade" id="transferInSearchModal" tabindex="-1"
	role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<div class="input-group">
					<input type="text" class="form-control"
						placeholder="Search Purchase Order"
						aria-label="Recipient's username" aria-describedby="basic-addon2">
					<div class="input-group-append">
						<span class="input-group-text"><i class="fa fa-search"></i></span>
					</div>
				</div>
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body">
				<table class="table table-hover table-bordered table-sm mb-0">
					<thead>
						<tr>
							<th>Transaction Id</th>
							<th>Transaction Date</th>
							<th>Department Name</th>
							<th><span><i class="fa fa-search"></i></span></th>
						</tr>
					</thead>
					<tbody id="invoicelist">
					
					
							<c:forEach items="${invoices}" var="list" varStatus="counter">
							<tr>
								<td>${counter.count}</td>
								<td id='id${counter.count}' data-itemid='${list.transferid}'>${list.transferid}</td>
								<td id='stock${list.transferid}'>${list.transefdate}</td>
								<td id='stock${list.transferid}'>${list.department}</td>
								<td > <input class='check' type='checkbox' id='check${list.itemid}' onchange=""></td>
							</tr>
						</c:forEach>

					</tbody>
				</table>
			</div>

		</div>
	</div>
</div>

<!-- Item Search Modal -->
<div class="modal fade" id="rollSearchModal" tabindex="-1" role="dialog"
	aria-labelledby="exampleModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-xl">
		<div class="modal-content">
			<div class="modal-header py-2">
				<div class="input-group input-group-sm">

					<input id="searchEverything" type="text" class="form-control"
						placeholder="Search Every Thing" aria-label="Recipient's username"
						aria-describedby="basic-addon2">
					<div class="input-group-append">
						<button class="form-control-sm" id="searchRefreshBtn">
							<i class="fa fa-refresh" style="cursor: pointer;"></i>
						</button>
					</div>


				</div>
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="row px-3">

				<div class="col-md-3 px-1">
					<input type="text" class="form-control-sm" id="itemNameSearch"
						placeholder="Item Name">
				</div>



			</div>
			<div class="modal-body table-responsive" style="height: 70vh">
				<table class="table table-hover table-bordered table-sm mb-0">
					<thead class="no-wrap-text bg-light">
						<tr>
							<th>SL#</th>
							<th>Item Name</th>
							<th>Balance Qty</th>
							<th><span><input type="checkbox" id="checkAll"></span></th>
						</tr>
					</thead>
					<tbody id="itemsearchlist">
						<c:forEach items="${itemsList}" var="list" varStatus="counter">
							<tr>
								<td>${counter.count}</td>
								<td id='id${counter.count}' data-itemid='${list.itemid}'>${list.itemname}</td>
								<td id='stock${list.itemid}'>${list.stock}</td>
								<td > <input class='check' type='checkbox' id='check${list.itemid}' onchange=""></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<div class="modal-footer py-2">
				<div class="d-flex justify-content-end">
					<button id="rollAddBtn" class="btn btn-primary btn-sm">
						<span><i class="fas fa-plus-circle"></i></span> Add
					</button>
				</div>
			</div>
		</div>
	</div>
</div>

<jsp:include page="../include/footer.jsp" />

<script
	src="${pageContext.request.contextPath}/assets/js/store/general-transfer-out.js"></script>
