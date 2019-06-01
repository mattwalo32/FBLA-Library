<?php
	$stmt = $conn->prepare("SELECT ShortDescription, Description, LinkedItem, Image FROM dailyMessage");
	$stmt->execute();
	$stmt->store_result();
	$stmt->bind_result($ShortDescription, $Description, $LinkedItem, $Image);
	
	$JSONResponse = array();	
	while($stmt->fetch()){
		$JSONResponse['ShortDescription'] = $ShortDescription;
		$JSONResponse['Description'] = $Description;
		$JSONResponse['LinkedItem'] = $LinkedItem;
		$JSONResponse['Image'] = $Image;
	}
				
			
	$result->success = 3;
	$result->JSON = $JSONResponse;
?>
			