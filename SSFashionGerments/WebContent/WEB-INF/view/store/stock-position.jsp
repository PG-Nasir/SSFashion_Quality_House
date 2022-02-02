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
					Summary</h5>

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
					</div>


					<div class="row">
						<div style="overflow: auto; max-height: 500px;" class="col-sm-12">
							<table class="table table-hover table-bordered table-sm mb-0">
								<thead>
									<tr>

										<th>Purchase Order</th>
										<th>Style No</th>
										<th>Style Item</th>
										<th>Color</th>
										<th>Item Name</th>
										<th>Item Color</th>
										<th>Roll/Size</th>
										<th>Unit</th>
										<th>Closing Balance</th>
									</tr>
								</thead>
								<tbody id="pendingInvoiceList">
									<c:forEach items="${stockItemList}" var="item"
										varStatus="counter">
										<tr id='row-${counter.count}'>
											<td id="purchaseOrder-${counter.count}">${item.purchaseOrder }</td>
											<td id="styleNo-${counter.count}">${item.styleNo }</td>
											<td id="itemName-${counter.count}">${item.itemName }</td>
											<td id="itemColor-${counter.count}">${item.itemColor }</td>
											<td id="stockItemName-${counter.count}">${item.stockItemName }</td>
											<td id="stockItemColorName-${counter.count}">${item.stockItemColorName }</td>
											<td id="rollSizeName-${counter.count}">${item.sizeName }
											<td id="unit-${counter.count}">${item.unit }</td>
											<td id="balanceQty-${counter.count}">${item.balanceQty }</td>
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

<jsp:include page="../include/footer.jsp" />

<script
	src="${pageContext.request.contextPath}/assets/js/store/stock-position-summery.js"></script>
