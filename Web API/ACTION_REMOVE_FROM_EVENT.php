			<?php
			$uID = $_GET['UID'];
			$eID = $_GET['EID'];
			$password = $_GET['PASSWORD'];

			require ('ValidateData.php');
			
			$stmt = $conn->prepare("SELECT Attendees FROM events WHERE EID = ?");
			$stmt->bind_param('i', $eID);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($Attendees);
			
			while($stmt->fetch()){
				$attendees = $Attendees;
			}
			
			$newAttendees = str_replace($uID.", ", "", $attendees);
			
			$stmt = $conn->prepare("UPDATE events SET Attendees = ? WHERE EID = ?");
			$stmt->bind_param('si', $newAttendees, $eID);
			$stmt->execute();
			
			$stmt = $conn->prepare("SELECT Events FROM users WHERE UID = ?");
			$stmt->bind_param('i', $uID);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($Events);
			
			while($stmt->fetch()){
				$events = $Events;
			}
			
			$newEvents = str_replace($eID.", ", "", $events);
			
			$stmt = $conn->prepare("UPDATE users SET Events = ? WHERE UID = ?");
			$stmt->bind_param('si', $newEvents, $uID);
			$stmt->execute();
			
			$result->success = true;
			$result->message = "You are no longer signed up for this event";
		?>