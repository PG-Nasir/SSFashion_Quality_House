window.onload = ()=>{
	document.title = "General Item Create";
  } 
function saveAction() {	
  let itemName = $("#ItemName").val();
  let categoryId = $("#categoryId").val();
  let unitId = $("#unit").val();
  let buyPrice = $("#buyPrice").val()==''?"0": $("#buyPrice").val();
  let openingStock = $("#openingStock").val()==''?"0": $("#openingStock").val();
  let stockLimit = $("#stockLimit").val()==''?"0": $("#stockLimit").val();
  let userId = $("#userId").val();
  

  if (itemName != '') {
	  if(categoryId!=''){
		    $.ajax({
		        type: 'POST',
		        dataType: 'json',
		        url: './saveGeneralItem',
		        data: {
		      	itemId: "0",
		      	itemName: itemName,
		      	categoryId: categoryId,
		      	unitId:unitId,
		      	buyPrice:buyPrice,
		      	openingStock:openingStock,
		      	stockLimit:stockLimit,
		        userId: userId
		        },
		        success: function (data) {
		          if (data.result == "Something Wrong") {
		            dangerAlert("Something went wrong");
		          } else if (data.result == "duplicate") {
		            dangerAlert("Duplicate General Item Name..This Merchandiser Name Allreary Exist")
		          } else {
		            successAlert("General Item Name Save Successfully");

		            $("#dataList").empty();
		            $("#dataList").append(drawDataTable(data.result));

		          }
		        }
		      });
	  }
	  else{
		    warningAlert("Empty Category... Please Enter Category Name");
	  }
	  
  } else {
    warningAlert("Empty Item Name... Please Enter Item Name");
  }
}

function editAction() {

	  let itemId = $("#itemId").val();
	  let itemName = $("#ItemName").val();
	  let categoryId = $("#categoryId").val();
	  let unitId = $("#unit").val();
	  let buyPrice = $("#buyPrice").val()==''?"0": $("#buyPrice").val();
	  let openingStock = $("#openingStock").val()==''?"0": $("#openingStock").val();
	  let stockLimit = $("#stockLimit").val()==''?"0": $("#stockLimit").val();
	  let userId = $("#userId").val();
	  

	  if (itemName != '') {
		  if(categoryId!=''){
			    $.ajax({
			        type: 'POST',
			        dataType: 'json',
			        url: './editGeneralItem',
			        data: {
			      	itemId: itemId,
			      	itemName: itemName,
			      	categoryId: categoryId,
			      	unitId:unitId,
			      	buyPrice:buyPrice,
			      	openingStock:openingStock,
			      	stockLimit:stockLimit,
			        userId: userId
			        },
			        success: function (data) {
			          if (data.result == "Something Wrong") {
			            dangerAlert("Something went wrong");
			          } else if (data.result == "duplicate") {
			            dangerAlert("Duplicate General Item Name..This General Item Name Allreary Exist")
			          } else {
			            successAlert("General Item Name Save Successfully");

			            $("#dataList").empty();
			            $("#dataList").append(drawDataTable(data.result));

			          }
			        }
			      });
		  }
		  else{
			    warningAlert("Empty Category... Please Enter Category Name");
		  }
		  
	  } else {
	    warningAlert("Empty Item Name... Please Enter Item Name");
	  }
	}



function refreshAction() {
  location.reload();
  /*let element = $(".alert");
  element.hide();
  document.getElementById("fabricsItemId").value = "0";
  document.getElementById("fabricsItemName").value = "";
  document.getElementById("reference").value = "";
  document.getElementById("btnSave").disabled = false;
  document.getElementById("btnEdit").disabled = true;*/
}


function setData(itemId) {


	console.log(" unit id "+$('#UnitId'+itemId).val())
  document.getElementById("itemId").value = itemId;
  document.getElementById("ItemName").value = $('#ItemName'+itemId).val();
  document.getElementById("categoryId").value = $('#CategoryId'+itemId).val();
  $('.selectpicker').selectpicker('refresh')
  document.getElementById("unit").value = $('#UnitId'+itemId).val();
  $('.selectpicker').selectpicker('refresh')
  var buyprice=$('#BuyPrice'+itemId).val();
  buyprice=Math.floor(buyprice*100)/100;
  document.getElementById("buyPrice").value = buyprice.toFixed(2);
  
  var openingStock=$('#OpeningStock'+itemId).val();
  openingStock=Math.floor(openingStock*100)/100;  
  document.getElementById("openingStock").value = openingStock.toFixed(2);
  
  var stockLimit=$('#StockLimit'+itemId).val();
  stockLimit=Math.floor(stockLimit*100)/100; 
  document.getElementById("stockLimit").value = stockLimit.toFixed(2);

/*  document.getElementById("buyPrice").value = document.getElementById("buyPrice" + itemId).value;
  document.getElementById("openingStock").value = document.getElementById("openingStock" +itemId).value;
  document.getElementById("stockLimit").value = document.getElementById("stockLimit" +itemId).value;*/
  document.getElementById("btnSave").disabled = true;
  document.getElementById("btnEdit").disabled = false;

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
  row.append($("<td>" + c + "</td>"));
  row.append($("<td id='itemName" + rowData.itemId + "'>" + rowData.itemName + "</td>"));
  row.append($("<td id='Category" + rowData.itemId + "'>" + rowData.categoryName + "</td>"));
  row.append($("<td>" +
		"<input type='hidden' id='ItemName"+rowData.ItemId+"' value='"+rowData.categoryId+"' />" +
  		"<input type='hidden' id='CategoryId"+rowData.itemId+"' value='"+rowData.categoryId+"' />" +
  		"<input type='hidden' id='UnitId"+rowData.itemId+"' value='"+rowData.unitId+"' />" +
  		"<input type='hidden' id='BuyPrice"+rowData.itemId+"' value='"+rowData.buyPrice+"' />" +
  		"<input type='hidden' id='OpeningStock"+rowData.itemId+"' value='"+rowData.openingStock+"' />" +
  		"<input type='hidden' id='StockLimit"+rowData.itemId+"' value='"+rowData.stockLimit+"' />" +
  		"<i class='fa fa-edit' onclick='setData(" + rowData.itemId + ")'> </i></td>"));

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


jQuery.extend(jQuery.expr[':'], {
    focusable: function (el, index, selector) {
        return $(el).is('a, button, :input, [tabindex]');
    }
});

$(document).on('keypress', 'input,select', function (e) {
    if (e.which == 13) {
        e.preventDefault();
        var $canfocus = $(':focusable');
        var index = $canfocus.index(document.activeElement) + 1;
        if (index >= $canfocus.length) index = 0;
        $canfocus.eq(index).focus();
    }
});
