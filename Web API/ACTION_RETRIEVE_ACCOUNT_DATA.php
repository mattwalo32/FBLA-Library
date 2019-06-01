			<?php
			//If the variables needed to fetch a user are missing, then die;
			$password = $_GET["password"];
			$email = $_GET["email"];
		
			
			/*Prepare and execute statement to 
			  select all information on a user
			  with a matching password and email.
			  */
			$stmt = $conn->prepare("SELECT UID, SID, Name, Status FROM users WHERE password=? AND email=?");
			$stmt->bind_param('ss', $password, $email);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($UID, $SID, $Name, $Status, $Books);
			
			//If query was successful but no match was found, then die
			if($stmt->num_rows < 1){
				$result->success = false;
				$result->message = "Email or password are incorrect";
				die(json_encode($result));
			}
			
			while($stmt->fetch()){
				$result->UID = $UID;
				$result->SchoolID = $SID;
				$result->Name = $Name;
				$result->Status = $Status;
				$books = explode(", ", $Books);
				array_pop($books);
				$result->Books = $books;
			}
			
			$result->Success = true;
			?>