			<?php
			ini_set("allow_url_fopen", 1);
			
			$BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";
			$gIDSearchTerm = "id:";
			
			$gID = $_GET['gID'];
			$copies = $_GET['copies'];
			
			$displayCopies = $copies;
			
			$stmt = $conn->prepare("SELECT Copies FROM books WHERE GID = ? LIMIT 1");
			$stmt->bind_param("s", $gID);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($Copies);
			
			if($stmt->num_rows > 0){
				while($stmt->fetch()){
					$displayCopies = $Copies;
				}
				$displayCopies+=$copies;
				
				$stmt = $conn->prepare("UPDATE books SET Copies = ? WHERE GID = ?");
				$stmt->bind_param("is", $displayCopies, $gID);
				$stmt->execute();
			}
			
			$bookInfoJson = file_get_contents($BASE_URL . $gIDSearchTerm . $gID);
			$bookObj = json_decode($bookInfoJson, true);
			
			$dbEntry = new stdClass();
			$dbEntry->title = $bookObj["items"][0]["volumeInfo"]["title"];
			$dbEntry->description = $bookObj["items"][0]["volumeInfo"]["description"];
			$dbEntry->thumbnailSmall = $bookObj["items"][0]["volumeInfo"]["imageLinks"]["smallThumbnail"];
			$dbEntry->thumbnail = $bookObj["items"][0]["volumeInfo"]["imageLinks"]["thumbnail"];
			$dbEntry->isbn10 = $bookObj["items"][0]["volumeInfo"]["industryIdentifiers"][1]["identifier"];
			$dbEntry->isbn13 = $bookObj["items"][0]["volumeInfo"]["industryIdentifiers"][0]["identifier"];
			$dbEntry->authors = implode(", ", $bookObj["items"][0]["volumeInfo"]["authors"]);;
			$dbEntry->textSnippet = $bookObj["items"][0]["searchInfo"]["textSnippet"];
			$dbEntry->subject = implode(", ",  $bookObj["items"][0]["volumeInfo"]["categories"]);
			$dbEntry->gID = $gID;
			$dbEntry->copies = $displayCopies;
			
			for($i=0; $i<$copies; $i++){
				$stmt = $conn->prepare("INSERT INTO books "
				."(Title, Description, SmallThumbnail, Thumbnail, ISBN10, ISBN13, Authors, TextSnippet, Copies, GID, subject) "
				."VALUES "
				."(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				$stmt->bind_param('sssssssssss', $dbEntry->title, $dbEntry->description, $dbEntry->thumbnailSmall, $dbEntry->thumbnail, $dbEntry->isbn10, $dbEntry->isbn13, $dbEntry->authors, $dbEntry->textSnippet, $dbEntry->copies, $dbEntry->gID, $dbEntry->subject);
				$stmt->execute();
			}
				
			$result->success = true;
			$result->message = "Book successfully added";
			?>