package pg.orderModel;

import java.util.ArrayList;
import java.util.List;

public class PurchaseOrder {
	
	String poNo;
	String orderDate;
	String deliveryDate;
	String deliveryTo;
	String orderBy;
	String billTo;
	String manualPO;
	String paymentType;
	String currency;
	String caNo;
	String rnNo;
	String fabricsContent;
	String note;
	String subject;
	String body;
	String terms;
	String itemListString;
	List<PurchaseOrderItem> itemList;
	String userId;
	
	String purchaseOrder;
	String styleId;
	String styleNo;
	String supplierId;
	String supplierName;
	String type;
	int mdApproval = 0;
	String mdApprovalStatus = "";
	String buyerId="";

	
	String combinePOList="";
	String combineitemTypeList="";
	
	public PurchaseOrder() {}
	
	
	
	public PurchaseOrder( String purchaseOrder, String styleId, String styleNo, String supplierId,
			String supplierName, String poNo,String type,String orderDate,int mdApproval) {
		super();
		this.purchaseOrder = purchaseOrder;
		this.styleId = styleId;
		this.styleNo = styleNo;
		this.supplierId = supplierId;
		this.supplierName = supplierName;
		this.poNo = poNo;
		this.type = type;
		this.orderDate = orderDate;
		this.mdApproval = mdApproval;
	}



	public int getMdApproval() {
		return mdApproval;
	}



	public void setMdApproval(int mdApproval) {
		this.mdApproval = mdApproval;
	}



	public PurchaseOrder(String poNo,String orderDate) {
		this.poNo = poNo;
		this.orderDate = orderDate;
	}
	
	public PurchaseOrder(String poNo,String orderDate, String deliveryDate,String BuyerId,String supplierId, String deliveryTo, String orderBy, String billTo,
			String manualPO, String paymentType, String currency, String note, String subject, List<PurchaseOrderItem>  itemList,
			String poType,String userId) {
		super();
		
		this.poNo = poNo;
		this.orderDate = orderDate;
		this.deliveryDate = deliveryDate;
		
		this.buyerId=BuyerId;
		System.out.println("buyerId "+buyerId);
		this.supplierId = supplierId;
		this.deliveryTo = deliveryTo;
		this.orderBy = orderBy;
		this.billTo = billTo;
		this.manualPO = manualPO;
		this.paymentType = paymentType;
		this.currency = currency;
		this.note = note;
		this.subject = subject;
		this.itemList = itemList;
		this.type = poType;
		this.userId = userId;
	}
	
	
	public String getPoNo() {
		return poNo;
	}

	public void setPoNo(String poNo) {
		this.poNo = poNo;
	}

	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public String getDeliveryTo() {
		return deliveryTo;
	}
	public void setDeliveryTo(String deliveryTo) {
		this.deliveryTo = deliveryTo;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getBillTo() {
		return billTo;
	}
	public void setBillTo(String billTo) {
		this.billTo = billTo;
	}
	public String getManualPO() {
		return manualPO;
	}
	public void setManualPO(String manulPO) {
		this.manualPO = manulPO;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getItemListString() {
		return itemListString;
	}
	public void setItemListString(String itemListString) {
		try {
		String[] itemList = itemListString.split("#");
		List<PurchaseOrderItem> list = new ArrayList<PurchaseOrderItem>();
		String autoId,type,supplierId,dollar,currency;
		double rate,amount,sampleQty;
		boolean isCheck;
		for (String item : itemList) {
			String[] itemProperty = item.split(",");
			autoId = itemProperty[0].substring(itemProperty[0].indexOf(":")+1).trim();
			type = itemProperty[1].substring(itemProperty[1].indexOf(":")+1).trim();
			supplierId = itemProperty[2].substring(itemProperty[2].indexOf(":")+1).trim();
			rate = Double.valueOf(itemProperty[3].substring(itemProperty[3].indexOf(":")+1).trim());
			dollar = itemProperty[4].substring(itemProperty[4].indexOf(":")+1).trim();
			currency = itemProperty[5].substring(itemProperty[5].indexOf(":")+1).trim();
			amount = Double.valueOf(itemProperty[6].substring(itemProperty[6].indexOf(":")+1).trim());
			sampleQty = Double.valueOf(itemProperty[7].substring(itemProperty[7].indexOf(":")+1).trim());
			isCheck = Boolean.valueOf(itemProperty[8].substring(itemProperty[8].indexOf(":")+1).trim());
			
			list.add(new PurchaseOrderItem(autoId, type, supplierId, rate, dollar, amount,sampleQty, currency, supplierId, isCheck));
		}
		
		this.itemList = list;
		this.itemListString = itemListString;
		}catch(Exception e) {
			e.printStackTrace();
		}
		//itemListGenerate(this.itemListString);
	}
	private void itemListGenerate(String itemListString) {
		// TODO Auto-generated method stub
		
	}

	public List<PurchaseOrderItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<PurchaseOrderItem> itemList) {
		this.itemList = itemList;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getPurchaseOrder() {
		return purchaseOrder;
	}
	public void setPurchaseOrder(String purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}
	public String getStyleId() {
		return styleId;
	}
	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}
	public String getStyleNo() {
		return styleNo;
	}
	public void setStyleNo(String styleNo) {
		this.styleNo = styleNo;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getCaNo() {
		return caNo;
	}
	public void setCaNo(String caNo) {
		this.caNo = caNo;
	}
	public String getRnNo() {
		return rnNo;
	}
	public void setRnNo(String rnNo) {
		this.rnNo = rnNo;
	}
	public String getTerms() {
		return terms;
	}
	public void setTerms(String terms) {
		this.terms = terms;
	}
	public String getFabricsContent() {
		return fabricsContent;
	}
	public void setFabricsContent(String fabricsContent) {
		this.fabricsContent = fabricsContent;
	}



	public String getBuyerId() {
		return buyerId;
	}



	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}



	public String getCombinePOList() {
		return combinePOList;
	}



	public void setCombinePOList(String combinePOList) {
		this.combinePOList = combinePOList;
	}



	public String getCombineitemTypeList() {
		return combineitemTypeList;
	}



	public void setCombineitemTypeList(String combineitemTypeList) {
		this.combineitemTypeList = combineitemTypeList;
	}



	public String getMdApprovalStatus() {
		return mdApprovalStatus;
	}



	public void setMdApprovalStatus(String mdApprovalStatus) {
		this.mdApprovalStatus = mdApprovalStatus;
	}

	
}
