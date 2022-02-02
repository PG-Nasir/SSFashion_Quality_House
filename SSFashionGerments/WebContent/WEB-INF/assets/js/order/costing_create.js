let itemIdForSet = 0;
let particularItemIdForSet = 0;

window.onload = () => {
  document.title = "Costing Create";

  let sessionObject = JSON.parse(sessionStorage.getItem("pendingCosting") ? sessionStorage.getItem("pendingCosting") : false);
  let itemList = sessionObject.itemList ? sessionObject.itemList : [];
  if (sessionObject) {
    itemIdForSet = sessionObject.itemId;
    $("#styleName").val(sessionObject.styleId).change();
    drawSessionDataTable(itemList);
  }
}

function itemWiseCostingReport(styleId, itemId,costingNo) {


 
	
  let url = `printCostingReport/${styleId}/${itemId}/${costingNo}`;
  window.open(url, '_blank');

}

function styleWiseItemLoad() {
  let styleId = $("#styleName").val();
  if (styleId != 0) {
    $("#loader").show();
    $.ajax({
      type: 'GET',
      dataType: 'json',
      url: './getStyleWiseItem',
      data: {
        styleId: styleId
      },
      success: function (data) {

        let itemList = data.itemList;
        let options = "<option  value='0' selected>Select Item Name</option>";
        let length = itemList.length;
        for (let i = 0; i < length; i++) {
          options += "<option  value='" + itemList[i].itemId + "'>" + itemList[i].itemName + "</option>";
        };
        document.getElementById("itemName").innerHTML = options;
        $('#itemName').selectpicker('refresh');
        $("#itemName").prop("selectedIndex", 1).change();
        itemIdForSet = 0;
        $("#loader").hide();
      }
    });
  } else {
    let options = "<option  value='0' selected>Select Item Name</option>";
    $("#itemName").html(options);
    $('#itemName').selectpicker('refresh');
    $('#itemName').val(0).change();
    itemIdForSet = 0;
  }

}

function typeWiseParticularLoad() {
  let type = $("#particularType").val();

  if (type != 0) {
    $("#loader").show();
    $.ajax({
      type: 'GET',
      dataType: 'json',
      url: './typeWiseParticularLoad',
      data: {
        type: type
      },
      success: function (data) {

        let particularList = data.particularList;
        let options = "<option  value='0' selected>Select Particular Name</option>";
        let length = particularList.length;
        for (let i = 0; i < length; i++) {
          options += "<option  value='" + particularList[i].particularItemId + "'>" + particularList[i].particularItemName + "</option>";
        };
        document.getElementById("particularName").innerHTML = options;
        $('#particularName').selectpicker('refresh');
        $('#particularName').val(particularItemIdForSet).change();
        particularItemIdForSet = 0;
        $("#loader").hide();
      }
    });
  } else {
    let options = "<option  value='0' selected>Select Particular Name</option>";
    $("#particularName").html(options);
    $('#particularName').selectpicker('refresh');
    $('#particularName').val(0).change();
  }

}


function cloneButtonAction() {
  let styleId = $("#styleName").val();
  let itemId = $("#itemName").val();
  if (styleId != 0) {
    if (itemId != 0) {
      $('#cloneModal').modal('show');
      let element = $(".alert");
      element.hide();
    } else {
      warningAlert("Item Type not selected... Please Select Item Type");
      $("#itemName").focus();
    }
  } else {
    warningAlert("Style No not selected... Please Select Style No");
    $("#styleName").focus();
  }
}

function loadCostingOnStyleChange() {
  let styleId = $("#styleName").val();
  let itemId = $("#itemName").val();
  let costingNo = $("#costingNo").val();
  $("#dataList").empty();
  if (styleId != 0 && itemId != 0) {
    $("#loader").show();
    $.ajax({
      type: 'GET',
      dataType: 'json',
      url: './searchCosting',
      data: {
        styleId: styleId,
        itemId: itemId,
        costingNo : costingNo
      },
      success: function (data) {
        if (data.result == "Something Wrong") {
          dangerAlert("Something went wrong");
        } else if (data.result == "duplicate") {
          dangerAlert("Duplicate Item Name..This Item Name Already Exist")
        } else {
          $('#searchModal').modal('hide');
          let costingList = data.result;
          $("#dataList").empty();
          $("#dataList").append(drawDataTable(costingList));
          if (sessionStorage.getItem("pendingCosting")) {
            const pendingCosting = JSON.parse(sessionStorage.getItem("pendingCosting"));
            if (styleId == pendingCosting.styleId && itemId == pendingCosting.itemId) {
              $("#dataList").append(drawSessionDataTable(pendingCosting.itemList));
            }
          }
        }
        $("#loader").hide();
      }
    });
  }
}

