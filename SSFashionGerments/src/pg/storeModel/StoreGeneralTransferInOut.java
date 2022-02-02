package pg.storeModel;

public class StoreGeneralTransferInOut {
	
	String transferid;
	String transefdate;
	String itemid;
	String itemname;
	String unit;
	String unitid;
	String catid;
	String qty;
	String receive;
	String returned;
	String transferout;
	String transferin;
	String issue;
	String receivedby;
	String stock;
	String transferdate;
	String department;
	String detparmentid;
	String remark;
	String userid;
	String type;
	String searched;
	String issuereturned;

	
	
	public StoreGeneralTransferInOut() {
		
	}
	
	
	public StoreGeneralTransferInOut(String itemid, String itemname, String stock) {
		this.itemid=itemid;
		this.itemname=itemname;
		this.stock=stock;
	}
	
	public StoreGeneralTransferInOut(String invoice, String date, String deptname,String none) {
		this.transferid=invoice;
		this.transefdate=date;
		this.department=deptname;
	}
	
	public StoreGeneralTransferInOut(String itemid, String itemname,String unitid, String unit,String rec, String trin, String trout,String issue,String returned,String qty,String issuereturn) {
		this.itemid=itemid;
		this.itemname=itemname;
		this.unitid=unitid;
		this.unit=unit;
		this.receive=rec;
		this.transferin=trin;
		this.transferout=trout;
		this.issue=issue;
		this.returned=returned;
		this.qty=qty;
		this.issuereturned=issuereturn;
		
	}
	
	

	
	
	public StoreGeneralTransferInOut(String invoiceno,String recby, String date, String deptid, String remark, String itemid, String itemname, String unit, String unitname, String rec, String trin, String trout, String issue,String retruned,String qty,String issuereturn) {
		this.transferid=invoiceno;
		this.receivedby=recby;
		this.transferdate=date;
		this.detparmentid=deptid;
		this.remark=remark;
		this.itemid=itemid;
		this.itemname=itemname;
		this.unitid=unit;
		this.unit=unitname;
		this.receive=rec;
		this.transferin=trin;
		this.transferout=trout;
		this.issue=issue;
		this.returned=retruned;
		this.qty=qty;
		this.issuereturned=issuereturn;
	}


	public String getTransferid() {
		return transferid;
	}


	public void setTransferid(String transferid) {
		this.transferid = transferid;
	}


	public String getItemid() {
		return itemid;
	}


	public void setItemid(String itemid) {
		this.itemid = itemid;
	}


	public String getItemname() {
		return itemname;
	}


	public void setItemname(String itemname) {
		this.itemname = itemname;
	}


	public String getCatid() {
		return catid;
	}


	public void setCatid(String catid) {
		this.catid = catid;
	}


	public String getQty() {
		return qty;
	}


	public void setQty(String qty) {
		this.qty = qty;
	}


	public String getReceive() {
		return receive;
	}


	public void setReceive(String receive) {
		this.receive = receive;
	}


	public String getReturned() {
		return returned;
	}


	public void setReturned(String returned) {
		this.returned = returned;
	}


	public String getTransferout() {
		return transferout;
	}


	public void setTransferout(String transferout) {
		this.transferout = transferout;
	}


	public String getTransferin() {
		return transferin;
	}


	public void setTransferin(String transferin) {
		this.transferin = transferin;
	}


	public String getIssue() {
		return issue;
	}


	public void setIssue(String issue) {
		this.issue = issue;
	}


	public String getReceivedby() {
		return receivedby;
	}


	public void setReceivedby(String receivedby) {
		this.receivedby = receivedby;
	}


	public String getStock() {
		return stock;
	}


	public void setStock(String stock) {
		this.stock = stock;
	}


	public String getTransferdate() {
		return transferdate;
	}


	public void setTransferdate(String transferdate) {
		this.transferdate = transferdate;
	}


	public String getDepartment() {
		return department;
	}


	public void setDepartment(String department) {
		this.department = department;
	}


	public String getDetparmentid() {
		return detparmentid;
	}


	public void setDetparmentid(String detparmentid) {
		this.detparmentid = detparmentid;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getUnit() {
		return unit;
	}


	public void setUnit(String unit) {
		this.unit = unit;
	}


	public String getUnitid() {
		return unitid;
	}


	public void setUnitid(String unitid) {
		this.unitid = unitid;
	}


	public String getTransefdate() {
		return transefdate;
	}


	public void setTransefdate(String transefdate) {
		this.transefdate = transefdate;
	}


	public String getUserid() {
		return userid;
	}


	public void setUserid(String userid) {
		this.userid = userid;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getSearched() {
		return searched;
	}


	public void setSearched(String searched) {
		this.searched = searched;
	}


	public String getIssuereturned() {
		return issuereturned;
	}


	public void setIssuereturned(String issuereturned) {
		this.issuereturned = issuereturned;
	}


	
	
	
	

}
