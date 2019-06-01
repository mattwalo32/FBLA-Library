			<?php
			//If the variables needed to create a user are missing, then die;
				$name = $_GET["name"];
				$password = $_GET["password"];
				$email = $_GET["email"];
				$sID = $_GET["sid"];
				$status = $_GET["status"];
			
			/*Prepare and execute statement to 
			  retrieve users with matching school ID
			  If any users are returned, then an account
			  with this ID already exists
			  */
			$stmt = $conn->prepare("SELECT UID FROM users WHERE SID = ?");
			$stmt->bind_param('i', $sID);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($UID);
			
			//If account already exists then die
			if($stmt->num_rows != 0){
				$result->success = false;
				$result->message = "An account with this school ID already exists";
				$result->Schoolid = $sID;
				die(json_encode($result));
			}
			
			/*Prepare and execute statement to 
			  retrieve users with matching email address
			  If any users are returned, then an account
			  with this email already exists
			  */
			$stmt = $conn->prepare("SELECT UID FROM users WHERE Email = ?");
			$stmt->bind_param('s', $email);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($UID);
			
			//If account already exists then die
			if($stmt->num_rows != 0){
				$result->success = false;
				$result->message = "An account with this email already exists";
				die(json_encode($result));
			}
			
			/*Prepare and execute statement to 
			  insert a new user into the users
			  table
			  */
			$stmt = $conn->prepare("INSERT INTO users "
			."(SID, Name, Email, Password, Status) "
			."VALUES "
			."(?, ?, ?, ?, ?)");
			$stmt->bind_param('issss', $sID, $name, $email, $password, $status);
			$stmt->execute();
				
			$stmt = $conn->prepare("SELECT UID FROM users WHERE SID = ? AND Password = ? LIMIT 1");
			$stmt->bind_param('ss', $sID, $password);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($UID);
			while($stmt->fetch()){
				$result->UID = $UID;
			}
			$result->success = true;
			$result->message = "Account successfully created";
			?>