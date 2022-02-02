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
	String userId = (String) session.getAttribute("userId");
	String userName = (String) session.getAttribute("userName");
%>

<jsp:include page="../include/header.jsp" />

<div class="page-wrapper">

	<input type="hidden" id="userId" value="<%=userId%>"> <input
		type="hidden" id="poNo" value="0"><input type="hidden"
		id="poType" value="">

	<div class="card-box">
		<header class="d-flex justify-content-between">
			<h5 class="text-center" style="display: inline;">
				Purchase Order <span class="badge badge-primary" id='poNoBadge'>New</span>
			</h5>
			<button type="button" class="btn btn-outline-dark btn-sm"
				data-toggle="modal" data-target="#searchModal">
				<i class="fa fa-search"></i>
			</button>
		</header>
		<hr class="my-1">
		<div class="row">
			<div class="col-md-4">
				<div class="form-group mb-0  row">
					<label for="orderDate"
						class="col-md-4 col-form-label-sm pr-0 mb-1 pb-1">Order
						Date<span style="color: red">*</span>
					</label> <input id="orderDate" type="date"
						class="col-md-8 form-control-sm customDate"
						data-date-format="DD MMM YYYY">

				</div>
				<div class="form-group mb-0  row">
					<label for="deliveryDate"
						class="col-md-4 col-form-label-sm pr-0 mb-1 pb-1">Delivery
						Date<span style="color: red">*</span>
					</label> <input id="deliveryDate" type="date"
						class="col-md-8 form-control-sm customDate"
						data-date-format="DD MMM YYYY">

				</div>

				<%-- <div class="form-group col-md-3 mb-1  pr-0 pl-1">
				<label for="supplierName" class="col-form-label-sm my-0 py-0">Supplier Name</label>
				<div class="row">
					<select id="supplierName" class="selectpicker col-md-12"
						data-live-search="true"
						data-style="btn-light btn-sm border-light-gray">
						<option id="supplierName" value="0">--Select
							SupplierName--</option>
						<c:forEach items="${supplierList}" var="supplier">
							<option id="supplierName" value="${supplier.supplierid}">${supplier.suppliername}</option>
						</c:forEach>
					</select>
				</div>

			</div> --%>
				<div class="form-group mb-0  row">
					<label for="supplierName"
						class="col-md-4 col-form-label-sm pr-0 mb-1 pb-1">Supplier
						Name<span style="color: red">*</span>
					</label> <select id="supplierName" class="selectpicker col-md-8 px-0"
						data-live-search="true"
						data-style="btn-light btn-sm border-light-gray">
						<option value="0">--Select SupplierName--</option>
						<c:forEach items="${supplierList}" var="supplier">
							<option value="${supplier.supplierid}">${supplier.suppliername}</option>
						</c:forEach>
					</select>

				</div>
				

				
			</div>
			
			
			<div class="col-md-4">
				<div class="form-group mb-0  row">
					<label for="deliveryTo"
						class="col-md-3 col-form-label-sm pr-0 mb-1 pb-1">Delivery
						To<span style="color: red">*</span>
					</label> <select id="deliveryTo" class="selectpicker col-md-9 px-0"
						data-live-search="true"
						data-style="btn-light btn-sm border-light-gray">
						<option id="deliveryTo" value="0">--- Select ---</option>
						<c:forEach items="${factoryList}" var="factory">
							<option id="deliveryTo" value="${factory.factoryId}">${factory.factoryName}</option>
						</c:forEach>
					</select>

				</div>
				<%-- <div class="form-group mb-0  row">
					<label for="orderBy"
						class="col-md-3 col-form-label-sm pr-0 mb-1 pb-1">Order By<span style="color:red">*</span></label>
					<select id="orderBy" class="selectpicker col-md-9 px-0"
						data-live-search="true"
						data-style="btn-light btn-sm border-light-gray">
						<option id="orderBy" value="0">--- Select ---</option>
						<c:forEach items="${merchendiserList}" var="merchandiser">
							<option id="orderBy" value="${merchandiser.merchendiserId}">${merchandiser.name}</option>
						</c:forEach>
					</select>

				</div> --%>
				<div class="form-group mb-0  row">
					<label for="billTo"
						class="col-md-3 col-form-label-sm pr-0 mb-1 pb-1">Bill To</label>
					<select id="billTo" class="selectpicker col-md-9 px-0"
						data-live-search="true"
						data-style="btn-light btn-sm border-light-gray">
						<option id="billTo" value="0">--- Select ---</option>
						<c:forEach items="${factoryList}" var="factory">
							<option id="billTo" value="${factory.factoryId}">${factory.factoryName}</option>
						</c:forEach>
					</select>

				</div>
								<div class="form-group mb-0  row">
					<label for="supplierName"
						class="col-md-3 col-form-label-sm pr-0 mb-1 pb-1">Buyer Name
						<span style="color: red">*</span>
					</label> <select id="buyerId" class="selectpicker col-md-9 px-0"
						data-live-search="true"
						data-style="btn-light btn-sm border-light-gray">
						<option value="0">--Select BuyerName--</option>
						<c:forEach items="${buyerList}" var="v">
							<option value="${v.buyerid}">${v.buyername}</option>
						</c:forEach>
					</select>

				</div>
			</div>
			<div class="col-md-4">
				<div class="form-group mb-0  row">
					<label for="manualPO"
						class="col-md-4 col-form-label-sm pr-0 mb-1 pb-1">Manual
						PO</label> <input id="manualPO" type="text"
						class="col-md-8 form-control-sm" disabled>

				</div>
				<div class="form-group mb-0  row">
					<label for="paymentType"
						class="col-md-4 col-form-label-sm pr-0 mb-1 pb-1">Payment
						Type<span style="color: red">*</span>
					</label> <select id="paymentType" class="form-control-sm col-md-8 px-0">
						<option value="0">Select Payment Type</option>
						<%
							for (PaymentType payment : PaymentType.values()) {
						%>
						<option value="<%=payment.name()%>"><%=payment.name()%></option>
						<%
							}
						%>
					</select>

				</div>

				
				<div class="form-group mb-0  row">
					<label for="currency"
						class="col-md-4 col-form-label-sm pr-0 mb-1 pb-1">Currency<span
						style="color: red">*</span></label> <select id="currency"
						class="form-control-sm col-md-8 px-0">
						<option value="0">Select Currency</option>
						<%
							for (Currency currency : Currency.values()) {
						%>
						<option value="<%=currency.name()%>"><%=currency.name()%></option>
						<%
							}
						%>
					</select>

				</div>
			</div>
		</div>
		<div class="row">
			<%-- <div class="col-md-2 pr-0 pl-1">
				<label for="styleNo" class="col-form-label-sm my-0 py-0">Style
					No</label> <select id="styleNo" onchange="typeWiseIndentItemLoad()"
					class="selectpicker col-md-12 px-0" data-live-search="true"
					data-style="btn-light btn-sm border-light-gray">
					<option id="styleNo" value="0">Select Style</option>
				</select>
			</div>
			
			<div class="col-md-1 pr-0 pl-1">
				<label for="type" class="col-form-label-sm my-0 py-0">Type</label> <select
					id="type" onchange="typeWiseIndentItemLoad()"
					class="form-control-sm col-md-12 px-0">
					<option id="type" value="0">Select Type</option>
					<option id="type" value="1">Fabrics</option>
					<option id="type" value="2">Accessories</option>
					<option id="type" value="3">Curton</option>
				</select>
			</div> --%>


			<div class='col-md-3 px-1'>
				<div class="input-group input-group-sm" style="margin-top: 21px;">
					<input id="indentId" type="text" class="form-control"
						placeholder="Indent Id" aria-label="input"
						aria-describedby="button-addon4" readonly="readonly"> <input
						id="indentType" type="text" class="form-control"
						placeholder="Indent Type" aria-label="input"
						aria-describedby="button-addon4" readonly="readonly">
					<div class="input-group-append" id="button-addon4">

						<button type="button" class="btn btn-outline-dark btn-sm"
							data-toggle="modal" data-target="#indentSearchModal">
							<i class="fa fa-search"></i>
						</button>
					</div>
				</div>
			</div>

			<div class="col-md-2 pr-0 pl-1">
				<label for="styleNo" class="col-form-label-sm my-0 py-0">Style
					No</label> <select id="styleNo" class="selectpicker col-md-12 px-0"
					data-live-search="true"
					data-style="btn-light btn-sm border-light-gray">
					<option value="0">--Select Style No--</option>
				</select>
			</div>

			<div class="col-md-3 pr-0 pl-1">
				<label for="indentItem" class="col-form-label-sm my-0 py-0">Indent
					Item</label> <select id="indentItem" class="selectpicker col-md-12 px-0"
					data-live-search="true"
					data-style="btn-light btn-sm border-light-gray">
					<option value="0">--Select Indent Item--</option>
				</select>
			</div>

			<%-- <div class="form-group col-md-3 mb-1  pr-0 pl-1">
				<label for="supplierName" class="col-form-label-sm my-0 py-0">Supplier
					Name</label>
				<div class="row">
					<select id="supplierName" class="selectpicker col-md-12"
						data-live-search="true"
						data-style="btn-light btn-sm border-light-gray">
						<option id="supplierName" value="0">--Select
							SupplierName--</option>
						<c:forEach items="${supplierList}" var="supplier">
							<option id="supplierName" value="${supplier.supplierid}">${supplier.suppliername}</option>
						</c:forEach>
					</select>
				</div>

			</div> --%>

			<div class="col-md-3 pr-0">
				<div class="row">
					<div class="col-md-4 pr-0 pl-1">
						<label for="rate" class="col-form-label-sm my-0 py-0">Rate</label>
						<input id="rate" type="number" class="form-control-sm pr-0 pl-1">
					</div>
					<div class="col-md-4 pr-0 pl-1">
						<label for="dollar" class="col-form-label-sm my-0 py-0">Dollar</label>
						<input id="dollar" type="number" class="form-control-sm pr-0 pl-1">
					</div>
					<div class="col-md-4 pr-0 pl-1">

						<button id="btnAdd" type="button" style="margin-top: 1.3rem;"
							class="btn btn-primary btn-sm" onclick="indentItemAdd()"
							accesskey="A">
							<i class="fa fa-plus-circle"></i><span
								style="text-decoration: underline;"> Add</span>
						</button>
					</div>

				</div>

			</div>

		</div>

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
		<hr class="my-1">
		<div class="row">
			<div class='col-md-4'>
					<input id="indentListSearch" type="text" class="form-control-sm"
						placeholder="Search Here Anything....">
			</div>
			<div class='col-md-4 px-1'>
					<div class="input-group input-group-sm mb-1">
					<div class="input-group-prepend">
						<span class="input-group-text" for="fabricsContent">Fabrics Content</span>
					</div>
					<input id="fabricsContent" type="text" class="form-control"
						aria-label="Sizing example input"
						aria-describedby="inputGroup-sizing-sm">
				</div>
			</div>
			<div class="col-md-2 px-1">
				<div class="input-group input-group-sm mb-1">
					<div class="input-group-prepend">
						<span class="input-group-text" for="caNo">CA NO.</span>
					</div>
					<input id="caNo" type="text" class="form-control"
						aria-label="Sizing example input"
						aria-describedby="inputGroup-sizing-sm">
				</div>
			</div>
			<div class="col-md-2 px-1">
				<div class="input-group input-group-sm mb-1">
					<div class="input-group-prepend">
						<span class="input-group-text" for="rnNo">RN NO.</span>
					</div>
					<input id="rnNo" type="text" class="form-control"
						aria-label="Sizing example input"
						aria-describedby="inputGroup-sizing-sm">
				</div>
			</div>
		</div>

		<div class="row mt-1">
			<div style="overflow: auto; max-height: 300px;"
				class="col-sm-12 px-1 table-responsive">
				<table
					class="table table-hover table-bordered table-sm mb-0 small-font">
					<thead class="no-wrap-text">
						<tr>
							<th>Buyer PO</th>
							<th>Style No</th>
							<th>Item Name</th>
							<th>Item Color</th>
							<th>Size</th>
							<th>Grand Qty</th>
							<th>Unit</th>
							<th>Dollar</th>
							<th>Rate</th>
							<th>Amount</th>
							<th>Sample Qty</th>
							<th><input type="checkbox" id="allCheck"></th>
						</tr>
					</thead>
					<tbody id="dataList">
						<%-- <c:forEach items="${fabricsIndentList}" var="indent"
							varStatus="counter">
							<tr>

								<td>${indent.styleName}</td>
								<td>${indent.itemName}</td>
								<td>${indent.itemColorName}</td>
								<td>${indent.supplierName}</td>
								<td>${indent.dozenQty}</td>
								<td>${indent.consumption}</td>
								<td>${indent.percentQty}</td>
								<td>${indent.totalQty}</td>
								<td>${indent.unit}</td>
								<th><input type="checkbox"></th>
							</tr>
						</c:forEach> --%>
					</tbody>
				</table>
			</div>
		</div>


		<div class="row mt-1">

			<div class="col-md-6">
				<div class="input-group input-group-sm mb-1">
					<div class="input-group-prepend">
						<span class="input-group-text" for="subject">Subject</span>
					</div>
					<textarea id="subject" class="form-control"
						aria-label="Sizing example input"
						aria-describedby="inputGroup-sizing-sm"></textarea>
				</div>
			</div>
			<div class="col-md-6">
				<div class="input-group input-group-sm mb-1">
					<div class="input-group-prepend">
						<span class="input-group-text" for="body">body</span>
					</div>
					<textarea id="body" class="form-control"
						aria-label="Sizing example input"
						aria-describedby="inputGroup-sizing-sm">Please deliver goods within 7 working days.&#13;&#10;Any Questions regarding this order should be directed to </textarea>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6">
				<div class="input-group input-group-sm mb-1">
					<div class="input-group-prepend">
						<span class="input-group-text" for="note">Note</span>
					</div>
					<textarea id="note" class="form-control"
						aria-label="Sizing example input"
						aria-describedby="inputGroup-sizing-sm"></textarea>
				</div>
			</div>

			<div class="col-md-6">
				<div class="input-group input-group-sm mb-1">
					<div class="input-group-prepend">
						<span class="input-group-text" for="terms">Terms &
							Condition</span>
					</div>
					<textarea id="terms" class="form-control"
						aria-label="Sizing example input"
						aria-describedby="inputGroup-sizing-sm">Pls follow up the bellow instruction for avoiding any problem.
