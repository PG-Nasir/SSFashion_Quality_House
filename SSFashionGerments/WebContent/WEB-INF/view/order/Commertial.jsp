<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="f"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="pg.model.WareInfo"%>
<%@page import="pg.model.Module"%>
<%@page import="pg.model.Login"%>
<%@page import="java.util.List"%>
<jsp:include page="../include/header.jsp" />

	<%
		List<Login> lg = (List<Login>) session.getAttribute("pg_admin");
	%>
	<div class="page-wrapper">
		<div class="container-fluid mt-2">
			<input type="hidden" id="userId" value="<%=lg.get(0).getId()%>">
			<div class="row">
				<div class="col-md-12">
					<div class="card-box pt-2 pb-2">
						<div class="row">
							<label class="col-sm-1 p-0">Contract Id</label>
							<div class="col-sm-3">
								<input type="text" class="form-control-sm" id="contractId">
							</div>

							<label class="col-sm-2">Received Date</label>
							<div class="col-sm-2">
								<input type="date" class="form-control-sm col-sm-12"
									id="receivedDate">
							</div>

							
							<div class="col-sm-4">
								<div class="input-group-append">
									<input type="text"
										class="form-control mdb-autocomplete input-sm ml-1"
										placeholder="Search" id="search" aria-label="Search"><span
										style="height: 30px;" class="input-group-text" id="search"><i
										class="fa fa-search" aria-hidden="true"></i></span>
								</div>
							</div>

						</div>

						<div class="row mt-1">
							<label class="col-sm-1 p-0">PO Number</label>
							<div class="col-sm-3">
								<select id="poNumber" class="selectpicker form-control"
									data-live-search="true"
									data-style="btn-light btn-sm border-secondary form-control-sm">
									<option id="select" value="Select Item">Select Item</option>

								</select>
							</div>

							<label class="col-sm-2">Expiry Date</label>
							<div class="col-sm-2">
								<input type="date" class="form-control-sm col-sm-12"
									id="expiryDate">
							</div>
							<label class="col-sm-2"></label>
							<div class="col-sm-2">
								<button id="btnPreview" class="btn btn-sm btn-info col-sm-12">Preview</button>
							</div>
						</div>

						<div class="row mt-1">
							<label class="col-sm-1 p-0">Style No</label>
							<div class="col-sm-3">
								<select id="styleNo" class="selectpicker form-control"
									data-live-search="true"
									data-style="btn-light btn-sm border-secondary form-control-sm">
									<option id="select" value="Select Item">Select Item</option>

								</select>
							</div>

							<label class="col-sm-2">Ammendment Date</label>
							<div class="col-sm-2">
								<input type="date" class="form-control-sm col-sm-12"
									id="ammendmentDate">
							</div>

							<label class="col-sm-2">Courieer</label>
							<div class="col-sm-2">
								<input type="text" class="form-control-sm" id="courieer">
							</div>

						</div>

						<div class="row mt-1">
							<label class="col-sm-1 p-0">Item Description</label>
							<div class="col-sm-3">
								<select id="itemDescription" class="selectpicker form-control"
									data-live-search="true"
									data-style="btn-light btn-sm border-secondary form-control-sm">
									<option id="select" value="Select Item">Select Item</option>

								</select>
							</div>

							<label class="col-sm-2">Extended Date</label>
							<div class="col-sm-2">
								<input type="date" class="form-control-sm col-sm-12"
									id="extendedDate">
							</div>

							<label class="col-sm-2">Forward Address</label>
							<div class="col-sm-2">
								<textarea style="height: 30px;" rows="1" id="forwardAddress"
									class="form-control"></textarea>
							</div>

						</div>

						<div class="row mt-1">

							<label class="col-sm-1 p-0">Color</label>
							<div class="col-sm-3">
								<select id="color" class="selectpicker form-control"
									data-live-search="true"
									data-style="btn-light btn-sm border-secondary form-control-sm">
									<option id="select" value="Select Item">Select Item</option>

								</select>
							</div>

							<label class="col-sm-2">Export Date</label>
							<div class="col-sm-2">
								<input type="date" class="form-control-sm col-sm-12"
									id="exportDate">
							</div>

						</div>
						<hr style="background-color: gray;" class="mt-1 mb-1">
						<div class="row mt-1">

							<label class="col-sm-1 p-0">Good Description</label>
							<div class="col-sm-3">
								<select id="goodDescription" class="selectpicker form-control"
									data-live-search="true"
									data-style="btn-light btn-sm border-secondary form-control-sm">
									<option id="select" value="Select Item">Select Item</option>

								</select>
							</div>
						</div>

						<div class="row mt-1">
							<label class="col-sm-1 p-0">Roll Qty</label>
							<div class="col-sm-3">
								<input type="text" id="rollQty" class="form-control-sm">
							</div>

							<label class="col-sm-2">Invoice Number</label>
							<div class="col-sm-2">
								<input type="text" class="form-control-sm" id="invoiceNumber">
							</div>

							<label class="col-sm-2">UN Makeing Date</label>
							<div class="col-sm-2">
								<input type="date" class="form-control-sm col-sm-12"
									id="unMakeingDate">
							</div>

						</div>

						<div class="row mt-1">
							<label class="col-sm-1 p-0">CTN Qty</label>
							<div class="col-sm-3">
								<input type="text" id="ctnQty" class="form-control-sm">
							</div>

							<label class="col-sm-2">Invoice Date</label>
							<div class="col-sm-2">
								<input type="date" class="form-control-sm col-sm-12"
									id="invoiceDate">
							</div>

							<label class="col-sm-2">UN Ammendment</label>
							<div class="col-sm-2">
								<input type="date" class="form-control-sm col-sm-12"
									id="unAmmendment">
							</div>

						</div>

						<div class="row mt-1">
							<label class="col-sm-1 p-0">Gross Weight</label>
							<div class="col-sm-3">
								<input type="text" id="grossWeight" class="form-control-sm">
							</div>

							<label class="col-sm-2">AWB Number</label>
							<div class="col-sm-2">
								<input type="text" class="form-control-sm" id="awbNumber">
							</div>

							<label class="col-sm-2">UN Submit Date</label>
							<div class="col-sm-2">
								<input type="date" class="form-control-sm col-sm-12"
									id="unSubmitDate">
							</div>

						</div>

						<div class="row mt-1">
							<label class="col-sm-1 p-0">Net Weight</label>
							<div class="col-sm-3">
								<input type="text" id="netWeight" class="form-control-sm">
							</div>

							<label class="col-sm-2">B/L Date</label>
							<div class="col-sm-2">
								<input type="date" class="form-control-sm col-sm-12" id="blDate">
							</div>

							<label class="col-sm-2">UN Received Date</label>
							<div class="col-sm-2">
								<select id="unReceivedDate" class="selectpicker form-control"
									data-live-search="true"
									data-style="btn-light btn-sm border-secondary form-control-sm">
									<option id="select" value="Select Item">Select Item</option>

								</select>
							</div>

						</div>

						<div class="row mt-1">
							<label class="col-sm-1 p-0">Unit</label>
							<div class="col-sm-3">
								<select id="unit" class="selectpicker form-control"
									data-live-search="true"
									data-style="btn-light btn-sm border-secondary form-control-sm">
									<option id="select" value="Select Item">Select Item</option>

								</select>
							</div>

							<label class="col-sm-2">Tracking Number</label>
							<div class="col-sm-2">
								<input type="text" class="form-control-sm" id="trackingNumber">
							</div>

							<label class="col-sm-2">UN Hover Date</label>
							<div class="col-sm-2">
								<input type="date" class="form-control-sm col-sm-12"
									id="unHoverDate">
							</div>

						</div>

						<div class="row mt-1">
							<label class="col-sm-1 p-0">Unit Price</label>
							<div class="col-sm-3">
								<input type="text" class="form-control-sm" id="unitPrice">
							</div>

							<label class="col-sm-2">Shipper Address</label>
							<div class="col-sm-2">
								<textarea rows="1" style="height: 30px;" id="shipperAddress"
									class="form-control"></textarea>
							</div>

							<label class="col-sm-2">Birthing Date</label>
							<div class="col-sm-2">
								<input type="date" class="form-control-sm col-sm-12"
									id="birthingDate">
							</div>

						</div>
						<hr style="background-color: gray;" class="mt-1 mb-1">
						<div class="row mt-1">

							<label class="col-sm-1 p-0">Currency</label>
							<div class="col-sm-3">
								<select id="currency" class="selectpicker form-control"
									data-live-search="true"
									data-style="btn-light btn-sm border-secondary form-control-sm">
									<option id="select" value="Select Item">Select Item</option>

								</select>
							</div>
						</div>

						<div class="row mt-1">
							<label class="col-sm-1 p-0">Amount</label>
							<div class="col-sm-3">
								<input type="text" class="form-control-sm" id="amount">
							</div>

							<label class="col-sm-2">Consign Address</label>
							<div class="col-sm-2">
								<textarea rows="1" style="height: 30px;" id="consignAddress"
									class="form-control"></textarea>
							</div>

							<label class="col-sm-2">Buyer Name</label>
							<div class="col-sm-2">
								<select id="buyerName" class="selectpicker form-control"
									data-live-search="true"
									data-style="btn-light btn-sm border-secondary form-control-sm">
									<option id="select" value="Select Item">Select Item</option>

								</select>
							</div>

						</div>

						<div class="row mt-1">
							<label class="col-sm-1 p-0">ETD Date</label>
							<div class="col-sm-3">
								<input type="date" class="form-control-sm col-sm-12"
									id="etdDate">
							</div>

							<label class="col-sm-2">C&F Handover Date</label>
							<div class="col-sm-2">
								<input type="date" id="cfHandoverDate"
									class="form-control-sm col-sm-12">
							</div>

							<label class="col-sm-2">Master L/C</label>
							<div class="col-sm-2">
								<input type="text" class="form-control-sm" id="masterLC">
							</div>

						</div>

						<div class="row mt-1">
							<label class="col-sm-1 p-0">ETA Date</label>
							<div class="col-sm-3">
								<input type="date" class="form-control-sm col-sm-12"
									id="etaDate">
							</div>

							<label class="col-sm-2">C&F Address</label>
							<div class="col-sm-2">
								<textarea rows="1" style="height: 30px;" id="cfAddress"
									class="form-control"></textarea>
							</div>

							<label class="col-sm-2">BBL/C</label>
							<div class="col-sm-2">
								<input type="text" class="form-control-sm" id="bblc">
							</div>

						</div>

						<div class="row mt-1">
							<label class="col-sm-1 p-0">ETC Date</label>
							<div class="col-sm-3">
								<input type="date" class="form-control-sm col-sm-12"
									id="etdDate">
							</div>

							<label class="col-sm-2">Telephone</label>
							<div class="col-sm-2">
								<input type="text" id="telephone" class="form-control-sm">
							</div>

							<label class="col-sm-2">VESSEL Name</label>
							<div class="col-sm-2">
								<input type="text" class="form-control-sm" id="vvsselName">
							</div>

						</div>

						<div class="row mt-1">
							<label class="col-sm-1 p-0">Clear Date</label>
							<div class="col-sm-3">
								<input type="date" class="form-control-sm col-sm-12"
									id="clearDate">
							</div>

							<label class="col-sm-2">Mobile</label>
							<div class="col-sm-2">
								<input type="text" id="mobile" class="form-control-sm">
							</div>

							<label class="col-sm-2">Invoice Qty</label>
							<div class="col-sm-2">
								<input type="text" class="form-control-sm" id="invoiceQty">
							</div>

						</div>

						<div class="row mt-1">

							<label class="col-sm-1 p-0">Contact No</label>
							<div class="col-sm-3">
								<input type="text" id="contactNo" class="form-control-sm">
							</div>

							<label class="col-sm-2">Fax No</label>
							<div class="col-sm-2">
								<input type="text" class="form-control-sm" id="faxNo">
							</div>

							<label class="col-sm-2">On Board Date</label>
							<div class="col-sm-2">
								<input type="date" class="form-control-sm col-sm-12"
									id="onBoardDate">
							</div>

						</div>

						<div class="row mt-1">

							<label class="col-sm-1 p-0">Ready Date</label>
							<div class="col-sm-3">
								<input type="date" class="form-control-sm col-sm-12"
									id="readyDate">
							</div>

							<label class="col-sm-2">Contact Person</label>
							<div class="col-sm-2">
								<input type="text" class="form-control-sm" id="contactPerson">
							</div>

							<label class="col-sm-2">Submit Date</label>
							<div class="col-sm-2">
								<input type="date" class="form-control-sm col-sm-12"
									id="submitDate">
							</div>

						</div>

						<div class="row mt-1">
							<div class="col-sm-12 p-0">
								<button id="btnSave" class="btn btn-sm btn-primary">Save</button>
								<button id="btnEdit" class="btn btn-sm btn-dark">Edit</button>
								<button id="btnRefresh" class="btn btn-sm btn-danger">Refresh</button>
							</div>
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>



	<jsp:include page="../include/footer.jsp" />

	<script>
		$('.bsdatepicker').datepicker({

		});
	</script>
	<script
		src="${pageContext.request.contextPath}/assets/js/custom/link.js"></script>