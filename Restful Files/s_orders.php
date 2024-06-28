<?php
require_once("dbConn.php");

header("Content-Type: application/json");

try {
    $sql = "
        SELECT 
            c.c_id, c.c_name, p.o_date, p.purchase_id
        FROM 
            customer c
        INNER JOIN 
            purchase p ON c.c_id = p.c_id
        GROUP BY 
            c.c_id, c.c_name
        ORDER BY 
            c.c_id ASC";

    $stmt = $dbPDO->prepare($sql);
    $stmt->execute();

    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($result);
} catch (PDOException $e) {
    echo json_encode(["error" => "Database error: " . $e->getMessage()]);
}
?>
