package org.zerock.data_proj.trash;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.*;
import java.io.FileInputStream;
import java.util.Iterator;
import java.io.Reader;
import java.io.InputStreamReader;

public class ExcelToDatabase {

    public static void main(String[] args) {
        String excelFilePath = "/Users/admin/Desktop/trash/해안쓰레기_정보(2018년~2020년)_지역별.xlsx";

        try (FileInputStream file = new FileInputStream(excelFilePath)) {
            //한글 깨짐 처리
            Reader reader = new InputStreamReader(file, "UTF-8");
            // Create Workbook instance holding reference to .xlsx file
            Workbook workbook = new XSSFWorkbook(file);

            // Get first/desired sheet from the workbook
            Sheet sheet = workbook.getSheetAt(0);

            // Establish database connection
            Connection connection = getConnection();

            // Insert data into the database
            insertData(connection, sheet);

            // Close workbook and database connection

            workbook.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void processExcelFile(FileInputStream inputStream, String url, String user, String password) {
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Connection connection = getConnection(url, user, password);

            // Specify the sheet name or index
            Sheet sheet = workbook.getSheet("Sheet1");

            // Insert data into the database
            insertData(connection, sheet);

            // Retrieve and print data from the database
            retrieveData(connection);

            workbook.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static Connection getConnection(String url, String user, String password) throws SQLException {
        //Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }

    public static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/trash";
        String user = "root";
        String password = "password";

        return DriverManager.getConnection(url, user, password);
    }

    public String getExcelFilePath() {
        return "/Users/admin/Desktop/trash/해안쓰레기_정보(2018년~2020년)_지역별.xlsx";
    }

    public String getDatabaseUrl() {
        return "jdbc:oracle:thin:@localhost:1521:XE";
    }



    private static void insertData(Connection connection, Sheet sheet) throws SQLException {
        String sql = "INSERT INTO trash (지역, year_2018_count, year_2018_weight, year_2019_count, year_2019_weight, year_2020_count, year_2020_weight) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next(); // Skip header row

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                preparedStatement.setString(1, getStringCellValue(row.getCell(0))); // region
                preparedStatement.setInt(2, (int) row.getCell(1).getNumericCellValue()); // year_2018_count
                preparedStatement.setDouble(3, row.getCell(2).getNumericCellValue()); // year_2018_weight
                preparedStatement.setInt(4, (int) row.getCell(3).getNumericCellValue()); // year_2019_count
                preparedStatement.setDouble(5, row.getCell(4).getNumericCellValue()); // year_2019_weight
                preparedStatement.setInt(6, (int) row.getCell(5).getNumericCellValue()); // year_2020_count
                preparedStatement.setDouble(7, row.getCell(6).getNumericCellValue()); // year_2020_weight

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



    private static void retrieveData(Connection connection) throws SQLException {
        String sql = "SELECT * FROM trash";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String region = resultSet.getString("지역");
                int year2018Count = resultSet.getInt("year_2018_count");
                double year2018Weight = resultSet.getDouble("year_2018_weight");
                int year2019Count = resultSet.getInt("year_2019_count");
                double year2019Weight = resultSet.getDouble("year_2019_weight");
                int year2020Count = resultSet.getInt("year_2020_count");
                double year2020Weight = resultSet.getDouble("year_2020_weight");

                System.out.println("지역: " + region + ", 2018 Count: " + year2018Count + ", 2018 Weight: " + year2018Weight +
                        ", 2019 Count: " + year2019Count + ", 2019 Weight: " + year2019Weight +
                        ", 2020 Count: " + year2020Count + ", 2020 Weight: " + year2020Weight);
            }
        }
    }
    /*private static void exportToCSV(Connection connection, String csvFilePath) throws SQLException, IOException {
        String sql = "SELECT * FROM trash";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery();
             Writer writer = new OutputStreamWriter(new FileOutputStream(csvFilePath), "UTF-8");
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

            // Write CSV header
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                bufferedWriter.write(metaData.getColumnName(i));
                if (i < columnCount) {
                    bufferedWriter.write(",");
                }
            }
            bufferedWriter.newLine();

            // Write CSV data
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    bufferedWriter.write(resultSet.getString(i));
                    if (i < columnCount) {
                        bufferedWriter.write(",");
                    }
                }
                bufferedWriter.newLine();
            }
        }
    }*/


}