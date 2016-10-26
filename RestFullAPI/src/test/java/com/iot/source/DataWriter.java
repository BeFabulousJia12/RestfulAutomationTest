package com.iot.source;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;


public class DataWriter {

	public static void writeSheet(XSSFRow row, String... data){ 
		 		for(int i=0;i<data.length;i++){ 
		 			row.createCell(i).setCellValue(data[i]); 
		 		} 
		 	} 

		//Actual Output Sheet and Comparison Sheet
		public static void writeData(XSSFSheet targetFile,String result, String id, String tsname) { 
	        try { 
	        	//write actual response message to the output and Comparison sheet
	        	int lastNum = targetFile.getLastRowNum(); 
	        	System.out.println("lastNum:"+lastNum); 
	        	if(0 == lastNum){ 
	        		writeSheet(targetFile.createRow(lastNum),"ID","TestCase","Details:"); 
	        		lastNum ++; 
	        		} 
	        	writeSheet(targetFile.createRow(lastNum),id,tsname,result); 

	        } catch (Exception e) { 
	            e.printStackTrace(); 
	        } 
		}
	//Add Test Result: Passed or Failed into Result sheet
		public static void writeData(XSSFSheet targetFile,String passedorfail, String id, String tsname, int i) { 
	        try { 
	        	//write Passed/Failed status into the Result sheet
	        	int lastNum = targetFile.getLastRowNum(); 
	        	if(0 == lastNum){ 
	        		writeSheet(targetFile.createRow(lastNum),"ID","TestCase","TestResult"); 
	        		lastNum ++; 
	        		} 
	        	writeSheet(targetFile.createRow(lastNum),id,tsname,passedorfail); 
	        } catch (Exception e) { 
	            e.printStackTrace(); 
	        } 
		}
//	//If Response Statuscode is not 200, Add actual and expectation response into comparison sheet
//				public static void writeData(XSSFSheet targetFile, String baselinemessage, String statusline, String id, String tsname) { 
//			        try { 
//			        	//write Add actual and expectation to the comparsionSheet sheet
//			        } catch (Exception e) { 
//			            e.printStackTrace(); 
//			        } 
//				}
	//Add Passed proportion into comparison sheet
				public static void writeData(XSSFSheet targetFile, double totalcase, double failedcase, String startTime, String endTime) { 
			        try { 
			        	int lastNum = targetFile.getLastRowNum();
			        	writeSheet(targetFile.createRow(lastNum),String.valueOf(totalcase),String.valueOf(failedcase),startTime,endTime);
			        } catch (Exception e) { 
			            e.printStackTrace(); 
			        } 
				}
}
