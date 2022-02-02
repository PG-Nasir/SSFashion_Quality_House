
window.onload = ()=>{
	document.title = "Accessories Create";
} 

function deleteAccessoriesItem(itemId){
    if(confirm("Are you sure to delete this Item?")){
    	
    	 var userId = $("#userId").val();
    	 var linkName=$('#linkName').val();
    	  
        $("#loader").show();
        $.ajax({
          type: 'POST',
          dataType: 'json',
          url: '/deleteAccessoriesItem',
          data:{
        	  itemId:itemId,
        	  userId:userId,
        	  linkName:linkName
          },
          success: function (data) {
            if (data.result == "Something Wrong") {
              dangerAlert("Something went wrong");
            }
            else if(data.result =="You have no permission to delete this item"){
            	successAlert("You have no permission to delete this item");
            }
            else {
              
              $("#dataList").empty();
              $("#dataList").append(drawDataTable(data.result));
  
            }
            $("#loader").hide();
          }
        });
      }
}

function saveAction() {
  const accessoriesItemName = $("#accessoriesItemName").val().trim();
  const accessoriesItemCode = $("#accessoriesItemCode").val().trim();
  const unitId = $("#unit").val();
  const userId = $("#userId").val();

  if (accessoriesItemName != '') {
    if (unitId != '0') {
      $("#loader").show();
      $.ajax({
        type: 'POST',
        dataType: 'json',
        url: './saveAccessoriesItem',
        data: {
          accessoriesItemId: "0",
          accessoriesItemName: accessoriesItemName,
          accessoriesItemCode: accessoriesItemCode,
          unitId : unitId,
          userId: userId
        },
        success: function (data) {
          if (data.result == "Something Wrong") {
            dangerAlert("Something went wrong");
          } else if (data.result == "duplicate") {
            dangerAlert("Duplicate Accessories Item Name..This Accessories Item Name Allreary Exist")
          } else {
            successAlert("AccessoriesItem Name Save Successfully");

            $("#dataList").empty();
            $("#dataList").append(drawDataTable(data.result));

          }
          $("#loader").hide();
        }
      });

    } else {
      warningAlert("Unit Not Selected... Please Select Unit");
      $("#unit").focus();
    }
  } else {
    warningAlert("Empty Accessories Item Name... Please Enter Accessories Item Name");
    $("#accessoriesItemName").focus();
  }
}


function editAction() {
  const accessoriesItemId = $("#accessoriesItemId").val();
  const accessoriesItemName = $("#accessoriesItemName").val().trim();
  const accessoriesItemCode = $("#accessoriesItemCode").val().trim();
  const unitId = $("#unit").val();
  const userId = $("#userId").val();

  if (accessoriesItemName != '') {
    if (unitId != '0') {
      $("#loader").show();
      $.ajax({
        type: 'POST',
        dataType: 'json',
        url: './editAccessoriesItem',
        data: {
          accessoriesItemId: accessoriesItemId,
          accessoriesItemName: accessoriesItemName,
          accessoriesItemCode: accessoriesItemCode,
          unitId : unitId,
          userId: userId
        },
        success: function (data) {
          if (data.result == "Something Wrong") {
            dangerAlert("Something went wrong");
          } else if (data.result == "duplicate") {
            dangerAlert("Duplicate Accessories Item Name..This Accessories Item Name Already Exist")
          } else {
            successAlert("Accessories Item Name Edit Successfully");

            $("#dataList").empty();
            $("#dataList").append(drawDataTable(data.result));

          }
          $("#loader").hide();
        }
      });

    } else {
      warningAlert("Unit Not Selected... Please Select Unit");
      $("#unit").focus();
    }
  } else {
    warningAlert("Empty Accessories Item Name... Please Enter Accessories Item Name");
    $("#accessoriesItemName").focus();
  }
}


function refreshAction() {
  location.reload();
  /*let element = $(".alert");
  element.hide();
  document.getElementById("accessoriesItemId").value = "0";
  document.getElementById("accessoriesItemName").value = "";
  document.getElementById("accessoriesItemCode").value = "";
  document.getElementById("btnSave").disabled = false;
  document.getElementById("btnEdit").disabled = true;*/
}

