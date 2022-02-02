package pg.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import com.barcodelib.barcode.a.c.f;

import pg.model.CommonModel;
import pg.orderModel.AccessoriesIndent;
import pg.orderModel.FabricsIndent;
import pg.orderModel.PurchaseOrderItem;
import pg.orderModel.SampleRequisitionItem;
import pg.orderModel.Style;
import pg.share.HibernateUtil;
import pg.share.ItemType;
import pg.share.SizeValuesType;
import pg.share.StoreTransaction;
import pg.storeModel.AccessoriesIssue;
import pg.storeModel.AccessoriesIssueReturn;
import pg.storeModel.AccessoriesQualityControl;
import pg.storeModel.AccessoriesReceive;
import pg.storeModel.AccessoriesReturn;
import pg.storeModel.AccessoriesSize;
import pg.storeModel.AccessoriesStock;
import pg.storeModel.AccessoriesTransferIn;
import pg.storeModel.AccessoriesTransferOut;
import pg.storeModel.CuttingFabricsUsed;
import pg.storeModel.FabricsIssue;
import pg.storeModel.FabricsIssueReturn;
import pg.storeModel.FabricsQualityControl;
import pg.storeModel.FabricsReceive;
import pg.storeModel.FabricsReturn;
import pg.storeModel.FabricsRoll;
import pg.storeModel.FabricsTransferIn;
import pg.storeModel.FabricsTransferOut;
import pg.storeModel.PendingTransaction;
import pg.storeModel.StockItem;
import pg.storeModel.StoreGeneralCategory;
import pg.storeModel.StoreGeneralReceived;
import pg.storeModel.StoreGeneralTransferInOut;
import pg.storeModel.StoreGeneralCategory;
import pg.storeModel.StoreGeneralReceived;


@Repository
public class StoreDAOImpl implements StoreDAO{

