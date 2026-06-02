package com.durian.manage.system.common.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 请填写类的描述
 *
 * @author lizhi597
 * @date 2020-12-16 16:52
 */
public class DateUtils {
    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static void main(String[] args) {
        String inputFilePath = "/Users/maven12/Downloads/tfclunwen.pdf";
        String outputFilePath = "/Users/maven12/Downloads/tfclunwen.txt";
        try {
            PDDocument document = PDDocument.load(new File(inputFilePath));
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                // Write the extracted text to a file
                FileWriter writer = new FileWriter(outputFilePath);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write(text);
                bufferedWriter.close();
                System.out.println("Text extracted successfully and written to " + outputFilePath);
            document.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat yyyymmdd = new SimpleDateFormat(PATTERN);
        return yyyymmdd.format(date);
    }


    public static String dateToString(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat yyyymmdd = new SimpleDateFormat(pattern);
        return yyyymmdd.format(date);
    }

    public static Date formatStringToDate(String date, String sdf) {
        try {
            return new SimpleDateFormat(sdf).parse(date);
        } catch (ParseException var3) {
            return null;
        }
    }



}
