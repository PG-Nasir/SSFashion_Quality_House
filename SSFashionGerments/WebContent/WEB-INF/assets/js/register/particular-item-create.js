

window.onload = ()=>{
	document.title = "Particular Item Create";
} 

function deleteCostingItem(itemId){
    if(confirm("Are you sure to delete this Item?")){
    	
   	 var userId = $("#userId").val();
   	 var linkName=$('#linkName').val();
   	  
       $("#loader").show();
       $.ajax({
         type: 'POST',
         dataType: 'json',
         url: './deleteCostingItem',
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
  let particularItemName = $("#particularItemName").val().trim();
  let userId = $("#userId").val();

  if (particularItemName != '') {
    $("#loader").show();
    $.ajax({
      type: 'POST',
      dataType: 'json',
      url: './saveParticularItem',
      data: {
        particularItemId: "0",
        particularItemName: particularItemName,
        userId: userId
      },
      success: function (data) {
        if (data.result == "Something Wrong") {
          dangerAlert("Something went wrong");
        } else if (data.result == "duplicate") {
          dangerAlert("Duplicate Particular Item Name..This Particular Item Name Allreary Exist")
        } else {
          successAlert("Particular Item Name Save Successfully");

          $("#dataList").empty();
          $("#dataList").append(drawDataTable(data.result));

        }
        $("#loader").hide();
      }
    });
  } else {
    warningAlert("Empty Particular Item Name... Please Enter Particular Item Name");
  }
}


function editAction() {
  let particularItemId = $("#particularItemId").val();
  let particularItemName = $("#particularItemName").val().trim();
  let userId = $("#userId").val();

  if (particularItemName != '') {
    $("#loader").show();
    $.ajax({
      type: 'POST',
      dataType: 'json',
      url: './editParticularItem',
      data: {
        particularItemId: particularItemId,
        particularItemName: particularItemName,
        userId: userId
      },
      success: function (data) {
        if (data.result == "Something Wrong") {
          dangerAlert("Something went wrong");
        } else if (data.result == "duplicate") {
          dangerAlert("Duplicate Particular Item Name..This Particular Item Name Allreary Exist")
        } else {
          successAlert("Particular Item Name Edit Successfully");

          $("#dataList").empty();
          $("#dataList").append(drawDataTable(data.result));

        }
        $("#loader").hide();
      }
    });
  } else {
    warningAlert("Empty Particular Item Name... Please Enter Particular Item Name");
  }
}


function refreshAction() {
  location.reload();
  /*let element = $(".alert");
  element.hide();
  document.getElementById("particularItemId").value = "0";
  document.getElementById("particularItemName").value = "";
  document.getElementById("btnSave").disabled = false;
  document.getElementById("btnEdit").disabled = true;*/
}


function setData(particularItemId) {


  document.getElementById("particularItemId").value = particularItemId;
  document.getElementById("particularItemName").value = document.getElementById("particularItemName" + particularItemId).innerHTML;
  $("#btnSave").hide();
  $("#btnEdit").show();

}

function drawDataTable(data) {
  let rows = [];
  let length = data.length;

  for (let i = 0; i < length; i++) {
    rows.push(drawRowDataTable(data[i], i+1));
  }

  return rows;
}

function drawRowDataTable(rowData, c) {

  let row = $("<tr />")
  row.append($("<td>" + rowData.particularItemId + "</td>"));
  row.append($("<td id='particularItemName" + rowData.particularItemId + "'>" + rowData.particularItemName + "</td>"));
  row.append($("<td ><i class='fa fa-edit' onclick=\"setData(" + rowData.particularItemId + ")\"> </i></td>"));
  row.append($("<td ><i class='fa fa-trash' onclick=\"deleteCostingItem(" + rowData.particularItemId + ")\"> </i></td>"));

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
  document.getElementById("warningAlert").innerHTML = "<strong>Warning!</strong> "+message+"..";
  element.show();
  setTimeout(() => {
    element.toggle('fade');
  }, 2500);
}

function dangerAlert(message) {
  let element = $(".alert");
  element.hide();
  element = $(".alert-danger");
  document.getElementById("dangerAlert").innerHTML = "<strong>Duplicate!</strong> "+message+"..";
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