	@Override
	public List<FabricsIndent> getFabricsPurchaseOrdeIndentrList(String supplierId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;



		List<FabricsIndent> datalist=new ArrayList<FabricsIndent>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql="select rf.id,rf.pono,rf.PurchaseOrder,rf.styleId,ISNULL(rf.StyleNo,'') as StyleNo,rf.itemId,ISNULL(rf.itemname,'') as itemname,rf.itemcolor as itemColorId,ISNULL(rf.itemColorName,'') as itemColorName,rf.fabricsid,fi.ItemName as fabricsName,rf.fabricscolor as fabricsColorId,isnull(fc.Colorname,'') as fabricsColor\n" + 
					"from tbFabricsIndent rf\n" + 
					"left join tbColors fc\n" + 
					"on rf.fabricscolor = fc.ColorId\n" + 
					"left join TbFabricsItem fi\n" + 
					"on rf.fabricsid = fi.id\n" + 
					"where rf.pono is not null and rf.pono!='0'  and rf.supplierId = '"+supplierId+"'\n" + 
					"order by rf.id desc";

			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				datalist.add(new FabricsIndent(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(),  element[11].toString(),element[12].toString()));		
			}		
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}



	@Override
	public FabricsIndent getFabricsIndentInfo(String autoId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		FabricsIndent indent= null;
		try{	
			tx=session.getTransaction();
			tx.begin();	
			String sql="select rf.id,PurchaseOrder,rf.styleId,rf.StyleNo,rf.itemid,rf.itemname,rf.itemcolor,rf.itemColorName,rf.fabricsid,fi.ItemName as fabricsName,rf.fabricscolor,ISNULL((select colorname from tbColors where ColorId=rf.fabricscolor),'') as FabricsColor,rf.unitId,u.unitname,rf.RequireUnitQty,\n" + 
					"(select isnull(sum(receivedQty),0) from tbFabricsAccessoriesTransaction fat where fat.purchaseOrder = rf.PurchaseOrder and fat.styleId = rf.styleId and fat.styleItemId = rf.itemid and fat.colorId = rf.itemcolor and fat.dItemId = rf.fabricsId and fat.itemColorId = rf.fabricscolor and fat.transactionType = '1') as previousReceiveQty\n" + 
					"from tbFabricsIndent rf\n" + 
					"left join TbFabricsItem fi\n" + 
					"on rf.fabricsid = fi.id\n" + 
					"left join tbunits u\n" + 
					"on rf.unitId = u.Unitid\n" + 
					"where rf.id = '"+autoId+"'";

			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				indent = new FabricsIndent(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), Double.valueOf(element[14].toString()),Double.valueOf(element[15].toString()));
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return indent;
	}



	@Override
	public boolean submitFabricsReceive(FabricsReceive fabricsReceive) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select (isnull(max(transactionid),0)+1) as maxId from tbfabricsReceiveInfo";
			List<?> list = session.createSQLQuery(sql).list();
			String transactionId="0";
			if(list.size()>0) {
				transactionId = list.get(0).toString();
			}

			sql="insert into tbfabricsReceiveInfo (transactionid,"
					+ "grnNo,"
					+ "grnDate,"
					+ "location,"		
					+ "supplierId,"
					+ "challanNo,"
					+ "challanDate,"
					+ "remarks,"
					+ "departmentId,"
					+ "preperedBy,"
					+ "entryTime,"
					+ "createBy) \r\n" + 
					"values('"+transactionId+"',"
					+ "'"+fabricsReceive.getGrnNo()+"',"
					+ "'"+fabricsReceive.getGrnDate()+"',"
					+ "'"+fabricsReceive.getLocation()+"',"
					+ "'"+fabricsReceive.getSupplierId()+"',"
					+ "'"+fabricsReceive.getChallanNo()+"',"
					+ "'"+fabricsReceive.getChallanDate()+"',"
					+ "'"+fabricsReceive.getRemarks()+"',"
					+ "'1',"
					//+ "'"+fabricsReceive.getDepartmentId()+"',"
					+ "'"+fabricsReceive.getPreparedBy()+"',"
					+ "CURRENT_TIMESTAMP,"
					+ "'"+fabricsReceive.getUserId()+"') ;";
			session.createSQLQuery(sql).executeUpdate();


			String resultList=fabricsReceive.getFabricsRollList().substring(fabricsReceive.getFabricsRollList().indexOf("[")+1, fabricsReceive.getFabricsRollList().indexOf("]"));
			System.out.println("resultList "+resultList);
			StringTokenizer token=new StringTokenizer(resultList, "@");
			while(token.hasMoreTokens()){
				String splitToken=token.nextToken().toString();
				System.out.println("splitToken "+splitToken);
				StringTokenizer resulttoken=new StringTokenizer(splitToken, "*");
				while(resulttoken.hasMoreTokens()) {

					String purchaseOrder=resulttoken.nextToken().replace(",", "");
					String styleId=resulttoken.nextToken();
					String styleName=resulttoken.nextToken().toString().replace(".", "");
					String itemId=resulttoken.nextToken().toString().replace(".", "");
					String itemNo=resulttoken.nextToken().toString().replace(".", "");
					String itemColorId=resulttoken.nextToken().toString().replace(".", "");
					String itemColorName=resulttoken.nextToken().toString().replace(".", "");
					String fabricsId=resulttoken.nextToken().toString().replace(".", "");
					String fabricsColorId=resulttoken.nextToken().toString().replace(".", "");
					String unitId=resulttoken.nextToken().toString().replace(".", "");
					String fabricsName=resulttoken.nextToken().toString().replace(".", "");
					String fabricsColor=resulttoken.nextToken().toString().replace(".", "");
					String rollId=resulttoken.nextToken().toString().replace(".", "");
					String supplierRollId=resulttoken.nextToken().toString().replace(".", "");
					String receivedQty=resulttoken.nextToken();
					String rackName=resulttoken.nextToken();
					String binName=resulttoken.nextToken();

					sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,"
							+ "styleId,styleNo,"
							+ "styleItemId,itemName,"
							+ "colorId,colorName,"
							+ "itemColorId,"
							+ "transactionId,transactionType,"
							+ "itemType,rollId,"
							+ "unitId,qty,"
							+ "dItemId,cItemId,"
							+ "departmentId,"
							+ "rackName,binName,"
							+ "entryTime,userId) \r\n" + 
							"values('"+purchaseOrder+"',"
							+ "'"+styleId+"','"+styleName+"',"
							+ "'"+itemId+"','"+itemNo+"',"
							+ "'"+itemColorId+"','"+itemColorName+"',"
							+ "'"+fabricsColorId+"',"
							+ "'"+transactionId+"','"+StoreTransaction.FABRICS_RECEIVE.getType()+"',"
							+ "'"+ItemType.FABRICS.getType()+"','"+rollId+"',"
							+ "'"+unitId+"','"+receivedQty+"',"
							+ "'"+fabricsId+"','0',"
							+ "'"+fabricsReceive.getDepartmentId()+"',"
							+ "'"+rackName+"','"+binName+"',"
							+ "CURRENT_TIMESTAMP,'"+fabricsReceive.getUserId()+"');";		
					session.createSQLQuery(sql).executeUpdate();

				}
			}


			/*			for (FabricsRoll roll : fabricsReceive.getFabricsRollList()) {
				rollId++;
				sql="insert into tbFabricsRollInfo (rollId,supplierRollId,entryTime,createBy) values('"+rollId+"','"+roll.getSupplierRollId()+"',CURRENT_TIMESTAMP,'"+fabricsReceive.getUserId()+"');";		
				session.createSQLQuery(sql).executeUpdate();

				sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,rollId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
						"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getItemId()+"','"+roll.getItemColorId()+"','"+roll.getFabricsColorId()+"','"+transactionId+"','"+StoreTransaction.FABRICS_RECEIVE.getType()+"','"+ItemType.FABRICS.getType()+"','"+rollId+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getUnitQty()+"','"+roll.getFabricsId()+"','0','"+fabricsReceive.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+fabricsReceive.getUserId()+"');";		
				session.createSQLQuery(sql).executeUpdate();
			}*/

			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public boolean editFabricsReceive(FabricsReceive fabricsReceive) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();
			String sql="update tbfabricsReceiveInfo set "
					+ "grnNo='"+fabricsReceive.getGrnNo()+"',"
					+ "grnDate='"+fabricsReceive.getGrnDate()+"',"
					+ "location='"+fabricsReceive.getLocation()+"',"
					+ "supplierId='"+fabricsReceive.getSupplierId()+"',"
					+ "challanNo='"+fabricsReceive.getChallanNo()+"',"
					+ "challanDate='"+fabricsReceive.getChallanDate()+"',"
					+ "remarks='"+fabricsReceive.getRemarks()+"',"
					+ "preperedBy='"+fabricsReceive.getPreparedBy()+"' where transactionId= '"+fabricsReceive.getTransactionId()+"'";


			session.createSQLQuery(sql).executeUpdate();

			sql = "select isnull(max(autoId),0) as id from  tbFabricsRollInfo";		
			int rollId = 0;
			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				rollId = Integer.valueOf(list.get(0).toString());				
			}	

			/*			if(fabricsReceive.getFabricsRollList() != null) {
				for (FabricsRoll roll : fabricsReceive.getFabricsRollList()) {
					rollId++;
					sql="insert into tbFabricsRollInfo (rollId,supplierRollId,entryTime,createBy) values('"+rollId+"','"+roll.getSupplierRollId()+"',CURRENT_TIMESTAMP,'"+fabricsReceive.getUserId()+"');";		
					session.createSQLQuery(sql).executeUpdate();

					sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,rollId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
							"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getItemId()+"','"+roll.getItemColorId()+"','"+roll.getFabricsColorId()+"','"+fabricsReceive.getTransactionId()+"','"+StoreTransaction.FABRICS_RECEIVE.getType()+"','"+ItemType.FABRICS.getType()+"','"+roll.getRollId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getUnitQty()+"','"+roll.getFabricsId()+"','0','"+fabricsReceive.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+fabricsReceive.getUserId()+"');";		
					session.createSQLQuery(sql).executeUpdate();
				}

			}*/

			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public String editReceiveRollInTransaction(FabricsRoll fabricsRoll) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();
			String sql = "select far.autoId,far.purchaseOrder,far.qty from tbFabricsAccessoriesTransaction far\r\n" + 
					"join tbFabricsAccessoriesTransaction far2\r\n" + 
					"on far.dItemId= far2.cItemId and far2.rollId = far.rollId and far2.itemColorId = far.itemColorId and far2.colorId = far.colorId and far2.styleItemId= far.styleItemId \r\n" + 
					"and far2.styleId = far.styleId and far2.purchaseOrder = far.purchaseOrder and (far.transactionType ='"+StoreTransaction.FABRICS_RETURN.getType()+"' or far.transactionType ='"+StoreTransaction.FABRICS_ISSUE.getType()+"')\r\n" + 
					"where far.autoId = '"+fabricsRoll.getAutoId()+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()==0) {
				sql = "update tbFabricsRollInfo set supplierRollId = '"+fabricsRoll.getSupplierRollId()+"' where rollId = '"+fabricsRoll.getRollId()+"'";
				session.createSQLQuery(sql).executeUpdate();

				sql = "update tbFabricsAccessoriesTransaction set unitQty = '"+fabricsRoll.getUnitQty()+"',qty = '"+fabricsRoll.getUnitQty()+"' where autoId = '"+fabricsRoll.getAutoId()+"'";
				if(session.createSQLQuery(sql).executeUpdate()==1) {
					tx.commit();
					return "Successful";
				}
			}			
			tx.commit();

			return "Used";
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}



	@Override
	public String deleteReceiveRollFromTransaction(FabricsRoll fabricsRoll) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();
			String sql = "select far.autoId,far.purchaseOrder,far.qty from tbFabricsAccessoriesTransaction far\r\n" + 
					"join tbFabricsAccessoriesTransaction far2\r\n" + 
					"on far.dItemId= far2.cItemId and far2.rollId = far.rollId and far2.itemColorId = far.itemColorId and far2.colorId = far.colorId and far2.styleItemId= far.styleItemId \r\n" + 
					"and far2.styleId = far.styleId and far2.purchaseOrder = far.purchaseOrder and (far.transactionType ='"+StoreTransaction.FABRICS_RETURN.getType()+"' or far.transactionType ='"+StoreTransaction.FABRICS_ISSUE.getType()+"')\r\n" + 
					"where far.autoId = '"+fabricsRoll.getAutoId()+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()==0) {
				sql = "delete from tbFabricsAccessoriesTransaction where autoId = '"+fabricsRoll.getAutoId()+"'";
				if(session.createSQLQuery(sql).executeUpdate()==1) {
					sql = "delete from tbFabricsRollInfo where rollId = '"+fabricsRoll.getRollId()+"'";
					session.createSQLQuery(sql).executeUpdate();
					tx.commit();
					return "Successful";
				}
			}			
			tx.commit();

			return "Used";
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}



	@Override
	public List<FabricsReceive> getFabricsReceiveList() {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<FabricsReceive> datalist=new ArrayList<FabricsReceive>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select autoId,transactionId,grnNo,(select convert(varchar,grnDate,103))as grnDate,location,fri.supplierId,fri.challanNo,fri.challanDate,fri.remarks,fri.preperedBy,fri.createBy \r\n" + 
					"from tbFabricsReceiveInfo fri";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				datalist.add(new FabricsReceive(element[0].toString(), element[1].toString(),element[2].toString(), element[3].toString(), element[4].toString(), "", element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString()));				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}

	@Override
	public FabricsReceive getFabricsReceiveInfo(String transactionId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsReceive fabricsReceive = null;
		FabricsRoll tempRoll;
		List<FabricsRoll> fabricsRollList = new ArrayList<FabricsRoll>();	
		try{	
			tx=session.getTransaction();
			tx.begin();	

			String sql="select far.autoId,far.transactionId,far.purchaseOrder,far.styleId,findent.styleNo,far.styleItemId,findent.itemname,far.colorId as itemColorId ,findent.itemColorName as itemColorName,dItemId as fabricsId,fi.ItemName as fabricsName,far.itemColorId as fabricsColorId,ISNULL(fc.Colorname,'') as fabricsColorName,far.rollId,isnull(fri.supplierRollId,'')as supplierRollId,far.unitId,u.unitname,far.qty,rackName,BinName,far.userId,\n" + 
					"findent.RequireUnitQty as orderQty,(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.dItemId = t.dItemId and far.itemColorId = t.itemColorId and t.transactionType = '1' and t.departmentId = far.departmentId and far.transactionId != t.transactionId) as previousReceiveQty,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.dItemId = t.cItemId and far.itemColorId = t.itemColorId and far.rollId = t.rollId and t.transactionType = '3' and t.departmentId = far.departmentId) as returnQty,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.dItemId = t.cItemId and far.itemColorId = t.itemColorId and far.rollId = t.rollId and t.transactionType = '4' and t.departmentId = far.departmentId) as issueQty,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction qc where far.purchaseOrder=qc.purchaseOrder and far.styleId =qc.styleId and far.styleItemId= qc.styleItemId and far.colorId = qc.colorId and far.dItemId = qc.dItemId and far.itemColorId = qc.itemColorId and far.rollId = qc.rollId and qc.transactionType = '2' and qc.departmentId = far.departmentId) as qcPassedQty \n" + 
					"from tbFabricsAccessoriesTransaction far\n" + 
					"left join tbFabricsIndent findent\n" + 
					"on far.PurchaseOrder = findent.purchaseOrder and far.styleId = findent.styleId and far.styleItemId= findent.itemId and far.colorId = findent.itemcolor and far.dItemId = findent.fabricsid and far.itemColorId = findent.fabricscolor\n" + 
					"\n" + 
					"left join tbfabricsRollInfo fri\n" + 
					"on far.rollId = fri.rollId \n" + 
					"left join tbunits u\n" + 
					"on far.unitId = u.Unitid\n" + 
					"left join TbFabricsItem fi\n" + 
					"on far.dItemId = fi.id\n" + 
					"\n" + 
					"left join tbColors fc\n" + 
					"on far.itemColorId = fc.ColorId\n" + 
					"where transactionId = '"+transactionId+"' and transactionType='"+StoreTransaction.FABRICS_RECEIVE.getType()+"'";


			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempRoll = new FabricsRoll(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(),element[7].toString(), element[8].toString(),element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(), element[15].toString(),element[16].toString(),0.0, Double.valueOf(element[17].toString()), element[18].toString(), element[19].toString(),1);
				tempRoll.setOrderQty(Double.valueOf(element[21].toString()));
				tempRoll.setPreviousReceiveQty(Double.valueOf(element[22].toString()));
				tempRoll.setReturnQty(Double.valueOf(element[23].toString()));
				tempRoll.setIssueQty(Double.valueOf(element[24].toString()));
				tempRoll.setQcPassedQty(Double.valueOf(element[25].toString()));
				fabricsRollList.add(tempRoll);				
			}

			sql = "select autoId,transactionId,grnNo,(select convert(varchar,grnDate,103))as grnDate,location,fri.supplierId,fri.challanNo,fri.challanDate,fri.remarks,fri.preperedBy,fri.createBy \r\n" + 
					"from tbFabricsReceiveInfo fri\r\n" + 
					"where fri.transactionId = '"+transactionId+"'";		
			list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				Object[] element = (Object[]) list.get(0);

				fabricsReceive = new FabricsReceive(element[0].toString(), element[1].toString(),element[2].toString(), element[3].toString(), element[4].toString(), "", element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString());
				fabricsReceive.setFabricsRollListData(fabricsRollList);
			}

			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return fabricsReceive;
	}


	//Fabrics Quality Control
	@Override
	public boolean submitFabricsQC(FabricsQualityControl fabricsQC) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.FABRICS_QC;
		ItemType itemType = ItemType.FABRICS;


		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select (isnull(max(transactionId),0)+1) as maxId from tbfabricsQualityControlInfo";
			List<?> list = session.createSQLQuery(sql).list();
			String transactionId="0";
			if(list.size()>0) {
				transactionId = list.get(0).toString();
			}

			sql="insert into tbfabricsQualityControlInfo (transactionId"
					+ ",date"
					+ ",grnNo"
					+ ",remarks"
					+ ",departmentId"
					+ ",checkBy"
					+ ",entryTime"
					+ ",createBy) values("
					+ "'"+transactionId+"'"
					+ ",'"+fabricsQC.getQcDate()+"'"
					+ ",'"+fabricsQC.getGrnNo()+"'"
					+ ",'"+fabricsQC.getRemarks()+"'"
					+ ",'"+fabricsQC.getDepartmentId()+"'"
					+ ",'"+fabricsQC.getCheckBy()+"',"
					+ "CURRENT_TIMESTAMP,'"+fabricsQC.getUserId()+"');";
			session.createSQLQuery(sql).executeUpdate();



			for (FabricsRoll roll : fabricsQC.getFabricsRollList()) {
				sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,rollId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
						"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getItemId()+"','"+roll.getItemColorId()+"','"+roll.getFabricsColorId()+"','"+transactionId+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getRollId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getUnitQty()+"','"+roll.getFabricsId()+"','0','"+fabricsQC.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+fabricsQC.getUserId()+"');";		
				session.createSQLQuery(sql).executeUpdate();
			}

			for (FabricsRoll roll : fabricsQC.getFabricsRollList()) {
				sql="insert into tbQualityControlDetails (transactionId,transactionType,itemType,itemId,rollId,unitId,QCCheckQty,shade,shrinkage,gsm,width,defect,remarks,qcPassedType,entryTime,userId) \r\n" + 
						"values('"+transactionId+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getFabricsId()+"','"+roll.getRollId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getShadeQty()+"','"+roll.getShrinkageQty()+"','"+roll.getGsmQty()+"','"+roll.getWidthQty()+"','"+roll.getDefectQty()+"','"+roll.getRemarks()+"','"+roll.getQcPassedType()+"',CURRENT_TIMESTAMP,'"+fabricsQC.getUserId()+"')";		
				session.createSQLQuery(sql).executeUpdate();
			}

			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public boolean editFabricsQC(FabricsQualityControl fabricsQC) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="update tbfabricsQualityControlInfo set "
					+ "date = '"+fabricsQC.getQcDate()+"'"
					+ ",grnNo = '"+fabricsQC.getGrnNo()+"'"
					+ ",remarks = '"+fabricsQC.getRemarks()+"'"
					+ ",entryTime = CURRENT_TIMESTAMP"
					+ ",createBy = '"+fabricsQC.getUserId()+"' where TransactionId='"+fabricsQC.getQcTransactionId()+"';";

			session.createSQLQuery(sql).executeUpdate();

			if(fabricsQC.getFabricsRollList() != null) {
				for (FabricsRoll roll : fabricsQC.getFabricsRollList()) {
					sql="update tbQualityControlDetails set QCTransactionId='"+fabricsQC.getQcTransactionId()+"',qcPassedQty='"+roll.getQcPassedQty()+"',qcPassedType='"+roll.getQcPassedType()+"' where autoId='"+roll.getAutoId()+"'";		
					session.createSQLQuery(sql).executeUpdate();
				}
			}
			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public List<FabricsQualityControl> getFabricsQCList() {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<FabricsQualityControl> datalist=new ArrayList<FabricsQualityControl>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select qci.AutoId,qci.TransactionId,(select convert(varchar,qci.date,103))as qcDate,qci.grnNo,(select convert(varchar,fri.grnDate,103))as receiveDate,qci.remarks,fri.supplierId,qci.checkBy,qci.createBy \r\n" + 
					"from tbFabricsQualityControlInfo qci\r\n" + 
					"left join tbFabricsReceiveInfo fri\r\n" + 
					"on qci.grnNo = fri.grnNo";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();

				datalist.add(new FabricsQualityControl(element[0].toString(),element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString()));				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}

	@Override
	public FabricsQualityControl getFabricsQCInfo(String qcTransactionId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsQualityControl fabricsQC = null;
		List<FabricsRoll> fabricsRollList = new ArrayList<FabricsRoll>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select qcd.autoId,qcd.transactionId,qcd.rollId,fri.supplierRollId,fat.purchaseOrder,fat.styleId,fat.styleItemId,fat.colorId,fat.dItemId,fi.ItemName as fabricsName,fat.itemColorId,isnull(c.Colorname,'') as fabricsColor ,qcd.unitId,u.unitname,far.unitQty,QCCheckQty,shade,shrinkage,gsm,width,defect,remarks,fat.rackName,fat.BinName,qcPassedType,qcd.userId  \r\n" + 
					"from tbQualityControlDetails qcd\r\n"
					+ "left join tbfabricsRollInfo fri\r\n" + 
					"on qcd.rollId = fri.rollId\r\n" + 
					"left join tbunits u\r\n" + 
					"on qcd.unitId = u.Unitid\r\n" + 
					"left join tbFabricsAccessoriesTransaction far\r\n" + 
					"on qcd.transactionId = far.transactionId and far.transactionType = '"+StoreTransaction.FABRICS_QC.getType()+"' and qcd.itemId = far.dItemId and qcd.rollId = far.rollId\r\n" + 
					"left join tbFabricsAccessoriesTransaction fat\r\n" + 
					"on qcd.transactionId = fat.transactionId and qcd.transactionType = fat.transactionType and qcd.itemId = fat.dItemId and qcd.rollId = fat.rollId \n"+
					"left join tbFabricsItem fi \n" + 
					"on fat.dItemId = fi.id \n" + 
					"left join tbColors c\r\n" + 
					"on fat.itemColorId = c.ColorId\r\n" + 
					"where qcd.transactionId = '"+qcTransactionId+"' and qcd.transactionType='"+StoreTransaction.FABRICS_QC.getType()+"' \r\n" + 
					"";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();

				fabricsRollList.add(new FabricsRoll(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), Double.valueOf(element[14].toString()), Double.valueOf(element[15].toString()), Double.valueOf(element[16].toString()), Double.valueOf(element[17].toString()), Double.valueOf(element[18].toString()), Double.valueOf(element[19].toString()),Double.valueOf(element[20].toString()), element[21].toString(), element[22].toString(), element[23].toString(), Integer.valueOf(element[24].toString())));				
			}
			sql = "select qci.AutoId,qci.TransactionId,(select convert(varchar,qci.date,103))as qcDate,qci.grnNo,(select convert(varchar,fri.grnDate,103))as receiveDate,qci.remarks,fri.supplierId,qci.checkBy,qci.createBy \r\n" + 
					"from tbFabricsQualityControlInfo qci\r\n" + 
					"left join tbFabricsReceiveInfo fri\r\n" + 
					"on qci.grnNo = fri.grnNo where qci.transactionId = '"+qcTransactionId+"'";		
			list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				Object[] element = (Object[]) list.get(0);

				fabricsQC = new FabricsQualityControl(element[0].toString(), qcTransactionId, element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(),element[8].toString());
				fabricsQC.setFabricsRollList(fabricsRollList);
			}

			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return fabricsQC;
	}

	@Override
	public List<FabricsRoll> getFabricsRollListBySupplier(String supplierId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsRoll tempRoll;
		List<FabricsRoll> datalist=new ArrayList<FabricsRoll>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select fat.autoID,fri.supplierId,s.name as supplierName,fat.purchaseOrder,fat.styleId,sc.StyleNo,fat.styleItemId,id.itemname,fat.colorid,c.colorName,fat.dItemId as fabricsId,fi.ItemName as fabricsName,fat.itemColorId,ic.Colorname as fabricsColor,fat.rollId,frinfo.supplierRollId,fi.unitId,u.unitname,dbo.fabricsBalanceQty(fat.purchaseOrder,fat.styleid,fat.styleItemId,fat.colorId,fat.dItemId,fat.ItemcolorId,fat.rollId,fat.departmentId) as balanceQty,fat.rackName,fat.binName,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.dItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = fat.transactionType and t.itemColorId = fat.itemColorId and t.dItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as ReceiveQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.cItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = '"+StoreTransaction.FABRICS_ISSUE.getType()+"' and t.itemColorId = fat.itemColorId and t.cItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as IssueQty,\r\n" +
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.cItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = '"+StoreTransaction.FABRICS_RETURN.getType()+"' and t.itemColorId = fat.itemColorId and t.cItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as previousReturnQty\r\n" +
					"from tbFabricsReceiveInfo fri\r\n" + 
					"left join tbSupplier s\r\n" + 
					"on fri.supplierId = s.id\r\n" + 
					"left join tbFabricsAccessoriesTransaction fat\r\n" + 
					"on fri.transactionId = fat.transactionId and fat.transactionType = '"+StoreTransaction.FABRICS_RECEIVE.getType()+"'\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on fat.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on fat.styleItemId = id.itemid\r\n" + 
					"left join tbColors c\r\n" + 
					"on fat.colorId = c.ColorId\r\n" + 
					"left join TbFabricsItem fi\r\n" + 
					"on fat.dItemId = fi.id\r\n" + 
					"left join tbColors ic\r\n" + 
					"on fat.itemColorId = ic.ColorId\r\n"
					+ "left join tbfabricsRollInfo frinfo\r\n" + 
					"on fat.rollId = frinfo.rollId \r\n" + 
					"left join tbunits u\r\n" + 
					"on fi.unitId = u.Unitid\r\n" + 
					" where fri.supplierId='"+supplierId+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempRoll = new FabricsRoll(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(),element[15].toString(),element[16].toString(),element[17].toString(), Double.valueOf(element[18].toString()),element[19].toString(),element[20].toString());
				tempRoll.setPreviousReceiveQty(Double.valueOf(element[21].toString()));
				tempRoll.setIssueQty(Double.valueOf(element[22].toString()));
				tempRoll.setReturnQty(Double.valueOf(element[23].toString()));

				datalist.add(tempRoll);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}



	@Override
	public boolean submitFabricsReturn(FabricsReturn fabricsReturn) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.FABRICS_RETURN;
		ItemType itemType = ItemType.FABRICS;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select (isnull(max(transactionId),0)+1) as maxId from tbFabricsReturnInfo";
			List<?> list = session.createSQLQuery(sql).list();
			String transactionId="0";
			if(list.size()>0) {
				transactionId = list.get(0).toString();
			}

			sql="insert into tbFabricsReturnInfo (transactionId"
					+ ",date"
					+ ",supplierId"
					+ ",remarks"
					+ ",departmentId"
					+ ",entryTime"
					+ ",createBy) values("
					+ "'"+transactionId+"'"
					+ ",'"+fabricsReturn.getReturnDate()+"'"
					+ ",'"+fabricsReturn.getSupplierId()+"'"
					+ ",'"+fabricsReturn.getRemarks()+"'"
					+ ",'"+fabricsReturn.getDepartmentId()+"',"
					+ "CURRENT_TIMESTAMP,'"+fabricsReturn.getUserId()+"');";
			session.createSQLQuery(sql).executeUpdate();


			for (FabricsRoll roll : fabricsReturn.getFabricsRollList()) {
				sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,rollId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
						"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getItemId()+"','"+roll.getItemColorId()+"','"+roll.getFabricsColorId()+"','"+transactionId+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getRollId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getUnitQty()+"','0','"+roll.getFabricsId()+"','"+fabricsReturn.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+fabricsReturn.getUserId()+"');";		
				session.createSQLQuery(sql).executeUpdate();
			}

			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public boolean editFabricsReturn(FabricsReturn fabricsReturn) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.FABRICS_RETURN;
		ItemType itemType = ItemType.FABRICS;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="update tbfabricsReturnInfo set "
					+ "date = '"+fabricsReturn.getReturnDate()+"'"
					+ ",supplierId = '"+fabricsReturn.getSupplierId()+"'"
					+ ",remarks = '"+fabricsReturn.getRemarks()+"'"
					+ ",entryTime = CURRENT_TIMESTAMP"
					+ ",createBy = '"+fabricsReturn.getUserId()+"' where transactionId='"+fabricsReturn.getReturnTransactionId()+"';";

			session.createSQLQuery(sql).executeUpdate();


			if(fabricsReturn.getFabricsRollList() != null) {
				for (FabricsRoll roll : fabricsReturn.getFabricsRollList()) {
					sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,rollId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
							"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getItemId()+"','"+roll.getItemColorId()+"','"+roll.getFabricsColorId()+"','"+fabricsReturn.getReturnTransactionId()+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getRollId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getUnitQty()+"','0','"+roll.getFabricsId()+"','"+fabricsReturn.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+fabricsReturn.getUserId()+"');";		
					session.createSQLQuery(sql).executeUpdate();
				}
			}


			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public String editReturnRollInTransaction(FabricsRoll fabricsRoll) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();
			String sql = "select far.autoId,dbo.fabricsBalanceQtyExceptAutoId(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.rollId,far.departmentId,far.autoId) as balanceQty \r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"where far.autoId = '"+fabricsRoll.getAutoId()+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()>0) {
				Object[] element = (Object[]) list.get(0);
				if(Double.valueOf(element[1].toString())>=fabricsRoll.getUnitQty()) {
					sql = "update tbFabricsAccessoriesTransaction set unitQty = '"+fabricsRoll.getUnitQty()+"',qty = '"+fabricsRoll.getUnitQty()+"' where autoId = '"+fabricsRoll.getAutoId()+"'";
					if(session.createSQLQuery(sql).executeUpdate()==1) {
						tx.commit();
						return "Successful";
					}
				}else {
					tx.commit();
					return "Return Qty Exist";
				}
			}			

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}



	@Override
	public String deleteReturnRollFromTransaction(FabricsRoll fabricsRoll) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();

			String sql = "delete from tbFabricsAccessoriesTransaction where autoId = '"+fabricsRoll.getAutoId()+"'";
			if(session.createSQLQuery(sql).executeUpdate()==1) {
				tx.commit();
				return "Successful";
			}

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}



	@Override
	public List<FabricsReturn> getFabricsReturnList() {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<FabricsReturn> datalist=new ArrayList<FabricsReturn>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select fri.AutoId,fri.transactionId,(select convert(varchar,fri.date,103))as returnDate,fri.supplierId,s.name as supplierName,fri.remarks,fri.createBy \r\n" + 
					"from tbFabricsReturnInfo fri\r\n" + 
					"left join tbSupplier s\r\n" + 
					"on fri.supplierId = s.id";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();

				datalist.add(new FabricsReturn(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString()));				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}

	@Override
	public FabricsReturn getFabricsReturnInfo(String returnTransactionId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsReturn fabricsReturn = null;
		FabricsRoll tempRoll;
		List<FabricsRoll> fabricsRollList = new ArrayList<FabricsRoll>();	

		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select far.autoId,far.transactionId,far.purchaseOrder,far.styleId,sc.styleNo,far.styleItemId,id.itemname,far.colorId as itemColorId ,ic.Colorname as itemColorName,cItemId as fabricsId,fi.ItemName as fabricsName,far.itemColorId as fabricsColorId,isnull(fc.Colorname,'') as fabricsColorName,far.rollId,fri.supplierRollId,far.unitId,u.unitname,unitQty,rackName,BinName,far.userId\r\n" + 
					",dbo.fabricsBalanceQty(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.rollId,far.departmentId) as balanceQty \r\n" +
					",(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.dItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.FABRICS_RECEIVE.getType()+"' and t.departmentId = far.departmentId) as previousReceiveQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.FABRICS_RETURN.getType()+"' and t.departmentId = far.departmentId) as returnQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.FABRICS_ISSUE.getType()+"' and t.departmentId = far.departmentId) as issueQty\r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on far.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on far.styleItemId = id.itemid\r\n"
					+ "left join tbfabricsRollInfo fri\r\n" + 
					"on far.rollId = fri.rollId \r\n" + 
					"left join tbunits u\r\n" + 
					"on far.unitId = u.Unitid\r\n" + 
					"left join TbFabricsItem fi\r\n" + 
					"on far.cItemId = fi.id\r\n" + 
					"left join tbColors ic\r\n" + 
					"on far.colorId = ic.ColorId\r\n" + 
					"left join tbColors fc\r\n" + 
					"on far.itemColorId = fc.ColorId\r\n" + 
					"where transactionId = '"+returnTransactionId+"' and transactionType='"+StoreTransaction.FABRICS_RETURN.getType()+"'";	
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempRoll = new FabricsRoll(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(),element[7].toString(), element[8].toString(),element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(), element[15].toString(),element[16].toString(),0.0, Double.valueOf(element[17].toString()), element[18].toString(), element[19].toString(),1);
				tempRoll.setBalanceQty(Double.valueOf(element[21].toString()));
				tempRoll.setPreviousReceiveQty(Double.valueOf(element[22].toString()));
				tempRoll.setReturnQty(Double.valueOf(element[23].toString()));
				tempRoll.setIssueQty(Double.valueOf(element[24].toString()));

				fabricsRollList.add(tempRoll);				
			}
			sql = "select fri.autoId,fri.transactionId,(select convert(varchar,fri.date,103))as date,fri.supplierId,s.name as supplierName,fri.remarks,fri.createBy \r\n" + 
					"from tbFabricsReturnInfo fri\r\n" + 
					"left join tbSupplier s\r\n" + 
					"on fri.supplierId = s.id\r\n" + 
					"where fri.transactionId = '"+returnTransactionId+"'";		
			list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				Object[] element = (Object[]) list.get(0);

				fabricsReturn = new FabricsReturn(element[0].toString(),element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString());
				fabricsReturn.setFabricsRollList(fabricsRollList);
			}

			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return fabricsReturn;
	}

	@Override
	public FabricsReceive getFabricsReceiveInfoForReturn(String transactionId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsReceive fabricsReceive = null;
		List<FabricsRoll> fabricsRollList = new ArrayList<FabricsRoll>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select autoId,isnull(returnTransactionId,'')as returnTransactionId,rollId,supplierRollId,frd.unitId,u.unitname,rollQty,qcPassedQty,rackName,BinName,qcPassedType,isReturn,createBy  \r\n" + 
					"from tbFabricsRollDetails frd\r\n" + 
					"left join tbunits u\r\n" + 
					"on frd.unitId = u.Unitid\r\n" + 
					"where transactionId = '"+transactionId+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				boolean isReturn = element[11].toString().equals("1")?true:false;
				//fabricsRollList.add(new FabricsRoll(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), Double.valueOf(element[6].toString()), Double.valueOf(element[7].toString()), element[8].toString(), element[9].toString(), Integer.valueOf(element[10].toString()), isReturn, element[12].toString()));				
			}

			sql = "select autoId,transactionId,grnNo,(select convert(varchar,grnDate,103))as grnDate,location,fIndent.PurchaseOrder,fIndent.styleId,sc.StyleNo,fIndent.itemid,id.itemname,fIndent.fabricscolor as colorId,c.Colorname,fri.fabricsId,ISNULL(fi.ItemName,'')as fabricsName,indentId,fri.unitId,grnQty,noOfRoll,fri.supplierId,fri.buyer,fri.challanNo,fri.challanDate,fri.remarks,fri.preperedBy,fri.createBy \r\n" + 
					"from tbFabricsReceiveInfo fri\r\n" + 
					"left join tbFabricsIndent fIndent\r\n" + 
					"on fri.indentId = fIndent.id\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on fIndent.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on fIndent.itemid = id.itemid\r\n" + 
					"left join tbColors c\r\n" + 
					"on fIndent.itemcolor = c.ColorId\r\n" + 
					"left join TbFabricsItem fi\r\n" + 
					"on fIndent.fabricsId = fi.id where fri.transactionId = '"+transactionId+"'";		
			list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				Object[] element = (Object[]) list.get(0);
				boolean  isReturn = element[24].toString()=="1"?true:false;
				fabricsReceive = new FabricsReceive();
				//fabricsReceive.setFabricsRollList(fabricsRollList);
			}
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return fabricsReceive;

	}

	@Override
	public List<FabricsRoll> getAvailableFabricsRollListInDepartment(String departmentId) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsRoll tempRoll;
		List<FabricsRoll> datalist=new ArrayList<FabricsRoll>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			
			String sql="select fat.autoId,'supplierId' as supplierId,'supplierName' as supplierName,fat.purchaseOrder,fat.styleId,fat.StyleNo,fat.styleItemId,fat.itemname,fat.colorid,fat.colorName,fat.dItemId as fabricsId,fi.ItemName as fabricsName,fat.itemColorId,ISNULL(ic.Colorname,'') as fabricsColor,fat.rollId,ISNULL(frinfo.supplierRollId,'') as supplierRollId,fi.unitId,u.unitname,dbo.fabricsBalanceQty(fat.purchaseOrder,fat.styleid,fat.styleItemId,fat.colorId,fat.dItemId,fat.ItemcolorId,fat.rollId,fat.departmentId) as balanceQty,fat.rackName,fat.binName,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.dItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = '1' and t.itemColorId = fat.itemColorId  and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder and t.rollId=fat.rollId) as ReceiveQty,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.cItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = '4' and t.itemColorId = fat.itemColorId  and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder and t.rollId=fat.rollId) as IssueQty,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.dItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = '5' and t.itemColorId = fat.itemColorId  and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder and t.rollId=fat.rollId) as previousReturnQty,\n" + 
					"dbo.fabricsBalanceQty(fat.purchaseOrder,fat.styleid,fat.styleItemId,fat.colorId,fat.dItemId,fat.itemColorId,fat.rollId,fat.departmentId) as BalanceQty\n" + 
					"from tbFabricsAccessoriesTransaction as fat\n" + 
					"left join TbFabricsItem fi\n" + 
					"on fat.dItemId = fi.id\n" + 
					"left join tbColors ic\n" + 
					"on fat.itemColorId = ic.ColorId\n" + 
					"left join tbfabricsRollInfo frinfo\n" + 
					"on fat.rollId = frinfo.rollId \n" + 
					"left join tbunits u\n" + 
					"on fi.unitId = u.Unitid\n" + 
					"where fat.transactionType = '"+StoreTransaction.FABRICS_RECEIVE.getType()+"' and fat.departmentId = '"+departmentId+"' and dbo.fabricsBalanceQty(fat.purchaseOrder,fat.styleid,fat.styleItemId,fat.colorId,fat.dItemId,fat.itemColorId,fat.rollId,fat.departmentId) >0\n" + 
					"order by fat.purchaseOrder,fat.styleId,fat.styleItemId,fat.colorId,fat.dItemId,fat.itemColorId\n" + 
					"";
			/*String sql = "select fat.autoId,'supplierId' as supplierId,'supplierName' as supplierName,fat.purchaseOrder,fat.styleId,sc.StyleNo,fat.styleItemId,id.itemname,fat.colorid,c.colorName,fat.dItemId as fabricsId,fi.ItemName as fabricsName,fat.itemColorId,ISNULL(ic.Colorname,'') as fabricsColor,fat.rollId,frinfo.supplierRollId,fi.unitId,u.unitname,dbo.fabricsBalanceQty(fat.purchaseOrder,fat.styleid,fat.styleItemId,fat.colorId,fat.dItemId,fat.ItemcolorId,fat.rollId,fat.departmentId) as balanceQty,fat.rackName,fat.binName,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.dItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = fat.transactionType and t.itemColorId = fat.itemColorId and t.dItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as ReceiveQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.cItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = '"+StoreTransaction.FABRICS_ISSUE.getType()+"' and t.itemColorId = fat.itemColorId and t.cItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as IssueQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.cItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = '"+StoreTransaction.FABRICS_RETURN.getType()+"' and t.itemColorId = fat.itemColorId and t.cItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as previousReturnQty\r\n" + 
					"from tbFabricsAccessoriesTransaction as fat\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on fat.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on fat.styleItemId = id.itemid\r\n" + 
					"left join tbColors c\r\n" + 
					"on fat.colorId = c.ColorId\r\n" + 
					"left join TbFabricsItem fi\r\n" + 
					"on fat.dItemId = fi.id\r\n" + 
					"left join tbColors ic\r\n" + 
					"on fat.itemColorId = ic.ColorId\r\n" + 
					"left join tbfabricsRollInfo frinfo\r\n" + 
					"on fat.rollId = frinfo.rollId \r\n" + 
					"left join tbunits u\r\n" + 
					"on fi.unitId = u.Unitid\r\n" + 
					"where fat.transactionType = '"+StoreTransaction.FABRICS_RECEIVE.getType()+"' and fat.departmentId = '"+departmentId+"' \r\n"+
					"and dbo.fabricsBalanceQty(fat.purchaseOrder,fat.styleid,fat.styleItemId,fat.colorId,fat.dItemId,fat.ItemcolorId,fat.rollId,fat.departmentId) > 0 \r\n"+
					"order by fat.purchaseOrder,fat.styleId,fat.styleItemId,fat.colorId,fat.dItemId,fat.itemColorId";		*/
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempRoll = new FabricsRoll(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(),element[15].toString(),element[16].toString(),element[17].toString(), Double.valueOf(element[18].toString()),element[19].toString(),element[20].toString());
				tempRoll.setPreviousReceiveQty(Double.valueOf(element[21].toString()));
				tempRoll.setIssueQty(Double.valueOf(element[22].toString()));
				tempRoll.setReturnQty(Double.valueOf(element[23].toString()));
				datalist.add(tempRoll);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}

			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}

	@Override
	public boolean submitFabricsIssue(FabricsIssue fabricsIssue) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.FABRICS_ISSUE;
		ItemType itemType = ItemType.FABRICS;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select (isnull(max(transactionId),0)+1) as maxId from tbFabricsIssueInfo";
			List<?> list = session.createSQLQuery(sql).list();
			String transactionId="0";
			if(list.size()>0) {
				transactionId = list.get(0).toString();
			}

			sql="insert into tbFabricsIssueInfo (transactionId"
					+ ",date"
					+ ",issuedTo"
					+ ",receiveBy"
					+ ",remarks"
					+ ",departmentId"
					+ ",status"
					+ ",entryTime"
					+ ",createBy) values("
					+ "'"+transactionId+"'"
					+ ",'"+fabricsIssue.getIssueDate()+"'"
					+ ",'"+fabricsIssue.getIssuedTo()+"'"
					+ ",'"+fabricsIssue.getReceiveBy()+"'"
					+ ",'"+fabricsIssue.getRemarks()+"'"
					+ ",'"+fabricsIssue.getDepartmentId()+"'"
					+ ",'1',"
					+ "CURRENT_TIMESTAMP,'"+fabricsIssue.getUserId()+"');";
			session.createSQLQuery(sql).executeUpdate();

			for (FabricsRoll roll : fabricsIssue.getFabricsRollList()) {
				sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleNo,styleItemId,itemName,colorId,colorName,itemColorId,transactionId,transactionType,itemType,rollId,unitId,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
						"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getStyleNo()+"','"+roll.getItemId()+"','"+roll.getItemName()+"','"+roll.getItemColorId()+"','"+roll.getItemColor()+"','"+roll.getFabricsColorId()+"','"+transactionId+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getRollId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','0','"+roll.getFabricsId()+"','"+fabricsIssue.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+fabricsIssue.getUserId()+"');";		
				session.createSQLQuery(sql).executeUpdate();
			}

			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public boolean editFabricsIssue(FabricsIssue fabricsIssue) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.FABRICS_ISSUE;
		ItemType itemType = ItemType.FABRICS;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="update tbfabricsIssueInfo set "
					+ "date = '"+fabricsIssue.getIssueDate()+"'"
					+ ",issuedTo = '"+fabricsIssue.getIssuedTo()+"'"
					+ ",receiveBy = '"+fabricsIssue.getReceiveBy()+"'"
					+ ",remarks = '"+fabricsIssue.getRemarks()+"'"
					+ ",entryTime = CURRENT_TIMESTAMP"
					+ ",createBy = '"+fabricsIssue.getUserId()+"' where transactionId='"+fabricsIssue.getTransactionId()+"';";

			session.createSQLQuery(sql).executeUpdate();


			if(fabricsIssue.getFabricsRollList() != null) {
				for (FabricsRoll roll : fabricsIssue.getFabricsRollList()) {
					sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,rollId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
							"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getItemId()+"','"+roll.getItemColorId()+"','"+roll.getFabricsColorId()+"','"+fabricsIssue.getTransactionId()+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getRollId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getUnitQty()+"','0','"+roll.getFabricsId()+"','"+fabricsIssue.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+fabricsIssue.getUserId()+"');";		
					session.createSQLQuery(sql).executeUpdate();
				}
			}


			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public String editIssuedRollInTransaction(FabricsRoll fabricsRoll) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();
			String sql = "select far.autoId,dbo.fabricsBalanceQtyExceptAutoId(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.rollId,far.departmentId,far.autoId) as balanceQty \r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"where far.autoId = '"+fabricsRoll.getAutoId()+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()>0) {
				Object[] element = (Object[]) list.get(0);
				if(Double.valueOf(element[1].toString())>=fabricsRoll.getUnitQty()) {
					sql = "update tbFabricsAccessoriesTransaction set unitQty = '"+fabricsRoll.getUnitQty()+"',qty = '"+fabricsRoll.getUnitQty()+"' where autoId = '"+fabricsRoll.getAutoId()+"'";
					if(session.createSQLQuery(sql).executeUpdate()==1) {
						tx.commit();
						return "Successful";
					}
				}else {
					tx.commit();
					return "Return Qty Exist";
				}
			}			

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}

	@Override
	public String deleteIssuedRollFromTransaction(FabricsRoll fabricsRoll) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();

			String sql = "delete from tbFabricsAccessoriesTransaction where autoId = '"+fabricsRoll.getAutoId()+"'";
			if(session.createSQLQuery(sql).executeUpdate()==1) {
				tx.commit();
				return "Successful";
			}

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}
	@Override
	public List<FabricsRoll> getIssuedFabricsRollListInDepartment(String departmentId, String returnDepartmentId) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsRoll tempRoll;
		List<FabricsRoll> datalist=new ArrayList<FabricsRoll>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select sil.*,\r\n" + 
					"isnull(dbo.fabricsIssueReturnedQty('"+departmentId+"','"+returnDepartmentId+"',sil.rollId),0) as previousIssueReturnQty\r\n" + 
					"from dbo.issuedItemList('"+departmentId+"','"+returnDepartmentId+"') as sil\r\n"
					+ "where (sil.issuedQty - isnull(dbo.fabricsIssueReturnedQty('"+departmentId+"','"+returnDepartmentId+"',sil.rollId),0))>0";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempRoll = new FabricsRoll(element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(), element[15].toString(), element[16].toString(), Double.valueOf(element[17].toString()), 0, Double.valueOf(element[18].toString()));			
				datalist.add(tempRoll);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}



	@Override
	public List<FabricsIssue> getFabricsIssueList() {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsIssue tempIssue;
		List<FabricsIssue> datalist=new ArrayList<FabricsIssue>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select fii.AutoId,fii.transactionId,(select convert(varchar,fii.date,103))as issuedDate,fii.issuedTo,fii.receiveBy,fii.remarks,fii.createBy,di.DepartmentName,fi.FactoryName\r\n" + 
					"from tbFabricsIssueInfo fii\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on fii.issuedTo = di.DepartmentId\r\n" + 
					"left join TbFactoryInfo fi\r\n" + 
					"on di.FactoryId = fi.FactoryId";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempIssue = new FabricsIssue(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString());
				tempIssue.setIssuedDepartmentName(element[7].toString()+"("+element[8].toString()+")");
				datalist.add(tempIssue);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}
	@Override
	public FabricsIssue getFabricsIssueInfo(String issueTransactionId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsIssue fabricsIssue = null;
		FabricsRoll tempRoll;
		List<FabricsRoll> fabricsRollList = new ArrayList<FabricsRoll>();	

		try{	
			tx=session.getTransaction();
			tx.begin();		
			
			String sql="select far.autoId,far.transactionId,far.purchaseOrder,far.styleId,far.styleNo,far.styleItemId,far.itemname,far.colorId as itemColorId ,far.Colorname as itemColorName,cItemId as fabricsId,fi.ItemName as fabricsName,far.itemColorId as fabricsColorId,isnull(ic.colorName,'') as fabricsColorName,far.rollId,ISNULL(fri.supplierRollId,'') as supplierRollId,far.unitId,u.unitname,far.Qty,rackName,BinName,far.userId\n" + 
					",dbo.fabricsBalanceQty(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.rollId,far.departmentId) as balanceQty \n" + 
					",(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.dItemId and far.itemColorId = t.itemColorId and t.transactionType = '1' and t.departmentId = far.departmentId) as previousReceiveQty,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '3' and t.departmentId = far.departmentId) as returnQty,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '4' and t.departmentId = far.departmentId) as issueQty\n" + 
					"from tbFabricsAccessoriesTransaction far\n" + 
					"\n" + 
					"left join tbfabricsRollInfo fri\n" + 
					"on far.rollId = fri.rollId \n" + 
					"left join tbunits u\n" + 
					"on far.unitId = u.Unitid\n" + 
					"left join TbFabricsItem fi\n" + 
					"on far.cItemId = fi.id\n" + 
					"left join tbColors ic\n" + 
					"on far.itemColorId = ic.ColorId\n" + 
					"where transactionId = '"+issueTransactionId+"' and transactionType='"+StoreTransaction.FABRICS_ISSUE.getType()+"'";
			
/*			String sql = "select far.autoId,far.transactionId,far.purchaseOrder,far.styleId,sc.styleNo,far.styleItemId,id.itemname,far.colorId as itemColorId ,ic.Colorname as itemColorName,cItemId as fabricsId,fi.ItemName as fabricsName,far.itemColorId as fabricsColorId,isnull(fc.Colorname,'') as fabricsColorName,far.rollId,fri.supplierRollId,far.unitId,u.unitname,unitQty,rackName,BinName,far.userId\r\n" + 
					",dbo.fabricsBalanceQty(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.rollId,far.departmentId) as balanceQty \r\n" +
					",(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.dItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.FABRICS_RECEIVE.getType()+"' and t.departmentId = far.departmentId) as previousReceiveQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.FABRICS_RETURN.getType()+"' and t.departmentId = far.departmentId) as returnQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.FABRICS_ISSUE.getType()+"' and t.departmentId = far.departmentId) as issueQty\r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on far.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on far.styleItemId = id.itemid\r\n"
					+ "left join tbfabricsRollInfo fri\r\n" + 
					"on far.rollId = fri.rollId \r\n" + 
					"left join tbunits u\r\n" + 
					"on far.unitId = u.Unitid\r\n" + 
					"left join TbFabricsItem fi\r\n" + 
					"on far.cItemId = fi.id\r\n" + 
					"left join tbColors ic\r\n" + 
					"on far.colorId = ic.ColorId\r\n" + 
					"left join tbColors fc\r\n" + 
					"on far.itemColorId = fc.ColorId\r\n" + 
					"where transactionId = '"+issueTransactionId+"' and transactionType='"+StoreTransaction.FABRICS_ISSUE.getType()+"'";*/	
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempRoll = new FabricsRoll(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(),element[7].toString(), element[8].toString(),element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(), element[15].toString(),element[16].toString(),0.0, Double.valueOf(element[17].toString()), element[18].toString(), element[19].toString(),1);
				tempRoll.setBalanceQty(Double.valueOf(element[21].toString()));
				tempRoll.setPreviousReceiveQty(Double.valueOf(element[22].toString()));
				tempRoll.setReturnQty(Double.valueOf(element[23].toString()));
				tempRoll.setIssueQty(Double.valueOf(element[24].toString()));

				fabricsRollList.add(tempRoll);				
			}
			sql = "select fii.AutoId,fii.transactionId,(select convert(varchar,fii.date,103))as issuedDate,fii.issuedTo,fii.receiveBy,fii.remarks,fii.createBy,di.DepartmentName,fi.FactoryName\r\n" + 
					"from tbFabricsIssueInfo fii\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on fii.issuedTo = di.DepartmentId\r\n" + 
					"left join TbFactoryInfo fi\r\n" + 
					"on di.FactoryId = fi.FactoryId\r\n" + 
					"where fii.transactionId = '"+issueTransactionId+"'\r\n" + 
					"";		
			list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				Object[] element = (Object[]) list.get(0);

				fabricsIssue = new FabricsIssue(element[0].toString(), element[1].toString(), element[2].toString(),  element[3].toString(),  element[4].toString(),  element[5].toString(), element[6].toString());
				fabricsIssue.setFabricsRollList(fabricsRollList);
			}

			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return fabricsIssue;
	}



	@Override
	public boolean submitFabricsIssueReturn(FabricsIssueReturn fabricsIssueReturn) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.FABRICS_ISSUE_RETURN;
		ItemType itemType = ItemType.FABRICS;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select (isnull(max(transactionId),0)+1) as maxId from tbFabricsIssueReturnInfo";
			List<?> list = session.createSQLQuery(sql).list();
			String transactionId="0";
			if(list.size()>0) {
				transactionId = list.get(0).toString();
			}

			sql="insert into tbFabricsIssueReturnInfo (transactionId"
					+ ",date"
					+ ",issueReturnFrom"
					+ ",receiveFrom"
					+ ",remarks"
					+ ",departmentId"
					+ ",entryTime"
					+ ",createBy) values("
					+ "'"+transactionId+"'"
					+ ",'"+fabricsIssueReturn.getIssueReturnDate()+"'"
					+ ",'"+fabricsIssueReturn.getIssueReturnFrom()+"'"
					+ ",'"+fabricsIssueReturn.getReceiveFrom()+"'"
					+ ",'"+fabricsIssueReturn.getRemarks()+"'"
					+ ",'"+fabricsIssueReturn.getDepartmentId()+"',"
					+ "CURRENT_TIMESTAMP,'"+fabricsIssueReturn.getUserId()+"');";
			session.createSQLQuery(sql).executeUpdate();

			for (FabricsRoll roll : fabricsIssueReturn.getFabricsRollList()) {
				sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleNo,styleItemId,ItemName,colorId,ColorName,itemColorId,transactionId,transactionType,itemType,rollId,unitId,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
						"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getStyleNo()+"','"+roll.getItemId()+"','"+roll.getItemName()+"','"+roll.getItemColorId()+"','"+roll.getItemColor()+"','"+roll.getFabricsColorId()+"','"+transactionId+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getRollId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getFabricsId()+"','0','"+fabricsIssueReturn.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+fabricsIssueReturn.getUserId()+"');";		
				session.createSQLQuery(sql).executeUpdate();
			}

			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public boolean editFabricsIssueReturn(FabricsIssueReturn fabricsIssueReturn) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.FABRICS_ISSUE_RETURN;
		ItemType itemType = ItemType.FABRICS;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="update tbfabricsIssueReturnInfo set "
					+ "date = '"+fabricsIssueReturn.getIssueReturnDate()+"'"
					+ ",issueReturnFrom = '"+fabricsIssueReturn.getIssueReturnFrom()+"'"
					+ ",receiveFrom = '"+fabricsIssueReturn.getReceiveFrom()+"'"
					+ ",remarks = '"+fabricsIssueReturn.getRemarks()+"'"
					+ ",entryTime = CURRENT_TIMESTAMP"
					+ ",createBy = '"+fabricsIssueReturn.getUserId()+"' where transactionId='"+fabricsIssueReturn.getTransactionId()+"';";

			session.createSQLQuery(sql).executeUpdate();


			if(fabricsIssueReturn.getFabricsRollList() != null) {
				for (FabricsRoll roll : fabricsIssueReturn.getFabricsRollList()) {
					sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,rollId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
							"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getItemId()+"','"+roll.getItemColorId()+"','"+roll.getFabricsColorId()+"','"+fabricsIssueReturn.getTransactionId()+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getRollId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getUnitQty()+"','"+roll.getFabricsId()+"','0','"+fabricsIssueReturn.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+fabricsIssueReturn.getUserId()+"');";		
					session.createSQLQuery(sql).executeUpdate();
				}
			}


			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public String editIssueReturndRollInTransaction(FabricsRoll fabricsRoll) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();
			String sql = "select fat.transactionId,dbo.fabricsIssuedQty(firi.departmentId,firi.issueReturnFrom,fat.rollId) as issuedQty,dbo.fabricsIssueReturnedQty(firi.departmentId,firi.issueReturnFrom,fat.rollId) as returnedQty from tbFabricsAccessoriesTransaction fat\r\n" + 
					"left join tbfabricsIssueReturnInfo firi\r\n" + 
					"on fat.transactionId = firi.transactionId where fat.autoId='"+fabricsRoll.getAutoId()+"' and (dbo.fabricsIssuedQty(firi.departmentId,firi.issueReturnFrom,fat.rollId)-dbo.fabricsIssueReturnedQty(firi.departmentId,firi.issueReturnFrom,fat.rollId)-"+fabricsRoll.getUnitQty()+")>=0";		
			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()>0) {
				Object[] element = (Object[]) list.get(0);
				if(Double.valueOf(element[1].toString())>=fabricsRoll.getUnitQty()) {
					sql = "update tbFabricsAccessoriesTransaction set unitQty = '"+fabricsRoll.getUnitQty()+"',qty = '"+fabricsRoll.getUnitQty()+"',rackName='"+fabricsRoll.getRackName()+"',binName='"+fabricsRoll.getBinName()+"' where autoId = '"+fabricsRoll.getAutoId()+"'";
					if(session.createSQLQuery(sql).executeUpdate()==1) {
						tx.commit();
						return "Successful";
					}
				}else {
					tx.commit();
					return "Return Qty Exist";
				}
			}			

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}

	@Override
	public String deleteIssueReturndRollFromTransaction(FabricsRoll fabricsRoll) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();

			String sql = "delete from tbFabricsAccessoriesTransaction where autoId = '"+fabricsRoll.getAutoId()+"'";
			if(session.createSQLQuery(sql).executeUpdate()==1) {
				tx.commit();
				return "Successful";
			}

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}

	@Override
	public List<FabricsIssueReturn> getFabricsIssueReturnList() {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsIssueReturn tempIssueReturn;
		List<FabricsIssueReturn> datalist=new ArrayList<FabricsIssueReturn>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select firi.AutoId,firi.transactionId,(select convert(varchar,firi.date,103))as issuedDate,firi.issueReturnFrom,firi.receiveFrom,firi.remarks,firi.createBy,di.DepartmentName,fi.FactoryName\r\n" + 
					"from tbFabricsIssueReturnInfo firi\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on firi.issueReturnFrom = di.DepartmentId\r\n" + 
					"left join TbFactoryInfo fi\r\n" + 
					"on di.FactoryId = fi.FactoryId";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempIssueReturn = new FabricsIssueReturn(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString());
				tempIssueReturn.setIssueReturnDepartmentName(element[7].toString()+"("+element[8].toString()+")");
				datalist.add(tempIssueReturn);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}

	@Override
	public FabricsIssueReturn getFabricsIssueReturnInfo(String issueReturnTransectionId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsIssueReturn fabricsIssueReturn = null;
		FabricsRoll tempRoll;
		List<FabricsRoll> fabricsRollList = new ArrayList<FabricsRoll>();	

		try{	
			tx=session.getTransaction();
			tx.begin();		
			
			String sql="select far.autoId,far.transactionId,far.purchaseOrder,far.styleId,ISNULL(far.styleNo,'') as styleNo,far.styleItemId,ISNULL(far.itemname,'') as itemname,far.colorId as itemColorId ,ISNULL(far.Colorname,'') as itemColorName,dItemId as fabricsId,fi.ItemName as fabricsName,far.itemColorId as fabricsColorId,isnull(fc.Colorname,'') as fabricsColorName,far.rollId,ISNULL(fri.supplierRollId,'') as supplierRollId,far.unitId,u.unitname,far.Qty,rackName,BinName,far.userId,\n" + 
					"dbo.fabricsissuedQty(firi.departmentId,firi.issueReturnFrom,far.rollId) as issuedQty,\n" + 
					"dbo.fabricsIssueReturnedQty(firi.departmentId,firi.issueReturnFrom,far.rollId) as returnedQty\n" + 
					"from tbFabricsAccessoriesTransaction far\n" + 
					"left join tbFabricsIssueReturnInfo firi\n" + 
					"on far.transactionId = firi.transactionId\n" + 
					"left join tbfabricsRollInfo fri\n" + 
					"on far.rollId = fri.rollId \n" + 
					"left join tbunits u\n" + 
					"on far.unitId = u.Unitid\n" + 
					"left join TbFabricsItem fi\n" + 
					"on far.dItemId = fi.id\n" + 
					"left join tbColors fc\n" + 
					"on far.itemColorId = fc.ColorId\n" + 
					"where far.transactionId = '"+issueReturnTransectionId+"' and far.transactionType='"+StoreTransaction.FABRICS_ISSUE_RETURN.getType()+"'";
			
			/*String sql = "select far.autoId,far.transactionId,far.purchaseOrder,far.styleId,sc.styleNo,far.styleItemId,id.itemname,far.colorId as itemColorId ,ic.Colorname as itemColorName,dItemId as fabricsId,fi.ItemName as fabricsName,far.itemColorId as fabricsColorId,isnull(fc.Colorname,'') as fabricsColorName,far.rollId,fri.supplierRollId,far.unitId,u.unitname,far.Qty,rackName,BinName,far.userId,\r\n" + 
					"dbo.fabricsissuedQty(firi.departmentId,firi.issueReturnFrom,far.rollId) as issuedQty,\r\n" + 
					"dbo.fabricsIssueReturnedQty(firi.departmentId,firi.issueReturnFrom,far.rollId) as returnedQty\r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"left join tbFabricsIssueReturnInfo firi\r\n" + 
					"on far.transactionId = firi.transactionId\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on far.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on far.styleItemId = id.itemid\r\n" + 
					"left join tbfabricsRollInfo fri\r\n" + 
					"on far.rollId = fri.rollId \r\n" + 
					"left join tbunits u\r\n" + 
					"on far.unitId = u.Unitid\r\n" + 
					"left join TbFabricsItem fi\r\n" + 
					"on far.dItemId = fi.id\r\n" + 
					"left join tbColors ic\r\n" + 
					"on far.colorId = ic.ColorId\r\n" + 
					"left join tbColors fc\r\n" + 
					"on far.itemColorId = fc.ColorId\r\n" + 
					"where far.transactionId = '"+issueReturnTransectionId+"' and far.transactionType='"+StoreTransaction.FABRICS_ISSUE_RETURN.getType()+"'";	*/
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempRoll = new FabricsRoll(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(),element[7].toString(), element[8].toString(),element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(), element[15].toString(),element[16].toString(),0.0, Double.valueOf(element[17].toString()), element[18].toString(), element[19].toString(),1);
				tempRoll.setIssueQty(Double.valueOf(element[21].toString()));
				tempRoll.setPreviousReturnQty(Double.valueOf(element[22].toString()));
				fabricsRollList.add(tempRoll);				
			}
			sql = "select fii.AutoId,fii.transactionId,(select convert(varchar,fii.date,103))as issuedDate,fii.issueReturnFrom,fii.receiveFrom,fii.remarks,fii.createBy,di.DepartmentName,fi.FactoryName\r\n" + 
					"from tbFabricsIssueReturnInfo fii\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on fii.issueReturnFrom = di.DepartmentId\r\n" + 
					"left join TbFactoryInfo fi\r\n" + 
					"on di.FactoryId = fi.FactoryId\r\n" + 
					"where fii.transactionId = '"+issueReturnTransectionId+"'\r\n" + 
					"";		
			list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				Object[] element = (Object[]) list.get(0);

				fabricsIssueReturn = new FabricsIssueReturn(element[0].toString(), element[1].toString(), element[2].toString(),  element[3].toString(),  element[4].toString(),  element[5].toString(), element[6].toString());
				fabricsIssueReturn.setFabricsRollList(fabricsRollList);
			}
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return fabricsIssueReturn;
	}

	@Override
	public boolean submitFabricsTransferOut(FabricsTransferOut fabricsTransferOut) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.FABRICS_TRANSFER_OUT;
		ItemType itemType = ItemType.FABRICS;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select (isnull(max(transactionId),0)+1) as maxId from tbFabricsTransferOutInfo";
			List<?> list = session.createSQLQuery(sql).list();
			String transactionId="0";
			if(list.size()>0) {
				transactionId = list.get(0).toString();
			}

			sql="insert into tbFabricsTransferOutInfo (transactionId"
					+ ",date"
					+ ",transferTo"
					+ ",receiveBy"
					+ ",remarks"
					+ ",departmentId"
					+ ",entryTime"
					+ ",createBy) values("
					+ "'"+transactionId+"'"
					+ ",'"+fabricsTransferOut.getTransferDate()+"'"
					+ ",'"+fabricsTransferOut.getTransferTo()+"'"
					+ ",'"+fabricsTransferOut.getReceiveBy()+"'"
					+ ",'"+fabricsTransferOut.getRemarks()+"'"
					+ ",'"+fabricsTransferOut.getDepartmentId()+"',"
					+ "CURRENT_TIMESTAMP,'"+fabricsTransferOut.getUserId()+"');";
			session.createSQLQuery(sql).executeUpdate();

			for (FabricsRoll roll : fabricsTransferOut.getFabricsRollList()) {
				
				System.out.println(" rac"+roll.getRackName());
				sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleNo,styleItemId,itemName,colorId,colorName,itemColorId,transactionId,transactionType,itemType,rollId,unitId,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
						"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getStyleNo()+"','"+roll.getItemId()+"','"+roll.getItemName()+"','"+roll.getItemColorId()+"','"+roll.getItemColor()+"','"+roll.getFabricsColorId()+"','"+transactionId+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getRollId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','0','"+roll.getFabricsId()+"','"+fabricsTransferOut.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+fabricsTransferOut.getUserId()+"');";		
				session.createSQLQuery(sql).executeUpdate();
			}

			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public boolean editFabricsTransferOut(FabricsTransferOut fabricsTransferOut) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.FABRICS_TRANSFER_OUT;
		ItemType itemType = ItemType.FABRICS;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="update tbfabricsTransferOutInfo set "
					+ "date = '"+fabricsTransferOut.getTransferDate()+"'"
					+ ",transferTo = '"+fabricsTransferOut.getTransferTo()+"'"
					+ ",receiveBy = '"+fabricsTransferOut.getReceiveBy()+"'"
					+ ",remarks = '"+fabricsTransferOut.getRemarks()+"'"
					+ ",entryTime = CURRENT_TIMESTAMP"
					+ ",createBy = '"+fabricsTransferOut.getUserId()+"' where transactionId='"+fabricsTransferOut.getTransactionId()+"';";

			session.createSQLQuery(sql).executeUpdate();

			if(fabricsTransferOut.getFabricsRollList() != null) {
				for (FabricsRoll roll : fabricsTransferOut.getFabricsRollList()) {
					sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,rollId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
							"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getItemId()+"','"+roll.getItemColorId()+"','"+roll.getFabricsColorId()+"','"+fabricsTransferOut.getTransactionId()+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getRollId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getUnitQty()+"','0','"+roll.getFabricsId()+"','"+fabricsTransferOut.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+fabricsTransferOut.getUserId()+"');";		
					session.createSQLQuery(sql).executeUpdate();
				}
			}


			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public String editTransferOutdRollInTransaction(FabricsRoll fabricsRoll) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();
			String sql = "select far.autoId,dbo.fabricsBalanceQtyExceptAutoId(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.rollId,far.departmentId,far.autoId) as balanceQty \r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"where far.autoId = '"+fabricsRoll.getAutoId()+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()>0) {
				Object[] element = (Object[]) list.get(0);
				if(Double.valueOf(element[1].toString())>=fabricsRoll.getUnitQty()) {
					sql = "update tbFabricsAccessoriesTransaction set unitQty = '"+fabricsRoll.getUnitQty()+"',qty = '"+fabricsRoll.getUnitQty()+"' where autoId = '"+fabricsRoll.getAutoId()+"'";
					if(session.createSQLQuery(sql).executeUpdate()==1) {
						tx.commit();
						return "Successful";
					}
				}else {
					tx.commit();
					return "Return Qty Exist";
				}
			}			

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}

	@Override
	public String deleteTransferOutdRollFromTransaction(FabricsRoll fabricsRoll) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();

			String sql = "delete from tbFabricsAccessoriesTransaction where autoId = '"+fabricsRoll.getAutoId()+"'";
			if(session.createSQLQuery(sql).executeUpdate()==1) {
				tx.commit();
				return "Successful";
			}

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}

	@Override
	public List<FabricsTransferOut> getFabricsTransferOutList() {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsTransferOut tempIssue;
		List<FabricsTransferOut> datalist=new ArrayList<FabricsTransferOut>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select fii.AutoId,fii.transactionId,(select convert(varchar,fii.date,103))as issuedDate,fii.transferTo,fii.receiveBy,fii.remarks,fii.createBy,di.DepartmentName,fi.FactoryName\r\n" + 
					"from tbFabricsTransferOutInfo fii\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on fii.transferTo = di.DepartmentId\r\n" + 
					"left join TbFactoryInfo fi\r\n" + 
					"on di.FactoryId = fi.FactoryId";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempIssue = new FabricsTransferOut(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString());
				tempIssue.setTransferDepartmentName(element[7].toString()+"("+element[8].toString()+")");
				datalist.add(tempIssue);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}

	@Override
	public FabricsTransferOut getFabricsTransferOutInfo(String transferOutTransectionId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsTransferOut fabricsTransfer = null;
		FabricsRoll tempRoll;
		List<FabricsRoll> fabricsRollList = new ArrayList<FabricsRoll>();	

		try{	
			tx=session.getTransaction();
			tx.begin();	
			
			String sql=" select far.autoId,far.transactionId,far.purchaseOrder,far.styleId,ISNULL(far.styleNo,'') as styleNo,far.styleItemId,ISNULL(far.itemname,'') as itemname,far.colorId as itemColorId ,ISNULL(far.Colorname,'') as itemColorName,cItemId as fabricsId,fi.ItemName as fabricsName,far.itemColorId as fabricsColorId,isnull(fc.Colorname,'') as fabricsColorName,far.rollId,ISNULL(fri.supplierRollId,'') as supplierRollId ,far.unitId,u.unitname,far.qty,rackName,BinName,far.userId\n" + 
					",dbo.fabricsBalanceQty(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.rollId,far.departmentId) as balanceQty \n" + 
					",(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.dItemId and far.itemColorId = t.itemColorId and t.transactionType = '1' and t.departmentId = far.departmentId) as previousReceiveQty,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '3' and t.departmentId = far.departmentId) as returnQty,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '4' and t.departmentId = far.departmentId) as issueQty\n" + 
					"from tbFabricsAccessoriesTransaction far\n" + 
					"left join tbfabricsRollInfo fri\n" + 
					"on far.rollId = fri.rollId \n" + 
					"left join tbunits u\n" + 
					"on far.unitId = u.Unitid\n" + 
					"left join TbFabricsItem fi\n" + 
					"on far.cItemId = fi.id\n" + 
					"left join tbColors fc\n" + 
					"on far.itemColorId = fc.ColorId\n" + 
					"where transactionId = '"+transferOutTransectionId+"' and transactionType='"+StoreTransaction.FABRICS_TRANSFER_OUT.getType()+"'";
			
			/*String sql = "select far.autoId,far.transactionId,far.purchaseOrder,far.styleId,sc.styleNo,far.styleItemId,id.itemname,far.colorId as itemColorId ,ic.Colorname as itemColorName,cItemId as fabricsId,fi.ItemName as fabricsName,far.itemColorId as fabricsColorId,isnull(fc.Colorname,'') as fabricsColorName,far.rollId,fri.supplierRollId,far.unitId,u.unitname,far.qty,rackName,BinName,far.userId\r\n" + 
					",dbo.fabricsBalanceQty(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.rollId,far.departmentId) as balanceQty \r\n" +
					",(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.dItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.FABRICS_RECEIVE.getType()+"' and t.departmentId = far.departmentId) as previousReceiveQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.FABRICS_RETURN.getType()+"' and t.departmentId = far.departmentId) as returnQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.FABRICS_ISSUE.getType()+"' and t.departmentId = far.departmentId) as issueQty\r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on far.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on far.styleItemId = id.itemid\r\n"
					+ "left join tbfabricsRollInfo fri\r\n" + 
					"on far.rollId = fri.rollId \r\n" + 
					"left join tbunits u\r\n" + 
					"on far.unitId = u.Unitid\r\n" + 
					"left join TbFabricsItem fi\r\n" + 
					"on far.cItemId = fi.id\r\n" + 
					"left join tbColors ic\r\n" + 
					"on far.colorId = ic.ColorId\r\n" + 
					"left join tbColors fc\r\n" + 
					"on far.itemColorId = fc.ColorId\r\n" + 
					"where transactionId = '"+transferOutTransectionId+"' and transactionType='"+StoreTransaction.FABRICS_TRANSFER_OUT.getType()+"'"*/;	
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempRoll = new FabricsRoll(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(),element[7].toString(), element[8].toString(),element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(), element[15].toString(),element[16].toString(),0.0, Double.valueOf(element[17].toString()), element[18].toString(), element[19].toString(),1);
				tempRoll.setBalanceQty(Double.valueOf(element[21].toString()));
				tempRoll.setPreviousReceiveQty(Double.valueOf(element[22].toString()));
				tempRoll.setReturnQty(Double.valueOf(element[23].toString()));
				tempRoll.setIssueQty(Double.valueOf(element[24].toString()));

				fabricsRollList.add(tempRoll);				
			}
			sql = "select fii.AutoId,fii.transactionId,(select convert(varchar,fii.date,103))as issuedDate,fii.transferTo,fii.receiveBy,fii.remarks,fii.createBy,di.DepartmentName,fi.FactoryName\r\n" + 
					"from tbFabricsTransferOutInfo fii\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on fii.transferTo = di.DepartmentId\r\n" + 
					"left join TbFactoryInfo fi\r\n" + 
					"on di.FactoryId = fi.FactoryId\r\n" + 
					"where fii.transactionId = '"+transferOutTransectionId+"'\r\n" + 
					"";		
			list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				Object[] element = (Object[]) list.get(0);

				fabricsTransfer = new FabricsTransferOut(element[0].toString(), element[1].toString(), element[2].toString(),  element[3].toString(),  element[4].toString(),  element[5].toString(), element[6].toString());
				fabricsTransfer.setFabricsRollList(fabricsRollList);
			}

			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return fabricsTransfer;
	}

	@Override
	public List<FabricsRoll> getTransferInFabricsRollList(String departmentId, String transferDepartemntId) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsRoll tempRoll;
		List<FabricsRoll> datalist=new ArrayList<FabricsRoll>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			
			String sql="select ftoi.transferTo,di.DepartmentName,fat.purchaseOrder,fat.styleId,ISNULL(fat.styleNo,'') as styleNo,fat.styleItemId,ISNULL(fat.itemname,'') as itemname,fat.colorId as itemColorId ,ISNULL(fat.Colorname,'') as itemColorName,fat.cItemId as fabricsId,fi.ItemName as fabricsName,fat.itemColorId as fabricsColorId,isnull(fc.Colorname,'') as fabricsColorName,fat.rollId,ISNULL(fri.supplierRollId,'') as supplierRollId,fat.unitId,u.unitname,fat.Qty,\n" + 
					"ISNULL((select sum(t.qty) from tbFabricsAccessoriesTransaction t where t.purchaseOrder=fat.purchaseOrder and t.styleId=fat.styleId and t.styleItemId=fat.styleItemId and t.colorId=fat.colorId and t.dItemId=fat.cItemId and t.transactionType='6'),0) as TransferInQty\n" + 
					"from tbFabricsTransferOutInfo ftoi\n" + 
					"left join tbFabricsAccessoriesTransaction fat\n" + 
					"on ftoi.transactionId = fat.transactionId and fat.transactionType = '7'\n" + 
					"left join TbDepartmentInfo di\n" + 
					"on ftoi.transferTo = di.DepartmentId\n" + 
					"left join tbfabricsRollInfo fri\n" + 
					"on fat.rollId = fri.rollId \n" + 
					"left join tbunits u\n" + 
					"on fat.unitId = u.Unitid\n" + 
					"left join TbFabricsItem fi\n" + 
					"on fat.cItemId = fi.id\n" + 
					"left join tbColors fc\n" + 
					"on fat.itemColorId = fc.ColorId\n" + 
					" where ftoi.transferTo = '"+departmentId+"' and ftoi.departmentId = '"+transferDepartemntId+"'";
			
