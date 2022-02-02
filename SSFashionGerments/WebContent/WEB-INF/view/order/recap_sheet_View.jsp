<%@ page contentType="application/pdf" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ page import="net.sf.jasperreports.engine.design.JRDesignQuery" %>
<%@ page import="net.sf.jasperreports.engine.design.JasperDesign" %>
<%@ page import="net.sf.jasperreports.engine.xml.JRXmlLoader" %>
<%@ page import="net.sf.jasperreports.engine.*" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="java.io.FileNotFoundException" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="pg.config.*" %>

<%

    try {
		
    	String mBuyerName=(String)request.getAttribute("mBuyerName");
    	String sDate=(String)request.getAttribute("sDate");
    	String eDate=(String)request.getAttribute("eDate");

        SpringRootConfig sp=new SpringRootConfig();
        
      	String Sql="";
      	Sql="select (select organizationLogo from tbOrganizationInfo where organizationId='1') as organizationLogo, (select b.factoryname from tbfactoryinfo b where b.factoryid=boed.factoryid) as factoryname, (select b.styleno from tbstylecreate b where b.styleid=boed.styleid) as styleno, boed.purchaseorder,'' as brand, '' as SKU, boes.totalunit, boes.shipmentdate, isnull(boes.fabricpo,'') as fabricpo, isnull(boes.triumpo,'') as triumpo,'"+sDate+"' as startDate,'"+eDate+"' as endDate from TbBuyerOrderEstimateSummary boes join TbBuyerOrderEstimateDetails boed on boes.autoid=boed.buyerorderid where boes.shipmentdate between '"+sDate+"' and '"+eDate+"' and boes.buyerid='"+mBuyerName+"'";
		System.err.println("Recap sheet sql : "+Sql);
      	String jrxmlFile = session.getServletContext().getRealPath("WEB-INF/jasper/order/recapSheet.jrxml");
        InputStream input = new FileInputStream(new File(jrxmlFile));

        
    	JasperDesign jd=JRXmlLoader.load(input);
		JRDesignQuery jq=new JRDesignQuery();
		
		jq.setText(Sql);
		jd.setQuery(jq);
		
        //Generating the report
        JasperReport jr = JasperCompileManager.compileReport(jd);
      
        JasperPrint jp = JasperFillManager.fillReport(jr, null, sp.getDataSource().getConnection());

        //Exporting the report as a PDF
        JasperExportManager.exportReportToPdfStream(jp, response.getOutputStream());
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (JRException e) {
        e.printStackTrace();
    }  catch (SQLException e) {
        e.printStackTrace();
    }


%>