1) Label should be as per buyer approved copy.
2) Colours should be matched with approved copy.
3) Any shortage will not be allowed.
4) Have to submit bill with challan & receiver signature copy.
5) Sample qty s/b sent to SS Fashion Wear.</textarea>
				</div>
			</div>

			<div class='col-md-6'></div>
			<div class="col-md-6">
				<div class="row">
					<div class="col-md-12 d-flex justify-content-end">
						<button id="btnPOSubmit" type="button" accesskey="S"
							class="btn btn-primary btn-sm" onclick="submitAction()">
							<i class="fas fa-save"></i><span
								style="text-decoration: underline;"> Submit</span>
						</button>
						<button id="btnPOEdit" type="button"
							class="btn btn-success btn-sm ml-1" onclick="purchaseOrderEdit()"
							style="display: none;">
							<i class="fa fa-pencil-square"></i> Edit
						</button>
						<button id="btnRefresh" type="button"
							class="btn btn-secondary btn-sm ml-1" onclick="refreshAction()">
							<i class="fa fa-refresh"></i> Refresh
						</button>
						<!-- <button id="btnPreview" type="button"
							class="btn btn-info btn-sm ml-1" onclick="previewAction()"
							style="display: none;">
							<i class="fa fa-print"></i> Preview
						</button> -->

						<div class="btn-group ml-1" role="group" id="btnPreview"
							aria-label="Button group with nested dropdown">
							<button type="button" class="btn btn-sm btn-info"
								onclick="previewAction('primary')">
								<i class="fa fa-print"></i> Preview
							</button>
							<div class="btn-group" role="group">
								<button id="btnGroupDrop1" type="button"
									class="btn btn-sm btn-info dropdown-toggle"
									data-toggle="dropdown" aria-haspopup="true"
									aria-expanded="false"></button>
								<div class="dropdown-menu" aria-labelledby="btnGroupDrop1">
									<div class="form-group form-check">
										<input type="checkbox" class="form-check-input"
											id="withBrandCheck"> <label class="form-check-label"
											for="withBrandCheck">With Brand</label>
									</div>
									<div class="form-group form-check">
										<input type="checkbox" class="form-check-input"
											id="withSQNumberCheck"> <label
											class="form-check-label" for="withSQNumberCheck">With
											Color SKU Number</label>
									</div>
									<div class="form-group form-check">
										<input type="checkbox" class="form-check-input"
											id="withSKUNumberCheck"> <label
											class="form-check-label" for="withSKUNumberCheck">With
											Style SKU Number</label>
									</div>
									<div class="form-group form-check">
										<input type="checkbox" class="form-check-input"
											id="landscapeViewCheck"> <label
											class="form-check-label" for="landscapeViewCheck">Landscape
											View</label>
									</div>
									<a class="dropdown-item" onclick="previewAction('withPcs')"
										href="#">With Pcs</a> <a class="dropdown-item"
										onclick="previewAction('withOutPcs')" href="#">Without Pcs</a>
									<a class="dropdown-item" onclick="previewAction()" href="#">Zipper
										Preview</a> <a class="dropdown-item"
										onclick="previewAction('general')" href="#">General
										Preview</a>

								</div>
							</div>
						</div>
					</div>
				</div>

			</div>
		</div>
	</div>
