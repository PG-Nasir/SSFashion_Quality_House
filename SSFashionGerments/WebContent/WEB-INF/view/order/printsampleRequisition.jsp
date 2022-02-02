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
		
    	String SampleReqId=(String)request.getAttribute("SampleReqId");
	
        SpringRootConfig sp=new SpringRootConfig();
        
		String Sql="select a.sampleReqId,a.PurchaseOrder,(lo.username) as merchendiser,b.dateLine,b.samplerequestdate,g.name,d.StyleNo,e.itemId,e.itemname,a.sizeGroupId \n"+ 
				"from TbSampleRequisitionDetails a  \n"+
				"left join tbSampleRequisition b  \n"+
				"on a.sampleReqId=b.sampleReqId  \n"+
				"left join TbStyleCreate d  \n"+
				"on a.StyleId=d.StyleId  \n"+
				"left join tbItemDescription e  \n"+
				"on a.ItemId=e.itemid  \n"+
				"left join tbBuyer g on a.buyerId=g.id left join tblogin lo on a.UserId=lo.id left join TbSampleTypeInfo s  \n"+
				"on s.AutoId=a.SampleTypeId  \n"+
				"where a.sampleReqId='"+SampleReqId+"'  \n"+
				"group by a.sampleReqId,lo.username,g.name,a.PurchaseOrder,b.dateLine,b.samplerequestdate,d.StyleNo,e.itemId,e.itemname,a.sizeGroupId \n"+ 
				"order by a.sampleReqId,a.sizeGroupId";
      	System.out.println("sql "+Sql);
      	
		String jrxmlFile = session.getServletContext().getRealPath("WEB-INF/jasper/order/SampleRequistionReport.jrxml");
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