function buyerWiseCostingLoad(){
  let buyerId = $("#buyerName").val();
  let userId = $("#userId").val();
  $("#loader").show();
  $.ajax({
    type: 'POST',
    dataType: 'json',
    url: './buyerWiseCostingSearch',
    data: {
      buyerId: buyerId,
      userId:userId
    },
    success: function (data) {
      $("#groupCostingList").empty();
      $("#groupCostingList").append(drawGroupCostingList(data.result));
      $("#loader").hide();
    }
  });
}

function printGroupCosting(){

  let groupStyleList = $("#groupCostingList tr");
  let costingId = '';
  groupStyleList.each((index,row)=>{

    let id = row.getAttribute('data-id');
    if($("#groupCheck-"+id).prop("checked")==true)
      costingId += id+",";
  });
  console.log("Costing id-",costingId);
  if(costingId.length >0){
    costingId = costingId.slice(0,-1);
    
    console.log("costingId "+costingId);
    
    let url = `printGroupCostingReport/${costingId}`;
    window.open(url, '_blank');
  
  }
}

function cloningCosting(costingNo,oldStyleId, oldItemId) {
  let newStyleId = $("#styleName").val();
  let newStyleName = $("#styleName option:selected").text();
  let newItemId = $("#itemName").val();
  let newItemName = $("#itemName option:selected").text();
  let userId = $("#userId").val();
  $("#loader").show();
  $.ajax({
    type: 'GET',
    dataType: 'json',
    url: './cloningCosting',
    data: {
      costingNo: costingNo,
      oldStyleId: oldStyleId,
      oldItemId: oldItemId,
      newStyleId: newStyleId,
      newItemId: newItemId,
      userId: userId
    },
    success: function (data) {

      if (data.result == "Something Wrong") {
        dangerAlert("Something went wrong");
      } else if (data.result == "duplicate") {
        dangerAlert("Duplicate Item Name..This Item Name Already Exist")
      } else {

        //$("#dataList").empty();
        // $("#dataList").append(drawDataTable(data.result));
        const rowList = $("#dataList tr");
        let idList = [];
        let length = rowList.length;
        for (let i = 0; i < length; i++) {
          idList.push(rowList[i].getAttribute("data-particular-id"));
        }
        const costingLength = data.result.length;

        if (costingLength > 0) {
          let sessionObject = JSON.parse(sessionStorage.getItem("pendingCosting") ? sessionStorage.getItem("pendingCosting") : "{}");
          let itemList = [];
          if (sessionObject) {
            if (newStyleId == sessionObject.styleId && newItemId == sessionObject.itemId) {
              itemList = sessionObject.itemList;
            }
          }
          for (let i = 0; i < costingLength; i++) {
            const rowData = data.result[i];
            console.log(idList.includes(rowData.particularId));
            if (!idList.includes(rowData.particularId)) {

              const id = rowData.autoId;
              const row = `<tr id='row-${id}' class='newCosting' data-type='newCosting' data-style-id='${newStyleId}' data-item-id='${newItemId}' data-particular-type='${rowData.particularType}' data-particular-id='${rowData.particularId}' data-unit-id='${rowData.unitId}' data-commission='${rowData.commission}'>
                              <td id='styleName-${id}'>${newStyleName}</td>
                              <td id='particularName-${id}'>${rowData.particularName}</td>
                              <td id='width-${id}'>${rowData.width}</td>
                              <td id='yard-${id}'>${rowData.yard}</td>
                              <td id='gsm-${id}'>${rowData.gsm}</td>
                              <td id='consumption-${id}'>${rowData.consumption}</td>
                              <td id='unitPrice-${id}'>${rowData.unitPrice}</td>
                              <td id='amount-${id}'>${rowData.amount}</td>
                              <td ><i class='fa fa-edit' onclick="costingItemSet('${id}','new')" style="cursor:pointer;"></i></td>
                              <td ><i class='fa fa-trash' onclick="deleteCostingItem('${id}','new','${rowData.styleId}','${rowData.itemId}')" style="cursor:pointer;"></i></td>
                            </tr>`;
              $("#dataList").append(row);

              itemList.push({
                "autoId": rowData.autoId,
                "styleId": newStyleId,
                "styleName": newStyleName,
                "itemId": newItemId,
                "itemName": newItemName,
                "particularType": rowData.particularType,
                "particularId": rowData.particularId,
                "particularName": rowData.particularName,
                "unitId": rowData.unitId,
                "commission": rowData.commission,
                "width": rowData.width,
                "yard": rowData.yard,
                "gsm": rowData.gsm,
                "consumption": rowData.consumption,
                "unitPrice": rowData.unitPrice,
                "amount": rowData.amount
              });
            }
          }
          sessionObject = {
            "styleId": newStyleId,
            "itemId": newItemId,
            "itemList": itemList
          }
          sessionStorage.setItem("pendingCosting", JSON.stringify(sessionObject));
        }
        $("#cloneModal").modal('hide');
      }
      $("#loader").hide();
    }
  });
}

