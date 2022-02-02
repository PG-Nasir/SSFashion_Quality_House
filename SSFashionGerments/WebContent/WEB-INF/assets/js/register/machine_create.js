let machineId;
let lineId = 0;
let departmentId = 0;


window.onload = () => {
	document.title = "Machine Create";
	allMachine();
}

function factoryWiseLine() {
	let factoryId = $("#factoryId").val();
	if (factoryId != 0) {
		$("#loader").show();
		$.ajax({
			type: 'GET',
			dataType: 'json',
			url: './factoryWiseDepartment/' + factoryId,
			success: function (data) {
				loadDepartment(data.departmentList);
				$("#loader").hide();
			}
		});
	}
}

function loadDepartment(data) {

	let itemList = data;
	let options = "<option id='departmentId' value='0' selected>Select Department</option>";
	let length = itemList.length;
	for (let i = 0; i < length; i++) {
		options += "<option id='departmentId' value='" + itemList[i].departmentId + "'>" + itemList[i].departmentName + "</option>";
	};
	document.getElementById("departmentId").innerHTML = options;
	$('.selectpicker').selectpicker('refresh');
	$('#departmentId').val(departmentId).change();
	departmentId = 0;

}

function departmentWiseLine() {

	let departmentId = $("#departmentId").val();

	if (departmentId != 0) {
		$("#loader").show();
		$.ajax({
			type: 'GET',
			dataType: 'json',
			url: './departmentWiseLine/' + departmentId,
			success: function (data) {
				loadLine(data.lineList);
				$("#loader").hide();
			}
		});
	}
}

function loadLine(data) {

	let itemList = data;
	let options = "<option id='lineId' value='0' selected>Select Line</option>";
	let length = itemList.length;
	for (let i = 0; i < length; i++) {
		options += "<option id='lineId' value='" + itemList[i].lineId + "'>" + itemList[i].lineName + "</option>";
	};
	document.getElementById("lineId").innerHTML = options;
	$('.selectpicker').selectpicker('refresh');
	$('#lineId').val(lineId).change();
	lineId = 0;

}

function saveAction() {

	let factoryId = $("#factoryId").val();
	let departmentId = $("#departmentId").val();
	let lineId = $("#lineId").val();
	let name = $("#name").val();
	let brand = $("#brand").val();
	let modelNo = $("#modelNo").val();
	let motor = $("#motor").val();
	let empId = $("#employee").val();
	let userId = $("#userId").val();

	if (name != '') {
		$("#loader").show();
		$.ajax({
			type: 'POST',
			dataType: 'json',
			url: './saveMachine',
			data: {
				factoryId: factoryId,
				departmentId: departmentId,
				lineId: lineId,
				name: name,
				brand: brand,
				modelNo: modelNo,
				motor: motor,
				employeeId: empId,
				UserId: userId,

			},

			success: function (data) {
				if (data.result == "Something Wrong") {
					dangerAlert("Something went wrong");
				} else if (data.result == "duplicate") {
					dangerAlert("Duplicate Machine Code..This Machine Name Allreary Exist")
				} else {
					successAlert("Machine Save Successfully");

					$("#machineList").empty();
					allMachine();
				}
				$("#loader").hide();
			}

		});
	}else{
		alert("Information Not Complete")
	}
}


function editAction() {

	let name = $("#name").val();
	let brand = $("#brand").val();
	let modelNo = $("#modelNo").val();
	let motor = $("#motor").val();
	let empId = $("#employee").val();
	let userId = $("#userId").val();

	if (name != '') {
		$("#loader").show();
		$.ajax({
			type: 'POST',
			dataType: 'json',
			url: './editMachine',
			data: {

				machineId: machineId,
				name: name,
				brand: brand,
				modelNo: modelNo,
				motor: motor,
				employeeId: empId,
				UserId: userId,

			},

			success: function (data) {
				if (data.result == "Something Wrong") {
					dangerAlert("Something went wrong");
				} else if (data.result == "duplicate") {
					dangerAlert("Duplicate Machine Name..This Machine Name Allreary Exist")
				} else {
					successAlert("Update Successfully");

					$("#machineList").empty();
					allMachine();
				}
				$("#loader").hide();
			}

		});
	}
}

function allMachine() {
	$.ajax({
		type: 'POST',
		dataType: 'json',
		url: './allMachine',
		data: {
		},
		success: function (data) {
			$("#machineList").empty();
			patchdata(data.result);
		}
	});
}

