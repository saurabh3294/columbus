<?php
error_reporting(E_ERROR);
ini_set('display_error','1');
$docroot = dirname(__FILE__);
require_once($docroot.'/amazon-sdk/sdk.class.php');
function sendMailFromAmazon($to,  $subject, $message, $from , $cc=null, $bcc=null, $ajaxCall=true) {

    //Provide the Key and Secret keys from amazon here.
    #old keys
    #$AWS_KEY = "AKIAIPT74FHV5KIH6CBA";
    #$AWS_SECRET_KEY = "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";

    #new keys
    $AWS_KEY = "AKIAIERS5YQ2JMRPGGQA";
    $AWS_SECRET_KEY = "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";
    //certificate_authority true means will read CA of amazon sdk and false means will read CA of OS
    $CA = true;

    $amazonSes = new AmazonSES(array( "key" => $AWS_KEY, "secret" => $AWS_SECRET_KEY, "certificate_authority" => $CA ));

    if($from==''){$from = "no-reply@proptiger.com";}

	$sendArray =array();
	if(!empty($to)) { $sendArray["ToAddresses"] = array($to);}
	if(!empty($cc)) { $sendArray["CcAddresses"] = array($cc);}
	if(!empty($bcc)) { $sendArray["BccAddresses"] = array($bcc);}
	
	$response = $amazonSes->send_email($from,
        $sendArray,
        array("Subject" =>array("Data"=>$subject),
                "Body"=>array(
                				"Text"=>array("Data"=>$message),
                				"Html"=>array("Data"=>$message)
             				)
        		));


	if (!$response->isOK()) {
            if($ajaxCall)
                echo 'Not Send';
            else
                return false;
	}else {
                
                if($ajaxCall)
                   echo 'Send';
               else
                   return true;
	}
       
}

function sendRawEmailFromAmazon($to, $from, $cc, $subject, $body, $attachmentname, $attachmentpath, $destination, $ajaxCall=true){
    

	#old keys
    #$AWS_KEY = "AKIAIPT74FHV5KIH6CBA";
    #$AWS_SECRET_KEY = "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";

    #new keys
    $AWS_KEY = "AKIAIERS5YQ2JMRPGGQA";
    $AWS_SECRET_KEY = "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";


	$CA = true;
	$amazonSes = new AmazonSES(array( "key" => $AWS_KEY, "secret" => $AWS_SECRET_KEY, "certificate_authority" => $CA ));
        if($to != "")
	$message= "To:".$to."\n";
        if($from != "")
	$message.= "From:".$from."\n";
        if($cc != "")
	$message.= "Cc:".$cc."\n";
        if($subject != "")
	$message.= "Subject:".$subject."\n";
	$message.= "MIME-Version: 1.0\n";
	$message.= 'Content-Type: multipart/mixed; boundary="aRandomString_with_signs_or_9879497q8w7r8number"';
	$message.= "\n\n";
	$message.= "--aRandomString_with_signs_or_9879497q8w7r8number\n";
	$message.= 'Content-Type: text/html; charset="utf-8"';
	$message.= "\n";
	$message.= "Content-Transfer-Encoding: 7bit\n";
	$message.= "Content-Disposition: inline\n";
	$message.= "\n";
	$message.= $body."\n\n";
	$message.= "\n\n";
	$message.= "--aRandomString_with_signs_or_9879497q8w7r8number\n";
	$message.= "Content-ID: \<77987_SOME_WEIRD_TOKEN_BUT_UNIQUE_SO_SOMETIMES_A_@domain.com_IS_ADDED\>\n";
	if($attachmentname != "")
	$message.= 'Content-Type: text/plain; name='.$attachmentname;
	$message.= "\n";
	if($attachmentname != "")
	$message.= "Content-Transfer-Encoding: base64\n";
	if($attachmentname != "")
	$message.= 'Content-Disposition: attachment; filename='.$attachmentname;
	$message.= "\n";
	if($attachmentname != "")
	$message.= base64_encode(file_get_contents($attachmentpath));
	$message.= "\n";
	$message.= "--aRandomString_with_signs_or_9879497q8w7r8number--\n";
	
	 if($destination != ""){
		$response = $amazonSes->send_raw_email(array(
					'Data'=> base64_encode($message)),
					array('Source'=>'no-reply@proptiger.com', 'Destinations'=>$destination));
       }
	
	if (!$response->isOK()) {
            if($ajaxCall)
                echo 'Not Sent';
            else
                return false;
	}else {
                
                if($ajaxCall)
                   echo 'Sent';
               else
                   return true;
	}

}

?>
