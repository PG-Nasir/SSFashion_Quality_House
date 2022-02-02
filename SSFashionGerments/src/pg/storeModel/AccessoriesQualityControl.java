package pg.storeModel;

import java.util.ArrayList;
import java.util.List;

public class AccessoriesQualityControl {
	String autoId;
	String qcTransactionId;
	String qcDate;
	String grnNo;
	String receiveDate;
	String remarks;
	String fabricsName;
	String supplierId;
	String checkBy;
	String departmentId;
	String sizeList;
	List<AccessoriesSize> accessoriesSizeList;
	String userId;
	public AccessoriesQualityControl() {}
	public AccessoriesQualityControl(String autoId, String qcTransectionId, String qcDate, String grnNo, String remarks,String userId) {
		super();
		this.autoId = autoId;
		this.qcTransactionId = qcTransectionId;
		this.qcDate = qcDate;
		this.grnNo = grnNo;
		this.remarks = remarks;
		this.userId = userId;
	}
	
	public AccessoriesQualityControl(String autoId, String transectionId, String qcDate, String grnNo,String receiveDate, String remarks,
			 String supplierId,String checkBy, String userId) {
		super();
		this.autoId = autoId;
		this.qcTransactionId = transectionId;
		this.qcDate = qcDate;
		this.grnNo = grnNo;
		this.receiveDate = receiveDate;
		this.remarks = remarks;
		this.supplierId = supplierId;
		this.checkBy = checkBy;
		this.userId = userId;
	}
	public String getFabricsName() {
		return fabricsName;
	}
	public void setFabricsName(String fabricsName) {
		this.fabricsName = fabricsName;
	}
	public String getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getQcTransactionId() {
		return qcTransactionId;
	}
	public void setQcTransactionId(String qcTransactionId) {
		this.qcTransactionId = qcTransactionId;
	}
	public String getQcDate() {
		return qcDate;
	}
	public void setQcDate(String qcDate) {
		this.qcDate = qcDate;
	}
	
	public String getReceiveDate() {
		return receiveDate;
	}
	public void setReceiveDate(String receiveDate) {
		this.receiveDate = receiveDate;
	}
	public String getGrnNo() {
		return grnNo;
	}
	public void setGrnNo(String grnNo) {
		this.grnNo = grnNo;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getSizeList() {
		return sizeList;
	}
	public void setSizeList(String sizeList) {
		try {
			System.out.println(sizeList);
			if(sizeList.trim().length()>0) {
				String[] sizeLists = sizeList.split("#");
				List<AccessoriesSize> list = new ArrayList<AccessoriesSize>();
				String autoId,transectionId,purchaseOrder,styleId,itemId,itemColorId,fabricsId,fabricsColorId,rollId,unitId,remarks,rackName,binName,userId;
				double unitQty; int qcPassedType;
				for (String item : sizeLists) {
					String[] itemProperty = item.split(",");

					autoId = itemProperty[0].substring(itemProperty[0].indexOf(":")+1).trim();
					transectionId = itemProperty[1].substring(itemProperty[1].indexOf(":")+1).trim();
					purchaseOrder = itemProperty[2].substring(itemProperty[2].indexOf(":")+1).trim();
					styleId = itemProperty[3].substring(itemProperty[3].indexOf(":")+1).trim();
					itemId = itemProperty[4].substring(itemProperty[4].indexOf(":")+1).trim();
					itemColorId = itemProperty[5].substring(itemProperty[5].indexOf(":")+1).trim();
					fabricsId = itemProperty[6].substring(itemProperty[6].indexOf(":")+1).trim();
					fabricsColorId = itemProperty[7].substring(itemProperty[7].indexOf(":")+1).trim();
					rollId = itemProperty[8].substring(itemProperty[8].indexOf(":")+1).trim();
					unitId = itemProperty[9].substring(itemProperty[9].indexOf(":")+1).trim();
					unitQty = Double.valueOf(itemProperty[10].substring(itemProperty[10].indexOf(":")+1).trim());
					rackName = itemProperty[11].substring(itemProperty[11].indexOf(":")+1).trim();
					binName = itemProperty[12].substring(itemProperty[12].indexOf(":")+1).trim();
					remarks = "";
					qcPassedType = Integer.valueOf(itemProperty[13].substring(itemProperty[13].indexOf(":")+1).trim());
					userId = itemProperty[14].substring(itemProperty[14].indexOf(":")+1).trim();
					list.add(new AccessoriesSize(autoId, transectionId,purchaseOrder,styleId,itemId,itemColorId,fabricsId,fabricsColorId,rollId,unitId ,unitQty,rackName,binName,remarks, qcPassedType, false,userId));
				}
				this.accessoriesSizeList = list;
			}
			
			this.sizeList = sizeList;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<AccessoriesSize> getAccessoriesSizeList() {
		return accessoriesSizeList;
	}
	public void setAccessoriesSizeList(List<AccessoriesSize> accessoriesSizeList) {
		this.accessoriesSizeList = accessoriesSizeList;
	}
	public String getCheckBy() {
		return checkBy;
	}
	public void setCheckBy(String checkBy) {
		this.checkBy = checkBy;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}



}
