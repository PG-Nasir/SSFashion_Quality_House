window.onload = ()=>{
  document.title = "Fabrics Quality Control";
} 
$("#newFabricsQCBtn").click(function () {

  $("#qcTransactionId").val("-- New Transaction --");
  $("#btnSubmit").prop("disabled", false);
  $("#btnEdit").prop("disabled", true);
});

$("#findFabricsQCBtn").click(function () {
  $.ajax({
    type: 'GET',
    dataType: 'json',
    url: './getFabricsQCList',
    data: {},
    success: function (data) {

      console.log(data);
      drawFabricsQCListTable(data.fabricsQCList);
    }
  });
});

$("#grnSearchBtn").click(function () {
  $.ajax({
    type: 'GET',
    dataType: 'json',
    url: './getFabricsReceiveList',
    data: {},
    success: function (data) {

      $("#fabricsReceiveList").empty();
      $("#fabricsReceiveList").append(drawFabricsReceiveListTable(data.fabricsReceiveList));

    }
  });
});

function setFabricsReceiveInfo(transactionId) {
  $.ajax({
    type: 'GET',
    dataType: 'json',
    url: './getFabricsReceiveInfo',
    data: {
      transactionId: transactionId
    },
    success: function (data) {

      const fabricsReceive = data.fabricsReceive;

      $("#qcTransactionId").val(fabricsReceive.transactionId);
      $("#fabricsId").val(fabricsReceive.fabricsId);
      $("#fabrics").val(fabricsReceive.fabricsName);
      $("#grnNo").val(fabricsReceive.grnNo);
      let date = fabricsReceive.grnDate.split("/");
      $("#receiveDate").val(date[2] + "-" + date[1] + "-" + date[0]);

      $("#remarks").val(fabricsReceive.remarks);
      $("#supplier").val(fabricsReceive.supplierId).change();
      $('#grnSearchModal').modal('hide');
      drawFabricsRollListTable(fabricsReceive.fabricsRollList,false);
      $("#btnSubmit").prop("disabled", false);
      $("#btnEdit").prop("disabled", true);

    }
  });
}

function setFabricsQCInfo(qcTransactionId) {
  $.ajax({
    type: 'GET',
    dataType: 'json',
    url: './getFabricsQCInfo',
    data: {
      qcTransactionId: qcTransactionId
    },
    success: function (data) {

      const fabricsQC = data.fabricsQC;
      console.log(fabricsQC);
      $("#qcTransactionId").val(fabricsQC.qcTransactionId);
      $("#fabrics").val(fabricsQC.fabricsName);
      $("#grnNo").val(fabricsQC.grnNo);
      let date = fabricsQC.receiveDate.split("/");
      $("#receiveDate").val(date[2] + "-" + date[1] + "-" + date[0]);

      $("#remarks").val(fabricsQC.remarks);
      $("#supplier").val(fabricsQC.supplierId).change();
      $('#qcSearchModal').modal('hide');
      drawFabricsRollListTable(fabricsQC.fabricsRollList,true);
      $("#btnSubmit").prop("disabled", true);
      $("#btnEdit").prop("disabled", false);

    }
  });
}

function submitAction() {
  const rowList = $("#rollList tr");
  const length = rowList.length;

  const qcTransactionId = $("#qcTransactionId").val();
  const qcDate = $("#qcDate").val();
  const grnNo = $("#grnNo").val();
  const remarks = $("#remarks").val();
  const checkBy = $("#checkBy").val();
  const departmentId = $("#departmentId").val();;
  const userId = $("#userId").val();

  let rollList = ""

  for (let i = 0; i < length; i++) {

    const row = rowList[i];
    const id = row.id.slice(6);
    const checked = $("#isCheck-" + id).prop('checked');
    if (checked) {
      const purchaseOrder = row.getAttribute('data-purchase-order');
      const styleId = row.getAttribute('data-style-id');
      const itemId = row.getAttribute('data-item-id');
      const itemColorId = row.getAttribute('data-item-color-id');
      const fabricsId = row.getAttribute('data-fabrics-id');
      const fabricsColorId = row.getAttribute('data-fabrics-color-id');
      const rollId = row.getAttribute('data-roll-id');
      const unitId = row.getAttribute('data-unit-id');
      const unitQty = $("#checkQty-" + id).text().trim() == '' ? "0" : $("#checkQty-" + id).text();
      const qcPassedType = $("#qcPassed-" + id).val();
      const rackName = $("#rackName-" + id).text();
      const binName = $("#binName-" + id).text();

      rollList += `autoId : ${id},qcTransactionId : ${qcTransactionId},purchaseOrder : ${purchaseOrder},styleId : ${styleId},itemId : ${itemId},itemColorId : ${itemColorId},fabricsId : ${fabricsId},fabricsColorId : ${fabricsColorId},rollId : ${rollId},unitId : ${unitId},unitQty : ${unitQty},rackName : ${rackName},binName : ${binName},qcPassedType : ${qcPassedType},userId : ${userId} #`;
    }
  }

  rollList = rollList.slice(0, -1);
  console.log(rollList);
  if (length > 0) {
    if (qcTransactionId != '') {
      if (grnNo != '') {
        if (confirm("Are you sure to submit this Fabrics Quality Control...")) {
          $.ajax({
            type: 'POST',
            dataType: 'json',
            url: './submitFabricsQC',
            data: {
              qcTransactionId: qcTransactionId,
              qcDate: qcDate,
              grnNo: grnNo,
              remarks: remarks,
              rollList: rollList,
              checkBy: checkBy,
              departmentId : departmentId,
              userId: userId
            },
            success: function (data) {
              if (data.result == "Something Wrong") {
                dangerAlert("Something went wrong");
              } else if (data.result == "duplicate") {
                dangerAlert("Duplicate Item Name..This Item Name Already Exist")
              } else {
                alert("Successfully Submit...");
                refreshAction();
              }
            }
          });
        }
      } else {
        warningAlert("Please Enter GRN No")
        $("#grnNo").focus();
      }
    } else {
      warningAlert("Please Get a transaction Id")
      $("#transactionId").focus();
    }
  } else {
    warningAlert("Please Enter Fabrics Roll");
  }
}

