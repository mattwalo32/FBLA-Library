			<?php
			$BID = $_GET['BID'];
			$uID = $_GET['uID'];
			$password = $_GET['password'];
			require('ValidateData.php');
			
			$stmt = $conn->prepare("SELECT Status, Books FROM users WHERE UID = ?");
			$stmt->bind_param('i', $uID);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($Status, $Books);
			
			while($stmt->fetch()){
				$status = $Status;
				$books = $Books;
			}
			$numBooks = substr_count($books, ", ");
			
			$stmt = $conn->prepare("SELECT StudentLimit, TeacherLimit, CheckoutPeriod FROM globalVars");
			$stmt->execute();
			$stmt->bind_result($StudentLimit, $TeacherLimit, $CheckoutPeriod);
			
			while($stmt->fetch()){
				$studentLimit = $StudentLimit;
				$teacherLimit = $TeacherLimit;
				$checkoutPeriod = $CheckoutPeriod;
			}
			$returnDate = time() + (($checkoutPeriod)/1000);
			
			if(($status=="Student" && $numBooks < $studentLimit) || ($status=="Teacher" && $numBooks < $teacherLimit)){
				$stmt = $conn->prepare("SELECT Title FROM books WHERE BID = ? AND UID IS NULL");
				$stmt->bind_param('i', $BID);
				$stmt->execute();
				$stmt->store_result();
				$stmt->bind_result($Title);
				
				if($stmt->num_rows == 0){
					$result->success = false;
					$result->message = "Book is already checked out";
					die(json_encode($result));
				}
				
				$curTime = time();
				
				$stmt = $conn->prepare("UPDATE books SET UID = ?, CheckoutTimestamp = ?, ReturnTimestamp = ? WHERE BID = ?");
				$stmt->bind_param('iiii', $uID, $curTime, $returnDate, $BID);
				$stmt->execute();
			}else{
				$result->success = false;
				$result->message = "You have too many books checked out";
				die(json_encode($result));
			}
			
			$booksString = $books.$BID.", ";
			
			$stmt = $conn->prepare("UPDATE users SET Books = ? WHERE UID = ?");
			$stmt->bind_param('si', $booksString, $uID);
			$stmt->execute();
			
			$result->success = true;
			$result->message = "You have successfully checked out this book";
			?>