package com.iot.source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class createDataBytes {
	
	static String file ="src/test/resources/responseData.dat";
	
	public static byte[] requestBytes(String inputString)
	   {
		   inputString = inputString.replace(" ", "");
		   char[] sc = inputString.toCharArray();
			//System.out.println("sc length: " + sc.length/2);
	       byte[] ba = new byte[sc.length / 2];
	       for (int i = 0; i < ba.length; i++) {
	           int nibble0 = Character.digit(sc[i * 2], 16);
	           int nibble1 = Character.digit(sc[i * 2 + 1], 16);
	           if (nibble0 == -1 || nibble1 == -1){
	               throw new IllegalArgumentException(
	               "Hex-encoded binary string contains an invalid hex digit in '"+sc[i * 2]+sc[i * 2 + 1]+"'");
	           }
	           ba[i] = (byte) ((nibble0 << 4) | (nibble1));
	       }
	       //System.out.println(Arrays.toString(ba));
			byte[] heartbeat = new byte[sc.length/2 + 1];
			byte checksum = ba[2];
			heartbeat[0] = ba[0];
			heartbeat[1] = ba[1];
			heartbeat[2] = ba[2];
			for(int i=3; i<ba.length; i++)
			{	
				checksum = (byte) (checksum^ba[i]);
				//System.out.println(checksum);
				heartbeat[i] = ba[i];
			}

			heartbeat[ba.length]=(byte)(checksum & 0xff);
			
			return heartbeat;
	   }
	   public static String bytesToStringFunc(byte[] b)
	   {   
		   StringBuffer buffer = new StringBuffer();
		   for (int i = 0; i < b.length; ++i){
			   buffer.append(toHexString1(b[i]));
		   }
		   return buffer.toString();
		   
	   }
	   public static String toHexString1(byte b){
		   String s = Integer.toHexString(b & 0xFF);
		   if (s.length() == 1){
			   return "0" + s;
			   }
		   else{
			   return s;
		   }
		   
	   }
	   public static void flush() throws IOException
	   {
		   FileWriter fw = new FileWriter(file);
		   fw.flush();
		   fw.close();
	   }
	   public static void writedata(String inputData)
	   {
		    
		   BufferedWriter out = null;   
		     try {   
		         out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));   
		         out.write(inputData);   
		     } catch (Exception e) {   
		         e.printStackTrace();   
		     } finally {   
		         try {   
		         	if(out != null){
		         		out.close();   
		             }
		         } catch (IOException e) {   
		             e.printStackTrace();   
		         }   
		     }    
	   }
	   public static boolean readlinesfromtestresultfile(String expectationfailedString)throws IOException{
	    	BufferedReader dataReader = null;
	        boolean isFailed = true;
	        try{
	        dataReader = new BufferedReader(new FileReader(file));
	        while(true)
	        {
		        String line = dataReader.readLine();
		        if (line == null) {
		            //throw new IOException(filename + ": unable to read line");
		        	break;
		        }
		        if(line.contains(expectationfailedString))
		         {
		        	isFailed = false;
		        	//System.out.println("=======###Found###======");
		         	break;
		            }
	        
	        }
	    }catch (Exception e) {   
	         e.printStackTrace();   
	     } finally {   
	         try {   
	         	if(dataReader != null){
	         		dataReader.close();   
	             }
	         } catch (IOException e) {   
	             e.printStackTrace();   
	         } 
	     }
	     return isFailed;
	   }
}
