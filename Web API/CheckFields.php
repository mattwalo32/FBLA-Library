<?php
	$fields = array("Test"=>"yes", "Test2"=>null);
	
	$willDie = false;
	foreach($fields as $fieldName => $fieldValue){
		if($fieldValue == NULL){
			$result->missingParameters = true;
			$result->message = "Required fields are missing";
			$willDie = true;
		}
	}
	
	if($willDie)
		die(json_encode($result));
?>