function unitAddAction(){
  const unitQty = $("#unitQty").val();
  const accessoriesItemId = $("#accessoriesItemId").val();
  const itemType = $("#itemType").val();
  const unitId = $("#unit").val();
  const userId = $("#userId").val();

  if (accessoriesItemId != '') {
    if (unitId != '0') {
      if (unitQty != '' && Number(unitQty) > 0) {
        if(confirm("Are you sure to Add this Unit with Quantity '"+ unitQty +"'?")){
          $("#loader").show();
          $.ajax({
            type: 'POST',
            dataType: 'json',
            url: './addItemUnits',
            data: {
              unitId : unitId,
              unitQty : unitQty,
              userId : userId,
              itemId : accessoriesItemId,
              itemType : itemType
            },
            success: function (data) {
              if (data.result == "Something Wrong") {
                dangerAlert("Something went wrong");
              } else if (data.result == "duplicate") {
                dangerAlert("Duplicate Fabrics Item Name..This Fabrics Item Name Allreary Exist")
              } else {
                drawUnitTable(data.result);
              }
              $("#loader").hide();
            }
          });
        }
        
      } else {
        warningAlert("Invalid Minimum Qty... Please Enter Valid Minimum Qty");
        $("#unitQty").focus();
      }
    }
    else {
      warningAlert("Unit Not Selected... Please Select Unit");
      $("#unit").focus();
    }
  } else {
    warningAlert("Empty Accessories Item Name... Please Select Accessories Item Name");
    $("#accessoriesItemId").focus();
  }
}

function setData(accessoriesItemId) {
  $("#loader").show();
  $.ajax({
    type: 'POST',
    dataType: 'json',
    url: './getAccessoriesItem',
    data: {
      accessoriesItemId: accessoriesItemId,
    },
    success: function (data) {
      if (data.result == "Something Wrong") {
        dangerAlert("Something went wrong");
      } else if (data.result == "duplicate") {
        dangerAlert("Duplicate Fabrics Item Name..This Fabrics Item Name Allreary Exist")
      } else {
        const accessoriesItem = data.result;
        $("#accessoriesItemId").val(accessoriesItem.accessoriesItemId);
        $("#accessoriesItemName").val(accessoriesItem.accessoriesItemName);
        $("#accessoriesItemCode").val(accessoriesItem.accessoriesItemCode);
        $("#unit").val(accessoriesItem.unitId).change();
        drawUnitTable(accessoriesItem.unitList);
        $("#btnSave").hide();
        $("#btnEdit").show();
      }
      $("#loader").hide();
    }
  });
  

}


function drawUnitTable(unitList) {
  const length = unitList.length;
  let rowList = "";
  for (let i = 0; i < length; i++) {
    rowList += '<tr>'
      + '<td>' + unitList[i].unitName + '</td>'
      + '<td>' + unitList[i].unitQty + '</td>'
      + '</tr>';
  }

  $("#unitList").html(rowList);
}


function drawDataTable(data) {
  let rows = [];
  let length = data.length;

  for (let i = 0; i < length; i++) {
    rows.push(drawRowDataTable(data[i], i));
  }

  return rows;
}

function drawRowDataTable(rowData, c) {

  let row = $("<tr />")
  row.append($("<td>" + rowData.accessoriesItemId + "</td>"));
  row.append($("<td id='accessoriesItemName" + rowData.accessoriesItemId + "'>" + rowData.accessoriesItemName + "</td>"));
  row.append($("<td id='accessoriesItemCode" + rowData.accessoriesItemId + "'>" + rowData.accessoriesItemCode + "</td>"));
  row.append($("<td ><i class='fa fa-edit' onclick=\"setData(" + rowData.accessoriesItemId + ")\" style='cursor:pointer;'>  </i></td>"));
  row.append($("<td ><i class='fa fa-trash' onclick=\"deleteAccessoriesItem(" + rowData.accessoriesItemId + ")\" style='cursor:pointer'> </i></td>"));
  return row;
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
    $("#dataList tr").filter(function () {
      $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
    });
  });
});


$('.inputs').keyup(function (e) {
	if (e.which === 13) {
		var index = $('.inputs').index(this) + 1;
		$('.inputs').eq(index).focus();
	}
});
