			<?php
			$fID = $_GET['fID'];
			$uID = $_GET['uID'];
			$password = $_GET['password'];
			
			require('ValidateData.php');
			require('ValidateKey.php');
			
			$stmt = $conn->prepare("SELECT Description FROM fees WHERE FID = ?");
			$stmt->bind_param('i', $fID);
			$stmt->execute();
			$stmt->bind_result($Description);
			
			while($stmt->fetch()){
				$description = $Description;
			}
			
			$description .= " --- FEE PAID ON ".time();
			
			$stmt = $conn->prepare("UPDATE fees SET Amount = 0, Description = ? WHERE FID = ?");
			$stmt->bind_param('si', $description, $fID);
			$stmt->execute();
			
			$result->success = true;
			$result->message = "Fee paid";
			?>