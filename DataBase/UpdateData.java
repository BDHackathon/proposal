package org.zerock.data_proj.trash_location;

import java.sql.*;

public class UpdateData {

    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/trash";
        String username = "root";
        String password = "password";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            updateData(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateData(Connection connection) throws SQLException {
        String selectQuery =
                "SELECT trash_location.지역, South_Sea.str_la, South_Sea.str_lo, South_Sea.end_la, " +
                        "South_Sea.end_lo " +
                "FROM trash_location " +
                "JOIN South_Sea ON trash_location.지역 LIKE CONCAT('%', South_Sea.invs_area_nm, '%')";

        String updateQuery = "UPDATE trash_location " +
                "SET str_la = ?, str_lo = ?, end_la = ?, end_lo = ? " +
                "WHERE 지역 = ?";

        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
             PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {

            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                String trashLocation = resultSet.getString("지역");
                String strLa = resultSet.getString("str_la");
                String strLo = resultSet.getString("str_lo");
                String endLa = resultSet.getString("end_la");
                String endLo = resultSet.getString("end_lo");

                updateStatement.setString(1, strLa);
                updateStatement.setString(2, strLo);
                updateStatement.setString(3, endLa);
                updateStatement.setString(4, endLo);
                updateStatement.setString(5, trashLocation);

                int rowsUpdated = updateStatement.executeUpdate();
                System.out.println("Rows updated: " + rowsUpdated);
            }
        }
    }
}
