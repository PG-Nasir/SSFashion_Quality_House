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
	<div class="content container-fluid mt-0">
		<div class="alert alert-success alert-dismissible fade show"
			style="display: none;">
			<p id="successAlert" class="mb-0"><strong>Success!</strong> Merchandiser Name Save Successfully..</p>
		</div>
		<div class="alert alert-warning alert-dismissible fade show"
			style="display: none;">
			<p id="warningAlert" class="mb-0"><strong>Warning!</strong> Merchandiser Name Empty.Please Enter Merchandiser Name...</p>
		</div>
		<div class="alert alert-danger alert-dismissible fade show"
			style="display: none;">
			<p id="dangerAlert" class="mb-0"><strong>Wrong!</strong> Something Wrong...</p>
		</div>
		<input type="hidden" id="userId" value="<%=userId%>">
		<input type="hidden" id="departmentId" value="<%=departmentId%>">
		<input type="hidden" id="itemId" value="0">

		<div class="row mb-0">
			<div class="col-sm-12 col-md-12 col-lg-12 mb-0">
				<div class="card-box">
					<div class="row">
						<div class="col-sm-6 col-md-6 col-lg-6">

							<div class="row mb-0">
								<h2>
									<b>General Item Create</b>
								</h2>
							</div>
							<hr>

							<div class="form-group mb-0">
								<label for="fabricsItemName mb-0">Name:</label> <input type="text"
									class="form-control-sm" id="ItemName" name="text">
							</div>
							<div class="form-group mb-0">
								<label for="reference mb-0">Category:</label> 
								<select id="categoryId" class="selectpicker col-md-12"
										data-live-search="true"
										data-style="btn-light btn-sm border-light-gray"
										>
										<option id="categoryId" value="0">Select Category</option>
										<c:forEach items="${catList}" var="list">
											<option id="categoryId" value="${list.categoryId}">${list.categoryName}</option>
										</c:forEach>
								</select>
							</div>
							<div class="form-group mb-0">
								<label for="reference mb-0">Unit:</label> 									
								<select id="unit" class="selectpicker col-md-12"
										data-live-search="true"
										data-style="btn-light btn-sm border-light-gray"
										>
										<option id="unitId" value="0">Select Unit</option>
										<c:forEach items="${unitList}" var="unit">
											<option id="unitId" value="${unit.unitId}">${unit.unitName}</option>
										</c:forEach>
								</select>
							</div>

							<div class="form-group mb-0">
								<label for="reference mb-0">Buy Price (Pcs):</label> <input type="text"
									class="form-control-sm" id="buyPrice" name="text">
							</div>
							<div class="form-group mb-0">
								<label for="reference mb-0">Opening Stock:</label> <input type="text"
									class="form-control-sm" id="openingStock" name="text">
							</div>
							<div class="form-group mb-0">
								<label for="reference mb-0">Stock Limit:</label> <input type="text"
									class="form-control-sm" id="stockLimit" name="text">
							</div>
	
							<button type="button" id="btnSave" accesskey="S" class="btn btn-primary btn-sm"
								onclick="saveAction()"><span style="text-decoration:underline;"> Save</span></button>

							<button type="button" id="btnEdit" class="btn btn-primary btn-sm" onclick="editAction()"
								disabled>Edit</button>
							<button type="button" id="btnRefresh"
								class="btn btn-primary btn-sm" onclick="refreshAction()">Refresh</button>

						</div>
						<div class="col-sm-6 col-md-6 col-lg-6 shadow ">
							<div class="input-group my-2">
								<input type="text" class="form-control"
									placeholder="Search Item Name" aria-describedby="findButton"
									id="search" name="search">
								<div class="input-group-append">
									<button class="btn btn-primary" type="button" id="findButton">
										<i class="fa fa-search"></i>
									</button>
								</div>
							</div>
							<hr>
							<div class="row" >
								<div class="col-sm-12 col-md-12 col-lg-12" style="overflow: auto; max-height: 400px;">
								<table class="table table-hover table-bordered table-sm" >
								<thead>
									<tr>
										<th scope="col">#</th>
										<th scope="col">Item Name</th>
										<th scope="col">Category</th>
										<th scope="col">edit</th>
									</tr>
								</thead>
								<tbody id="dataList">
									<c:forEach items="${itemList}" var="list"
													varStatus="counter">
										<tr>
											<td>${counter.count}</td>
											<td id='itemName${list.itemId}'>${list.itemName}</td>
											<td id='Category${list.itemId}'>${list.categoryName}</td>
											<td>
											<input type="hidden" id='ItemName${list.itemId}' value="${list.itemName}" />
											<input type="hidden" id='CategoryId${list.itemId}' value="${list.categoryId}" />
											<input type="hidden" id='UnitId${list.itemId}' value="${list.unitId}" />
											<input type="hidden" id='BuyPrice${list.itemId}' value="${list.buyPrice}" />
											<input type="hidden" id='OpeningStock${list.itemId}' value="${list.openingStock}" />
											<input type="hidden" id='StockLimit${list.itemId}' value="${list.stockLimit}" />
											<i class="fa fa-edit"  onclick="setData(${list.itemId})"> </i></td>
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
	src="${pageContext.request.contextPath}/assets/js/store/general_item_create.js"></script>