function editAction() {
  const rowList = $("#rollList tr");
  const length = rowList.length;

  const qcTransactionId = $("#qcTransactionId").val();
  const qcDate = $("#qcDate").val();
  const grnNo = $("#grnNo").val();
  const remarks = $("#remarks").val();
  const checkBy = $("#checkBy").val();
  const userId = $("#userId").val();

  let rollList = ""

  for (let i = 0; i < length; i++) {

    const row = rowList[i];
    const id = row.id.slice(6);
    const checked = $("#isCheck-" + id).prop('checked');
    if (checked) {
      const purchaseOrder = row.getAttribute('data-purchase-order');
      const styleId = row.getAttribute('data-style-id');
      const itemId = row.getAttribute('data-item-id');
      const itemColorId = row.getAttribute('data-item-color-id');
      const fabricsId = row.getAttribute('data-fabrics-id');
      const fabricsColorId = row.getAttribute('data-fabrics-color-id');
      const rollId = row.getAttribute('data-roll-id');;
      const unitId = row.getAttribute('data-unit-id');
      const qcPassedQty = $("#qcPassedQty-" + id).text().trim() == '' ? "0" : $("#qcPassedQty-" + id).text();
      const qcPassedType = $("#qcPassed-" + id).val();
      const rackName = $("#rackName-" + id).text();
      const binName = $("#binName-" + id).text();

      rollList += `autoId : ${id},qcTransactionId : ${qcTransactionId},purchaseOrder : ${purchaseOrder},styleId : ${styleId},itemId : ${itemId},itemColorId : ${itemColorId},fabricsId : ${fabricsId},fabricsColorId : ${fabricsColorId},rollId : ${rollId},unitId : ${unitId},qcPassedQty : ${qcPassedQty},rackName : ${rackName},binName : ${binName},qcPassedType : ${qcPassedType},userId : ${userId} #`;
    }
  }

  rollList = rollList.slice(0, -1);
  console.log(rollList);
  if (length > 0) {
    if (qcTransactionId != '') {
      if (grnNo != '') {
        if (confirm("Are you sure to Edit this Fabrics Quality Control...")) {
          $.ajax({
            type: 'POST',
            dataType: 'json',
            url: './editFabricsQC',
            data: {
              qcTransactionId: qcTransactionId,
              qcDate: qcDate,
              grnNo: grnNo,
              remarks: remarks,
              rollList: rollList,
              userId: userId
            },
            success: function (data) {
              if (data.result == "Something Wrong") {
                dangerAlert("Something went wrong");
              } else if (data.result == "duplicate") {
                dangerAlert("Duplicate Item Name..This Item Name Already Exist")
              } else {
                alert("Successfully Edit...");
                refreshAction();
              }
            }
          });
        }
      } else {
        warningAlert("Please Enter GRN No")
        $("#grnNo").focus();
      }
    } else {
      warningAlert("Please Get a transaction Id")
      $("#transactionId").focus();
    }
  } else {
    warningAlert("Please Enter Fabrics Roll");
  }
}

function refreshAction() {
  location.reload();

}


function drawFabricsReceiveListTable(data) {
  let rows = [];
  const length = data.length;

  for (let i = 0; i < length; i++) {
    const rowData = data[i];
    let row = $("<tr/>")
    row.append($("<td>" + rowData.transactionId + "</td>"));
    row.append($("<td>" + rowData.grnNo + "</td>"));
    row.append($("<td>" + rowData.grnDate + "</td>"));
    row.append($("<td ><i class='fa fa-search' onclick=\"setFabricsReceiveInfo('" + rowData.transactionId + "')\" style='cursor:pointer;'> </i></td>"));

    rows.push(row);
  }

  return rows;
}

