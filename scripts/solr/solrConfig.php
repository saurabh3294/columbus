<?php

/* Domain name of the Solr server */
define('TYPEAHEAD_SOLR_SERVER_HOSTNAME', 'guest:12345@localhost');

/* Whether or not to run in secure mode */
define('TYPEAHEAD_SOLR_SECURE', false);

/* HTTP Port to connection */
define('TYPEAHEAD_SOLR_SERVER_PORT', ((SOLR_SECURE) ? 8983 : 8983));

/*************************** REPLICATION ************************************/

/* Domain name of the Solr server */
define('TYPEAHEAD_SOLR_SLAVE_SERVER_HOSTNAME', 'guest:12345@localhost');

/* Whether or not to run in secure mode */
define('TYPEAHEAD_SOLR_SLAVE_SECURE', false);

/* HTTP Port to connection */
define('TYPEAHEAD_SOLR_SLAVE_SERVER_PORT', ((SOLR_SECURE) ? 8983 : 8983));

/* HTTP Basic Authentication Username */
define('SOLR_SERVER_USERNAME', '');

/* HTTP Basic Authentication password */
define('SOLR_SERVER_PASSWORD', '');

/* HTTP connection timeout */
/* This is maximum time in seconds allowed for the http data transfer operation. Default value is 30 seconds */
define('SOLR_SERVER_TIMEOUT', 10);

/* File name to a PEM-formatted private key + private certificate (concatenated in that order) */
define('SOLR_SSL_CERT', 'certs/combo.pem');

/* File name to a PEM-formatted private certificate only */
define('SOLR_SSL_CERT_ONLY', 'certs/solr.crt');

/* File name to a PEM-formatted private key */
define('SOLR_SSL_KEY', 'certs/solr.key');

/* Password for PEM-formatted private key file */
define('SOLR_SSL_KEYPASSWORD', 'StrongAndSecurePassword');

/* Name of file holding one or more CA certificates to verify peer with*/
define('SOLR_SSL_CAINFO', 'certs/cacert.crt');

/* Name of directory holding multiple CA certificates to verify peer with */
define('SOLR_SSL_CAPATH', 'certs/');

?>
