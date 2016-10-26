package com.iot.source;


import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class SheetUtils {
	public SheetUtils() {
	  }
	//Delete Sheet
	public static void removeSheetByName(XSSFWorkbook targetFile,String sheetName) { 
        try { 
        	//delete Sheet
        	XSSFSheet sheet = targetFile.getSheet(sheetName);
        	targetFile.removeSheetAt(targetFile.getSheetIndex(sheet)); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 

	}
}