function addAction() {

  const rowList = $("#dataList tr");
  const length = rowList.length;
  let isExist = false;
  let costingNo = $("#costingNo").val();


  let styleName = $("#styleName option:selected").text();
  let styleId = $("#styleName").val();
  let itemName = $("#itemName option:selected").text();
  let itemId = $("#itemName").val();
  let particularType = $("#particularType").val();
  let particularName = $("#particularName option:selected").text();
  let particularId = $("#particularName").val();
  let unitId = $("#unit").val();
  let commission = $("#commission").val() == "" ? 0 : $("#commission").val();
  let width = $("#width").val() == "" ? 0 : $("#width").val();
  let yard = $("#yard").val() == "" ? "0" : $("#yard").val();
  let gsm = $("#gsm").val() == "" ? "0" : $("#gsm").val();

  let consumption = $("#consumption").val() == "" ? "0" : $("#consumption").val();
  let unitPrice = $("#unitPrice").val() == "" ? "0" : $("#unitPrice").val();
  let amount = (Number(consumption) * Number(unitPrice)).toFixed(2);


  for (let i = 0; i < length; i++) {
    if (particularId == rowList[i].getAttribute("data-particular-id") && particularType == rowList[i].getAttribute("data-particular-type")) {
      isExist = true;
      break;
    }
  }

  if (!isExist) {
    //console.log(sessionStorage.getItem("pendingCosting").itemList);
    if (styleId != 0) {
      if (itemId != 0) {
        if (particularId != 0) {
          if (unitId != 0) {
            //if (commission != 0) {
            if (consumption != 0) {


              const id = length;
              
              const row = `<tr id='row-${id}' class='newCosting' data-type='newCosting' data-style-id='${styleId}' data-item-id='${itemId}' data-particular-type='${particularType}' data-particular-id='${particularId}' data-unit-id='${unitId}' data-commission='${commission}'>
                  <td id='styleNo-${id}'>${styleName}</td>
                  <td id='particularName-${id}'>${particularName}</td>
                  <td id='width-${id}'>${width}</td>
                  <td id='yard-${id}'>${yard}</td>
                  <td id='gsm-${id}'>${gsm}</td>
                  <td id='consumption-${id}'>${consumption}</td>
                  <td id='unitPrice-${id}'>${unitPrice}</td>
                  <td id='amount-${id}'>${amount}</td>
                  <td ><i class='fa fa-edit' onclick="costingItemSet('${id}','new')" style="cursor:pointer;"></i></td>
                  <td ><i class='fa fa-trash' onclick="deleteCostingItem('${id}','new','${styleId}','${itemId}')" style="cursor:pointer;"></i></td>
                </tr>`;
              $("#dataList").append(row);
              console.log("Costing No=",costingNo);
              if(costingNo == "new"){
                let sessionObject = JSON.parse(sessionStorage.getItem("pendingCosting") ? sessionStorage.getItem("pendingCosting") : "{}");
                let itemList = sessionObject.itemList ? sessionObject.itemList : [];

                itemList.push({
                  "autoId": id,
                  "styleId": styleId,
                  "styleName": styleName,
                  "itemId": itemId,
                  "itemName": itemName,
                  "particularType": particularType,
                  "particularId": particularId,
                  "particularName": particularName,
                  "unitId": unitId,
                  "commission": commission,
                  "width": width,
                  "yard": yard,
                  "gsm": gsm,
                  "consumption": consumption,
                  "unitPrice": unitPrice,
                  "amount": amount
                });

                sessionObject = {
                  "styleId": styleId,
                  "itemId": itemId,
                  "itemList": itemList
                }
                sessionStorage.setItem("pendingCosting", JSON.stringify(sessionObject));
              }
              

            } else {
              warningAlert("Consumption is empty ... Please Enter Consumption");
              $("#consumption").focus();
            }
            // } else {
            //   warningAlert("Commission is empty ... Please Enter Commission");
            //   $("#commission").focus();
            // }
          } else {
            warningAlert("Unit Not Selected... Please Select Unit");
            $("#unit").focus();
          }
        } else {
          warningAlert("Particular not selected... Please Select Particular Name");
          $("#particularName").focus();
        }
      } else {
        warningAlert("Item Type not selected... Please Select Item Type");
        $("#itemName").focus();
      }
    } else {
      warningAlert("Style No not selected... Please Select Style No");
      $("#styleName").focus();
    }
  } else {
    warningAlert("This Particular Item Already Exist , Please Change Particular Name or Click edit icon to change value....");
  }
}

