package pg.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sun.istack.internal.logging.Logger;

import pg.model.CommonModel;
import pg.orderModel.AccessoriesIndent;
import pg.orderModel.AccessoriesIndentCarton;
import pg.orderModel.BuyerPO;
import pg.orderModel.BuyerPoItem;
import pg.orderModel.CheckListModel;
import pg.orderModel.Costing;
import pg.orderModel.FabricsIndent;
import pg.orderModel.FileUpload;
import pg.orderModel.ParcelModel;
import pg.orderModel.PurchaseOrder;
import pg.orderModel.PurchaseOrderItem;
import pg.orderModel.SampleCadAndProduction;
import pg.orderModel.SampleRequisitionItem;
import pg.orderModel.Style;
import pg.registerModel.AccessoriesItem;
import pg.registerModel.Brand;
import pg.registerModel.BuyerModel;
import pg.registerModel.Color;
import pg.registerModel.CourierModel;
import pg.registerModel.Employee;
import pg.registerModel.FabricsItem;
import pg.registerModel.Factory;
import pg.registerModel.FactoryModel;
import pg.registerModel.ItemDescription;
import pg.registerModel.ParticularItem;
import pg.registerModel.Size;
import pg.registerModel.SizeGroup;
import pg.registerModel.SupplierModel;
import pg.registerModel.Unit;
import pg.services.OrderService;
import pg.services.ProductionService;
import pg.services.RegisterService;



@Controller
@RestController
public class OrderController {

	private static String UPLOAD_FILE_SAVE_FOLDER = "C:/uploadspringfiles/";
	
	private static final String UPLOAD_DIRECTORY ="/WEB-INF/upload";  

	//private static final String DIRECTORY="E:/uploadspringfiles/";
	

	DecimalFormat df = new DecimalFormat("#.00");

	@Autowired
	private OrderService orderService;
	@Autowired
	private ProductionService productionService;
	@Autowired
	private RegisterService registerService;

	String poid;
	//String styleid;
	//String itemid;
	String sampleId;

	String empCode[],dept,userId;

	int type;

	boolean fileupload=false;


	//Style Create 

	String styleNo="",date="";

	String FrontImg="",BackImg;

	String AiNo="",BuyerPoId="",SampleReqId="";
	
	String mBuyerName;
	String sDate;
	String eDate;
	


	private String getImageName(CommonsMultipartFile frontImage, HttpSession session) throws IOException  {
		ServletContext context = session.getServletContext();  
		String path = context.getRealPath(UPLOAD_DIRECTORY);  

		String filename = frontImage.getOriginalFilename();  

		byte[] bytes = frontImage.getBytes();  
		BufferedOutputStream stream =new BufferedOutputStream(new FileOutputStream(  
				new File(path + File.separator + filename)));  
		stream.write(bytes);  
		stream.flush();  
		stream.close();  

		String frontfile=session.getServletContext().getRealPath("WEB-INF/upload/"+filename);

		return frontfile;
	}


	@RequestMapping(value = "/getStyleWiseItem/{value}",method=RequestMethod.GET)
	public @ResponseBody JSONObject getStyleAndItem(@PathVariable ("value") String value) {
		JSONObject objmain = new JSONObject();
		JSONArray mainarray = new JSONArray();

		List<Style> lablist=orderService.getStyleAndItem(value);
		for(int a=0;a<lablist.size();a++) {
			JSONObject obj = new JSONObject();
			obj.put("id", lablist.get(a).getItemId());
			obj.put("value", lablist.get(a).getItemName());

			mainarray.add(obj);
		}
		objmain.put("result", mainarray);
		System.out.println(objmain.toString());
		return objmain;
	}



	//Costing Create Work of nasir bai...
	@RequestMapping(value = "/costing_create",method=RequestMethod.GET)
	public ModelAndView costing_create(ModelMap map,HttpSession session) {

		String userId=(String)session.getAttribute("userId");
		String userName=(String)session.getAttribute("userName");

		ModelAndView view = new ModelAndView("order/costing_create");
		List<Unit> unitList= registerService.getUnitList();	
		List<Style> styleList= orderService.getStyleList(userId);
		List<BuyerModel> buyerList= registerService.getAllBuyers(userId);
		List<ParticularItem> particularList = orderService.getTypeWiseParticularList("1");
		List<Costing> costingList = orderService.getCostingList(userId);
		map.addAttribute("styleList",styleList);
		map.addAttribute("unitList",unitList);
		map.addAttribute("buyerList",buyerList);
		map.addAttribute("particularList",particularList);
		map.addAttribute("costingList",costingList);

		map.addAttribute("userId",userId);
		map.addAttribute("userName",userName);

		return view; //JSP - /WEB-INF/view/index.jsp
	}
	
	

	@RequestMapping(value = "/cloningCosting",method=RequestMethod.GET)
	public @ResponseBody JSONObject cloningCosting(String costingNo,String oldStyleId,String oldItemId,String newStyleId,String newItemId,String userId) {
		JSONObject objmain = new JSONObject();

		List<Costing> costingList = orderService.cloningCosting(costingNo,oldStyleId,oldItemId);
		objmain.put("result",costingList);

		return objmain;
	}

	//Costing Create Work of nasir bai...
	@RequestMapping(value = "/costing_new_version",method=RequestMethod.GET)
	public ModelAndView costing_new_version(ModelMap map,HttpSession session) {

		String userId=(String)session.getAttribute("userId");
		String userName=(String)session.getAttribute("userName");

		ModelAndView view = new ModelAndView("order/costing_new_version");
		List<Unit> unitList= registerService.getUnitList();	
		List<Style> styleList= orderService.getStyleList(userId);
		List<BuyerModel> buyerList= registerService.getAllBuyers(userId);
		List<ParticularItem> particularList = orderService.getTypeWiseParticularList("1");
		List<Costing> costingList = orderService.getNewCostingList(userId);
		map.addAttribute("styleList",styleList);
		map.addAttribute("unitList",unitList);
		map.addAttribute("buyerList",buyerList);
		map.addAttribute("particularList",particularList);
		map.addAttribute("costingList",costingList);

		map.addAttribute("userId",userId);
		map.addAttribute("userName",userName);

		return view; //JSP - /WEB-INF/view/index.jsp
	}
	
	@RequestMapping(value = "/printNewCostingReport/{costingNo}",method=RequestMethod.GET)
	public @ResponseBody ModelAndView printCostingReport(ModelMap map,@PathVariable("costingNo") String costingNo) {
		
		ModelAndView view=new ModelAndView("order/printNewCostingReport");
		map.addAttribute("costingNo", costingNo);

		return view;
	}
	
	@RequestMapping(value = "/updateConfirmCostingNewVersion",method=RequestMethod.POST)
	public @ResponseBody String updateConfirmCostingNewVersion(Costing v) {
		String msg="";
			boolean flag=orderService.updateConfirmCostingNewVersion(v);
			System.out.println("flag "+flag);
			if(flag) {
				msg="Costing Update Succesfully";
			}


		return msg;
	}
	
	
	
	
	@RequestMapping(value = "/confirmCostingNewVersion",method=RequestMethod.POST)
	public @ResponseBody String confirmCostingNewVersion(Costing v) {
		String msg="";
		if(!orderService.checkCostingExist(v)) {
			boolean flag=orderService.saveCostingNewVersion(v);
			System.out.println("flag "+flag);
			if(flag) {
				msg="Costing Create Succesfully";
			}
		}
		else{
			msg="Costing Already Save";
		}
		return msg;
	}
	
	
	@RequestMapping(value = "/cloneCostingNewVersion",method=RequestMethod.POST)
	public @ResponseBody JSONObject cloneCostingNewVersion(String costingNo,String userId,String styleNo,String itemName) {
		JSONObject objmain = new JSONObject();
		
		JSONArray mainArray = new JSONArray();
		List<Costing> costingList = orderService.cloneCostingNewVersion(costingNo,userId,styleNo,itemName);
		objmain.put("result",costingList);
		return objmain;
	}

	
	@RequestMapping(value = "/searchCostingNewVersion",method=RequestMethod.POST)
	public @ResponseBody JSONObject searchCostingNewVersion(String costingNo) {
		JSONObject objmain = new JSONObject();

		JSONArray mainArray = new JSONArray();
		List<Costing> costingList = orderService.searchCostingNewVersion(costingNo);
		objmain.put("result",costingList);
		return objmain;
	}

	
	
	@RequestMapping(value = "/getCostingItemList",method=RequestMethod.GET)
	public @ResponseBody JSONObject getCostingItemList(Costing costing) {
		JSONObject objmain = new JSONObject();
		List<Costing> costingFabricsList = orderService.getFabricsItemForCosting();
		//List<Costing> costingItemList = orderService.getCostingItemList();
		List<Unit> unitList = orderService.getUnitList();
		objmain.put("costingFabricsList",costingFabricsList);
		objmain.put("unitList",unitList);
		return objmain;
	}

	@RequestMapping(value = "/confirmCosting",method=RequestMethod.POST)
	public @ResponseBody JSONObject confirmCosting(String costingList) {
		JSONObject objmain = new JSONObject();

		try {
			String[] itemList = costingList.split("#");
			System.out.println("Item List size="+itemList.length);
			List<Costing> list = new ArrayList<Costing>();
			String autoId,styleId="",styleNo,itemId="",itemName,particularType,particularId,particularName,unitId,commission,width,yard,gsm,consumption,unitPrice,amount,date,userId;

			for (String item : itemList) {
				System.out.println(item);
				String[] itemProperty = item.split(",");
				autoId = itemProperty[0].substring(itemProperty[0].indexOf(":")+1).trim();
				styleId = itemProperty[1].substring(itemProperty[1].indexOf(":")+1).trim();
				styleNo = itemProperty[2].substring(itemProperty[2].indexOf(":")+1).trim();
				itemId = itemProperty[3].substring(itemProperty[3].indexOf(":")+1).trim();
				itemName = itemProperty[4].substring(itemProperty[4].indexOf(":")+1).trim();
				particularType = itemProperty[5].substring(itemProperty[5].indexOf(":")+1).trim();
				particularId = itemProperty[6].substring(itemProperty[6].indexOf(":")+1).trim();
				particularName = itemProperty[7].substring(itemProperty[7].indexOf(":")+1).trim();
				unitId = itemProperty[8].substring(itemProperty[8].indexOf(":")+1).trim();
				commission = itemProperty[9].substring(itemProperty[9].indexOf(":")+1).trim();
				width = itemProperty[10].substring(itemProperty[10].indexOf(":")+1).trim();
				yard = itemProperty[11].substring(itemProperty[11].indexOf(":")+1).trim();
				gsm = itemProperty[12].substring(itemProperty[12].indexOf(":")+1).trim();
				consumption = itemProperty[13].substring(itemProperty[13].indexOf(":")+1).trim();
				unitPrice = itemProperty[14].substring(itemProperty[14].indexOf(":")+1).trim();
				amount = itemProperty[15].substring(itemProperty[15].indexOf(":")+1).trim();
				date = itemProperty[16].substring(itemProperty[16].indexOf(":")+1).trim();
				userId = itemProperty[17].substring(itemProperty[17].indexOf(":")+1).trim();
				list.add(new Costing(autoId, styleId, itemId, particularType, particularId, unitId, Double.valueOf(width), Double.valueOf(yard), Double.valueOf(gsm), Double.valueOf(consumption), Double.valueOf(unitPrice), Double.valueOf(amount), Double.valueOf(commission), date, userId));
			}
			String costingNo = orderService.confirmCosting(list);
			objmain.put("result",costingNo);
			objmain.put("particularList",orderService.getCostingList(styleId, itemId, costingNo));
		}catch(Exception e) {
			e.printStackTrace();
		}


		return objmain;
	}


	@RequestMapping(value = "/editCostingNo",method=RequestMethod.POST)
	public @ResponseBody JSONObject editCostingNo(String costingList) {
		JSONObject objmain = new JSONObject();

		try {
			String[] itemList = costingList.split("#");
			System.out.println("Item List size="+itemList.length);
			List<Costing> list = new ArrayList<Costing>();
			Costing temp = null;
			String costingNo="",autoId,styleId="",styleNo,itemId="",itemName,particularType,particularId,particularName,unitId,commission,width,yard,gsm,consumption,unitPrice,amount,date,userId;

			for (String item : itemList) {
				System.out.println(item);
				String[] itemProperty = item.split(",");
				autoId = itemProperty[0].substring(itemProperty[0].indexOf(":")+1).trim();
				styleId = itemProperty[1].substring(itemProperty[1].indexOf(":")+1).trim();
				styleNo = itemProperty[2].substring(itemProperty[2].indexOf(":")+1).trim();
				itemId = itemProperty[3].substring(itemProperty[3].indexOf(":")+1).trim();
				itemName = itemProperty[4].substring(itemProperty[4].indexOf(":")+1).trim();
				particularType = itemProperty[5].substring(itemProperty[5].indexOf(":")+1).trim();
				particularId = itemProperty[6].substring(itemProperty[6].indexOf(":")+1).trim();
				particularName = itemProperty[7].substring(itemProperty[7].indexOf(":")+1).trim();
				unitId = itemProperty[8].substring(itemProperty[8].indexOf(":")+1).trim();
				commission = itemProperty[9].substring(itemProperty[9].indexOf(":")+1).trim();
				width = itemProperty[10].substring(itemProperty[10].indexOf(":")+1).trim();
				yard = itemProperty[11].substring(itemProperty[11].indexOf(":")+1).trim();
				gsm = itemProperty[12].substring(itemProperty[12].indexOf(":")+1).trim();
				consumption = itemProperty[13].substring(itemProperty[13].indexOf(":")+1).trim();
				unitPrice = itemProperty[14].substring(itemProperty[14].indexOf(":")+1).trim();
				amount = itemProperty[15].substring(itemProperty[15].indexOf(":")+1).trim();
				date = itemProperty[16].substring(itemProperty[16].indexOf(":")+1).trim();
				userId = itemProperty[17].substring(itemProperty[17].indexOf(":")+1).trim();
				costingNo = itemProperty[18].substring(itemProperty[18].indexOf(":")+1).trim();
				temp = new Costing(autoId, styleId, itemId, particularType, particularId, unitId, Double.valueOf(width), Double.valueOf(yard), Double.valueOf(gsm), Double.valueOf(consumption), Double.valueOf(unitPrice), Double.valueOf(amount), Double.valueOf(commission), date, userId);
				temp.setCostingNo(costingNo);
				list.add(temp);
			}

			objmain.put("result",orderService.editCostingNo(list));
			objmain.put("particularList",orderService.getCostingList(styleId, itemId, costingNo));
		}catch(Exception e) {
			e.printStackTrace();
		}


		return objmain;
	}

	@RequestMapping(value = "/editCosting",method=RequestMethod.POST)
	public @ResponseBody JSONObject editCosting(Costing costing) {
		JSONObject objmain = new JSONObject();
		if(orderService.editCosting(costing)) {

			List<Costing> costingList = orderService.getCostingList(costing.getStyleId(),costing.getItemId(),costing.getCostingNo());

			objmain.put("result",costingList);
		}else {
			objmain.put("result", "Something Wrong");
		}

		return objmain;
	}

