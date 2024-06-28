<?php
require_once("dbConn.php");

header("Content-Type: application/json");

try {
    // Get the raw POST data
    $postData = file_get_contents("php://input");
    $data = json_decode($postData, true);

    // Validate incoming data
    if (isset($data['c_id']) && isset($data['purchase_id'])) {
        $c_id = $data['c_id'];
        $purchase_id = $data['purchase_id'];

        // Update status to "Paid" in the database
        $sql = "UPDATE purchase SET status = 'Paid' WHERE c_id = :c_id AND purchase_id = :purchase_id";
        $stmt = $dbPDO->prepare($sql);
        $stmt->bindParam(':c_id', $c_id, PDO::PARAM_INT);
        $stmt->bindParam(':purchase_id', $purchase_id, PDO::PARAM_INT);
        $stmt->execute();

        echo json_encode(["message" => "Payment status updated successfully"]);
    } else {
        echo json_encode(["message" => "Invalid input"]);
    }
} catch (PDOException $e) {
    echo json_encode(["message" => "Error: " . $e->getMessage()]);
}
?>
