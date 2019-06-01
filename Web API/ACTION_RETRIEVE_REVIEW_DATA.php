			<?php
			$gID = $_GET["GID"];
			
			$stmt = $conn->prepare("SELECT CID, UID, Rating, Comment, Timestamp FROM reviews WHERE GID = ?");
			$stmt->bind_param('s', $gID);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($CID, $UID, $Rating, $Comment, $Timestamp);
			
			$JSONResponse = array();
			$i = 0;
			
			while($stmt->fetch()){
				$JSONResponse[$i]["CID"] = $CID;
				$JSONResponse[$i]["UID"] = $UID;
				$JSONResponse[$i]["Rating"] = $Rating;
				$JSONResponse[$i]["Comment"] = $Comment;
				$JSONResponse[$i]["Timestamp"] = $Timestamp;
				
				$i++
			}
			
			$result->JSONResponse = $JSONResponse;
			$result->success = true;
		?>