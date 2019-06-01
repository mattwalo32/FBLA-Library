			<?php
			$numOfResults=$_GET["numOfResults"];
			
			$Fict = "Fiction";
			$FictJuv = "Juvenile Fiction";
			$NonFictJuv = "Juvenile Nonfiction";
			$FictYoungAdult = "Young Adult Fiction";
			$Drama = "Drama";
			$Humor = "Humor";
			$Mystery = "Mystery";
			$subjects = array($Fict, $FictJuv, $NonFictJuv, $FictYoungAdult, $Drama, $Humor, $Mystery);
			
			$JSONResponse = array('Popular' => array(),
			'Fiction' => array(),
			'Juvenile Fiction' => array(),
			'Juvenile Nonfiction' => array(),
			'Young Adult Fiction' => array(),
			'Drama' => array(),
			'Humor' => array(),
			'Mystery' => array());
			
			for($i=0; $i<count($subjects); $i++){
				$likeClause = '%'.$subjects[$i].'%';
				
				$stmt = $conn->prepare("SELECT GID, Title, BID, Thumbnail, SmallThumbnail FROM books WHERE subject LIKE ? GROUP BY GID ORDER BY NumberRatings LIMIT ?");
				$stmt->bind_param('si', $likeClause, $numOfResults);
				$stmt->execute();
				$stmt->store_result();
				$stmt->bind_result($GID, $Title, $BID, $Thumbnail, $SmallThumbnail);
				
				$j=0;
				while($stmt->fetch()){
					
					$JSONResponse[$subjects[$i]][$j]['Title'] = $Title;
					$JSONResponse[$subjects[$i]][$j]['BID'] = $BID;
					$JSONResponse[$subjects[$i]][$j]['Thumbnail'] = $Thumbnail;
					$JSONResponse[$subjects[$i]][$j]['SmallThumbnail'] = $SmallThumbnail;
					
					$j++;
				}
			}

			$stmt = $conn->prepare("SELECT GID, Title, Thumbnail, SmallThumbnail FROM books GROUP BY GID ORDER BY NumberRatings LIMIT ?");
			$stmt->bind_param('i', $numOfResults);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($GID, $Title, $Thumbnail, $SmallThumbnail);
				
			$i=0;
			while($stmt->fetch()){
					
					$JSONResponse['Popular'][$i]['Title'] = $Title;
					$JSONResponse['Popular'][$i]['GID'] = $GID;
					$JSONResponse['Popular'][$i]['Thumbnail'] = $Thumbnail;
					$JSONResponse['Popular'][$i]['SmallThumbnail'] = $SmallThumbnail;
					
					$i++;
				}
			
			$result->JSON = $JSONResponse;
			?>