$("#btnNewCosting").click(() => {
  let rowList = $("#dataList tr");
  let length = rowList.length;

  if (length > 0) {
    // rowList = $("tr.newCosting");
    // length = rowList.length;
   
      let costingList = '';

      for (let i = 0; i < length; i++) {
        const newRow = rowList[i];
        const id = newRow.id.slice(4);

        let styleNo = $("#styleName option:selected").text();
        let styleId = $("#styleName").val();
        let itemName = $("#itemName option:selected").text();
        let itemId = $("#itemName").val();
        let particularType = newRow.getAttribute("data-particular-type");
        let particularName = $("#particularName -" + id).text();
        let particularId = newRow.getAttribute("data-particular-id");
        let unitId = newRow.getAttribute("data-unit-id");
        let commission = newRow.getAttribute("data-commission");
        let width = $("#width-" + id).text();
        let yard = $("#yard-" + id).text();
        let gsm = $("#gsm-" + id).text();
        let consumption = $("#consumption-" + id).text();
        let unitPrice = $("#unitPrice-" + id).text();
        let amount = $("#amount-" + id).text();
        let submissionDate = $("#submissionDate").val();
        let userId = $("#userId").val();

        costingList += `autoId : ${id},styleId : ${styleId},styleNo : ${styleNo},itemId : ${itemId},itemName : ${itemName},particularType : ${particularType},particularId : ${particularId},particularName : ${particularName}
                      ,unitId : ${unitId},commission : ${commission},width : ${width},yard : ${yard},gsm : ${gsm},consumption : ${consumption},unitPrice : ${unitPrice},amount : ${amount},submissionDate : ${submissionDate},userId : ${userId} #`;
      }
      costingList = costingList.slice(0, -1);
      if (confirm("Are you sure to confirm..")) {
        $("#loader").show();
        $.ajax({
          type: 'POST',
          dataType: 'json',
          url: './confirmCosting',
          data: {
            costingList: costingList
          },
          success: function (data) {
            if (data.result == "Something Wrong") {
              dangerAlert("Something went wrong");
            } else if (data.result == "duplicate") {
              dangerAlert("Duplicate Item Name..This Item Name Already Exist")
            } else {
              // $("#dataList").empty();
              // $("#dataList").append(drawDataTable(data.result));
              successAlert("Costing Item Save Successfully");
              $("#costingNo").val(data.result);
              $("#badgeCostingNo").text(data.result);
              $("#btnEditCosting").show();
              $("#dataList").empty();
              $("#dataList").append(drawDataTable(data.particularList));
                     
              sessionStorage.setItem("pendingCosting",false);
            }
            $("#loader").hide();
          }
        });
      }

  } else {
    warningAlert("Please Enter Any Particular Name...");
  }
});


