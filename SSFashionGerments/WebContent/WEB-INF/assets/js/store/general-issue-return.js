window.onload = ()=>{
	console.log(" general store transfer ")
	document.title = "General Store Transfer Out";
} 

var type=6;
var searched=0;
var searchedinvoice=0;





$("#newFabricsTransferOutBtn").click(function () {

	console.log(" tb button")

	$("#transferOutTransactionId").val("-- New Transaction --");
	$("#btnSubmit").prop("disabled", false);
	$("#btnEdit").prop("disabled", true);
});

$("#findFabricsTransferOutBtn").click(function () {
	$.ajax({
		type: 'POST',
		dataType: 'json',
		url: './getGeneralTransferOutList',
		data: {
			type:type
		},
		success: function (data) {
			fetchinvoice(data)
		}
	});
});


function fetchinvoice(data){

	$('#invoicelist').empty();
	var rowIdx = 0; 
	for (var i = 0; i < data.length; i++) {
		rowIdx=i+1;
		//var status=data[i].status=="0"?"Not Approved":"Approved"
		/*let rec=parseInt(data[i].receive)
		console.log(" rec "+rec);
		let trin=parseInt(data[i].transferin)
		console.log(" trin "+trin);
		let trout=parseInt(data[i].transferout)
		console.log(" trout "+trout);
		let issue=parseInt(data[i].issue)
		console.log(" issue "+issue);
		let returned=parseInt(data[i].returned)
		console.log(" returend "+returned);

		const balance=(rec+trin)-(trout+issue+returned);
		console.log(" balance "+balance)*/

		$('#invoicelist').append(`<tr name="tr" data-reqid="${data.requestid}" id="R${rowIdx}">



				<td class="row-index text-center" id="invoice-${rowIdx}" value="${data[i].transferid}" data-itemid-${rowIdx}="${data[i].transferid}"> <p>${data[i].transferid}</p></td> 
				<td class="row-index text-center" id="unitname-${rowIdx}" data-unitid-${rowIdx}="${data[i].transferid}" > <p>${data[i].transefdate}</p></td>
				<td class="row-index text-center" id="receiveqty-${rowIdx}" data-receiveqty-${rowIdx}="${data[i].transferid}"> <p>${data[i].department}</p></td>
				<td><i class="fa fa-search" id="click-${rowIdx}" onclick="searchInvoice(this)"> </i></td>

		</tr>`); 

	}



}


function searchInvoice(a){
	var rowIndex = $(a).closest('tr').index();
	var initindex=rowIndex+1
	console.log(" initindex "+initindex)

	var invoice=$("#invoice-"+initindex).text()
	console.log(" invoice no "+invoice)
	searchedinvoice=invoice;
	console.log(" searched invoice "+searchedinvoice )

	$.ajax({
		type: 'POST',
		dataType: 'json',
		url: './getInvoiceItems',
		data: {
			invoice:invoice,
			type:type
		},
		success: function (data) {
			$("#transferInSearchModal").modal('hide');
			document.getElementById("btnEdit").disabled = false;
			document.getElementById("btnSubmit").disabled = true;
			//searchedinvoice=data[0].transferid;
			var date = data[0].transferdate;
			var newdate = date.split("/").reverse().join("-");
			console.log(" date "+newdate)
			$("#transferOutDate").val(newdate)
			$("#department").val(data[0].detparmentid)
			$('.selectpicker').selectpicker('refresh')
			$("#receiveBy").val(data[0].receivedby)
			$("#remarks").val(data[0].remark)
			datafetch(data)
			searched=1;

		}
	});

}


$("#itemsearch").click(function () {
	const departmentId = $("#department").val();

	if (departmentId!=0) {
		$.ajax({
			type: 'GET',
			dataType: 'json',
			url: './getAvailableFabricsRollList',
			data: {
				departmentId : departmentId
			},
			success: function (data) {

				$("#rollSearchModal").modal('show');
			}
		});
	}else{
		warningAlert("Select Department")
	}

});


function editItemInDatabase(autoId) {
	const transferOutQty = $("#rollTransferOutQty-" + autoId).val();

	$.ajax({
		type: 'GET',
		dataType: 'json',
		url: './editTransferOutRollInTransaction',
		data: {
			autoId: autoId,
			unitQty: transferOutQty
		},
		success: function (data) {
			if (data.result == "Successful") {
				alert("Edit Successfully...");
			} else if (data.result == "TransferOut Qty Exist") {
				alert("TransferOut Qty Exist...");
			}
		}
	});
}
function deleteItemFromDatabase(autoId) {
	if(confirm("Are You Sure To Delete...")){
		$.ajax({
			type: 'GET',
			dataType: 'json',
			url: './deleteTransferOutRollFromTransaction',
			data: {
				autoId: autoId
			},
			success: function (data) {
				if (data.result == "Successful") {
					const parentRowId = $("#rowId-" + autoId).attr('data-parent-row');
					$("#rowId-" + autoId).remove();
					if ($(".rowGroup-" + parentRowId).length == 0) {
						$(".parentRowGroup-" + parentRowId).remove();
					}
				} else {

				}

			}
		});
	}

}