/*			String sql = "select ftoi.transferTo,di.DepartmentName,fat.purchaseOrder,fat.styleId,ISNULL(fat.styleNo,'') as styleNo,fat.styleItemId,ISNULL(fat.itemname,'') as itemname,fat.colorId as itemColorId ,ISNULL(fat.Colorname,'') as itemColorName,fat.cItemId as fabricsId,fi.ItemName as fabricsName,fat.itemColorId as fabricsColorId,isnull(fc.Colorname,'') as fabricsColorName,fat.rollId,ISNULL(fri.supplierRollId,'') as supplierRollId,fat.unitId,u.unitname,fat.Qty,fat.rackName,fat.BinName,fat.userId\n" + 
					"from tbFabricsTransferOutInfo ftoi\n" + 
					"left join tbFabricsAccessoriesTransaction fat\n" + 
					"on ftoi.transactionId = fat.transactionId and fat.transactionType = '7'\n" + 
					"left join TbDepartmentInfo di\n" + 
					"on ftoi.transferTo = di.DepartmentId\n" + 
					"left join tbfabricsRollInfo fri\n" + 
					"on fat.rollId = fri.rollId \n" + 
					"left join tbunits u\n" + 
					"on fat.unitId = u.Unitid\n" + 
					"left join TbFabricsItem fi\n" + 
					"on fat.cItemId = fi.id\n" + 
					"left join tbColors fc\n" + 
					"on fat.itemColorId = fc.ColorId\n" + 
					" where ftoi.transferTo = '"+departmentId+"' and ftoi.departmentId = '"+transferDepartemntId+"'";	*/	
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempRoll = new FabricsRoll(element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(), element[15].toString(), element[16].toString(), Double.valueOf(element[17].toString()), Double.valueOf(element[18].toString()), 0.0);			
				tempRoll.setRackName("1");
				tempRoll.setBinName("1");
				datalist.add(tempRoll);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}


	@Override
	public boolean submitFabricsTransferIn(FabricsTransferIn fabricsTransferIn) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.FABRICS_TRANSFER_IN;
		ItemType itemType = ItemType.FABRICS;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select (isnull(max(transactionId),0)+1) as maxId from tbFabricsTransferInInfo";
			List<?> list = session.createSQLQuery(sql).list();
			String transactionId="0";
			if(list.size()>0) {
				transactionId = list.get(0).toString();
			}

			sql="insert into tbFabricsTransferInInfo (transactionId"
					+ ",date"
					+ ",transferFrom"
					+ ",receiveFrom"
					+ ",remarks"
					+ ",departmentId"
					+ ",entryTime"
					+ ",createBy) values("
					+ "'"+transactionId+"'"
					+ ",'"+fabricsTransferIn.getTransferDate()+"'"
					+ ",'"+fabricsTransferIn.getTransferFrom()+"'"
					+ ",'"+fabricsTransferIn.getReceiveFrom()+"'"
					+ ",'"+fabricsTransferIn.getRemarks()+"'"
					+ ",'"+fabricsTransferIn.getDepartmentId()+"',"
					+ "CURRENT_TIMESTAMP,'"+fabricsTransferIn.getUserId()+"');";
			session.createSQLQuery(sql).executeUpdate();

			for (FabricsRoll roll : fabricsTransferIn.getFabricsRollList()) {
				sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleNo,styleItemId,itemName,colorId,colorName,itemColorId,transactionId,transactionType,itemType,rollId,unitId,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
						"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getStyleNo()+"','"+roll.getItemId()+"','"+roll.getItemName()+"','"+roll.getItemColorId()+"','"+roll.getItemColor()+"','"+roll.getFabricsColorId()+"','"+transactionId+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getRollId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getFabricsId()+"','0','"+fabricsTransferIn.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+fabricsTransferIn.getUserId()+"');";		
				session.createSQLQuery(sql).executeUpdate();
			}

			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public boolean editFabricsTransferIn(FabricsTransferIn fabricsTransferIn) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.FABRICS_TRANSFER_IN;
		ItemType itemType = ItemType.FABRICS;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="update tbfabricsTransferInInfo set "
					+ "date = '"+fabricsTransferIn.getTransferDate()+"'"
					+ ",transferFrom = '"+fabricsTransferIn.getTransferFrom()+"'"
					+ ",receiveFrom = '"+fabricsTransferIn.getReceiveFrom()+"'"
					+ ",remarks = '"+fabricsTransferIn.getRemarks()+"'"
					+ ",entryTime = CURRENT_TIMESTAMP"
					+ ",createBy = '"+fabricsTransferIn.getUserId()+"' where transactionId='"+fabricsTransferIn.getTransactionId()+"';";

			session.createSQLQuery(sql).executeUpdate();


			if(fabricsTransferIn.getFabricsRollList() != null) {
				for (FabricsRoll roll : fabricsTransferIn.getFabricsRollList()) {
					sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,rollId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
							"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getItemId()+"','"+roll.getItemColorId()+"','"+roll.getFabricsColorId()+"','"+fabricsTransferIn.getTransactionId()+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getRollId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getUnitQty()+"','"+roll.getFabricsId()+"','0','"+fabricsTransferIn.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+fabricsTransferIn.getUserId()+"');";		
					session.createSQLQuery(sql).executeUpdate();
				}
			}


			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public String editTransferIndRollInTransaction(FabricsRoll fabricsRoll) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();
			String sql = "select far.autoId,dbo.fabricsBalanceQtyExceptAutoId(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.rollId,far.departmentId,far.autoId) as balanceQty \r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"where far.autoId = '"+fabricsRoll.getAutoId()+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()>0) {
				Object[] element = (Object[]) list.get(0);

				sql = "update tbFabricsAccessoriesTransaction set unitQty = '"+fabricsRoll.getUnitQty()+"',qty = '"+fabricsRoll.getUnitQty()+"' where autoId = '"+fabricsRoll.getAutoId()+"'";
				if(session.createSQLQuery(sql).executeUpdate()==1) {
					tx.commit();
					return "Successful";
				}

			}			

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}

	@Override
	public String deleteTransferIndRollFromTransaction(FabricsRoll fabricsRoll) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();

			String sql = "delete from tbFabricsAccessoriesTransaction where autoId = '"+fabricsRoll.getAutoId()+"'";
			if(session.createSQLQuery(sql).executeUpdate()==1) {
				tx.commit();
				return "Successful";
			}

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}

	@Override
	public List<FabricsTransferIn> getFabricsTransferInList() {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsTransferIn tempIssue;
		List<FabricsTransferIn> datalist=new ArrayList<FabricsTransferIn>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select fii.AutoId,fii.transactionId,(select convert(varchar,fii.date,103))as issuedDate,fii.transferFrom,fii.receiveFrom,fii.remarks,fii.createBy,di.DepartmentName,fi.FactoryName\r\n" + 
					"from tbFabricsTransferInInfo fii\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on fii.transferFrom = di.DepartmentId\r\n" + 
					"left join TbFactoryInfo fi\r\n" + 
					"on di.FactoryId = fi.FactoryId";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempIssue = new FabricsTransferIn(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString());
				tempIssue.setTransferDepartmentName(element[7].toString()+"("+element[8].toString()+")");
				datalist.add(tempIssue);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}

	@Override
	public FabricsTransferIn getFabricsTransferInInfo(String transferOutTransectionId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsTransferIn fabricsTransfer = null;
		FabricsRoll tempRoll;
		List<FabricsRoll> fabricsRollList = new ArrayList<FabricsRoll>();	

		try{	
			tx=session.getTransaction();
			tx.begin();		
			
			String sql="select far.autoId,far.transactionId,far.purchaseOrder,far.styleId,ISNULL(far.styleNo,'') as styleNo ,far.styleItemId,ISNULL(far.itemname,'') as itemname,far.colorId as itemColorId ,ISNULL(far.Colorname,'') as itemColorName,dItemId as fabricsId,fi.ItemName as fabricsName,far.itemColorId as fabricsColorId,isnull(fc.Colorname,'') as fabricsColorName,far.rollId,ISNULL(fri.supplierRollId,'') as supplierRollId,far.unitId,u.unitname,far.qty,rackName,BinName,far.userId\n" + 
					",dbo.fabricsBalanceQty(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.rollId,far.departmentId) as balanceQty \n" + 
					",(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.dItemId and far.itemColorId = t.itemColorId and t.transactionType = '1' and t.departmentId = far.departmentId) as previousReceiveQty,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '3' and t.departmentId = far.departmentId) as returnQty,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '4' and t.departmentId = far.departmentId) as issueQty\n" + 
					"from tbFabricsAccessoriesTransaction far\n" + 
					"left join tbfabricsRollInfo fri\n" + 
					"on far.rollId = fri.rollId \n" + 
					"left join tbunits u\n" + 
					"on far.unitId = u.Unitid\n" + 
					"left join TbFabricsItem fi\n" + 
					"on far.dItemId = fi.id\n" + 
					"left join tbColors fc\n" + 
					"on far.itemColorId = fc.ColorId\n" + 
					"where transactionId = '"+transferOutTransectionId+"' and transactionType='"+StoreTransaction.FABRICS_TRANSFER_IN.getType()+"'";
			
			/*String sql = "select far.autoId,far.transactionId,far.purchaseOrder,far.styleId,sc.styleNo,far.styleItemId,id.itemname,far.colorId as itemColorId ,ic.Colorname as itemColorName,dItemId as fabricsId,fi.ItemName as fabricsName,far.itemColorId as fabricsColorId,isnull(fc.Colorname,'') as fabricsColorName,far.rollId,fri.supplierRollId,far.unitId,u.unitname,far.qty,rackName,BinName,far.userId\r\n" + 
					",dbo.fabricsBalanceQty(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.rollId,far.departmentId) as balanceQty \r\n" +
					",(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.dItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.FABRICS_RECEIVE.getType()+"' and t.departmentId = far.departmentId) as previousReceiveQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.FABRICS_RETURN.getType()+"' and t.departmentId = far.departmentId) as returnQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.FABRICS_ISSUE.getType()+"' and t.departmentId = far.departmentId) as issueQty\r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on far.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on far.styleItemId = id.itemid\r\n"
					+ "left join tbfabricsRollInfo fri\r\n" + 
					"on far.rollId = fri.rollId \r\n" + 
					"left join tbunits u\r\n" + 
					"on far.unitId = u.Unitid\r\n" + 
					"left join TbFabricsItem fi\r\n" + 
					"on far.dItemId = fi.id\r\n" + 
					"left join tbColors ic\r\n" + 
					"on far.colorId = ic.ColorId\r\n" + 
					"left join tbColors fc\r\n" + 
					"on far.itemColorId = fc.ColorId\r\n" + 
					"where transactionId = '"+transferOutTransectionId+"' and transactionType='"+StoreTransaction.FABRICS_TRANSFER_IN.getType()+"'";*/	
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempRoll = new FabricsRoll(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(),element[7].toString(), element[8].toString(),element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(), element[15].toString(),element[16].toString(),0.0, Double.valueOf(element[17].toString()), element[18].toString(), element[19].toString(),1);
				tempRoll.setBalanceQty(Double.valueOf(element[21].toString()));
				tempRoll.setPreviousReceiveQty(Double.valueOf(element[22].toString()));
				tempRoll.setReturnQty(Double.valueOf(element[23].toString()));
				tempRoll.setIssueQty(Double.valueOf(element[24].toString()));

				fabricsRollList.add(tempRoll);				
			}
			sql = "select fii.AutoId,fii.transactionId,(select convert(varchar,fii.date,103))as issuedDate,fii.transferFrom,fii.receiveFrom,fii.remarks,fii.createBy,di.DepartmentName,fi.FactoryName\r\n" + 
					"from tbFabricsTransferInInfo fii\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on fii.transferFrom = di.DepartmentId\r\n" + 
					"left join TbFactoryInfo fi\r\n" + 
					"on di.FactoryId = fi.FactoryId\r\n" + 
					"where fii.transactionId = '"+transferOutTransectionId+"'\r\n" + 
					"";		
			list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				Object[] element = (Object[]) list.get(0);

				fabricsTransfer = new FabricsTransferIn(element[0].toString(), element[1].toString(), element[2].toString(),  element[3].toString(),  element[4].toString(),  element[5].toString(), element[6].toString());
				fabricsTransfer.setFabricsRollList(fabricsRollList);
			}

			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return fabricsTransfer;
	}















	@Override
	public List<AccessoriesIndent> getAccessoriesPurchaseOrdeIndentrList(String supplierId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;



		List<AccessoriesIndent> datalist=new ArrayList<AccessoriesIndent>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql="select ai.pono,ai.PurchaseOrder,ai.styleid,ai.styleNo,ai.Itemid,ai.itemName,ai.ColorId,ai.colorName,ai.accessoriesItemId,aItem.itemname as accessoriesName,ai.IndentColorId as accessoriesColorId,isnull(ic.Colorname,'') accessoriesColor,count(ai.PurchaseOrder),\n" + 
					"sum(TotalQty) as OrderQty,(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction where purchaseOrder=ai.PurchaseOrder and styleId = ai.styleid and styleItemId = ai.Itemid and ColorId=ai.ColorId  and dItemId = ai.accessoriesItemId and transactionType='8' and itemType = '2') as ReceivedQty,\n" + 
					"sum(TotalQty)-(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction where purchaseOrder=ai.PurchaseOrder and styleId = ai.styleid and styleItemId = ai.Itemid and ColorId=ai.ColorId  and dItemId = ai.accessoriesItemId and transactionType='8' and itemType = '2') as BalanceQty\n" + 
					"from tbAccessoriesIndent ai \n" + 
					"left join tbColors ic\n" + 
					"on ai.IndentColorId = ic.ColorId\n" + 
					"left join TbAccessoriesItem aItem\n" + 
					"on aItem.itemid = ai.accessoriesItemId\n" + 
					" where ai.pono is not null and ai.pono!='0' and ai.supplierid = '"+supplierId+"' and  (select sum(TotalQty) from tbAccessoriesIndent where pono=ai.pono and PurchaseOrder=ai.PurchaseOrder and styleId=ai.styleid and ItemId=ai.Itemid and ColorId=ai.ColorId and accessoriesItemId=ai.accessoriesItemId)-(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction where purchaseOrder=ai.PurchaseOrder and styleId = ai.styleid and styleItemId = ai.Itemid and ColorId=ai.ColorId  and dItemId = ai.accessoriesItemId and transactionType='8' and itemType = '2') >0\n" + 
					" group by ai.pono,ai.PurchaseOrder,ai.styleid,ai.styleNo,ai.Itemid,ai.itemName,ai.ColorId,ai.colorName,ai.accessoriesItemId,aItem.itemname,ai.IndentColorId,ic.Colorname\n" + 
					"";

			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				datalist.add(new AccessoriesIndent(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(),element[11].toString(),element[13].toString(),element[14].toString(),element[15].toString()));		
			}		
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}



	@Override
	public AccessoriesIndent getAccessoriesIndentInfo(String autoId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		AccessoriesIndent indent= null;
		try{	
			tx=session.getTransaction();
			tx.begin();	
			String sql="select rf.id,PurchaseOrder,rf.styleId,sc.StyleNo,rf.itemid,id.itemname,rf.itemcolor,c.colorName,rf.accessoriesid,fi.ItemName as accessoriesName,rf.accessoriescolor,fc.Colorname,rf.unitId,u.unitname,rf.RequireUnitQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction fat where fat.purchaseOrder = rf.PurchaseOrder and fat.styleId = rf.styleId and fat.styleItemId = rf.itemid and fat.colorId = rf.itemcolor and fat.dItemId = rf.accessoriesId and fat.itemColorId = rf.accessoriescolor and fat.transactionType = '"+StoreTransaction.ACCESSORIES_RECEIVE.getType()+"') as previousReceiveQty\r\n" + 
					"from tbAccessoriesIndent rf\r\n" + 
					"left join TbStyleCreate  sc\r\n" + 
					"on rf.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on rf.itemid = id.itemid\r\n" + 
					"left join tbColors c\r\n" + 
					"on rf.itemcolor = c.ColorId\r\n" + 
					"left join TbAccessoriesItem fi\r\n" + 
					"on rf.accessoriesid = fi.id\r\n" + 
					"left join tbColors fc\r\n" + 
					"on rf.accessoriescolor = fc.ColorId\r\n" + 
					"left join tbunits u\r\n" + 
					"on rf.unitId = u.Unitid\r\n" + 
					" where rf.id = '"+autoId+"'";

			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				indent = new AccessoriesIndent();
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return indent;
	}



	@Override
	public boolean submitAccessoriesReceive(AccessoriesReceive accessoriesReceive) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select (isnull(max(transactionid),0)+1) as maxId from tbaccessoriesReceiveInfo";
			List<?> list = session.createSQLQuery(sql).list();
			String transactionId="0";
			if(list.size()>0) {
				transactionId = list.get(0).toString();
			}

			sql="insert into tbaccessoriesReceiveInfo (transactionid,"
					+ "grnNo,"
					+ "grnDate,"
					+ "location,"		
					+ "supplierId,"
					+ "challanNo,"
					+ "challanDate,"
					+ "remarks,"
					+ "departmentId,"
					+ "preperedBy,"
					+ "entryTime,"
					+ "createBy) \r\n" + 
					"values('"+transactionId+"',"
					+ "'"+accessoriesReceive.getGrnNo()+"',"
					+ "'"+accessoriesReceive.getGrnDate()+"',"
					+ "'"+accessoriesReceive.getLocation()+"',"
					+ "'"+accessoriesReceive.getSupplierId()+"',"
					+ "'"+accessoriesReceive.getChallanNo()+"',"
					+ "'"+accessoriesReceive.getChallanDate()+"',"
					+ "'"+accessoriesReceive.getRemarks()+"',"
					+ "'"+accessoriesReceive.getDepartmentId()+"',"
					+ "'"+accessoriesReceive.getPreparedBy()+"',"
					+ "CURRENT_TIMESTAMP,"
					+ "'"+accessoriesReceive.getUserId()+"') ;";
			session.createSQLQuery(sql).executeUpdate();

			String resultValue=accessoriesReceive.getResult().substring(accessoriesReceive.getResult().indexOf("(")+1, accessoriesReceive.getResult().indexOf(")"));
			System.out.println("resultValue "+resultValue);

			StringTokenizer token=new StringTokenizer(resultValue, ",");
			while(token.hasMoreTokens()) {
				String tokenvalue=token.nextToken();
				StringTokenizer tokenresult=new StringTokenizer(tokenvalue, "*"); 
				while(tokenresult.hasMoreTokens()) {
					String purchaseOrder=tokenresult.nextToken();
					String styleId=tokenresult.nextToken();
					String styleNo=tokenresult.nextToken();
					String itemId=tokenresult.nextToken();
					String itemName=tokenresult.nextToken();
					String itemColorId=tokenresult.nextToken();
					System.out.println("itemColorId "+itemColorId);
					String itemColorName=tokenresult.nextToken();

					String accessoriesId=tokenresult.nextToken();
					String accessoriesName=tokenresult.nextToken();
					String accessoriesColorId=tokenresult.nextToken();
					String accessoriesColor=tokenresult.nextToken();
					String sizeId=tokenresult.nextToken();
					String sizeName=tokenresult.nextToken();
					String unitId=tokenresult.nextToken();
					String orderQty=tokenresult.nextToken();
					String receivedQty=tokenresult.nextToken();
					String rackName=tokenresult.nextToken();
					String binName=tokenresult.nextToken();

					sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleNo,styleItemId,itemName,colorId,itemColorId,colorName,transactionId,transactionType,"
							+ "itemType,sizeId,unitId,orderQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
							"values('"+purchaseOrder+"',"
							+ "'"+styleId+"',"
							+ "'"+styleNo+"',"
							+ "'"+itemId+"',"
							+ "'"+itemName+"',"
							+ "'"+itemColorId+"',"							
							+ "'"+accessoriesColorId+"',"
							+ "'"+itemColorName+"',"
							+ "'"+transactionId+"',"
							+ "'"+StoreTransaction.ACCESSORIES_RECEIVE.getType()+"',"
							+ "'2',"
							+ "'"+sizeId+"',"
							+ "'"+unitId+"',"
							+ "'"+orderQty+"',"
							+ "'"+receivedQty+"',"
							+ "'"+accessoriesId+"',"
							+ "'0',"
							+ "'"+accessoriesReceive.getDepartmentId()+"',"
							+ "'"+rackName+"',"
							+ "'"+binName+"',"
							+ "CURRENT_TIMESTAMP,"
							+ "'"+accessoriesReceive.getUserId()+"');";		


					session.createSQLQuery(sql).executeUpdate();

				}

			}

			/*			for (AccessoriesSize accessoriesSize : accessoriesReceive.getAccessoriesSizeList()) {


				//sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,sizeId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
				//"values('"+accessoriesSize.getPurchaseOrder()+"','"+accessoriesSize.getStyleId()+"','"+accessoriesSize.getItemId()+"','"+accessoriesSize.getItemColorId()+"','"+accessoriesSize.getAccessoriesColorId()+"','"+transactionId+"','"+StoreTransaction.ACCESSORIES_RECEIVE.getType()+"','"+ItemType.ACCESSORIES.getType()+"','"+accessoriesSize.getSizeId()+"','"+accessoriesSize.getUnitId()+"','"+accessoriesSize.getOrderQty()+"','"+accessoriesSize.getReceivedQty()+"','"+accessoriesSize.getAccessoriesId()+"','0','"+accessoriesReceive.getDepartmentId()+"','"+accessoriesSize.getRackName()+"','"+accessoriesSize.getBinName()+"',CURRENT_TIMESTAMP,'"+accessoriesReceive.getUserId()+"');";		

				sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,"
						+ "itemType,sizeId,unitId,orderQty,receivedqty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
						"values('"+accessoriesSize.getPurchaseOrder()+"',"
						+ "'"+accessoriesSize.getStyleId()+"',"
						+ "'"+accessoriesSize.getItemId()+"',"
						+ "'"+accessoriesSize.getItemColorId()+"',"
						+ "'"+accessoriesSize.getAccessoriesColorId()+"',"
						+ "'"+transactionId+"',"
						+ "'"+StoreTransaction.ACCESSORIES_RECEIVE.getType()+"',"
						+ "'Accessories',"
						+ "'"+accessoriesSize.getSizeId()+"',"
						+ "'"+accessoriesSize.getUnitId()+"',"
						+ "'"+accessoriesSize.getOrderQty()+"',"
						+ "'"+accessoriesSize.getReceivedQty()+"',"
						+ "'"+accessoriesSize.getAccessoriesId()+"',"
						+ "'0',"
						+ "'0',"
						+ "'"+accessoriesSize.getRackName()+"',"
						+ "'"+accessoriesSize.getBinName()+"',"
						+ "CURRENT_TIMESTAMP,"
						+ "'"+accessoriesReceive.getUserId()+"');";		


				session.createSQLQuery(sql).executeUpdate();
			}*/

			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public boolean editAccessoriesReceive(AccessoriesReceive accessoriesReceive) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();
			String sql="update tbaccessoriesReceiveInfo set "
					+ "grnNo='"+accessoriesReceive.getGrnNo()+"',"
					+ "grnDate='"+accessoriesReceive.getGrnDate()+"',"
					+ "location='"+accessoriesReceive.getLocation()+"',"
					+ "supplierId='"+accessoriesReceive.getSupplierId()+"',"
					+ "challanNo='"+accessoriesReceive.getChallanNo()+"',"
					+ "challanDate='"+accessoriesReceive.getChallanDate()+"',"
					+ "remarks='"+accessoriesReceive.getRemarks()+"',"
					+ "preperedBy='"+accessoriesReceive.getPreparedBy()+"' where transactionId= '"+accessoriesReceive.getTransactionId()+"'";


			session.createSQLQuery(sql).executeUpdate();


			String result=accessoriesReceive.getResult().toString();

			result=result.substring(1,result.length()-1);

			StringTokenizer resulttoken=new StringTokenizer(result,",");
			while(resulttoken.hasMoreTokens()) {
				String eachToken=resulttoken.nextToken().toString().trim();
				StringTokenizer tokenValue=new StringTokenizer(eachToken,"*");
				while(tokenValue.hasMoreTokens()) {
					String purhaseOrder=tokenValue.nextToken();
					String styleId=tokenValue.nextToken();
					//String styleItemId=tokenValue.nextToken();
					//String colorId=tokenValue.nextToken();
					String itemColorId=tokenValue.nextToken();
					String transactionId=tokenValue.nextToken();
					String transactionType=tokenValue.nextToken();
					String itemType=tokenValue.nextToken();
					String sizeId=tokenValue.nextToken();
					String unitId=tokenValue.nextToken();
					String unitValue=tokenValue.nextToken();
					String qty=tokenValue.nextToken();
					String dItemId=tokenValue.nextToken();
					String cItemId=tokenValue.nextToken();
					String departmentId=tokenValue.nextToken();
					String rackName=tokenValue.nextToken();
					String binName=tokenValue.nextToken();
				}
			}

			/*			if(accessoriesReceive.getAccessoriesSizeList() != null) {
				for (AccessoriesSize size : accessoriesReceive.getAccessoriesSizeList()) {

					sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,sizeId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
							"values('"+size.getPurchaseOrder()+"','"+size.getStyleId()+"','"+size.getItemId()+"','"+size.getItemColorId()+"','"+size.getAccessoriesColorId()+"','"+accessoriesReceive.getTransactionId()+"','"+StoreTransaction.ACCESSORIES_RECEIVE.getType()+"','"+ItemType.ACCESSORIES.getType()+"','"+size.getSizeId()+"','"+size.getUnitId()+"','"+size.getUnitQty()+"','"+size.getUnitQty()+"','"+size.getAccessoriesId()+"','0','"+accessoriesReceive.getDepartmentId()+"','"+size.getRackName()+"','"+size.getBinName()+"',CURRENT_TIMESTAMP,'"+accessoriesReceive.getUserId()+"');";		
					session.createSQLQuery(sql).executeUpdate();
				}

			}*/

			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public String editReceiveSizeInTransaction(AccessoriesSize accessoriesRoll) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();
			String sql = "select far.autoId,far.purchaseOrder,far.qty from tbFabricsAccessoriesTransaction far\r\n" + 
					"join tbFabricsAccessoriesTransaction far2\r\n" + 
					"on far.dItemId= far2.cItemId and far2.sizeId = far.sizeId and far2.itemColorId = far.itemColorId and far2.colorId = far.colorId and far2.styleItemId= far.styleItemId \r\n" + 
					"and far2.styleId = far.styleId and far2.purchaseOrder = far.purchaseOrder and (far.transactionType ='"+StoreTransaction.ACCESSORIES_RETURN.getType()+"' or far.transactionType ='"+StoreTransaction.ACCESSORIES_ISSUE.getType()+"')\r\n" + 
					"where far.autoId = '"+accessoriesRoll.getAutoId()+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()==0) {
				sql = "update tbFabricsAccessoriesTransaction set unitQty = '"+accessoriesRoll.getUnitQty()+"',qty = '"+accessoriesRoll.getUnitQty()+"' where autoId = '"+accessoriesRoll.getAutoId()+"'";
				if(session.createSQLQuery(sql).executeUpdate()==1) {
					tx.commit();
					return "Successful";
				}
			}			
			tx.commit();

			return "Used";
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}



	@Override
	public String deleteReceiveSizeFromTransaction(AccessoriesSize accessoriesSize) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();
			String sql = "select far.autoId,far.purchaseOrder,far.qty from tbFabricsAccessoriesTransaction far\r\n" + 
					"join tbFabricsAccessoriesTransaction far2\r\n" + 
					"on far.dItemId= far2.cItemId and far2.sizeId = far.sizeId and far2.itemColorId = far.itemColorId and far2.colorId = far.colorId and far2.styleItemId= far.styleItemId \r\n" + 
					"and far2.styleId = far.styleId and far2.purchaseOrder = far.purchaseOrder and (far.transactionType ='"+StoreTransaction.ACCESSORIES_RETURN.getType()+"' or far.transactionType ='"+StoreTransaction.ACCESSORIES_ISSUE.getType()+"')\r\n" + 
					"where far.autoId = '"+accessoriesSize.getAutoId()+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()==0) {
				sql = "delete from tbFabricsAccessoriesTransaction where autoId = '"+accessoriesSize.getAutoId()+"'";
				session.createSQLQuery(sql).executeUpdate();
				tx.commit();
				return "Successful";

			}			
			tx.commit();

			return "Used";
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}



	@Override
	public List<AccessoriesReceive> getAccessoriesReceiveList() {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<AccessoriesReceive> datalist=new ArrayList<AccessoriesReceive>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select autoId,transactionId,grnNo,(select convert(varchar,grnDate,103))as grnDate,location,fri.supplierId,fri.challanNo,fri.challanDate,fri.remarks,fri.preperedBy,fri.createBy \r\n" + 
					"from tbAccessoriesReceiveInfo fri";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				datalist.add(new AccessoriesReceive(element[0].toString(), element[1].toString(),element[2].toString(), element[3].toString(), element[4].toString(), "", element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString()));				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}

	@Override
	public List<AccessoriesSize> getAccessoriesReceiveInfo(String transactionId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesReceive accessoriesReceive = null;
		AccessoriesSize tempSize;
		List<AccessoriesSize> accessoriesSizeList = new ArrayList<AccessoriesSize>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql="select far.purchaseOrder,far.styleId,ISNULL((select StyleNo from TbStyleCreate where StyleId=far.styleItemId),'') as StyleNo,far.styleItemId,ISNULL((select itemname from tbItemDescription where itemid=far.styleItemId),'') as ItemName,far.colorId as itemColorId,ISNULL((select colorname from tbColors where ColorId=itemColorId),'') as colorName,dItemId as accessoriesId,(select itemname from TbAccessoriesItem where itemid=dItemId) as AccessoriesName,far.itemColorId as accessoriesColorId,ISNULL((select colorname from tbColors where ColorId=itemColorId),'') as AccessoriesColorName,far.sizeId,ISNULL((select sizeName from tbStyleSize where id=far.styleId),'') as SizeName,far.unitId,(select unitname from tbunits where unitId=far.unitId) as UnitName,far.orderQty,far.qty,rackName,BinName,far.userId,"
					+ "(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.dItemId = t.dItemId and far.itemColorId = t.itemColorId and t.sizeId=far.sizeId and t.transactionType = '8' and t.departmentId = far.departmentId  and t.entryTime<far.entryTime) as previousReceiveQty,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.dItemId = t.cItemId and far.itemColorId = t.itemColorId and far.sizeId = t.sizeId and t.transactionType = '10' and t.departmentId = far.departmentId) as returnQty,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.dItemId = t.cItemId and far.itemColorId = t.itemColorId and far.sizeId = t.sizeId and t.transactionType = '11' and t.departmentId = far.departmentId) as issueQty,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction qc where far.purchaseOrder=qc.purchaseOrder and far.styleId =qc.styleId and far.styleItemId= qc.styleItemId and far.colorId = qc.colorId and far.dItemId = qc.dItemId and far.itemColorId = qc.itemColorId and far.sizeId = qc.sizeId and qc.transactionType = '9' and qc.departmentId = far.departmentId) as qcPassedQty,ar.supplierId,ar.challanNo,ar.challanDate,ar.grnNo,ar.grnDate,ar.remarks \n" + 
					"from tbFabricsAccessoriesTransaction far  join tbaccessoriesReceiveInfo ar on  ar.transactionId=far.transactionId \n" + 
					"where  far.transactionId = '"+transactionId+"' and far.transactionType='"+StoreTransaction.ACCESSORIES_RECEIVE.getType()+"' and far.itemType='2'";
			/*			String sql = "select far.autoId,far.transactionId,far.purchaseOrder,far.styleId,sc.styleNo,far.styleItemId,id.itemname,far.colorId as itemColorId ,ic.Colorname as itemColorName,dItemId as accessoriesId,acceItem.itemname as accessoriesName,far.itemColorId as accessoriesColorId,isnull(fc.Colorname,'') as accessoriesColorName,far.sizeId,isnull(sSize.sizeName,'') as sizeName,far.unitId,u.unitname,unitQty,rackName,BinName,far.userId,\r\n" + 
					"findent.RequireUnitQty as orderQty,(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.dItemId = t.dItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.ACCESSORIES_RECEIVE.getType()+"' and t.departmentId = far.departmentId and far.transactionId != t.transactionId) as previousReceiveQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.dItemId = t.cItemId and far.itemColorId = t.itemColorId and far.sizeId = t.sizeId and t.transactionType = '"+StoreTransaction.ACCESSORIES_RETURN.getType()+"' and t.departmentId = far.departmentId) as returnQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.dItemId = t.cItemId and far.itemColorId = t.itemColorId and far.sizeId = t.sizeId and t.transactionType = '"+StoreTransaction.ACCESSORIES_ISSUE.getType()+"' and t.departmentId = far.departmentId) as issueQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction qc where far.purchaseOrder=qc.purchaseOrder and far.styleId =qc.styleId and far.styleItemId= qc.styleItemId and far.colorId = qc.colorId and far.dItemId = qc.dItemId and far.itemColorId = qc.itemColorId and far.sizeId = qc.sizeId and qc.transactionType = '"+StoreTransaction.ACCESSORIES_QC.getType()+"' and qc.departmentId = far.departmentId) as qcPassedQty \r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"left join tbAccessoriesIndent findent\r\n" + 
					"on far.PurchaseOrder = findent.purchaseOrder and far.styleId = findent.styleId and far.styleItemId= findent.itemId and far.colorId = findent.colorId and far.dItemId = findent.accessoriesItemId and far.itemColorId = findent.IndentColorId and far.sizeId = findent.size\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on far.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on far.styleItemId = id.itemid\r\n" + 
					"left join tbStyleSize sSize\r\n" + 
					"on far.sizeId = sSize.id \r\n" + 
					"left join tbunits u\r\n" + 
					"on far.unitId = u.Unitid\r\n" + 
					"left join TbAccessoriesItem acceItem\r\n" + 
					"on far.dItemId = acceItem.itemid\r\n" + 
					"left join tbColors ic\r\n" + 
					"on far.colorId = ic.ColorId\r\n" + 
					"left join tbColors fc\r\n" + 
					"on far.itemColorId = fc.ColorId\r\n" + 
					"where transactionId = '"+transactionId+"' and transactionType='"+StoreTransaction.ACCESSORIES_RECEIVE.getType()+"'";*/		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				//tempSize = new AccessoriesSize(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(),Double.parseDouble(element[15].toString()), Double.parseDouble(element[16].toString()),element[17].toString(),element[18].toString(),0.0, 0.0, 0.0);
				//tempSize = new AccessoriesSize(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(),element[7].toString(), element[8].toString(),element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(), element[15].toString(),element[16].toString(),0.0, Double.valueOf(element[17].toString()), element[18].toString(), element[19].toString(),1);
				/*			tempSize.setOrderQty(Double.valueOf(element[21].toString()));
				tempSize.setPreviousReceiveQty(Double.valueOf(element[22].toString()));
				tempSize.setReturnQty(Double.valueOf(element[23].toString()));
				tempSize.setIssueQty(Double.valueOf(element[24].toString()));
				tempSize.setQcPassedQty(Double.valueOf(element[25].toString()));
				accessoriesSizeList.add(tempSize);	*/	

				System.out.println("PR "+element[20].toString());

				accessoriesSizeList.add(new AccessoriesSize(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(),Double.parseDouble(element[15].toString()), Double.parseDouble(element[16].toString()),element[17].toString(),element[18].toString(),0.0, 0.0, Double.parseDouble(element[20].toString()),element[24].toString(),element[25].toString(),element[26].toString(),element[27].toString(),element[28].toString(),element[29].toString()));
			}

			/*		sql = "select autoId,transactionId,grnNo,(select convert(varchar,grnDate,103))as grnDate,location,fri.supplierId,fri.challanNo,fri.challanDate,fri.remarks,fri.preperedBy,fri.createBy \r\n" + 
					"from tbAccessoriesReceiveInfo fri\r\n" + 
					"where fri.transactionId = '"+transactionId+"'";		
			list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				Object[] element = (Object[]) list.get(0);

				accessoriesReceive = new AccessoriesReceive(element[0].toString(), element[1].toString(),element[2].toString(), element[3].toString(), element[4].toString(), "", element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString());
				accessoriesReceive.setAccessoriesSizeList(accessoriesSizeList);
			}*/

			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return accessoriesSizeList;
	}


	@Override
	public List<AccessoriesSize> getAccessoriesSizeListByAccessories(String accessorisList) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<AccessoriesSize> datalist=new ArrayList<AccessoriesSize>();	
		try{	
			tx=session.getTransaction();
			tx.begin();	


			String result=accessorisList;

			result=result.substring(1,result.length()-1);

			StringTokenizer resulttoken=new StringTokenizer(result,",");

			System.out.println("resulttoken "+resulttoken);

			while(resulttoken.hasMoreElements()) {
				String tokenValue=resulttoken.nextToken();
				StringTokenizer tokenResult=new StringTokenizer(tokenValue,"*");
				while(tokenResult.hasMoreTokens()){
					String purchaseOrder=tokenResult.nextToken().toString().replace("@", "");
					String styleNo=tokenResult.nextToken().toString().replace("@", "");
					String itemName=tokenResult.nextToken().toString().replace("@", "");
					String colorName=tokenResult.nextToken().toString().replace("@", "");
					String accessoriesId=tokenResult.nextToken().toString().replace("@", "");
					String indentColorId=tokenResult.nextToken().toString().replace("@", "");

					String sql="select ai.PurchaseOrder,ai.styleid,ai.StyleNo,ai.Itemid,ai.itemname,ai.ColorId,ai.Colorname,ai.accessoriesItemId,aItem.itemname as accessoriesName,ai.IndentColorId as accessoriesColorId,isnull(ic.Colorname,'') accessoriesColor,ai.size,isnull(ss.sizeName,'') as sizeName,ai.UnitId,u.unitname,ai.TotalQty,"
							+ "(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.purchaseOrder=ai.purchaseOrder and t.styleId = ai.styleId and t.styleItemId=ai.Itemid and t.colorId=ai.ColorId and t.dItemId=ai.accessoriesItemId  and t.sizeId=ai.size and t.transactionType = '8' ) as previousReceiveQty  \n" + 
							"from tbAccessoriesIndent ai\n" + 
							"left join tbColors ic\n" + 
							"on ai.IndentColorId = ic.ColorId\n" + 
							"left join TbAccessoriesItem aItem\n" + 
							"on aItem.itemid = ai.accessoriesItemId\n" + 
							"left join tbStyleSize ss\n" + 
							"on ai.size = ss.id\n" + 
							"left join tbunits u\n" + 
							"on ai.UnitId = u.Unitid\n" + 
							" where ai.pono is not null and ai.PurchaseOrder = '"+purchaseOrder+"' and ai.styleNo = '"+styleNo+"' and ai.ItemName = '"+itemName+"' and ai.accessoriesItemId = '"+accessoriesId+"' ";

					List<?> list = session.createSQLQuery(sql).list();
					for(Iterator<?> iter = list.iterator(); iter.hasNext();)
					{	
						Object[] element = (Object[]) iter.next();
						System.out.println("colorName "+element[10].toString());
						AccessoriesSize accessoriesSize = new AccessoriesSize(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(),Double.parseDouble(element[15].toString()), 0.0, 0.0, Double.parseDouble(element[16].toString()));
						//accessoriesSize.setUnitQty(Double.valueOf(element[15].toString()));
						datalist.add(accessoriesSize);				
					}	

				}
			}


			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}



	//Accessories Quality Control
	@Override
	public boolean submitAccessoriesQC(AccessoriesQualityControl accessoriesQC) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.ACCESSORIES_QC;
		ItemType itemType = ItemType.ACCESSORIES;


		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select (isnull(max(transactionId),0)+1) as maxId from tbaccessoriesQualityControlInfo";
			List<?> list = session.createSQLQuery(sql).list();
			String transactionId="0";
			if(list.size()>0) {
				transactionId = list.get(0).toString();
			}

			sql="insert into tbaccessoriesQualityControlInfo (transactionId"
					+ ",date"
					+ ",grnNo"
					+ ",remarks"
					+ ",departmentId"
					+ ",checkBy"
					+ ",entryTime"
					+ ",createBy) values("
					+ "'"+transactionId+"'"
					+ ",'"+accessoriesQC.getQcDate()+"'"
					+ ",'"+accessoriesQC.getGrnNo()+"'"
					+ ",'"+accessoriesQC.getRemarks()+"'"
					+ ",'"+accessoriesQC.getDepartmentId()+"'"
					+ ",'"+accessoriesQC.getCheckBy()+"',"
					+ "CURRENT_TIMESTAMP,'"+accessoriesQC.getUserId()+"');";
			session.createSQLQuery(sql).executeUpdate();


			for (AccessoriesSize roll : accessoriesQC.getAccessoriesSizeList()) {
				sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,sizeId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
						"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getItemId()+"','"+roll.getItemColorId()+"','"+roll.getAccessoriesColorId()+"','"+transactionId+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getSizeId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getUnitQty()+"','"+roll.getAccessoriesId()+"','0','"+accessoriesQC.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+accessoriesQC.getUserId()+"');";		
				session.createSQLQuery(sql).executeUpdate();
			}

			for (AccessoriesSize roll : accessoriesQC.getAccessoriesSizeList()) {
				sql="insert into tbQualityControlDetails (transactionId,transactionType,itemType,itemId,sizeId,unitId,QCCheckQty,shade,shrinkage,gsm,width,defect,remarks,qcPassedType,entryTime,userId) \r\n" + 
						"values('"+transactionId+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getAccessoriesId()+"','"+roll.getSizeId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getShadeQty()+"','"+roll.getShrinkageQty()+"','"+roll.getGsmQty()+"','"+roll.getWidthQty()+"','"+roll.getDefectQty()+"','"+roll.getRemarks()+"','"+roll.getQcPassedType()+"',CURRENT_TIMESTAMP,'"+accessoriesQC.getUserId()+"')";		
				session.createSQLQuery(sql).executeUpdate();
			}

			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public boolean editAccessoriesQC(AccessoriesQualityControl accessoriesQC) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="update tbaccessoriesQualityControlInfo set "
					+ "date = '"+accessoriesQC.getQcDate()+"'"
					+ ",grnNo = '"+accessoriesQC.getGrnNo()+"'"
					+ ",remarks = '"+accessoriesQC.getRemarks()+"'"
					+ ",entryTime = CURRENT_TIMESTAMP"
					+ ",createBy = '"+accessoriesQC.getUserId()+"' where transactionId='"+accessoriesQC.getQcTransactionId()+"';";

			session.createSQLQuery(sql).executeUpdate();

			if(accessoriesQC.getAccessoriesSizeList() != null) {
				for (AccessoriesSize size : accessoriesQC.getAccessoriesSizeList()) {
					sql="update tbQualityControlDetails set qcCheckQty='"+size.getQcPassedQty()+"',qcPassedType='"+size.getQcPassedType()+"' where autoId='"+size.getAutoId()+"'";		
					session.createSQLQuery(sql).executeUpdate();
				}
			}
			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public List<AccessoriesQualityControl> getAccessoriesQCList() {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<AccessoriesQualityControl> datalist=new ArrayList<AccessoriesQualityControl>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select qci.AutoId,qci.TransactionId,(select convert(varchar,qci.date,103))as qcDate,qci.grnNo,(select convert(varchar,fri.grnDate,103))as receiveDate,qci.remarks,fri.supplierId,qci.checkBy,qci.createBy \r\n" + 
					"from tbAccessoriesQualityControlInfo qci\r\n" + 
					"left join tbAccessoriesReceiveInfo fri\r\n" + 
					"on qci.grnNo = fri.grnNo";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();

				datalist.add(new AccessoriesQualityControl(element[0].toString(),element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString()));				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}

	@Override
	public AccessoriesQualityControl getAccessoriesQCInfo(String qcTransactionId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesQualityControl accessoriesQC = null;
		List<AccessoriesSize> accessoriesSizeList = new ArrayList<AccessoriesSize>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select qcd.autoId,qcd.transactionId,qcd.sizeId,ss.sizeName,fat.purchaseOrder,fat.styleId,fat.styleItemId,fat.colorId,fat.dItemId,ai.itemname as accessoriesName,fat.itemColorId,isnull(c.Colorname,'') as accessoriesColor,qcd.unitId,u.unitname,far.unitQty,QCCheckQty,shade,shrinkage,gsm,width,defect,remarks,fat.rackName,fat.BinName,qcPassedType,qcd.userId \r\n" + 
					"from tbQualityControlDetails qcd\r\n" + 
					"left join tbStyleSize ss\r\n" + 
					"on qcd.sizeId = ss.id\r\n" + 
					"left join tbunits u\r\n" + 
					"on qcd.unitId = u.Unitid\r\n" + 
					"left join tbFabricsAccessoriesTransaction far\r\n" + 
					"on qcd.transactionId = far.transactionId and far.transactionType = '"+StoreTransaction.ACCESSORIES_QC.getType()+"' and qcd.itemId = far.dItemId and qcd.sizeId = far.sizeId\r\n" + 
					"left join tbFabricsAccessoriesTransaction fat\r\n" + 
					"on qcd.transactionId = fat.transactionId and qcd.transactionType = fat.transactionType and qcd.itemId = fat.dItemId and qcd.sizeId = fat.sizeId\r\n "+
					"left join TbAccessoriesItem ai \n" + 
					"on fat.dItemId = ai.itemid \n" + 
					"left join tbColors c \n" + 
					"on fat.itemColorId = c.ColorId \n" + 
					"where qcd.transactionId = '"+qcTransactionId+"' and far.transactionType = '"+StoreTransaction.ACCESSORIES_QC.getType()+"' \r\n" + 
					"";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();

				accessoriesSizeList.add(new AccessoriesSize(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), Double.valueOf(element[14].toString()), Double.valueOf(element[15].toString()), Double.valueOf(element[16].toString()), Double.valueOf(element[17].toString()), Double.valueOf(element[18].toString()), Double.valueOf(element[19].toString()),Double.valueOf(element[20].toString()), element[21].toString(), element[22].toString(), element[23].toString(), Integer.valueOf(element[24].toString())));				
			}
			sql = "select qci.AutoId,qci.TransactionId,(select convert(varchar,qci.date,103))as qcDate,qci.grnNo,(select convert(varchar,fri.grnDate,103))as receiveDate,qci.remarks,fri.supplierId,qci.checkBy,qci.createBy \r\n" + 
					"from tbAccessoriesQualityControlInfo qci\r\n" + 
					"left join tbAccessoriesReceiveInfo fri\r\n" + 
					"on qci.grnNo = fri.grnNo where qci.transactionId = '"+qcTransactionId+"'";		
			list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				Object[] element = (Object[]) list.get(0);

				accessoriesQC = new AccessoriesQualityControl(element[0].toString(), qcTransactionId, element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(),element[8].toString());
				accessoriesQC.setAccessoriesSizeList(accessoriesSizeList);
			}

			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return accessoriesQC;
	}

	@Override
	public List<AccessoriesSize> getAccessoriesSizeListBySupplier(String supplierId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesSize tempSize;
		List<AccessoriesSize> datalist=new ArrayList<AccessoriesSize>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select fat.autoID,fri.supplierId,s.name as supplierName,fat.purchaseOrder,fat.styleId,sc.StyleNo,fat.styleItemId,id.itemname,fat.colorid,c.colorName,fat.dItemId as accessoriesId,ai.ItemName as accessoriesName,fat.itemColorId,isnull(ic.Colorname,'') as accessoriesColor,fat.sizeId,isnull(ss.sizeName,'')as sizeName,fat.unitId,u.unitname,dbo.accessoriesBalanceQty(fat.purchaseOrder,fat.styleid,fat.styleItemId,fat.colorId,fat.dItemId,fat.ItemcolorId,fat.sizeId,fat.departmentId) as balanceQty,fat.rackName,fat.binName,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.dItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = fat.transactionType and t.itemColorId = fat.itemColorId and t.dItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as ReceiveQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.cItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = '"+StoreTransaction.ACCESSORIES_ISSUE.getType()+"' and t.itemColorId = fat.itemColorId and t.cItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as IssueQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.cItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = '"+StoreTransaction.ACCESSORIES_RETURN.getType()+"' and t.itemColorId = fat.itemColorId and t.cItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as previousReturnQty\r\n" + 
					"from tbAccessoriesReceiveInfo fri\r\n" + 
					"left join tbSupplier s\r\n" + 
					"on fri.supplierId = s.id\r\n" + 
					"left join tbFabricsAccessoriesTransaction fat\r\n" + 
					"on fri.transactionId = fat.transactionId and fat.transactionType = '"+StoreTransaction.ACCESSORIES_RECEIVE.getType()+"'\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on fat.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on fat.styleItemId = id.itemid\r\n" + 
					"left join tbColors c\r\n" + 
					"on fat.colorId = c.ColorId\r\n" + 
					"left join TbAccessoriesItem ai\r\n" + 
					"on fat.dItemId = ai.itemid\r\n" + 
					"left join tbColors ic\r\n" + 
					"on fat.itemColorId = ic.ColorId\r\n" + 
					"left join tbStyleSize ss\r\n" + 
					"on fat.sizeId = ss.id\r\n" + 
					"left join tbunits u\r\n" + 
					"on fat.unitId = u.Unitid\r\n" + 
					" where fri.supplierId='"+supplierId+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempSize = new AccessoriesSize(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(),element[15].toString(),element[16].toString(),element[17].toString(), Double.valueOf(element[18].toString()),element[19].toString(),element[20].toString());
				tempSize.setPreviousReceiveQty(Double.valueOf(element[21].toString()));
				tempSize.setIssueQty(Double.valueOf(element[22].toString()));
				tempSize.setReturnQty(Double.valueOf(element[23].toString()));

				datalist.add(tempSize);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}

	@Override
	public boolean submitAccessoriesReturn(AccessoriesReturn accessoriesReturn) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.ACCESSORIES_RETURN;
		ItemType itemType = ItemType.ACCESSORIES;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select (isnull(max(transactionId),0)+1) as maxId from tbAccessoriesReturnInfo";
			List<?> list = session.createSQLQuery(sql).list();
			String transactionId="0";
			if(list.size()>0) {
				transactionId = list.get(0).toString();
			}

			sql="insert into tbAccessoriesReturnInfo (transactionId"
					+ ",date"
					+ ",supplierId"
					+ ",remarks"
					+ ",departmentId"
					+ ",entryTime"
					+ ",createBy) values("
					+ "'"+transactionId+"'"
					+ ",'"+accessoriesReturn.getReturnDate()+"'"
					+ ",'"+accessoriesReturn.getSupplierId()+"'"
					+ ",'"+accessoriesReturn.getRemarks()+"'"
					+ ",'"+accessoriesReturn.getDepartmentId()+"',"
					+ "CURRENT_TIMESTAMP,'"+accessoriesReturn.getUserId()+"');";
			session.createSQLQuery(sql).executeUpdate();


			for (AccessoriesSize roll : accessoriesReturn.getAccessoriesSizeList()) {
				sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,sizeId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
						"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getItemId()+"','"+roll.getItemColorId()+"','"+roll.getAccessoriesColorId()+"','"+transactionId+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getSizeId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getUnitQty()+"','0','"+roll.getAccessoriesId()+"','"+accessoriesReturn.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+accessoriesReturn.getUserId()+"');";		
				session.createSQLQuery(sql).executeUpdate();
			}

			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public boolean editAccessoriesReturn(AccessoriesReturn accessoriesReturn) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.ACCESSORIES_RETURN;
		ItemType itemType = ItemType.ACCESSORIES;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="update tbaccessoriesReturnInfo set "
					+ "date = '"+accessoriesReturn.getReturnDate()+"'"
					+ ",supplierId = '"+accessoriesReturn.getSupplierId()+"'"
					+ ",remarks = '"+accessoriesReturn.getRemarks()+"'"
					+ ",entryTime = CURRENT_TIMESTAMP"
					+ ",createBy = '"+accessoriesReturn.getUserId()+"' where transactionId='"+accessoriesReturn.getReturnTransactionId()+"';";

			session.createSQLQuery(sql).executeUpdate();


			if(accessoriesReturn.getAccessoriesSizeList() != null) {
				for (AccessoriesSize roll : accessoriesReturn.getAccessoriesSizeList()) {
					sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,sizeId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
							"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getItemId()+"','"+roll.getItemColorId()+"','"+roll.getAccessoriesColorId()+"','"+accessoriesReturn.getReturnTransactionId()+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getSizeId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getUnitQty()+"','0','"+roll.getAccessoriesId()+"','"+accessoriesReturn.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+accessoriesReturn.getUserId()+"');";		
					session.createSQLQuery(sql).executeUpdate();
				}
			}


			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public String editReturnSizeInTransaction(AccessoriesSize accessoriesSize) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();
			String sql = "select far.autoId,dbo.accessoriesBalanceQtyExceptAutoId(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.sizeId,far.departmentId,far.autoId) as balanceQty \r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"where far.autoId = '"+accessoriesSize.getAutoId()+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()>0) {
				Object[] element = (Object[]) list.get(0);
				if(Double.valueOf(element[1].toString())>=accessoriesSize.getUnitQty()) {
					sql = "update tbFabricsAccessoriesTransaction set unitQty = '"+accessoriesSize.getUnitQty()+"',qty = '"+accessoriesSize.getUnitQty()+"' where autoId = '"+accessoriesSize.getAutoId()+"'";
					if(session.createSQLQuery(sql).executeUpdate()==1) {
						tx.commit();
						return "Successful";
					}
				}else {
					tx.commit();
					return "Return Qty Exist";
				}
			}			

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}



	@Override
	public String deleteReturnSizeFromTransaction(AccessoriesSize accessoriesSize) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();

			String sql = "delete from tbFabricsAccessoriesTransaction where autoId = '"+accessoriesSize.getAutoId()+"'";
			if(session.createSQLQuery(sql).executeUpdate()==1) {
				tx.commit();
				return "Successful";
			}

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}



	@Override
	public List<AccessoriesReturn> getAccessoriesReturnList() {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<AccessoriesReturn> datalist=new ArrayList<AccessoriesReturn>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select fri.AutoId,fri.transactionId,(select convert(varchar,fri.date,103))as returnDate,fri.supplierId,s.name as supplierName,fri.remarks,fri.createBy \r\n" + 
					"from tbAccessoriesReturnInfo fri\r\n" + 
					"left join tbSupplier s\r\n" + 
					"on fri.supplierId = s.id";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();

				datalist.add(new AccessoriesReturn(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString()));				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}

	@Override
	public AccessoriesReturn getAccessoriesReturnInfo(String returnTransactionId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesReturn accessoriesReturn = null;
		AccessoriesSize tempSize;
		List<AccessoriesSize> accessoriesSizeList = new ArrayList<AccessoriesSize>();	

		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select far.autoId,far.transactionId,far.purchaseOrder,far.styleId,sc.styleNo,far.styleItemId,id.itemname,far.colorId as itemColorId ,ic.Colorname as itemColorName,cItemId as accessoriesId,ai.ItemName as accessoriesName,far.itemColorId as accessoriesColorId,isnull(fc.Colorname,'') as accessoriesColorName,far.sizeId,isnull(ss.sizeName,'') as sizeName,far.unitId,u.unitname,unitQty,rackName,BinName,far.userId\r\n" + 
					",dbo.accessoriesBalanceQty(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.sizeId,far.departmentId) as balanceQty \r\n" + 
					",(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.dItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.ACCESSORIES_RECEIVE.getType()+"' and t.departmentId = far.departmentId) as previousReceiveQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.ACCESSORIES_RETURN.getType()+"' and t.departmentId = far.departmentId) as returnQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.FABRICS_ISSUE.getType()+"' and t.departmentId = far.departmentId) as issueQty\r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on far.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on far.styleItemId = id.itemid\r\n" + 
					"left join tbStyleSize ss\r\n" + 
					"on far.sizeId = ss.id \r\n" + 
					"left join tbunits u\r\n" + 
					"on far.unitId = u.Unitid\r\n" + 
					"left join TbAccessoriesItem ai\r\n" + 
					"on far.cItemId = ai.itemid\r\n" + 
					"left join tbColors ic\r\n" + 
					"on far.colorId = ic.ColorId\r\n" + 
					"left join tbColors fc\r\n" + 
					"on far.itemColorId = fc.ColorId\r\n" + 
					"where transactionId = '"+returnTransactionId+"' and transactionType='"+StoreTransaction.ACCESSORIES_RETURN.getType()+"'";	
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempSize = new AccessoriesSize(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(),element[7].toString(), element[8].toString(),element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(), element[15].toString(),element[16].toString(),0.0, Double.valueOf(element[17].toString()), element[18].toString(), element[19].toString(),1);
				tempSize.setBalanceQty(Double.valueOf(element[21].toString()));
				tempSize.setPreviousReceiveQty(Double.valueOf(element[22].toString()));
				tempSize.setReturnQty(Double.valueOf(element[23].toString()));
				tempSize.setIssueQty(Double.valueOf(element[24].toString()));

				accessoriesSizeList.add(tempSize);				
			}
			sql = "select fri.autoId,fri.transactionId,(select convert(varchar,fri.date,103))as date,fri.supplierId,s.name as supplierName,fri.remarks,fri.createBy \r\n" + 
					"from tbAccessoriesReturnInfo fri\r\n" + 
					"left join tbSupplier s\r\n" + 
					"on fri.supplierId = s.id\r\n" + 
					"where fri.transactionId = '"+returnTransactionId+"'";		
			list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				Object[] element = (Object[]) list.get(0);

				accessoriesReturn = new AccessoriesReturn(element[0].toString(),element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString());
				accessoriesReturn.setAccessoriesSizeList(accessoriesSizeList);
			}
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return accessoriesReturn;
	}

	@Override
	public AccessoriesReceive getAccessoriesReceiveInfoForReturn(String transactionId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesReceive accessoriesReceive = null;
		List<AccessoriesSize> accessoriesSizeList = new ArrayList<AccessoriesSize>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select autoId,isnull(returnTransactionId,'')as returnTransactionId,sizeId,supplierSizeId,frd.unitId,u.unitname,rollQty,qcPassedQty,rackName,BinName,qcPassedType,isReturn,createBy  \r\n" + 
					"from tbAccessoriesSizeDetails frd\r\n" + 
					"left join tbunits u\r\n" + 
					"on frd.unitId = u.Unitid\r\n" + 
					"where transactionId = '"+transactionId+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				boolean isReturn = element[11].toString().equals("1")?true:false;
				//accessoriesSizeList.add(new AccessoriesSize(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), Double.valueOf(element[6].toString()), Double.valueOf(element[7].toString()), element[8].toString(), element[9].toString(), Integer.valueOf(element[10].toString()), isReturn, element[12].toString()));				
			}

			sql = "select autoId,transactionId,grnNo,(select convert(varchar,grnDate,103))as grnDate,location,fIndent.PurchaseOrder,fIndent.styleId,sc.StyleNo,fIndent.itemid,id.itemname,fIndent.accessoriescolor as colorId,c.Colorname,fri.accessoriesId,ISNULL(fi.ItemName,'')as accessoriesName,indentId,fri.unitId,grnQty,noOfSize,fri.supplierId,fri.buyer,fri.challanNo,fri.challanDate,fri.remarks,fri.preperedBy,fri.createBy \r\n" + 
					"from tbAccessoriesReceiveInfo fri\r\n" + 
					"left join tbAccessoriesIndent fIndent\r\n" + 
					"on fri.indentId = fIndent.id\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on fIndent.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on fIndent.itemid = id.itemid\r\n" + 
					"left join tbColors c\r\n" + 
					"on fIndent.itemcolor = c.ColorId\r\n" + 
					"left join TbAccessoriesItem fi\r\n" + 
					"on fIndent.accessoriesId = fi.id where fri.transactionId = '"+transactionId+"'";		
			list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				Object[] element = (Object[]) list.get(0);
				boolean  isReturn = element[24].toString()=="1"?true:false;
				accessoriesReceive = new AccessoriesReceive();
				accessoriesReceive.setAccessoriesSizeList(accessoriesSizeList);
			}
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return accessoriesReceive;

	}

	@Override
	public List<AccessoriesSize> getAvailableAccessoriesSizeListInDepartment(String departmentId) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesSize tempSize;
		List<AccessoriesSize> datalist=new ArrayList<AccessoriesSize>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			
			String sql="select fat.autoId,'supplierId' as supplierId,'supplierName' as supplierName,fat.purchaseOrder,fat.styleId,fat.StyleNo,fat.styleItemId,fat.itemname,fat.colorid,c.colorName,fat.dItemId as accessoriesId,ai.ItemName as accessoriesName,fat.itemColorId,isnull(fat.Colorname,'') as accessoriesColor,fat.sizeId,ss.sizeName,fat.unitId,u.unitname,dbo.accessoriesBalanceQty(fat.purchaseOrder,fat.styleid,fat.styleItemId,fat.colorId,fat.dItemId,fat.ItemcolorId,fat.sizeId,fat.departmentId) as balanceQty,fat.rackName,fat.binName,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.dItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = fat.transactionType and t.itemColorId = fat.itemColorId and t.dItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as ReceiveQty,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.cItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = '11' and t.itemColorId = fat.itemColorId and t.cItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as IssueQty,\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.cItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = '10' and t.itemColorId = fat.itemColorId and t.cItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as previousReturnQty\n" + 
					"from tbFabricsAccessoriesTransaction as fat\n" + 
					"left join tbColors c\n" + 
					"on fat.colorId = c.ColorId\n" + 
					"left join TbAccessoriesItem ai\n" + 
					"on fat.dItemId = ai.itemid\n" + 
					"left join tbStyleSize ss\n" + 
					"on fat.sizeId = ss.id \n" + 
					"left join tbunits u\n" + 
					"on fat.unitId = u.Unitid \n" + 
					"where fat.transactionType = '"+StoreTransaction.ACCESSORIES_RECEIVE.getType()+"' and fat.departmentId = '"+departmentId+"' \n" + 
					"and dbo.accessoriesBalanceQty(fat.purchaseOrder,fat.styleid,fat.styleItemId,fat.colorId,fat.dItemId,fat.ItemcolorId,fat.sizeId,fat.departmentId) > 0 \n" + 
					"order by fat.purchaseOrder,fat.styleId,fat.styleItemId,fat.colorId,fat.dItemId,fat.itemColorId\n" + 
					"";
			