$("#btnEditCosting").click(() => {
  let rowList = $("#dataList tr");
  let length = rowList.length;

  if (length > 0) {
    rowList = $("tr.newCosting");
    length = rowList.length;
    if (length > 0) {
      let costingList = '';
      let costingNo = $("#costingNo").val();
      for (let i = 0; i < length; i++) {
        const newRow = rowList[i];
        const id = newRow.id.slice(4);

        let styleNo = $("#styleName option:selected").text();
        let styleId = $("#styleName").val();
        let itemName = $("#itemName option:selected").text();
        let itemId = $("#itemName").val();
        let particularType = newRow.getAttribute("data-particular-type");
        let particularName = $("#particularName -" + id).text();
        let particularId = newRow.getAttribute("data-particular-id");
        let unitId = newRow.getAttribute("data-unit-id");
        let commission = newRow.getAttribute("data-commission");
        let width = $("#width-" + id).text();
        let yard = $("#yard-" + id).text();
        let gsm = $("#gsm-" + id).text();
        let consumption = $("#consumption-" + id).text();
        let unitPrice = $("#unitPrice-" + id).text();
        let amount = $("#amount-" + id).text();
        let submissionDate = $("#submissionDate").val();
        let userId = $("#userId").val();

        costingList += `autoId : ${id},styleId : ${styleId},styleNo : ${styleNo},itemId : ${itemId},itemName : ${itemName},particularType : ${particularType},particularId : ${particularId},particularName : ${particularName}
                      ,unitId : ${unitId},commission : ${commission},width : ${width},yard : ${yard},gsm : ${gsm},consumption : ${consumption},unitPrice : ${unitPrice},amount : ${amount},submissionDate : ${submissionDate},userId : ${userId},costingNo: ${costingNo} #`;
      }
      costingList = costingList.slice(0, -1);
      if (confirm("Are you sure to confirm..")) {
        $("#loader").show();
        $.ajax({
          type: 'POST',
          dataType: 'json',
          url: './editCostingNo',
          data: {
            costingList: costingList
          },
          success: function (data) {
            if (data.result == "Something Wrong") {
              dangerAlert("Something went wrong");
            } else if (data.result == "duplicate") {
              dangerAlert("Duplicate Item Name..This Item Name Already Exist")
            } else {
              // $("#dataList").empty();
              // $("#dataList").append(drawDataTable(data.result));
              successAlert("Costing Item Save Successfully");
              $("#dataList").empty();
              $("#dataList").append(drawDataTable(data.particularList));
                     
              sessionStorage.setItem("pendingCosting",false);
            }
            $("#loader").hide();
          }
        });
      }
    }

  } else {
    warningAlert("Please Enter Any Particular Name...");
  }
});

