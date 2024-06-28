<?php
    require_once("dbConn.php");

    header("Content-Type: application/json");

    try {
        // Get the raw DELETE data
        $deleteData = file_get_contents("php://input");
        $data = json_decode($deleteData, true);

        if (isset($data['p_name'])) {
            $p_name = $data['p_name'];

            // Delete the plant data
            $sql = "DELETE FROM plants WHERE p_name = :p_name";
            $stmt = $dbPDO->prepare($sql);
            $stmt->bindParam(':p_name', $p_name);

            if ($stmt->execute()) {
                echo json_encode(["message" => "Plant deleted successfully"]);
            } else {
                echo json_encode(["message" => "Failed to delete plant"]);
            }
        } else {
            echo json_encode(["message" => "Invalid input"]);
        }
    } catch (PDOException $e) {
        echo json_encode(["message" => "Error: " . $e->getMessage()]);
    }
?>
