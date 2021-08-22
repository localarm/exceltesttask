package com.pavel.excelparser;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

public class ParseCommand implements Runnable {

    @Option(names = {"-d"}, description = "path to directory with To_load and Loaded, for default is same directory " +
            "as jar location")
    String directory = "./";
    @Option(names = {"-u"}, description = "well defined jdbc url", required = true)
    String jdbcUrl;
    @Option(names = {"-l", "-login"}, description = "database login", required = true)
    String login;
    @Option(names = {"-p", "-password"}, description = "database password", required = true, interactive = true)
    String password;

    @Override
    public void run() {
        MyDataSource ds = new MyDataSource(jdbcUrl, login, password);
        TaxesDAO taxesDAO = new TaxesDAOPostgresImpl(ds.buildDatasource());
        ExcelParser excelParser = new ExcelParser(taxesDAO);
        FileParser fileParser = new FileParser(directory, excelParser);
        try {
            fileParser.parseDirectory();
            System.out.println("file parsing done");
        } catch (AccessDeniedException e) {
            System.out.println("Open write mode in output directory to write");
        }

    }

    public static void main(String[] args)  {
        int exitCode = new CommandLine(new ParseCommand()).execute(args);
        System.exit(exitCode);
    }
}
