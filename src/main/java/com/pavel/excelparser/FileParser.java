package com.pavel.excelparser;

import java.io.File;
import java.nio.file.AccessDeniedException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileParser {
    private final static String XLSEXT = ".xls";
    private final static String XLSXEXT = ".xlsx";
    private final String directory;
    private final ExcelParser excelParser;

    public FileParser(String directory, ExcelParser excelParser) {
        this.directory = directory;
        this.excelParser = excelParser;
    }

    /**Проходится по файлам в директории и сохраняет данные из excel файлов в базу данных
     */
    public void parseDirectory() throws AccessDeniedException {
        File inputDirectoryFile = new File(directory,"To_load");
        File outputDirectoryFile = new File(directory,"Loaded");
        if (!outputDirectoryFile.canWrite()){
            throw new AccessDeniedException("Output directory must be writeable");
        }
        File [] files = inputDirectoryFile.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file: files){
            if (file.isFile()){
                String name = file.getName();
                int lastIndexOfDot = name.lastIndexOf(".");
                if (lastIndexOfDot != -1) {
                    String ext = name.substring(lastIndexOfDot).toLowerCase();
                    if (ext.equals(XLSEXT) || ext.equals(XLSXEXT)) {
                        String[] regionAndDate = parseName(name);
                        //пропускает файл, если он имеет не правильное имя
                        if (regionAndDate == null) {
                            System.out.println("file " + name + " skipped due wrong name pattern");
                            continue;
                        }
                        try {
                            boolean successLoad = parseExcelDependsOnFormat(file, regionAndDate[0], regionAndDate[1], ext);
                            if (successLoad) {
                                File loadedFile = new File(outputDirectoryFile, name);
                                if (!file.renameTo(loadedFile)) {
                                    System.out.println("File " + name +
                                            " stored in database, but failed to relocate to Loaded dir");
                                } else {
                                    System.out.println("File " + name +
                                            " stored in database");
                                }
                            } else {
                                System.out.println("File" + name + " does not contain searched table");
                            }
                        } catch (ExcelParseException e) {
                            System.out.println("Failed to parse and load data from " + regionAndDate[0] + "_"
                                    + regionAndDate[1] + " excel with exception: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    /**Выбирает правильный вариант парсинга excel
     * @param file имя файла
     * @param ter поле ter в базе данных
     * @param dat поле dat в базе данных
     * @param ext расширение excel файла
     * @return true если ексель файл загружен в бд
     * @throws ExcelParseException если произошла ошибка при парсинге или загрузке в бд
     */
    private boolean parseExcelDependsOnFormat(File file, String ter, String dat, String ext) throws ExcelParseException {
        if (ext.equals(XLSEXT)) {
            return excelParser.parseOldFormat(file, ter, dat);
        }
        else {
            return excelParser.parseNewFormat(file, ter, dat);
        }
    }

    /**Выделяет из имени файла регион и дату
     * @param fileName имя файла
     * @return массив, где первое значение часть наименования файла, соответствующая региону, второе - дата, если в имени
     * файла нет нужного паттерна возвращает null
     */
    private String[] parseName(String fileName) {
        String[] strings = new String[2];
        Pattern regionPattern = Pattern.compile("^\\d*?(?=_)");
        Matcher matcher = regionPattern.matcher(fileName);
        if (matcher.find()) {
            strings[0] = matcher.group();
        } else {
            return null;
        }
        Pattern datePattern = Pattern.compile("_([0-9]{2}).([0-9]{2}).([0-9]{4})");
        matcher = datePattern.matcher(fileName);
        if (matcher.find()) {
            strings[1] = fileName.substring(matcher.start()+1, matcher.end());
        } else {
            return null;
        }
        return strings;
    }

}
