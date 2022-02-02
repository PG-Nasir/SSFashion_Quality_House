<%@page import="java.util.Iterator"%>
<%@page import="pg.share.HibernateUtil"%>
<%@page import="org.hibernate.Session"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@ page contentType="application/pdf"%>

<%@ page trimDirectiveWhitespaces="true"%>


<%@ page import="net.sf.jasperreports.engine.design.JRDesignQuery"%>
<%@ page import="net.sf.jasperreports.engine.design.JasperDesign"%>
<%@ page import="net.sf.jasperreports.engine.xml.JRXmlLoader"%>

<%@ page import="net.sf.jasperreports.engine.*"%>
<%@ page import="java.io.File"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="java.io.FileNotFoundException"%>
<%@ page import="java.io.InputStream"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.DriverManager"%>
<%@ page import="java.sql.SQLException"%>
<%@ page import="pg.config.*"%>

<%
	System.out.println("report = " + request.getAttribute("poNo"));
	String poNo = request.getAttribute("poNo").toString();
	String supplierId = request.getAttribute("supplierId").toString();
	String type = request.getAttribute("type").toString();
	String previewType = request.getAttribute("previewType").toString();
	boolean landscapeCheck = Boolean.valueOf(request.getAttribute("landscapeCheck").toString());
	boolean brandCheck = Boolean.valueOf(request.getAttribute("brandCheck").toString());
	boolean sqCheck = Boolean.valueOf(request.getAttribute("sqCheck").toString());
	boolean skuCheck = Boolean.valueOf(request.getAttribute("skuCheck").toString());

	try {

		HashMap map = new HashMap();
		Session dbsession = HibernateUtil.openSession();

		String sql = "select name,Telephone,SupplierAddress,ContactPerson from tbSupplier where id='"
				+ supplierId + "'";
		List<?> list = dbsession.createSQLQuery(sql).list();
		for (Iterator<?> iter = list.iterator(); iter.hasNext();) {
			Object[] element = (Object[]) iter.next();
			map.put("supName", element[0].toString());
			map.put("supPhone", element[1].toString());
			map.put("supAddress", element[2].toString());
			map.put("SupplierContactPerson", element[3].toString());
		}

		sql = "select FactoryName,TelePhone,Address from TbFactoryInfo where Factoryid=(select billTo from tbPurchaseOrderSummary  where pono='"
				+ poNo + "')";
		list = dbsession.createSQLQuery(sql).list();
		for (Iterator<?> iter = list.iterator(); iter.hasNext();) {
			Object[] element = (Object[]) iter.next();
			map.put("headFactoryName", element[0].toString());
			map.put("headFactoryPhone", element[1].toString());
			map.put("headFactoryAddress", element[2].toString());
		}

		sql = "select FactoryName,TelePhone,Address from TbFactoryInfo where Factoryid=(select deliveryto from tbPurchaseOrderSummary  where pono='"
				+ poNo + "')";
		list = dbsession.createSQLQuery(sql).list();
		for (Iterator<?> iter = list.iterator(); iter.hasNext();) {
			Object[] element = (Object[]) iter.next();
			map.put("FactoryName", element[0].toString());
			map.put("FactoryPhone", element[1].toString());
			map.put("FactoryAddress", element[2].toString());
		}

		String report = "";
		String jrxmlFile = "";
		if (type.equalsIgnoreCase("Accessories")) {
			
			System.out.println("Tec");

			sql = "select (select dbo.number((select sum(amount) from tbAccessoriesIndent where pono=a.pono and supplierId=b.supplierid),b.dolar)) as Taka,b.AINo,b.ShippingMarks,b.PurchaseOrder, \r\n"
					+ "isnull(b.StyleNo,'') as StyleNo,isnull(gc.colorName,'') as garmentsColor,   \r\n"
					+ " isnull(ai.ItemName,'') as AccessorisItem,  \r\n"
					+ "ISNULL(c.Colorname,'') as ColorName,isnull(b.sqNumber,'')as sqNumber,isnull(b.skuNumber,'')as skuNumber,isnull(brand.name,'') as brand,b.accessoriesSize,ss.sizeName as size, \r\n"
					+ "(select unitname from tbunits where UnitId=b.UnitId) as UnitName,  \r\n"
					+ "b.TotalQty,b.RequireUnitQty,b.rate,b.dolar,b.amount,b.sampleQty,a.currency,b.poManual,a.orderDate,deliveryDate,  \r\n"
					+ "(select MerchendiserName from TbMerchendiserInfo  where MerchendiserId=a.orderby)   \r\n"
					+ "OrderBy,ei.contact as MerMobile,  \r\n"
					+ "ei.email as MerEmail,a.Note,a.Subject,cast(a.body as varchar(300)) as body,a.ManualPo,  \r\n"
					+ "(select Signature from Tblogin where id=b.IndentPostBy) as Signature,  \r\n"
					+ "(select Signature from Tblogin where id='9') as MdSignature, \r\n"
					+ " (select organizationLogo from tbOrganizationInfo where organizationId = 1) organizationLogo, \r\n"
					+ "b.mdapproval,a.poNo,a.caNo,a.contentNo,a.fabricsContent,a.terms  \r\n"
					+ "from tbPurchaseOrderSummary a   \r\n" + "join tbAccessoriesIndent b   \r\n"
					+ "on a.pono=b.pono    \r\n" + "left join tbColors gc \r\n"
					+ "on b.ColorId = cast(gc.ColorId as varchar) \r\n"
					+ " left join tbAccessoriesItem ai  \r\n"
					+ " on b.accessoriesItemId = cast(ai.itemId as varchar)  \r\n"
					+ " left join tbColors c  \r\n" + " on b.IndentColorId = cast(c.ColorId as varchar)  \r\n"
					+ "left join tbStyleSize ss \r\n" + "on b.size = ss.id \r\n"
					+ "left join tbbrands brand\r\n" + "on b.IndentBrandId = brand.id\r\n"
					+ "left join Tblogin l \r\n" + "on a.entryBy = l.id \r\n"
					+ "left join tbEmployeeInfo ei \r\n" + "on l.employeeId = ei.autoId \r\n"
					+ "where  a.pono='" + poNo + "' and b.supplierid = '" + supplierId + "'   \r\n"
					+ "order by b.styleid,b.PurchaseOrder,b.Itemid,b.accessoriesItemId,b.ColorId,b.IndentColorId,b.ShippingMarks,b.SizeSorting asc";
			if (previewType.equalsIgnoreCase("withoutPcs")) {
				if (landscapeCheck) {
					jrxmlFile = session.getServletContext()
							.getRealPath("WEB-INF/jasper/order/AccessoriesPurchaseOrderLandscape.jrxml");
				} else {
					jrxmlFile = session.getServletContext()
							.getRealPath("WEB-INF/jasper/order/AccessoriesPurchaseOrderWithoutPcs.jrxml");

					if (sqCheck && skuCheck) {
						jrxmlFile = session.getServletContext().getRealPath(
								"WEB-INF/jasper/order/AccessoriesPurchaseOrderWithoutPcsWithSQ&SKQ.jrxml");
					} else if (sqCheck) {
						jrxmlFile = session.getServletContext().getRealPath(
								"WEB-INF/jasper/order/AccessoriesPurchaseOrderWithoutPcsWithSQ.jrxml");
					} else if (skuCheck) {
						jrxmlFile = session.getServletContext().getRealPath(
								"WEB-INF/jasper/order/AccessoriesPurchaseOrderWithoutPcsWithSKQ.jrxml");
					}
				}

			} else {
				if (landscapeCheck) {
					/* jrxmlFile = session.getServletContext()
							.getRealPath("WEB-INF/jasper/order/AccessoriesPurchaseOrderLandscape.jrxml"); */
					jrxmlFile = session.getServletContext()
							.getRealPath("WEB-INF/jasper/order/SupplierWisePurchaseOrderLandscape.jrxml");
					if (sqCheck && skuCheck && brandCheck) {
								jrxmlFile = session.getServletContext().getRealPath(
										"WEB-INF/jasper/order/AccessoriesPurchaseOrderWithPcsWithSQ&SKQ&BrandLandscape.jrxml");
							}
					else if (sqCheck && skuCheck) {
						jrxmlFile = session.getServletContext().getRealPath(
								"WEB-INF/jasper/order/AccessoriesPurchaseOrderWithPcsWithSQ&SKQLandscape.jrxml");
					} 

					else if (sqCheck && brandCheck) {
						jrxmlFile = session.getServletContext().getRealPath(
								"WEB-INF/jasper/order/AccessoriesPurchaseOrderWithPcsWithSQBrandLandscape.jrxml");
					} 
					else if (skuCheck && brandCheck) {
						jrxmlFile = session.getServletContext().getRealPath(
								"WEB-INF/jasper/order/AccessoriesPurchaseOrderWithPcsWithSKQBrandLandscape.jrxml");
					} 
					else if (sqCheck) {
						jrxmlFile = session.getServletContext().getRealPath(
								"WEB-INF/jasper/order/AccessoriesPurchaseOrderWithPcsWithSQLandscape.jrxml");
					} 
					else if (skuCheck) {
						jrxmlFile = session.getServletContext().getRealPath(
								"WEB-INF/jasper/order/AccessoriesPurchaseOrderWithPcsWithSKQLandscape.jrxml");
					}		
				} 
				else {
					jrxmlFile = session.getServletContext()
							.getRealPath("WEB-INF/jasper/order/SupplierWisePurchaseOrder.jrxml");

					if (sqCheck && skuCheck) {
						jrxmlFile = session.getServletContext().getRealPath(
								"WEB-INF/jasper/order/AccessoriesPurchaseOrderWithPcsWithSQ&SKQ.jrxml");
					} else if (sqCheck) {
						jrxmlFile = session.getServletContext().getRealPath(
								"WEB-INF/jasper/order/AccessoriesPurchaseOrderWithPcsWithSQ.jrxml");
					} else if (skuCheck) {
						jrxmlFile = session.getServletContext().getRealPath(
								"WEB-INF/jasper/order/AccessoriesPurchaseOrderWithPcsWithSKQ.jrxml");
					}
				}

			}

		} else if (type.equalsIgnoreCase("Zipper And Others")) {

			if (previewType.equalsIgnoreCase("general")) {

				sql = "select (select dbo.number((select sum(amount) from tbZipperIndent where pono=a.pono and supplierId=b.supplierid),b.dolar)) as Taka,b.ShippingMarks,b.PurchaseOrder, \r\n"
						+ "isnull(b.StyleNo,'') as StyleNo,  \r\n"
						+ " isnull(ai.ItemName,'') as AccessorisItem,  \r\n"
						+ "ISNULL(c.Colorname,'') as ColorName,b.accessoriesSize,ss.sizeName as size, \r\n"
						+ "(select unitname from tbunits where UnitId=b.UnitId) as UnitName,  \r\n"
						+ "(select unitname from tbunits where UnitId=b.lengthUnitId) as lengthUnitName, \r\n"
						+ "b.TotalQty,b.RequireUnitQty,b.rate,b.dolar,b.amount ,a.currency,b.poManual,a.orderDate,deliveryDate,  \r\n"
						+ "(select MerchendiserName from TbMerchendiserInfo  where MerchendiserId=a.orderby)   \r\n"
						+ "OrderBy,ei.contact as MerMobile,  \r\n"
						+ "ei.email as MerEmail,a.Note,a.Subject,cast(a.body as varchar(300)) as body,a.ManualPo,  \r\n"
						+ "(select Signature from Tblogin where id=b.IndentPostBy) as Signature,  \r\n"
						+ "(select Signature from Tblogin where id='9') as MdSignature, \r\n"
						+ " (select organizationLogo from tbOrganizationInfo where organizationId = 1) organizationLogo, \r\n"
						+ "b.mdapproval,a.poNo,a.caNo,a.contentNo,a.fabricsContent,a.terms  \r\n"
						+ "from tbPurchaseOrderSummary a   \r\n" + "join tbZipperIndent b   \r\n"
						+ "on a.pono=b.pono    \r\n" + " left join tbAccessoriesItem ai  \r\n"
						+ " on b.accessoriesItemId = cast(ai.itemId as varchar)  \r\n"
						+ " left join tbColors c  \r\n"
						+ " on b.IndentColorId = cast(c.ColorId as varchar)  \r\n"
						+ "left join tbStyleSize ss \r\n" 
						+ "on b.size = ss.id \r\n"
						+ "left join Tblogin l \r\n" + "on a.entryBy = l.id \r\n"
						+ "left join tbEmployeeInfo ei \r\n" + "on l.employeeId = ei.autoId \r\n"
						+ "where  a.pono='" + poNo + "' and b.supplierid = '" + supplierId + "'   \r\n"
						+ "order by b.styleid,b.PurchaseOrder,b.Itemid,b.accessoriesItemId,b.ColorId,b.ShippingMarks asc";

				if (landscapeCheck) {
					jrxmlFile = session.getServletContext()
							.getRealPath("WEB-INF/jasper/order/ZipperGeneralPurchaseOrderViewLandscape.jrxml");
				} else {
					jrxmlFile = session.getServletContext()
							.getRealPath("WEB-INF/jasper/order/ZipperGeneralPurchaseOrderView.jrxml");
				}

			} else {

				sql = "select (select dbo.number((select sum(amount) from tbZipperIndent where pono=a.pono),b.dolar)) as Taka,b.ShippingMarks,b.styleId,  \r\n"
						+ "isnull(sc.StyleNo,'') as StyleNo,b.itemId,id.itemname as itemDescription,  \r\n"
						+ "b.accessoriesItemId,isnull(ai.ItemName,'') as AccessorisItem,b.colorId, \r\n"
						+ "ISNULL(c.Colorname,'') as ColorName,b.SizeGroupId, \r\n"
						+ "(select unitname from tbunits where UnitId=b.UnitId) as UnitName, \r\n"
						+ "a.currency,b.poManual,a.orderDate,deliveryDate,   \r\n"
						+ "(select MerchendiserName from TbMerchendiserInfo  where MerchendiserId=a.orderby) \r\n"
						+ "OrderBy,(select Mobile from TbMerchendiserInfo  where MerchendiserId=a.orderby) as MerMobile,  \r\n"
						+ "(select Email from TbMerchendiserInfo  where MerchendiserId=a.orderby) as MerEmail,a.Note,a.Subject,cast(a.body as varchar(300)) as body,a.ManualPo, \r\n"
						+ "(select Signature from Tblogin where id=b.IndentPostBy) as Signature,   \r\n"
						+ "(select Signature from Tblogin where id='9') as MdSignature,  \r\n"
						+ "(select organizationLogo from tbOrganizationInfo where organizationId = 1) organizationLogo, \r\n"
						+ "b.mdapproval,a.poNo ,count(a.poNo) as cnt,b.dolar,a.caNo,a.contentNo,a.fabricsContent,a.terms  \r\n"
						+ "from tbPurchaseOrderSummary a    \r\n" + "join tbZipperIndent b    \r\n"
						+ "on a.pono=b.pono     \r\n" + " left join TbStyleCreate sc \r\n"
						+ " on b.styleId = cast(sc.StyleId as varchar) \r\n"
						+ "left join tbItemDescription id \r\n"
						+ "on b.Itemid = cast(id.itemid as varchar) \r\n"
						+ " left join tbAccessoriesItem ai   \r\n"
						+ " on b.accessoriesItemId = cast(ai.itemId as varchar) \r\n"
						+ "left join tbColors c   \r\n" + "on b.IndentColorId = cast(c.ColorId as varchar) \r\n"
						+ "left join tbStyleSize ss  \r\n" + "on b.size = ss.id \r\n" + "where  a.pono='" + poNo
						+ "' and b.supplierid = '" + supplierId + "'   \r\n"
						+ "group by a.pono,b.dolar,b.shippingMarks,b.styleId,sc.StyleNo,b.Itemid,id.itemname,b.accessoriesItemId,ai.itemname,b.SizeGroupId,b.colorId,c.Colorname,b.UnitId,a.orderby,a.Note,a.Subject,cast(a.body as varchar(300)),a.ManualPo,b.IndentPostBy,b.mdapproval,b.PurchaseOrder,b.Itemid,b.currency,b.poManual,a.orderDate,deliveryDate,a.caNo,a.contentNo,a.terms\r\n"
						+ "order by b.styleid,b.PurchaseOrder,b.Itemid,b.accessoriesItemId,b.ColorId,b.ShippingMarks asc";
				if (landscapeCheck) {
					jrxmlFile = session.getServletContext()
							.getRealPath("WEB-INF/jasper/order/ZipperPurchaseOrderLandscape.jrxml");
				} else {
					jrxmlFile = session.getServletContext()
							.getRealPath("WEB-INF/jasper/order/ZipperPurchaseOrder.jrxml");
				}
			}

		} else if (type.equalsIgnoreCase("Fabrics")) {
			System.out.println("Fab");
			sql = "select (select dbo.number((select sum(amount) from tbFabricsIndent where pono=a.pono and supplierId=b.supplierid),b.dolar)) as Taka,b.indentId,' ' as ShippingMarks,isnull(sc.StyleNo,'') as StyleNo, isnull(fi.ItemName,'') as AccessorisItem,  ISNULL(c.Colorname,'') as ColorName,isnull(brand.name,'') as brandName,'' as accessoriesSize,'' as size,    \r\n"
					+ " (select unitname from tbunits where UnitId=b.UnitId) as UnitName,b.width,b.GSM,b.TotalQty,b.TotalQty as RequireUnitQty,b.sqNumber,b.skuNumber,b.dolar,b.rate,b.dolar,b.amount,b.sampleQty,a.currency,b.poManual,a.orderDate,deliveryDate,  \r\n"
					+ " (select MerchendiserName from TbMerchendiserInfo  where MerchendiserId=a.orderby) OrderBy,    \r\n"
					+ " ei.contact as MerMobile,    \r\n"
					+ " ei.email as MerEmail,a.Note,a.Subject,cast(a.body as varchar(300)) as body,a.ManualPo,  \r\n"
					+ " (select Signature from Tblogin where id=b.entryby) as Signature,    \r\n"
					+ " (select Signature from Tblogin where id='9') as MdSignature, \r\n"
					+ " (select organizationLogo from tbOrganizationInfo where organizationId = 1) organizationLogo, \r\n"
					+ " b.mdapproval,a.poNo,a.caNo,a.contentNo,a.fabricsContent,a.terms  \r\n"
					+ " from tbPurchaseOrderSummary a     \r\n" + " join tbFabricsIndent b   \r\n"
					+ " on a.pono=b.pono    \r\n" + " left join TbStyleCreate sc  \r\n"
					+ " on b.styleId = cast(sc.StyleId as varchar)  \r\n" + " left join TbFabricsItem fi  \r\n"
					+ " on b.fabricsid = cast(fi.id as varchar)  \r\n" + " left join tbColors c  \r\n"
					+ " on b.fabricscolor = cast(c.ColorId as varchar)  \r\n" + "left join tbbrands brand \r\n"
					+ "on b.brand = brand.id \r\n" + "left join Tblogin l \r\n" + "on a.entryBy = l.id \r\n"
					+ "left join tbEmployeeInfo ei \r\n" + "on l.employeeId = ei.autoId \r\n"
					+ " where a.pono='" + poNo + "' and a.supplierid='" + supplierId + "'  \r\n"
					+ " order by  b.styleid,b.PurchaseOrder,b.Itemid,b.fabricsid asc ";

			if (landscapeCheck) {
				jrxmlFile = session.getServletContext()
						.getRealPath("WEB-INF/jasper/order/FabricsPurchaseOrderLandscape.jrxml");
			} else {
				jrxmlFile = session.getServletContext()
						.getRealPath("WEB-INF/jasper/order/FabricsPurchaseOrder.jrxml");
			}
			if (sqCheck && skuCheck) {
				jrxmlFile = session.getServletContext()
						.getRealPath("WEB-INF/jasper/order/FabricsPurchaseOrderWithSQ&SKQ.jrxml");
			} else if (sqCheck) {
				jrxmlFile = session.getServletContext()
						.getRealPath("WEB-INF/jasper/order/FabricsPurchaseOrderWithSQ.jrxml");
			} else if (skuCheck) {
				jrxmlFile = session.getServletContext()
						.getRealPath("WEB-INF/jasper/order/FabricsPurchaseOrderWithSKQ.jrxml");
			}

		} else if (type.equalsIgnoreCase("Carton")) {
			sql = "select 'only' as Taka ,b.PurchaseOrder,b.styleid,b.Itemid,' ' as ShippingMarks,(select StyleNo from  TbStyleCreate where StyleId=b.styleid) as StyleNo, \r\n"
					+ " (select ItemName from TbAccessoriesItem where itemid=b.accessoriesItemId) as AccessorisItem,   \r\n"
					+ "  (select colorName from tbColors where ColorId=b.ColorId) as ColorName,  \r\n"
					+ " b.cartonSize as accessoriesSize,isnull((select ss.sizeName from tbStyleSize ss  where ss.id = b.sizeId),'') as size, \r\n"
					+ " (select unitname from tbunits where UnitId=b.UnitId) as UnitName,b.Ply,b.type,b.Length1,b.Width1,b.Height1,b.cbm,  \r\n"
					+ " b.Qty,b.Qty as RequireUnitQty,b.dolar,b.rate,b.dolar,b.amount,a.currency,b.poManual,a.orderDate,deliveryDate,   \r\n"
					+ " (select username from Tblogin  where id=a.orderby) OrderBy,   \r\n"
					+ " ei.contact as MerMobile, \r\n"
					+ " ei.email as MerEmail,a.Note,a.Subject,cast(a.body as varchar(300)) as body,a.ManualPo, \r\n"
					+ " (select Signature from Tblogin where id=b.IndentPostBy) as Signature,   \r\n"
					+ " (select Signature from Tblogin where id='9') as MdSignature, \r\n"
					+ " (select organizationLogo from tbOrganizationInfo where organizationId = 1) organizationLogo, \r\n"
					+ "	b.mdapproval,a.poNo,a.caNo,a.contentNo,a.fabricsContent,a.terms \r\n"
					+ " from tbPurchaseOrderSummary a    \r\n" 
					+ " join tbAccessoriesIndentForCarton b \r\n"
					+ " on a.pono=b.pono \r\n" 
					+ " left join Tblogin l \r\n" 
					+ " on a.entryBy = l.id \r\n"
					+ " left join tbEmployeeInfo ei \r\n" 
					+ " on l.employeeId = ei.autoId \r\n"
					+ "  where a.pono='" + poNo + "' and a.supplierid='" + supplierId + "'   \r\n"
					+ " order by  b.styleid,b.PurchaseOrder,b.Itemid,b.accessoriesItemId asc";

			if (landscapeCheck) {
				jrxmlFile = session.getServletContext()
						.getRealPath("WEB-INF/jasper/order/CartonPurchaseOrderLandscape.jrxml");
			} else {
				jrxmlFile = session.getServletContext()
						.getRealPath("WEB-INF/jasper/order/CartonPurchaseOrder.jrxml");
			}

		}

		System.out.println(sql);
		System.out.println("jrxml file=" + jrxmlFile);
		SpringRootConfig sp = new SpringRootConfig();

		InputStream input = new FileInputStream(new File(jrxmlFile));

		JasperDesign jd = JRXmlLoader.load(input);
		JRDesignQuery jq = new JRDesignQuery();

		jq.setText(sql);
		jd.setQuery(jq);

		//Generating the report
		JasperReport jr = JasperCompileManager.compileReport(jd);

		JasperPrint jp = JasperFillManager.fillReport(jr, map, sp.getDataSource().getConnection());

		//Exporting the report as a PDF
		JasperExportManager.exportReportToPdfStream(jp, response.getOutputStream());
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (JRException e) {
		e.printStackTrace();
	} catch (SQLException e) {
		e.printStackTrace();
	}
%>