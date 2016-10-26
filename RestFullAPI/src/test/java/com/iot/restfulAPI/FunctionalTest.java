package com.iot.restfulAPI;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
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

import com.iot.source.createDataBytes;
import com.iot.source.ContentMsg;
import com.iot.source.DataReader;
import com.iot.source.DataWriter;
import com.iot.source.HTTPReqGen;
import com.iot.source.RecordHandler;
import com.iot.source.SheetUtils;
//import com.iot.source.utils.Utils;
import com.jayway.restassured.response.Response;


public class FunctionalTest implements ITest {
	private Response response;
    private DataReader myInputData;
    private DataReader myBaselineData;
    private String template;
    private String contentString;
    private int statusCode;
    private String createTime;
    private String codeMsg;
	static String logonResp = "2E 2E 06 01 00 00 00 00 01 80 36 79 39 70 00 00 01 00 08 00 00 00 00 00 00 00 00";
	static String remoteDoorOpenResp = "2E 2E 82 FE 00 00 00 00 01 80 36 79 39 70 00 00 01 00 09 12 34 56 78 99 0A 80 01 01";
	static String remoteDoorLockResp = "2E 2E 82 FE 00 00 00 00 01 80 36 79 39 70 00 00 01 00 09 12 34 56 78 99 0A 80 01 02";
	static String remoteWindowleftOpenResp = "2E 2E 82 FE 00 00 00 00 01 80 36 79 39 70 00 00 01 00 0A 12 34 56 78 99 0A 80 02 01 01";
	static String remoteWindowleftLockResp = "2E 2E 82 FE 00 00 00 00 01 80 36 79 39 70 00 00 01 00 0A 12 34 56 78 99 0A 80 02 01 02";
	static String remoteWindowRightOpenResp = "2E 2E 82 FE 00 00 00 00 01 80 36 79 39 70 00 00 01 00 0A 12 34 56 78 99 0A 80 02 02 01";
	static String remoteWindowRightLockResp = "2E 2E 82 FE 00 00 00 00 01 80 36 79 39 70 00 00 01 00 0A 12 34 56 78 99 0A 80 02 02 02";
	static String remoteWindowLeftBackOpenResp = "2E 2E 82 FE 00 00 00 00 01 80 36 79 39 70 00 00 01 00 0A 12 34 56 78 99 0A 80 02 03 01";
	static String remoteWindowLeftBackLockResp = "2E 2E 82 FE 00 00 00 00 01 80 36 79 39 70 00 00 01 00 0A 12 34 56 78 99 0A 80 02 03 02";
	static String remoteWindowRightBackOpenResp = "2E 2E 82 FE 00 00 00 00 01 80 36 79 39 70 00 00 01 00 0A 12 34 56 78 99 0A 80 02 04 01";
	static String remoteWindowRightBackLockResp = "2E 2E 82 FE 00 00 00 00 01 80 36 79 39 70 00 00 01 00 0A 12 34 56 78 99 0A 80 02 04 02";
	static String remoteWindowWithoutActionResp = "2E 2E 82 FE 00 00 00 00 01 80 36 79 39 70 00 00 01 00 0A 12 34 56 78 99 0A 80 02 00 00";
	
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
    //对服务端发起连接请求 
  	public Socket createTCP() throws UnknownHostException, IOException{
  		Socket socket=null;
  		System.out.println("=========TCP建立连接到终端=======");
  		//socket = new Socket("10.1.64.229", 9001);
  		socket = new Socket("125.69.151.39", 8856);
  		return socket;
  	}
  	
