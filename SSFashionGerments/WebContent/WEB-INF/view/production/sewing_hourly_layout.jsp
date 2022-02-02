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
	<input type="hidden" id="userId" value="<%=userId%>">
	<input type="hidden" id="itemAutoId" value="0">

	<div class="card-box">
		<header class="">
			<h5 class="text-center" style="display: inline;">Sewing Layout</h5>
			<button type="button" class="btn btn-outline-dark btn-sm"
				data-toggle="modal" data-target="#exampleModal">
				<i class="fa fa-search"></i>Layout Plan List
			</button>
			<button type="button" class="btn btn-outline-dark btn-sm"
				data-toggle="modal" data-target="#exampleModalForSewiing">
				<i class="fa fa-search"></i>Sewing Layout List
			</button>
		</header>
		<hr class="my-1">
		<div class="row">
			<div class="col-md-12 mb-1 pr-1">
				<div class="row">
					<div class="form-group col-md-4 mb-1" style="padding-left: 30px;padding-right: 35px;">
						<label for="buyerName" class="col-form-label-sm mb-0 pb-0">Buyer
							Name:</label>
						<div class="row">
							
							<input type="text" readonly id="buyerName" class="col-md-12 form-control-sm" /><input type="hidden" id="buyerId"/>
						</div>


					</div>
					<div class="form-group col-md-2 mb-1"
						style="padding-left: 1px; padding-right: 1px;">
						<label for="purchaseOrder" class="col-form-label-sm mb-0 pb-0">Purchase Order:</label>
						<div class="row">
							<input type="text" readonly id="purchaseOrder" class="col-md-12 form-control-sm" /><input type="hidden" id="buyerorderId"/>
						</div>

					</div>
					<div class="form-group col-md-2 mb-1"
						style="padding-left: 32px; padding-right: 1px;">
						<label for="styleNo" class="col-form-label-sm mb-0 pb-0">Style
							No:</label>
						<div class="row">
							<input type="text" readonly id="styleNo" class="col-md-12 form-control-sm" /><input type="hidden" id="styleId"/>
						</div>

					</div>
					<div class="form-group col-md-4 mb-1" style="padding-left: 30px;">
						<label for="itemType" class="col-form-label-sm mb-0 pb-0">Item
							Description:</label>
						<div class="row">
							<input type="text" readonly id="itemName" class="col-md-12 form-control-sm" /><input type="hidden" id="itemId"/>
						</div>

					</div>
				</div>
				
				<div class="row">
					
					<div class="form-group col-md-2 mb-1"
						style="padding-left: 15px; padding-right: 16px;padding-left: 35px;">
						<label for="sample" class="col-form-label-sm mb-0 pb-0">Plan Qty</label>
						<div class="row">
								<input type="text" readonly id="planQty" class="col-md-12 form-control-sm" />
						</div>
					</div>					
					
					<div class="form-group col-md-2 mb-1"
						style="padding-left: 15px; padding-right: 36px;padding-left: 20px;">
						<label for="sample" class="col-form-label-sm mb-0 pb-0">Daily Target</label>
						<div class="row">
								<input type="text" readonly id="dailyTargetQty" class="col-md-12 form-control-sm" />
						</div>
					</div>		
					
					<div class="form-group col-md-1 mb-1"
						style="padding-left: 15px; padding-right: 0px;padding-left: 3px;">
						<label for="sample" class="col-form-label-sm mb-0 pb-0">Line Target</label>
						<div class="row">
								<input type="text" readonly id="dailyLineTargetQty" class="col-md-12 form-control-sm" />
						</div>
					</div>
										
					<div class="form-group col-md-1 mb-1"
						style="padding-left: 15px; padding-right: 0px;padding-left: 35px;">
						<label for="sample" class="col-form-label-sm mb-0 pb-0">Hours</label>
						<div class="row">
								<input type="text" readonly id="hours" class="col-md-12 form-control-sm" />
						</div>
					</div>	
					
					<div class="form-group col-md-2 mb-1"
						style="padding-left: 15px; padding-right: 0px;padding-left: 35px;">
						<label for="sample" class="col-form-label-sm mb-0 pb-0">Hourly Target</label>
						<div class="row">
								<input type="text" readonly id="hourlyTarget" class="col-md-12 form-control-sm" />
						</div>
					</div>
										
					<div class="form-group col-md-2" style="padding-left: 35px;">
						<label for="buyerName" class="col-form-label-sm mb-0 pb-0 ">Date</label>
						<div class="row">
							<input type="date" id="layoutDate"
								class="col-md-12 form-control-sm" />
						</div>
					</div>
					<div class="form-group col-md-2 mb-1"
						style="padding-left: 15px; padding-right: 26px;padding-left: 25px;">
						<label for="sample" class="col-form-label-sm mb-0 pb-0">Line</label>
						<div class="row">
						<div id="lineoption">
						</div>
							<%-- <select id="lineId" 
												class="selectpicker form-control" data-live-search="true"
												data-style="btn-light border-secondary form-control-sm">

								</select> --%>
						</div>
					</div>	

				</div>


			</div>

		</div>
		
		
		
		
		<div id="tableList">
