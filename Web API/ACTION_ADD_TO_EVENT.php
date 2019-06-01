			<?php
			$uID = $_GET['UID'];
			$eID = $_GET['EID'];
			$password = $_GET['PASSWORD'];

			require ('ValidateData.php');
			
			$stmt = $conn->prepare("SELECT MaxAttendees, Open, Attendees FROM events WHERE EID = ?");
			$stmt->bind_param('i', $eID);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($MaxAttendees, $Open, $Attendees);
			
			while($stmt->fetch()){
				$maxAttendees = $MaxAttendees;
				$open = $Open;
				$attendees = $Attendees;
			}
			
			$attendeesExp = explode(", ", $attendees);
			$numAttendees = count($attendeesExp);
			
			$attendee = $uID.", ";
			echo $maxAttendees;
			if(!(strpos($attendees, $attendee) !== FALSE)){
				$result->success = false;
				$result->message = "You are already signed up for this event";
			}else if($numAttendees >= $maxAttendees){
				$result->success = false;
				$result->message = "There are too many people signed up for this event";
			}else if(!$open){
				$result->success = false;
				$result->message = "You cannot sign up for this event";
			}
			
			$stmt = $conn->prepare("UPDATE events SET Attendees = IFNULL(CONCAT(Attendees, ?), ?) WHERE EID = ?");
			$stmt->bind_param('ssi', $attendee, $attendee, $eID);
			$stmt->execute();
			
			$event = $eID.", ";
			$stmt = $conn->prepare("UPDATE users SET Events = IFNULL(CONCAT(Events, ?), ?) WHERE UID = ?");
			$stmt->bind_param('ssi', $event, $event, $uID);
			$stmt->execute();
			
			$result->success = true;
			$result->message = "Event successfully added";
		?>