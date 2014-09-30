<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN">
<html>
<head>
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html;CHARSET=iso-8859-1">
<title>Response</title>
<link href="../css/default.css" rel="stylesheet" type="text/css">

</head>
<body>
	<%
		String pgRespCode, authIdCode, issuerRefNo, pgTxnNo, TxMsg, TxStatus, TxRefNo, TxId, amount, currency, TxGateway, paymentMode, issuerCode, maskedCardNumber, cardType, mobileNo, firstName, lastName, email, addressStreet1, addressCity, addressState, addressZip, addressCountry;

		pgRespCode = request.getParameter("pgRespCode");
		authIdCode = request.getParameter("authIdCode");
		issuerRefNo = request.getParameter("issuerRefNo");
		pgTxnNo = request.getParameter("pgTxnNo");
		TxMsg = request.getParameter("TxMsg");
		TxStatus = request.getParameter("TxStatus");
		TxRefNo = request.getParameter("TxRefNo");
		TxId = request.getParameter("TxId");
		amount = request.getParameter("amount");
		currency = request.getParameter("currency");
		TxGateway = request.getParameter("TxGateway");
		paymentMode = request.getParameter("paymentMode");
		issuerCode = request.getParameter("issuerCode");
		maskedCardNumber = request
				.getParameter("maskedCardNumber");
		cardType = request.getParameter("cardType");
		mobileNo = request.getParameter("mobileNo");
		firstName = request.getParameter("firstName");
		lastName = request.getParameter("lastName");
		email = request.getParameter("email");
		addressStreet1 = request.getParameter("addressStreet1");
		addressCity = request.getParameter("addressCity");
		addressState = request.getParameter("addressState");
		addressZip = request.getParameter("addressZip");
		addressCountry = request.getParameter("addressCountry");
		System.out.println("Notify response start ==========================================");
		System.out.println("pgRespCode=" + pgRespCode + "\nauthIdCode="
				+ authIdCode + "\nissuerRefNo=" + issuerRefNo
				+ "\npgTxnNo=" + pgTxnNo + "\nTxMsg=" + TxMsg
				+ "\nTxStatus=" + TxStatus + "\nTxRefNo=" + TxRefNo
				+ "\nTxId=" + TxId + "\namount=" + amount + "\ncurrency="
				+ currency + "\nTxGateway=" + TxGateway + "\npaymentMode="
				+ paymentMode + "\nissuerCode=" + issuerCode
				+ "\nmaskedCardNumber=" + maskedCardNumber + "\ncardType="
				+ cardType + "\nmobileNo=" + mobileNo + "\nfirstName="
				+ firstName + "\nlastName=" + lastName + "\nemail=" + email
				+ "\naddressStreet1=" + addressStreet1 + "\naddressCity="
				+ addressCity + "\naddressState=" + addressState
				+ "\naddressZip=" + addressZip + "\naddressCountry="
				+ addressCountry);
		System.out.println("Notify response end ============================================");
	%>

	<div
		style="padding-left: 800px; padding-bottom: 20px; padding-top: 20px;">
		<div>Copyrights © 2012 Citrus.</div>
	</div>
</body>

</html>