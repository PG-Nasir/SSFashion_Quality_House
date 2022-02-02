package pg.storeModel;

import java.util.ArrayList;
import java.util.List;

public class AccessoriesReturn {
	
	String autoId;
	String returnTransactionId;
	String returnDate;
	String receiveDate;
	String supplierId;
	String supplierName;
	String remarks;
	String departmentId;
	String sizeList;
	List<AccessoriesSize> accessoriesSizeList;
	String userId;
	
	public AccessoriesReturn() {}
	public AccessoriesReturn(String autoId, String returnTransectionId, String returnDate,
		String supplierId,String supplierName, String remarks, String userId) {
		super();
		this.autoId = autoId;
		this.returnTransactionId = returnTransectionId;
		this.returnDate = returnDate;	
		this.supplierId = supplierId;
		this.supplierName = supplierName;
		this.remarks = remarks;
		this.userId = userId;
	}
	
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getReturnTransactionId() {
		return returnTransactionId;
	}
	public void setReturnTransactionId(String returnTransactionId) {
		this.returnTransactionId = returnTransactionId;
	}
	public String getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}
	public String getReceiveDate() {
		return receiveDate;
	}
	public void setReceiveDate(String receiveDate) {
		this.receiveDate = receiveDate;
	}

	
	public String getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
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
				String autoId,transectionId,purchaseOrder,styleId,itemId,itemColorId,fabricsId,fabricsName,fabricsColorId,fabricsColorName,rollId,supplierRollId,unitId,unit,userId,rackName,binName;
				double qcReturnQty,unitQty,balanceQty; int qcPassedType;
				boolean isReturn;
				for (String item : sizeLists) {
					String[] itemProperty = item.split(",");
					autoId = itemProperty[0].substring(itemProperty[0].indexOf(":")+1).trim();
					transectionId = itemProperty[1].substring(itemProperty[1].indexOf(":")+1).trim();
					purchaseOrder = itemProperty[2].substring(itemProperty[2].indexOf(":")+1).trim();
					styleId = itemProperty[3].substring(itemProperty[3].indexOf(":")+1).trim();
					itemId = itemProperty[4].substring(itemProperty[4].indexOf(":")+1).trim();
					itemColorId = itemProperty[5].substring(itemProperty[5].indexOf(":")+1).trim();
					fabricsId = itemProperty[6].substring(itemProperty[6].indexOf(":")+1).trim();
					fabricsName = itemProperty[7].substring(itemProperty[7].indexOf(":")+1).trim();
					fabricsColorId = itemProperty[8].substring(itemProperty[8].indexOf(":")+1).trim();
					fabricsColorName = itemProperty[9].substring(itemProperty[9].indexOf(":")+1).trim();;
					balanceQty = 0;
					rollId = itemProperty[10].substring(itemProperty[10].indexOf(":")+1).trim();
					supplierRollId = itemProperty[11].substring(itemProperty[11].indexOf(":")+1).trim();
					unitId = itemProperty[12].substring(itemProperty[12].indexOf(":")+1).trim();
					unit = itemProperty[13].substring(itemProperty[13].indexOf(":")+1).trim();
					unitQty = Double.valueOf(itemProperty[14].substring(itemProperty[14].indexOf(":")+1).trim());
					rackName = itemProperty[15].substring(itemProperty[15].indexOf(":")+1).trim();
					binName = itemProperty[16].substring(itemProperty[16].indexOf(":")+1).trim();
					qcPassedType = 1;
					list.add(new AccessoriesSize(autoId, transectionId, purchaseOrder, styleId,"Style No", itemId,"item Name" ,itemColorId,"item color", fabricsId,fabricsName, fabricsColorId,fabricsColorName, rollId ,supplierRollId, unitId,"unitName", balanceQty,unitQty, rackName, binName,qcPassedType));
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	
	
	

}