	@RequestMapping(value = "/deleteCosting",method=RequestMethod.GET)
	public @ResponseBody JSONObject deleteCosting(String autoId,String styleId,String itemId,String costingNo) {
		JSONObject objmain = new JSONObject();
		if(orderService.deleteCosting(autoId)) {

			List<Costing> costingList = orderService.getCostingList(styleId,itemId,costingNo);

			objmain.put("result",costingList);
		}else {
			objmain.put("result", "Something Wrong");
		}

		return objmain;
	}

	@RequestMapping(value = "/searchCosting",method=RequestMethod.GET)
	public @ResponseBody JSONObject searchCosting(String styleId,String itemId,String costingNo) {
		JSONObject objmain = new JSONObject();

		JSONArray mainArray = new JSONArray();
		List<Costing> costingList = orderService.getCostingList(styleId,itemId,costingNo);
		objmain.put("result",costingList);
		return objmain;
	}

	@RequestMapping(value = "/buyerWiseCostingSearch",method=RequestMethod.POST)
	public @ResponseBody JSONObject buyerWiseCostingSearch(String buyerId,String userId) {
		JSONObject objmain = new JSONObject();

		JSONArray mainArray = new JSONArray();
		List<Costing> costingList = orderService.getBuyerWiseCostingList(buyerId,userId);
		objmain.put("result",costingList);
		return objmain;
	}

	@RequestMapping(value = "/searchCostingItem",method=RequestMethod.GET)
	public @ResponseBody JSONObject searchCostingItem(String autoId) {
		JSONObject objmain = new JSONObject();

		JSONArray mainArray = new JSONArray();
		Costing costing = orderService.getCostingItem(autoId);
		objmain.put("result",costing);
		return objmain;
	}

	/*@RequestMapping(value = "/costingReportInfo",method=RequestMethod.GET)
	public @ResponseBody String costingReportInfo(String styleId,String itemId) {
		this.StyleId=styleId;
		this.ItemId=itemId;
		return "Success";
	}*/

	@RequestMapping(value = "/printCostingReport/{styleId}/{itemId}/{costingNo}",method=RequestMethod.GET)
	public @ResponseBody ModelAndView printCostingReport(ModelMap map,@PathVariable("styleId") String styleId,@PathVariable("itemId") String itemId,@PathVariable("costingNo") String costingNo) {


		ModelAndView view=new ModelAndView("order/printCostingReport");


		map.addAttribute("StyleId", styleId);
		map.addAttribute("ItemId", itemId);
		map.addAttribute("costingNo", costingNo);

		return view;
	}

	@RequestMapping(value = "/printGroupCostingReport/{costingId}",method=RequestMethod.GET)
	public @ResponseBody ModelAndView printGroupCostingReport(ModelMap map,@PathVariable("costingId") String costingId) {


		ModelAndView view=new ModelAndView("order/printGroupCostingReport");

		map.addAttribute("costingId", costingId);

		return view;
	}

	@RequestMapping(value = "/typeWiseParticularLoad",method=RequestMethod.GET)
	public @ResponseBody JSONObject typeWiseParticularLoad(String type) {
		JSONObject objmain = new JSONObject();
		List<ParticularItem> particularList= orderService.getTypeWiseParticularList(type);

		objmain.put("particularList", particularList);

		return objmain;
	}

	@RequestMapping(value = "/itemDescriptionLoad",method=RequestMethod.GET)
	public @ResponseBody JSONObject itemDescriptionLoad() {
		JSONObject objmain = new JSONObject();
		JSONArray mainarray = new JSONArray();

		List<ItemDescription> List= orderService.getItemDescriptionList();

		for(int a=0;a<List.size();a++) {
			JSONObject obj = new JSONObject();
			obj.put("id", List.get(a).getItemId());
			obj.put("value", List.get(a).getItemName());
			mainarray.add(obj);
		}

		objmain.put("result", mainarray);
		System.out.println(objmain.toString());
		return objmain;
	}

	@RequestMapping(value = "/buyerLoad",method=RequestMethod.GET)
	public @ResponseBody JSONObject buyerLoad() {
		JSONObject objmain = new JSONObject();


		JSONArray mainarray = new JSONArray();

		/*		List<Buyer> List= orderService.getBuyerList();

		for(int a=0;a<List.size();a++) {
			JSONObject obj = new JSONObject();
			obj.put("id", List.get(a).getBuyerId());
			obj.put("value", List.get(a).getBuyerName());
			mainarray.add(obj);
		}*/

		objmain.put("result", mainarray);
		System.out.println(objmain.toString());
		return objmain;
	}


	//Buyer Purchase Order Create
	@RequestMapping(value = "/buyer_purchase_order",method=RequestMethod.GET)
	public ModelAndView buyer_purchase_order(ModelMap map,HttpSession session) {

		String userId=(String)session.getAttribute("userId");
		String userName=(String)session.getAttribute("userName");

		ModelAndView view = new ModelAndView("order/buyer-purchase-order");
		List<SizeGroup> groupList = registerService.getStyleSizeGroupList();
		List<BuyerModel> buyerList= registerService.getAllBuyers(userId);
		List<FactoryModel> factoryList = registerService.getAllFactories();
		List<Color> colorList = registerService.getColorList();
		List<BuyerPO> buyerPoList = orderService.getBuyerPoList(userId);
		view.addObject("groupList",groupList);
		view.addObject("buyerList",buyerList);
		view.addObject("factoryList",factoryList);
		view.addObject("colorList",colorList);
		view.addObject("buyerPoList",buyerPoList);

		map.addAttribute("userId",userId);
		map.addAttribute("userName",userName);
		return view; //JSP - /WEB-INF/view/index.jsp
	}

	@ResponseBody
	@RequestMapping(value = "/buyerIndentInfo",method=RequestMethod.GET)
	public String buyerIndentInfo(String buyerPoId) {
		this.BuyerPoId=buyerPoId;
		return "Success";
	}


	@RequestMapping(value = "/printBuyerPoOrder",method=RequestMethod.GET)
	public @ResponseBody ModelAndView printBuyerPoOrder(ModelMap map) {

		ModelAndView view=new ModelAndView("order/printBuyerPoOrder");
		map.addAttribute("BuyerPoId", BuyerPoId);
		return view;
	}


	@ResponseBody
	@RequestMapping(value = "/sizesLoadByGroup",method=RequestMethod.GET)
	public JSONObject sizesLoadByGroup() {

		JSONObject objmain = new JSONObject();	
		JSONObject size = new JSONObject() ;
		JSONArray sizeArray = new JSONArray();
		JSONObject groupObj = new JSONObject();

		List<Size> sizeList = registerService.getStyleSizeList();
		int lastSize= sizeList.size();

		for(int i=0;i<lastSize;i++) {

			size.put("sizeId", sizeList.get(i).getSizeId());
			size.put("sizeName", sizeList.get(i).getSizeName());
			sizeArray.add(size);
			if((i+1 == lastSize) || !sizeList.get(i).getGroupId().equals( sizeList.get(i+1).getGroupId())) {

				groupObj.put("groupId"+sizeList.get(i).getGroupId(), sizeArray);
				sizeArray = new JSONArray();
			}
			size = new JSONObject();

		}
		objmain.put("sizeList", groupObj);
		return objmain;

	}

	@ResponseBody
	@RequestMapping(value = "/getBuyerWiseStylesItem",method=RequestMethod.GET)
	public JSONObject getBuyerWiseStylesItem(String buyerId) {
		JSONObject objMain = new JSONObject();	
		List<Style> styleList = orderService.getBuyerWiseStylesItem(buyerId);

		objMain.put("styleList",styleList);
		return objMain; 
	}

	@ResponseBody
	@RequestMapping(value = "/getPurchaseOrderAndStyleListByMultipleBuyers",method=RequestMethod.GET)
	public JSONObject getPurchaseOrderAndStyleListByMultipleBuyers(String buyersId) {
		JSONObject objMain = new JSONObject();	
		List<Style> styleList = orderService.getBuyerPOStyleListByMultipleBuyers(buyersId);
		List<CommonModel> poList = orderService.getPurchaseOrderListByMultipleBuyers(buyersId);
		objMain.put("styleList",styleList);
		objMain.put("buyerPOList",poList);
		return objMain; 
	}

	@ResponseBody
	@RequestMapping(value = "/getStyleListByMultiplePurchaseOrder",method=RequestMethod.GET)
	public JSONObject getStyleListByMultiplePurchaseOrder(String purchaseOrders) {
		JSONObject objMain = new JSONObject();	
		List<Style> styleList = orderService.getBuyerPOStyleListByMultiplePurchaseOrders(purchaseOrders);
		objMain.put("styleList",styleList);
		return objMain; 
	}

	@ResponseBody
	@RequestMapping(value = "/getPurchaseOrderByMultipleStyleId",method=RequestMethod.GET)
	public JSONObject getPurchaseOrderByMultipleStyleId(String styleIdList) {
		JSONObject objMain = new JSONObject();	
		List<CommonModel> buyerPOList = orderService.getPurchaseOrderByMultipleStyle(styleIdList);
		objMain.put("buyerPOList",buyerPOList);
		return objMain; 
	}

	@ResponseBody
	@RequestMapping(value = "/getStyleWiseItem",method=RequestMethod.GET)
	public JSONObject getStyleWiseItem(String styleId) {
		JSONObject objMain = new JSONObject();	
		List<ItemDescription> itemList = orderService.getStyleWiseItem(styleId);

		objMain.put("itemList",itemList);
		return objMain; 
	}

	@ResponseBody
	@RequestMapping(value = "/getItemListByMultipleStyleId",method=RequestMethod.GET)
	public JSONObject getItemListByMultipleStyleId(String styleIdList) {
		JSONObject objMain = new JSONObject();	
		List<ItemDescription> itemList = orderService.getItemListByMultipleStyleId(styleIdList);

		objMain.put("itemList",itemList);
		return objMain; 
	}

	@ResponseBody
	@RequestMapping(value = "/getColorAndShippingListByMultipleStyleId",method=RequestMethod.GET)
	public JSONObject getColorAndShippingListByMultipleStyleId(String purchaseOrders,String styleIdList) {
		JSONObject objMain = new JSONObject();	
		List<Color> colorList = orderService.getColorListByMultiplePoAndStyle(purchaseOrders, styleIdList);
		List<String> shippingMarkList = orderService.getShippingMarkListByMultiplePoAndStyle(purchaseOrders, styleIdList);
		objMain.put("colorList",colorList);
		objMain.put("shippingMarkList",shippingMarkList);
		return objMain; 
	}

	@ResponseBody
	@RequestMapping(value = "/getStyleWiseBuyerPO",method=RequestMethod.GET)
	public JSONObject getStyleWiseBuyerPO(String styleId) {
		JSONObject objMain = new JSONObject();	
		List<CommonModel> poList = orderService.getStyleWiseBuyerPO(styleId);

		objMain.put("poList",poList);
		return objMain; 
	}



	@RequestMapping(value = "/addItemToBuyerPO",method=RequestMethod.POST)
	public @ResponseBody JSONObject addItemToBuyerPO(BuyerPoItem buyerPoItem) {
		JSONObject objmain = new JSONObject();
		if(!orderService.isBuyerPoItemExist(buyerPoItem)) {
			if(orderService.addBuyerPoItem(buyerPoItem)) {
				JSONArray mainArray = new JSONArray();
				List<BuyerPoItem> buyerPOItemList = orderService.getBuyerPOItemList(buyerPoItem.getBuyerPOId(),buyerPoItem.getUserId());
				objmain.put("result",buyerPOItemList);
			}else {
				objmain.put("result", "Something Wrong");
				objmain.put("message", "Something Wrong");
			}
		}else {
			objmain.put("result", "duplicate");
			objmain.put("message", "Duplicate Item");
		}
		

		return objmain;
	}



	@RequestMapping(value = "/editBuyerPoItem",method=RequestMethod.POST)
	public @ResponseBody JSONObject editBuyerPoItem(BuyerPoItem buyerPoItem) {
		JSONObject objmain = new JSONObject();

		if(!orderService.isBuyerPoItemExist(buyerPoItem)) {
			if(orderService.editBuyerPoItem(buyerPoItem)) {
				JSONArray mainArray = new JSONArray();
				List<BuyerPoItem> buyerPOItemList = orderService.getBuyerPOItemList(buyerPoItem.getBuyerPOId(),buyerPoItem.getUserId());
				objmain.put("result",buyerPOItemList);
			}else {
				objmain.put("result", "Something Wrong");
			}
		}else {
			objmain.put("result", "duplicate");
		}
		return objmain;
	}
	
	
	@RequestMapping(value = "/getBuyerPOItemsList",method=RequestMethod.GET)
	public @ResponseBody JSONObject getBuyerPOItemsList(String buyerPoNo,String userId) {
		JSONObject objmain = new JSONObject();


		JSONArray mainArray = new JSONArray();
		List<BuyerPoItem> buyerPOItemList = orderService.getBuyerPOItemList(buyerPoNo,userId);
		objmain.put("result",buyerPOItemList);

		return objmain;
	}

	@RequestMapping(value = "/getBuyerPOItem",method=RequestMethod.GET)
	public @ResponseBody JSONObject getBuyerPOItem(String itemAutoId) {
		JSONObject objmain = new JSONObject();

		JSONArray mainArray = new JSONArray();
		BuyerPoItem buyerPoItem = orderService.getBuyerPOItem(itemAutoId);
		objmain.put("poItem",buyerPoItem);

		return objmain;
	}

	@RequestMapping(value = "/deleteBuyerPoItem",method=RequestMethod.GET)
	public @ResponseBody JSONObject deleteBuyerPoItem(String buyerPoNo,String itemAutoId,String userId) {
		JSONObject objmain = new JSONObject();

		if(orderService.deleteBuyerPoItem(itemAutoId)) {
			JSONArray mainArray = new JSONArray();
			List<BuyerPoItem> buyerPOItemList = orderService.getBuyerPOItemList(buyerPoNo,userId);
			objmain.put("result",buyerPOItemList);
		}else {
			objmain.put("result","Something Wrong");
		}
		return objmain;
	}

	@RequestMapping(value = "/getBuyerPO",method=RequestMethod.GET)
	public @ResponseBody JSONObject getBuyerPO(String buyerPoNo) {
		JSONObject objmain = new JSONObject();


		JSONArray mainArray = new JSONArray();
		BuyerPO buyerPo = orderService.getBuyerPO(buyerPoNo);
		List<FileUpload> fileList = orderService.findfiles(buyerPo.getBuyerId(), "bpo-"+buyerPo.getBuyerPoId(), 1);


		objmain.put("buyerPO",buyerPo);
		objmain.put("fileList",fileList);
		return objmain;
	}

	@RequestMapping(value = "/submitBuyerPO",method=RequestMethod.POST)
	public @ResponseBody JSONObject submitBuyerPO(BuyerPO buyerPO) {
		JSONObject objmain = new JSONObject();

		if(orderService.submitBuyerPO(buyerPO)) {
			objmain.put("result", "Successfull");
		}else {
			objmain.put("result", "Something Wrong");
		}

		return objmain;
	}

	@RequestMapping(value = "/editBuyerPO",method=RequestMethod.POST)
	public @ResponseBody JSONObject editBuyerPO(BuyerPO buyerPO) {
		JSONObject objmain = new JSONObject();

		if(orderService.editBuyerPO(buyerPO)) {
			objmain.put("result", "Successfull");
		}else {
			objmain.put("result", "Something Wrong");
		}

		return objmain;
	}




