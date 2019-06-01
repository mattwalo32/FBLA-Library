			<?php
			$gID = $_GET['gID'];
			$uID = $_GET['uID'];
			$password = $_GET['password'];
			$rating = $_GET['Rating'];
			$comment = $_GET['Comment'];
			require('ValidateData.php');
			
			$cID = 0;
			$previousRating = 0;
			
			$stmt = $conn->prepare("SELECT CID, rating FROM reviews WHERE UID=?");
			$stmt->bind_param('i', $uID);
			$stmt->execute();
			$stmt->bind_result($CID, $Rating);
		
			while($stmt->fetch()){
				$cID = $CID;
				$previousRating = $Rating;
			}
			 
			$stmt = $conn->prepare("SELECT NumberRatings FROM books WHERE GID=?");
			$stmt->bind_param('s', $gID);
			$stmt->execute();
			$stmt->bind_result($NumberRatings);
		
			while($stmt->fetch()){
				$numberRatings = $NumberRatings;
			}
			 
			 $updating = false;
			 
			 $curTime = time();
			 
			if($cID == 0){
				$stmt = $conn->prepare("INSERT INTO reviews (UID, GID, Rating, Comment, Timestamp) VALUES (?, ?, ?, ?, ?)");
				$stmt->bind_param('isdsi', $uID, $gID, $rating, $comment, $curTime);
				$stmt->execute();
				
				$numberRatings++;
				$stmt = $conn->prepare("UPDATE books SET NumberRatings = ? WHERE GID = ?");
				$stmt->bind_param('is', $NumberRatings, $gID);
				$stmt->execute();
			}else{
				$updating = true;
				$stmt = $conn->prepare("UPDATE reviews SET Rating = ?, Comment = ?, Timestamp = ? WHERE CID = ?");
				$stmt->bind_param('dsii', $rating, $comment, $curTime, $cID);
				$stmt->execute();
			}
			
			$result->success = true;
			$result->message = "New review added";
			
			$stmt = $conn->prepare("SELECT AverageRating FROM books WHERE GID=?");
			$stmt->bind_param('s', $gID);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($AverageRating);
		
			while($stmt->fetch()){
				$averageRating = $AverageRating;
			}
			
			
			$totalStars = $updating?($averageRating * $numberRatings + ($rating-$previousRating)):(($averageRating * ($numberRatings - 1)) + $rating);
			
			$stmt = $conn->prepare("UPDATE books SET NumberRatings=? WHERE GID = ?");
			$stmt->bind_param('is', $numberRatings, $gID);
			$stmt->execute();
			
			if($numberRatings == 0 || ($numberRatings == 1 && $updating)){
				$averageRating = $rating;
			}else{
				$averageRating = $totalStars / $numberRatings;
			}
			
			$stmt = $conn->prepare("UPDATE books SET AverageRating = ? WHERE GID = ?");
			$stmt->bind_param('ds', $averageRating, $gID);
			$stmt->execute();
		?>