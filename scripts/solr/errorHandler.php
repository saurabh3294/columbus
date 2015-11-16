<?php

$currentDir = dirname(__FILE__);
require_once ($currentDir . '/../send_mail_amazon.php');
require_once 'emailConfig.php';

function myErrorHandler($errno, $errstr, $errfile, $errline)
{
	$mailSubject;
	$mailMessage;		
	if(in_array($errno, array(E_WARNING,E_USER_WARNING))){
		//print_r(sprintf(ERROR_CONSOLE_MESSAGE, "myErrorHandler", $errno, $errstr, $errfile, $errline));
		$mailSubject = 'Warning in solr Indexing on ' . exec('hostname');
		$mailMessage = "Warning in solr Indexing on server: " . exec('hostname') . " on line  $errline in file $errfile. Warning message: $errstr.";
		$additionalEmailRecipients = explode(",", ADDITIONAL_EMAIL_RECEIPIENTS);
		sendRawEmailFromAmazon(FAILURE_EMAIL_RECEIPIENT, '', '', $mailSubject , $mailMessage, '', '', $additionalEmailRecipients);
    }
    else if(in_array($errno, array(E_ERROR,E_USER_ERROR))){
    	//print_r(sprintf(ERROR_CONSOLE_MESSAGE, "myErrorHandler", $errno, $errstr, $errfile, $errline));
    	$mailSubject = 'Error in solr Indexing on ' . exec('hostname');
    	$mailMessage = "Error in solr Indexing on server: " . exec('hostname') . " on line  $errline in file $errfile. Error message: $errstr. Aborting....";
		$additionalEmailRecipients = explode(",", ADDITIONAL_EMAIL_RECEIPIENTS);
		sendRawEmailFromAmazon(FAILURE_EMAIL_RECEIPIENT, '', '', $mailSubject , $mailMessage, '', '', $additionalEmailRecipients);
    	exit(1);
    }
    else{
      return false;
    }
}

function fatalErrorShutdownHandler()
{
	//print_r("\nErrorHandler(fatalErrorShutdownHandler)\n");
	$last_error = error_get_last();
	if ($last_error['type'] == E_ERROR || $last_error['type'] == E_USER_ERROR) {
		myErrorHandler($last_error['type'], $last_error['message'], $last_error['file'], $last_error['line']);
	}
}

// registering error handler function with php
$old_error_handler = set_error_handler("myErrorHandler");
register_shutdown_function('fatalErrorShutdownHandler');
?>
