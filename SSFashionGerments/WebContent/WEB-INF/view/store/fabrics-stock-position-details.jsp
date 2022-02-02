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
String userId=(String)session.getAttribute("userId");
String userName=(String)session.getAttribute("userName");
String departmentId=(String)session.getAttribute("departmentId");

%>

<jsp:include page="../include/header.jsp" />


<div class="page-wrapper">
	<div class="m-2">
		<div class="alert alert-success alert-dismissible fade show"
			style="display: none;">
			<p id="successAlert" class="mb-0">
				<strong>Success!</strong> Costing Save Successfully..
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
		<input type="hidden" id="userId" value="<%=userId%>">
		<input type="hidden" id="departmentId"
			value="<%=departmentId%>"> <input
			type="hidden" id="itemAutoId" value="0">
		<div class="card-box pt-1">
			<header class="d-flex justify-content-between">
				<h5 class="text-center" style="display: inline;">Stock Position
					Details (Fabrics)</h5>

			</header>
			<hr class="my-1">
			<div class="row">
				<div class="col-md-12">
					<div class="row px-3">
						<div class="col-md-2 px-1">
							<input type="text" class="form-control-sm"
								id="purchaseOrderSearch" placeholder="Purchase Order">
						</div>
						<div class="col-md-2 px-1">
							<input type="text" class="form-control-sm" id="styleNoSearch"
								placeholder="Style No">
						</div>
						<div class="col-md-3 px-1">
							<input type="text" class="form-control-sm" id="itemNameSearch"
								placeholder="Style Item">
						</div>
						<div class="col-md-3 px-1">
							<input type="text" class="form-control-sm" id="fabricsItemSearch"
								placeholder="Stock Item Name">
						</div>
						<div class="col-md-2 px-1">
							<input type="text" class="form-control-sm" id="colorSearch"
								placeholder="Color">
						</div>
						<div class="col-md-2 px-1">
							<input type="text" class="form-control-sm" id="rollSizeSearch"
								placeholder="Roll/Size Name">
						</div>
						<div class="col-md-3">
							<div class="form-group mb-0  row">
								<label for="fromDate" class="col-md-4 col-form-label-sm pr-0">From
									Date</label> <input id="fromDate" type="date" onchange="loadStockItemPositionList()"
									class="col-md-7 form-control-sm px-0">
							</div>
						</div>

						<div class="col-md-3">
							<div class="form-group mb-0  row">
								<label for="toDate" class="col-md-3 col-form-label-sm pr-0 pr-0">To
									Date</label> <input id="toDate" type="date" onchange="loadStockItemPositionList()"
									class="col-md-7 form-control-sm px-0">
							</div>
						</div>
						<div class="col-md-3">
							<div class="form-group mb-0  row">
								<button id="btnSearch" type="button" accesskey="S"
									class="btn btn-success btn-sm ml-1">
									<i class="fa fa-search"></i><span style="text-decoration:underline;"> Search</span>
								</button>
							
							</div>
						</div>
					</div>


				
					<div class="row">
						<div style="overflow: auto; max-height: 400px;" class="col-sm-12">
							<table class="table table-hover table-bordered table-sm mb-0">
								<thead>
									<tr>

										<th>Purchase Order</th>
										<th>Style No</th>
										<th>Style Item</th>
										<th>Color Name</th>
										<th>Acc. Item Name</th>
										<th>Roll/Size</th>
										<th>Unit</th>
										<th>Opening Balance</th>
										<th>Receive Qty</th>
										<th>Receive Return Qty</th>
										<th>Issue Qty</th>
										<th>Iss. Ret. Qty</th>
										<th>Trans. In Qty</th>
										<th>Trans. Out Qty</th>
										<th>Closing Balance</th>
									</tr>
								</thead>
								<tbody id="pendingInvoiceList">
									<c:forEach items="${stockList}" var="item"
										varStatus="counter">
										<tr id='row-${counter.count}'>
											<td id="purchaseOrder-${counter.count}">${item.purchaseOrder }</td>
											<td id="styleNo-${counter.count}">${item.styleNo }</td>
											<td id="itemName-${counter.count}">${item.itemName }</td>
											<td id="itemColor-${counter.count}">${item.colorName }</td>
											<td id="stockItemName-${counter.count}">${item.accessoriesName }</td>
											<td id="rollSizeName-${counter.count}">${item.sizeName }
											<td id="unit-${counter.count}"></td>
											<td id="openingBalance-${counter.count}">${item.openingBalance}</td>
											<td id="receiveQty-${counter.count}">${item.receivedQy }</td>
											<td id="qcPassedQty-${counter.count}">${item.storeReturnQty }</td>
											<td id="returnQty-${counter.count}">${item.storeIssueQty }</td>
											<td id="issueQty-${counter.count}">${item.storeIssueReturnQty }</td>
											
											<td id="issueReturnQty-${counter.count}">${item.storeTransferInQty }</td>
											<td id="transferInQty-${counter.count}">${item.storeTransferOutQty }</td>

											<td id="closingBalance-${counter.count}">${item.closingQty }</td>
											
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
					<div class="row mt-1">
						<div class="col-md-12 d-flex justify-content-end">
							<button id="btnRefresh" type="button"
								class="btn btn-primary btn-sm ml-1">
								<i class="fa fa-refresh"></i> Refresh
							</button>

						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<jsp:include page="../include/footer.jsp" />

<script
	src="${pageContext.request.contextPath}/assets/js/store/fabrics-stock-position-details.js"></script>
