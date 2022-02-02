package pg.storeModel;

public class AccessoriesStock {
	String purchaseOrder;
	String styleId;
	String styleNo;
	String itemId;
	String itemName;
	String colorId;
	String colorName;
	String sizeId;
	String accessoriesName;
	String sizeName;
	String receivedQy;
	String storeReturnQty;
	String storeIssueQty;
	String storeIssueReturnQty;
	String storeTransferInQty;
	String storeTransferOutQty;
	String openingBalance;
	String closingQty;
	String rollId;
	public AccessoriesStock() {
		
	}

	
	
	public AccessoriesStock(String purchaseOrder, String styleId, String styleNo, String itemId, String itemName,
			String colorId, String colorName, String sizeId, String accessoriesName, String sizeName, String openingBalance,String receivedQy,
			String storeReturnQty, String storeIssueQty, String storeIssueReturnQty, String storeTransferInQty,
			String storeTransferOutQty) {
		super();
		this.purchaseOrder = purchaseOrder;
		this.styleId = styleId;
		this.styleNo = styleNo;
		this.itemId = itemId;
		this.itemName = itemName;
		this.colorId = colorId;
		this.colorName = colorName;
		this.sizeId = sizeId;
		this.accessoriesName = accessoriesName;
		this.sizeName = sizeName;
		this.openingBalance=openingBalance;
		this.receivedQy = receivedQy;
		this.storeReturnQty = storeReturnQty;
		this.storeIssueQty = storeIssueQty;
		this.storeIssueReturnQty = storeIssueReturnQty;
		this.storeTransferInQty = storeTransferInQty;
		this.storeTransferOutQty = storeTransferOutQty;
		
		double ClosingQty=(Double.parseDouble(openingBalance)+Double.parseDouble(receivedQy)+Double.parseDouble(storeIssueReturnQty)+Double.parseDouble(storeTransferInQty))-(Double.parseDouble(storeIssueQty)+Double.parseDouble(storeTransferOutQty));
		
		closingQty=Double.toString(ClosingQty);
	}

	
	public AccessoriesStock(String purchaseOrder, String styleId, String styleNo, String itemId, String itemName,
			String colorId, String colorName, String sizeId, String accessoriesName,String openingBalance,String receivedQy,
			String storeReturnQty, String storeIssueQty, String storeIssueReturnQty, String storeTransferInQty,
			String storeTransferOutQty) {
		super();
		this.purchaseOrder = purchaseOrder;
		this.styleId = styleId;
		this.styleNo = styleNo;
		this.itemId = itemId;
		this.itemName = itemName;
		this.colorId = colorId;
		this.colorName = colorName;
		this.rollId = sizeId;
		this.accessoriesName = accessoriesName;
		this.openingBalance=openingBalance;
		this.receivedQy = receivedQy;
		this.storeReturnQty = storeReturnQty;
		this.storeIssueQty = storeIssueQty;
		this.storeIssueReturnQty = storeIssueReturnQty;
		this.storeTransferInQty = storeTransferInQty;
		this.storeTransferOutQty = storeTransferOutQty;
		
		double ClosingQty=(Double.parseDouble(openingBalance)+Double.parseDouble(receivedQy)+Double.parseDouble(storeIssueReturnQty)+Double.parseDouble(storeTransferInQty))-(Double.parseDouble(storeIssueQty)+Double.parseDouble(storeTransferOutQty));
		
		closingQty=Double.toString(ClosingQty);
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

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getColorId() {
		return colorId;
	}

	public void setColorId(String colorId) {
		this.colorId = colorId;
	}

	public String getColorName() {
		return colorName;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	public String getSizeId() {
		return sizeId;
	}

	public void setSizeId(String sizeId) {
		this.sizeId = sizeId;
	}

	public String getAccessoriesName() {
		return accessoriesName;
	}

	public void setAccessoriesName(String accessoriesName) {
		this.accessoriesName = accessoriesName;
	}

	public String getSizeName() {
		return sizeName;
	}

	public void setSizeName(String sizeName) {
		this.sizeName = sizeName;
	}

	public String getReceivedQy() {
		return receivedQy;
	}

	public void setReceivedQy(String receivedQy) {
		this.receivedQy = receivedQy;
	}

	public String getStoreReturnQty() {
		return storeReturnQty;
	}

	public void setStoreReturnQty(String storeReturnQty) {
		this.storeReturnQty = storeReturnQty;
	}

	public String getStoreIssueQty() {
		return storeIssueQty;
	}

	public void setStoreIssueQty(String storeIssueQty) {
		this.storeIssueQty = storeIssueQty;
	}

	public String getStoreIssueReturnQty() {
		return storeIssueReturnQty;
	}

	public void setStoreIssueReturnQty(String storeIssueReturnQty) {
		this.storeIssueReturnQty = storeIssueReturnQty;
	}

	public String getStoreTransferInQty() {
		return storeTransferInQty;
	}

	public void setStoreTransferInQty(String storeTransferInQty) {
		this.storeTransferInQty = storeTransferInQty;
	}

	public String getStoreTransferOutQty() {
		return storeTransferOutQty;
	}

	public void setStoreTransferOutQty(String storeTransferOutQty) {
		this.storeTransferOutQty = storeTransferOutQty;
	}



	public String getOpeningBalance() {
		return openingBalance;
	}



	public void setOpeningBalance(String openingBalance) {
		this.openingBalance = openingBalance;
	}



	public String getClosingQty() {
		return closingQty;
	}



	public void setClosingQty(String closingQty) {
		this.closingQty = closingQty;
	}



	public String getRollId() {
		return rollId;
	}



	public void setRollId(String rollId) {
		this.rollId = rollId;
	}
	
	
}
