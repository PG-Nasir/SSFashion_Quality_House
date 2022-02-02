<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="f"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="pg.services.SettingServiceImpl"%>
<%@page import="pg.model.WareInfo"%>
<%@page import="pg.model.Module"%>
<%@page import="pg.model.Login"%>
<%@page import="java.util.List"%>

<%
	String userId = (String) session.getAttribute("userId");
	String userName = (String) session.getAttribute("userName");
%>

<jsp:include page="../include/header.jsp" />

<script type="text/javascript"> var contexPath = "<%=request.getContextPath()%>";
</script>


<div class="page-wrapper">
	<div class="container-fluid">
		<div class="row mt-1">
			<div class="col-lg-12 d-flex justify-content-between">
				<h2 class="page-header">Users Panel</h2>
				<button type="button" class="btn btn-outline-dark btn-sm" data-toggle="modal" data-target="#exampleModal" title="Search">
								User List <i class="fa fa-search"></i>
							</button>
			</div>
		</div>

		<input type="hidden" id="userId" value="<%=userId%>"> <input
			type="hidden" id="employeeAutoId" value="0">
			<input type="hidden" id="userAutoId" value="0">
		<div class="row mt-2">
			<div class="col-lg-12">
				<div class="card">

					<div class="card-body">
						<div class="row">
							<div class="col-sm-5">
								<div class="input-group my-2">
									<input type="text" class="form-control form-control-sm"
										placeholder="Search Employee" aria-describedby="findButton"
										id="employeeId" name="employeeId">
									<div class="input-group-append">
										<button class="btn btn-sm btn-primary" type="button"
											id="employeeSearch" onclick="employeeSearch()">
											<i class="fa fa-search"></i>
										</button>
									</div>
								</div>
								<div class="input-group input-group-sm mb-1">
									<div class="input-group-prepend">
										<span class="input-group-text" id="inputGroup-sizing-sm"><label
											class="my-0" for="name">Name<span style="color: red">*</span></label></span>
									</div>
									<input id="name" type="text" class="form-control"
										aria-label="Sizing example input"
										aria-describedby="inputGroup-sizing-sm" readonly>
								</div>

								<div class="input-group input-group-sm mb-1">
									<div class="input-group-prepend">
										<span class="input-group-text" id="inputGroup-sizing-sm"><label
											class="my-0" for="userName">User Name<span
												style="color: red">*</span></label></span>
									</div>
									<input id="userName" type="text" class="form-control"
										aria-label="Sizing example input"
										aria-describedby="inputGroup-sizing-sm">
								</div>
								<div class="input-group input-group-sm mb-1">
									<div class="input-group-prepend">
										<span class="input-group-text" id="inputGroup-sizing-sm"><label
											class="my-0" for="password" style="width: 120px;">Password<span
												style="color: red">*</span></label></span>
									</div>

										<div class="col-sm-4 p-0">
										<input id="password" type="password" class="form-control" aria-label="Sizing example input" aria-describedby="inputGroup-sizing-sm">
										</div>
										<div class="col-sm-4 p-0">
											<input type="checkbox" id="sp"> Show Password
										</div>
								</div>

								<div class="input-group input-group-sm mb-1">
									<div class="input-group-prepend">
										<span class="input-group-text" id="inputGroup-sizing-sm"><label
											class="my-0" for="confirmPassword">Confirm Password<span
												style="color: red">*</span></label></span>
									</div>
									<div class="col-sm-4 p-0">
										<input id="confirmPassword" type="password"
										class="form-control" aria-label="Sizing example input"
										aria-describedby="inputGroup-sizing-sm">
									</div>
									<div class="col-sm-4 p-0">
											<input type="checkbox" id="sp1"> Show Password
									</div>

								</div>

								<div class="input-group input-group-sm mb-1">


									<div class="input-group-prepend">
										<span class="input-group-text" id="inputGroup-sizing-sm"><label
											class='my-0' for="userRole">User Role<span
												style="color: red">*</span></label></span>
									</div>
									<select id="userRole" class="form-control selectpicker"
										aria-label="Sizing example input"
										aria-describedby="inputGroup-sizing-sm"
										data-live-search="true"
										data-style="btn-light btn-sm border-light-gray form-control-sm"
										multiple onchange="loadRolePermissions()">
										<option value="0">Select Role</option>
										<c:forEach items="${roleList}" var="role">
											<option value="${role.roleId}">${role.roleName}</option>
										</c:forEach>
									</select>

								</div>

								<div class="input-group input-group-sm mb-1">
									<div class="input-group-prepend">
										<span class="input-group-text" id="inputGroup-sizing-sm"><label
											class='my-0' for="activeStatus">Active Status<span
												style="color: red">*</span></label></span>
									</div>
									<select id="activeStatus" class="form-control-sm">
										<option value="1">Active</option>

										<option value="0">Inactive</option>
									</select>
								</div>

								<button id="btnRefresh" type="button"
									class="btn btn-secondary btn-sm ml-1" onclick="fieldRefresh()">
									<i class="fa fa-refresh"></i> Refresh
								</button>
							</div>

							<div class="col-sm-7">
								<div class="card">
									<div class="card-header">
										<strong> User Access </strong>
									</div>
									<div class="card-body py-1">
										<div class="row">
											<div class="col-sm-12 col-md-12 col-lg-12 p-0 m-0"
												style="overflow: auto; max-height: 250px;">
												<table class="table table-hover table-bordered table-sm">
													<thead>
														<tr>
															<th class="text-center">#</th>
															<th>Module</th>
															<th>Sub Name</th>
															<th class="text-center">Add </th>
															<th class="text-center">Edit </th>
															<th class="text-center">View </th>
															<th class="text-center">Delete </th>
														</tr>
													</thead>
													<tbody id="permissionList">

													</tbody>
												</table>
											</div>
										</div>

									</div>
								</div>
							</div>

							<label for="extraPermissionCheck"><input
								id="extraPermissionCheck" type="checkbox"
								onchange="toggleExtraDiv()"> Extra Permission/Limitation</label>
						</div>

						<div id="extraDiv" class="row" style="display: none;">

							<div class="col-md-12">
								<div class="row">
									<div class="col-md-9">
										<select id="moduleName" class="form-control selectpicker"
											aria-label="Sizing example input" data-size="5"
											data-selected-text-format="count>2" data-actions-box="true"
											aria-describedby="inputGroup-sizing-sm"
											data-live-search="true" onchange="loadExtraPermissionInTable()"
											data-style="btn-light btn-sm border-secondary form-control-sm"
											multiple>
											<option value="0">Select Module</option>
											<c:forEach items="${allModule}" var="allModule">
												<option id="moduleName" value="${allModule.id}">${allModule.modulename}</option>
											</c:forEach>
										</select>
									</div>
								</div>

								<div class="row">
									<div class="col-sm-12 col-md-9 col-lg-9"
										style="overflow: auto; max-height: 250px;">
										<table class="table table-hover table-bordered table-sm">
											<thead>
												<tr>
													<th class="text-center">#</th>
													<th>Module</th>
													<th>Sub Name</th>
													<th class="text-center">Add </th>
													<th class="text-center">Edit </th>
													<th class="text-center">View </th>
													<th class="text-center">Delete </th>
													<th class="text-center">Permission </th>
													<th class="text-center">Limit </th>
												</tr>
											</thead>
											<tbody id="extraPermissionList">

											</tbody>
										</table>
									</div>
								</div>
							</div>



						</div>

						<div class="row">
							<div class="col-sm-12 col-md-12 text-right">
								<button type="button" id="btnSave"
									class="btn btn-primary btn-sm" onclick="saveAction()"
									accesskey="S">
									<span style="text-decoration: underline;"> Save</span>
								</button>

								<button type="button" id="btnEdit"
									class="btn btn-success btn-sm" onclick="editAction()"
									accesskey="E" style="display: none;">
									<span style="text-decoration: underline;"> Edit</span>
								</button>
								<button type="button" id="btnRefresh"
									class="btn btn-secondary btn-sm" onclick="refreshAction()">Refresh</button>
							</div>
						</div>
					</div>
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
						placeholder="Search User"
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
							<th>Employee Name</th>
							<th>User Name</th>
							<th>Department</th>
							<th>Designation</th>
							<th>Active Status</th>						
							<th><span><i class="fa fa-search"></i></span></th>
						</tr>
					</thead>
					<tbody id="poList">
						<c:forEach items="${userList}" var="user"
							varStatus="counter">
							<tr>
								<td>${counter.count}</td>
								<td>${user.fullName}</td>
								<td>${user.username}</td>
								<td>${user.departmentName}</td>
								<td>${user.designationName}</td>
								<td>${user.activeStatus}</td>
								<td><i class="fa fa-search" style="cursor:pointer;"
									onclick="searchUser('${user.id}')"> </i></td>
									
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
	src="${pageContext.request.contextPath}/assets/js/custom/link.js"></script>
<%-- <script
	src="${pageContext.request.contextPath}/assets/js/custom/user.js"></script> --%>
<script
	src="${pageContext.request.contextPath}/assets/js/settings/user-profile-create.js"></script>




