
package pg.orderModel;

import java.util.ArrayList;

import pg.registerModel.Size;

public class AccessoriesIndent {
	String autoid;
	String user;
	String po;
	String poNo;
	String styleId;
	String style;
	String itemId;
	String itemname;
	String itemColorId;
	String itemColor;
	String shippingmark;
	String accessoriesId;
	String accessoriesName;
	String accessoriessize;
	String size;
	String orderqty;
	String qtyindozen;
	String reqperpcs;
	String reqperdozen;
	String perunit;
	String totalbox;
	String dividedby;
	String extrainpercent;
	String percentqty;
	String totalqty;
	String unit;
	String unitId;
	String grandqty;
	String brand;
	String accessoriesColorId;
	String accessoriesColor;
	String sizeName;
	String sqNo;
	String skuNo;
	String requiredUnitQty;
	String indentBrandId;
	String indentColorId;
	String aiNo;
	String purchaseOrder;
	String styleNo;
	String sizeGroupId;
	String indentDate;
	String indentType;
	String lengthUnitId;
	String orderQty;
	String storeReceivedBalance;
	String receivedRemain;
	ArrayList<Size> sizeList;
	public AccessoriesIndent() {
		
	}
	
	
	public AccessoriesIndent(String PoNo,String purchaseOrder,String styleId,String styleName,String itemId,String itemName,String itemColorId,String itemColorName,String accessoriesId,String accessoriesName,String accessoriesColorId,String accessoriesColorName,String OrderQty,String StoreReceivedBalance,String ReceivedRemain) {
		this.poNo = PoNo;
		this.po = purchaseOrder;
		this.styleId = styleId;
		this.style = styleName;
		//System.out.println("style "+style);
		
		this.itemId = itemId;
		this.itemname = itemName;
		//System.out.println("itemname "+itemname);
		
		this.itemColorId = itemColorId;
		this.itemColor = itemColorName;
		System.out.println("se itemColorId "+itemColorId);
		this.accessoriesId = accessoriesId;
		//System.out.println("accessoriesId "+accessoriesId);
		this.accessoriesName = accessoriesName;
		
		
		this.accessoriesColorId = accessoriesColorId;
		this.accessoriesColor = accessoriesColorName;
		System.out.println("accessoriesColor "+accessoriesColor);
		//Extra..
		this.purchaseOrder = purchaseOrder;
		this.orderQty=OrderQty;
		this.storeReceivedBalance=StoreReceivedBalance;
		this.receivedRemain=ReceivedRemain;
	}
	
	public AccessoriesIndent(String AccIndentId,String PurchaseOrder,String StyleNo,String ItemName,String ColorName,String ShippingMarks,String AccessoriesName,String SizeName,String accessoriesSize,String PerUnit,String TotalBox,String OrderQty,String QtyInDozen,String ReqPerPices,String ReqPerDoz,String DividedBy,String PercentageExtra,String PercentageExtraQty,String TotalQty,String UnitName,String RequireUnitQty,String IndentBrandId,String IndentColorId) {
		this.autoid=AccIndentId;
		this.style=StyleNo;
		this.po=PurchaseOrder;
		this.itemname=ItemName;
		this.itemColor=ColorName;
		this.shippingmark=ShippingMarks;
		this.accessoriesName=AccessoriesName;
		this.sizeName=SizeName;
		this.accessoriessize=accessoriesSize;
		this.perunit=PerUnit;
		this.totalbox=TotalBox;
		this.orderqty=OrderQty;
		this.qtyindozen=QtyInDozen;
		this.reqperpcs=ReqPerPices;
		this.reqperdozen=ReqPerDoz;
		this.dividedby=DividedBy;
		this.extrainpercent=PercentageExtra;
		this.percentqty=PercentageExtraQty;
		this.totalqty=TotalQty;
		this.unit=UnitName;
		this.requiredUnitQty=RequireUnitQty;
		this.indentBrandId=IndentBrandId;
		this.indentColorId=IndentColorId;
	}
	
	public AccessoriesIndent(String autoId,String po, String style, String itemname, String itemcolor,String shippingmark,String accessoriesName,String sizeName,String requiredUnitQty) {
		this.autoid=autoId;
		this.po=po;
		this.style=style;
		this.itemname=itemname;
		this.itemColor=itemcolor;
		this.shippingmark=shippingmark;
		this.accessoriesName=accessoriesName;
		this.sizeName=sizeName;
		this.requiredUnitQty=requiredUnitQty;
	}
	
	public AccessoriesIndent(String AINo,String PurchaseOrder,String StyleNo,String ItemName) {
		this.aiNo=AINo;
		this.purchaseOrder=PurchaseOrder;
		this.styleNo=StyleNo;
		this.itemname=ItemName;
	}
	
	public String getIndentBrandId() {
		return indentBrandId;
	}


	public void setIndentBrandId(String indentBrandId) {
		this.indentBrandId = indentBrandId;
	}


	public String getIndentColorId() {
		return indentColorId;
	}


	public void setIndentColorId(String indentColorId) {
		this.indentColorId = indentColorId;
	}


	public String getStyleId() {
		return styleId;
	}

	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemColorId() {
		return itemColorId;
	}

