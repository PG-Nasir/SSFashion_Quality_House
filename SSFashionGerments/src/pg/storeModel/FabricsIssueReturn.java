package pg.storeModel;

import java.util.ArrayList;
import java.util.List;

public class FabricsIssueReturn {
	String autoId;
	String transactionId;
	String issueReturnDate;
	String issueReturnFrom;
	String issueReturnDepartmentName;
	String remarks;
	String receiveFrom;
	String departmentId;
	String rollList;
	List<FabricsRoll> fabricsRollList;
	String userId;
	String styleNo="";
	String itemName="";
	String itemColor="";
	
	public FabricsIssueReturn() {}
	public FabricsIssueReturn(String autoId, String transectionId, String issueReturnDate, String issueReturnFrom, String receiveFrom, String remarks, String userId) {
		super();
		this.autoId = autoId;
		this.transactionId = transectionId;
		this.issueReturnDate = issueReturnDate;
		this.issueReturnFrom = issueReturnFrom;
		this.receiveFrom = receiveFrom;
		this.remarks = remarks;
		this.userId = userId;
	}
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getRollList() {
		return rollList;
	}
	public void setRollList(String rollList) {
		try {
			System.out.println(rollList);
			if(rollList.trim().length()>0) {
				String[] rollLists = rollList.split("#");
				List<FabricsRoll> list = new ArrayList<FabricsRoll>();
				String autoId,transectionId,purchaseOrder,styleId,itemId,itemColorId,fabricsId,fabricsName,fabricsColorId,fabricsColorName,rollId,supplierRollId,unitId,unit,userId,rackName,binName;
				double qcReturnQty,unitQty,balanceQty; int qcPassedType;
				boolean isReturn;
				for (String item : rollLists) {
					String[] itemProperty = item.split("@");
					autoId = itemProperty[0].substring(itemProperty[0].indexOf(":")+1).trim();
					transectionId = itemProperty[1].substring(itemProperty[1].indexOf(":")+1).trim();
					purchaseOrder = itemProperty[2].substring(itemProperty[2].indexOf(":")+1).trim();
					styleId = itemProperty[3].substring(itemProperty[3].indexOf(":")+1).trim();
					styleNo = itemProperty[4].substring(itemProperty[4].indexOf(":")+1).trim();
					System.out.println("styleNo "+styleNo);
					
					itemId = itemProperty[5].substring(itemProperty[5].indexOf(":")+1).trim();
					itemName = itemProperty[6].substring(itemProperty[6].indexOf(":")+1).trim();
					System.out.println("itemName "+itemName);
					
					itemColorId = itemProperty[7].substring(itemProperty[7].indexOf(":")+1).trim();
					itemColor = itemProperty[8].substring(itemProperty[8].indexOf(":")+1).trim();
					fabricsId = itemProperty[9].substring(itemProperty[9].indexOf(":")+1).trim();
					fabricsName = itemProperty[10].substring(itemProperty[10].indexOf(":")+1).trim();
					fabricsColorId = itemProperty[11].substring(itemProperty[11].indexOf(":")+1).trim();
					fabricsColorName = itemProperty[12].substring(itemProperty[12].indexOf(":")+1).trim();;
					balanceQty = 0;
					rollId = itemProperty[13].substring(itemProperty[13].indexOf(":")+1).trim();
					supplierRollId = itemProperty[14].substring(itemProperty[14].indexOf(":")+1).trim();
					unitId = itemProperty[15].substring(itemProperty[15].indexOf(":")+1).trim();
					unit = itemProperty[16].substring(itemProperty[16].indexOf(":")+1).trim();
					unitQty = Double.valueOf(itemProperty[17].substring(itemProperty[17].indexOf(":")+1).trim());
					rackName = itemProperty[18].substring(itemProperty[18].indexOf(":")+1).trim();
					binName = itemProperty[19].substring(itemProperty[19].indexOf(":")+1).trim();
					qcPassedType = 1;
					list.add(new FabricsRoll(autoId, transectionId, purchaseOrder, styleId,styleNo, itemId,itemName ,itemColorId,itemColor, fabricsId,fabricsName, fabricsColorId,fabricsColorName, rollId ,supplierRollId, unitId,"unitName", balanceQty,unitQty, rackName, binName,qcPassedType));
				}

				this.fabricsRollList = list;
				
			}
			this.rollList = rollList;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public List<FabricsRoll> getFabricsRollList() {
		return fabricsRollList;
	}
	public void setFabricsRollList(List<FabricsRoll> fabricsRollList) {
		this.fabricsRollList = fabricsRollList;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getIssueReturnDate() {
		return issueReturnDate;
	}
	public void setIssueReturnDate(String issueReturnDate) {
		this.issueReturnDate = issueReturnDate;
	}
	public String getIssueReturnFrom() {
		return issueReturnFrom;
	}
	public void setIssueReturnFrom(String issueReturnFrom) {
		this.issueReturnFrom = issueReturnFrom;
	}
	public String getIssueReturnDepartmentName() {
		return issueReturnDepartmentName;
	}
	public void setIssueReturnDepartmentName(String issueReturnDepartmentName) {
		this.issueReturnDepartmentName = issueReturnDepartmentName;
	}
	public String getReceiveFrom() {
		return receiveFrom;
	}
	public void setReceiveFrom(String receiveFrom) {
		this.receiveFrom = receiveFrom;
	}
	public String getStyleNo() {
		return styleNo;
	}
	public void setStyleNo(String styleNo) {
		this.styleNo = styleNo;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemColor() {
		return itemColor;
	}
	public void setItemColor(String itemColor) {
		this.itemColor = itemColor;
	}
	
	
}
