package com.alexanderfoerster.commons;

import com.vaadin.flow.server.StreamResource;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ExcelStreamResource extends StreamResource {

    public ExcelStreamResource(XSSFWorkbook workbook, String filename) {
        super(filename, () -> {
            try {
                // Create a ByteArrayOutputStream to hold the Excel data
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                workbook.write(baos);

                // Convert the ByteArrayOutputStream to a byte array
                byte[] data = baos.toByteArray();

                // Return the byte array as an input stream
                return new ByteArrayInputStream(data);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}

