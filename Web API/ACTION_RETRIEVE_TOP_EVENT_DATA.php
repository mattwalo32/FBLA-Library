			<?php
			$curTime = time();
			
			$stmt = $conn->prepare("SELECT EventName, EventType, StartTime, EndTime, Image FROM events  WHERE StartTime > ? ORDER BY StartTime");
			$stmt->bind_param('i', $curTime);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($EventName, $EventType, $StartTime, $EndTime, $Image);
			
			$JSONResponse = array();
			$i = 0;
			
			while($stmt->fetch()){
				$JSONResponse[$i]["EventName"] = $EventName;
				$JSONResponse[$i]["EventType"] = $EventType;
				$JSONResponse[$i]["StartTime"] = $StartTime;
				$JSONResponse[$i]["EndTime"] = $EndTime;
				$JSONResponse[$i]["Image"] = $Image;
			}
			
			$result->JSONResponse = $JSONResponse;
			$result->success = true;
		?>