</div>
<!-- Modal -->
<div class="modal fade" id="searchModal" tabindex="-1" role="dialog"
	aria-labelledby="exampleModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<div class="input-group">
					<input id="search" type="text" class="form-control"
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
							<th>Sl</th>
							<th>Purchase Order No</th>
							<th>Supplier Name</th>
							<th>Date</th>
							<th>Type</th>
							<th>Approve Status</th>
							<th><span><i class="fa fa-search"></i></span></th>
							<th><span><i class="fa fa-print"></i></span></th>
						</tr>
					</thead>
					<tbody id="purchaseOrderList">
						<c:forEach items="${purchaseOrderList}" var="po"
							varStatus="counter">
							<tr id="row-${counter.count}" data-supplierId="${po.supplierId}">
								<td>${counter.count}</td>
								<td>${po.poNo}</td>
								<td>${po.supplierName}</td>
								<td>${po.orderDate}</td>
								<td>${po.type}</td>
								<td>${po.mdApprovalStatus}</td>
								<td><i class="fa fa-search" style="cursor: pointer;"
									onclick="searchPurchaseOrder('${po.poNo}','${po.type}')"> </i></td>
								<td><i class="fa fa-print" style="cursor: pointer;"
									onclick="showPreview('${po.poNo}','${po.supplierId}','${po.type}')">
								</i></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>

		</div>
	</div>
</div>


<!-- Modal -->
<div class="modal fade" id="indentSearchModal" tabindex="-1"
	role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<div class="input-group">
					<input id="indentSearch" type="text" class="form-control"
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
							<th>Indent Id</th>
							<th>Style No</th>
							<th>Purchase Order</th>
							<th>Indent Type</th>
							<th>Date</th>
							<th><span><i class="fa fa-search"></i></span></th>

						</tr>
					</thead>
					<tbody id="purchaseOrderList">
						<c:forEach items="${pendingIndentList}" var="indent"
							varStatus="counter">
							<tr id="row-${indent.id}+${indent.type}">
								<td>${indent.id}</td>
								<td>${indent.styleNo}</td>
								<td>${indent.purchaseOrder}</td>
								<td>${indent.indentType}</td>
								<td>${indent.date}</td>
								<td><i class="fa fa-search" style="cursor: pointer;"
									onclick="searchIndentItem('${indent.id}','${indent.indentType }')">
								</i></td>

							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>

		</div>
	</div>
</div>

<jsp:include page="../include/footer.jsp" />

<script
	src="${pageContext.request.contextPath}/assets/js/order/purchase-order.js?v=0.1"></script>
