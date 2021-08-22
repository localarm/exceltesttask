package com.pavel.excelparser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTests {

    @Test
    public void dateRegexTestSuccess() {
        String date = "_11.22.2222";
        Pattern datePattern = Pattern.compile("_([0-9]{2}).([0-9]{2}).([0-9]{4})");
        Matcher matcher = datePattern.matcher(date);
        assertTrue(matcher.find());
    }

    @Test
    public void dateRegexTestSuccess2() {
        String date = "16_11.22.2222.xls";
        Pattern datePattern = Pattern.compile("_([0-9]{2}).([0-9]{2}).([0-9]{4})");
        Matcher matcher = datePattern.matcher(date);
        assertTrue(matcher.find());
    }
}
