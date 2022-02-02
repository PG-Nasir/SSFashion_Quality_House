
<%@page import="java.awt.Image"%>
<%@page import="java.util.Iterator"%>
<%@page import="pg.share.HibernateUtil"%>
<%@page import="org.hibernate.Transaction"%>
<%@page import="org.hibernate.Session"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
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
    	
    	String costingNo=(String)request.getAttribute("costingNo");
    	
        SpringRootConfig sp=new SpringRootConfig();
        
        String Sql= "";
        String jrxmlFile="";
        
        
        HashMap map=new HashMap();
    	List<HashMap<String,String>> datalist=new ArrayList<HashMap<String,String>>();
    	Session dbsession=HibernateUtil.openSession();
		Transaction tx=null;
		
		Sql="select * from funCostingForStyleWiseItemNewVersion('"+costingNo+"') a order by a.ItemName desc";
      	System.out.println("sql "+Sql);
      	
		jrxmlFile = session.getServletContext().getRealPath("WEB-INF/jasper/order/CostingCreateReportWithOutPic.jrxml");
       
        
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