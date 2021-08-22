package com.pavel.excelparser;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ExcelParser {

    private final TaxesDAO taxesDAO;
    private final DataFormatter dataFormatter = new DataFormatter();

    public ExcelParser(TaxesDAO taxesDAO) {
        this.taxesDAO = taxesDAO;
    }

    /**Находит данные и сохраняет их в базе данных для старого формата ексель .xls
     * @param file excel файл, который содержит необходимую таблицу
     * @param ter для заполнения соответствующего столбца в базе данных, указывающего на регион
     * @param dat для заполнения соответствующего столбца в базе данных, указывающего на дату
     * @return true если данные записаны в базу данных, false если нужных данных нет в файле
     * @throws ExcelParseException если возникли проблемы с чтением файла или сохранением в базу данных
     */
    public boolean parseOldFormat(File file, String ter, String dat) throws ExcelParseException {
        try (FileInputStream fio = new FileInputStream(file)) {
            Workbook workbook = new HSSFWorkbook(fio);
            return parse(workbook, ter, dat);
        } catch (IOException e) {
            throw new ExcelParseException("Failed to parse file " + ter + "_" + dat + e.getMessage(), e);
        } catch (SQLException e) {
            throw new ExcelParseException("Failed to load records of" + ter + "_" + dat + " in database store"
                    + e.getMessage(), e);
        }
    }

    /**Находит данные и сохраняет их в базе данных для старого формата ексель .xls
     * @param file excel файл, который содержит необходимую таблицу
     * @param ter для заполнения соответствующего столбца в базе данных, указывающего на регион
     * @param dat для заполнения соответствующего столбца в базе данных, указывающего на дату
     * @return true если данные записаны в базу данных, false если нужных данных нет в файле
     * @throws ExcelParseException если возникли проблемы с чтением файла или сохранением в базу данных
     */
    public boolean parseNewFormat(File file, String ter, String dat) throws ExcelParseException {
        try (FileInputStream fio = new FileInputStream(file)) {
            Workbook workbook = new XSSFWorkbook(fio);
            return parse(workbook, ter, dat);
        } catch (IOException e) {
            throw new ExcelParseException("Failed to parse file " + ter + "_" + dat + e.getMessage(), e);
        } catch (SQLException e) {
            throw new ExcelParseException("Failed to load records of" + ter + "_" + dat + " in database store"
                    + e.getMessage(), e);
        }
    }

    /**Проходит по листам екселя в поиске необходимой таблицы, после чего сохраняет список строк с нужными ячейками в бд
     * @param workbook книга экселя по которой будет происходить поиск, not null
     * @param ter для заполнения соответствующего столбца в базе данных, указывающего на регион
     * @param dat для заполнения соответствующего столбца в базе данных, указывающего на дату
     * @return true, если данные успешно записаны, false - если
     * @throws SQLException если возникли проблемы с сохранением в базу данных
     */
    private boolean parse(Workbook workbook, String ter, String dat) throws SQLException {
        workbook.getNumberOfSheets();
        Sheet sheet = null;
        boolean check = false;
        int tableStartRowIndex = 1;
        for (Sheet wbSheet : workbook) {
            for (int i = 0; i < wbSheet.getLastRowNum(); i++) {
                if (checkTableStructure(wbSheet.getRow(i))) {
                    check = true;
                    sheet = wbSheet;
                    tableStartRowIndex += i;
                    break;
                }
            }
            if (check) {
                break;
            }
        }
        //если в екселе не содержится необходимая таблица
        if (!check) {
            return false;
        }

        List<Record> records = new ArrayList<>();
        for (int i = tableStartRowIndex; i < sheet.getLastRowNum()
                    && (sheet.getRow(i) != null && sheet.getRow(i).getCell(0) != null); i++) {
            Row row = sheet.getRow(i);
            //проверка условия, что первый и третий столбец должны быть не пустыми
            if (row.getCell(0).getCellType() != CellType.BLANK
                    && row.getCell(2).getCellType() != CellType.BLANK) {
                records.add(fillRecord(row, ter, dat));
            }
        }
        if (records.isEmpty()) {
                return false;
            }
        taxesDAO.saveAll(records);
        return true;
    }

    /**Проверяет, что строка является началом нужной таблицы
     * @param row проверяемая строка
     * @return возвращает true, если данная строка совпадает с началом таблицы
     */
    private boolean checkTableStructure(Row row) {
        return row!= null &&  row.getCell(3) != null
                && dataFormatter.formatCellValue(row.getCell(3)).equals("1")
                && row.getCell(27) != null && dataFormatter.formatCellValue(row.getCell(27)).equals("25");
    }

    private Record fillRecord(Row row, String ter, String dat) {
        return new Record(getCellValue(row.getCell(0)), getCellValue(row.getCell(1)),
                getCellValue(row.getCell(2)), getCellValue(row.getCell(3)),
                getCellValue(row.getCell(4)), getCellValue(row.getCell(5)),
                getCellValue(row.getCell(6)), getCellValue(row.getCell(7)),
                getCellValue(row.getCell(8)), getCellValue(row.getCell(9)),
                getCellValue(row.getCell(10)), getCellValue(row.getCell(11)),
                getCellValue(row.getCell(12)), getCellValue(row.getCell(13)),
                getCellValue(row.getCell(14)), getCellValue(row.getCell(15)),
                getCellValue(row.getCell(16)), getCellValue(row.getCell(17)),
                getCellValue(row.getCell(18)), getCellValue(row.getCell(19)),
                getCellValue(row.getCell(20)), getCellValue(row.getCell(21)),
                getCellValue(row.getCell(22)), getCellValue(row.getCell(23)),
                getCellValue(row.getCell(24)), getCellValue(row.getCell(25)),
                getCellValue(row.getCell(26)), getCellValue(row.getCell(27)), ter, dat);
    }

    /**Возвращает значение из ячейки, заменяет значение пустой ячейки на 0
     * @param cell ячейка таблицы ексель, не null
     * @return если пустая ячейка возвращает 0, иначе значение ячейки
     */
    private String getCellValue(Cell cell){
        String cellValue = dataFormatter.formatCellValue(cell);
        return cellValue.equals("")?"0":cellValue;
    }
}