function deleteItemFromList(rowId) {
	const parentRowId = $("#rowId-" + rowId).attr('data-parent-row');
	$("#rowId-" + rowId).remove();

	if ($(".rowGroup-" + parentRowId).length == 0) {
		$(".parentRowGroup-" + parentRowId).remove();
	}
}

function totalTransferOutQtyCount(id) {
	const elements = $(".rollTransferOutGroup-" + id);
	const length = elements.length;
	let total = 0;
	for (let i = 0; i < length; i++) {

		total += Number(elements[i].value);
	};
	$("#transferOutQty-" + id).text(total);
	$("#bottomTotalTransferOut-" + id).text(total);
}

document.getElementById("checkAll").addEventListener("click", function () {
	if ($(this).prop('checked')) {
		$(".check").prop('checked', true);
	} else {
		$(".check").prop('checked', false);
	}
});

$("#rollAddBtn").click(function(){

	const itemids =[];

	const rowList=$("#itemsearchlist tr").length;

	for (var i = 0; i < rowList; i++) {
		let k=i+1;
		var id=$("#id"+k).attr("data-itemid");
		if($("#check"+id).is(":checked")){
			console.log(" ids "+id)


			itemids.push(id)	
		}
	}


	console.log("ids "+itemids)



	$.ajax({
		type: 'POST',
		dataType: 'json',
		url: './getitemsDetails',
		data: {

			itemids: itemids
		},
		success: function (data) {
			$("#rollSearchModal").modal('hide');
			datafetch(data)
			/*if (data.result == "Successful") {
	          const parentRowId = $("#rowId-" + autoId).attr('data-parent-row');
	          $("#rowId-" + autoId).remove();
	          if ($(".rowGroup-" + parentRowId).length == 0) {
	            $(".parentRowGroup-" + parentRowId).remove();
	          }
	        } else {

	        }*/

		}
	});
});


function datafetch(data){

	$('#itemlist').empty();
	var rowIdx = 0; 
	for (var i = 0; i < data.length; i++) {
		rowIdx=i+1;
		//var status=data[i].status=="0"?"Not Approved":"Approved"
		var recc=parseInt(data[i].receive)
		var rec=Math.floor(recc*100)/100;
		rec.toFixed(2);


		console.log(" rec "+rec);
		let trin=parseInt(data[i].transferin)
		trin=Math.floor(trin*100)/100;
		trin.toFixed(2);

		console.log(" trin "+trin);
		let trout=parseInt(data[i].transferout)		
		trout=Math.floor(trout*100)/100;
		trout.toFixed(2);
		console.log(" trout "+trout);


		let issue=parseInt(data[i].issue)
		issue=Math.floor(issue*100)/100;
		issue.toFixed(2);;
		
		let issuereturn=parseInt(data[i].issuereturned)
		issuereturn=Math.floor(issuereturn*100)/100;
		issuereturn.toFixed(2);;

		console.log(" issue "+issue);
		let returned=parseInt(data[i].returned)
		returned=Math.floor(returned*100)/100;
		returned.toFixed(2);;
		console.log(" returend "+returned);

		let balance=(issue-issuereturn);
		balance=Math.floor(balance*100)/100;
		balance.toFixed(2);;
		console.log(" balance "+balance)

		console.log(" qty "+data[i].qty)
		let qty=data[i].qty
		qty=Math.floor(qty*100)/100;
		qty.toFixed(2);;
		$('#itemlist').append(`<tr name="tr" data-reqid="${data.requestid}" id="R${rowIdx}">



				<td class="row-index text-center" id="itemname-${rowIdx}" data-itemid-${rowIdx}="${data[i].itemid}"> <p>${data[i].itemname}</p></td> 
				<td class="row-index text-center" id="unitname-${rowIdx}" data-unitid-${rowIdx}="${data[i].unitid}" > <p>${data[i].unit}</p></td>
				<td class="row-index text-center" id="receiveqty-${rowIdx}" data-receiveqty-${rowIdx}="${data[i].itemid}"> <p>${rec}</p></td>

				<td class="row-index text-center"   data-stock="stock-${rowIdx}"> <p>${trin}</p></input></td>

				<td class="row-index text-center"   data-trout="trout-${rowIdx}"> <p>${trout}</p></td>
				<td class="row-index text-center"   data-issue="issue-${rowIdx}"> <p>${issue}</p></td>
				<td class="row-index text-center"   data-issue="issue-${rowIdx}"> <p>${issuereturn}</p></td>
				<td class="row-index text-center"   data-bal="bal-${rowIdx}" id="bal-${rowIdx}"> <p>${balance}</p></td>
				<td class="row-index text-center"   data-trqty="trqty-${rowIdx}"><input id="issue-${rowIdx}" value="${qty}" class="form-control-sm" type="text" onkeyup="checkitembalance(this)"></input></td>

		</tr>`); 

	}



}