/*			String sql = "select fat.autoId,'supplierId' as supplierId,'supplierName' as supplierName,fat.purchaseOrder,fat.styleId,sc.StyleNo,fat.styleItemId,id.itemname,fat.colorid,c.colorName,fat.dItemId as accessoriesId,ai.ItemName as accessoriesName,fat.itemColorId,isnull(ic.Colorname,'') as accessoriesColor,fat.sizeId,ss.sizeName,fat.unitId,u.unitname,dbo.accessoriesBalanceQty(fat.purchaseOrder,fat.styleid,fat.styleItemId,fat.colorId,fat.dItemId,fat.ItemcolorId,fat.sizeId,fat.departmentId) as balanceQty,fat.rackName,fat.binName,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.dItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = fat.transactionType and t.itemColorId = fat.itemColorId and t.dItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as ReceiveQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.cItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = '"+StoreTransaction.ACCESSORIES_ISSUE.getType()+"' and t.itemColorId = fat.itemColorId and t.cItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as IssueQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.cItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = '"+StoreTransaction.ACCESSORIES_RETURN.getType()+"' and t.itemColorId = fat.itemColorId and t.cItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as previousReturnQty\r\n" + 
					"from tbFabricsAccessoriesTransaction as fat\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on fat.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on fat.styleItemId = id.itemid\r\n" + 
					"left join tbColors c\r\n" + 
					"on fat.colorId = c.ColorId\r\n" + 
					"left join TbAccessoriesItem ai\r\n" + 
					"on fat.dItemId = ai.itemid\r\n" + 
					"left join tbColors ic\r\n" + 
					"on fat.itemColorId = ic.ColorId\r\n" + 
					"left join tbStyleSize ss\r\n" + 
					"on fat.sizeId = ss.id \r\n" + 
					"left join tbunits u\r\n" + 
					"on fat.unitId = u.Unitid \r\n" + 
					"where fat.transactionType = '"+StoreTransaction.ACCESSORIES_RECEIVE.getType()+"' and fat.departmentId = '"+departmentId+"' \r\n"+
					"and dbo.accessoriesBalanceQty(fat.purchaseOrder,fat.styleid,fat.styleItemId,fat.colorId,fat.dItemId,fat.ItemcolorId,fat.sizeId,fat.departmentId) > 0 \r\n"+
					"order by fat.purchaseOrder,fat.styleId,fat.styleItemId,fat.colorId,fat.dItemId,fat.itemColorId";		
			*/
			
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempSize = new AccessoriesSize(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(),element[15].toString(),element[16].toString(),element[17].toString(), Double.valueOf(element[18].toString()),element[19].toString(),element[20].toString());
				tempSize.setPreviousReceiveQty(Double.valueOf(element[21].toString()));
				tempSize.setIssueQty(Double.valueOf(element[22].toString()));
				tempSize.setReturnQty(Double.valueOf(element[23].toString()));
				datalist.add(tempSize);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}



	@Override
	public List<AccessoriesSize> getRequisitionAccessoriesList(String cuttingEntryId, String departmentId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesSize tempSize;
		List<AccessoriesSize> datalist=new ArrayList<AccessoriesSize>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select cis.cuttingEntryId,cis.purchaseOrder,cis.StyleId,sc.StyleNo,cis.ItemId,id.itemname,cid.ColorId,c.Colorname,ai.accessoriesItemId,accItem.itemname as accessoriesItemName,ai.IndentColorId,accColor.Colorname as accColorName,count(cis.purchaseOrder) as count\r\n" + 
					"from TbCuttingInformationSummary cis \r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on cis.StyleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on cis.ItemId = id.itemid\r\n" + 
					"join TbCuttingInformationDetails cid\r\n" + 
					"on cis.CuttingEntryId = cid.CuttingEntryId and  cid.status = 2\r\n" + 
					"left join tbColors c\r\n" + 
					"on cid.ColorId = c.colorId\r\n" + 
					"join tbAccessoriesIndent ai\r\n" + 
					"on cis.purchaseOrder = ai.PurchaseOrder and cis.StyleId = ai.styleid and cis.ItemId = ai.Itemid and cid.ColorId = ai.ColorId\r\n" + 
					"left join TbAccessoriesItem accItem\r\n" + 
					"on ai.accessoriesItemId = accItem.itemid\r\n" + 
					"left join tbColors accColor\r\n" + 
					"on ai.IndentColorId = accColor.ColorId\r\n" + 
					"where cis.CuttingEntryId = '"+cuttingEntryId+"'\r\n" + 
					"group by cis.CuttingEntryId,cis.purchaseOrder,cis.StyleId,sc.StyleNo,cis.ItemId,id.itemname,cid.ColorId,c.colorName,ai.accessoriesItemId,accItem.itemname,ai.IndentColorId,accColor.Colorname";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempSize = new AccessoriesSize(element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), "", "", "", "",0.0, 0.0, 0.0, 0.0);

				datalist.add(tempSize);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}

	@Override
	public List<AccessoriesSize> getRequisitionAccessoriesSizeList(String cuttingEntryId,String accessoriesIdList,String departmentId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesSize tempSize;
		List<AccessoriesSize> datalist=new ArrayList<AccessoriesSize>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select cis.cuttingEntryId,'supplierId' as supplierId,'supplierName' as supplierName,cis.purchaseOrder,cis.StyleId,sc.StyleNo,cis.ItemId,id.itemname,cid.ColorId,c.Colorname,ai.accessoriesItemId,accItem.itemname as accessoriesItemName,ai.IndentColorId,accColor.Colorname accColorName,ai.size,ss.sizeName,ai.unitId,u.unitname,sv.sizeQuantity*ai.ReqPerPices as qty,'1' as rackName,'1' as binName,dbo.accessoriesBalanceQty(cis.purchaseOrder,cis.styleid,cis.ItemId,cid.colorId,ai.accessoriesItemId,ai.IndentColorId,ai.size,'"+departmentId+"') as balanceQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.sizeId = ai.size and t.dItemId = ai.accessoriesItemId and t.transactionType = '"+StoreTransaction.ACCESSORIES_RECEIVE.getType()+"' and t.itemColorId = ai.IndentColorId and t.dItemId = ai.accessoriesItemId and t.colorId = cid.ColorId and t.styleItemId = cis.itemId and t.styleId = cis.styleId and t.purchaseOrder = cis.purchaseOrder) as ReceiveQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.sizeId = ai.size and t.cItemId = ai.accessoriesItemId and t.transactionType = '"+StoreTransaction.ACCESSORIES_ISSUE.getType()+"' and t.itemColorId = ai.IndentColorId and t.cItemId = ai.accessoriesItemId and t.colorId = cid.ColorId and t.styleItemId = cis.itemId and t.styleId = cis.styleId and t.purchaseOrder = cis.purchaseOrder) as IssueQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.sizeId = ai.size and t.cItemId = ai.accessoriesItemId and t.transactionType = '"+StoreTransaction.ACCESSORIES_RETURN.getType()+"' and t.itemColorId = ai.IndentColorId and t.cItemId = ai.accessoriesItemId and t.colorId = cid.ColorId and t.styleItemId = cis.itemId and t.styleId = cis.styleId and t.purchaseOrder = cis.purchaseOrder) as previousReturnQty\r\n" + 
					"from TbCuttingInformationSummary cis \r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on cis.StyleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on cis.ItemId = id.itemid\r\n" + 
					"join TbCuttingInformationDetails cid\r\n" + 
					"on cis.CuttingEntryId = cid.CuttingEntryId and  cid.status = 2\r\n" + 
					"left join tbColors c\r\n" + 
					"on cid.ColorId = c.colorId\r\n" + 
					"join tbAccessoriesIndent ai\r\n" + 
					"on cis.purchaseOrder = ai.PurchaseOrder and cis.StyleId = ai.styleid and cis.ItemId = ai.Itemid and cid.ColorId = ai.ColorId\r\n" + 
					"left join TbAccessoriesItem accItem\r\n" + 
					"on ai.accessoriesItemId = accItem.itemid\r\n" + 
					"left join tbColors accColor\r\n" + 
					"on ai.IndentColorId = accColor.ColorId\r\n" + 
					"left join tbStyleSize ss\r\n" + 
					"on ai.size = ss.id\r\n" + 
					"left join tbSizeValues sv\r\n" + 
					"on cid.cuttingAutoId = sv.linkedAutoId and sv.type = '"+SizeValuesType.CUTTING_QTY.getType()+"' and ai.size=sv.sizeId\r\n" + 
					"left join tbunits u\r\n" + 
					"on ai.UnitId = u.Unitid\r\n" + 
					"where cis.CuttingEntryId = '"+cuttingEntryId+"' and ai.accessoriesItemId in ("+accessoriesIdList+")";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempSize = new AccessoriesSize(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(),element[15].toString(),element[16].toString(),element[17].toString(), Double.valueOf(element[21].toString()),element[19].toString(),element[20].toString());
				tempSize.setUnitQty(Double.valueOf(element[18].toString()));
				tempSize.setPreviousReceiveQty(Double.valueOf(element[22].toString()));
				tempSize.setIssueQty(Double.valueOf(element[23].toString()));
				tempSize.setReturnQty(Double.valueOf(element[24].toString()));
				datalist.add(tempSize);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}



	@Override
	public boolean submitAccessoriesIssue(AccessoriesIssue accessoriesIssue) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.ACCESSORIES_ISSUE;
		ItemType itemType = ItemType.ACCESSORIES;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select (isnull(max(transactionId),0)+1) as maxId from tbAccessoriesIssueInfo";
			List<?> list = session.createSQLQuery(sql).list();
			String transactionId="0";
			if(list.size()>0) {
				transactionId = list.get(0).toString();
			}
			sql="insert into tbAccessoriesIssueInfo (transactionId"
					+ ",date"
					+ ",issuedTo"
					+ ",receiveBy"
					+ ",remarks"
					+ ",departmentId"
					+ ",status"
					+ ",entryTime"
					+ ",createBy) values("
					+ "'"+transactionId+"'"
					+ ",'"+accessoriesIssue.getIssueDate()+"'"
					+ ",'"+accessoriesIssue.getIssuedTo()+"'"
					+ ",'"+accessoriesIssue.getReceiveBy()+"'"
					+ ",'"+accessoriesIssue.getRemarks()+"'"
					+ ",'"+accessoriesIssue.getDepartmentId()+"'"
					+ ",'1',"
					+ "CURRENT_TIMESTAMP,'"+accessoriesIssue.getUserId()+"');";
			session.createSQLQuery(sql).executeUpdate();

			for (AccessoriesSize roll : accessoriesIssue.getAccessoriesSizeList()) {
				sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleNo,styleItemId,itemName,colorId,colorName,itemColorId,transactionId,transactionType,itemType,sizeId,unitId,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
						"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getStyleNo()+"','"+roll.getItemId()+"','"+roll.getItemName()+"','"+roll.getItemColorId()+"','"+roll.getItemColor()+"','"+roll.getAccessoriesColorId()+"','"+transactionId+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getSizeId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','0','"+roll.getAccessoriesId()+"','"+accessoriesIssue.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+accessoriesIssue.getUserId()+"');";		
				session.createSQLQuery(sql).executeUpdate();
			}
			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public boolean editAccessoriesIssue(AccessoriesIssue accessoriesIssue) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.ACCESSORIES_ISSUE;
		ItemType itemType = ItemType.ACCESSORIES;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="update tbaccessoriesIssueInfo set "
					+ "date = '"+accessoriesIssue.getIssueDate()+"'"
					+ ",issuedTo = '"+accessoriesIssue.getIssuedTo()+"'"
					+ ",receiveBy = '"+accessoriesIssue.getReceiveBy()+"'"
					+ ",remarks = '"+accessoriesIssue.getRemarks()+"'"
					+ ",entryTime = CURRENT_TIMESTAMP"
					+ ",createBy = '"+accessoriesIssue.getUserId()+"' where transactionId='"+accessoriesIssue.getTransactionId()+"';";

			session.createSQLQuery(sql).executeUpdate();


			if(accessoriesIssue.getAccessoriesSizeList() != null) {
				for (AccessoriesSize roll : accessoriesIssue.getAccessoriesSizeList()) {
					sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,sizeId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
							"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getItemId()+"','"+roll.getItemColorId()+"','"+roll.getAccessoriesColorId()+"','"+accessoriesIssue.getTransactionId()+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getSizeId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getUnitQty()+"','0','"+roll.getAccessoriesId()+"','"+accessoriesIssue.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+accessoriesIssue.getUserId()+"');";		
					session.createSQLQuery(sql).executeUpdate();
				}
			}


			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public String editIssuedSizeInTransaction(AccessoriesSize accessoriesSize) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();
			String sql = "select far.autoId,dbo.accessoriesBalanceQtyExceptAutoId(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.sizeId,far.departmentId,far.autoId) as balanceQty \r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"where far.autoId = '"+accessoriesSize.getAutoId()+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()>0) {
				Object[] element = (Object[]) list.get(0);
				if(Double.valueOf(element[1].toString())>=accessoriesSize.getUnitQty()) {
					sql = "update tbFabricsAccessoriesTransaction set unitQty = '"+accessoriesSize.getUnitQty()+"',qty = '"+accessoriesSize.getUnitQty()+"' where autoId = '"+accessoriesSize.getAutoId()+"'";
					if(session.createSQLQuery(sql).executeUpdate()==1) {
						tx.commit();
						return "Successful";
					}
				}else {
					tx.commit();
					return "Return Qty Exist";
				}
			}			

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}

	@Override
	public String deleteIssuedSizeFromTransaction(AccessoriesSize accessoriesSize) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();

			String sql = "delete from tbFabricsAccessoriesTransaction where autoId = '"+accessoriesSize.getAutoId()+"'";
			if(session.createSQLQuery(sql).executeUpdate()==1) {
				tx.commit();
				return "Successful";
			}

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}
	@Override
	public List<AccessoriesSize> getIssuedAccessoriesSizeListInDepartment(String departmentId, String returnDepartmentId) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesSize tempSize;
		List<AccessoriesSize> datalist=new ArrayList<AccessoriesSize>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select sil.*,\r\n" + 
					"isnull(dbo.accessoriesIssueReturnedQty('"+departmentId+"','"+returnDepartmentId+"',sil.sizeId),0) as previousIssueReturnQty\r\n" + 
					"from dbo.issuedAccessoriesItemList('"+departmentId+"','"+returnDepartmentId+"','"+StoreTransaction.ACCESSORIES_ISSUE.getType()+"') as sil\r\n"
					+ "where (sil.issuedQty - isnull(dbo.accessoriesIssueReturnedQty('"+departmentId+"','"+returnDepartmentId+"',sil.sizeId),0))>0";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempSize = new AccessoriesSize(element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(), element[15].toString(), element[16].toString(),0.0, Double.valueOf(element[17].toString()), 0, Double.valueOf(element[18].toString()));			
				datalist.add(tempSize);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}



	@Override
	public List<AccessoriesIssue> getAccessoriesIssueList() {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesIssue tempIssue;
		List<AccessoriesIssue> datalist=new ArrayList<AccessoriesIssue>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select fii.AutoId,fii.transactionId,(select convert(varchar,fii.date,103))as issuedDate,fii.issuedTo,fii.receiveBy,fii.remarks,fii.createBy,di.DepartmentName,fi.FactoryName\r\n" + 
					"from tbAccessoriesIssueInfo fii\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on fii.issuedTo = di.DepartmentId\r\n" + 
					"left join TbFactoryInfo fi\r\n" + 
					"on di.FactoryId = fi.FactoryId";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempIssue = new AccessoriesIssue(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString());
				tempIssue.setIssuedDepartmentName(element[7].toString()+"("+element[8].toString()+")");
				datalist.add(tempIssue);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}
	@Override
	public AccessoriesIssue getAccessoriesIssueInfo(String issueTransactionId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesIssue accessoriesIssue = null;
		AccessoriesSize tempSize;
		List<AccessoriesSize> accessoriesSizeList = new ArrayList<AccessoriesSize>();	

		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select far.autoId,far.transactionId,far.purchaseOrder,far.styleId,sc.styleNo,far.styleItemId,id.itemname,far.colorId as itemColorId ,ic.Colorname as itemColorName,cItemId as accessoriesId,ai.ItemName as accessoriesName,far.itemColorId as accessoriesColorId,isnull(fc.Colorname,'') as accessoriesColorName,far.sizeId,ss.sizeName,far.unitId,u.unitname,far.qty,rackName,BinName,far.userId\r\n" + 
					",dbo.accessoriesBalanceQty(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.sizeId,far.departmentId) as balanceQty \r\n" +
					",(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.dItemId and far.itemColorId = t.itemColorId and far.sizeId=t.sizeId and t.transactionType = '"+StoreTransaction.ACCESSORIES_RECEIVE.getType()+"' and t.departmentId = far.departmentId) as previousReceiveQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and far.sizeId=t.sizeId and t.transactionType = '"+StoreTransaction.ACCESSORIES_RETURN.getType()+"' and t.departmentId = far.departmentId) as returnQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and far.sizeId=t.sizeId and t.transactionType = '"+StoreTransaction.ACCESSORIES_ISSUE.getType()+"' and t.departmentId = far.departmentId) as issueQty\r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on far.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on far.styleItemId = id.itemid\r\n" + 
					"left join tbStyleSize ss\r\n" + 
					"on far.sizeId = ss.id \r\n" + 
					"left join tbunits u\r\n" + 
					"on far.unitId = u.Unitid\r\n" + 
					"left join TbAccessoriesItem ai\r\n" + 
					"on far.cItemId = ai.itemid\r\n" + 
					"left join tbColors ic\r\n" + 
					"on far.colorId = ic.ColorId\r\n" + 
					"left join tbColors fc\r\n" + 
					"on far.itemColorId = fc.ColorId\r\n" + 
					"where transactionId = '"+issueTransactionId+"' and transactionType='"+StoreTransaction.ACCESSORIES_ISSUE.getType()+"'";	
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempSize = new AccessoriesSize(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(),element[7].toString(), element[8].toString(),element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(), element[15].toString(),element[16].toString(),0.0, Double.valueOf(element[17].toString()), element[18].toString(), element[19].toString(),1);
				tempSize.setBalanceQty(Double.valueOf(element[21].toString()));
				tempSize.setPreviousReceiveQty(Double.valueOf(element[22].toString()));
				tempSize.setReturnQty(Double.valueOf(element[23].toString()));
				tempSize.setIssueQty(Double.valueOf(element[24].toString()));

				accessoriesSizeList.add(tempSize);				
			}
			sql = "select fii.AutoId,fii.transactionId,(select convert(varchar,fii.date,103))as issuedDate,fii.issuedTo,fii.receiveBy,fii.remarks,fii.createBy,di.DepartmentName,fi.FactoryName\r\n" + 
					"from tbAccessoriesIssueInfo fii\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on fii.issuedTo = di.DepartmentId\r\n" + 
					"left join TbFactoryInfo fi\r\n" + 
					"on di.FactoryId = fi.FactoryId\r\n" + 
					"where fii.transactionId = '"+issueTransactionId+"'\r\n" + 
					"";		
			list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				Object[] element = (Object[]) list.get(0);

				accessoriesIssue = new AccessoriesIssue(element[0].toString(), element[1].toString(), element[2].toString(),  element[3].toString(),  element[4].toString(),  element[5].toString(), element[6].toString());
				accessoriesIssue.setAccessoriesSizeList(accessoriesSizeList);
			}

			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return accessoriesIssue;
	}



	@Override
	public boolean submitAccessoriesIssueReturn(AccessoriesIssueReturn accessoriesIssueReturn) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.ACCESSORIES_ISSUE_RETURN;
		ItemType itemType = ItemType.ACCESSORIES;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select (isnull(max(transactionId),0)+1) as maxId from tbAccessoriesIssueReturnInfo";
			List<?> list = session.createSQLQuery(sql).list();
			String transactionId="0";
			if(list.size()>0) {
				transactionId = list.get(0).toString();
			}

			sql="insert into tbAccessoriesIssueReturnInfo (transactionId"
					+ ",date"
					+ ",issueReturnFrom"
					+ ",receiveFrom"
					+ ",remarks"
					+ ",departmentId"
					+ ",entryTime"
					+ ",createBy) values("
					+ "'"+transactionId+"'"
					+ ",'"+accessoriesIssueReturn.getIssueReturnDate()+"'"
					+ ",'"+accessoriesIssueReturn.getIssueReturnFrom()+"'"
					+ ",'"+accessoriesIssueReturn.getReceiveFrom()+"'"
					+ ",'"+accessoriesIssueReturn.getRemarks()+"'"
					+ ",'"+accessoriesIssueReturn.getDepartmentId()+"',"
					+ "CURRENT_TIMESTAMP,'"+accessoriesIssueReturn.getUserId()+"');";
			session.createSQLQuery(sql).executeUpdate();

			for (AccessoriesSize roll : accessoriesIssueReturn.getAccessoriesSizeList()) {
				sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleNo,styleItemId,ItemName,colorId,colorName,itemColorId,transactionId,transactionType,itemType,sizeId,unitId,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
						"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getStyleNo()+"','"+roll.getItemId()+"','"+roll.getItemName()+"','"+roll.getItemColorId()+"','"+roll.getItemColor()+"','"+roll.getAccessoriesColorId()+"','"+transactionId+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getSizeId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getAccessoriesId()+"','0','"+accessoriesIssueReturn.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+accessoriesIssueReturn.getUserId()+"');";		
				session.createSQLQuery(sql).executeUpdate();
			}

			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public boolean editAccessoriesIssueReturn(AccessoriesIssueReturn accessoriesIssueReturn) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.ACCESSORIES_ISSUE_RETURN;
		ItemType itemType = ItemType.ACCESSORIES;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="update tbaccessoriesIssueReturnInfo set "
					+ "date = '"+accessoriesIssueReturn.getIssueReturnDate()+"'"
					+ ",issueReturnFrom = '"+accessoriesIssueReturn.getIssueReturnFrom()+"'"
					+ ",receiveFrom = '"+accessoriesIssueReturn.getReceiveFrom()+"'"
					+ ",remarks = '"+accessoriesIssueReturn.getRemarks()+"'"
					+ ",entryTime = CURRENT_TIMESTAMP"
					+ ",createBy = '"+accessoriesIssueReturn.getUserId()+"' where transactionId='"+accessoriesIssueReturn.getTransactionId()+"';";

			session.createSQLQuery(sql).executeUpdate();


			if(accessoriesIssueReturn.getAccessoriesSizeList() != null) {
				for (AccessoriesSize roll : accessoriesIssueReturn.getAccessoriesSizeList()) {
					sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,sizeId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
							"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getItemId()+"','"+roll.getItemColorId()+"','"+roll.getAccessoriesColorId()+"','"+accessoriesIssueReturn.getTransactionId()+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getSizeId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getUnitQty()+"','"+roll.getAccessoriesId()+"','0','"+accessoriesIssueReturn.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+accessoriesIssueReturn.getUserId()+"');";		
					session.createSQLQuery(sql).executeUpdate();
				}
			}


			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public String editIssueReturndSizeInTransaction(AccessoriesSize accessoriesSize) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();
			String sql = "select fat.transactionId,dbo.accessoriesIssuedQty(firi.departmentId,firi.issueReturnFrom,fat.sizeId) as issuedQty,dbo.accessoriesIssueReturnedQty(firi.departmentId,firi.issueReturnFrom,fat.sizeId) as returnedQty from tbFabricsAccessoriesTransaction fat\r\n" + 
					"left join tbaccessoriesIssueReturnInfo firi\r\n" + 
					"on fat.transactionId = firi.transactionId where fat.autoId='"+accessoriesSize.getAutoId()+"' and (dbo.accessoriesIssuedQty(firi.departmentId,firi.issueReturnFrom,fat.sizeId)-dbo.accessoriesIssueReturnedQty(firi.departmentId,firi.issueReturnFrom,fat.sizeId)-"+accessoriesSize.getUnitQty()+")>=0";		
			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()>0) {
				Object[] element = (Object[]) list.get(0);
				if(Double.valueOf(element[1].toString())>=accessoriesSize.getUnitQty()) {
					sql = "update tbFabricsAccessoriesTransaction set unitQty = '"+accessoriesSize.getUnitQty()+"',qty = '"+accessoriesSize.getUnitQty()+"',rackName='"+accessoriesSize.getRackName()+"',binName='"+accessoriesSize.getBinName()+"' where autoId = '"+accessoriesSize.getAutoId()+"'";
					if(session.createSQLQuery(sql).executeUpdate()==1) {
						tx.commit();
						return "Successful";
					}
				}else {
					tx.commit();
					return "Return Qty Exist";
				}
			}			

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}

	@Override
	public String deleteIssueReturndSizeFromTransaction(AccessoriesSize accessoriesSize) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();

			String sql = "delete from tbFabricsAccessoriesTransaction where autoId = '"+accessoriesSize.getAutoId()+"'";
			if(session.createSQLQuery(sql).executeUpdate()==1) {
				tx.commit();
				return "Successful";
			}

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}

	@Override
	public List<AccessoriesIssueReturn> getAccessoriesIssueReturnList() {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesIssueReturn tempIssueReturn;
		List<AccessoriesIssueReturn> datalist=new ArrayList<AccessoriesIssueReturn>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select firi.AutoId,firi.transactionId,(select convert(varchar,firi.date,103))as issuedDate,firi.issueReturnFrom,firi.receiveFrom,firi.remarks,firi.createBy,di.DepartmentName,fi.FactoryName\r\n" + 
					"from tbAccessoriesIssueReturnInfo firi\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on firi.issueReturnFrom = di.DepartmentId\r\n" + 
					"left join TbFactoryInfo fi\r\n" + 
					"on di.FactoryId = fi.FactoryId";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempIssueReturn = new AccessoriesIssueReturn(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString());
				tempIssueReturn.setIssueReturnDepartmentName(element[7].toString()+"("+element[8].toString()+")");
				datalist.add(tempIssueReturn);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}

	@Override
	public AccessoriesIssueReturn getAccessoriesIssueReturnInfo(String issueReturnTransectionId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesIssueReturn accessoriesIssueReturn = null;
		AccessoriesSize tempSize;
		List<AccessoriesSize> accessoriesSizeList = new ArrayList<AccessoriesSize>();	

		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select far.autoId,far.transactionId,far.purchaseOrder,far.styleId,sc.styleNo,far.styleItemId,id.itemname,far.colorId as itemColorId ,ic.Colorname as itemColorName,dItemId as accessoriesId,fi.ItemName as accessoriesName,far.itemColorId as accessoriesColorId,isnull(fc.Colorname,'') as accessoriesColorName,far.sizeId,ss.sizeName,far.unitId,u.unitname,far.qty,rackName,BinName,far.userId,\r\n" + 
					"dbo.accessoriesissuedQty(firi.departmentId,firi.issueReturnFrom,far.sizeId) as issuedQty,\r\n" + 
					"dbo.accessoriesIssueReturnedQty(firi.departmentId,firi.issueReturnFrom,far.sizeId) as returnedQty\r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"left join tbAccessoriesIssueReturnInfo firi\r\n" + 
					"on far.transactionId = firi.transactionId\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on far.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on far.styleItemId = id.itemid\r\n" + 
					"left join tbStyleSize ss\r\n" + 
					"on far.sizeId = ss.id \r\n" + 
					"left join tbunits u\r\n" + 
					"on far.unitId = u.Unitid\r\n" + 
					"left join TbAccessoriesItem fi\r\n" + 
					"on far.dItemId = fi.itemid\r\n" + 
					"left join tbColors ic\r\n" + 
					"on far.colorId = ic.ColorId\r\n" + 
					"left join tbColors fc\r\n" + 
					"on far.itemColorId = fc.ColorId\r\n" + 
					"where far.transactionId = '"+issueReturnTransectionId+"' and far.transactionType='"+StoreTransaction.ACCESSORIES_ISSUE_RETURN.getType()+"'";	
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempSize = new AccessoriesSize(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(),element[7].toString(), element[8].toString(),element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(), element[15].toString(),element[16].toString(),0.0, Double.valueOf(element[17].toString()), element[18].toString(), element[19].toString(),1);
				tempSize.setIssueQty(Double.valueOf(element[21].toString()));
				tempSize.setPreviousReturnQty(Double.valueOf(element[22].toString()));
				accessoriesSizeList.add(tempSize);				
			}
			sql = "select fii.AutoId,fii.transactionId,(select convert(varchar,fii.date,103))as issuedDate,fii.issueReturnFrom,fii.receiveFrom,fii.remarks,fii.createBy,di.DepartmentName,fi.FactoryName\r\n" + 
					"from tbAccessoriesIssueReturnInfo fii\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on fii.issueReturnFrom = di.DepartmentId\r\n" + 
					"left join TbFactoryInfo fi\r\n" + 
					"on di.FactoryId = fi.FactoryId\r\n" + 
					"where fii.transactionId = '"+issueReturnTransectionId+"'\r\n" + 
					"";		
			list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				Object[] element = (Object[]) list.get(0);

				accessoriesIssueReturn = new AccessoriesIssueReturn(element[0].toString(), element[1].toString(), element[2].toString(),  element[3].toString(),  element[4].toString(),  element[5].toString(), element[6].toString());
				accessoriesIssueReturn.setAccessoriesSizeList(accessoriesSizeList);
			}
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return accessoriesIssueReturn;
	}


	@Override
	public boolean submitAccessoriesTransferOut(AccessoriesTransferOut accessoriesTransferOut) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.ACCESSORIES_TRANSFER_OUT;
		ItemType itemType = ItemType.ACCESSORIES;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select (isnull(max(transactionId),0)+1) as maxId from tbAccessoriesTransferOutInfo";
			List<?> list = session.createSQLQuery(sql).list();
			String transactionId="0";
			if(list.size()>0) {
				transactionId = list.get(0).toString();
			}

			sql="insert into tbAccessoriesTransferOutInfo (transactionId"
					+ ",date"
					+ ",transferTo"
					+ ",receiveBy"
					+ ",remarks"
					+ ",departmentId"
					+ ",entryTime"
					+ ",createBy) values("
					+ "'"+transactionId+"'"
					+ ",'"+accessoriesTransferOut.getTransferDate()+"'"
					+ ",'"+accessoriesTransferOut.getTransferTo()+"'"
					+ ",'"+accessoriesTransferOut.getReceiveBy()+"'"
					+ ",'"+accessoriesTransferOut.getRemarks()+"'"
					+ ",'"+accessoriesTransferOut.getDepartmentId()+"',"
					+ "CURRENT_TIMESTAMP,'"+accessoriesTransferOut.getUserId()+"');";
			session.createSQLQuery(sql).executeUpdate();

			for (AccessoriesSize roll : accessoriesTransferOut.getAccessoriesSizeList()) {
				sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleNo,styleItemId,itemName,colorId,colorName,itemColorId,transactionId,transactionType,itemType,sizeId,unitId,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
						"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getStyleNo()+"','"+roll.getItemId()+"','"+roll.getItemName()+"','"+roll.getItemColorId()+"','"+roll.getItemColor()+"','"+roll.getAccessoriesColorId()+"','"+transactionId+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getSizeId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','0','"+roll.getAccessoriesId()+"','"+accessoriesTransferOut.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+accessoriesTransferOut.getUserId()+"');";		
				session.createSQLQuery(sql).executeUpdate();
			}

			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public boolean editAccessoriesTransferOut(AccessoriesTransferOut accessoriesTransferOut) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.ACCESSORIES_TRANSFER_OUT;
		ItemType itemType = ItemType.ACCESSORIES;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="update tbaccessoriesTransferOutInfo set "
					+ "date = '"+accessoriesTransferOut.getTransferDate()+"'"
					+ ",transferTo = '"+accessoriesTransferOut.getTransferTo()+"'"
					+ ",receiveBy = '"+accessoriesTransferOut.getReceiveBy()+"'"
					+ ",remarks = '"+accessoriesTransferOut.getRemarks()+"'"
					+ ",entryTime = CURRENT_TIMESTAMP"
					+ ",createBy = '"+accessoriesTransferOut.getUserId()+"' where transactionId='"+accessoriesTransferOut.getTransactionId()+"';";

			session.createSQLQuery(sql).executeUpdate();


			if(accessoriesTransferOut.getAccessoriesSizeList() != null) {
				for (AccessoriesSize roll : accessoriesTransferOut.getAccessoriesSizeList()) {
					sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,sizeId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
							"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getItemId()+"','"+roll.getItemColorId()+"','"+roll.getAccessoriesColorId()+"','"+accessoriesTransferOut.getTransactionId()+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getSizeId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getUnitQty()+"','0','"+roll.getAccessoriesId()+"','"+accessoriesTransferOut.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+accessoriesTransferOut.getUserId()+"');";		
					session.createSQLQuery(sql).executeUpdate();
				}
			}


			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public String editTransferOutdSizeInTransaction(AccessoriesSize accessoriesSize) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();
			String sql = "select far.autoId,dbo.accessoriesBalanceQtyExceptAutoId(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.sizeId,far.departmentId,far.autoId) as balanceQty \r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"where far.autoId = '"+accessoriesSize.getAutoId()+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()>0) {
				Object[] element = (Object[]) list.get(0);
				if(Double.valueOf(element[1].toString())>=accessoriesSize.getUnitQty()) {
					sql = "update tbFabricsAccessoriesTransaction set unitQty = '"+accessoriesSize.getUnitQty()+"',qty = '"+accessoriesSize.getUnitQty()+"' where autoId = '"+accessoriesSize.getAutoId()+"'";
					if(session.createSQLQuery(sql).executeUpdate()==1) {
						tx.commit();
						return "Successful";
					}
				}else {
					tx.commit();
					return "Return Qty Exist";
				}
			}			

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}

	@Override
	public String deleteTransferOutdSizeFromTransaction(AccessoriesSize accessoriesSize) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();

			String sql = "delete from tbFabricsAccessoriesTransaction where autoId = '"+accessoriesSize.getAutoId()+"'";
			if(session.createSQLQuery(sql).executeUpdate()==1) {
				tx.commit();
				return "Successful";
			}

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}

	@Override
	public List<AccessoriesTransferOut> getAccessoriesTransferOutList() {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesTransferOut tempIssue;
		List<AccessoriesTransferOut> datalist=new ArrayList<AccessoriesTransferOut>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select fii.AutoId,fii.transactionId,(select convert(varchar,fii.date,103))as issuedDate,fii.transferTo,fii.receiveBy,fii.remarks,fii.createBy,di.DepartmentName,fi.FactoryName\r\n" + 
					"from tbAccessoriesTransferOutInfo fii\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on fii.transferTo = di.DepartmentId\r\n" + 
					"left join TbFactoryInfo fi\r\n" + 
					"on di.FactoryId = fi.FactoryId";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempIssue = new AccessoriesTransferOut(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString());
				tempIssue.setTransferDepartmentName(element[7].toString()+"("+element[8].toString()+")");
				datalist.add(tempIssue);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}

	@Override
	public AccessoriesTransferOut getAccessoriesTransferOutInfo(String transferOutTransectionId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesTransferOut accessoriesTransfer = null;
		AccessoriesSize tempSize;
		List<AccessoriesSize> accessoriesSizeList = new ArrayList<AccessoriesSize>();	

		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select far.autoId,far.transactionId,far.purchaseOrder,far.styleId,sc.styleNo,far.styleItemId,id.itemname,far.colorId as itemColorId ,ic.Colorname as itemColorName,cItemId as accessoriesId,fi.ItemName as accessoriesName,far.itemColorId as accessoriesColorId,isnull(fc.Colorname,'') as accessoriesColorName,far.sizeId,ss.sizeName,far.unitId,u.unitname,far.qty,rackName,BinName,far.userId\r\n" + 
					",dbo.accessoriesBalanceQty(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.sizeId,far.departmentId) as balanceQty \r\n" +
					",(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.dItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.ACCESSORIES_RECEIVE.getType()+"' and t.departmentId = far.departmentId) as previousReceiveQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.ACCESSORIES_RETURN.getType()+"' and t.departmentId = far.departmentId) as returnQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.ACCESSORIES_ISSUE.getType()+"' and t.departmentId = far.departmentId) as issueQty\r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on far.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on far.styleItemId = id.itemid\r\n" + 
					"left join tbStyleSize ss\r\n" + 
					"on far.sizeId = ss.id\r\n" + 
					"left join tbunits u\r\n" + 
					"on far.unitId = u.Unitid\r\n" + 
					"left join TbAccessoriesItem fi\r\n" + 
					"on far.cItemId = fi.itemid\r\n" + 
					"left join tbColors ic\r\n" + 
					"on far.colorId = ic.ColorId\r\n" + 
					"left join tbColors fc\r\n" + 
					"on far.itemColorId = fc.ColorId\r\n" + 
					"where transactionId = '"+transferOutTransectionId+"' and transactionType='"+StoreTransaction.ACCESSORIES_TRANSFER_OUT.getType()+"'";	
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempSize = new AccessoriesSize(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(),element[7].toString(), element[8].toString(),element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(), element[15].toString(),element[16].toString(),0.0, Double.valueOf(element[17].toString()), element[18].toString(), element[19].toString(),1);
				tempSize.setBalanceQty(Double.valueOf(element[21].toString()));
				tempSize.setPreviousReceiveQty(Double.valueOf(element[22].toString()));
				tempSize.setReturnQty(Double.valueOf(element[23].toString()));
				tempSize.setIssueQty(Double.valueOf(element[24].toString()));

				accessoriesSizeList.add(tempSize);				
			}
			sql = "select fii.AutoId,fii.transactionId,(select convert(varchar,fii.date,103))as issuedDate,fii.transferTo,fii.receiveBy,fii.remarks,fii.createBy,di.DepartmentName,fi.FactoryName\r\n" + 
					"from tbAccessoriesTransferOutInfo fii\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on fii.transferTo = di.DepartmentId\r\n" + 
					"left join TbFactoryInfo fi\r\n" + 
					"on di.FactoryId = fi.FactoryId\r\n" + 
					"where fii.transactionId = '"+transferOutTransectionId+"'\r\n" + 
					"";		
			list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				Object[] element = (Object[]) list.get(0);

				accessoriesTransfer = new AccessoriesTransferOut(element[0].toString(), element[1].toString(), element[2].toString(),  element[3].toString(),  element[4].toString(),  element[5].toString(), element[6].toString());
				accessoriesTransfer.setAccessoriesSizeList(accessoriesSizeList);
			}

			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return accessoriesTransfer;
	}

	@Override
	public List<AccessoriesSize> getTransferInAccessoriesSizeList(String departmentId, String transferDepartmentId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesSize tempSize;
		List<AccessoriesSize> datalist=new ArrayList<AccessoriesSize>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select ftoi.transferTo,di.DepartmentName,fat.purchaseOrder,fat.styleId,sc.styleNo,fat.styleItemId,id.itemname,fat.colorId as itemColorId ,ic.Colorname as itemColorName,fat.cItemId as fabricsId,ai.ItemName as fabricsName,fat.itemColorId as fabricsColorId,isnull(fc.Colorname,'') as fabricsColorName,fat.sizeId,ss.sizeName,fat.unitId,u.unitname,\r\n" + 
					"(select ISNULL(sum(t.qty),0) from tbFabricsAccessoriesTransaction t where t.purchaseOrder=fat.purchaseOrder and t.styleId=fat.styleId and t.styleItemId=fat.styleItemId and t.cItemId=fat.cItemId and t.sizeId=fat.sizeId and t.transactionType='14') as transferOutQty, \n" + 
					"(select ISNULL(sum(t.qty),0) from tbFabricsAccessoriesTransaction t where t.purchaseOrder=fat.purchaseOrder and t.styleId=fat.styleId and t.styleItemId=fat.styleItemId and t.dItemId=fat.cItemId and t.sizeId=fat.sizeId and t.transactionType='13') as transferInQty, \n" + 
					"(select ISNULL(sum(t.qty),0) from tbFabricsAccessoriesTransaction t where t.purchaseOrder=fat.purchaseOrder and t.styleId=fat.styleId and t.styleItemId=fat.styleItemId and t.cItemId=fat.cItemId and t.sizeId=fat.sizeId and t.transactionType='14')-(select ISNULL(sum(t.qty),0) from tbFabricsAccessoriesTransaction t where t.purchaseOrder=fat.purchaseOrder and t.styleId=fat.styleId and t.styleItemId=fat.styleItemId and t.dItemId=fat.cItemId and t.sizeId=fat.sizeId and t.transactionType='13') as RemainTransferQty \n" + 
					"from tbAccessoriesTransferOutInfo ftoi\r\n" + 
					"left join tbFabricsAccessoriesTransaction fat\r\n" + 
					"on ftoi.transactionId = fat.transactionId and fat.transactionType = '"+StoreTransaction.ACCESSORIES_TRANSFER_OUT.getType()+"'\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on ftoi.transferTo = di.DepartmentId\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on fat.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on fat.styleItemId = id.itemid\r\n" + 
					"left join tbStyleSize ss\r\n" + 
					"on fat.sizeId = ss.id \r\n" + 
					"left join tbunits u\r\n" + 
					"on fat.unitId = u.Unitid\r\n" + 
					"left join TbAccessoriesItem ai\r\n" + 
					"on fat.cItemId = ai.itemid\r\n" + 
					"left join tbColors ic\r\n" + 
					"on fat.colorId = ic.ColorId\r\n" + 
					"left join tbColors fc\r\n" + 
					"on fat.itemColorId = fc.ColorId\r\n" + 
					" where ftoi.transferTo = '"+departmentId+"' and ftoi.departmentId = '"+transferDepartmentId+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempSize = new AccessoriesSize(element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(), element[15].toString(), element[16].toString(),Double.valueOf(element[17].toString()), Double.valueOf(element[18].toString()), Double.valueOf(element[19].toString()),"","");			
				tempSize.setRackName("1");
				tempSize.setBinName("1");
				datalist.add(tempSize);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}



	@Override
	public boolean submitAccessoriesTransferIn(AccessoriesTransferIn accessoriesTransferIn) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.ACCESSORIES_TRANSFER_IN;
		ItemType itemType = ItemType.ACCESSORIES;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select (isnull(max(transactionId),0)+1) as maxId from tbAccessoriesTransferInInfo";
			List<?> list = session.createSQLQuery(sql).list();
			String transactionId="0";
			if(list.size()>0) {
				transactionId = list.get(0).toString();
			}

			sql="insert into tbAccessoriesTransferInInfo (transactionId"
					+ ",date"
					+ ",transferFrom"
					+ ",receiveFrom"
					+ ",remarks"
					+ ",departmentId"
					+ ",entryTime"
					+ ",createBy) values("
					+ "'"+transactionId+"'"
					+ ",'"+accessoriesTransferIn.getTransferDate()+"'"
					+ ",'"+accessoriesTransferIn.getTransferFrom()+"'"
					+ ",'"+accessoriesTransferIn.getReceiveFrom()+"'"
					+ ",'"+accessoriesTransferIn.getRemarks()+"'"
					+ ",'"+accessoriesTransferIn.getDepartmentId()+"',"
					+ "CURRENT_TIMESTAMP,'"+accessoriesTransferIn.getUserId()+"');";
			session.createSQLQuery(sql).executeUpdate();

			for (AccessoriesSize roll : accessoriesTransferIn.getAccessoriesSizeList()) {
				sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleNo,styleItemId,itemName,colorId,colorName,itemColorId,transactionId,transactionType,itemType,sizeId,unitId,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
						"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getStyleNo()+"','"+roll.getItemId()+"','"+roll.getItemName()+"','"+roll.getItemColorId()+"','"+roll.getItemColor()+"','"+roll.getAccessoriesColorId()+"','"+transactionId+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getSizeId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getAccessoriesId()+"','0','"+accessoriesTransferIn.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+accessoriesTransferIn.getUserId()+"');";		
				session.createSQLQuery(sql).executeUpdate();
			}

			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public boolean editAccessoriesTransferIn(AccessoriesTransferIn accessoriesTransferIn) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		StoreTransaction transaction = StoreTransaction.ACCESSORIES_TRANSFER_IN;
		ItemType itemType = ItemType.ACCESSORIES;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="update tbaccessoriesTransferInInfo set "
					+ "date = '"+accessoriesTransferIn.getTransferDate()+"'"
					+ ",transferFrom = '"+accessoriesTransferIn.getTransferFrom()+"'"
					+ ",receiveFrom = '"+accessoriesTransferIn.getReceiveFrom()+"'"
					+ ",remarks = '"+accessoriesTransferIn.getRemarks()+"'"
					+ ",entryTime = CURRENT_TIMESTAMP"
					+ ",createBy = '"+accessoriesTransferIn.getUserId()+"' where transactionId='"+accessoriesTransferIn.getTransactionId()+"';";

			session.createSQLQuery(sql).executeUpdate();


			if(accessoriesTransferIn.getAccessoriesSizeList() != null) {
				for (AccessoriesSize roll : accessoriesTransferIn.getAccessoriesSizeList()) {
					sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,sizeId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
							"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getItemId()+"','"+roll.getItemColorId()+"','"+roll.getAccessoriesColorId()+"','"+accessoriesTransferIn.getTransactionId()+"','"+transaction.getType()+"','"+itemType.getType()+"','"+roll.getSizeId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getUnitQty()+"','"+roll.getAccessoriesId()+"','0','"+accessoriesTransferIn.getDepartmentId()+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+accessoriesTransferIn.getUserId()+"');";		
					session.createSQLQuery(sql).executeUpdate();
				}
			}


			tx.commit();

			return true;
		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}

	@Override
	public String editTransferIndSizeInTransaction(AccessoriesSize accessoriesSize) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();
			String sql = "select far.autoId,dbo.accessoriesBalanceQtyExceptAutoId(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.sizeId,far.departmentId,far.autoId) as balanceQty \r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"where far.autoId = '"+accessoriesSize.getAutoId()+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()>0) {
				Object[] element = (Object[]) list.get(0);

				sql = "update tbFabricsAccessoriesTransaction set unitQty = '"+accessoriesSize.getUnitQty()+"',qty = '"+accessoriesSize.getUnitQty()+"' where autoId = '"+accessoriesSize.getAutoId()+"'";
				if(session.createSQLQuery(sql).executeUpdate()==1) {
					tx.commit();
					return "Successful";
				}

			}			

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}

	@Override
	public String deleteTransferIndSizeFromTransaction(AccessoriesSize accessoriesSize) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();

			String sql = "delete from tbFabricsAccessoriesTransaction where autoId = '"+accessoriesSize.getAutoId()+"'";
			if(session.createSQLQuery(sql).executeUpdate()==1) {
				tx.commit();
				return "Successful";
			}

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return "Something Wrong";
	}

	@Override
	public List<AccessoriesTransferIn> getAccessoriesTransferInList() {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesTransferIn tempIssue;
		List<AccessoriesTransferIn> datalist=new ArrayList<AccessoriesTransferIn>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select fii.AutoId,fii.transactionId,(select convert(varchar,fii.date,103))as issuedDate,fii.transferFrom,fii.receiveFrom,fii.remarks,fii.createBy,di.DepartmentName,fi.FactoryName\r\n" + 
					"from tbAccessoriesTransferInInfo fii\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on fii.transferFrom = di.DepartmentId\r\n" + 
					"left join TbFactoryInfo fi\r\n" + 
					"on di.FactoryId = fi.FactoryId";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempIssue = new AccessoriesTransferIn(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString());
				tempIssue.setTransferDepartmentName(element[7].toString()+"("+element[8].toString()+")");
				datalist.add(tempIssue);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}

	@Override
	public AccessoriesTransferIn getAccessoriesTransferInInfo(String transferOutTransectionId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		AccessoriesTransferIn accessoriesTransfer = null;
		AccessoriesSize tempSize;
		List<AccessoriesSize> accessoriesSizeList = new ArrayList<AccessoriesSize>();	

		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select far.autoId,far.transactionId,far.purchaseOrder,far.styleId,sc.styleNo,far.styleItemId,id.itemname,far.colorId as itemColorId ,ic.Colorname as itemColorName,dItemId as accessoriesId,fi.ItemName as accessoriesName,far.itemColorId as accessoriesColorId,isnull(fc.Colorname,'') as accessoriesColorName,far.sizeId,ss.sizeName,far.unitId,u.unitname,far.qty,rackName,BinName,far.userId\r\n" + 
					",dbo.accessoriesBalanceQty(far.purchaseOrder,far.styleId,far.styleItemId,far.colorId,far.cItemId,far.itemColorId,far.sizeId,far.departmentId) as balanceQty \r\n" +
					",(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.dItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.ACCESSORIES_RECEIVE.getType()+"' and t.departmentId = far.departmentId) as previousReceiveQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.ACCESSORIES_RETURN.getType()+"' and t.departmentId = far.departmentId) as returnQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where far.purchaseOrder=t.purchaseOrder and far.styleId = t.styleId and far.styleItemId= t.styleItemId and far.colorId = t.colorId and far.cItemId = t.cItemId and far.itemColorId = t.itemColorId and t.transactionType = '"+StoreTransaction.ACCESSORIES_ISSUE.getType()+"' and t.departmentId = far.departmentId) as issueQty\r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on far.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on far.styleItemId = id.itemid\r\n" + 
					"left join tbStyleSize ss\r\n" + 
					"on far.sizeId = ss.id \r\n" + 
					"left join tbunits u\r\n" + 
					"on far.unitId = u.Unitid\r\n" + 
					"left join TbAccessoriesItem fi\r\n" + 
					"on far.dItemId = fi.itemid\r\n" + 
					"left join tbColors ic\r\n" + 
					"on far.colorId = ic.ColorId\r\n" + 
					"left join tbColors fc\r\n" + 
					"on far.itemColorId = fc.ColorId\r\n" + 
					"where transactionId = '"+transferOutTransectionId+"' and transactionType='"+StoreTransaction.ACCESSORIES_TRANSFER_IN.getType()+"'";	
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempSize = new AccessoriesSize(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(),element[7].toString(), element[8].toString(),element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(), element[15].toString(),element[16].toString(),0.0, Double.valueOf(element[17].toString()), element[18].toString(), element[19].toString(),1);
				tempSize.setBalanceQty(Double.valueOf(element[21].toString()));
				tempSize.setPreviousReceiveQty(Double.valueOf(element[22].toString()));
				tempSize.setReturnQty(Double.valueOf(element[23].toString()));
				tempSize.setIssueQty(Double.valueOf(element[24].toString()));

				accessoriesSizeList.add(tempSize);				
			}
			sql = "select fii.AutoId,fii.transactionId,(select convert(varchar,fii.date,103))as issuedDate,fii.transferFrom,fii.receiveFrom,fii.remarks,fii.createBy,di.DepartmentName,fi.FactoryName\r\n" + 
					"from tbAccessoriesTransferInInfo fii\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on fii.transferFrom = di.DepartmentId\r\n" + 
					"left join TbFactoryInfo fi\r\n" + 
					"on di.FactoryId = fi.FactoryId\r\n" + 
					"where fii.transactionId = '"+transferOutTransectionId+"'\r\n" + 
					"";		
			list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				Object[] element = (Object[]) list.get(0);

				accessoriesTransfer = new AccessoriesTransferIn(element[0].toString(), element[1].toString(), element[2].toString(),  element[3].toString(),  element[4].toString(),  element[5].toString(), element[6].toString());
				accessoriesTransfer.setAccessoriesSizeList(accessoriesSizeList);
			}

			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return accessoriesTransfer;
	}

	@Override
	public boolean isStoreGenralItemExist(StoreGeneralCategory v) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select ItemName from tbStoreItemInformation where ItemName='"+v.getItemName()+"' and ItemId != '"+v.getItemId()+"'";

			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()>0) return true;
			tx.commit();
		}
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return false;
	}



	@Override
	public boolean saveGeneralItem(StoreGeneralCategory v) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		try{
			tx=session.getTransaction();
			tx.begin();
			String sql="insert into tbStoreItemInformation(ItemName,CatagoryId,UnitId,PcsBuyPrice,OpeningStock,StockLimit,Date,entrytime,UserId) values('"+v.getItemName()+"','"+v.getCategoryId()+"','"+v.getUnitId()+"','"+v.getBuyPrice()+"','"+v.getOpeningStock()+"','"+v.getStockLimit()+"',current_timestamp,current_timestamp,'"+v.getUserId()+"')";
			session.createSQLQuery(sql).executeUpdate();
			tx.commit();
			return true;
		}
		catch(Exception ee){

			if (tx != null) {
				tx.rollback();
				return false;
			}
			ee.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}



	@Override
	public List<StoreGeneralCategory> getStoreGeneralItemList() {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<StoreGeneralCategory> datalist=new ArrayList<StoreGeneralCategory>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select a.ItemId,a.ItemName,ISNULL((select headTitle from tbStoreItemCatagory where headId=a.CatagoryId),'') as Category,a.CatagoryId,a.UnitId,a.PcsBuyPrice,a.OpeningStock,a.StockLimit from tbStoreItemInformation a order by Category";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();

				datalist.add(new StoreGeneralCategory(element[0].toString(),element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString()));				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}



	@Override
	public boolean editGeneralItem(StoreGeneralCategory v) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		try{
			tx=session.getTransaction();
			tx.begin();
			String sql="update tbStoreItemInformation set  ItemName='"+v.getItemName()+"',CatagoryId='"+v.getCategoryId()+"',UnitId='"+v.getUnitId()+"',PcsBuyPrice='"+v.getBuyPrice()+"',OpeningStock='"+v.getOpeningStock()+"',StockLimit='"+v.getStockLimit()+"',UserId='"+v.getUserId()+"',entrytime=current_timestamp where ItemId='"+v.getItemId()+"'";
			session.createSQLQuery(sql).executeUpdate();
			tx.commit();
			return true;
		}
		catch(Exception ee){

			if (tx != null) {
				tx.rollback();
				return false;
			}
			ee.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}



	@Override
	public boolean addGeneralReceivedItem(StoreGeneralReceived v) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		try{
			tx=session.getTransaction();
			tx.begin();


			int exist=0;
			String sql="select ItemId from TbStoreTransectionDetails where ItemId='"+v.getItemId()+"' and InvoiceNo='"+v.getInvoiceNo()+"'";
			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()>0) {
				exist=1;
			}


			if(exist==0) {
				double TotalPrice=Double.parseDouble(v.getQty())*Double.parseDouble(v.getPirce());
				sql="insert into TbStoreTransectionDetails(itemId,unit,qty,buyPrice,discount,totalPrice,type,invoiceNo,status,Date,entrytime,UserId) "
						+ "values('"+v.getItemId()+"','"+v.getUnitId()+"','"+v.getQty()+"','"+v.getPirce()+"','0','"+TotalPrice+"','"+v.getType()+"','"+v.getInvoiceNo()+"','0',current_timestamp,current_timestamp,'"+v.getUserId()+"')";
				session.createSQLQuery(sql).executeUpdate();
			}
			else {
				double TotalPrice=Double.parseDouble(v.getQty())*Double.parseDouble(v.getPirce());
				sql="update TbStoreTransectionDetails set qty=qty+'"+v.getQty()+"',totalPrice=totalPrice+'"+TotalPrice+"',entryTime=current_timestamp where invoiceNo='"+v.getInvoiceNo()+"' ";
				session.createSQLQuery(sql).executeUpdate();
			}
			tx.commit();
			return true;
		}
		catch(Exception ee){

			if (tx != null) {
				tx.rollback();
				return false;
			}
			ee.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}



	@Override
	public String getMaxInvoiceId(String type) {
		String InvoiceId="";
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select (isnull(max(invoiceNo),0)+1) as maxId from TbStoreTransectionInvoice where Type='"+type+"'";
			List<?> list = session.createSQLQuery(sql).list();
			if(list.size()>0) {
				InvoiceId = list.get(0).toString();
			}

			tx.commit();
		}
		catch(Exception ee){

			if (tx != null) {
				tx.rollback();
			}
			ee.printStackTrace();
		}

		finally {
			session.close();
		}

		return InvoiceId;
	}



	@Override
	public List<StoreGeneralReceived> getStoreGeneralReceivedItemList(String invoiceNo, String type) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<StoreGeneralReceived> datalist=new ArrayList<StoreGeneralReceived>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select a.autoId,(select ItemName from tbStoreItemInformation where ItemId=a.ItemId) as ItemName,(select UnitName from tbunits where UnitId=a.unit) as UnitName,a.Qty,a.buyPrice,a.TotalPrice from TbStoreTransectionDetails a where a.invoiceNo='"+invoiceNo+"' and a.type='"+type+"' and a.status='0'";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();

				datalist.add(new StoreGeneralReceived(element[0].toString(),element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString()));				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}



	@Override
	public boolean confrimtoreGeneralReceivedItemt(StoreGeneralReceived v) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="insert into TbStoreTransectionInvoice"
					+ "("
					+ "invoiceNo,"
					+ "PersionId,"
					+ "PersionName,"
					+ "ChallanNo,"
					+ "type,"
					+ "amount,"
					+ "netAmount,"
					+ "discountPer,"
					+ "discountManual,"
					+ "discount,"
					+ "paid,"
					+ "cash,"
					+ "card,"
					+ "card_type,"
					+ "p_type,"
					+ "remark,"
					+ "Date,"
					+ "entrytime,"
					+ "UserId"
					+ ") "
					+ "values('"+v.getInvoiceNo()+"',"
					+ "'"+v.getSupplierId()+"',"
					+ "'',"
					+ "'"+v.getChallanNo()+"',"
					+ "'"+v.getType()+"',"
					+ "'0',"
					+ "'0',"
					+ "'0',"
					+ "'0',"
					+ "'0',"
					+ "'0',"
					+ "'0',"
					+ "'0',"
					+ "'0',"
					+ "'',"
					+ "'',"
					+"current_timestamp,current_timestamp,'"+v.getUserId()+"')";
			session.createSQLQuery(sql).executeUpdate();


			sql="update TbStoreTransectionDetails set status='1' where invoiceNo='"+v.getInvoiceNo()+"' ";
			session.createSQLQuery(sql).executeUpdate();

			tx.commit();
			return true;
		}
		catch(Exception ee){

			if (tx != null) {
				tx.rollback();
				return false;
			}
			ee.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}



	@Override
	public List<StoreGeneralReceived> getStoreGeneralReceivedIList(String type) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<StoreGeneralReceived> datalist=new ArrayList<StoreGeneralReceived>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select InvoiceNo,(select Name from tbSupplier where id=PersionId)as SupplierName,ChallanNo,(select convert(varchar,date,103))as Date  from TbStoreTransectionInvoice where type='"+type+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();

				datalist.add(new StoreGeneralReceived(element[0].toString(),element[1].toString(), element[2].toString(), element[3].toString()));				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}



	@Override
	public List<CuttingFabricsUsed> getCuttingUsedFabricsList(String cuttingEntryId) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<CuttingFabricsUsed> datalist=new ArrayList<CuttingFabricsUsed>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select a.CuttingEntryId,a.purchaseOrder,a.StyleId,a.ItemId,s.StyleNo,i.itemname,b.ColorId,b.UsedFabrics,c.Colorname,f.fabricsid,fb.ItemName as FabricsItem,f.fabricscolor,fc.Colorname as FabricsItemColor,f.unitId,u.unitname from TbCuttingInformationSummary a join TbCuttingInformationDetails b on a.CuttingEntryId=b.CuttingEntryId join tbFabricsIndent f on f.PurchaseOrder=a.purchaseOrder and f.styleId=a.StyleId and f.itemid=a.ItemId join TbStyleCreate s on a.StyleId=s.StyleId join tbItemDescription i on a.ItemId=i.itemId join tbColors c on c.ColorId=b.ColorId join TbFabricsItem fb on f.fabricsid=fb.id join tbColors fc on fc.ColorId=f.fabricscolor join tbunits u on u.Unitid=f.unitId where a.CuttingEntryId='"+cuttingEntryId+"' and b.Type='Cutting' group by a.CuttingEntryId,b.ColorId,b.UsedFabrics,c.Colorname,a.purchaseOrder,a.StyleId,a.ItemId,s.StyleNo,i.itemname,f.fabricsid,f.unitId,u.unitname,fb.ItemName,f.fabricscolor,fc.Colorname";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();

				datalist.add(new CuttingFabricsUsed(element[0].toString(),element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(),element[11].toString(), element[12].toString(),element[13].toString(),element[14].toString()));				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}



	@Override
	public boolean sendCuttingFabricsRequistion(CuttingFabricsUsed v) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="";
			String resultValue=v.getResultList().substring(v.getResultList().indexOf("[")+1, v.getResultList().indexOf("]"));
			System.out.println("resultValue "+resultValue);

			StringTokenizer token=new StringTokenizer(resultValue, ",");
			while(token.hasMoreTokens()) {

				String secondToken=token.nextToken();
				StringTokenizer token2=new StringTokenizer(secondToken, "*");
				while(token2.hasMoreTokens()) {
					String fabricsId=token2.nextToken();
					String colorId=token2.nextToken();
					String unitId=token2.nextToken();
					String usedFabrics=token2.nextToken();
					String requistionReq=token2.nextToken();

					if(!requistionReq.equals("0")){
						sql="insert into TbCuttingRequisitionDetailsv1"
								+ "("
								+ "CuttingEntryId,"
								+ "colorId,"
								+ "fabricsId,"
								+ "unitId,"
								+ "requisitionQuantity,"
								+ "requistionFlag,"
								+ "entrytime,"
								+ "UserId"
								+ ") "
								+ "values('"+v.getCuttingEntryId()+"',"
								+ "'"+colorId+"',"
								+ "'"+fabricsId+"',"
								+ "'"+unitId+"',"
								+ "'"+usedFabrics+"',"
								+ "'"+requistionReq+"',"
								+"current_timestamp,'"+v.getUserId()+"')";
						session.createSQLQuery(sql).executeUpdate();
					}

				}


			}


			sql="update TbCuttingInformationSummary set RequistionFlag='1' where CuttingEntryId='"+v.getCuttingEntryId()+"' ";
			session.createSQLQuery(sql).executeUpdate();

			tx.commit();
			return true;
		}
		catch(Exception ee){

			if (tx != null) {
				tx.rollback();
				return false;
			}
			ee.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}



	@Override
	public List<FabricsRoll> getCuttingUsedFabricsRequisitionList(String cuttingEntryId,String departmentId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsRoll tempRoll;
		List<FabricsRoll> datalist=new ArrayList<FabricsRoll>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select fat.autoId,'supplierId' as supplierId,'supplierName' as supplierName,a.purchaseOrder,a.StyleId,sc.StyleNo,a.ItemId,id.itemname,fat.colorid,c.colorName,fat.dItemId as fabricsId,fi.ItemName as fabricsName,fat.itemColorId,ic.Colorname as fabricsColor,fat.rollId,frinfo.supplierRollId,fi.unitId,u.unitname,dbo.fabricsBalanceQty(fat.purchaseOrder,fat.styleid,fat.styleItemId,fat.colorId,fat.dItemId,fat.ItemcolorId,fat.rollId,fat.departmentId) as balanceQty,fat.rackName,fat.binName,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.dItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = fat.transactionType and t.itemColorId = fat.itemColorId and t.dItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as ReceiveQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.cItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = '"+StoreTransaction.FABRICS_ISSUE.getType()+"' and t.itemColorId = fat.itemColorId and t.cItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as IssueQty,\r\n" + 
					"(select isnull(sum(qty),0) from tbFabricsAccessoriesTransaction t where t.cItemId = fat.dItemId and t.transactionId = fat.transactionId and t.transactionType = '"+StoreTransaction.FABRICS_RETURN.getType()+"' and t.itemColorId = fat.itemColorId and t.cItemId = fat.dItemId and t.colorId = fat.colorId and t.styleItemId = fat.styleItemId and t.styleId = fat.styleId and t.purchaseOrder = fat.purchaseOrder) as previousReturnQty,b.requisitionQuantity\r\n" + 
					"from TbCuttingInformationSummary a \r\n" + 
					"join TbCuttingRequisitionDetailsv1 b \r\n" + 
					"on a.CuttingEntryId=b.CuttingEntryId\r\n" + 
					"join tbFabricsAccessoriesTransaction fat\r\n" + 
					"on a.purchaseOrder = fat.purchaseOrder and a.StyleId = fat.styleId and a.ItemId = fat.styleItemId and b.colorId = fat.colorId and fat.dItemId = b.fabricsId and fat.departmentId = '"+departmentId+"'\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on fat.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on fat.styleItemId = id.itemid\r\n" + 
					"left join tbColors c\r\n" + 
					"on fat.colorId = c.ColorId\r\n" + 
					"left join TbFabricsItem fi\r\n" + 
					"on fat.dItemId = fi.id\r\n" + 
					"left join tbColors ic\r\n" + 
					"on fat.itemColorId = ic.ColorId\r\n" + 
					"left join tbfabricsRollInfo frinfo\r\n" + 
					"on fat.rollId = frinfo.rollId \r\n" + 
					"left join tbunits u\r\n" + 
					"on fi.unitId = u.Unitid\r\n" + 
					"where a.CuttingEntryId='"+cuttingEntryId+"' and a.RequistionFlag='1'\r\n" + 
					"and dbo.fabricsBalanceQty(fat.purchaseOrder,fat.styleid,fat.styleItemId,fat.colorId,fat.dItemId,fat.ItemcolorId,fat.rollId,fat.departmentId) > 0 \r\n" + 
					"order by fat.purchaseOrder,fat.styleId,fat.styleItemId,fat.colorId,fat.dItemId,fat.itemColorId";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				tempRoll = new FabricsRoll(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(),element[15].toString(),element[16].toString(),element[17].toString(), Double.valueOf(element[18].toString()),element[19].toString(),element[20].toString());
				tempRoll.setPreviousReceiveQty(Double.valueOf(element[21].toString()));
				tempRoll.setIssueQty(Double.valueOf(element[22].toString()));
				tempRoll.setReturnQty(Double.valueOf(element[23].toString()));
				datalist.add(tempRoll);				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}


	@Override
	public boolean updateFabricRequisitionStatus(String requisitionNo, String requisitionStatus) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		try{
			tx=session.getTransaction();
			tx.begin();
			String sql="update TbCuttingInformationSummary set RequistionFlag='"+requisitionStatus+"' where CuttingEntryId='"+requisitionNo+"' ";
			session.createSQLQuery(sql).executeUpdate();
			tx.commit();
			return true;
		}
		catch(Exception ee){

			if (tx != null) {
				tx.rollback();
				return false;
			}
			ee.printStackTrace();
		}

		finally {
			session.close();
		}

		return false;
	}



	@Override
	public List<PendingTransaction> getPendingTransactionList(String departmentId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<PendingTransaction> datalist=new ArrayList<PendingTransaction>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select fii.transactionId,fii.departmentId,isnull(di.DepartmentName,'') as departmentName,'Fabrics' as type,(select convert(varchar,fii.date,103))as date\r\n" + 
					"from tbFabricsIssueInfo fii\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on fii.departmentId = di.DepartmentId \r\n" + 
					"where fii.status = 1 and fii.issuedTo = '"+departmentId+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();

				datalist.add(new PendingTransaction(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString()));				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}



	@Override
	public boolean fabricsIssueReceive(String transactionId, String transactionType,String departmentId,String userId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		FabricsIssue fabricsIssue = null ;
		List<FabricsRoll> fabricsRollList = new ArrayList<FabricsRoll>();	

		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select far.autoId,far.transactionId,far.purchaseOrder,far.styleId,sc.styleNo,far.styleItemId,id.itemname,far.colorId as itemColorId ,ic.Colorname as itemColorName,cItemId as fabricsId,fi.ItemName as fabricsName,far.itemColorId as fabricsColorId,isnull(fc.Colorname,'') as fabricsColorName,far.rollId,fri.supplierRollId,far.unitId,u.unitname,unitQty,rackName,BinName,far.userId\r\n" + 
					"from tbFabricsAccessoriesTransaction far\r\n" + 
					"left join TbStyleCreate sc\r\n" + 
					"on far.styleId = sc.StyleId\r\n" + 
					"left join tbItemDescription id\r\n" + 
					"on far.styleItemId = id.itemid\r\n" + 
					"left join tbfabricsRollInfo fri\r\n" + 
					"on far.rollId = fri.rollId \r\n" + 
					"left join tbunits u\r\n" + 
					"on far.unitId = u.Unitid\r\n" + 
					"left join TbFabricsItem fi\r\n" + 
					"on far.cItemId = fi.id\r\n" + 
					"left join tbColors ic\r\n" + 
					"on far.colorId = ic.ColorId\r\n" + 
					"left join tbColors fc\r\n" + 
					"on far.itemColorId = fc.ColorId\r\n" + 
					"where transactionId = '"+transactionId+"' and transactionType='"+StoreTransaction.FABRICS_ISSUE.getType()+"'";	
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				fabricsRollList.add(new FabricsRoll(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(),element[7].toString(), element[8].toString(),element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[13].toString(), element[14].toString(), element[15].toString(),element[16].toString(),0.0, Double.valueOf(element[17].toString()), element[18].toString(), element[19].toString(),1));					
			}

			sql = "select fii.AutoId,fii.transactionId,(select convert(varchar,fii.date,103))as issuedDate,fii.issuedTo,fii.receiveBy,fii.remarks,fii.createBy,di.DepartmentName,fi.FactoryName,fii.departmentid\r\n" + 
					"from tbFabricsIssueInfo fii\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on fii.issuedTo = di.DepartmentId\r\n" + 
					"left join TbFactoryInfo fi\r\n" + 
					"on di.FactoryId = fi.FactoryId\r\n" + 
					"where fii.transactionId = '"+transactionId+"'\r\n" + 
					"";		
			list = session.createSQLQuery(sql).list();
			if(list.size()>0)
			{	
				Object[] element = (Object[]) list.get(0);

				fabricsIssue = new FabricsIssue(element[0].toString(), element[1].toString(), element[2].toString(),  element[3].toString(),  element[4].toString(),  element[5].toString(), element[6].toString());
				fabricsIssue.setDepartmentId(element[9].toString());
			}
			sql="select (isnull(max(transactionid),0)+1) as maxId from tbFabricsTransferInInfo";
			list = session.createSQLQuery(sql).list();
			transactionId="0";
			if(list.size()>0) {
				transactionId = list.get(0).toString();
			}

			sql="insert into tbFabricsTransferInInfo (transactionid,"
					+ "date,"	
					+ "transferFrom,"
					+ "receiveFrom,"
					+ "remarks,"
					+ "departmentId,"
					+ "entryTime,"
					+ "createBy) \r\n" + 
					"values('"+transactionId+"',"
					+ "'"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"',"
					+ "'"+fabricsIssue.getDepartmentId()+"',"
					+ "'"+fabricsIssue.getReceiveBy()+"',"
					+ "'"+fabricsIssue.getRemarks()+"',"
					+ "'"+fabricsIssue.getIssuedTo()+"',"
					+ "CURRENT_TIMESTAMP,"
					+ "'"+fabricsIssue.getUserId()+"') ;";
			session.createSQLQuery(sql).executeUpdate();


			for (FabricsRoll roll : fabricsRollList) {

				sql="insert into tbFabricsAccessoriesTransaction (purchaseOrder,styleId,styleItemId,colorId,itemColorId,transactionId,transactionType,itemType,rollId,unitId,unitQty,qty,dItemId,cItemId,departmentId,rackName,binName,entryTime,userId) \r\n" + 
						"values('"+roll.getPurchaseOrder()+"','"+roll.getStyleId()+"','"+roll.getItemId()+"','"+roll.getItemColorId()+"','"+roll.getFabricsColorId()+"','"+transactionId+"','"+StoreTransaction.FABRICS_TRANSFER_IN.getType()+"','"+ItemType.FABRICS.getType()+"','"+roll.getRollId()+"','"+roll.getUnitId()+"','"+roll.getUnitQty()+"','"+roll.getUnitQty()+"','"+roll.getFabricsId()+"','0','"+departmentId+"','"+roll.getRackName()+"','"+roll.getBinName()+"',CURRENT_TIMESTAMP,'"+userId+"');";		
				session.createSQLQuery(sql).executeUpdate();
			}

			sql = "update tbFabricsIssueInfo set status = '2' where transactionId = '"+transactionId+"'";
			session.createSQLQuery(sql).executeUpdate();

			tx.commit();


			return true;
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return false;
	}



	@Override
	public List<PendingTransaction> getPendingFabricsIssueList(String departmentId,String fromDate, String toDate, String itemType,
			String approveType) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<PendingTransaction> datalist=new ArrayList<PendingTransaction>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select fii.transactionId,fii.departmentId,isnull(di.DepartmentName,'') as departmentName,'Fabrics' as type,(select convert(varchar,fii.date,103))as date\r\n" + 
					"from tbFabricsIssueInfo fii\r\n" + 
					"left join TbDepartmentInfo di\r\n" + 
					"on fii.departmentId = di.DepartmentId \r\n" + 
					"where fii.status = '"+approveType+"' and fii.issuedTo = '"+departmentId+"' and fii.date between '"+fromDate+"' and'"+toDate+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				datalist.add(new PendingTransaction(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString()));				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}


	@Override
	public List<StockItem> getStockItemSummeryList(String fromDate,String toDate,String departmentId) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<StockItem> accessoriesSizeList = new ArrayList<StockItem>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select * from dbo.stockItemSummery('"+fromDate+"','"+toDate+"','"+departmentId+"')";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				if(element[20].toString().equals("1")) {
					accessoriesSizeList.add(new StockItem(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[13].toString(), element[14].toString(), element[15].toString(), element[16].toString(), Double.valueOf(element[17].toString()), element[20].toString()));
				}else if(element[20].toString().equals("2")) {
					accessoriesSizeList.add(new StockItem(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[15].toString(), element[16].toString(), Double.valueOf(element[17].toString()), element[20].toString()));
				}				
			}

			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return accessoriesSizeList;
	}


	@Override
	public List<StockItem> getStockItemDetailsList(String fromDate,String toDate,String departmentId,String itemGroup) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<StockItem> accessoriesSizeList = new ArrayList<StockItem>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select * from dbo.stockItemDetails('"+fromDate+"','"+toDate+"','"+departmentId+"')";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				if(element[28].toString().equals("1")) {
					accessoriesSizeList.add(new StockItem(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[13].toString(), element[14].toString(), element[15].toString(), element[16].toString(), Double.valueOf(element[17].toString()), Double.valueOf(element[18].toString()), Double.valueOf(element[19].toString()), Double.valueOf(element[20].toString()), Double.valueOf(element[21].toString()), Double.valueOf(element[22].toString()), Double.valueOf(element[23].toString()), Double.valueOf(element[24].toString()), Double.valueOf(element[25].toString()), element[28].toString()));
				}else if(element[28].toString().equals("2")) {
					accessoriesSizeList.add(new StockItem(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(), element[10].toString(), element[11].toString(), element[12].toString(), element[15].toString(), element[16].toString(), Double.valueOf(element[17].toString()), Double.valueOf(element[18].toString()), Double.valueOf(element[19].toString()), Double.valueOf(element[20].toString()), Double.valueOf(element[21].toString()), Double.valueOf(element[22].toString()), Double.valueOf(element[23].toString()), Double.valueOf(element[24].toString()), Double.valueOf(element[25].toString()), element[28].toString()));
				}				
			}

			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return accessoriesSizeList;
	}



	@Override
	public boolean storeFileUpload(String Filename, String pcname, String ipaddress,String purpose,String user,String buyerName, String purchaseOrder) {
		Session session=HibernateUtil.openSession();
		boolean fileinsert=false;
		Transaction tx=null;
		try{
			tx=session.getTransaction();
			tx.begin();

			if (!duplicateFile(user, Filename)) {
				String sql="insert into TbUploadFileLogInfo ( FileName, UploadBy, UploadIp, UploadMachine, Purpose, UploadDate, UploadEntryTime, buyerid, purchaseorder,Type) values('"+Filename+"','"+user+"','"+ipaddress+"','"+pcname+"','"+purpose+"',convert(varchar, getdate(), 23),CURRENT_TIMESTAMP,'"+buyerName+"','"+purchaseOrder+"','Store')";
				session.createSQLQuery(sql).executeUpdate();
				tx.commit();
				fileinsert= true;
			}
		}
		catch(Exception ee){

			if (tx != null) {
				tx.rollback();
				return false;
			}
			ee.printStackTrace();
		}

		finally {
			session.close();
		}
		session.close();
		return fileinsert;
	}


	public boolean duplicateFile(String user, String filename) {

		boolean exists=false;
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		List<SampleRequisitionItem> dataList=new ArrayList<SampleRequisitionItem>();
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select filename from TbUploadFileLogInfo where FileName like '"+filename+"' and uploadby='"+user+"'";
			System.out.println(sql);
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				//Object[] element = (Object[]) iter.next();							
				//dataList.add(new SampleRequisitionItem(element[0].toString(),element[1].toString(),element[2].toString(), element[3].toString(),element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(),element[10].toString(),element[11].toString(),element[12].toString(),element[13].toString(),element[14].toString()));
				exists=true;
			}


			tx.commit();
		}
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return exists;


	}



	@Override
	public String getAccessoriesReceiveGRN() {
		String grn="0";
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select (isnull(max(autoId),0)+1) as autoId from tbaccessoriesReceiveInfo";
			List<?> list = session.createSQLQuery(sql).list();
			String transactionId="0";
			if(list.size()>0) {
				grn = list.get(0).toString();
			}

		}
		catch(Exception e){

			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}

		finally {
			session.close();
		}

		return grn;
	}



	@Override
	public List<AccessoriesStock> getAccessoriesStockDetails() {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<AccessoriesStock> accessoriesSizeList = new ArrayList<AccessoriesStock>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select fat.purchaseOrder,fat.styleId,fat.styleNo,fat.styleItemId,fat.itemName,fat.colorId,fat.colorName,fat.sizeId,(select itemName from TbAccessoriesItem where itemid=fat.dItemId) as AccessoriesItem,(select sizeName from tbStyleSize where id=fat.sizeId) as SizeName,sum(fat.qty) as ReceivedQty,ISNULL((select sum(qty) from tbFabricsAccessoriesTransaction t where t.purchaseOrder=fat.purchaseOrder and t.styleId=fat.styleId and t.styleItemId=fat.styleItemId and t.colorId=fat.colorId and t.sizeId=fat.sizeId and t.transactionType='10'),0) as StoreReturnQty,ISNULL((select sum(qty) from tbFabricsAccessoriesTransaction t where t.purchaseOrder=fat.purchaseOrder and t.styleId=fat.styleId and t.styleItemId=fat.styleItemId and t.colorId=fat.colorId and t.sizeId=fat.sizeId and t.transactionType='11'),0) as StoreIssueQty,ISNULL((select sum(qty) from tbFabricsAccessoriesTransaction t where t.purchaseOrder=fat.purchaseOrder and t.styleId=fat.styleId and t.styleItemId=fat.styleItemId and t.colorId=fat.colorId and t.sizeId=fat.sizeId and t.transactionType='12'),0) as StoreIssueReturnQty,ISNULL((select sum(qty) from tbFabricsAccessoriesTransaction t where t.purchaseOrder=fat.purchaseOrder and t.styleId=fat.styleId and t.styleItemId=fat.styleItemId and t.colorId=fat.colorId and t.sizeId=fat.sizeId and t.transactionType='13'),0) as StoreTransferInQty,ISNULL((select sum(qty) from tbFabricsAccessoriesTransaction t where t.purchaseOrder=fat.purchaseOrder and t.styleId=fat.styleId and t.styleItemId=fat.styleItemId and t.colorId=fat.colorId and t.sizeId=fat.sizeId and t.transactionType='14'),0) as StoreTransferOutQty from tbFabricsAccessoriesTransaction fat where fat.transactionType='8' and fat.itemType='2' group by fat.purchaseOrder,fat.styleId,fat.styleNo,fat.styleItemId,fat.itemName,fat.colorId,fat.colorName,fat.sizeId,fat.dItemId,fat.sizeId order by purchaseOrder,styleId,colorId,dItemId";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				accessoriesSizeList.add(new AccessoriesStock(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(), element[9].toString(),"0", Double.toString(Double.parseDouble(element[10].toString())),Double.toString(Double.parseDouble(element[11].toString())),Double.toString(Double.parseDouble(element[12].toString())), Double.toString(Double.parseDouble(element[13].toString())), Double.toString(Double.parseDouble(element[14].toString())), Double.toString(Double.parseDouble(element[15].toString()))));			
			}

			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return accessoriesSizeList;
	}



	@Override
	public List<AccessoriesStock> getFabricsStockDetails() {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<AccessoriesStock> fabricsSizeList = new ArrayList<AccessoriesStock>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select fat.purchaseOrder,fat.styleId,fat.styleNo,fat.styleItemId,fat.itemName,fat.colorId,fat.colorName,fat.rollId,(select itemName from TbAccessoriesItem where itemid=fat.dItemId) as AccessoriesItem,sum(fat.qty) as ReceivedQty,ISNULL((select sum(qty) from tbFabricsAccessoriesTransaction t where t.purchaseOrder=fat.purchaseOrder and t.styleId=fat.styleId and t.styleItemId=fat.styleItemId and t.colorId=fat.colorId and t.rollId=fat.rollId and t.transactionType='10'),0) as StoreReturnQty,ISNULL((select sum(qty) from tbFabricsAccessoriesTransaction t where t.purchaseOrder=fat.purchaseOrder and t.styleId=fat.styleId and t.styleItemId=fat.styleItemId and t.colorId=fat.colorId and t.rollId=fat.rollId and t.transactionType='4'),0) as StoreIssueQty,ISNULL((select sum(qty) from tbFabricsAccessoriesTransaction t where t.purchaseOrder=fat.purchaseOrder and t.styleId=fat.styleId and t.styleItemId=fat.styleItemId and t.colorId=fat.colorId and t.rollId=fat.rollId and t.transactionType='5'),0) as StoreIssueReturnQty,ISNULL((select sum(qty) from tbFabricsAccessoriesTransaction t where t.purchaseOrder=fat.purchaseOrder and t.styleId=fat.styleId and t.styleItemId=fat.styleItemId and t.colorId=fat.colorId and t.rollId=fat.rollId and t.transactionType='6'),0) as StoreTransferInQty,ISNULL((select sum(qty) from tbFabricsAccessoriesTransaction t where t.purchaseOrder=fat.purchaseOrder and t.styleId=fat.styleId and t.styleItemId=fat.styleItemId and t.colorId=fat.colorId and t.rollId=fat.rollId and t.transactionType='7'),0) as StoreTransferOutQty from tbFabricsAccessoriesTransaction fat where fat.transactionType='1' and fat.itemType='1' group by fat.purchaseOrder,fat.styleId,fat.styleNo,fat.styleItemId,fat.itemName,fat.colorId,fat.colorName,fat.rollId,fat.dItemId,fat.rollId order by purchaseOrder,styleId,colorId,dItemId,rollId";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();
				fabricsSizeList.add(new AccessoriesStock(element[0].toString(), element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(), element[7].toString(), element[8].toString(),"0", Double.toString(Double.parseDouble(element[9].toString())), Double.toString(Double.parseDouble(element[10].toString())),Double.toString(Double.parseDouble(element[11].toString())),Double.toString(Double.parseDouble(element[12].toString())), Double.toString(Double.parseDouble(element[13].toString())), Double.toString(Double.parseDouble(element[14].toString()))));			
			}

			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return fabricsSizeList;
	}




	@Override
	public List<StoreGeneralTransferInOut> ItemsandStockList(String type) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<StoreGeneralTransferInOut> datalist=new ArrayList<StoreGeneralTransferInOut>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select itemid,itemname,isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemid and type in (1,4,6)),0)-isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemid and type in (2,3,5)),0) as stock from tbStoreItemInformation a order by itemid";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();

				if (Double.parseDouble(element[2].toString())>0) {
					datalist.add(new StoreGeneralTransferInOut(element[0].toString(),element[1].toString(), element[2].toString()));	
				}			
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}



	@Override
	public List<StoreGeneralTransferInOut> transferedOutStock(String type) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<StoreGeneralTransferInOut> datalist=new ArrayList<StoreGeneralTransferInOut>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql="";
			if (type.equals("4")) {
				 sql = "select itemid,itemname,isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemid and type in (3)),0)-isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemid and type in (4)),0) as stock from tbStoreItemInformation a order by itemid";		

			}else if(type.equals("6")) {
				 sql = "select itemid,itemname,isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemid and type in (5)),0)-isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemid and type in (6)),0) as stock from tbStoreItemInformation a order by itemid";		

			}
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();

				if (Double.parseDouble(element[2].toString())>0) {
					datalist.add(new StoreGeneralTransferInOut(element[0].toString(),element[1].toString(), element[2].toString()));	
				}			
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}

	@Override
	public List<StoreGeneralReceived> getStoreGeneralReceivedItemList(String invoiceNo, String type,String searched) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<StoreGeneralReceived> datalist=new ArrayList<StoreGeneralReceived>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			
			int status=0;
			if (searched.equals("1")) {
				status=1;
			}
			String sql = "select a.autoId,(select ItemName from tbStoreItemInformation where ItemId=a.ItemId) as ItemName,(select UnitName from tbunits where UnitId=a.unit) as UnitName,a.Qty,a.buyPrice,a.TotalPrice from TbStoreTransectionDetails a where a.invoiceNo='"+invoiceNo+"' and a.type='"+type+"' and a.status='"+status+"'";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();

				datalist.add(new StoreGeneralReceived(element[0].toString(),element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString()));				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}


	@Override
	public List<StoreGeneralReceived> getStoreGeneralItemList(String invoiceNo,String type) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<StoreGeneralReceived> datalist=new ArrayList<StoreGeneralReceived>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			String sql = "select a.autoId,(select ItemName from tbStoreItemInformation where ItemId=a.ItemId) as ItemName,(select UnitName from tbunits where UnitId=a.unit) as UnitName,a.Qty,a.buyPrice,a.TotalPrice from TbStoreTransectionDetails a where a.invoiceNo='"+invoiceNo+"' and a.type='"+type+"' and a.status='1'";		
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();

				datalist.add(new StoreGeneralReceived(element[0].toString(),element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString()));				
			}			
			tx.commit();			
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}


	@Override
	public List<StoreGeneralReceived> generalStoreInvoiceInfodata(String invoice, String type) {

		boolean exists=false;
		Session session=HibernateUtil.openSession();
		Transaction tx=null;
		List<StoreGeneralReceived> dataList=new ArrayList<StoreGeneralReceived>();
		try{
			tx=session.getTransaction();
			tx.begin();

			String sql="select invoiceno,persionid,(select name from tbSupplier where id=persionid) as suppliername,challanno,(select convert(varchar,date,103)) as date,amount as grossamount,netamount from TbStoreTransectionInvoice where invoiceno='"+invoice+"' and type='"+type+"'";
			System.out.println(sql);
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{	
				Object[] element = (Object[]) iter.next();							
				dataList.add(new StoreGeneralReceived(element[0].toString(),element[1].toString(),element[2].toString(), element[3].toString(),element[4].toString(), element[5].toString(),element[6].toString()));
				
			}

			

			tx.commit();
			return dataList;
		}
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return dataList;


	}



	@Override
	public List<StoreGeneralTransferInOut> generalstoreItemDetails(String []itemid) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<StoreGeneralTransferInOut> datalist=new ArrayList<StoreGeneralTransferInOut>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			for (int i = 0; i < itemid.length; i++) {
				String sql = "select itemid,itemname,unitid,(select unitname from tbunits where UnitId=a.UnitId) as unit,isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemid and type in (1)),0)as rec,isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemid and type in (4)),0) as trin,isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemid and type in (3)),0) as trout,isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemid and type in (5)),0) as issue,isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemid and type in (2)),0) as returned,isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemid and type in (6)),0) as issuereturn from tbStoreItemInformation a where a.ItemId='"+itemid[i]+"' order by itemid";		
				List<?> list = session.createSQLQuery(sql).list();
				for(Iterator<?> iter = list.iterator(); iter.hasNext();)
				{	
					Object[] element = (Object[]) iter.next();

					datalist.add(new StoreGeneralTransferInOut(element[0].toString(),element[1].toString(), element[2].toString(), element[3].toString(), element[4].toString(), element[5].toString(), element[6].toString(),element[7].toString(),element[8].toString(),"0.0",element[9].toString()));				
				}			
				
			}	
			tx.commit();
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}



	@Override
	public boolean insertStoreGeneralTrOut(String [] itemids,String [] unitids,String [] troutqty, StoreGeneralTransferInOut v) {
		Session session=HibernateUtil.openSession();
		boolean fileinsert=false;
		Transaction tx=null;
		try{
			tx=session.getTransaction();
			tx.begin();
			
			String invoiceno="";
			String sql="";
			if (v.getSearched().equals("0")) {
				 sql="select (isnull(max(invoiceNo),0)+1) as invoiceno from TbStoreTransectionInvoice where type='"+v.getType()+"'";
				List<?> list = session.createSQLQuery(sql).list();
				 invoiceno=list.get(0).toString();
				
				
				sql="insert into TbStoreTransectionInvoice ("
						+ "invoiceNo,"
						+ " PersionId, "
						+ "PersionName,"
						+ "type,  "
						+ "remark, "
						+ "date, "
						+ "entryTime, "
						+ "UserId,deptid) values ('"+invoiceno+"','"+v.getReceivedby()+"','"+v.getReceivedby()+"','"+v.getType()+"','"+v.getRemark()+"','"+v.getTransefdate()+"',current_timestamp,'"+v.getUserid()+"','"+v.getDetparmentid()+"')";
						session.createSQLQuery(sql).executeUpdate();

			}else {
				sql="delete from TbStoreTransectionDetails where invoiceno='"+v.getTransferid()+"' and type='"+v.getType()+"'";
				session.createSQLQuery(sql).executeUpdate();
				
				invoiceno=v.getTransferid();
			}
			
			for (int i = 0; i < itemids.length; i++) {
				 sql="insert into TbStoreTransectionDetails ( itemId, unit, qty, type, date, invoiceNo, Status, entryTime, UserId) "
				 		+ "values"
				 		+ "('"+itemids[i]+"','"+unitids[i]+"','"+troutqty[i]+"','"+v.getType()+"','"+v.getTransefdate()+"','"+invoiceno+"','1',current_timestamp,'"+v.getUserid()+"')";
				session.createSQLQuery(sql).executeUpdate();
			}
			
		
				
				tx.commit();
				fileinsert= true;
			
		}
		catch(Exception ee){

			if (tx != null) {
				tx.rollback();
				return false;
			}
			ee.printStackTrace();
		}

		finally {
			session.close();
		}
		
		return fileinsert;
	}


	@Override
	public List<StoreGeneralTransferInOut> getGenerStoreTrOutInvoices(String type) {
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<StoreGeneralTransferInOut> datalist=new ArrayList<StoreGeneralTransferInOut>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			
				String sql = "select invoiceno, convert(varchar,date,103) as grnDate,isnull((select DepartmentName from TbDepartmentInfo where DepartmentId=a.deptid),'') as deptname from TbStoreTransectionInvoice a where type='"+type+"'";		
				List<?> list = session.createSQLQuery(sql).list();
				for(Iterator<?> iter = list.iterator(); iter.hasNext();)
				{	
					Object[] element = (Object[]) iter.next();

					datalist.add(new StoreGeneralTransferInOut(element[0].toString(),element[1].toString(), element[2].toString(), ""));				
				}			
				
			
		tx.commit();
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}



	@Override
	public List<StoreGeneralTransferInOut> getGenerStoreTrOutInvoiceItems(String invoice, String type) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.openSession();
		Transaction tx=null;

		List<StoreGeneralTransferInOut> datalist=new ArrayList<StoreGeneralTransferInOut>();	
		try{	
			tx=session.getTransaction();
			tx.begin();		
			
				String sql = "select invoiceno, (select persionname from TbStoreTransectionInvoice where invoiceNo=a.invoiceNo and type=a.type) as recby,(select convert(varchar, date,103) from TbStoreTransectionInvoice where invoiceNo=a.invoiceNo and type=a.type) as date,(select deptid from TbStoreTransectionInvoice where invoiceNo=a.invoiceNo and type=a.type) as dept,(select remark from TbStoreTransectionInvoice where invoiceNo=a.invoiceNo and type=a.type) as remark, a.itemId,(select ItemName from tbStoreItemInformation where itemId=a.itemId) as itemname, a.unit, (select unitname from tbunits where Unitid=a.unit) as unitname,isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemId and type=1),0) as rec,isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemId and type=4),0) as trin,isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemId and type=3),0) as trout,isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemId and type=5),0) as issue,isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemId and type=2),0) as returned , a.qty,isnull((select sum(qty) from TbStoreTransectionDetails where itemId=a.itemId and type=6),0) as issuereturn  from TbStoreTransectionDetails a where invoiceNo='"+invoice+"' and type='"+type+"'";		
				List<?> list = session.createSQLQuery(sql).list();
				for(Iterator<?> iter = list.iterator(); iter.hasNext();)
				{	
					Object[] element = (Object[]) iter.next();

					datalist.add(new StoreGeneralTransferInOut(element[0].toString(),element[1].toString(), element[2].toString(), element[3].toString(),element[4].toString(),element[5].toString(),element[6].toString(),element[7].toString(),element[8].toString(),element[9].toString(),element[10].toString(),element[11].toString(),element[12].toString(),element[13].toString(),element[14].toString(),element[15].toString()));				
				}			
				
			
		tx.commit();
		}	
		catch(Exception e){
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return datalist;
	}

}
