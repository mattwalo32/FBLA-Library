			<?php
			$BID = $_GET['BID'];
			$uID = $_GET['uID'];
			$password = $_GET['password'];
			
			require('ValidateData.php');
			
			$stmt = $conn->prepare("UPDATE books SET UID = NULL, CheckoutTimestamp = NULL, ReturnTimestamp = NULL WHERE BID = ?");
			$stmt->bind_param('i', $BID);
			$stmt->execute();
			
			$stmt = $conn->prepare("SELECT Books FROM users WHERE UID = ?");
			$stmt->bind_param('i', $uID);
			$stmt->execute();
			$stmt->bind_result($Books);
			
			while($stmt->fetch()){
				$books = $Books;
			}
			
			$newBooks = str_replace($BID.", ", "", $books);
			
			$stmt = $conn->prepare("UPDATE users SET Books = ? WHERE UID = ?");
			$stmt->bind_param('si', $newBooks, $uID);
			$stmt->execute();
			
			$result->success = true;
			$result->message = "Book has been returned";
			?>