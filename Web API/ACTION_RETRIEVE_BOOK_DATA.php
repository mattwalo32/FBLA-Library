			<?php
			$searchItem = $_GET["searchItem"];
			$searchString = $_GET["searchString"];
			$uID = $_GET["uID"];
			
			$queryWildcard = '%'.$searchString.'%';
		
			switch($searchItem){
				case "TITLE":
					$sql = "SELECT GID, Title, Thumbnail, SmallThumbnail FROM books WHERE Title LIKE ? GROUP BY GID LIMIT 50";
					$numVars = 1;
				break;
				
				case "AUTHOR":
					$sql = "SELECT GID, Title, Thumbnail, SmallThumbnail FROM books WHERE Authors LIKE ? GROUP BY GID LIMIT 50";
					$numVars = 1;
				break;
				
				case "ISBN":
					$sql = "SELECT GID, Title, Thumbnail, SmallThumbnail FROM books WHERE ISBN10 = ? OR ISBN13 = ? GROUP BY GID LIMIT 50";
					$numVars = 2;
					$queryWildcard = $searchString;
				break;
				
				case "CHECKEDOUT":
					$stmt = $conn->prepare("SELECT Books FROM users WHERE UID=?");
					$stmt->bind_param('i', $uID);
					$stmt->execute();
					$stmt->store_result();
					$stmt->bind_result($Books);
					
					while($stmt->fetch()){
						$books = $Books;
					}
					
					$lastPos = 0;
					$i=0;
					$booksOut = array();
					$endIndex=0;
					while(strpos($books, ",", $lastPos) != false){
						$startIndex = ($endIndex==0)?0:($endIndex + 3);
						$endIndex = strpos($books, ",", $lastPos) - 1;
						$booksOut[$i] = substr($books, $startIndex, $endIndex - $startIndex + 1);
						$lastPos = $endIndex + 3;
						$i++;
					}

					if(sizeof($booksOut)>0){
						$sql = "SELECT GID, Title, Thumbnail, SmallThumbnail, CheckoutTimestamp, ReturnTimestamp FROM books WHERE BID=$booksOut[0] OR ";
						for($i=1; $i<sizeof($booksOut); $i++){
							$sql.="BID=$booksOut[$i]";
							if($i+1<sizeof($booksOut)){
								$sql.=" OR ";
							}
						}			
					}else{
						$result->success = true;
						$result->message = "No books are checked out by this user currently";
					}
				break;
				
				case "SUBJECT":
					$sql = "SELECT GID, Title, Thumbnail, SmallThumbnail FROM books WHERE Subject LIKE ? GROUP BY GID LIMIT 50";
					$numVars = 1;
				break;
				
				default:
				case "ALL":
					$sql = "SELECT GID, Title, Thumbnail, SmallThumbnail FROM books WHERE Title LIKE ? OR SubTitle LIKE ? OR TextSnippet LIKE ? OR Description LIKE ? OR Authors LIKE ? GROUP BY GID LIMIT 50";
					$numVars = 5;
			}
			
			if($searchItem != "CHECKEDOUT"){
				$stmt = $conn->prepare($sql);
			
				if($numVars == 1)
					$stmt->bind_param('s', $queryWildcard);
				else if($numVars == 2)
					$stmt->bind_param('ss', $queryWildcard, $queryWildcard);
				else if($numVars == 5)
					$stmt->bind_param('sssss', $queryWildcard, $queryWildcard, $queryWildcard, $queryWildcard, $queryWildcard);
			
				$stmt->execute();
				$stmt->store_result();
				$stmt->bind_result($GID, $Title, $Thumbnail, $SmallThumbnail);
			
				$JSONResponse = array();
			
				$i = 0;
				while($stmt->fetch()){
					$JSONResponse[$i]['GID'] = $GID;
					$JSONResponse[$i]['Title'] = $Title;
					$JSONResponse[$i]['Thumbnail'] = $Thumbnail;
					$JSONResponse[$i]['SmallThumbnail'] = $SmallThumbnail;	
					$i++;
				}
			}else{
				$queryResult = mysqli_query($conn, $sql);
				
				$JSONResponse = array();
			
				$i = 0;
				while($row = mysqli_fetch_assoc($queryResult)){
					$JSONResponse[$i]['GID'] = $row['GID'];
					$JSONResponse[$i]['Title'] = $row['Title'];
					$JSONResponse[$i]['Thumbnail'] = $row['Thumbnail'];
					$JSONResponse[$i]['SmallThumbnail'] = $row['SmallThumbnail'];	
					$i++;
				}
			}
			
			$result->success = "true";
			$result->JSON = $JSONResponse;
			?>