  	public static String Tcpresponse(Socket socket)
	{
		//建立TCP连接
  				String responseString = null;
				//List<byte[]> respArray = new ArrayList<byte[]>();
				int len = 0;
				byte[] b=new byte[1024];
				try {  
				//给服务端发送响应信息
				OutputStream os=socket.getOutputStream();
				List<String> array = new ArrayList<String>();
				//String logonString = "2E 2E 06 FE 00 00 00 00 06 05 04 03 02 01 00 00 01 00 2D 61 62 63 64 65 66 67 68 69 6A 6B 6C 31 32 33 34 35 36 37 38 39 30 30 30 30 30 30 30 30 31 2E 30 30 61 62 63 64 30 30 30 30 30 30 00 01";
				String logonString ="2E 2E 06 FE 00 00 00 00 01 80 36 79 39 70 00 00 01 00 2D 61 62 63 64 65 66 67 68 69 6A 6B 6C 31 32 33 34 35 36 37 38 39 30 30 30 30 30 30 30 30 31 2E 30 30 61 62 63 64 30 30 30 30 30 30 00 01";
				array.add(logonString);
				//终端需要登录
				byte[] request = createDataBytes.requestBytes(array.get(0));
				os.write(request);
				InputStream is=socket.getInputStream(); 
			    while((len=(is.read(b)))>0){
			          byte[] respBytes = new byte[len];
			          for(int j=0; j<len; j++)
			          {
			        	  respBytes[j] = b[j];
			          }
			          responseString = createDataBytes.bytesToStringFunc(respBytes);
			          //respArray.add(respBytes);
			          validation(respBytes);
			          System.out.println("TCP客户端收到的信息: "  + createDataBytes.bytesToStringFunc(respBytes));
			        }
				os.close();
				}
				catch (IOException e) { 
					// TODO Auto-generated catch block 
					e.printStackTrace();
				}
				return responseString;
	}
  	public static void validation(byte[] respBytes)
	{
		String responseString = null;
		System.out.println("第二个字节！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
	    byte[] b = new byte[1];
			//首先判断第三个字节
			switch(respBytes[2]){
			case 0x06:
				byte[] logonRespBytes = createDataBytes.requestBytes(logonResp);
				byte[] temp = new byte[logonRespBytes.length-1];
				for(int i=0; i<logonRespBytes.length-1; i++)
				{
					temp[i] = logonRespBytes[i];
				}
				//替换登录时间的内容
				for(int i=19; i<25; i++)
				{
					temp[i] = respBytes[i];
				}
				logonResp= createDataBytes.bytesToStringFunc(temp);
				logonRespBytes = createDataBytes.requestBytes(logonResp);
				logonResp= createDataBytes.bytesToStringFunc(logonRespBytes).toUpperCase();
				responseString = createDataBytes.bytesToStringFunc(respBytes).toUpperCase();
				System.out.println("登录消息期望的值：" + logonResp);
				System.out.println("登录消息实际的值：" + responseString);
				createDataBytes.writedata("登录消息期望的值：" + logonResp + "\r\n" + "登录消息实际的值：" + responseString + "\r\n");
				if(logonResp.equals(responseString))
				{
					createDataBytes.writedata("Passed!" + "\r\n");
				}else
				{
					createDataBytes.writedata("Failed!" + "\r\n");
				}
				break;
			case (byte) 0x82:
				//远程控制车门开启
				if(0x01 == respBytes[26] && 0x01 == respBytes[27])
				{
					byte[] remoteControlRespBytes = createDataBytes.requestBytes(remoteDoorOpenResp);
					byte[] temp02 = new byte[remoteControlRespBytes.length-1];
					for(int i=0; i<remoteControlRespBytes.length-1; i++)
					{
						temp02[i] = remoteControlRespBytes[i];
					}
					//替换流水号
					for(int i=15; i<17; i++)
					{
						temp02[i] = respBytes[i];
					}
					//替换时间的内容
					for(int i=19; i<25; i++)
					{
						temp02[i] = respBytes[i];
					}
					remoteDoorOpenResp= createDataBytes.bytesToStringFunc(temp02);
					logonRespBytes = createDataBytes.requestBytes(remoteDoorOpenResp);
					remoteDoorOpenResp= createDataBytes.bytesToStringFunc(logonRespBytes).toUpperCase();
					responseString = createDataBytes.bytesToStringFunc(respBytes).toUpperCase();
					System.out.println("远程控制车门开启消息期望的值：" + remoteDoorOpenResp);
					System.out.println("远程控制车门开启消息实际的值：" + responseString);
					createDataBytes.writedata("远程控制车门开启消息期望的值：" + remoteDoorOpenResp + "\r\n" + "远程控制车门开启消息实际的值：" + responseString + "\r\n");
					if(remoteDoorOpenResp.equals(responseString))
					{
						createDataBytes.writedata("Passed!" + "\r\n");
					}else
					{
						createDataBytes.writedata("Failed!" + "\r\n");
					}
					//Assert.assertEquals(remoteDoorOpenResp, responseString);
				}
			//远程控制车门关闭
			if(29==respBytes.length && 0x01 == respBytes[26] && 0x02 == respBytes[27])
			{
				byte[] remoteWindowRespBytes = createDataBytes.requestBytes(remoteDoorLockResp);
				byte[] temp03 = new byte[remoteWindowRespBytes.length-1];
				for(int i=0; i<remoteWindowRespBytes.length-1; i++)
				{
					temp03[i] = remoteWindowRespBytes[i];
				}
				//替换流水号
				for(int i=15; i<17; i++)
				{
					temp03[i] = respBytes[i];
				}
				//替换时间的内容
				for(int i=19; i<25; i++)
				{
					temp03[i] = respBytes[i];
				}
				remoteDoorLockResp= createDataBytes.bytesToStringFunc(temp03);
				logonRespBytes = createDataBytes.requestBytes(remoteDoorLockResp);
				remoteDoorLockResp= createDataBytes.bytesToStringFunc(logonRespBytes).toUpperCase();
				responseString = createDataBytes.bytesToStringFunc(respBytes).toUpperCase();
				System.out.println("远程控制车门锁住消息期望的值：" + remoteDoorLockResp);
				System.out.println("远程控制车门锁住消息实际的值：" + responseString);
				createDataBytes.writedata("远程控制车门锁住消息期望的值：" + remoteDoorLockResp + "\r\n" + "远程控制车门锁住消息实际的值：" + responseString + "\r\n");
				if(remoteDoorLockResp.equals(responseString))
				{
					createDataBytes.writedata("Passed!" + "\r\n");
				}else
				{
					createDataBytes.writedata("Failed!" + "\r\n");
				}
				//Assert.assertEquals(remoteDoorLockResp, responseString);
			}
			//远程控制左前车窗开启
			if(0x02 == respBytes[26] && 0x01 == respBytes[27] && 01 == respBytes[28] )
			{
				byte[] remoteControlRespBytes = createDataBytes.requestBytes(remoteWindowleftOpenResp);
				temp = new byte[remoteControlRespBytes.length-1];
				for(int i=0; i<remoteControlRespBytes.length-1; i++)
				{
					temp[i] = remoteControlRespBytes[i];
				}
				//替换流水号
				for(int i=15; i<17; i++)
				{
					temp[i] = respBytes[i];
				}
				//替换时间的内容
				for(int i=19; i<25; i++)
				{
					temp[i] = respBytes[i];
				}
				remoteWindowleftOpenResp= createDataBytes.bytesToStringFunc(temp);
				logonRespBytes = createDataBytes.requestBytes(remoteWindowleftOpenResp);
				remoteWindowleftOpenResp= createDataBytes.bytesToStringFunc(logonRespBytes).toUpperCase();
				responseString = createDataBytes.bytesToStringFunc(respBytes).toUpperCase();
				System.out.println("远程控制左前车窗开启消息期望的值：" + remoteWindowleftOpenResp);
				System.out.println("远程控制左前车窗开启消息实际的值：" + responseString);
				createDataBytes.writedata("远程控制左前车窗开启消息期望的值：" + remoteWindowleftOpenResp + "\r\n" + "远程控制左前车窗开启消息实际的值：" + responseString + "\r\n");
				if(remoteWindowleftOpenResp.equals(responseString))
				{
					createDataBytes.writedata("Passed!" + "\r\n");
				}else
				{
					createDataBytes.writedata("Failed!" + "\r\n");
				}
				//Assert.assertEquals(remoteWindowleftOpenResp, responseString);
			}	
			//远程控制左前车窗关闭
			if(0x02 == respBytes[26] && 0x01 == respBytes[27] && 02 == respBytes[28] )
			{
				byte[] remoteControlRespBytes = createDataBytes.requestBytes(remoteWindowleftLockResp);
				byte[] temp05 = new byte[remoteControlRespBytes.length-1];
				for(int i=0; i<remoteControlRespBytes.length-1; i++)
				{
					temp05[i] = remoteControlRespBytes[i];
				}
				//替换流水号
				for(int i=15; i<17; i++)
				{
					temp05[i] = respBytes[i];
				}
				//替换时间的内容
				for(int i=19; i<25; i++)
				{
					temp05[i] = respBytes[i];
				}
				remoteWindowleftLockResp= createDataBytes.bytesToStringFunc(temp05);
				logonRespBytes = createDataBytes.requestBytes(remoteWindowleftLockResp);
				remoteWindowleftLockResp= createDataBytes.bytesToStringFunc(logonRespBytes).toUpperCase();
				responseString = createDataBytes.bytesToStringFunc(respBytes).toUpperCase();
				System.out.println("远程控制左前车窗关闭消息期望的值：" + remoteWindowleftLockResp);
				System.out.println("远程控制左前车窗关闭消息实际的值：" + responseString);
				createDataBytes.writedata("远程控制左前车窗关闭消息期望的值：" + remoteWindowleftLockResp + "\r\n" + "远程控制左前车窗关闭消息实际的值：" + responseString + "\r\n");
				if(remoteWindowleftLockResp.equals(responseString))
				{
					createDataBytes.writedata("Passed!" + "\r\n");
				}else
				{
					createDataBytes.writedata("Failed!" + "\r\n");
				}
				//Assert.assertEquals(remoteWindowleftLockResp, responseString);
			}
			//远程控制右前车窗开启
			if(0x02 == respBytes[26] && 0x02 == respBytes[27] && 01 == respBytes[28] )
			{
				byte[] remoteControlRespBytes = createDataBytes.requestBytes(remoteWindowRightOpenResp);
				byte[] temp06 = new byte[remoteControlRespBytes.length-1];
				for(int i=0; i<remoteControlRespBytes.length-1; i++)
				{
					temp06[i] = remoteControlRespBytes[i];
				}
				//替换流水号
				for(int i=15; i<17; i++)
				{
					temp06[i] = respBytes[i];
				}
				//替换时间的内容
				for(int i=19; i<25; i++)
				{
					temp06[i] = respBytes[i];
				}
				remoteWindowRightOpenResp= createDataBytes.bytesToStringFunc(temp06);
				logonRespBytes = createDataBytes.requestBytes(remoteWindowRightOpenResp);
				remoteWindowRightOpenResp= createDataBytes.bytesToStringFunc(logonRespBytes).toUpperCase();
				responseString = createDataBytes.bytesToStringFunc(respBytes).toUpperCase();
				System.out.println("远程控制右前车窗开启消息期望的值：" + remoteWindowRightOpenResp);
				System.out.println("远程控制右前车窗开启消息实际的值：" + responseString);
				createDataBytes.writedata("远程控制右前车窗开启消息期望的值：" + remoteWindowRightOpenResp + "\r\n" + "远程控制右前车窗开启消息实际的值：" + responseString + "\r\n");
				if(remoteWindowRightOpenResp.equals(responseString))
				{
					createDataBytes.writedata("Passed!" + "\r\n");
				}else
				{
					createDataBytes.writedata("Failed!" + "\r\n");
				}
				//Assert.assertEquals(remoteWindowRightOpenResp, responseString);
			}
			//远程控制右前车窗关闭
			if(0x02 == respBytes[26] && 0x02 == respBytes[27] && 02 == respBytes[28] )
			{
				byte[] remoteControlRespBytes = createDataBytes.requestBytes(remoteWindowRightLockResp);
				byte[] temp06 = new byte[remoteControlRespBytes.length-1];
				for(int i=0; i<remoteControlRespBytes.length-1; i++)
				{
					temp06[i] = remoteControlRespBytes[i];
				}
				//替换流水号
				for(int i=15; i<17; i++)
				{
					temp06[i] = respBytes[i];
				}
				//替换时间的内容
				for(int i=19; i<25; i++)
				{
					temp06[i] = respBytes[i];
				}
				remoteWindowRightLockResp= createDataBytes.bytesToStringFunc(temp06);
				logonRespBytes = createDataBytes.requestBytes(remoteWindowRightLockResp);
				remoteWindowRightLockResp= createDataBytes.bytesToStringFunc(logonRespBytes).toUpperCase();
				responseString = createDataBytes.bytesToStringFunc(respBytes).toUpperCase();
				System.out.println("远程控制右前车窗关闭消息期望的值：" + remoteWindowRightLockResp);
				System.out.println("远程控制右前车窗关闭消息实际的值：" + responseString);
				createDataBytes.writedata("远程控制右前车窗关闭消息期望的值：" + remoteWindowRightLockResp + "\r\n" + "远程控制右前车窗关闭消息实际的值：" + responseString + "\r\n");
				if(remoteWindowRightLockResp.equals(responseString))
				{
					createDataBytes.writedata("Passed!" + "\r\n");
				}else
				{
					createDataBytes.writedata("Failed!" + "\r\n");
				}
				//Assert.assertEquals(remoteWindowRightLockResp, responseString);
			}
			//远程控制左后窗开启
			if(0x02 == respBytes[26] && 0x03 == respBytes[27] && 01 == respBytes[28] )
			{
				byte[] remoteControlRespBytes = createDataBytes.requestBytes(remoteWindowLeftBackOpenResp);
				byte[] temp06 = new byte[remoteControlRespBytes.length-1];
				for(int i=0; i<remoteControlRespBytes.length-1; i++)
				{
					temp06[i] = remoteControlRespBytes[i];
				}
				//替换流水号
				for(int i=15; i<17; i++)
				{
					temp06[i] = respBytes[i];
				}
				//替换时间的内容
				for(int i=19; i<25; i++)
				{
					temp06[i] = respBytes[i];
				}
				remoteWindowLeftBackOpenResp= createDataBytes.bytesToStringFunc(temp06);
				logonRespBytes = createDataBytes.requestBytes(remoteWindowLeftBackOpenResp);
				remoteWindowLeftBackOpenResp= createDataBytes.bytesToStringFunc(logonRespBytes).toUpperCase();
				responseString = createDataBytes.bytesToStringFunc(respBytes).toUpperCase();
				System.out.println("远程控制左后车窗开启消息期望的值：" + remoteWindowLeftBackOpenResp);
				System.out.println("远程控制左后车窗开启消息实际的值：" + responseString);
				createDataBytes.writedata("远程控制左后车窗开启消息期望的值：" + remoteWindowLeftBackOpenResp + "\r\n" + "远程控制左后车窗开启消息实际的值：" + responseString + "\r\n");
				if(remoteWindowLeftBackOpenResp.equals(responseString))
				{
					createDataBytes.writedata("Passed!" + "\r\n");
				}else
				{
					createDataBytes.writedata("Failed!" + "\r\n");
				}
				//Assert.assertEquals(remoteWindowRightLockResp, responseString);
			}
			//远程控制左后窗关闭
			if(0x02 == respBytes[26] && 0x03 == respBytes[27] && 02 == respBytes[28] )
			{
				byte[] remoteControlRespBytes = createDataBytes.requestBytes(remoteWindowLeftBackLockResp);
				byte[] temp06 = new byte[remoteControlRespBytes.length-1];
				for(int i=0; i<remoteControlRespBytes.length-1; i++)
				{
					temp06[i] = remoteControlRespBytes[i];
				}
				//替换流水号
				for(int i=15; i<17; i++)
				{
					temp06[i] = respBytes[i];
				}
				//替换时间的内容
				for(int i=19; i<25; i++)
				{
					temp06[i] = respBytes[i];
				}
				remoteWindowLeftBackLockResp= createDataBytes.bytesToStringFunc(temp06);
				logonRespBytes = createDataBytes.requestBytes(remoteWindowLeftBackLockResp);
				remoteWindowLeftBackLockResp= createDataBytes.bytesToStringFunc(logonRespBytes).toUpperCase();
				responseString = createDataBytes.bytesToStringFunc(respBytes).toUpperCase();
				System.out.println("远程控制左后车窗关闭消息期望的值：" + remoteWindowLeftBackLockResp);
				System.out.println("远程控制左后车窗关闭消息实际的值：" + responseString);
				createDataBytes.writedata("远程控制左后车窗关闭消息期望的值：" + remoteWindowLeftBackLockResp + "\r\n" + "远程控制左后车窗关闭消息实际的值：" + responseString + "\r\n");
				if(remoteWindowLeftBackLockResp.equals(responseString))
				{
					createDataBytes.writedata("Passed!" + "\r\n");
				}else
				{
					createDataBytes.writedata("Failed!" + "\r\n");
				}
				//Assert.assertEquals(remoteWindowRightLockResp, responseString);
			}
			//远程控制右后窗开启
			if(0x02 == respBytes[26] && 0x04 == respBytes[27] && 01 == respBytes[28] )
			{
				byte[] remoteControlRespBytes = createDataBytes.requestBytes(remoteWindowRightBackOpenResp);
				byte[] temp06 = new byte[remoteControlRespBytes.length-1];
				for(int i=0; i<remoteControlRespBytes.length-1; i++)
				{
					temp06[i] = remoteControlRespBytes[i];
				}
				//替换流水号
				for(int i=15; i<17; i++)
				{
					temp06[i] = respBytes[i];
				}
				//替换时间的内容
				for(int i=19; i<25; i++)
				{
					temp06[i] = respBytes[i];
				}
				remoteWindowRightBackOpenResp= createDataBytes.bytesToStringFunc(temp06);
				logonRespBytes = createDataBytes.requestBytes(remoteWindowRightBackOpenResp);
				remoteWindowRightBackOpenResp= createDataBytes.bytesToStringFunc(logonRespBytes).toUpperCase();
				responseString = createDataBytes.bytesToStringFunc(respBytes).toUpperCase();
				System.out.println("远程控制右后车窗开启消息期望的值：" + remoteWindowRightBackOpenResp);
				System.out.println("远程控制右后车窗开启消息实际的值：" + responseString);
				createDataBytes.writedata("远程控制右后车窗开启消息期望的值：" + remoteWindowRightBackOpenResp + "\r\n" + "远程控制左后车窗开启消息实际的值：" + responseString + "\r\n");
				if(remoteWindowRightBackOpenResp.equals(responseString))
				{
					createDataBytes.writedata("Passed!" + "\r\n");
				}else
				{
					createDataBytes.writedata("Failed!" + "\r\n");
				}
				//Assert.assertEquals(remoteWindowRightLockResp, responseString);
			}
			//远程控制右后窗关闭
			if(0x02 == respBytes[26] && 0x04 == respBytes[27] && 02 == respBytes[28] )
			{
				byte[] remoteControlRespBytes = createDataBytes.requestBytes(remoteWindowRightBackLockResp);
				byte[] temp06 = new byte[remoteControlRespBytes.length-1];
				for(int i=0; i<remoteControlRespBytes.length-1; i++)
				{
					temp06[i] = remoteControlRespBytes[i];
				}
				//替换流水号
				for(int i=15; i<17; i++)
				{
					temp06[i] = respBytes[i];
				}
				//替换时间的内容
				for(int i=19; i<25; i++)
				{
					temp06[i] = respBytes[i];
				}
				remoteWindowRightBackLockResp= createDataBytes.bytesToStringFunc(temp06);
				logonRespBytes = createDataBytes.requestBytes(remoteWindowRightBackLockResp);
				remoteWindowRightBackLockResp= createDataBytes.bytesToStringFunc(logonRespBytes).toUpperCase();
				responseString = createDataBytes.bytesToStringFunc(respBytes).toUpperCase();
				System.out.println("远程控制右后车窗关闭消息期望的值：" + remoteWindowRightBackLockResp);
				System.out.println("远程控制右后车窗关闭消息实际的值：" + responseString);
				createDataBytes.writedata("远程控制右后车窗关闭消息期望的值：" + remoteWindowRightBackLockResp + "\r\n" + "远程控制左后车窗关闭消息实际的值：" + responseString + "\r\n");
				if(remoteWindowRightBackLockResp.equals(responseString))
				{
					createDataBytes.writedata("Passed!" + "\r\n");
				}else
				{
					createDataBytes.writedata("Failed!" + "\r\n");
				}
				//Assert.assertEquals(remoteWindowRightLockResp, responseString);
			}
			//远程控制无动作
			if(0x02 == respBytes[26] && 0x00 == respBytes[27] && 00 == respBytes[28] )
			{
				byte[] remoteControlRespBytes = createDataBytes.requestBytes(remoteWindowWithoutActionResp);
				byte[] temp06 = new byte[remoteControlRespBytes.length-1];
				for(int i=0; i<remoteControlRespBytes.length-1; i++)
				{
					temp06[i] = remoteControlRespBytes[i];
				}
				//替换流水号
				for(int i=15; i<17; i++)
				{
					temp06[i] = respBytes[i];
				}
				//替换时间的内容
				for(int i=19; i<25; i++)
				{
					temp06[i] = respBytes[i];
				}
				remoteWindowWithoutActionResp= createDataBytes.bytesToStringFunc(temp06);
				logonRespBytes = createDataBytes.requestBytes(remoteWindowWithoutActionResp);
				remoteWindowWithoutActionResp= createDataBytes.bytesToStringFunc(logonRespBytes).toUpperCase();
				responseString = createDataBytes.bytesToStringFunc(respBytes).toUpperCase();
				System.out.println("远程控制右后车窗无动作消息期望的值：" + remoteWindowWithoutActionResp);
				System.out.println("远程控制右后车窗无动作消息实际的值：" + responseString);
				createDataBytes.writedata("远程控制右后车窗无动作消息期望的值：" + remoteWindowRightBackLockResp + "\r\n" + "远程控制右后车窗无动作消息实际的值：" + responseString + "\r\n");
				if(remoteWindowWithoutActionResp.equals(responseString))
				{
					createDataBytes.writedata("Passed!" + "\r\n");
				}else
				{
					createDataBytes.writedata("Failed!" + "\r\n");
				}
				//Assert.assertEquals(remoteWindowRightLockResp, responseString);
			}
			break;
		}
		
	}
    public class TcpThread extends Thread{
    	public void run()
    	{
    	
    		Socket socket;
			try {
				socket = createTCP();
				Tcpresponse(socket);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
    	}
    }
    @BeforeTest
    @Parameters("WorkBook")
    public void setup(String path) {
    	//cleanup /src/test/respirces/responseData
    	try {
			createDataBytes.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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

    @DataProvider(name = "WorkBookData")
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
    @Test()
    public void  OTA() throws UnknownHostException, IOException
    {
    	//启动一个tcp客户端
        TcpThread tcpthread = new TcpThread();
        tcpthread.start();
    }
    @Test()
    public void CommandsValidation() throws IOException
    {	//查询result里面是否有错误消息
    	createDataBytes.readlinesfromtestresultfile("Failed");
    }
    @Test(dataProvider = "WorkBookData", description = "ReqGenTest")
    public void api_test(String ID, String test_case) {
        System.out.println("Enter Test!!!");
        HTTPReqGen myReqGen = new HTTPReqGen();
        JSONCompareResult result = null;

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
               
              
               if(response.body().jsonPath().get("content") != null)
               {
	               //重新构造响应的明文值
	               contentString = response.body().jsonPath().get("content");     
	               statusCode = response.body().jsonPath().get("code");
	               codeMsg = response.body().jsonPath().get("code_msg");
	
	        	  //解密消息体，构成content值
		           String content = ContentMsg.AESDecode(contentString);
		           int contentLength = content.length();
	               content= content.substring(18, contentLength - 32 );
	               System.out.println("截取后的响应消息： "+ content);
	               JSONObject json = JSONObject.fromObject(response.body().jsonPath());
	               json.put("content", content);
	               json.put("code", statusCode);
	               json.put("code_msg", codeMsg);
	               //createtime字段替换成当前时间
	               JSONObject jsonpart = json.getJSONObject("content").getJSONObject("result").getJSONArray("rows").getJSONObject(0);
	               createTime = jsonpart.getString("createTime");
	               System.out.println("createTimes： " + createTime);
	               JSONObject jsonbase = JSONObject.fromObject(baseline_message);
	               JSONObject arr = jsonbase.getJSONObject("content").getJSONObject("result").getJSONArray("rows").getJSONObject(0);
	               arr.put("createTime", createTime);
	               baseline_message= jsonbase.toString();
	               System.out.println("newbaseline msg： " + baseline_message);
	               System.out.println("期望得到的响应： "+ baseline_message);
	               System.out.println("真实得到的响应：" + json.toString());
	               result = JSONCompare.compareJSON(baseline_message, json.toString(), JSONCompareMode.NON_EXTENSIBLE);
               }
               else{
            	   result = JSONCompare.compareJSON(baseline_message, response.asString(), JSONCompareMode.NON_EXTENSIBLE);
               }
               
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
            } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

