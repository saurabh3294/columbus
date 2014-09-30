<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN">
<html>
<head>
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html;CHARSET=iso-8859-1">
<title>Transaction Details Search</title>
<link href="../css/default.css" rel="stylesheet" type="text/css">

</head>
<body>
	<div id="page-header">
		<div class="page-wrap">
			<div class="logo-wrapper">
				<a href="/citruspay-admin-site/"> <img height="32" width="81"
					src="../images/logo_citrus.png" alt="Citrus" />
				</a>
			</div>
		</div>
	</div>
	<div id="page-client-logo">&#160;</div>
	<div id="page-wrapper">
		<div class="box-white">
			<div class="page-content">
				<%@ page language="java" import="java.util.*" session="false"
					isErrorPage="false"%>

				<form name="frmSearch">

					<input type="hidden" name="actionchanged" value="">

					<h3>Transaction Details Search</h3>

					<div style="overflow: auto;">
						<ul class="form-wrapper add-merchant clearfix">
							<li class="clearfix"><label>Merchant Access Key :</label> <input
								type="text" name="MerchantID" size="20" class="text"
								maxlength="64" value=""></li>

							<li class="clearfix"><label width="125px;">Enter Txn
									Start Date:</label> <input type="text" name="txtStartDate" size="20"
								class="text" maxlength="64" value="">&#160;(Please enter date in YYYY-MM-DD HH:MM:SS format only)</li>

							<li class="clearfix"><label width="125px;">Enter Txn
									End Date:</label> <input type="text" name="txtEndDate" size="20"
								class="text" maxlength="64" value="">&#160;(Please enter date in YYYY-MM-DD HH:MM:SS format only. Txn End Date should be within one month of start date)</li>

							<li class="clearfix"><label width="125px;">Enter Txn Search Position:
									</label> <input type="text" name="fromPosition" size="20"
								class="text" maxlength="64" value="">&#160;(Please enter Txn Search Position)</li>
								
							<li><label width="125px;">&nbsp;</label> <input
								type="button" class="btn-orange" name="btnSub" value="Search"
								onClick="JavaScript:onClk_Submit();"></li>

						</ul>
					</div>
				</form>


				<%
					if ("Submit".equals(request.getParameter("actionchanged"))) {
						String key = "338a66359a415abb25586f19192f62ff8df736ba";
						com.citruspay.pg.util.CitruspayConstant.merchantKey = key;
						java.util.Map params = new java.util.HashMap();
						params.put("merchantAccessKey", request.getParameter("MerchantID"));
						params.put("txnStartDate", request.getParameter("txtStartDate"));
						params.put("txnEndDate", request.getParameter("txtEndDate"));
						params.put("bankName", "ABC BANK");
						params.put("fromPosition", request.getParameter("fromPosition"));

						com.citruspay.pg.model.TransactionSearchCollection trans = com.citruspay.pg.model.TransactionSearch
								.all(params);

						System.out.println("Response Code: " + trans.getRespCode());
						System.out.println("Response Message : "
								+ trans.getRespMessage());
						
						System.out.println("No of Transactions : "
								+ trans.getTransaction());
				%>
				<div>
					<div>

						<h3>Transaction List</h3>
						<% if (trans.getTransaction() != null) {%>
						Showing <%=trans.getNoOfTxnsToDisplay() %> of <%=trans.getTotalTxnCount()%>
						<% }%>

						<ul class="tbl-wrapper clearfix" id="chkoutPageUserPramList">
							<li class="tbl-header">
								<div class="tbl-col  txnList col-11">Response Code</div>
								<div class="tbl-col  txnList col-4">Response Message</div>
								<div class="tbl-col  txnList col-4">Merchant Txn Id</div>
								<div class="tbl-col  txnList col-4">Txn Id</div>
								<div class="tbl-col  txnList col-4">Epg Txn Id</div>
								<div class="tbl-col  txnList col-5">AuthIdCode:</div>
								<div class="tbl-col  txnList col-3">Issuer Ref. No.</div>
								<div class="tbl-col  txnList col-7">TxnType</div>
								<div class="tbl-col  txnList col-11">Amount</div>
								<div class="tbl-col  txnList col-7">Date</div>
								<div class="tbl-col  txnList col-10">Payment Mode</div>
								<div class="tbl-col  txnList col-10">PG / Bank Name</div>
								<div class="tbl-col  txnList col-11">Original Amount</div>	
								<div class="tbl-col  txnList col-11">Adjustment</div>
								<div class="tbl-col  txnList col-11">Rule Name</div>
								<div class="tbl-col  txnList col-11">Offer Type</div>
								<div class="tbl-col  txnList col-10">Transaction Amount</div>
							</li>
							<%
								if (trans.getTransaction() != null && !trans.getTransaction().isEmpty()) {
										for (int index = 0; index < trans.getTransaction().size(); index++) {
											com.citruspay.pg.model.TransactionSearch transaction = (com.citruspay.pg.model.TransactionSearch) trans
													.getTransaction().get(index);
							%>
							<li>
								<div class="tbl-col  txnList col-11">
									<%
										out.println(transaction.getRespCode());
									%>
								</div>
								<div class="tbl-col  txnList col-4">
									<%
										out.println(transaction.getRespMessage()== null
												? ""
												:transaction.getRespMessage());
									%>
								</div>
								<div class="tbl-col  txnList col-4">
									<%
										out.println(transaction.getMerchantTxnId() == null
															? ""
															: transaction.getMerchantTxnId());
									%>
								</div>
								<div class="tbl-col  txnList col-4">
									<%
										out.println(transaction.getTxnId() == null
															? ""
															: transaction.getTxnId());
									%>
								</div>
								<div class="tbl-col  txnList col-4">
									<%
										out.println(transaction.getPgTxnId() == null
															? ""
															: transaction.getPgTxnId());
									%>
								</div>
								<div class="tbl-col  txnList col-5">
									<%
										out.println(transaction.getAuthIdCode() == null
															? ""
															: transaction.getAuthIdCode());
									%>
								</div>
								<div class="tbl-col  txnList col-3">
									<%
										out.println(transaction.getRRN() == null
															? ""
															: transaction.getRRN());
									%>
								</div>
								<div class="tbl-col  txnList col-7">
									<%
										out.println(transaction.getTxnType() == null
															? ""
															: transaction.getTxnType());
									%>
								</div>
								<div class="tbl-col  txnList col-11">
									<%
										out.println(transaction.getAmount() == null
															? ""
															: transaction.getAmount());
									%>
								</div>
								<div class="tbl-col  txnList col-7">
									<%
										out.println(transaction.getTxnDateTime() == null
															? ""
															: transaction.getTxnDateTime());
									%>
								</div>
								<div class="tbl-col  txnList col-10">
									<%
										out.println(transaction.getPaymentMode() == null
															? ""
															: transaction.getPaymentMode());
									%>
								</div>									
								<div class="tbl-col  txnList col-10">
									<%
										out.println(transaction.getBankOrPgName() == null
															? ""
															: transaction.getBankOrPgName());
									%>
								</div>	
								<div class="tbl-col  txnList col-11">
									<%
										out.println(transaction.getOriginalAmount() == null ? "" : transaction
													.getOriginalAmount());
									%>
								</div>								
								<div class="tbl-col  txnList col-11">
									<%
										out.println(transaction.getAdjustment() == null ? "" : transaction
													.getAdjustment());
									%>
								</div>	
								<div class="tbl-col  txnList col-11">
									<%
										out.println(transaction.getRuleName() == null ? "" : transaction
													.getRuleName());
									%>
								</div>	
								<div class="tbl-col  txnList col-11">
									<%
										out.println(transaction.getOfferType() == null ? "" : transaction
													.getOfferType());
									%>
								</div>
								
								<div class="tbl-col  txnList col-10">
									<%
										out.println(transaction.getTransactionAmount() == null ? "" : transaction
													.getTransactionAmount());
									%>
								</div>
							</li>
							<%
								}
									}else{
										%>
										
										<div class="tbl-col  txnList col-11">
									<%
										out.println(trans.getRespCode());	
									%>
								</div>
								<div class="tbl-col  txnList col-2">
									<%
									out.println(trans.getRespMessage()== null
												? ""
												:trans.getRespMessage());
									%>
								</div>
										
										<%
										
									}
							%>
						</ul>
					</div>

					<%
						}
					%>

					<script language="javascript">
						function onClk_Submit() {

							document.frmSearch.actionchanged.value = "Submit";
							document.frmSearch.method = "POST";
							document.frmSearch.submit();

						}
					</script>
					<!-- end content -->
			</div>
		</div>
	</div>
	</div>
	<div
		style="padding-left: 800px; padding-bottom: 20px; padding-top: 20px;">
		<div>Copyrights © 2012 Citrus.</div>
	</div>
</body>