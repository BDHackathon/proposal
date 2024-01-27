package org.zerock.data_proj.South_Sea;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.*;
import java.io.FileInputStream;
import java.util.Iterator;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class FileToDatabase {

    public static void main(String[] args) {
        String excelFolder = "/Users/admin/Desktop/공모전_데이터/South_Sea"; // 폴더 경로
        try {
            // Establish database connection
            Connection connection = getConnection();

            // Process all Excel files in the folder
            processAllExcelFiles(excelFolder, connection);

            // Close database connection
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processAllExcelFiles(String folderPath, Connection connection) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".xlsx")) {
                    processExcelFile(file, connection);
                }
            }
        }
    }

    private static void processExcelFile(File file, Connection connection) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Insert data into the database
            insertData(connection, sheet);

            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*public static void processExcelFile(FileInputStream inputStream, String url, String user, String password) {
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
    }*/

    /*private static Connection getConnection(String url, String user, String password) throws SQLException {
        //Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }*/

    private static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/trash";
        String user = "root";
        String password = "password";

        return DriverManager.getConnection(url, user, password);
    }


    public String getDatabaseUrl() {
        return "jdbc:oracle:thin:@localhost:1521:XE";
    }

    private static String getStringCellValue(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else {
            return cell.getStringCellValue();
        }
    }


    private static void insertData(Connection connection, Sheet sheet) throws SQLException {
        String sql = "INSERT INTO South_Sea (INVS_YR, INVS_DSRT, INVS_AREA_NM, DNF_SRC_NM, QTMT_NM, IEM_NM, IEM_CNT, METER_PER_IEM_CNT, INVS_YMD, QTMT_CD, IEM_CD, ADM_ZN_NM, CST_NM, STR_LA, STR_LO, END_LA, END_LO) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                preparedStatement.setString(1, getStringCellValue(row.getCell(0))); // INVS_YR
                preparedStatement.setString(2, getStringCellValue(row.getCell(1))); // INVS_DSRT
                preparedStatement.setString(3, getStringCellValue(row.getCell(2))); // INVS_AREA_NM
                preparedStatement.setString(4, getStringCellValue(row.getCell(3))); // DNF_SRC_NM
                preparedStatement.setString(5, getStringCellValue(row.getCell(4))); // QTMT_NM
                preparedStatement.setString(6, getStringCellValue(row.getCell(5))); // IEM_NM
                preparedStatement.setInt(7, (int) row.getCell(6).getNumericCellValue()); // IEM_CNT
                preparedStatement.setDouble(8, row.getCell(7).getNumericCellValue()); // METER_PER_IEM_CNT
                preparedStatement.setString(9, getStringCellValue(row.getCell(8))); // ADM_ZN_NM
                preparedStatement.setString(10, getStringCellValue(row.getCell(9))); // QTMT_CD
                preparedStatement.setString(11, getStringCellValue(row.getCell(10))); // IEM_CD
                preparedStatement.setString(12, getStringCellValue(row.getCell(11))); // INVS_YMD
                preparedStatement.setString(13, getStringCellValue(row.getCell(12))); // CST_NM
                preparedStatement.setDouble(14, row.getCell(13).getNumericCellValue()); // STR_LA
                preparedStatement.setDouble(15, row.getCell(14).getNumericCellValue()); // STR_LO
                preparedStatement.setDouble(16, row.getCell(15).getNumericCellValue()); // END_LA
                preparedStatement.setDouble(17, row.getCell(16).getNumericCellValue()); // END_LO

                preparedStatement.executeUpdate();
            }

        }
    }


    private static void retrieveData(Connection connection) throws SQLException {
        String sql = "SELECT * FROM South_Sea";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {


            while (resultSet.next()) {
                String invsYr = resultSet.getString("INVS_YR");
                String invsDsrt = resultSet.getString("INVS_DSRT");
                String invsAreaNm = resultSet.getString("INVS_AREA_NM");
                // (다른 컬럼들에 대한 변수 설정)

                System.out.println("INVS_YR: " + invsYr + ", INVS_DSRT: " + invsDsrt + ", INVS_AREA_NM: " + invsAreaNm + ", ...");
            }
        }
    }

}