	//File Upload

	@RequestMapping(value = "/file_upload",method=RequestMethod.GET)
	public ModelAndView fileUpload(ModelMap map,HttpSession session) {


		String userId=(String)session.getAttribute("userId");
		String userName=(String)session.getAttribute("userName");

		ModelAndView view = new ModelAndView("order/fileupload");
		view.addObject("buyer", registerService.getAllBuyers(userId));
		view.addObject("dept", registerService.getDepartmentList());

		map.addAttribute("userId",userId);
		map.addAttribute("userName",userName);
		return view; //JSP - /WEB-INF/view/index.jsp
	}


	// Process multiple file upload action and return a result page to user. 
	@RequestMapping(value="/save-product/{purpose}/{user}/{buyerName}/{purchaseOrderId}", method={RequestMethod.PUT, RequestMethod.POST})
	public String uploadFileSubmit(
			@PathVariable ("purpose") String purpose,
			@PathVariable ("user") String user,
			@PathVariable ("buyerName") String buyerName,
			@PathVariable ("purchaseOrderId") String purchaseOrderId,
			MultipartHttpServletRequest multipartRequest, HttpServletRequest request, HttpServletResponse response) {
		try
		{
			Logger.getLogger(this.getClass()).warning("Inside Confirm Servlet");  
			response.setContentType("text/html");

			String hostname = request.getRemoteHost(); // hostname
			System.out.println("hostname "+hostname);

			String computerName = null;
			String remoteAddress = request.getRemoteAddr();
			InetAddress inetAddress=null;


			inetAddress = InetAddress.getByName(remoteAddress);
			System.out.println("inetAddress: " + inetAddress);
			computerName = inetAddress.getHostName();

			System.out.println("computerName: " + computerName);


			if (computerName.equalsIgnoreCase("localhost")) {
				computerName = java.net.InetAddress.getLocalHost().getCanonicalHostName();
			}else if(hostname.equalsIgnoreCase("0:0:0:0:0:0:0:1")){
				inetAddress = InetAddress.getLocalHost();
				computerName=inetAddress.getHostName();
			}
			System.out.println("ip : " + inetAddress);
			System.out.println("computerName: " + computerName);

			//   Date date=new Date();
			// Get multiple file control names.
			Iterator<String> it = multipartRequest.getFileNames();

			while(it.hasNext())
			{
				String fileControlName = it.next();

				MultipartFile srcFile = multipartRequest.getFile(fileControlName);
				String uploadFileName = purchaseOrderId+srcFile.getOriginalFilename();
				System.out.println(" file names "+uploadFileName);
				// Create server side target file path.
				String destFilePath = UPLOAD_FILE_SAVE_FOLDER+uploadFileName;
				File existingfile=new File(destFilePath);

				System.out.println(" file exists "+uploadFileName+" "+existingfile.exists());

				if (!existingfile.exists()) {
					File destFile = new File(destFilePath);
					// Save uploaded file to target.
					srcFile.transferTo(destFile);
					fileupload = true;

					orderService.fileUpload(uploadFileName, computerName,inetAddress.toString(), purpose,user,buyerName,purchaseOrderId);

					CommonModel saveFileAccessDetails=new CommonModel(empCode,dept,userId,type);
					boolean SaveGeneralDuty=orderService.saveFileAccessDetails(saveFileAccessDetails);
					fileupload=false;
				}
				if (fileupload) {

				}
				//msgBuf.append("Upload file " + uploadFileName + " is saved to " + destFilePath + "<br/><br/>");
			}
			// Set message that will be displayed in return page.
			//  model.addAttribute("message", msgBuf.toString());
		}catch(IOException ex)
		{
			ex.printStackTrace();
		}finally
		{
			return "upload_file_result";
		}
	}

	@RequestMapping(value = "/findfile/{start}/{end}/{user}",method=RequestMethod.POST)
	public @ResponseBody JSONObject findfile(@PathVariable ("start") String start,@PathVariable ("end") String end,@PathVariable ("user") String user) {

		List<pg.orderModel.FileUpload> FileList=orderService.findfiles(start, end,user);
		JSONObject objmain = new JSONObject();

		JSONArray mainArray = new JSONArray();

		for (int i = 0; i < FileList.size(); i++) {
			JSONObject obj = new JSONObject();
			obj.put("id", FileList.get(i).getAutoid());
			obj.put("filename", FileList.get(i).getFilename());
			obj.put("upby", FileList.get(i).getUploadby());
			obj.put("upIp", FileList.get(i).getUploadip());
			obj.put("upMachine", FileList.get(i).getUploadmachine());
			obj.put("purpose", FileList.get(i).getPurpose());
			obj.put("upDate", FileList.get(i).getUploaddate());
			obj.put("upDateTime", FileList.get(i).getDatetime());
			obj.put("DownBy", FileList.get(i).getDownloadby());
			obj.put("DownIp", FileList.get(i).getDownloadip());
			obj.put("DownMachine", FileList.get(i).getDownloadmachine());
			obj.put("DownDate", FileList.get(i).getDownloaddate());
			obj.put("DownDatetime", FileList.get(i).getDownloadtime());

			mainArray.add(obj);

		}

		objmain.put("result", mainArray);

		return objmain;
	}


