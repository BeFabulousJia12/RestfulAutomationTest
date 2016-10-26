package com.iot.restfulAPI;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.iot.source.DataReader;
import com.iot.source.DataWriter;
import com.iot.source.HTTPReqGen;
import com.iot.source.RecordHandler;
import com.iot.source.SheetUtils;
//import com.iot.source.utils.Utils;
import com.jayway.restassured.response.Response;

public class userlogon implements ITest {
	private Response response;
    private DataReader myInputData;
    private DataReader myBaselineData;
    private String template;
    public static String accessToken;

    public String getTestName() {
        return "API Test";
    }
    private String userDir = System.getProperty("user.dir"); 
    String filePath = ""; 
    String templatePath =  userDir + File.separator + "http_request_template.txt"; 
        
    XSSFWorkbook wb = null;
    XSSFSheet inputSheet = null;
    XSSFSheet baselineSheet = null;
    XSSFSheet outputSheet = null;
    XSSFSheet comparisonSheet = null;
    XSSFSheet resultSheet = null;
    
    private double totalcase = 0;
    private double failedcase = 0;
    private String startTime = "";
    private String endTime = "";

    
    @BeforeTest
    @Parameters("LogonBook")
    public void setup(String path) {
        filePath = path;
        System.out.println(templatePath);
        try {
            wb = new XSSFWorkbook(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputSheet = wb.getSheet("Input");
        baselineSheet = wb.getSheet("Baseline");

        SheetUtils.removeSheetByName(wb, "Output");
        SheetUtils.removeSheetByName(wb, "Comparison");
        SheetUtils.removeSheetByName(wb, "Result");
        outputSheet = wb.createSheet("Output");
        comparisonSheet = wb.createSheet("Comparison");
        resultSheet = wb.createSheet("Result");

        try {
        	FileInputStream fis = new FileInputStream(new File(templatePath)); 
        	template = IOUtils.toString(fis, Charset.defaultCharset()); 
        } catch (Exception e) {
            Assert.fail("Problem fetching data from input file:" + e.getMessage());
        }
        
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        startTime = sf.format(new Date());
    }

    @DataProvider(name = "LogonkData")
    protected Iterator<Object[]> testProvider(ITestContext context) {

    	List<Object[]> test_IDs = new ArrayList<Object[]>();

        myInputData = new DataReader(inputSheet, true, true, 0);

        // sort map in order so that test cases ran in a fixed order
        Map<String, RecordHandler> sortmap = new TreeMap<String,RecordHandler>(new Comparator<String>(){

			public int compare(String key1, String key2) {
				return key1.compareTo(key2);
			}
        	
        });
        
        sortmap.putAll(myInputData.get_map());
       
        
        for (Map.Entry<String, RecordHandler> entry : sortmap.entrySet()) {
            String test_ID = entry.getKey();
            String test_case = entry.getValue().get("TestCase");
            if (!test_ID.equals("") && !test_case.equals("")) {
                test_IDs.add(new Object[] { test_ID, test_case });
            }
            totalcase++;
        }
        
        myBaselineData = new DataReader(baselineSheet, true, true, 0);

    return test_IDs.iterator();
    }

    @Test(dataProvider = "LogonkData", description = "ReqGenTest")
    public void logon_test(String ID, String test_case) {
        System.out.println("Enter Test!!!");
        HTTPReqGen myReqGen = new HTTPReqGen();

        try {
            myReqGen.generate_request(template, myInputData.get_record(ID));
            response = myReqGen.perform_request();
        } catch (Exception e) {
            Assert.fail("Problem using HTTPRequestGenerator to generate response: " + e.getMessage());
        }
        
        String baseline_message = myBaselineData.get_record(ID).get("Response");

        if (response.statusCode() == 200)
            try {
               DataWriter.writeData(outputSheet, response.asString(), ID, test_case);
               accessToken = response.body().jsonPath().get("accessToken");
               System.out.println("!!!!Response accessToken: " + accessToken);
               System.out.println("%%%%%%%" + baseline_message);
               System.out.println("%%%%%%%" + response.asString());
               //替换响应中的accessToken验证值
               JSONObject json = JSONObject.fromObject(baseline_message);
               json.put("accessToken", accessToken);
               //toDo: 以后要启动的获取accesstoken的返回值
               //JSONObject jsonpart = json.getJSONObject("result").getJSONArray("rows").getJSONObject(0);
              // jsonpart.put("accessToken", accessToken);
               baseline_message= json.toString();
               System.out.println("%%" + baseline_message);
                
                JSONCompareResult result = JSONCompare.compareJSON(baseline_message, response.asString(), JSONCompareMode.NON_EXTENSIBLE);
                if (!result.passed()) {
                    DataWriter.writeData(comparisonSheet, result.getMessage(), ID, test_case);
                    DataWriter.writeData(resultSheet, "false", ID, test_case, 0);
                    Assert.fail("Assert Response and baseline messages failed" + result.getMessage());
                    failedcase++;
                } else {
                    DataWriter.writeData(resultSheet, "true", ID, test_case, 0);
                }
            } catch (JSONException e) {
                //DataWriter.writeData(comparsionSheet, "", "Problem to assert Response and baseline messages: "+e.getMessage(), ID, test_case);
                DataWriter.writeData(resultSheet, "error", ID, test_case, 0);
                failedcase++;
                Assert.fail("Problem to assert Response and baseline messages: " + e.getMessage());
            }
        else {
        	Assert.fail("Response statusCode is wrong:" + response.statusLine());
            DataWriter.writeData(outputSheet, response.statusLine(), ID, test_case);

            if (baseline_message.equals(response.statusLine())) {
                DataWriter.writeData(resultSheet, "true", ID, test_case, 0);
            } else {
                DataWriter.writeData(comparisonSheet, baseline_message + ";" + response.statusLine(), ID, test_case);
                DataWriter.writeData(resultSheet, "false", ID, test_case, 0);
                failedcase++;
            }
        }
    }
   
    @AfterTest
    public void teardown() {
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        endTime = sf.format(new Date());
        DataWriter.writeData(resultSheet, totalcase, failedcase, startTime, endTime);
        
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            wb.write(fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

}

