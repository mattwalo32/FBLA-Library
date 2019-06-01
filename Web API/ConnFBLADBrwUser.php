<?php
	DEFINE('dbhost', '127.0.0.1', false);
	DEFINE('dbuser', 'walowtec_rUser', false);
	DEFINE('dbpassword', '#KTiCeHuVX-Z', false);
	DEFINE('dbname', 'walowtec_fbla_library_db', false);
		  
	$conn = mysqli_connect(dbhost, dbuser, dbpassword);
	mysqli_select_db($conn, dbname);
		  
	if(!$conn){
		$result->success = false;
		$result->message = "Could not connect to database";
		die(json_encode($result));
	}
?>