	public void setItemColorId(String itemColorId) {
		this.itemColorId = itemColorId;
	}

	public String getAccessoriesId() {
		return accessoriesId;
	}

	public void setAccessoriesId(String accessoriesId) {
		this.accessoriesId = accessoriesId;
	}

	public String getAccessoriesColorId() {
		return accessoriesColorId;
	}

	public void setAccessoriesColorId(String accessoriesColorId) {
		this.accessoriesColorId = accessoriesColorId;
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

	public String getRequiredUnitQty() {
		return requiredUnitQty;
	}

	public void setRequiredUnitQty(String requiredUnitQty) {
		this.requiredUnitQty = requiredUnitQty;
	}

	public String getAutoid() {
		return autoid;
	}
	public void setAutoid(String autoid) {
		this.autoid = autoid;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPo() {
		return po;
	}
	public void setPo(String po) {
		this.po = po;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getItemname() {
		return itemname;
	}
	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	
	
	public String getItemColor() {
		return itemColor;
	}


	public void setItemColor(String itemColor) {
		this.itemColor = itemColor;
	}


	public String getShippingmark() {
		return shippingmark;
	}
	public void setShippingmark(String shippingmark) {
		this.shippingmark = shippingmark;
	}
	
	public String getAccessoriessize() {
		return accessoriessize;
	}
	public void setAccessoriessize(String accessoriessize) {
		this.accessoriessize = accessoriessize;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getOrderqty() {
		return orderqty;
	}
	public void setOrderqty(String orderqty) {
		this.orderqty = orderqty;
	}
	public String getQtyindozen() {
		return qtyindozen;
	}
	public void setQtyindozen(String qtyindozen) {
		this.qtyindozen = qtyindozen;
	}
	public String getReqperpcs() {
		return reqperpcs;
	}
	public void setReqperpcs(String reqperpcs) {
		this.reqperpcs = reqperpcs;
	}
	public String getReqperdozen() {
		return reqperdozen;
	}
	public void setReqperdozen(String reqperdozen) {
		this.reqperdozen = reqperdozen;
	}
	public String getPerunit() {
		return perunit;
	}
	public void setPerunit(String perunit) {
		this.perunit = perunit;
	}
	public String getTotalbox() {
		return totalbox;
	}
	public void setTotalbox(String totalbox) {
		this.totalbox = totalbox;
	}
	public String getDividedby() {
		return dividedby;
	}
	public void setDividedby(String dividedby) {
		this.dividedby = dividedby;
	}
	public String getExtrainpercent() {
		return extrainpercent;
	}
	public void setExtrainpercent(String extrainpercent) {
		this.extrainpercent = extrainpercent;
	}
	public String getPercentqty() {
		return percentqty;
	}
	public void setPercentqty(String percentqty) {
		this.percentqty = percentqty;
	}
	public String getTotalqty() {
		return totalqty;
	}
	public void setTotalqty(String totalqty) {
		this.totalqty = totalqty;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getGrandqty() {
		return grandqty;
	}
	public void setGrandqty(String grandqty) {
		this.grandqty = grandqty;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	
	
	
	public String getAccessoriesColor() {
		return accessoriesColor;
	}


	public void setAccessoriesColor(String accessoriesColor) {
		this.accessoriesColor = accessoriesColor;
	}


	public String getAiNo() {
		return aiNo;
	}
	public void setAiNo(String aiNo) {
		this.aiNo = aiNo;
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
	public String getSizeGroupId() {
		return sizeGroupId;
	}
	public void setSizeGroupId(String sizeGroupId) {
		this.sizeGroupId = sizeGroupId;
	}


	public ArrayList<Size> getSizeList() {
		return sizeList;
	}


	public void setSizeList(ArrayList<Size> sizeList) {
		this.sizeList = sizeList;
	}


	public String getUnitId() {
		return unitId;
	}


	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}


	public String getIndentDate() {
		return indentDate;
	}


	public void setIndentDate(String indentDate) {
		this.indentDate = indentDate;
	}
	public String getIndentType() {
		return indentType;
	}
	public void setIndentType(String indentType) {
		this.indentType = indentType;
	}
	public String getLengthUnitId() {
		return lengthUnitId;
	}
	public void setLengthUnitId(String lengthUnitId) {
		this.lengthUnitId = lengthUnitId;
	}
	public String getSqNo() {
		return sqNo;
	}
	public void setSqNo(String sqNo) {
		this.sqNo = sqNo;
	}
	public String getSkuNo() {
		return skuNo;
	}
	public void setSkuNo(String skuNo) {
		this.skuNo = skuNo;
	}


	public String getPoNo() {
		return poNo;
	}


	public void setPoNo(String poNo) {
		this.poNo = poNo;
	}


	public String getOrderQty() {
		return orderQty;
	}


	public void setOrderQty(String orderQty) {
		this.orderQty = orderQty;
	}


	public String getStoreReceivedBalance() {
		return storeReceivedBalance;
	}


	public void setStoreReceivedBalance(String storeReceivedBalance) {
		this.storeReceivedBalance = storeReceivedBalance;
	}


	public String getReceivedRemain() {
		return receivedRemain;
	}


	public void setReceivedRemain(String receivedRemain) {
		this.receivedRemain = receivedRemain;
	}

}

