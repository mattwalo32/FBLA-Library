			<?php
			$amount = $_GET['amount'];
			$description = $_GET['description'];
			$uID = $_GET['uID'];
			$password = $_GET['password'];
			
			require('ValidateData.php');
			require('ValidateKey.php');
			
			$curTime = time();
			
			$stmt = $conn->prepare("INSERT INTO fees (UID, Amount, Description, Timestamp) VALUES (?, ?, ?, ?)");
			$stmt->bind_param('iisi', $uID, $amount, $description, $curTime);
			$stmt->execute();
			
			$result->success = true;
			$result->message = "Fee added to account";
			?>