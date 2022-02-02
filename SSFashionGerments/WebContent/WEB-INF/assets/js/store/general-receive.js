window.onload = ()=>{
	document.title = "General Item Receive";
	calculate();
  } 

var today = moment().format('YYYY-MM-DD');
document.getElementById("challanDate").value = today;

var searched=0;

function calculate(){
	var rowCount = $('#dataList tr').length;
	let k=0;
	console.log("row count "+rowCount)
	var totalPrice=0;
	for (var i = 0; i < rowCount; i++) {
		k=i+1;
		totalPrice=totalPrice+parseInt($("#totalprice"+k).text());
		console.log(k+" totalPrice "+totalPrice)
	}
	$("#grossAmount").val(totalPrice);
	$("#netAmount").val(totalPrice);
}


function setGeneralReceivedInvoice(invoiceNo,type){
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: './GeneralReceivedInvoiceInfo',
        data: {
        invoiceNo:invoiceNo,
        type:type
        },
        success: function (data) {
            if (data== "Success") {
          		let url = "printGeneralReceivedInvoiceReportt";
        		window.open(url, '_blank');

              }
        }
      });
}
function confrimAction(){
	let userId = $("#userId").val();
	let invoiceNo = $("#invoiceNo").val();
	let date = $("#date").val();
	let challanNo = $("#challanNo").val();
	let supplierId = $("#supplierId").val();
	let type = "1";
	let amount=$("#grossAmount").val();
	let netamount=$("#netAmount").val();
	
	if (supplierId != '0') {
		if (date != '0') {
		    $.ajax({
		        type: 'POST',
		        dataType: 'json',
		        url: './confrimGeneralReceivedItem',
		        data: {
		        invoiceNo:invoiceNo,
		        supplierId: supplierId,
		        challanNo: challanNo,
		        userId: userId,
		        date: date,
		        type:type,
		        grossAmount:amount,
		        netAmount:netamount,
		        },
		        success: function (data) {
		          if (data.result == "Something Wrong") {
		             dangerAlert("Something went wrong");
		          }
		          else {
		            successAlert("General Item Received Save Successfully");

		            refreshAction();

		          }
		        }
		      });
		}
		else{
			warningAlert("Empty Date... Please Enter Date");
		}
	}
	else{
		warningAlert("Empty Supplier Name... Please Enter Supplier Name");
	}
}

function refreshAction() {
	  location.reload();

	}

function submitAction(){
	let userId = $("#userId").val();
	let type = "1";
	let invoiceNo = $("#invoiceNo").val();
	let ItemId = $("#ItemId").val();
	let unitId = $("#unitId").val();
	let qty = $("#qty").val()==''?"0":$("#qty").val();
	let pirce = $("#pirce").val()==''?"0":$("#pirce").val();

	if (ItemId != '0') {
		if (qty != '0') {
		    $.ajax({
		        type: 'POST',
		        dataType: 'json',
		        url: './addGeneralReceivedItem',
		        data: {
		        invoiceNo:invoiceNo,
		        ItemId: ItemId,
		        unitId: unitId,
		        qty: qty,
		        pirce:pirce,
		        type:type,
		        userId: userId,
		        searched:searched
		        },
		        success: function (data) {
		          if (data.result == "Something Wrong") {
		             dangerAlert("Something went wrong");
		          }
		          else {
		            successAlert("General Item Received Save Successfully");
		           
		            $("#dataList").empty();
		            $("#dataList").append(drawDataTable(data.result));

		          }
		         
		         calculate();
		        }
		      });
		}
		else{
			warningAlert("Empty Qty... Please Enter Qty");
		}
	}
	else{
		warningAlert("Empty Item Name... Please Enter Item Name");
	}
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
	  row.append($("<td>" + c + "</td>"));
	 /* row.append($("<td id='itemName" + rowData.autoId + "'>" + rowData.itemName + "</td>"));
	  row.append($("<td id='Category" + rowData.autoId + "'>" + parseFloat(rowData.qty).toFixed() + "</td>"));
	  row.append($("<td id='Category" + rowData.autoId + "'>" + parseFloat(rowData.pirce).toFixed() + "</td>"));
	  row.append($("<td id='Category" + rowData.autoId + "'>" + parseFloat(rowData.totalPrice).toFixed()+ "</td>"));
	  row.append($("<td><i class='fa fa-remove' onclick='setData(" + rowData.autoId + ")'> </i></td>"));
	  row.append($("<td><i class='fa fa-edit' onclick='setData(" + rowData.autoId + ")'> </i></td>"));*/
	 
	  
	  row.append($("<td id='item" + c + "'  data-item-id-"+c+"='"+rowData.autoId+"'>" + rowData.itemName + "</td>"));
	  row.append($("<td id='qty" + c + "' data-item-qty='qty"+rowData.autoId+"'>" + parseFloat(rowData.qty).toFixed() + "</td>"));
	  row.append($("<td id='price" + c + "' data-item-price='price"+rowData.autoId+"'>" + parseFloat(rowData.pirce).toFixed() + "</td>"));
	  row.append($("<td id='totalprice" + c + "' data-item-totalprice='totalprice"+rowData.autoId+"'>" + parseFloat(rowData.totalPrice).toFixed()+ "</td>"));
	  row.append($("<td><i class='fa fa-remove' onclick='deleteItem(this)'> </i></td>"));
	  row.append($("<td><i class='fa fa-edit' onclick='deleteItem(this)'> </i></td>"));

	  return row;
	}


function deleteItem(a){
	 var rowIndex = $(a).closest('tr').index();
	 var initindex=rowIndex+1
	
	 
	var autoid= $("#item"+initindex).attr('data-item-id-'+initindex)
	 console.log(" autoid "+autoid)
	 var userPreference;
	 let type = "1";
	let invoiceNo = $("#invoiceNo").val();
	 
	if (searched==0) {
		if (confirm("Do you want to Delete Selected Item?") == true) {
			 $.ajax({
			        type: 'POST',
			        dataType: 'json',
			        url: './deleteInvoiceItem',
			        data: {
			        	autoid:autoid,
			        invoiceno:invoiceNo,
			        type:type
			        },
			        success: function (data) {
			          if (data.result == "Something Wrong") {
			             dangerAlert("Something went wrong");
			          }
			          else {
			            successAlert("Item Deleted Successfully");
			           
			            $("#dataList").empty();
			            $("#dataList").append(drawDataTable(data.result));

			          }
			         
			         calculate();
			        }
			      });
		 }
	}else{
		console.log("Item Cannot Be Deleted After Invoice is Confirmed")
	}
	 
}


function setGeneralReceivedInvoicedata(invoiceNo,type){
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: './GeneralReceivedInvoiceInfodata',
        data: {
        invoiceNo:invoiceNo,
        type:type
        },
        success: function (data) {
        	searched=1
        	$("#invoiceNo").val(invoiceNo);
        	//$("#date").val(data.result1[0].supplierName);
        	 $("#challanNo").val(data.result1[0].challanNo);
        	 console.log(" challan no "+data.result1[0].challanNo)
        	 
        	 console.log(" supplier no "+data.result1[0].supplierName)
        	 $("#supplierId").val(data.result1[0].supplierId);
        	 $('.selectpicker').selectpicker('refresh')
        	 
        	 
        	
        	$("#grossAmount").val(data.result1[0].grossAmountssss);
        	$("#netAmount").val();
        	
        	 $("#dataList").empty();
	         $("#dataList").append(drawDataTable(data.result));
        }
      });
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