function submitAction() {
	const transferOutTransactionId = $("#transferOutTransactionId").val();
	const transferOutDate = $("#transferOutDate").val();
	const transferOutDepartmentId = $("#department").val();
	const receiveBy = $("#receiveBy").val();
	const remarks = $("#remarks").val();
	const userId = $("#userId").val();
	if (searched=="0") {
		if (transferOutTransactionId=="") {
			warningAlert("Press Invoice Button")
		}

	}else{
		transferOutTransactionId=searchedinvoice;
	}


	if(transferOutTransactionId==""){
		warningAlert("Press Invoice Button")
	}else if(transferOutDepartmentId==0){
			warningAlert("Select Department")
		}else if(receiveBy==""){
			warningAlert("Insert Receiveb By")
		}else{
			const length=$("#ptable > tbody > tr").length;
			console.log(" length "+length)
			var index=0;


			var items=[];
			var unit=[];
			var troutqty=[];

			let valid=0;




			for (var i = 0; i < length; i++) {
				index=i+1
				var troutqtyy= $("#issue-"+index).val()
				console.log(" issuing qty "+troutqtyy)
				if (parseInt(troutqtyy)> 0) {
					var itemid=  $("#itemname-"+index).data('itemid-'+index);
					console.log("item id "+itemid)
					items.push(itemid);


					var unitid= $("#unitname-"+index).data('unitid-'+index);
					console.log("unit id "+unitid)
					unit.push(unitid);

					console.log("troutqty "+troutqtyy)
					troutqty.push(troutqtyy);

					valid++;
				}
			}

			

			if (parseInt(valid)>0) {
				$.ajax({
					type: 'POST',
					dataType: 'json',
					url: './insertTransferOut',
					data: {
						//transferid:searchedinvoice,
						type:type,
						transferid : searchedinvoice,
						transefdate : transferOutDate,
						detparmentid : transferOutDepartmentId,
						receivedby : receiveBy,
						remark : remarks,
						userid: userId,

						itemids:items,
						unitids:unit,
						troutqty:troutqty,
						searched:searched

					},
					success: function (data) {
						successAlert("Successfully Inserted")
						refreshAction()

					}
				});
			}
		}
	

}



function checkitembalance(a){
	var rowIndex = $(a).closest('tr').index();
	var initindex=rowIndex+1
	console.log(" initindex "+initindex)

	var trqty=parseInt($(a).val());
	console.log(" tr qty "+trqty)

	var balanceqty=parseInt($("#bal-"+initindex).text());
	console.log(" present bal "+balanceqty)

	if (trqty>balanceqty) {
		alert("Transer Qty Cannot Exceed Balance")
		$(a).val("0")
	}

}








function refreshAction() {
	location.reload();

}







$(document).ready(function () {
	$('.table-expandable tbody').on("click", ".odd", function () {
		let element = $(this);
		element.next('tr').toggle(0);
		element.find(".table-expandable-arrow").toggleClass("up");
	})
});

function qcPassedChangeBackground(element){
	if(element.value==1){
		element.classList.remove('bg-danger');
		element.classList.add('bg-success');
	}else{
		element.classList.remove('bg-success');
		element.classList.add('bg-danger');
	}
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
	$("input").focus(function () { $(this).select(); });
});
$(document).ready(function () {
	$("#purchaseOrderSearch , #styleNoSearch, #itemNameSearch,#fabricsItemSearch,#colorSearch,#rollIdSearch").on("keyup", function () {
		const po = $("#purchaseOrderSearch").val().toLowerCase();
		const style = $("#styleNoSearch").val().toLowerCase();
		const item = $("#itemNameSearch").val().toLowerCase();
		const fabrics = $("#fabricsItemSearch").val().toLowerCase();
		const color = $("#colorSearch").val().toLowerCase();
		const rollId = $("#rollIdSearch").val().toLowerCase();

		$("#fabricsRollSearchList tr").filter(function () {
			const id = this.id.slice(4);

			if($("#check-"+id).prop('checked') || ( ( !po.length || $("#purchaseOrder-"+id).text().toLowerCase().indexOf(po) > -1 ) && 
					( !style.length || $("#styleNo-"+id).text().toLowerCase().indexOf(style) > -1 ) &&
					( !item.length || $("#itemName-"+id).text().toLowerCase().indexOf(item) > -1 ) &&
					( !fabrics.length || $("#fabricsName-"+id).text().toLowerCase().indexOf(fabrics) > -1 ) &&
					( !color.length || $("#itemColor-"+id).text().toLowerCase().indexOf(color) > -1 || $("#fabricsColor-"+id).text().toLowerCase().indexOf(color) > -1 )  &&
					( !rollId.length || $("#rollId-"+id).text().toLowerCase().indexOf(rollId) > -1 ) ) ){      
				$(this).show();
			}else{      
				$(this).hide();
			}
		});
	});
});


let today = new Date();
document.getElementById("transferOutDate").value = today.getFullYear() + '-' + ('0' + (today.getMonth() + 1)).slice(-2) + '-' + ('0' + today.getDate()).slice(-2);


