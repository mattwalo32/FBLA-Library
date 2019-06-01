			<?php
			//If the variables needed to fetch a user are missing, then die;
			$bid = $_GET["bID"];
			$password = $_GET["password"];
			$uID = $_GET["uID"];
			
			require("ValidateData.php");
			
			/*Prepare and execute statement to 
			  select all information on a user
			  with a matching password and email.
			  */
			$likeClause = "%".$uID.", %" ;
			$stmt = $conn->prepare("SELECT WaitingList FROM books WHERE BID = ? AND UID IS NOT NULL");
			$stmt->bind_param('i', $bid);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($WaitingList);
			
			//If query was successful but no match was found, then die
			if($stmt->num_rows < 1){
				$result->success = false;
				$result->message = "Could not add to waiting list";
				die(json_encode($result));
			}
			
			while($stmt->fetch()){
				$waitingList = $WaitingList;
			}
			
			if(strpos($waitingList, $uID.", ") !== false){
				$result->success = false;
				$result->message = "You are already on the list";
				die(json_encode($result));
			}
			
			$waitingListAmount = explode(", ", $waitingList);
			$waitingListAmount = array_pop($waitingListAmount);
			$waitingListAmount = count($waitingListAmount);
			
			$waitingList .= $uID.", ";
			
			$stmt = $conn->prepare("UPDATE books SET WaitingList = ? WHERE BID = ?");
			$stmt->bind_param('si', $waitingList, $bid);
			$stmt->execute();
			
			$stmt = $conn->prepare("SELECT WaitingList FROM users WHERE UID = ?");
			$stmt->bind_param('i', $uID);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($WaitingListUID);
			
			while($stmt->fetch()){
				$waitingListUID = $WaitingListUID;
			}
			
			$waitingListUID .= $bid.", ";
			
			$stmt = $conn->prepare("UPDATE users SET WaitingList = ? WHERE UID = ?");
			$stmt->bind_param('si', $waitingListUID, $uID);
			$stmt->execute();
			
			$result->Success = true;
			$result->message = "Added to waiting list";
			$result->WaitingListSize = $waitingListAmount;
			?>