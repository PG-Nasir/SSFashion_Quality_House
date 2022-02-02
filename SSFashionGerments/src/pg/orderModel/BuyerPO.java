package pg.orderModel;

import java.util.List;

public class BuyerPO {
	
	String buyerPoId;
	String buyerId;
	String buyerName;
	String paymentTerm;
	String currency;
	double totalUnit;
	double unitCmt;
	double totalPrice;
	double unitFob;
	double totalAmount;
	String shipmentDate;
	String inspectionDate;
	String note;
	String remarks;
	String date;
	String userId;
	List<BuyerPoItem> itemList;
	String changedItemsList;
	String purchaseOrder;
	String styleNo;
	String fabricPo;
	String triumPo;

	public BuyerPO() {}
	
	public BuyerPO(String buyerPoId, String buyerId, String paymentTerm, String currency, String FabricPo,String TriumPo,double totalUnit,
			double unitCmt, double totalPrice, double unitFob, double totalAmount, String note, String remarks,
			String userId) {
		super();
		this.buyerPoId = buyerPoId;
		this.buyerId = buyerId;
		this.paymentTerm = paymentTerm;
		this.fabricPo=FabricPo;
		this.triumPo=TriumPo;
		this.currency = currency;
		this.totalUnit = totalUnit;
		this.unitCmt = unitCmt;
		this.totalPrice = totalPrice;
		this.unitFob = unitFob;
		this.totalAmount = totalAmount;
		this.note = note;
		this.remarks = remarks;
		this.userId = userId;
	}
	
	public BuyerPO(String buyerPoId,String buyerId,String buyerName,String date) {
		this.buyerPoId = buyerPoId;
		this.buyerId = buyerId;
		this.buyerName = buyerName;
		this.date = date;
	}
	
	public BuyerPO(String buyerPoId,String buyerId,String buyerName,String purchaseOrder,String styleNo,String date) {
		this.buyerPoId = buyerPoId;
		this.buyerId = buyerId;
		this.buyerName = buyerName;
		this.purchaseOrder = purchaseOrder;
		this.styleNo = styleNo;
		this.date = date;
	}
	
	
	
	public String getFabricPo() {
		return fabricPo;
	}

	public void setFabricPo(String fabricPo) {
		this.fabricPo = fabricPo;
	}

	public String getTriumPo() {
		return triumPo;
	}

	public void setTriumPo(String triumPo) {
		this.triumPo = triumPo;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getBuyerPoId() {
		return buyerPoId;
	}
	public void setBuyerPoId(String buyerPoId) {
		this.buyerPoId = buyerPoId;
	}
	public String getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}
	public String getPaymentTerm() {
		return paymentTerm;
	}
	public void setPaymentTerm(String paymentTerm) {
		this.paymentTerm = paymentTerm;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public double getTotalUnit() {
		return totalUnit;
	}
	public void setTotalUnit(double totalUnit) {
		this.totalUnit = totalUnit;
	}
	public double getUnitCmt() {
		return unitCmt;
	}
	public void setUnitCmt(double unitCmt) {
		this.unitCmt = unitCmt;
	}
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public double getUnitFob() {
		return unitFob;
	}
	public void setUnitFob(double unitFob) {
		this.unitFob = unitFob;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<BuyerPoItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<BuyerPoItem> itemList) {
		this.itemList = itemList;
	}

	public String getChangedItemsList() {
		return changedItemsList;
	}

	public void setChangedItemsList(String changedItemsList) {
		this.changedItemsList = changedItemsList;
	}

	public String getShipmentDate() {
		return shipmentDate;
	}

	public void setShipmentDate(String shipmentDate) {
		this.shipmentDate = shipmentDate;
	}

	public String getInspectionDate() {
		return inspectionDate;
	}

	public void setInspectionDate(String inspectionDate) {
		this.inspectionDate = inspectionDate;
	}

	public String getPurchaseOrder() {
		return purchaseOrder;
	}

	public void setPurchaseOrder(String purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}

	public String getStyleNo() {
		return styleNo;
	}

	public void setStyleNo(String styleNo) {
		this.styleNo = styleNo;
	}
	
}
