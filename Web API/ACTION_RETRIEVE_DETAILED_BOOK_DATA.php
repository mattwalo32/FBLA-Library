			<?php
			$gID = $_GET["gID"];
			
			$stmt = $conn->prepare("SELECT Title, SubTitle, TextSnippet, Subject, Description, Authors, BookDetails, Thumbnail, SmallThumbnail, Copies, ISBN10, ISBN13, NumberRatings, AverageRating FROM books WHERE GID = ? LIMIT 1");
			$stmt->bind_param('s', $gID);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($Title, $SubTitle, $TextSnippet, $Subject, $Description, $Authors, $BookDetails, $Thumbnail, $SmallThumbnail, $Copies, $ISBN10, $ISBN13, $NumberRatings, $AverageRating);
			
			$JSONResponse = array();
			
			while($stmt->fetch()){
				$JSONResponse['Title'] = $Title;
				$JSONResponse['SubTitle'] = $SubTitle;
				$JSONResponse['TextSnippet'] = $TextSnippet;
				$JSONResponse['Subject'] = $Subject;
				$JSONResponse['Description'] = $Description;
				$JSONResponse['Authors'] = $Authors;
				$JSONResponse['BookDetails'] = $BookDetails;
				$JSONResponse['Thumbnail'] = $Thumbnail;
				$JSONResponse['SmallThumbnail'] = $SmallThumbnail;
				$JSONResponse['Copies'] = $Copies;
				$JSONResponse['ISBN10'] = $ISBN10;
				$JSONResponse['ISBN13'] = $ISBN13;
				$JSONResponse['NumberRatings'] = $NumberRatings;
				$JSONResponse['AverageRating'] = $AverageRating;
			}
			
			$stmt = $conn->prepare("SELECT BID, CheckoutTimestamp, ReturnTimestamp, UID FROM books WHERE GID = ?");
			$stmt->bind_param('s', $gID);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($BID, $CheckoutTimestamp, $ReturnTimestamp, $UID);
			
			$JSONResponse['CopyDetails'] = array();
			$numberOut = 0;
			$i=0;
			while($stmt->fetch()){
				$JSONResponse['CopyDetails'][$i]['BID'] = $BID;
				$JSONResponse['CopyDetails'][$i]['CheckoutTimestamp'] = $CheckoutTimestamp;
				$JSONResponse['CopyDetails'][$i]['ReturnTimestamp'] = $ReturnTimestamp;
				if($UID != NULL){
					$numberOut++;
				}
				$i++;
			}
			$JSONResponse['AvailableCopies'] = $JSONResponse['Copies'] - $numberOut;
			
			$stmt = $conn->prepare("SELECT CID, UID, Rating, Comment, Timestamp FROM reviews WHERE GID = ?");
			$stmt->bind_param('s', $gID);
			$stmt->execute();
			$stmt->store_result($CID, $UID, $Rating, $Comment, $Timestamp);
			$stmt->bind_result();
			
			$JSONResponse['Comments'] = array();
			$i=0;
			while($stmt->fetch()){
				$JSONResponse['Comments'][$i]['CID'] = $CID;
				$JSONResponse['Comments'][$i]['UID'] = $UID;
				$JSONResponse['Comments'][$i]['Rating'] = $Rating;
				$JSONResponse['Comments'][$i]['Comment'] = $Comment;
				$JSONResponse['Comments'][$i]['Timestamp'] = $Timestamp;
				$i++;
			}
			
			$result->success = 0;
			$result->JSON = $JSONResponse;
			?>