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
	String userId = (String) request.getAttribute("userId");
	String userName = (String) request.getAttribute("userName");
%>
<div class="page-wrapper">
	<div class="content container-fluid">
		<div class="alert alert-success alert-dismissible fade show"
			style="display: none;">
			<p id="successAlert" class="mb-0">
				<strong>Success!</strong> Department Name Save Successfully..
			</p>
		</div>
		<div class="alert alert-warning alert-dismissible fade show"
			style="display: none;">
			<p id="warningAlert" class="mb-0">
				<strong>Warning!</strong> Department Name Empty.Please Enter
				Department Name...
			</p>
		</div>
		<div class="alert alert-danger alert-dismissible fade show"
			style="display: none;">
			<p id="dangerAlert" class="mb-0">
				<strong>Wrong!</strong> Something Wrong...
			</p>
		</div>
		<input type="hidden" id="userId" value="<%=userId%>"> <input
			type="hidden" id="notifyId" value="0">

		<div class="row">
			<div class="col-sm-12 col-md-12 col-lg-12">
				<div class="card-box">
					<div class="row">
						<div class="col-sm-6 col-md-6 col-lg-6">

							<div class="row ">
								<h2>
									<b>Notify Create</b>
								</h2>
							</div>
							<hr>
							<div class="row">
								<div class='col-md-12 px-1'>
									<div class="input-group input-group-sm mb-1">
										<div class="input-group-prepend">
											<span class="input-group-text" id="inputGroup-sizing-sm"><label
												class='my-0' for="buyerName">Buyer Name<span
													style="color: red">*</span></label></span>
										</div>
										<select id="buyerName" class="form-control selectpicker"
											aria-label="Sizing example input"
											aria-describedby="inputGroup-sizing-sm"
											data-live-search="true"
											data-style="btn-light btn-sm border-light-gray form-control-sm">
											<option value="0">Select Factory</option>
											<c:forEach items="${buyerList}" var="buyer">
												<option value="${buyer.buyerid}">${buyer.buyername}</option>
											</c:forEach>
										</select>
									</div>
								</div>
							</div>

							<div class="row mt-1">
								<div class="col-md-12 px-1">
									<div class="input-group input-group-sm mb-1">
										<div class="input-group-prepend">
											<span class="input-group-text" id="inputGroup-sizing-sm"><label
												class="my-0" for="notifyName">Notify Name<span
													style="color: red">*</span></label></span>
										</div>
										<input id="notifyName" type="text" class="form-control"
											aria-label="Sizing example input"
											aria-describedby="inputGroup-sizing-sm">
									</div>
								</div>
							</div>

							<div class="row mt-1">
								<div class="col-md-12 px-1">
									<div class="input-group input-group-sm mb-1">
										<div class="input-group-prepend">
											<span class="input-group-text" id="inputGroup-sizing-sm"><label
												class="my-0" for="notifyAddress">Notify Address</label></span>
										</div>
										<textarea id="notifyAddress" type="text" class="form-control"
											aria-label="Sizing example input"
											aria-describedby="inputGroup-sizing-sm"></textarea>
									</div>
								</div>
							</div>
							<div class="row mt-1">
								<div class="col-md-12 px-1">
									<div class="input-group input-group-sm mb-1">
										<div class="input-group-prepend">
											<span class="input-group-text" id="inputGroup-sizing-sm"><label
												class="my-0" for="country">Country</label></span>
										</div>
										<input id="country" type="text" class="form-control"
											aria-label="Sizing example input"
											aria-describedby="inputGroup-sizing-sm">
									</div>
								</div>
							</div>
							<div class="row mt-1">
								<div class="col-md-12 px-1">
									<div class="input-group input-group-sm mb-1">
										<div class="input-group-prepend">
											<span class="input-group-text" id="inputGroup-sizing-sm"><label
												class="my-0" for="telephone">Telephone</label></span>
										</div>
										<input id="telephone" type="text" class="form-control"
											aria-label="Sizing example input"
											aria-describedby="inputGroup-sizing-sm">
									</div>
								</div>
							</div>
							<div class="row mt-1">
								<div class="col-md-12 px-1">
									<div class="input-group input-group-sm mb-1">
										<div class="input-group-prepend">
											<span class="input-group-text" id="inputGroup-sizing-sm"><label
												class="my-0" for="email">Email</label></span>
										</div>
										<input id="email" type="text" class="form-control"
											aria-label="Sizing example input"
											aria-describedby="inputGroup-sizing-sm">
									</div>
								</div>
							</div>
							<div class="row mt-1">
								<div class="col-md-12 text-center">
									<button type="button" id="btnSave"
										class="btn btn-primary btn-sm" onclick="saveAction()" accesskey="S"><span style="text-decoration:underline;"> Save</span></button>

									<button type="button" id="btnEdit"
										class="btn btn-success btn-sm" onclick="editAction()"
										style="display: none;">Edit</button>
									<button type="button" id="btnRefresh"
										class="btn btn-secondary btn-sm" onclick="refreshAction()">Refresh</button>
								</div>
							</div>
						</div>
						<div class="col-sm-6 col-md-6 col-lg-6 shadow ">
							<div class="input-group my-2">
								<input type="text" class="form-control"
									placeholder="Search Department" aria-describedby="findButton"
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
												<th scope="col">#</th>
												<th scope="col">Notify Name</th>
												<th scope="col">Country</th>
												<th scope="col">Address</th>
											</tr>
										</thead>
										<tbody id="dataList">
											<c:forEach items="${notifyerList}" var="notifyer"
												varStatus="counter">
												<tr>
													<td>${counter.count}</td>
													<td>${notifyer.name}</td>
													<td>${notifyer.country}</td>
													<td>${notifyer.address}</td>
													<td><i class="fa fa-edit"
														onclick="setData(${notifyer.id})" style="cursor: pointer;">
													</i></td>
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
	src="${pageContext.request.contextPath}/assets/js/register/notify-create.js"></script>