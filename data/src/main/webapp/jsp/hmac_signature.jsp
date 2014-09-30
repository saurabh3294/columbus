<%@ page language="java" import="java.lang.*,com.citruspay.pg.*"
		session="false" isErrorPage="false"%>

<%
String key = "a071ad4f6cf52cf1bebcd9405601d41adf61e23e";
String merchantId = request.getParameter("merchantId");
String orderAmount = request.getParameter("orderAmount");
String merchantTxnId = request.getParameter("merchantTxnId");
String currency = request.getParameter("currency");
com.citruspay.pg.net.RequestSignature reqSigantureGen = new com.citruspay.pg.net.RequestSignature(); 
String data=merchantId+orderAmount+merchantTxnId+currency;
try {
%>
<%= reqSigantureGen.generateHMAC(data, key) %>
<%
}catch(Exception e){
		
}
%>