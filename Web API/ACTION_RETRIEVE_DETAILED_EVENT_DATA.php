			<?php
			$curTime = time();
			
			$stmt = $conn->prepare("SELECT EID, EventName, EventType, Description, StartTime, EndTime, Location, Image, MaxAttendees, Attendees, Open FROM events  WHERE StartTime > ? ORDER BY StartTime");
			$stmt->bind_param('i', $curTime);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($EventName, $EventType, $Description, $StartTime, $EndTime, $Location, $Image, $Open, $MaxAttendees, $Attendees);
			
			$JSONResponse = array();
			$i = 0;
			
			while($stmt->fetch()){
				$JSONResponse[$i]["EventName"] = $EventName;
				$JSONResponse[$i]["EventType"] = $EventType;
				$JSONResponse[$i]["Description"] = $Description;
				$JSONResponse[$i]["StartTime"] = $StartTime;
				$JSONResponse[$i]["Location"] = $Location;
				$JSONResponse[$i]["EndTime"] = $EndTime;
				$JSONResponse[$i]["Image"] = $Image;
				$JSONResponse[$i]["Open"] = $Open;
				$JSONResponse[$i]["MaxAttendees"] = $MaxAttendees;
				
				$numAttendees = substr_count($Attendees, ", ");
				
				$JSONResponse[$i]["NumberAttendees"] = $numAttendees;
				
				$i++
			}
			
			$result->JSONResponse = $JSONResponse;
			$result->success = true;
		?>