function editAction() {

  let autoId = $("#itemAutoId").val();
  let itemType = $("#itemType").val();
  let styleId = $("#styleName").val();
  let styleName = $("#styleName option:selected").text();
  let itemId = $("#itemName").val();
  let itemName = $("#itemName option:selected").text();
  let particularType = $("#particularType").val();
  let particularId = $("#particularName").val();
  let particularName = $("#particularName option:selected").text();
  let unitId = $("#unit").val();
  let commission = $("#commission").val() == "" ? 0 : $("#commission").val();
  let submissionDate = $("#submissionDate").val();
  let width = $("#width").val() == "" ? 0 : $("#width").val();
  let yard = $("#yard").val() == "" ? "0" : $("#yard").val();
  let gsm = $("#gsm").val() == "" ? "0" : $("#gsm").val();
  let consumption = $("#consumption").val() == "" ? "0" : $("#consumption").val();
  let unitPrice = $("#unitPrice").val() == "" ? "0" : $("#unitPrice").val();
  let amount = Number(consumption) * Number(unitPrice);
  let userId = $("#userId").val();




  if (styleId != 0) {
    if (itemId != 0) {
      if (particularId != 0) {
        if (unitId != 0) {
          //if (commission != 0) {
          if (consumption != 0) {
            if (unitPrice != 0) {

              if (itemType == 'new') {
                console.log("unit price=", unitPrice);
                console.log("auto id=", autoId);
                console.log($("#unitPrice-" + autoId));
                $("#row-" + autoId).attr("data-particular-type", particularType);
                $("#row-" + autoId).attr("data-particular-id", particularId);
                $("#row-" + autoId).attr("data-unit-id", unitId);
                $("#row-" + autoId).attr("data-commission", commission);
                $("#particularName-" + autoId).text(particularName);
                $("#width-" + autoId).text(width);
                $("#yard-" + autoId).text(yard);
                $("#gsm-" + autoId).text(gsm);
                $("#consumption-" + autoId).text(consumption);
                $("#unitPrice-" + autoId).text(unitPrice);
                $("#amount-" + autoId).text(amount);
                $("#btnAdd").show();
                $("#btnEdit").hide();

                let sessionObject = JSON.parse(sessionStorage.getItem("pendingCosting") ? sessionStorage.getItem("pendingCosting") : "{}");
                let itemList = sessionObject.itemList ? sessionObject.itemList : [];

                for (let i = 0; i < itemList.length; i++) {
                  if (itemList[i].autoId == autoId) {
                    itemList[i] = {
                      "autoId": autoId,
                      "styleId": styleId,
                      "styleName": styleName,
                      "itemId": itemId,
                      "itemName": itemName,
                      "particularType": particularType,
                      "particularId": particularId,
                      "particularName": particularName,
                      "unitId": unitId,
                      "commission": commission,
                      "width": width,
                      "yard": yard,
                      "gsm": gsm,
                      "consumption": consumption,
                      "unitPrice": unitPrice,
                      "amount": amount
                    }
                    sessionObject = {
                      "styleId": styleId,
                      "itemId": itemId,
                      "itemList": itemList
                    }

                    sessionStorage.setItem("pendingCosting", JSON.stringify(sessionObject));
                    break;
                  }
                }

              } else {
                let costingNo = $("#costingNo").val();
                $("#loader").show();
                $.ajax({
                  type: 'POST',
                  dataType: 'json',
                  url: './editCosting',
                  data: {
                    autoId: autoId,
                    costingNo: costingNo,
                    styleId: styleId,
                    itemId: itemId,
                    particularType: particularType,
                    particularId: particularId,
                    size: "",
                    unitId: unitId,
                    width: width,
                    yard: yard,
                    gsm: gsm,
                    consumption: consumption,
                    unitPrice: unitPrice,
                    amount: amount,
                    commission: commission,
                    date: submissionDate,
                    userId: userId
                  },
                  success: function (data) {
                    if (data.result == "Something Wrong") {
                      dangerAlert("Something went wrong");
                    } else if (data.result == "duplicate") {
                      dangerAlert("Duplicate Item Name..This Item Name Already Exist")
                    } else {
                      $("#btnAdd").show();
                      $("#btnEdit").hide();
                      $("#dataList").empty();
                      $("#dataList").append(drawDataTable(data.result));
                      successAlert("Costing Item Edit Successfully");
                      if (sessionStorage.getItem("pendingCosting")) {
                        const pendingCosting = JSON.parse(sessionStorage.getItem("pendingCosting"));
                        if (styleId == pendingCosting.styleId && itemId == pendingCosting.itemId) {
                          $("#dataList").append(drawSessionDataTable(pendingCosting.itemList));
                        }
                      }
                      $("#loader").hide();
                    }
                  }
                });
              }


            } else {
              warningAlert("Unit Price is empty ... Please Enter Unit Price");
              $("#unitPrice").focus();
            }
          } else {
            warningAlert("Consumption is empty ... Please Enter Consumption");
            $("#consumption").focus();
          }
          // } else {
          //   warningAlert("Commission is empty ... Please Enter Commission");
          //   $("#commission").focus();
          // }
        } else {
          warningAlert("Unit Not Selected... Please Select Unit");
          $("#unit").focus();
        }
      } else {
        warningAlert("Particular not selected... Please Select Particular Name");
        $("#particularName").focus();
      }
    } else {
      warningAlert("Item Type not selected... Please Select Item Type");
      $("#itemName").focus();
    }
  } else {
    warningAlert("Style No not selected... Please Select Style No");
    $("#styleName").focus();
  }

}

function refreshAction() {
  sessionStorage.setItem("pendingCosting",false);
  location.reload();
}


function inputSetByUnit() {
  let unit = $("#unit option:selected").text();

  if (unit.toLowerCase() == "kg") {
    $("#gsm").val("0");
    $("#yard").val("0");
    $("#yardDiv").show();
    $("#gsmDiv").show();
  } else {
    $("#yardDiv").hide();
    $("#gsmDiv").hide();
    $("#gsm").val("1");
    $("#yard").val("1");
  }
}

function searchCosting(styleId, itemId,costingNo) {

  $("#costingNo").val(costingNo);
  $("#badgeCostingNo").text(costingNo);
  $("#dataList").empty();
  itemIdForSet = itemId;
  $("#styleName").val(styleId).change();
  $("#btnEditCosting").show();
  // $.ajax({
  //   type: 'GET',
  //   dataType: 'json',
  //   url: './searchCosting',
  //   data: {
  //     styleId: styleId,
  //     itemId: itemId
  //   },
  //   success: function (data) {
  //     if (data.result == "Something Wrong") {
  //       dangerAlert("Something went wrong");
  //     } else if (data.result == "duplicate") {
  //       dangerAlert("Duplicate Item Name..This Item Name Already Exist")
  //     } else {
  //       $('#searchModal').modal('hide');
  //       let costingList = data.result;
  //       itemIdForSet = costingList[0].itemId;
  //       $("#styleName").val(costingList[0].styleId).change();
  //       $("#dataList").empty();
  //       $("#dataList").append(drawDataTable(costingList));
  //     }
  //   }
  // });
}

