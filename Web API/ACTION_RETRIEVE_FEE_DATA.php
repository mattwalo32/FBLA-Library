			<?php
			$uID = $_GET["uID"];
			$password = $_GET["password"];
			require('ValidateData.php');
			$stmt = $conn->prepare("SELECT FID, Amount, Description, Timestamp FROM fees WHERE UID = ?");
			$stmt->bind_param('i', $uID);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($FID, $Amount, $Description, $Timestamp);
			$totalAmount = 0;
			
			$i=0;
			$JSONResponse = array("activeFees"=>array(), "paidFees"=>array());
			while($stmt->fetch()){
				$amount = $Amount;
				if($amount > 0){
					$JSONResponse["activeFees"][$i]["FID"] = $FID;
					$JSONResponse["activeFees"][$i]["Amount"] = $Amount;
					$JSONResponse["activeFees"][$i]["Description"] = $Description;
					$JSONResponse["activeFees"][$i]["Timestamp"] = $Timestamp;
					$totalAmount += $Amount;
				}else{
					$JSONResponse["paidFees"][$i]["FID"] = $FID;
					$JSONResponse["paidFees"][$i]["Amount"] = $Amount;
					$JSONResponse["paidFees"][$i]["Description"] = $Description;
					$JSONResponse["paidFees"][$i]["Timestamp"] = $Timestamp;
				}
				$i++;
			}
			$JSONResponse["totalAmount"] = $totalAmount;
			$result->success = true;
			$result->JSON = $JSONResponse;
			?>