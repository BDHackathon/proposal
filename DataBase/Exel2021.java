package org.zerock.data_proj.trash;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.Reader;
import java.io.InputStreamReader;

public class Exel2021 {
    public static void main(String[] args) {
        String excelFilePath = "/Users/admin/Desktop/trash/해양쓰레기_정보_2021.xlsx";

        try (FileInputStream file = new FileInputStream(excelFilePath);
             Connection connection = getConnection()) {

            Workbook workbook = WorkbookFactory.create(file, "UTF-8");
            Sheet sheet = workbook.getSheetAt(0);

            updateDatabase(connection, sheet);

            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/trash";
        String user = "root";
        String password = "password";
        return DriverManager.getConnection(url, user, password);
    }

    private static void updateDatabase(Connection connection, Sheet sheet) throws SQLException {
        String sql = "UPDATE trash SET year_2021_count = ?, year_2021_weight = ? WHERE 지역 LIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next(); // Skip header row

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String region = getStringCellValue(row.getCell(0));
                int count = (int) row.getCell(1).getNumericCellValue();
                double weight = row.getCell(2).getNumericCellValue();

                // Extract the first two characters from the region
                String regionPrefix = region.substring(0, 2);

                // Update the database with the new values for regions having the same prefix
                preparedStatement.setInt(1, count);
                preparedStatement.setDouble(2, weight);
                preparedStatement.setString(3, regionPrefix + "%");
                preparedStatement.executeUpdate();
            }
        }
    }

    private static String getStringCellValue(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        } else {
            return cell.getStringCellValue();
        }
    }
}