<!--  				<table class="table table-hover table-bordered table-sm mb-0">
					<thead>
						<tr>
							<th>Line</th>
							<th>Style No</th>
							<th>Daily Target</th>
							<th>Hours Target</th>
							<th>Hours</th>
							<th>Sew.Sup Signature</th>
							<th>Q.C.Sup Signature</th>
							<th>08-09</th>
							<th>09-10</th>
							<th>10-11</th>
							<th>11-12</th>
							<th>12-01</th>
							<th>11-12</th>
							<th>12-01</th>
							<th>02-03</th>
							<th>03-04</th>
							<th>04-05</th>
							<th>05-06</th>
							<th>06-07</th>
							<th>Total</th>
						</tr>
					</thead>
					<tbody id="production_plan">
							
					</tbody>	 -->

			
		
		</div>
		
			<div class="row" style="margin-top: 15px;">
			<div class="col-md-12">
				<button id="btnPOSubmit" type="button"
					class="btn btn-primary btn-sm" onclick="saveAction()" accesskey="S">
					<i class="fas fa-save"></i><span style="text-decoration:underline;"> Save</span>
				</button>
				<button id="btnPOEdit" type="button"
					class="btn btn-primary btn-sm ml-1" onclick = "buyerPoEditAction()" disabled>
					<i class="fa fa-pencil-square" ></i> Edit
				</button>
				<button id="btnRefresh" type="button"
					class="btn btn-primary btn-sm ml-1" onclick="refreshAction()">
					<i class="fa fa-refresh"></i> Refresh
				</button>
	
			</div>
		</div>	
		</div>

	</div>
</div>
<!-- Modal -->
<div class="modal fade" id="exampleModal" tabindex="-1" role="dialog"
	aria-labelledby="exampleModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<div class="input-group">
					<input id="search" type="text" class="form-control"
						placeholder="Search Layout Plan"
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
								<th>SL#</th>
								<th>Buyer</th>
								<th>Purchase Order</th>
								<th>Style No</th>
								<th>Item Name</th>
								<th>Date</th>
								<th>Search</th>
							</tr>
						</thead>
						<tbody id="poList">
							<c:forEach items="${layoutList}" var="list"
								varStatus="counter">
								<tr>
									<td>${counter.count}</td>
									<td id='buyerId${list.buyerId}'>${list.buyerName}</td>
									<td id='purchaseOrder${list.buyerorderId}'>${list.purchaseOrder}</td>
									<td id='styleId${list.styleId}'>${list.styleNo}</td>
									<td id='itemId${list.itemId}'>${list.itemName}</td>
									<td id='layout${list.itemId}'>${list.productionDate}</td>
									<td><i class="fa fa-search"
										onclick="setProductPlanInfoForSewing(${list.buyerId},${list.buyerorderId},${list.styleId},${list.itemId},${list.planQty})">
									</i></td>
			
								</tr>
							</c:forEach>
						</tbody>
					</table>
			</div>

		</div>
	</div>
</div>

<div class="modal fade" id="exampleModalForSewiing" tabindex="-1" role="dialog"
	aria-labelledby="exampleModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<div class="input-group">
					<input id="search" type="text" class="form-control"
						placeholder="Search Sewing Layout Report"
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
								<th>SL#</th>
								<th>Buyer</th>
								<th>Purchase Order</th>
								<th>Style No</th>
								<th>Item Name</th>
								<th>Line Name</th>
								<th>Date</th>
								<th>Search</th>
								<th>Print</th>
							</tr>
						</thead>
						<tbody id="poList">
							<c:forEach items="${sewingProductionList}" var="list"
								varStatus="counter">
								<tr>
									<td>${counter.count}</td>
									<td id='buyerId${list.buyerId}'>${list.buyerName}</td>
									<td id='purchaseOrder${list.buyerorderId}'>${list.purchaseOrder}</td>
									<td id='styleId${list.styleId}'>${list.styleNo}</td>
									<td id='itemId${list.itemId}'>${list.itemName}</td>
									<td id='itemId${list.itemId}'>${list.lineName}</td>
									<td id='production${list.itemId}'>${list.productionDate}</td>
									<td><i class="fa fa-search"
										onclick="searchLayoutDetails(${list.buyerId},${list.buyerorderId},${list.styleId},${list.itemId},${list.productionDate})">
									</i></td>
									<td><i class="fa fa-search"
										onclick="searchSewingLayout(${list.buyerId},${list.buyerorderId},${list.styleId},${list.itemId},${list.lineId})">
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
	src="${pageContext.request.contextPath}/assets/js/production/sewing_hourly_layout.js"></script>