function patchdata(data) {
	let rows = [];

	for (let i = 0; i < data.length; i++) {
		rows.push(drawRow(data[i], i + 1));

	}

	$("#machineList").append(rows);
}

function drawRow(rowData, c) {

	let row = $("<tr />");
	row.append($("<td>" + c + "</td>"));
	row.append($("<td>" + rowData.Name + "</td>"));
	row.append($("<td>" + rowData.ModelNo + "</td>"));
	row.append($("<td>" + rowData.EmployeeName + "</td>"));
	row.append($("<td class='text-center'><i class='fa fa-edit' onclick=setData('" + encodeURIComponent(rowData.MachineId) + "','" + encodeURIComponent(rowData.Name) + "','" + encodeURIComponent(rowData.Brand) + "','" + encodeURIComponent(rowData.ModelNo) + "','" + encodeURIComponent(rowData.Motor) + "','" + encodeURIComponent(rowData.EmployeeId) + "','" + encodeURIComponent(rowData.EmployeeName) + "') style='cursor : pointer;'> </i></td>"));
	row.append($("<td class='text-center'><i class='fa fa-trash' onclick=deleteMachine('"+encodeURIComponent(rowData.MachineId)+"')> </i></td>"));
	return row;
}

function setData(MachineId, Name, Brand, ModelNo, Motor, EmployeeId, EmployeeName) {

	machineId = decodeURIComponent(MachineId);
	Name = decodeURIComponent(Name);

	Brand = decodeURIComponent(Brand);
	ModelNo = decodeURIComponent(ModelNo);
	Motor = decodeURIComponent(Motor);
	EmployeeId = decodeURIComponent(EmployeeId);
	EmployeeName = decodeURIComponent(EmployeeName);

	$("#name").val(Name);
	$("#brand").val(Brand);
	$("#modelNo").val(ModelNo);
	$("#motor").val(Motor);
	$("#employee").val(EmployeeId).change();


	$("#btnSave").hide();
  	$("#btnEdit").show();

}

function deleteMachine(mId){
	let machineId = decodeURIComponent(mId);
	$("#loader").show();
	$.ajax({
		type: 'POST',
		dataType: 'json',
		url: './deleteMachine/'+machineId,
		data: {
		},
		success: function (data) {
			if (data.result == "Something Wrong") {
				dangerAlert("Something went wrong");
			} else if (data.result == "duplicate") {
				dangerAlert("Duplicate Sample Type Name..This Sample Type Name Allreary Exist")
			} else {
				successAlert("Updated Successfully");

				$("#dataList").empty();
				allMachine();

			}
			$("#loader").hide();
		}
	});
}

function refreshAction() {

	location.reload();
	document.getElementById("btnSave").disabled = false;
	document.getElementById("btnEdit").disabled = true;
}


function successAlert(message) {
	let element = $(".alert");
	element.hide();
	element = $(".alert-success");
	document.getElementById("successAlert").innerHTML = "<strong>Success!</strong> " + message + "...";
	element.show();
	setTimeout(() => {
		element.toggle('fade');
	}, 2500);
}

function warningAlert(message) {
	let element = $(".alert");
	element.hide();
	element = $(".alert-warning");
	document.getElementById("warningAlert").innerHTML = "<strong>Warning!</strong> " + message + "..";
	element.show();
	setTimeout(() => {
		element.toggle('fade');
	}, 2500);
}

function dangerAlert(message) {
	let element = $(".alert");
	element.hide();
	element = $(".alert-danger");
	document.getElementById("dangerAlert").innerHTML = "<strong>Duplicate!</strong> " + message + "..";
	element.show();
	setTimeout(() => {
		element.toggle('fade');
	}, 2500);
}

$(document).ready(function () {
	$("input:text").focus(function () { $(this).select(); });
});

$(document).ready(function () {
	$("#search").on("keyup", function () {
		let value = $(this).val().toLowerCase();
		$("#machineList tr").filter(function () {
			$(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
		});
	});
});

let idListMicro = ["name","brand","modelNo","motor","factoryId","departmentId","employee","lineId","btnSave"];
idListMicro.forEach((id,index)=>{
  $('#'+id).keyup(function(event){
    if(event.keyCode ===13){
      event.preventDefault();
      $("#"+idListMicro[index+1]).focus();
    }
  });
})