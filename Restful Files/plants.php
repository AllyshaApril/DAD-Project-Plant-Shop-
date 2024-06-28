<?php
    require_once("dbConn.php");

    try {
        $sql = "SELECT p_id, p_name, p_stock, p_price FROM plants";
        $stmt = $dbPDO->prepare($sql);
        $stmt->execute();
        $plants = $stmt->fetchAll(PDO::FETCH_ASSOC);

        echo json_encode($plants);
    } catch(PDOException $e) {
        echo "Error: " . $e->getMessage();
    }
?>