function costingItemSet(autoId, itemType) {
  if (itemType == 'new') {
    $("#itemAutoId").val(autoId);
    $("#itemType").val("new");
    const row = $("#row-" + autoId);
    console.log(row);
    particularItemIdForSet = row.attr('data-particular-id');
    $("#particularType").val(row.attr('data-particular-type')).change();
    $("#particularName").val(row.attr('data-particular-id')).change();
    $("#unit").val(row.attr('data-unit-id')).change();
    $("#commission").val(row.attr('data-commission'));
    $("#width").val($("#width-" + autoId).text());
    $("#yard").val($("#yard-" + autoId).text());
    $("#gsm").val($("#gsm-" + autoId).text());
    $("#consumption").val($("#consumption-" + autoId).text());
    $("#unitPrice").val($("#unitPrice-" + autoId).text());
  } else {
    $("#itemType").val("old");
    $("#loader").show();
    $.ajax({
      type: 'GET',
      dataType: 'json',
      url: './searchCostingItem',
      data: {
        autoId: autoId,
      },
      success: function (data) {
        if (data.result == "Something Wrong") {
          dangerAlert("Something went wrong");
        } else if (data.result == "duplicate") {
          dangerAlert("Duplicate Item Name..This Item Name Already Exist")
        } else {

          let costing = data.result;
          $("#itemAutoId").val(costing.autoId);
          $("#itemType").val("old");
          itemIdForSet = costing.itemId;
          //$("#styleName").val(costing.styleId).change();
          particularItemIdForSet = costing.particularId;
          $("#particularType").val(costing.particularType).change();
          $("#particularName").val(costing.particularId).change();
          $("#unit").val(costing.unitId).change();
          $("#commission").val(costing.commission);
          let date = costing.date.split("/");
          $("#submissionDate").val(date[2] + "-" + date[1] + "-" + date[0]).change();
          $("#width").val(costing.width);
          $("#yard").val(costing.yard);
          $("#gsm").val(costing.gsm);
          $("#consumption").val(costing.consumption);
          $("#unitPrice").val(costing.unitPrice);

        }
        $("#loader").hide();
      }
    });
  }
  $("#btnAdd").hide();
  $("#btnEdit").show();
}

function deleteCostingItem(autoId, rowType, styleId, itemId) {
  if (confirm("Are you sure to Delete this item?")) {
    if (rowType == 'new') {
      const particularId = $("#row-" + autoId).attr("data-particular-id");
      console.log(particularId);
      const pendingCosting = JSON.parse(sessionStorage.getItem("pendingCosting"));
      const newItemList = pendingCosting.itemList.filter(item => item.particularId != particularId);
      pendingCosting.itemList = newItemList;
      sessionStorage.setItem("pendingCosting", JSON.stringify(pendingCosting));

      $("#row-" + autoId).remove();
    } else {
      let costingNo = $("#costingNo").val();
      $("#loader").show();
      $.ajax({
        type: 'GET',
        dataType: 'json',
        url: './deleteCosting',
        data: {
          autoId: autoId,
          styleId: styleId,
          itemId: itemId,
          costingNo : costingNo
        },
        success: function (data) {
          if (data.result == "Something Wrong") {
            dangerAlert("Something went wrong");
          } else if (data.result == "duplicate") {
            dangerAlert("Duplicate Item Name..This Item Name Already Exist")
          } else {

            let costingList = data.result;
            // if (costingList.size > 0) {
            //   itemIdForSet = costingList[0].itemId;
            //   $("#styleName").val(costingList[0].styleId).change();
            // }
            $("#dataList").empty();
            $("#dataList").append(drawDataTable(costingList));
            if (sessionStorage.getItem("pendingCosting")) {
              const pendingCosting = JSON.parse(sessionStorage.getItem("pendingCosting"));
              if (styleId == pendingCosting.styleId && itemId == pendingCosting.itemId) {
                $("#dataList").append(drawSessionDataTable(pendingCosting.itemList));
              }
            }
          }
          $("#loader").hide();
        }
      });
    }

  }

}

