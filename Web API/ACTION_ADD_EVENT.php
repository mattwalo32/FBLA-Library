			<?php
			$name = $_GET['NAME'];
			$type = $_GET['TYPE'];
			$description = $_GET['DESCRIPTION'];
			$startTime= $_GET['STARTTIME'];
			$endTime = $_GET['ENDTIME'];
			$location = $_GET['LOCATION'];
			$image = $_GET['IMAGE'] ?? NULL;
			$open = $_GET['OPEN'] ?? 1
			$max = $_GET['MAX'] ?? 50;
			
			$stmt = $conn->prepare("INSERT INTO events (EventName, EventType, Description, StartTime, EndTime, Location, Image, Open, MaxAttendees) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			$stmt->bind_param('sssiissii', $name, $type, $description, $startTime, $endTime, $location, $image, $open, $max);
			$stmt->execute();
		
			$result->success = true;
			$result->message = "Event successfully added";
		?>