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
String userId=(String)session.getAttribute("userId");
String userName=(String)session.getAttribute("userName");
%>


<jsp:include page="../include/header.jsp" />
<script type="text/javascript"> var contexPath = "<%=request.getContextPath()%>
	";
</script>
<body onload="getOrganizationName()">


	<div class="page-wrapper">
		<div class="container-fluid">
			<div class="alert alert-success alert-dismissible fade show"
				style="display: none;">
				<p id="successAlert" class="mb-0">
					<strong>Success!</strong> Organization Name Save Successfully..
				</p>
			</div>
			<div class="alert alert-danger alert-dismissible fade show"
				style="display: none;">
				<p id="dangerAlert" class="mb-0">
					<strong>Wrong!</strong> Something Wrong...
				</p>
			</div>
			<div class="row mt-1"></div>
			<input type="hidden" id="userId" value="<%=userId%>">
			<input type="hidden" id="organizationId"
				value="<%=userId%>">
			<div class="row mt-2">
				<div class="col-lg-12">
					<div class="card-box">
						<div class="row">
							<h2>Organization Information</h2>
						</div>
						<div class="row mt-1">
							<div class="col-sm-5">
								<div class="row">
									<label class="col-sm-4 p-0">Organization Name</label>
									<div class="col-sm-8">
										<input type="text" id="organizationName"
											class="form-control-sm">
									</div>
								</div>
								<div class="row mt-1">
									<label class="col-sm-4 p-0">Organization Contact</label>
									<div class="col-sm-8">
										<input type="text" id="organizationContact"
											class="form-control-sm">
									</div>
								</div>
								<div class="row mt-1">
									<label class="col-sm-4 p-0">Organization Address</label>
									<div class="col-sm-8">
										<textarea class="form-control form-control-sm"
											id="organizationAddress" rows="3"></textarea>
									</div>
								</div>
							</div>
						</div>
						<div class="row mt-1">
							<div class="col-sm-5">
								<div class="row">
									<div class="col-sm-12 text-right">
										<button type="button" id="con" onclick="changeAddress()" accesskey="C"
											class="btn btn-sm btn-success"><span style="text-decoration:underline;">Change</span></button>
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
		src="${pageContext.request.contextPath}/assets/js/custom/link.js"></script>
	<script

		src="${pageContext.request.contextPath}/assets/js/custom/organization-create.js"></script>