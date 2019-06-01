<!DOCTYPE HTML>
<html>
	<head>
	
	</head>
	<body>
	<?php
		DEFINE('ACTION_RETRIEVE_ACCOUNT_DATA', "ACTION_RETRIEVE_ACCOUNT_DATA");
		DEFINE('ACTION_RETRIEVE_BOOK_DATA', "ACTION_RETRIEVE_BOOK_DATA");
		DEFINE('ACTION_RETRIEVE_TOP_BOOKS_BY_CATEGORY', "ACTION_RETRIEVE_TOP_BOOKS_BY_CATEGORY");
		DEFINE('ACTION_RETRIEVE_DETAILED_BOOK_DATA', "ACTION_RETRIEVE_DETAILED_BOOK_DATA");
		DEFINE('ACTION_RETRIEVE_FEE_DATA', "ACTION_RETRIEVE_FEE_DATA");
		DEFINE('ACTION_CREATE_NEW_USER', "ACTION_CREATE_NEW_USER");
		DEFINE('ACTION_ADD_BOOK', "ACTION_ADD_BOOK");
		DEFINE('ACTION_ADD_REVIEW', "ACTION_ADD_REVIEW");
		DEFINE('ACTION_CHECKOUT_BOOK', "ACTION_CHECKOUT_BOOK");
		DEFINE('ACTION_RETURN_BOOK', "ACTION_RETURN_BOOK");
		DEFINE('ACTION_ADD_FEE', "ACTION_ADD_FEE");
		DEFINE('ACTION_REMOVE_FEE', "ACTION_REMOVE_FEE");
		
	
		$result = new stdClass();
		
		$action=$_GET["action"];
		switch($action){
			case ACTION_RETRIEVE_ACCOUNT_DATA:
				require("ConnFBLADBrUser.php");
				require("ACTION_RETRIEVE_ACCOUNT_DATA.php");
			break;
			case ACTION_RETRIEVE_TOP_BOOKS_BY_CATEGORY:
				require("ConnFBLADBrUser.php");
				require("ACTION_RETRIEVE_TOP_BOOKS_BY_CATEGORY.php");
			break;
			case ACTION_RETRIEVE_BOOK_DATA:
				require("ConnFBLADBrUser.php");
				require("ACTION_RETRIEVE_BOOK_DATA.php");
			break;
			case ACTION_RETRIEVE_DETAILED_BOOK_DATA:
				require("ConnFBLADBrUser.php");
				require("ACTION_RETRIEVE_DETAILED_BOOK_DATA.php");
			break;
			case ACTION_RETRIEVE_FEE_DATA:
				require("ConnFBLADBrUser.php");
				require("ACTION_RETRIEVE_FEE_DATA.php");
			break;
			case ACTION_CREATE_NEW_USER:
				require("ConnFBLADBrwUser.php");
				require("ACTION_CREATE_NEW_USER.php");
			break;
			case ACTION_ADD_BOOK:
				require("ConnFBLADBrwUser.php");
				require("ACTION_ADD_BOOK.php");
			break;
			case ACTION_ADD_REVIEW:
				require("ConnFBLADBrwUser.php");
				require("ACTION_ADD_REVIEW.php");
			break;
			case ACTION_CHECKOUT_BOOK:
				require("ConnFBLADBrwUser.php");
				require("ACTION_CHECKOUT_BOOK.php");
			break;
			case ACTION_RETURN_BOOK:
				require("ConnFBLADBrwUser.php");
				require("ACTION_RETURN_BOOK.php");
			break;
			case ACTION_ADD_FEE:
				require("ConnFBLADBrwUser.php");
				require("ACTION_ADD_FEE.php");
			break;
			case ACTION_REMOVE_FEE:
				require("ConnFBLADBrwUser.php");
				require("ACTION_REMOVE_FEE.php");
			break;
			default:
				$result->success = false;
				$result->message = "The action trying to be performed is unrecongnized";
		}
		
		
		echo(json_encode($result));
		mysqli_close($conn);
	?>
	</body>
</html>