function drawDataTable(data) {
  let rows = "";
  const length = data.length;

  for (let i = 0; i < length; i++) {
    const rowData = data[i];
    const id = rowData.autoId;
    rows += `<tr id='row-${id}' class='previousCosting' data-type='previousCosting' data-style-id='${rowData.styleId}' data-item-id='${rowData.itemId}' data-particular-type='${rowData.particularType}' data-particular-id='${rowData.particularId}' data-unit-id='${rowData.unitId}' data-commission='${rowData.commission}'>
            <td id='styleName-${id}'>${rowData.styleName}</td>
            <td id='particularName-${id}'>${rowData.particularName}</td>
            <td id='width-${id}'>${rowData.width}</td>
            <td id='yard-${id}'>${rowData.yard}</td>
            <td id='gsm-${id}'>${rowData.gsm}</td>
            <td id='consumption-${id}'>${rowData.consumption}</td>
            <td id='unitPrice-${id}'>${rowData.unitPrice}</td>
            <td id='amount-${id}'>${rowData.amount}</td>
            <td ><i class='fa fa-edit' onclick="costingItemSet('${id}','old')" style="cursor:pointer;"></i></td>
            <td ><i class='fa fa-trash' onclick="deleteCostingItem('${id}','old','${rowData.styleId}','${rowData.itemId}')" style="cursor:pointer;"></i></td>
    </tr>`;
    //rows.push(drawRowDataTable(data[i], i));
  }

  return rows;
}


function drawSessionDataTable(data) {
  let rows = "";
  const length = data.length;

  for (let i = 0; i < length; i++) {
    const rowData = data[i];
    const id = rowData.autoId;
    rows += `<tr id='row-${id}' class='newCosting' data-type='newCosting' data-style-id='${rowData.styleId}' data-item-id='${rowData.itemId}' data-particular-type='${rowData.particularType}' data-particular-id='${rowData.particularId}' data-unit-id='${rowData.unitId}' data-commission='${rowData.commission}'>
            <td id='styleName-${id}'>${rowData.styleName}</td>
            <td id='particularName-${id}'>${rowData.particularName}</td>
            <td id='width-${id}'>${rowData.width}</td>
            <td id='yard-${id}'>${rowData.yard}</td>
            <td id='gsm-${id}'>${rowData.gsm}</td>
            <td id='consumption-${id}'>${rowData.consumption}</td>
            <td id='unitPrice-${id}'>${rowData.unitPrice}</td>
            <td id='amount-${id}'>${rowData.amount}</td>
            <td ><i class='fa fa-edit' onclick="costingItemSet('${id}','new')" style="cursor:pointer;"></i></td>
            <td ><i class='fa fa-trash' onclick="deleteCostingItem('${id}','new','${rowData.styleId}','${rowData.itemId}')" style="cursor:pointer;"></i></td>
    </tr>`;
    //rows.push(drawRowDataTable(data[i], i));
  }

  return rows;
}


function drawGroupCostingList(data) {
  let rows = "";
  const length = data.length;

  for (let i = 0; i < length; i++) {
    const rowData = data[i];
    const id = rowData.autoId;
    rows += `<tr id='groupRow-${rowData.costingNo }' data-id='${rowData.costingNo}'>
    <td>${rowData.costingNo}</td>
    <td>${rowData.styleName}</td>
    <td>${rowData.itemName}</td>
    <td><input id='groupCheck-${rowData.costingNo}' type="checkbox" /></td>
  </tr>`;
    //rows.push(drawRowDataTable(data[i], i));
  }

  return rows;
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
  $("#searchCosting").on("keyup", function () {
    let value = $(this).val().toLowerCase();
    $("#costingListTable tr").filter(function () {
      $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
    });
  });
});

$(document).ready(function () {
  $("#cloneCostingSearch").on("keyup", function () {
    let value = $(this).val().toLowerCase();
    $("#cloneCostingTable tr").filter(function () {
      $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
    });
  });
});

let today = new Date();
$("#submissionDate").val(today.getFullYear() + '-' + ('0' + (today.getMonth() + 1)).slice(-2) + '-' + ('0' + today.getDate()).slice(-2)).change();


let idListMicro = ["styleName","itemName","copyCosting","particularType","particularName","unit","commission","submissionDate","width","consumption","unitPrice","btnAdd"];
idListMicro.forEach((id,index)=>{
  $('#'+id).keyup(function(event){
    if(event.keyCode ===13){
      event.preventDefault();
      $("#"+idListMicro[index+1]).focus();
    }
  });
})