	@RequestMapping(value="/download/{fileName:.+}/{user}", method=RequestMethod.POST)
	public @ResponseBody void downloadfile(HttpServletResponse response,@PathVariable ("fileName") String fileName,@PathVariable ("user") String user,HttpServletRequest request) throws IOException {
		System.out.println(" download controller ");

		Logger.getLogger(this.getClass()).warning("Inside Confirm Servlet");  
		response.setContentType("text/html");

		String hostname = request.getRemoteHost(); // hostname
		System.out.println("hostname "+hostname);

		String computerName = null;
		String remoteAddress = request.getRemoteAddr();
		InetAddress inetAddress=null;


		inetAddress = InetAddress.getByName(remoteAddress);
		System.out.println("inetAddress: " + inetAddress);
		computerName = inetAddress.getHostName();

		System.out.println("computerName: " + computerName);


		if (computerName.equalsIgnoreCase("localhost")) {
			computerName = java.net.InetAddress.getLocalHost().getCanonicalHostName();
		}else if(hostname.equalsIgnoreCase("0:0:0:0:0:0:0:1")){
			inetAddress = InetAddress.getLocalHost();
			computerName=inetAddress.getHostName();
		}
		System.out.println("ip : " + inetAddress);
		System.out.println("computerName: " + computerName);




		try {




			String filePath = UPLOAD_FILE_SAVE_FOLDER+fileName;

			System.out.println(" filename "+fileName);

			try {
				File file = new File(filePath);
				System.out.println(" file "+file.length()/(1024*1024));
				FileInputStream in = new FileInputStream(file);
				System.out.println(" file in "+in);
				response.setHeader("Expires", new Date().toGMTString());
				response.setContentType(URLConnection.guessContentTypeFromStream(in));

				// response.setContentLength(Files.readAllBytes(file.toPath()).length);

				response.setContentLength((int)file.length());

				response.setHeader("Content-Disposition","attachment; filename=\"" + fileName +"\"");
				response.setHeader("Pragma", "no-cache");

				response.setContentType("application/octet-stream");
				// FileCopyUtils.copy(in, response.getOutputStream());


				IOUtils.copyLarge(in, response.getOutputStream());


				boolean download=orderService.fileDownload(fileName, user, inetAddress.toString(), computerName);

				in.close();
				response.flushBuffer();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@RequestMapping(value = "/delete/{filename:.+}/{id}",method=RequestMethod.POST)
	public @ResponseBody boolean findfile(@PathVariable ("filename") String filename,@PathVariable ("id") String id) {

		boolean delete=orderService.deletefile(filename,id);

		if (delete) {
			File file=new File(UPLOAD_FILE_SAVE_FOLDER+filename);
			file.delete();
		}

		return delete;
	}


	//Accessoires Indent Create
	@RequestMapping(value = "/accessories_indent",method=RequestMethod.GET)
	public ModelAndView accessories_indent(ModelMap map,HttpSession session) {


		String userId=(String)session.getAttribute("userId");
		String userName=(String)session.getAttribute("userName");

		List<CommonModel>purchaseorders=orderService.getPurchaseOrders(userId);

		//List<AccessoriesIndent>listAccPending=orderService.getPendingAccessoriesIndent();
		List<BuyerModel> buyerList= registerService.getAllBuyers(userId);
		List<CommonModel>accessoriesitem=orderService.AccessoriesItem("1");

		List<AccessoriesIndent>listAccPostedData=orderService.getPostedAccessoriesIndent(userId);

		//List<commonModel>unit=orderService.Unit();
		List<CommonModel>brand=orderService.Brands();
		List<CommonModel>color=orderService.AllColors();
		ModelAndView view = new ModelAndView("order/accessories_indent");
		view.addObject("purchaseorders",purchaseorders);
		view.addObject("accessories",accessoriesitem);
		view.addObject("buyerList",buyerList);
		view.addObject("brand",brand);
		view.addObject("unitList",registerService.getUnitList());
		view.addObject("color",color);
		view.addObject("listAccPostedData",listAccPostedData);

		map.addAttribute("userId",userId);
		map.addAttribute("userName",userName);

		//view.addObject("listAccPending",listAccPending);

		return view; //JSP - /WEB-INF/view/index.jsp
	}




	@ResponseBody
	@RequestMapping(value = "/maxAIno",method=RequestMethod.POST)
	public String maxAIno( ) {
		System.out.println(" maxAIno Id ");
		String maxaino="";

		maxaino=orderService.maxAIno();

		return maxaino;

	}



	@ResponseBody
	@RequestMapping(value = "/poWiseStyles/{po}",method=RequestMethod.POST)
	public JSONObject poWiseStyles(@PathVariable ("po") String po) {
		System.out.println(" powisestyles ");


		JSONObject objmain = new JSONObject();
		JSONArray mainarray = new JSONArray();

		List<CommonModel>styles=orderService.Styles(po);

		for (int i = 0; i < styles.size(); i++) {
			JSONObject obj=new JSONObject();

			obj.put("id", styles.get(i).getId());
			obj.put("name", styles.get(i).getName());

			mainarray.add(obj);

		}

		objmain.put("result", mainarray);
		System.out.println(" obj main "+objmain);

		return objmain;

	}

	@ResponseBody
	@RequestMapping(value = "/stylewiseitems",method=RequestMethod.GET)
	public JSONObject stylewiseitems(String buyerorderid,String style) {

		JSONObject objmain = new JSONObject();
		JSONArray mainarray = new JSONArray();

		List<CommonModel>items=orderService.Items(buyerorderid,style);

		for (int i = 0; i < items.size(); i++) {
			JSONObject obj=new JSONObject();

			obj.put("id", items.get(i).getId());
			obj.put("name", items.get(i).getName());

			mainarray.add(obj);

		}

		objmain.put("result", mainarray);
		System.out.println(" obj main "+objmain);

		return objmain;

	}


	@ResponseBody
	@RequestMapping(value = "/styleItemsWiseColor",method=RequestMethod.GET)
	public JSONObject styleItemsWiseColor(String buyerorderid,String style,String item) {
		System.out.println(" stylewisei items ");


		JSONObject objmain = new JSONObject();
		JSONArray mainarray = new JSONArray();

		System.out.println("item "+item);

		List<CommonModel>items=orderService.styleItemsWiseColor(buyerorderid,style,item);

		for (int i = 0; i < items.size(); i++) {
			JSONObject obj=new JSONObject();

			obj.put("id", items.get(i).getId());
			obj.put("name", items.get(i).getName());

			mainarray.add(obj);

		}

		objmain.put("result", mainarray);
		System.out.println(" obj main "+objmain);

		return objmain;

	}

	@ResponseBody
	@RequestMapping(value = "/shippingMark/{po}/{style}/{item}",method=RequestMethod.POST)
	public List purchaseOrders(@PathVariable ("po") String po,@PathVariable ("style") String style,@PathVariable ("item") String item) {
		System.out.println(" shippingmarks ");

		List<CommonModel>shippingMarks=orderService.ShippingMark(po, style, item);



		return shippingMarks;

	}


	@ResponseBody
	@RequestMapping(value = "/styleitemColorWiseSize",method=RequestMethod.GET)
	public JSONObject itemWiseSize(String buyerorderid,String style,String item,String color) {

		System.out.println("size");

		JSONObject objmain = new JSONObject();
		JSONArray mainarray = new JSONArray();

		List<CommonModel>sizes=orderService.Size(buyerorderid, style,item,color);

		for (int i = 0; i < sizes.size(); i++) {
			JSONObject obj=new JSONObject();

			obj.put("id", sizes.get(i).getId());
			obj.put("name", sizes.get(i).getName());

			mainarray.add(obj);

		}

		objmain.put("size", mainarray);
		return objmain;

	}

	@ResponseBody
	@RequestMapping(value = "/styleitemColorWiseOrderQty",method=RequestMethod.GET)
	public JSONObject styleitemColorWiseOrderQty(String buyerorderid,String style,String item,String color,String size) {


		JSONObject objmain = new JSONObject();
		JSONArray mainarray = new JSONArray();

		List<CommonModel>sizes=orderService.SizewiseQty(buyerorderid, style,item,color,size);

		for (int i = 0; i < sizes.size(); i++) {
			JSONObject obj=new JSONObject();

			obj.put("qty", sizes.get(i).getQty());
			mainarray.add(obj);

		}

		objmain.put("size", mainarray);
		return objmain;

	}


	@ResponseBody
	@RequestMapping(value = "/itemWiseColor/{style}/{item}",method=RequestMethod.POST)
	public JSONObject itemWiseColor(@PathVariable ("style") String style,@PathVariable ("item") String item) {
		System.out.println(" Purchase Orders ");
		JSONObject objmain = new JSONObject();
		JSONArray mainarray = new JSONArray();




		List<CommonModel>colors=orderService.Colors(style, item);

		for (int i = 0; i < colors.size(); i++) {
			JSONObject obj=new JSONObject();

			obj.put("id", colors.get(i).getId());
			obj.put("name", colors.get(i).getName());

			mainarray.add(obj);

		}

		objmain.put("color", mainarray);
		System.out.println(" obj main "+objmain);

		return objmain;

	}


	@ResponseBody
	@RequestMapping(value = "/SizeWiseQty/{style}/{item}/{size}/{color}/{type}",method=RequestMethod.POST)
	public JSONObject SizeWiseQty(@PathVariable ("style") String style,@PathVariable ("item") String item,@PathVariable ("size") String size,@PathVariable ("color") String color,@PathVariable ("type") String type) {
		System.out.println(" Purchase Orders ");
		JSONObject objmain = new JSONObject();
		JSONArray mainarray = new JSONArray();




		List<CommonModel>qty=orderService.SizewiseQty(style,item,size,color,type);

		for (int i = 0; i < qty.size(); i++) {
			JSONObject obj=new JSONObject();


			obj.put("name", qty.get(i).getQty());

			mainarray.add(obj);

		}

		objmain.put("size", mainarray);
		System.out.println(" obj main "+objmain);

		return objmain;

	}


	@ResponseBody
	@RequestMapping(value = "/getAccessoriesRecyclingData",method = RequestMethod.GET)
	public JSONObject getAccessoriesRecyclingData(String query){
		JSONObject objmain = new JSONObject();
		objmain.put("dataList",orderService.getAccessoriesRecyclingData(query));
		return objmain;
	}

	@ResponseBody
	@RequestMapping(value = "/getAccessoriesRecyclingDataWithSize",method = RequestMethod.GET)
	public JSONObject getAccessoriesRecyclingDataWithSize(String query,String query2){
		JSONObject objmain = new JSONObject();
		objmain.put("dataList",orderService.getAccessoriesRecyclingDataWithSize(query, query2));
		return objmain;
	}

	@ResponseBody
	@RequestMapping(value = "/insertAccessoriesIndent",method=RequestMethod.POST)
	public JSONObject insertAccessoriesIndent(AccessoriesIndent v) {
		JSONObject objmain = new JSONObject();
		JSONArray mainarray = new JSONArray();
		boolean insert= orderService.insertAccessoriesIndent(v);

		if(insert) {
			List<AccessoriesIndent>qty=orderService.getAccessoriesIndent(v.getPo(),v.getStyle(),v.getItemname(),v.getItemColor());

			for (int i = 0; i < qty.size(); i++) {
				JSONObject obj=new JSONObject();

				obj.put("autoId", qty.get(i).getAutoid());
				obj.put("po", qty.get(i).getPo());
				obj.put("style", qty.get(i).getStyle());
				obj.put("itemname", qty.get(i).getItemname());
				obj.put("itemcolor", qty.get(i).getItemColor());
				obj.put("shippingmark", qty.get(i).getShippingmark());
				obj.put("accessoriesName", qty.get(i).getAccessoriesName());
				obj.put("sizeName", qty.get(i).getSizeName());
				obj.put("requiredUnitQty", qty.get(i).getRequiredUnitQty());
				mainarray.add(obj);

			}

			objmain.put("result", mainarray);
			System.out.println(" obj main "+objmain);

			return objmain;
		}
		else {
			return null;
		}

	}

	@ResponseBody
	@RequestMapping(value = "/InstallDataAsSameParticular",method=RequestMethod.POST)
	public JSONObject InstallDataAsSameParticular(String userId,String purchaseOrder,String styleId,String itemId,String colorId,String installAccessories,String forAccessories) {
		JSONObject objmain = new JSONObject();
		JSONArray mainarray = new JSONArray();
		boolean insert= orderService.InstallDataAsSameParticular(userId,purchaseOrder,styleId,itemId,colorId,installAccessories,forAccessories);

		if(insert) {
			List<AccessoriesIndent>qty=orderService.getAccessoriesIndent(purchaseOrder,styleId,itemId,colorId);

			for (int i = 0; i < qty.size(); i++) {
				JSONObject obj=new JSONObject();

				obj.put("autoId", qty.get(i).getAutoid());
				obj.put("po", qty.get(i).getPo());
				obj.put("style", qty.get(i).getStyle());
				obj.put("itemname", qty.get(i).getItemname());
				obj.put("itemcolor", qty.get(i).getItemColor());
				obj.put("shippingmark", qty.get(i).getShippingmark());
				obj.put("accessoriesName", qty.get(i).getAccessoriesName());
				obj.put("sizeName", qty.get(i).getSizeName());
				obj.put("requiredUnitQty", qty.get(i).getRequiredUnitQty());
				mainarray.add(obj);

			}

			objmain.put("result", mainarray);
			System.out.println(" obj main "+objmain);

			return objmain;
		}
		else {
			return null;
		}

	}

	@ResponseBody
	@RequestMapping(value = "/confirmAccessoriesIndent",method=RequestMethod.POST)
	public JSONObject confirmAccessoriesIndent(String accessoriesIndentId,String accessoriesItems) {
		JSONObject objmain = new JSONObject();

		String result = orderService.confirmAccessoriesIndent(accessoriesIndentId,accessoriesItems);
		objmain.put("result", result);

		return objmain;
	}

	@ResponseBody
	@RequestMapping(value = "/accessoriesIndentInfo",method=RequestMethod.GET)
	public String accessoriesIndentInfo(String aiNo) {
		this.AiNo=aiNo;
		return "Success";
	}


	@RequestMapping(value = "/printAccessoriesIndent/{accIndentId}",method=RequestMethod.GET)
	public @ResponseBody ModelAndView printAccessoriesIndent(ModelMap map,@PathVariable ("accIndentId") String accIndentId) {
		ModelAndView view=new ModelAndView("order/printAccessoriesIndent");
		map.addAttribute("AiNo", accIndentId);
		return view;
	}


	@ResponseBody
	@RequestMapping(value = "/editAccessoriesIndent",method=RequestMethod.POST)
	public String editAccessoriesIndent(AccessoriesIndent v) {
		String msg= "something wrong";
		boolean update= orderService.editAccessoriesIndent(v);
		if(update) {
			msg="successfull";
		}


		return msg;
	}
	
	@ResponseBody
	@RequestMapping(value = "/newEditAccessoriesIndent",method=RequestMethod.POST)
	public String newEditAccessoriesIndent(String changedIndentList) {
		String msg= "something wrong";
		
		msg=orderService.newEditAccessoriesIndent(changedIndentList);

		return msg;
	}

	@ResponseBody
	@RequestMapping(value = "/deleteAccessoriesIndent",method=RequestMethod.POST)
	public String deleteAccessoriesIndent(String accessoriesIndentId,String indentAutoId) {
		String msg= "something wrong";
		boolean update= orderService.deleteAccessoriesIndent(accessoriesIndentId, indentAutoId);
		if(update) {
			msg="successfull";
		}


		return msg;
	}


	@ResponseBody
	@RequestMapping(value = "/getAccessoriesIndentList",method=RequestMethod.GET)
	public JSONObject getAccessoriesIndentList(String accessoriesIndentId) {
		JSONObject objmain = new JSONObject();
		//JSONArray mainarray = new JSONArray();

		List<AccessoriesIndent>list=orderService.getAccessoriesIndentItemList(accessoriesIndentId);

		/*
		 * for (int i = 0; i < list.size(); i++) { JSONObject obj=new JSONObject();
		 * 
		 * obj.put("autoid", list.get(i).getAutoid()); obj.put("po",
		 * list.get(i).getPo()); obj.put("style", list.get(i).getStyle());
		 * obj.put("itemname", list.get(i).getItemname()); obj.put("itemcolor",
		 * list.get(i).getItemcolor()); obj.put("shippingmark",
		 * list.get(i).getShippingmark()); obj.put("accessoriesname",
		 * list.get(i).getAccessoriesName()); obj.put("sizeName",
		 * list.get(i).getSizeName()); obj.put("accessoriessize",
		 * list.get(i).getAccessoriessize());
		 * obj.put("perunit",df.format(Double.parseDouble( list.get(i).getPerunit())));
		 * obj.put("totalbox",
		 * df.format(Double.parseDouble(list.get(i).getTotalbox())));
		 * obj.put("orderqty",
		 * df.format(Double.parseDouble(list.get(i).getOrderqty())));
		 * obj.put("qtyindozen",
		 * df.format(Double.parseDouble(list.get(i).getQtyindozen())));
		 * obj.put("reqperpcs",
		 * df.format(Double.parseDouble(list.get(i).getReqperpcs())));
		 * obj.put("reqperdozen",
		 * df.format(Double.parseDouble(list.get(i).getReqperdozen())));
		 * obj.put("dividedby",
		 * df.format(Double.parseDouble(list.get(i).getDividedby())));
		 * obj.put("extrainpercent",
		 * df.format(Double.parseDouble(list.get(i).getExtrainpercent())));
		 * obj.put("percentqty",
		 * df.format(Double.parseDouble(list.get(i).getPercentqty())));
		 * obj.put("totalqty",
		 * df.format(Double.parseDouble(list.get(i).getTotalqty()))); obj.put("unit",
		 * list.get(i).getUnit()); obj.put("requiredUnitQty",
		 * df.format(Double.parseDouble(list.get(i).getRequiredUnitQty())));
		 * obj.put("indentBrandId", list.get(i).getIndentBrandId());
		 * obj.put("indentColorId", list.get(i).getIndentColorId());
		 * 
		 * obj.put("indentColorId", list.get(i).getIndentColorId());
		 * 
		 * mainarray.add(obj);
		 * 
		 * }
		 */

		objmain.put("result", list);

		return objmain;

	}

	//zipper_indent
	@RequestMapping(value = "/zipper_indent",method=RequestMethod.GET)
	public ModelAndView zipper_indent(ModelMap map,HttpSession session) {


		String userId=(String)session.getAttribute("userId");
		String userName=(String)session.getAttribute("userName");

		List<CommonModel>purchaseorders=orderService.getPurchaseOrders(userId);

		//List<AccessoriesIndent>listAccPending=orderService.getPendingAccessoriesIndent();
		List<BuyerModel> buyerList= registerService.getAllBuyers(userId);
		List<CommonModel>accessoriesitem=orderService.AccessoriesItem("1");

		List<AccessoriesIndent>listAccPostedData=orderService.getPostedZipperIndent(userId);

		//List<commonModel>unit=orderService.Unit();
		List<CommonModel>brand=orderService.Brands();
		List<CommonModel>color=orderService.AllColors();
		ModelAndView view = new ModelAndView("order/zipper_indent");
		view.addObject("purchaseorders",purchaseorders);
		view.addObject("accessories",accessoriesitem);
		view.addObject("buyerList",buyerList);
		view.addObject("brand",brand);
		view.addObject("unitList",registerService.getUnitList());
		view.addObject("color",color);
		view.addObject("listAccPostedData",listAccPostedData);

		map.addAttribute("userId",userId);
		map.addAttribute("userName",userName);

		//view.addObject("listAccPending",listAccPending);

		return view; //JSP - /WEB-INF/view/index.jsp
	}

	@ResponseBody
	@RequestMapping(value = "/confirmZipperIndent",method=RequestMethod.POST)
	public JSONObject confirmZipperIndent(String zipperIndentId,String zipperItems) {
		JSONObject objmain = new JSONObject();

		String result = orderService.confirmZipperIndent(zipperIndentId,zipperItems);
		objmain.put("result", result);

		return objmain;
	}

	@ResponseBody
	@RequestMapping(value = "/getZipperIndentList",method=RequestMethod.GET)
	public JSONObject getZipperIndentList(String zipperIndentId) {
		JSONObject objmain = new JSONObject();
		//JSONArray mainarray = new JSONArray();

		List<AccessoriesIndent>list=orderService.getZipperIndentItemList(zipperIndentId);

		objmain.put("result", list);

		return objmain;

	}

	@ResponseBody
	@RequestMapping(value = "/editZipperIndent",method=RequestMethod.POST)
	public String editZipperIndent(AccessoriesIndent v) {
		String msg= "something wrong";
		boolean update= orderService.editZipperIndent(v);
		if(update) {
			msg="successfull";
		}


		return msg;
	}

	@ResponseBody
	@RequestMapping(value = "/deleteZipperIndent",method=RequestMethod.POST)
	public String deleteZipperIndent(String zipperIndentId,String indentAutoId) {
		String msg= "something wrong";
		boolean update= orderService.deleteZipperIndent(zipperIndentId, indentAutoId);
		if(update) {
			msg="successfull";
		}
		return msg;
	}

	//accessories_indent_curton 
	@RequestMapping(value = "/accessories_indent_curton",method=RequestMethod.GET)
	public ModelAndView accessories_indent_curton(ModelMap map,HttpSession session) {

		String userId=(String)session.getAttribute("userId");
		String userName=(String)session.getAttribute("userName");

		List<CommonModel>purchaseorders=orderService.getPurchaseOrders(userId);
		List<BuyerModel> buyerList= registerService.getAllBuyers(userId);
		List<AccessoriesIndentCarton> indentList=orderService.getAllAccessoriesCartonData(userId);
		//List<CommonModel>unit=orderService.Unit();

		ModelAndView view = new ModelAndView("order/accessories_indent_curton");
		view.addObject("purchaseorders",purchaseorders);
		view.addObject("buyerList",buyerList);
		view.addObject("indentList",indentList);

		map.addAttribute("userId",userId);
		map.addAttribute("userName",userName);
		//view.addObject("unit",unit);
		//map.addAttribute("styleList",styleList);

		return view; //JSP - /WEB-INF/view/index.jsp
	}


	@ResponseBody
	@RequestMapping(value = "/confirmCartonIndent",method=RequestMethod.POST)
	public JSONObject confirmCartonIndent(String cartonIndentId,String cartonItems) {
		JSONObject objmain = new JSONObject();

		objmain.put("result", orderService.confirmCartonIndent(cartonIndentId, cartonItems));

		return objmain;

	}

	@RequestMapping(value = "/searchCartonIndent",method=RequestMethod.GET)
	public @ResponseBody JSONObject searchCartonIndent(String indentId) {
		JSONObject objmain = new JSONObject();
		List<AccessoriesIndentCarton> cartonIndentList  = orderService.getAccessoriesIndentCartonItemDetails(indentId);
		objmain.put("cartonIndentList",cartonIndentList);
		return objmain;
	}

	@ResponseBody
	@RequestMapping(value = "/saveAccessoriesCurton",method=RequestMethod.POST)
	public JSONObject saveAccessoriesCurton(AccessoriesIndentCarton v) {
		JSONObject objmain = new JSONObject();
		JSONArray mainarray = new JSONArray();
		boolean insert= orderService.saveAccessoriesCurton(v);

		if(insert) {
			List<AccessoriesIndentCarton>qty=orderService.getAccessoriesIndentCarton(v.getPoNo(),v.getStyle(),v.getItem(),v.getItemColor());

			for (int i = 0; i < qty.size(); i++) {
				JSONObject obj=new JSONObject();

				obj.put("autoId", qty.get(i).getAutoid());
				obj.put("poNo", qty.get(i).getPoNo());
				obj.put("style", qty.get(i).getStyle());
				obj.put("itemName", qty.get(i).getItem());
				obj.put("itemColor", qty.get(i).getItemColor());
				obj.put("shippingMark", qty.get(i).getShippingMark());
				obj.put("accessoriesName", qty.get(i).getAccessoriesItem());
				obj.put("cartonSize", qty.get(i).getAccessoriesSize());
				obj.put("totalQty", df.format(Double.parseDouble(qty.get(i).getTotalQty())));
				mainarray.add(obj);

			}

			objmain.put("result", mainarray);
			System.out.println(" obj main "+objmain);

			return objmain;
		}
		else {
			return null;
		}
	}

	@ResponseBody
	@RequestMapping(value = "/editAccessoriesCarton",method=RequestMethod.POST)
	public JSONObject editAccessoriesCurton(AccessoriesIndentCarton v) {
		JSONObject objmain = new JSONObject();
		if(orderService.editAccessoriesCarton(v)) {
			objmain.put("result","Successful");
		}else {
			objmain.put("result", "Something Wrong");
		}
		return objmain;
	}

	@RequestMapping(value = "/deleteCartonIndent",method=RequestMethod.GET)
	public @ResponseBody JSONObject deleteCartonIndent(String autoId,String indentId) {
		JSONObject objmain = new JSONObject();

		objmain.put("result",orderService.deleteAccessoriesCarton(autoId, indentId));
		return objmain;
	}

	@ResponseBody
	@RequestMapping(value = "/cartonIndentReportView/{indentId}",method=RequestMethod.GET)
	public ModelAndView cartonIndentReportView(ModelAndView map,@PathVariable("indentId") String indentId) {

		ModelAndView view = new ModelAndView("order/cartonIndentReport");
		view.addObject("indentId",indentId);
		return view;

	}
	@ResponseBody
	@RequestMapping(value = "/getAllAccessoriesCartonData",method=RequestMethod.POST)
	public JSONObject getAllAccessoriesCartonData(AccessoriesIndentCarton v) {
		JSONObject objmain = new JSONObject();
		JSONArray mainarray = new JSONArray();

		List<AccessoriesIndentCarton>qty=orderService.getAllAccessoriesCartonData(userId);

		for (int i = 0; i < qty.size(); i++) {
			JSONObject obj=new JSONObject();

			obj.put("autoId", qty.get(i).getAutoid());
			obj.put("poNo", qty.get(i).getPoNo());
			obj.put("style", qty.get(i).getStyle());
			obj.put("itemName", qty.get(i).getItem());
			obj.put("itemColor", qty.get(i).getItemColor());
			obj.put("shippingMark", qty.get(i).getShippingMark());
			obj.put("accessoriesName", qty.get(i).getAccessoriesItem());
			obj.put("cartonSize", qty.get(i).getAccessoriesSize());
			obj.put("totalQty", df.format(Double.parseDouble(qty.get(i).getTotalQty())));
			mainarray.add(obj);

		}

		objmain.put("result", mainarray);
		System.out.println(" obj main "+objmain);

		return objmain;

	}


	@ResponseBody
	@RequestMapping(value = "/accessoriesCartonItemSet/{id}",method=RequestMethod.GET)
	public JSONObject accessoriesCartonItemSet(@PathVariable ("id") String id) {
		JSONObject objmain = new JSONObject();
		JSONArray mainarray = new JSONArray();

		//List<AccessoriesIndent>list=orderService.getAccessoriesIndentItemDetails(id);
		List<AccessoriesIndentCarton>list=orderService.getAccessoriesIndentCartonItemDetails(id);

		for (int i = 0; i < list.size(); i++) {
			JSONObject obj=new JSONObject();

			obj.put("autoid", list.get(i).getAutoid());
			obj.put("poNo", list.get(i).getPoNo());
			obj.put("style", list.get(i).getStyle());
			obj.put("item", list.get(i).getItem());
			obj.put("itemColor", list.get(i).getItemColor());
			obj.put("shippingMark", list.get(i).getShippingMark());
			obj.put("accessoriesItem", list.get(i).getAccessoriesItem());
			obj.put("accessoriesSize", list.get(i).getAccessoriesSize());
			obj.put("unit", list.get(i).getUnit());


			obj.put("orderqty",df.format(Double.parseDouble( list.get(i).getOrderqty())));

			obj.put("length1", df.format(Double.parseDouble(list.get(i).getLength1())));
			obj.put("width1", df.format(Double.parseDouble(list.get(i).getWidth1())));
			obj.put("height1", df.format(Double.parseDouble(list.get(i).getHeight1())));
			obj.put("add1", df.format(Double.parseDouble(list.get(i).getAdd1())));


			obj.put("length2", df.format(Double.parseDouble(list.get(i).getLength2())));
			obj.put("width2", df.format(Double.parseDouble(list.get(i).getWidth2())));
			obj.put("height2", df.format(Double.parseDouble(list.get(i).getHeight2())));
			obj.put("add2", df.format(Double.parseDouble(list.get(i).getAdd2())));

			obj.put("totalQty", df.format(Double.parseDouble(list.get(i).getTotalQty())));
			obj.put("devideBy", df.format(Double.parseDouble(list.get(i).getDevideBy())));
			obj.put("ply", df.format(Double.parseDouble(list.get(i).getPly())));



			mainarray.add(obj);

		}

		objmain.put("result", mainarray);
		System.out.println(" obj main "+objmain);

		return objmain;

	}


	//Fabrics Indent 
	@RequestMapping(value = "/fabrics_indent",method=RequestMethod.GET)
	public ModelAndView fabrics_indent(ModelMap map,HttpSession session) {

		String userId=(String)session.getAttribute("userId");
		String userName=(String)session.getAttribute("userName");

		List<FabricsIndent> fabricindentsummarylist= orderService.getStyleDetailsForFabricsIndent(userId);
		List<CommonModel>purchaseorders=orderService.getPurchaseOrders(userId);
		List<Color> colorList = registerService.getColorList();
		List<FabricsItem> fabricsItemList = registerService.getFabricsItemList();
		List<Brand> brandList = registerService.getBrandList();
		//List<Unit> unitList = registerService.getUnitList();
		//List<FabricsIndent> fabricsIndentList = orderService.getFabricsIndentList();
		ModelAndView view = new ModelAndView("order/fabrics-indent");

		view.addObject("fabricindentsummarylist", fabricindentsummarylist);
		view.addObject("purchaseorders", purchaseorders);
		view.addObject("fabricsList",fabricsItemList);
		view.addObject("colorList",colorList);
		view.addObject("brandList",brandList);

		map.addAttribute("userId",userId);
		map.addAttribute("userName",userName);

		//view.addObject("unitList",unitList);
		//view.addObject("fabricsIndentList",fabricsIndentList);
		return view; //JSP - /WEB-INF/view/index.jsp
	}

	@RequestMapping(value = "/confirmFabricsIndent",method=RequestMethod.POST)
	public @ResponseBody JSONObject confirmFabricsIndent(String	fabricsIndentId,String fabricsItems) {
		JSONObject objmain = new JSONObject();

		objmain.put("result",orderService.confirmFabricsIndent(fabricsIndentId, fabricsItems));

		return objmain;
	}

	@RequestMapping(value = "/editFabricsIndent",method=RequestMethod.POST)
	public @ResponseBody JSONObject editFabricsIndent(FabricsIndent	fabricsIndent) {
		JSONObject objmain = new JSONObject();

		if(orderService.editFabricsIndent(fabricsIndent)) {
			objmain.put("result","Successful");
		}else {
			objmain.put("result", "Something Wrong");
		}


		return objmain;
	}

	@RequestMapping(value = "/searchFabricsIndent",method=RequestMethod.GET)
	public @ResponseBody JSONObject searchFabricsIndent(String indentId) {
		JSONObject objmain = new JSONObject();
		List<FabricsIndent> fabricsIndentList  = orderService.getFabricsIndent(indentId);
		objmain.put("fabricsIndentList",fabricsIndentList);
		return objmain;
	}

	@RequestMapping(value = "/deleteFabricsIndent",method=RequestMethod.GET)
	public @ResponseBody JSONObject deleteFabricsIndent(String autoId,String indentId) {
		JSONObject objmain = new JSONObject();

		objmain.put("result",orderService.deleteFabricsIndent(autoId, indentId));
		return objmain;
	}

	@RequestMapping(value = "/getFabricsIndent",method=RequestMethod.GET)
	public @ResponseBody JSONObject getFabricsIndent(String autoId) {
		JSONObject objmain = new JSONObject();
		FabricsIndent fabricsIndent = orderService.getFabricsIndentInfo(autoId);
		objmain.put("fabricsIndent",fabricsIndent);
		return objmain;
	}

	@ResponseBody
	@RequestMapping(value = "/fabricsIndentReportView/{indentId}",method=RequestMethod.GET)
	public ModelAndView fabricsIndentReportView(ModelAndView map,@PathVariable("indentId") String indentId) {

		ModelAndView view = new ModelAndView("order/fabricsIndentReport");
		view.addObject("indentId",indentId);
		return view;

	}

	@RequestMapping(value = "/getPOWiseStyleLoad",method=RequestMethod.GET)
	public @ResponseBody JSONObject getPOWiseStyleLoad(String purchaseOrder) {
		JSONObject objmain = new JSONObject();
		List<Style> styleList = orderService.getPOWiseStyleList(purchaseOrder);
		objmain.put("styleList",styleList);
		return objmain;
	}

	@RequestMapping(value = "/getStyleItemWiseColor",method=RequestMethod.GET)
	public @ResponseBody JSONObject getItemWiseColor(String styleId,String itemId) {
		JSONObject objmain = new JSONObject();
		List<Color> colorList = orderService.getStyleItemWiseColor(styleId,itemId);
		objmain.put("colorList",colorList);
		return objmain;
	}

	@RequestMapping(value = "/getOrderQtyByPOStyleItemAndColor",method=RequestMethod.GET)
	public @ResponseBody JSONObject getOrderQtyByPOStyleItemAndColor(String purchaseOrder,String styleId,String itemId,String colorId) {
		JSONObject objmain = new JSONObject();
		double orderQty = orderService.getOrderQuantity(purchaseOrder,styleId,itemId,colorId);
		objmain.put("orderQuantity",orderQty);
		objmain.put("dozenQuantity", orderQty/12);
		return objmain;
	}

	@RequestMapping(value = "/getOrderQtyByMultipleId",method=RequestMethod.GET)
	public @ResponseBody JSONObject getOrderQtyByMultipleId(String purchaseOrder,String styleId,String itemId,String colorId) {
		JSONObject objmain = new JSONObject();
		double orderQty = orderService.getOrderQuantityByMultipleId(purchaseOrder,styleId,itemId,colorId);
		objmain.put("orderQuantity",orderQty);
		objmain.put("dozenQuantity", orderQty/12);
		return objmain;
	}

	@RequestMapping(value = "/getUnitValue",method=RequestMethod.GET)
	public @ResponseBody JSONObject getUnitValue(String unitId) {
		JSONObject objmain = new JSONObject();
		Unit unit = registerService.getUnit(unitId);
		objmain.put("unitValue",unit.getUnitValue());
		return objmain;
	}


	//Buyer Purchase Order Create
	@RequestMapping(value = "/sample_requisition",method=RequestMethod.GET)
	public ModelAndView sample_requisition(ModelMap map,HttpSession session) {

		String userId=(String)session.getAttribute("userId");
		String userName=(String)session.getAttribute("userName");

		ModelAndView view = new ModelAndView("order/sample_requisition");
		List<SizeGroup> groupList = registerService.getStyleSizeGroupList();
		List<BuyerModel> buyerList= registerService.getAllBuyers(userId);
		List<Style> styleList= orderService.getStyleList(userId);
		List<Color> colorList = registerService.getColorList();
		//List<FactoryModel> factoryList = registerSfervice.getAllFactories();
		List<SampleRequisitionItem> sampleReqList = orderService.getSampleRequisitionList(userId);
		List<CommonModel> sampleList = orderService.getSampleList();
		//List<CommonModel> inchargeList = orderService.getInchargeList();
		List<CommonModel> sampleEmpList = orderService.getSampleEmployeeList();
		List<CommonModel> merchendizerList = orderService.getMerchendizerList();

		view.addObject("groupList",groupList);
		view.addObject("buyerList",buyerList);
		view.addObject("styleList",styleList);
		view.addObject("colorList",colorList);
		//view.addObject("factoryList",factoryList);
		view.addObject("sampleReqList",sampleReqList);
		view.addObject("sampleList",sampleList);
		view.addObject("sampleEmpList",sampleEmpList);
		view.addObject("merchendizerList",merchendizerList);

		map.addAttribute("userId",userId);
		map.addAttribute("userName",userName);
		return view; //JSP - /WEB-INF/view/index.jsp
	}

	
	@ResponseBody
	@RequestMapping(value = "/getStyleWisePurchaseOrder/{styleId}",method=RequestMethod.GET)
	public JSONObject getStyleWisePurchaseOrder(@PathVariable ("styleId") String styleId) {

		System.out.println("Wise");
		JSONObject objmain = new JSONObject();
		JSONArray mainarray = new JSONArray();

		List<CommonModel>po=orderService.getStyleWisePurchaseOrder(styleId);

		for (int i = 0; i < po.size(); i++) {
			JSONObject obj=new JSONObject();

			obj.put("id", po.get(i).getId());
			obj.put("name", po.get(i).getName());

			mainarray.add(obj);

		}

		objmain.put("result", mainarray);
		System.out.println(" obj main "+objmain);

		return objmain;

	}
	
	@ResponseBody
	@RequestMapping(value = "/getAllColor",method=RequestMethod.POST)
	public JSONObject getAllColor() {

		JSONObject objmain = new JSONObject();
		JSONArray mainarray = new JSONArray();


		List<Color>items=registerService.getColorList();

		for (int i = 0; i < items.size(); i++) {
			JSONObject obj=new JSONObject();

			obj.put("id", items.get(i).getColorId());
			obj.put("name", items.get(i).getColorName());

			mainarray.add(obj);

		}

		objmain.put("result", mainarray);


		return objmain;

	}

	@RequestMapping(value = "/addItemToSampleRequisition",method=RequestMethod.POST)
	public @ResponseBody JSONObject addItemToSampleRequisition(SampleRequisitionItem v) {
		JSONObject objmain = new JSONObject();

		if(!orderService.checkDoplicateSampleRequisition(v)) {
			if(orderService.addItemToSampleRequisition(v)) {
				JSONArray mainArray = new JSONArray();
				List<SampleRequisitionItem> sampleList = orderService.getIncomepleteSampleRequisitionItemList(v.getUserId());
				//List<SampleRequisitionItem> sampleList = orderService.getSampleRequisitionItemList(v.getUserId());
				objmain.put("result",sampleList);
			}else {
				objmain.put("result", "Something Wrong");
			}
		}
		else {
			objmain.put("result", "Doplicate sample requisition never be save");
		}
		

		return objmain;
	}

	@RequestMapping(value = "/editItemToSampleRequisition",method=RequestMethod.POST)
	public @ResponseBody JSONObject editItemToSampleRequisition(SampleRequisitionItem v) {
		JSONObject objmain = new JSONObject();

		if(orderService.editItemToSampleRequisition(v)) {
			JSONArray mainArray = new JSONArray();
			List<SampleRequisitionItem> sampleList = orderService.getSampleRequisitionDetails(v.getSampleReqId());
			objmain.put("result",sampleList);
		}else {
			objmain.put("result", "Something Wrong");
		}

		return objmain;
	}

	@RequestMapping(value = "/userWiseNullSampleReqDataList",method=RequestMethod.POST)
	public @ResponseBody JSONObject userWiseNullSampleReqDataList(String userId) {
		JSONObject objmain = new JSONObject();

		System.out.println("SampleIncompleteData");
		JSONArray mainArray = new JSONArray();
		List<SampleRequisitionItem> sampleList = orderService.getIncomepleteSampleRequisitionItemList(userId);
		//List<SampleRequisitionItem> sampleList = orderService.getSampleRequisitionItemList(v.getUserId());
		objmain.put("result",sampleList);

		System.out.println("SampleIncompleteData Size"+sampleList.size());

		return objmain;
	}

	@RequestMapping(value = "/confirmItemToSampleRequisition",method=RequestMethod.POST)
	public @ResponseBody String confirmItemToSampleRequisition(SampleRequisitionItem v) {
		String msg="Create Occue while confrim sample requisition";

		if(orderService.confirmItemToSampleRequisition(v)) {
			msg="Sample Requisition Confrim Successfully";
		}

		return msg;
	}

	@RequestMapping(value = "/searchSampleRequisition",method=RequestMethod.POST)
	public @ResponseBody JSONObject searchSampleRequisition(String sampleReqId,String user) {
		JSONObject objmain = new JSONObject();

		JSONArray mainArray = new JSONArray();
		List<SampleRequisitionItem> sampleList = orderService.getSampleRequisitionDetails(sampleReqId);
		//List<FileUpload>filelist=orderService.findsamplecadfiles(user, sampleReqId);
		objmain.put("result",sampleList);
		//objmain.put("files",filelist);

		return objmain;
	}

	@RequestMapping(value = "/deleteSampleRequisitionItem",method=RequestMethod.POST)
	public @ResponseBody JSONObject deleteSampleRequisitionItem(String sapleAutoId,String sampleReqId) {
		JSONObject objmain = new JSONObject();
		JSONArray mainArray = new JSONArray();

		System.out.println("sampleReqId "+sampleReqId);

		if(!sampleReqId.equals("0")) {
			System.out.println("HassampleReqId "+sampleReqId);
			objmain.put("result","Sorry You are already confirm sample requisition");
		}
		else {
			boolean flag=orderService.deleteSampleRequisitionItem(sapleAutoId);
			if(flag) {
				List<SampleRequisitionItem> sampleList = orderService.getSampleRequisitionDetails(sampleReqId);
				objmain.put("result",sampleList);
			}
			else{

				objmain.put("result","Something has wrong!!");
			}
		}

		return objmain;
	}

	@RequestMapping(value = "/getSampleRequistionItemData",method=RequestMethod.POST)
	public @ResponseBody JSONObject getSampleRequistionItemData(String itemAutoId) {
		JSONObject objmain = new JSONObject();
		JSONArray mainArray = new JSONArray();
		List<SampleRequisitionItem> sampleRequistionItemList = orderService.getSampleRequistionItemData(itemAutoId);
		System.out.println("Size "+sampleRequistionItemList.size());

		objmain.put("result",sampleRequistionItemList);

		return objmain;
	}

	@ResponseBody
	@RequestMapping(value = "/sampleRequisitionInfo",method=RequestMethod.GET)
	public String sampleRequisitionInfo(String sampleReqId) {
		this.SampleReqId=sampleReqId;
		return "Success";
	}


	@RequestMapping(value = "/printDateWiseSampleRequsition/{idList}",method=RequestMethod.GET)
	public @ResponseBody ModelAndView printDateWiseSampleRequsition(ModelMap map,@PathVariable ("idList") String idList) {

		ModelAndView view=new ModelAndView("order/printDateWiseSampleRequsition");

		String id[] = idList.split("@");
		map.addAttribute("date", id[0]);
		map.addAttribute("userId", id[1]);

		return view;
	}

	
	@RequestMapping(value = "/printDateWiseAllSampleRequsition/{idList}",method=RequestMethod.GET)
	public @ResponseBody ModelAndView printDateWiseAllSampleRequsition(ModelMap map,@PathVariable ("idList") String idList) {
		
		ModelAndView view=new ModelAndView("order/printDateWiseAllSampleRequsition");
		
		String id[] = idList.split("@");
		map.addAttribute("date", id[0]);

		
		return view;
	}
	
	@RequestMapping(value = "/printsampleRequisition",method=RequestMethod.GET)
	public @ResponseBody ModelAndView printsampleRequisition(ModelMap map) {


		ModelAndView view=new ModelAndView("order/printsampleRequisition");


		map.addAttribute("SampleReqId", SampleReqId);


		return view;
	}

	//Purchase Order
	@RequestMapping(value = "/purchase_order",method=RequestMethod.GET)
	public ModelAndView purchase_order(ModelMap map,HttpSession session) {


		String userId=(String)session.getAttribute("userId");
		String userName=(String)session.getAttribute("userName");

		ModelAndView view = new ModelAndView("order/purchase-order");
		List<BuyerModel> buyerList= registerService.getAllBuyers(userId);
		List<Factory> factoryList = registerService.getFactoryNameList();
		//List<MerchandiserInfo> merchandiserList = registerService.getMerchandiserList();
		List<SupplierModel> supplierList = registerService.getAllSupplier();
		List<PurchaseOrder> purchaseOrderList = orderService.getPurchaseOrderSummeryList(userId);
		List<CommonModel> pendingIndentList = orderService.getPendingIndentList(userId);
		//view.addObject("poList",poList);
		view.addObject("factoryList",factoryList);
		view.addObject("buyerList",buyerList);
		view.addObject("supplierList",supplierList);
		view.addObject("purchaseOrderList",purchaseOrderList);
		view.addObject("pendingIndentList",pendingIndentList);
		map.addAttribute("userId",userId);
		map.addAttribute("userName",userName);
		return view; //JSP - /WEB-INF/view/index.jsp
	}

	@RequestMapping(value = "/getIndentItems",method=RequestMethod.GET)
	public @ResponseBody JSONObject getIndentItems(String indentId,String indentType) {
		JSONObject objmain = new JSONObject();
		List<AccessoriesItem>  itemList = orderService.getIndentItems(indentId, indentType);
		List<Style>  styleList = orderService.getIndentStyles(indentId, indentType);
		objmain.put("itemList", itemList);
		objmain.put("styleList", styleList);
		return objmain;
	}
	@RequestMapping(value = "/getTypeWiseIndentItems",method=RequestMethod.GET)
	public @ResponseBody JSONObject getTypeWiseIndentItems(String purchaseOrder,String styleId,String type) {
		JSONObject objmain = new JSONObject();
		List<AccessoriesItem>  itemList = orderService.getTypeWiseIndentItems(purchaseOrder,styleId,type);
		objmain.put("itemList", itemList);
		return objmain;
	}

	@RequestMapping(value = "/addIndentItem",method=RequestMethod.GET)
	public @ResponseBody JSONObject addIndentItem(AccessoriesIndent accessoriesIndent) {
		JSONObject objmain = new JSONObject();
		List<PurchaseOrderItem> poItemList;
		if(accessoriesIndent.getStyleId().equals("0"))
			poItemList = orderService.getPurchaseOrderItemList(accessoriesIndent);
		else
			poItemList = orderService.getPurchaseOrderItemListByStyleId(accessoriesIndent);
		objmain.put("poItemList", poItemList);
		return objmain;
	}

	@RequestMapping(value = "/submitPurchaseOrder",method=RequestMethod.POST)
	public @ResponseBody JSONObject submitPurchaseOrder(PurchaseOrder	purchaseOrder) {
		JSONObject objmain = new JSONObject();
		if(orderService.submitPurchaseOrder(purchaseOrder)) {
			objmain.put("result", "successfull");
		}else {
			objmain.put("result", "duplicate");
		}
		return objmain;
	}

	@RequestMapping(value = "/editPurchaseOrder",method=RequestMethod.POST)
	public @ResponseBody JSONObject editPurchaseOrder(PurchaseOrder	purchaseOrder) {
		JSONObject objmain = new JSONObject();
		if(orderService.editPurchaseOrder(purchaseOrder)) {
			objmain.put("result", "successfull");
		}else {
			objmain.put("result", "duplicate");
		}
		return objmain;
	}

	@RequestMapping(value = "/searchPurchaseOrder",method=RequestMethod.GET)
	public @ResponseBody JSONObject searchPurchaseOrder(String poNo,String poType) {
		JSONObject objmain = new JSONObject();
		PurchaseOrder  purchaseOrder = orderService.getPurchaseOrder(poNo,poType);
		objmain.put("poInfo", purchaseOrder);
		return objmain;
	}

	@RequestMapping(value="/getPurchaseOrderReport/{poNo}/{supplierId}/{type}/{previewType}")
	public @ResponseBody ModelAndView getPurchaseOrderReport(ModelMap map,@PathVariable String poNo,@PathVariable String supplierId,@PathVariable String type,@PathVariable String previewType) {

		ModelAndView view = new ModelAndView("order/purchaseOrderReportView");
		System.out.println("null test"+poNo+" "+supplierId+" "+type);
		map.addAttribute("poNo",poNo);
		map.addAttribute("supplierId",supplierId);
		map.addAttribute("type",type);
		map.addAttribute("previewType",previewType);
		map.addAttribute("landscapeCheck","false");
		map.addAttribute("brandCheck","false");
		map.addAttribute("sqCheck","false");
		map.addAttribute("skuCheck","false");
		return view;
	}

	@RequestMapping(value="/getPurchaseOrderReport/{poNo}/{supplierId}/{type}/{previewType}/{data}")
	public @ResponseBody ModelAndView getPurchaseOrderReportWithData(ModelMap map,@PathVariable String poNo,@PathVariable String supplierId,@PathVariable String type,@PathVariable String previewType,@PathVariable String data) {

		ModelAndView view = new ModelAndView("order/purchaseOrderReportView");
		System.out.println("null test"+poNo+" "+supplierId+" "+type);
		map.addAttribute("poNo",poNo);
		map.addAttribute("supplierId",supplierId);
		map.addAttribute("type",type);
		map.addAttribute("previewType",previewType);

		String[] dataList = data.split("@");
		map.addAttribute("landscapeCheck",dataList[0]);
		map.addAttribute("brandCheck",dataList[1]);
		map.addAttribute("sqCheck",dataList[2]);
		map.addAttribute("skuCheck",dataList[3]);
		return view;
	}

	@RequestMapping(value="/getPurchaseOrderGeneralReport/{poNo}/{supplierId}/{type}")
	public @ResponseBody ModelAndView getPurchaseOrderGeneralReport(ModelMap map,@PathVariable String poNo,@PathVariable String supplierId,@PathVariable String type) {

		ModelAndView view = new ModelAndView("order/purchaseOrderReportView");
		System.out.println("null test"+poNo+" "+supplierId+" "+type);
		map.addAttribute("poNo",poNo);
		map.addAttribute("supplierId",supplierId);
		map.addAttribute("type",type);
		map.addAttribute("previewType","general");
		map.addAttribute("landscapeCheck","false");
		return view;
	}


	//Sample Production
	@RequestMapping(value = "/sample_production",method=RequestMethod.GET)
	public ModelAndView sample_production(ModelMap map,HttpSession session) {

		String userId=(String)session.getAttribute("userId");
		String userName=(String)session.getAttribute("userName");
		ModelAndView view = new ModelAndView("order/sample_production");
		
		List<Employee> employeeList = registerService.getEmployeeList();
		List<SampleCadAndProduction>sampleCadList=orderService.getSampleComments(userId);	
		view.addObject("employeeList",employeeList);
		map.addAttribute("sampleCadList",sampleCadList);

		map.addAttribute("userId",userId);
		map.addAttribute("userName",userName);

		return view; //JSP - /WEB-INF/view/index.jsp
	}

	@RequestMapping(value = "/getSampleCommentsList",method=RequestMethod.GET)
	public @ResponseBody JSONObject getSampleCommentsList() {
		JSONObject objmain = new JSONObject();
		List<SampleCadAndProduction> sampleCommentsList = orderService.getSampleCommentsList();
		objmain.put("sampleCommentsList", sampleCommentsList);
		return objmain;
	}

	@RequestMapping(value = "/getSampleProductionInfo",method=RequestMethod.GET)
	public @ResponseBody JSONObject getSampleProductionInfo(String sampleCommentId,String sampleReqId) {
		JSONObject objmain = new JSONObject();
		
		List<SampleRequisitionItem> sampleRequisitionList = orderService.getSampleRequisitionAndCuttingDetails(sampleReqId,sampleCommentId);
		SampleCadAndProduction sampleProduction = orderService.getSampleProductionInfo(sampleCommentId);
		objmain.put("sampleProduction", sampleProduction);
		objmain.put("result_sample_requisition", sampleRequisitionList);

		return objmain;
	}

	@RequestMapping(value = "/postSampleProduction",method=RequestMethod.POST)
	public @ResponseBody JSONObject postSampleProduction(SampleCadAndProduction	sampleCadAndProduction) {
		JSONObject objmain = new JSONObject();
		
		
		if(orderService.postSampleProductionInfo(sampleCadAndProduction)) {
			objmain.put("result", "successfull");
		}else {
			objmain.put("result", "duplicate");
		}
		return objmain;
	}

	@RequestMapping(value="/getSampleProductionReport/{idList}",method=RequestMethod.GET)
	public @ResponseBody ModelAndView getSampleProductionReport(ModelMap map,@PathVariable ("idList") String idList) {

		ModelAndView view = new ModelAndView("order/sample-production-report-view");

		String id[] = idList.split("@");
		
		map.addAttribute("sampleCommentId",id[0]);
		map.addAttribute("printType",id[1]);

		return view;

	}

	@RequestMapping(value="/sampleProductionDateWiseReport/{idList}",method=RequestMethod.GET)
	public @ResponseBody ModelAndView sampleProductionDateWiseReport(ModelMap map,@PathVariable ("idList") String idList) {

		ModelAndView view = new ModelAndView("order/date-wise-sample-production-report-view");

		String id[] = idList.split("@");
		
		map.addAttribute("date",id[0]);
		map.addAttribute("reportType",id[1]);

		return view;

	}
	
	@RequestMapping(value = "style_create")
	public ModelAndView style_create(ModelMap map,HttpSession session) {

		String userId=(String)session.getAttribute("userId");
		String userName=(String)session.getAttribute("userName");

		ModelAndView view = new ModelAndView("order/style_create");
		List<BuyerModel> List= registerService.getAllBuyers(userId);
		List<ItemDescription> itemList= orderService.getItemDescriptionList();

		List<Style> styleList= orderService.getStyleWiseItemList(userId);

		map.addAttribute("buyerList",List);
		map.addAttribute("itemList",itemList);
		map.addAttribute("styleList",styleList);

		map.addAttribute("userId",userId);
		map.addAttribute("userName",userName);
		return view; //JSP - /WEB-INF/view/index.jsp
	}

	@ResponseBody
	@RequestMapping(value = "/submitStyleFiles", method = RequestMethod.POST)
	public ModelAndView submitFiles(@RequestParam String submit,@RequestParam String styleItemAutoId,@RequestParam String styleid,@RequestParam String buyerId,@RequestParam String hbuyerId,@RequestParam String itemId,@RequestParam String styleNo,@RequestParam String size,@RequestParam String date,@RequestParam MultipartFile frontImage,@RequestParam MultipartFile backImage,HttpSession session,Model map,RedirectAttributes attr) throws IOException, SQLException {
		String userId=(String)session.getAttribute("userId");
		if (submit.equals("1")) {
			boolean flag=orderService.SaveStyleCreate(userId,buyerId,itemId,styleNo,size,date,frontImage,backImage) ;
		}else {
			boolean flag=orderService.editStyle(styleItemAutoId, hbuyerId, itemId,styleid, styleNo, size, date, frontImage, backImage);
		}
		ModelAndView view=new ModelAndView("redirect:style_create");
		return view;
	}




	@ResponseBody
	@RequestMapping(value = "/buyerWisePoLoad/{buyerId}",method=RequestMethod.POST)
	public JSONObject buyerWisePoNo(@PathVariable ("buyerId") String buyerId) {

		JSONObject objmain = new JSONObject();
		JSONArray mainarray = new JSONArray();

		List<CommonModel>styles=orderService.BuyerWisePo(buyerId);

		for (int i = 0; i < styles.size(); i++) {
			JSONObject obj=new JSONObject();

			obj.put("id", styles.get(i).getId());
			obj.put("name", styles.get(i).getName());

			mainarray.add(obj);

		}

		objmain.put("result", mainarray);
		System.out.println(" obj main "+objmain);

		return objmain;

	}









	//Parcel
	@RequestMapping(value = "/parcel",method=RequestMethod.GET)
	public ModelAndView parcel(ModelMap map,HttpSession session) {

		String userId=(String)session.getAttribute("userId");
		String userName=(String)session.getAttribute("userName");

		ModelAndView view = new ModelAndView("order/parcel");
		List<BuyerModel> buyerList= registerService.getAllBuyers(userId);
		List<CommonModel> sampleList = orderService.getSampleList();
		List<Style> styleList= orderService.getStyleList(userId);
		List<CourierModel> courierList=orderService.getcourierList();
		List<Unit> unitList= registerService.getUnitList();	
		List<ParcelModel> parcelList= orderService.parcelList();	

		view.addObject("buyerList",buyerList);
		view.addObject("StyleList",styleList);
		view.addObject("sampletype",sampleList);
		view.addObject("courierList",courierList);
		view.addObject("unitList",unitList);
		view.addObject("parcelList",parcelList);

		map.addAttribute("userId",userId);
		map.addAttribute("userName",userName);
		return view; //JSP - /WEB-INF/view/index.jsp
	}

	@ResponseBody
	@RequestMapping(value = "/confirmParcel",method=RequestMethod.POST)
	public String ConfirmParcel(ParcelModel parcel) {
		boolean insert=orderService.ConfirmParcel(parcel);

		if (insert) {
			return "success";
		}
		return "fail";
	}



	@ResponseBody
	@RequestMapping(value = "/getParcelDetails",method=RequestMethod.GET)
	public JSONObject getParcelDetails(String autoId) {
		JSONObject objectMain = new JSONObject();
		ParcelModel parcelInfo = orderService.getParcelInfo(autoId);
		List<ParcelModel> parcelItems = orderService.getParcelItems(autoId);
		objectMain.put("parcelInfo", parcelInfo);
		objectMain.put("parcelItems", parcelItems);
		return objectMain;

	}



	@ResponseBody
	@RequestMapping(value = "/editParcel",method=RequestMethod.POST)
	public String editParcel(ParcelModel parcel) {
		boolean insert=orderService.editParecel(parcel);
		if (insert) {
			return "success";
		}			
		return "fail";

	}

	@ResponseBody
	@RequestMapping(value = "/editParcelItem",method=RequestMethod.GET)
	public JSONObject editParcelItem(ParcelModel parcelItem) {
		JSONObject objectMain = new JSONObject();
		boolean insert=orderService.editParecelItem(parcelItem);
		if (insert) {
			List<ParcelModel> parcelItems = orderService.getParcelItems(parcelItem.getParcelId());
			objectMain.put("result", parcelItems);
		}else {
			objectMain.put("result","Something Wrong");
		}
		return objectMain;
	}





	@ResponseBody
	@RequestMapping(value = "/parcelReportView/{id}")
	public ModelAndView department_medicine_delvierOpen(ModelMap map, @PathVariable ("id") String id) {

		System.out.println(" Open Ooudoor sales report ");	
		ModelAndView view = new ModelAndView("order/ParcelReportView");
		map.addAttribute("id",id);
		view.addObject("id",id);					

		return view;			
	}



	@RequestMapping(value = "/sample_cad",method=RequestMethod.GET)
	public ModelAndView sample_cad(ModelMap map,HttpSession session) {


		String userId=(String)session.getAttribute("userId");
		String userName=(String)session.getAttribute("userName");

		//List<SampleRequisitionItem> sampleReqList = orderService.getSampleRequisitionList(userId);
		List<SampleRequisitionItem> sampleReqList = orderService.getAllSampleRequisitionList();
		ModelAndView view = new ModelAndView("order/sample_cad");


		List<CommonModel> sampleList = orderService.getSampleList();
		List<SampleCadAndProduction>sampleCadList=orderService.getSampleComments(userId);
		
		List<Employee> employeeList = registerService.getEmployeeList();

		view.addObject("sampleList",sampleList);
		view.addObject("sampleCadList",sampleCadList);
		view.addObject("sampleReqList",sampleReqList);
		view.addObject("employeeList",employeeList);
		map.addAttribute("userId",userId);
		map.addAttribute("userName",userName);

		return view; //JSP - /WEB-INF/view/index.jsp
	}


	@RequestMapping(value = "/searchSampleCadDetails",method=RequestMethod.GET)
	public @ResponseBody JSONObject searchSampleCadDetails(String sampleCommentId,String sampleReqId,String user) {
		JSONObject objmain = new JSONObject();

		JSONArray mainArray = new JSONArray();
		
		System.out.println("sampleReqId "+sampleReqId);
		System.out.println("sampleCommentId "+sampleCommentId);
		List<SampleRequisitionItem> sampleRequisitionList = orderService.getSampleRequisitionDetails(sampleReqId);

		
		List<FileUpload>filelist=orderService.findsamplecadfiles(user, sampleCommentId);

		List<SampleCadAndProduction>  samplelist=orderService.getSampleDetails(sampleCommentId);
		
		//List<SampleCadAndProduction> sampleCadList = orderService.getSampleCadDetailsForProduction(sampleCommentId);

		objmain.put("result_sample_requisition",sampleRequisitionList);
		objmain.put("result_sample_cad",samplelist);
		objmain.put("files",filelist);

		return objmain;
	}


	@ResponseBody
	@RequestMapping(value = "/insertSampleCad",method=RequestMethod.GET)
	public String insertSampleCad(SampleCadAndProduction sample) {
		boolean insert=orderService.sampleCadInsert(sample);

		if (insert) {
			return "success";
		}
		return "fail";

	}



	@ResponseBody
	@RequestMapping(value = "/getSampleDetails/{id}",method=RequestMethod.GET)
	public List<SampleCadAndProduction> insertSamplCad(@PathVariable ("id") String id) {
		List<SampleCadAndProduction>  samplelist=orderService.getSampleDetails(id);

		return samplelist;

	}

	@ResponseBody
	@RequestMapping(value = "/editSampleCad",method=RequestMethod.GET)
	public String editSampleCad(SampleCadAndProduction sample) {
		boolean insert=orderService.editSampleCad(sample);

		if (insert) {
			return "success";
		}
		return "fail";

	}


	@ResponseBody
	@RequestMapping(value = "/SampleReport/{id}",method=RequestMethod.POST)
	public String SampleReport(@PathVariable ("id") String id) {
		System.out.println(" Open Ooudoor sales report 1");

		this.sampleId=id;
		return "yes";

	}


	@ResponseBody
	@RequestMapping(value = "/SampleCadReportView",method=RequestMethod.GET)
	public ModelAndView SampleReportView(ModelAndView map, FabricsIndent p) {


		System.out.println(" Open Ooudoor sales report ");	
		ModelAndView view = new ModelAndView("order/SampleCadReportView");

		view.addObject("id",sampleId);					

		return view;			
	}

	@ResponseBody
	@RequestMapping(value = "/SampleCadDateWiseReportView/{idList}",method=RequestMethod.GET)
	public ModelAndView SampleCadDateWiseReportView(ModelAndView map,@PathVariable ("idList") String idList) {

		ModelAndView view = new ModelAndView("order/sample_cad_date_wise_report_view");
		String id[] = idList.split("@");
		view.addObject("date",  id[0]);		
		view.addObject("userId",id[1]);
		view.addObject("reportType",id[2]);
		return view;			
	}

	@ResponseBody
	@RequestMapping(value = "/printDateWiseSamplCad",method=RequestMethod.GET)
	public ModelAndView printDateWiseSamplCad(ModelAndView map, FabricsIndent p) {


		System.out.println(" Open Ooudoor sales report ");	
		ModelAndView view = new ModelAndView("order/printDateWiseSamplCad");

		view.addObject("id",sampleId);					

		return view;			
	}


	@RequestMapping(value="/purchase_order_approve_from_md",method=RequestMethod.GET)
	public @ResponseBody ModelAndView purchase_order_approve_from_md(ModelMap map,HttpSession session){

		String userId=(String)session.getAttribute("userId");
		String userName=(String)session.getAttribute("userName");

		ModelAndView view = new ModelAndView("order/purchase-order-approve-from-md");
		List<BuyerModel> buyerList= registerService.getAllBuyers(userId);
		List<SupplierModel> supplierList = registerService.getAllSupplier();
		map.addAttribute("userId",userId);
		map.addAttribute("userName",userName);
		
		view.addObject("buyerList", buyerList);
		view.addObject("supplierList", supplierList);

		return view;
	}


	@RequestMapping(value = "/getPOListForMd",method=RequestMethod.GET)
	public @ResponseBody JSONObject getPOListForMd(String fromDate,String toDate,String itemType,String approveType,String buyerId,String supplierId) {
		JSONObject objmain = new JSONObject();
		List<PurchaseOrder> purchaseOrderList = orderService.getPurchaseOrderApprovalList(fromDate, toDate, itemType, approveType,buyerId,supplierId);
		objmain.put("purchaseOrderList", purchaseOrderList);
		return objmain;
	}

	/*@RequestMapping(value = "/confirmPurchaseOrder",method=RequestMethod.GET)
	public @ResponseBody JSONObject confirmPurchaseOrder(String purchaseOrderList) {
		JSONObject objmain = new JSONObject();
		try {
			String[] itemList = purchaseOrderList.split("#");
			List<PurchaseOrder> poList = new ArrayList<PurchaseOrder>();
			String purchaseOrder,styleId,supplierId,poNo,type;
			int approval=0;
			for (String item : itemList) {
				String[] itemProperty = item.split("@");

				purchaseOrder = itemProperty[0].substring(itemProperty[0].indexOf(":")+1).trim();
				styleId = itemProperty[1].substring(itemProperty[1].indexOf(":")+1).trim();
				supplierId = itemProperty[2].substring(itemProperty[2].indexOf(":")+1).trim();
				poNo = itemProperty[3].substring(itemProperty[3].indexOf(":")+1).trim();
				type = itemProperty[4].substring(itemProperty[4].indexOf(":")+1).trim();
				System.out.println("itemProperty 5="+itemProperty[5]);
				approval = Integer.valueOf(itemProperty[5].substring(itemProperty[5].indexOf(":")+1).trim());

				poList.add(new PurchaseOrder(purchaseOrder, styleId, "", supplierId, "", poNo, type, "",approval));
			}

			if(orderService.purchaseOrderApproveConfirm(poList)) {
				objmain.put("result", "Successfull");
			}else {
				objmain.put("result", "Something Wrong");
			}

		}catch(Exception e) {
			objmain.put("result", "Something Wrong");
			e.printStackTrace();
		}


		return objmain;
	}*/
	
	@RequestMapping(value = "/confirmPurchaseOrder", method = RequestMethod.POST)
	public @ResponseBody boolean approveAllPendings(PurchaseOrder v) {
		boolean approvePendingsByMd = orderService.purchaseOrderApproveConfirm(v);
		return approvePendingsByMd;
	}


	//Parcel
	@RequestMapping(value = "/accessories_check_list",method=RequestMethod.GET)
	public ModelAndView accessories_check_list(ModelMap map,HttpSession session) {

		String userId=(String)session.getAttribute("userId");
		String userName=(String)session.getAttribute("userName");

		ModelAndView view = new ModelAndView("order/accessories-check-list");
		List<BuyerModel> buyerList= registerService.getAllBuyers(userId);
		List<CommonModel> sampleList = orderService.getSampleList();
		List<Unit> unitList= registerService.getUnitList();	
		List<CheckListModel> checkList= orderService.getChekList();	
		List<FabricsItem> fabricsItemList = registerService.getFabricsItemList();
		view.addObject("buyerList",buyerList);
		view.addObject("sampletype",sampleList);
		view.addObject("unitList",unitList);
		view.addObject("parcelList",checkList);
		view.addObject("fabricsList",fabricsItemList);

		map.addAttribute("userId",userId);
		map.addAttribute("userName",userName);

		return view; //JSP - /WEB-INF/view/index.jsp
	}

	@ResponseBody
	@RequestMapping(value = "/confirmCheckList",method=RequestMethod.POST)
	public String confirmCheckList(CheckListModel checkList) {
		boolean insert=orderService.ConfirmCheckList(checkList);

		if (insert) {
			return "success";
		}
		return "fail";
	}

	@RequestMapping(value = "/getFabricsItems",method=RequestMethod.GET)
	public @ResponseBody JSONObject getFabricsItems() {
		JSONObject objmain = new JSONObject();
		List<FabricsItem> fabricsItemList = registerService.getFabricsItemList();
		objmain.put("fabricsList", fabricsItemList);
		return objmain;
	}
	
	@RequestMapping(value = "/getAccessoriesItems",method=RequestMethod.GET)
	public @ResponseBody JSONObject getAccessoriesItems() {
		JSONObject objmain = new JSONObject();
		List<CommonModel>accessoriesitem=orderService.AccessoriesItem("1");
		objmain.put("accessoriesItems", accessoriesitem);
		return objmain;
	}


	@ResponseBody
	@RequestMapping(value = "/getCheckListDetails",method=RequestMethod.GET)
	public JSONObject getCheckListDetails(String autoId) {
		JSONObject objectMain = new JSONObject();
		CheckListModel checkListInfo = orderService.getCheckListInfo(autoId);
		List<CheckListModel> checkListItems = orderService.getCheckListItems(autoId);
		objectMain.put("checkListInfo", checkListInfo);
		objectMain.put("checkListItems", checkListItems);
		return objectMain;

	}



	@ResponseBody
	@RequestMapping(value = "/editCheckList",method=RequestMethod.POST)
	public String editCheckList(CheckListModel checkList) {
		boolean insert=orderService.editCheckList(checkList);
		if (insert) {
			return "success";
		}			
		return "fail";

	}

	@ResponseBody
	@RequestMapping(value = "/editCheckListItem",method=RequestMethod.GET)
	public JSONObject editCheckListItem(CheckListModel checkList) {
		JSONObject objectMain = new JSONObject();
		boolean insert=orderService.editCheckListItem(checkList);
		if (insert) {
			List<CheckListModel> checkListItems = orderService.getCheckListItems(checkList.getCheckListId());
			objectMain.put("result", checkListItems);
		}else {
			objectMain.put("result","Something Wrong");
		}
		return objectMain;
	}

	@ResponseBody
	@RequestMapping(value = "/checkListReportView/{id}")
	public ModelAndView checkListReport(ModelMap map,@PathVariable ("id") String id) {
		ModelAndView view = new ModelAndView("order/checkListReportView");
		map.addAttribute("id",id);
		return view;

	}

	@RequestMapping(value = "/getImages",method=RequestMethod.GET)
	public @ResponseBody List<Style> getImages(Style style) {
		List<Style> images=orderService.images(style);

		System.out.println("front "+images.get(0).getFrontimage());


		return images;
	}

	// Create by Arman
	@ResponseBody
	@RequestMapping(value = "/departmentWiseReceiver/{deptId}",method=RequestMethod.POST)
	public List departmentWiseReceiver(@PathVariable ("deptId") String deptId) {
		List<CommonModel> departmentWiseReceiverList= orderService.departmentWiseReceiver(deptId);

		return departmentWiseReceiverList;				
	}

	@RequestMapping(value = "/saveFileAccessDetails", method = RequestMethod.POST)
	public @ResponseBody String saveFileAccessDetails(
			@RequestParam (value="empCode[]",  required=false) String [] empCode,
			@RequestParam (value="dept",  required=false) String dept,
			@RequestParam (value="type",  required=false) int type,
			@RequestParam (value="userId",  required=false) String userId) {

		this.empCode=empCode;
		this.dept=dept;
		this.type=type;
		this.userId=userId;

		//CommonModel saveFileAccessDetails=new CommonModel(empCode,dept,userId,type);
		//		boolean SaveGeneralDuty=orderService.saveFileAccessDetails(saveFileAccessDetails);
		fileupload=true;

		return "success";

	}

	@RequestMapping(value = "/getIdWiseFileLogDetails", method = RequestMethod.POST)
	public @ResponseBody List<CommonModel> getAllFromFileLogDetails(CommonModel v) {

		List<CommonModel> AllFromFileLogDetails = orderService.getAllFromFileLogDetails(v);

		return AllFromFileLogDetails;
	}

	@RequestMapping(value = "/addNewPermission", method = RequestMethod.POST)
	public @ResponseBody String addNewPermission(
			@RequestParam (value="empCode[]",  required=false) String [] empCode,
			@RequestParam (value="dept",  required=false) String dept,
			@RequestParam (value="type",  required=false) int type,
			@RequestParam (value="id",  required=false) String id,
			@RequestParam (value="userId",  required=false) String userId) {
		CommonModel AddNewPermission=new CommonModel(empCode,dept,userId,type,id);
		boolean ADDNewPermission=orderService.addNewPermission(AddNewPermission);
		return "success";

	}

	/*@RequestMapping(value = "/setModalData", method = RequestMethod.POST)
	public @ResponseBody List<CommonModel> setModalData(CommonModel v) {

		List<CommonModel> setModalData = orderService.getModalData(v);

		return setModalData;
	}*/



	@RequestMapping(value="/save-samplecad/{samplecadid}/{user}/{searchtype}", method={RequestMethod.PUT, RequestMethod.POST})
	public String uploadSampleFileSubmit(
			@PathVariable ("samplecadid") String samplecadid,
			@PathVariable ("user") String user,@PathVariable ("searchtype") String searchtype,

			MultipartHttpServletRequest multipartRequest, HttpServletRequest request, HttpServletResponse response) {

		String filelocation="C:/uploadspringfiles/samplecadfiles/";
		try
		{


			Logger.getLogger(this.getClass()).warning("Inside Confirm Servlet");  
			response.setContentType("text/html");

			String hostname = request.getRemoteHost(); // hostname
			System.out.println("hostname "+hostname);

			String computerName = null;
			String remoteAddress = request.getRemoteAddr();
			InetAddress inetAddress=null;


			inetAddress = InetAddress.getByName(remoteAddress);
			System.out.println("inetAddress: " + inetAddress);
			computerName = inetAddress.getHostName();

			System.out.println("computerName: " + computerName);


			if (computerName.equalsIgnoreCase("localhost")) {
				computerName = java.net.InetAddress.getLocalHost().getCanonicalHostName();
			}else if(hostname.equalsIgnoreCase("0:0:0:0:0:0:0:1")){
				inetAddress = InetAddress.getLocalHost();
				computerName=inetAddress.getHostName();
			}
			System.out.println("ip : " + inetAddress);
			System.out.println("computerName: " + computerName);

			//   Date date=new Date();
			// Get multiple file control names.
			Iterator<String> it = multipartRequest.getFileNames();

			while(it.hasNext())
			{
				String fileControlName = it.next();

				MultipartFile srcFile = multipartRequest.getFile(fileControlName);
				String uploadFileName ="";
				
				if (searchtype.equals("1")) {
					samplecadid=orderService.getMaxCadId();
					uploadFileName="sam-"+orderService.getMaxCadId()+srcFile.getOriginalFilename();
					
				}else if(searchtype.equals("2")) {
					uploadFileName="sam-"+samplecadid+srcFile.getOriginalFilename();
				}

				System.out.println(" sample cad id "+samplecadid);

				System.out.println(" file names "+uploadFileName);



				// Create server side target file path.


				String destFilePath = filelocation+uploadFileName;

				File existingfile=new File(destFilePath);

				System.out.println(" file exists "+uploadFileName+" "+existingfile.exists());

				if (!existingfile.exists()) {
					File destFile = new File(destFilePath);
					// Save uploaded file to target.
					srcFile.transferTo(destFile);
					fileupload = true;
					System.out.println(" sample id "+samplecadid);
					orderService.samplecadfileupload(samplecadid, uploadFileName, user, inetAddress.toString(),searchtype);

					//	CommonModel saveFileAccessDetails=new CommonModel(empCode,dept,userId,type);
					//boolean SaveGeneralDuty=orderService.saveFileAccessDetails(saveFileAccessDetails);
					//fileupload=false;
				}



				if (fileupload) {

				}
				//msgBuf.append("Upload file " + uploadFileName + " is saved to " + destFilePath + "<br/><br/>");
			}

			// Set message that will be displayed in return page.
			//  model.addAttribute("message", msgBuf.toString());



		}catch(IOException ex)
		{
			ex.printStackTrace();
		}finally
		{
			return "upload_file_result";
		}
	}



	@RequestMapping(value="/download-samplecad/{fileName:.+}/{user}", method=RequestMethod.POST)
	public @ResponseBody void downloadsamplecad(HttpServletResponse response,@PathVariable ("fileName") String fileName,@PathVariable ("user") String user,HttpServletRequest request) throws IOException {
		System.out.println(" download controller ");

		Logger.getLogger(this.getClass()).warning("Inside Confirm Servlet");  
		response.setContentType("text/html");

		String hostname = request.getRemoteHost(); // hostname
		System.out.println("hostname "+hostname);

		String computerName = null;
		String remoteAddress = request.getRemoteAddr();
		InetAddress inetAddress=null;


		inetAddress = InetAddress.getByName(remoteAddress);
		System.out.println("inetAddress: " + inetAddress);
		computerName = inetAddress.getHostName();

		System.out.println("computerName: " + computerName);


		if (computerName.equalsIgnoreCase("localhost")) {
			computerName = java.net.InetAddress.getLocalHost().getCanonicalHostName();
		}else if(hostname.equalsIgnoreCase("0:0:0:0:0:0:0:1")){
			inetAddress = InetAddress.getLocalHost();
			computerName=inetAddress.getHostName();
		}
		System.out.println("ip : " + inetAddress);
		System.out.println("computerName: " + computerName);




		try {


			String filelocation="E:/uploadspringfiles/samplecadfiles/";

			String filePath = filelocation+fileName;

			System.out.println(" filename "+fileName);

			try {
				File file = new File(filePath);
				System.out.println(" file "+file.length()/(1024*1024));
				FileInputStream in = new FileInputStream(file);
				System.out.println(" file in "+in);
				response.setHeader("Expires", new Date().toGMTString());
				response.setContentType(URLConnection.guessContentTypeFromStream(in));

				// response.setContentLength(Files.readAllBytes(file.toPath()).length);

				response.setContentLength((int)file.length());

				response.setHeader("Content-Disposition","attachment; filename=\"" + fileName +"\"");
				response.setHeader("Pragma", "no-cache");

				response.setContentType("application/octet-stream");
				// FileCopyUtils.copy(in, response.getOutputStream());


				IOUtils.copyLarge(in, response.getOutputStream());


				//boolean download=orderService.fileDownload(fileName, user, inetAddress.toString(), computerName);

				in.close();
				response.flushBuffer();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@RequestMapping(value = "/deletesamplecadfile/{filename:.+}/{id}",method=RequestMethod.POST)
	public @ResponseBody boolean deletesamplecadfile(@PathVariable ("filename") String filename,@PathVariable ("id") String id) {

		boolean delete=orderService.deletesamplefile(filename,id);
		String filelocation="E:/uploadspringfiles/samplecadfiles/";
		if (delete) {
			File file=new File(filelocation+filename);
			file.delete();
		}

		return delete;
	}


	@RequestMapping(value="/multidownload/{fileName:.+}/{user}", method=RequestMethod.POST)
	public @ResponseBody void multidownload(HttpServletResponse response,@PathVariable ("fileName") String fileName,@PathVariable ("user") String user,HttpServletRequest request) throws IOException {
		System.out.println(" download controller ");

		Logger.getLogger(this.getClass()).warning("Inside Confirm Servlet");  
		response.setContentType("text/html");

		String hostname = request.getRemoteHost(); // hostname
		System.out.println("hostname "+hostname);

		String computerName = null;
		String remoteAddress = request.getRemoteAddr();
		InetAddress inetAddress=null;


		inetAddress = InetAddress.getByName(remoteAddress);
		System.out.println("inetAddress: " + inetAddress);
		computerName = inetAddress.getHostName();

		System.out.println("computerName: " + computerName);


		if (computerName.equalsIgnoreCase("localhost")) {
			computerName = java.net.InetAddress.getLocalHost().getCanonicalHostName();
		}else if(hostname.equalsIgnoreCase("0:0:0:0:0:0:0:1")){
			inetAddress = InetAddress.getLocalHost();
			computerName=inetAddress.getHostName();
		}
		System.out.println("ip : " + inetAddress);
		System.out.println("computerName: " + computerName);




		try {	

			System.out.println(" filename "+fileName);		
			List<String> filelist= orderService.getMultifiles(fileName);


			String zipfilename=user+"-"+"Zippedfiles.zip";
			String zipfilenamewithpath=UPLOAD_FILE_SAVE_FOLDER+zipfilename;
			FileOutputStream fos = new FileOutputStream(zipfilenamewithpath);
			ZipOutputStream zos = new ZipOutputStream(fos);


			for (int i = 0; i < filelist.size(); i++) {

				// File firstFile = new File(UPLOAD_FILE_SAVE_FOLDER+filelist.get(i));
				String aFile=UPLOAD_FILE_SAVE_FOLDER+filelist.get(i);
				/* fos = new FileOutputStream(zipFileName);
					             zos = new ZipOutputStream(fos);*/
				boolean download=orderService.fileDownload(filelist.get(i), user, inetAddress.toString(), computerName);

				zos.putNextEntry(new ZipEntry(new File(aFile).getName()));

				byte[] bytes = Files.readAllBytes(Paths.get(aFile));
				zos.write(bytes, 0, bytes.length);
				zos.closeEntry();


			}

			zos.close();


			System.out.println(" end ");

			try {
				File file = new File(zipfilenamewithpath);
				System.out.println(" file "+file.length()/(1024*1024));
				FileInputStream in = new FileInputStream(file);
				System.out.println(" file in "+in);
				response.setHeader("Expires", new Date().toGMTString());
				response.setContentType(URLConnection.guessContentTypeFromStream(in));

				// response.setContentLength(Files.readAllBytes(file.toPath()).length);

				response.setContentLength((int)file.length());

				response.setHeader("Content-Disposition","attachment; filename=\"" + zipfilename +"\"");
				response.setHeader("Pragma", "no-cache");

				response.setContentType("application/octet-stream");
				// FileCopyUtils.copy(in, response.getOutputStream());


				IOUtils.copyLarge(in, response.getOutputStream());


				//boolean download=orderService.fileDownload(fileName, user, inetAddress.toString(), computerName);

				in.close();
				response.flushBuffer();
				file.delete();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	





		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	@RequestMapping(value="/multiCaddownload/{fileName:.+}/{user}", method=RequestMethod.POST)
	public @ResponseBody void multiCaddownload(HttpServletResponse response,@PathVariable ("fileName") String fileName,@PathVariable ("user") String user,HttpServletRequest request) throws IOException {
		System.out.println(" download controller ");
		
		
		String filelocation="E:/uploadspringfiles/samplecadfiles/";
		UPLOAD_FILE_SAVE_FOLDER=filelocation;

		Logger.getLogger(this.getClass()).warning("Inside Confirm Servlet");  
		response.setContentType("text/html");

		String hostname = request.getRemoteHost(); // hostname
		System.out.println("hostname "+hostname);

		String computerName = null;
		String remoteAddress = request.getRemoteAddr();
		InetAddress inetAddress=null;


		inetAddress = InetAddress.getByName(remoteAddress);
		System.out.println("inetAddress: " + inetAddress);
		computerName = inetAddress.getHostName();

		System.out.println("computerName: " + computerName);


		if (computerName.equalsIgnoreCase("localhost")) {
			computerName = java.net.InetAddress.getLocalHost().getCanonicalHostName();
		}else if(hostname.equalsIgnoreCase("0:0:0:0:0:0:0:1")){
			inetAddress = InetAddress.getLocalHost();
			computerName=inetAddress.getHostName();
		}
		System.out.println("ip : " + inetAddress);
		System.out.println("computerName: " + computerName);




		try {	

			System.out.println(" filename "+fileName);		
			List<String> filelist= orderService.getMultiCadfiles(fileName);


			String zipfilename=user+"-"+"Zippedfiles.zip";
			String zipfilenamewithpath=UPLOAD_FILE_SAVE_FOLDER+zipfilename;
			FileOutputStream fos = new FileOutputStream(zipfilenamewithpath);
			ZipOutputStream zos = new ZipOutputStream(fos);


			for (int i = 0; i < filelist.size(); i++) {

				// File firstFile = new File(UPLOAD_FILE_SAVE_FOLDER+filelist.get(i));
				String aFile=UPLOAD_FILE_SAVE_FOLDER+filelist.get(i);
				/* fos = new FileOutputStream(zipFileName);
					             zos = new ZipOutputStream(fos);*/
			//	boolean download=orderService.fileDownload(filelist.get(i), user, inetAddress.toString(), computerName);

				zos.putNextEntry(new ZipEntry(new File(aFile).getName()));

				byte[] bytes = Files.readAllBytes(Paths.get(aFile));
				zos.write(bytes, 0, bytes.length);
				zos.closeEntry();


			}

			zos.close();


			System.out.println(" end ");

			try {
				File file = new File(zipfilenamewithpath);
				System.out.println(" file "+file.length()/(1024*1024));
				FileInputStream in = new FileInputStream(file);
				System.out.println(" file in "+in);
				response.setHeader("Expires", new Date().toGMTString());
				response.setContentType(URLConnection.guessContentTypeFromStream(in));

				// response.setContentLength(Files.readAllBytes(file.toPath()).length);

				response.setContentLength((int)file.length());

				response.setHeader("Content-Disposition","attachment; filename=\"" + zipfilename +"\"");
				response.setHeader("Pragma", "no-cache");

				response.setContentType("application/octet-stream");
				// FileCopyUtils.copy(in, response.getOutputStream());


				IOUtils.copyLarge(in, response.getOutputStream());


				//boolean download=orderService.fileDownload(fileName, user, inetAddress.toString(), computerName);

				in.close();
				response.flushBuffer();
				file.delete();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	





		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@ResponseBody
	@RequestMapping(value = "/recapSheet",method=RequestMethod.GET)
	public String recapSHeet(String mBuyerName,String sDate,String eDate) {
		this.mBuyerName=mBuyerName;
		this.sDate=sDate;
		this.eDate=eDate;
		
		return "Success";
	}
	
	@RequestMapping(value = "/printRecapSHeet",method=RequestMethod.GET)
	public @ResponseBody ModelAndView printRecapSHeet(ModelMap map) {

		ModelAndView view=new ModelAndView("order/recap_sheet_View");
		map.addAttribute("mBuyerName", mBuyerName);
		map.addAttribute("sDate", sDate);
		map.addAttribute("eDate", eDate);
		return view;
	}


}