function drawFabricsRollListTable(data,isSearch) {
  const length = data.length;
  let rows = "";
  $("#rollList").empty();
  
  let options = "<option  id='qcPassed-1' value='1'>QC Passed</option><option  id='qcPassed-1' value='2'>QC Failed</option>";
  for (let i = 0; i < length; i++) {

    const rowData = data[i];
    const id = rowData.autoId;
    rows += "<tr id='rowId-" + id + "' data-purchase-order='" + rowData.purchaseOrder + "' data-style-id='" + rowData.styleId + "' data-item-id='" + rowData.itemId + "' data-item-color-id='" + rowData.itemColorId + "' data-fabrics-id='" + rowData.fabricsId + "' data-fabrics-color-id='" + rowData.fabricsColorId + "' data-roll-id='"+rowData.rollId+"' data-unit-id='" + rowData.unitId + "'>"
      + "<td id='fabricsName-" + id + "'>" + rowData.fabricsName + "</td>"
      + "<td id='fabricsColor-" + id + "'>" + rowData.fabricsColorName + "</td>"
      + "<td id='rollId-" + id + "'>" + rowData.supplierRollId + "</td>"
      + "<td id='unitQty-" + id + "'>" + rowData.unitQty + "</td>"
      + "<td id='qcPassedQty-" + rowData.autoId + "'>"+rowData.qcPassedQty+"</td>"
      + "<td id='checkQty-" + rowData.autoId + "'  contenteditable = 'true'>" + (isSearch? rowData.unitQty : (rowData.unitQty - rowData.qcPassedQty)) + "</td>"
      + "<td id='unit-" + id + "'>" + rowData.unit + "</td>"
      + "<td id='shade-" + id + "'>0</td>"
      + "<td id='shrinkage-" + id + "'>0</td>"
      + "<td id='gsm-" + id + "'>0</td>"
      + "<td id='width-" + id + "'>0</td>"
      + "<td id='defect-" + id + "'>0</td>"
      + "<td id='rackName-" + id + "'>" + rowData.rackName + "</td>"
      + "<td id='binName-" + id + "'>" + rowData.binName + "</td>"
      + "<td><select id='qcPassed-" + rowData.autoId + "' class='form-control-sm px-0 bg-success' onchange='qcPassedChangeBackground(this)'>" + options + "</select></td>"
      + "<td class='text-center align-middle'><input class='check' id='isCheck-" + rowData.autoId + "' type='checkbox'></td>"
      + "</tr>";
  }
  $("#rollList").html(rows);
  for (let i = 0; i < length; i++) {
    const rowData = data[i];
    let element = document.getElementById("qcPassed-" + rowData.autoId);
    if (rowData.qcPassedType == 1) {

      element.classList.remove('bg-danger');
      element.classList.add('bg-success');
    } else {
      element.classList.remove('bg-success');
      element.classList.add('bg-danger');
    }
    element.value = rowData.qcPassedType;
  }
}



function drawFabricsQCListTable(data) {
  const length = data.length;
  let tr_list = "";
  $("#fabricsQCList").empty();
  let options = "<option value='1'>QC Passed</option><option value='2'>QC Failed</option>";
  for (let i = 0; i < length; i++) {
    const rowData = data[i];
    tr_list = tr_list + "<tr id='row-" + rowData.autoId + "'>"
      + "<td>" + rowData.qcTransactionId + "</td>"
      + "<td>" + rowData.qcDate + "</td>"
      + "<td>" + rowData.grnNo + "</td>"
      + "<td ><i class='fa fa-search' onclick=\"setFabricsQCInfo('" + rowData.qcTransactionId + "')\" style='cursor:pointer;'> </i></td>"
      + "</tr>";
  }
  $("#fabricsQCList").html(tr_list);
}
function qcPassedChangeBackground(element) {
  if (element.value == 1) {
    element.classList.remove('bg-danger');
    element.classList.add('bg-success');
  } else {
    element.classList.remove('bg-success');
    element.classList.add('bg-danger');
  }
}

document.getElementById("allCheck").addEventListener("click", function () {
  if ($(this).prop('checked')) {
    $(".check").prop('checked', true);
  } else {
    $(".check").prop('checked', false);
  }

});

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
  $("input").focus(function () { $(this).select(); });
});
$(document).ready(function () {
  $("#search").on("keyup", function () {
    let value = $(this).val().toLowerCase();
    $("#poList tr").filter(function () {
      $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
    });
  });
});


let today = new Date();
document.getElementById("qcDate").value = today.getFullYear() + '-' + ('0' + (today.getMonth() + 1)).slice(-2) + '-' + ('0' + today.getDate()).slice(-2);
