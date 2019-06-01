<!DOCTYPE HTML>
<html>
	<head>
	
	</head>
	<body>
	<?php
		$result = new stdClass();

		$stmt = $conn->prepare("SELECT UID FROM users WHERE UID = ? AND Password = ?");
		$stmt->bind_param('is', $uID, $password);
		$stmt->execute();
		$stmt->store_result();
		$stmt->bind_result($UID);
			
		if($stmt->num_rows > 0){
			$result->validationSuccess = true;
		}else{
			$result->success = false;
			$result->message = "Invalid credentials";
			die(json_encode($result));
		}
	?>
	</body>
</html>