<?php
	$hostAddr = "localhost";
	$dbName = "akaa";
	$dbUser = "root";
	$dbPwd = "";
	$dbPort = 3306;

	try {
		$dbPDO = new PDO("mysql:host=$hostAddr;dbname=$dbName", $dbUser, $dbPwd);
		$dbPDO->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	} catch (PDOException $e) {
		echo "Connection failed: " . $e->getMessage();
		die();
	}
?>
