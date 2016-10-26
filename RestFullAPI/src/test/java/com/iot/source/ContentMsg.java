package com.iot.source;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.RandomStringUtils;

//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;

public class ContentMsg {
	
	private static String cKey = "B38050E7F92A01D3";
	public static String Encrypt(byte[] sSrc, String sKey, String siv) throws Exception {
		if (sKey == null) {
		System.out.print("Key为空null");
		return null;
		}
		// 判断Key是否为16位
		if (sKey.length() != 16) {
		System.out.print("Key长度不是16位");
		return null;
		}
		byte[] raw = sKey.getBytes();
		byte[] ivbytes = new byte[16];
		for(int i=0; i<16; i++)
		{
			ivbytes[i] = raw[i];
		}
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");//"算法/模式/补码方式"
		//IvParameterSpec iv = new IvParameterSpec(siv.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
		IvParameterSpec iv = new IvParameterSpec(ivbytes);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		byte[] encrypted = cipher.doFinal(sSrc);
		//此处使用BASE64做转码功能，同时能起到2次加密的作用;
		//return new BASE64Encoder().encode(encrypted);
		return new String(Base64.getEncoder().encodeToString(encrypted));
		}
	// 解密
	public static String Decrypt(String sSrc, String sKey ,String siv) throws Exception {
			try {
				// 判断Key是否正确
				if (sKey == null) {
					System.out.print("Key为空null");
					return null;
					}
				// 判断Key是否为16位
				if (sKey.length() != 16) {
					System.out.print("Key长度不是16位");
					return null;
					}
				byte[] raw = sKey.getBytes("UTF-8");
				byte[] ivbytes = new byte[16];
				for(int i=0; i<16; i++)
				{
					ivbytes[i] = raw[i];
				}
				SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
				//IvParameterSpec iv = new IvParameterSpec(siv.getBytes());
				IvParameterSpec iv = new IvParameterSpec(ivbytes);
				cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
				//byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);//先用base64解密
				byte[] encrypted1 = Base64.getDecoder().decode(sSrc);
				try {
					byte[] original = cipher.doFinal(encrypted1);
					String originalString = new String(original);
					return originalString;
					} catch (Exception e) {
					System.out.println(e.toString());
					return null;
					}
				} catch (Exception ex) {
					System.out.println(ex.toString());
					return null;
				}
	}
	public static String AESEncrypt(String bodymsg) throws Exception {
		byte[] contentbytes;

		
		/*
		* 加密用的Key 可以用26个字母和数字组成，最好不要用保留字符，虽然不会错，至于怎么裁决，个人看情况而定
		* 此处使用AES-128-CBC加密模式，key需要为16位。
		*/
		//生成8字节的随机值
		String nonce =RandomStringUtils.randomAlphabetic(8);
		//10字节的时间戳
		String timestamp = Long.toString(System.currentTimeMillis()/1000);
		System.out.println("timestamp length: " + timestamp.length());
		
		//消息体格式
		//String msg = "{\"userUuid\":\"04dc8cea32c311e6a09bc2a6faf2cf37\",\"userName\":\"root\",\"ipStr\":\"10.1.64.223\",\"operation\":\"findEvbdpVehicleInfoPageDataByConditions\",\"pageNum\":1,\"pageSize\":10,\"parameterMap\":{},\"conditionMap\":{}}";;
		String msg = bodymsg;
		//user id
		//String userid = "ChinaUnicommChinaUnicommChinaUni";
		String userid = "3B5A64FB49409165E05AC10A3B203F5D";
		System.out.println("userid length: " + userid.getBytes().length);
		String cSrc=nonce + timestamp + msg + userid;
		System.out.println(cSrc);
		//转化成bytes
		contentbytes = cSrc.getBytes();
		System.out.println("contentbytes length: " + contentbytes.length);
		System.out.println(Arrays.toString(contentbytes));
		// 加密
		String enString = ContentMsg.Encrypt(contentbytes, cKey, cKey);
		System.out.println("加密后的字串是：" + enString);
		return enString;
		
		}
	
	public static String AESDecode(String bodymsg) throws Exception {
		
		/*
		* 加密用的Key 可以用26个字母和数字组成，最好不要用保留字符，虽然不会错，至于怎么裁决，个人看情况而定
		* 此处使用AES-128-CBC加密模式，key需要为16位。
		*/
		
		// 解密
		long lStart = System.currentTimeMillis();
		String DeString = ContentMsg.Decrypt(bodymsg, cKey, cKey);
		System.out.println("解密后的字串是：" + DeString);
		long lUseTime = System.currentTimeMillis() - lStart;
		System.out.println("解密耗时：" + lUseTime + "毫秒");
		return DeString;
		
		}
	public static void main(String[] args) throws Exception {
		byte[] contentbytes;

		
		/*
		* 加密用的Key 可以用26个字母和数字组成，最好不要用保留字符，虽然不会错，至于怎么裁决，个人看情况而定
		* 此处使用AES-128-CBC加密模式，key需要为16位。
		*/
		//生成8字节的随机值
		String nonce =RandomStringUtils.randomAlphabetic(8);
		//10字节的时间戳
		String timestamp = Long.toString(System.currentTimeMillis()/1000);
		System.out.println("timestamp: " + timestamp.length());
		
		//消息体格式
		String msg = "{\"userUuid\":\"04dc8cea32c311e6a09bc2a6faf2cf37\",\"userName\":\"root\",\"ipStr\":\"10.1.64.223\",\"operation\":\"findEvbdpVehicleInfoPageDataByConditions\",\"pageNum\":1,\"pageSize\":10,\"parameterMap\":{},\"conditionMap\":{}}";;
		//user id
		String userid = "ChinaUnicommChinaUnicommChinaUni";
		System.out.println("userid: " + userid.getBytes().length);
		String cSrc=nonce + timestamp + msg + userid;
		System.out.println(cSrc);
		//转化成bytes
		contentbytes = cSrc.getBytes();
		System.out.println("contentbytes length: " + contentbytes.length);
		System.out.println(Arrays.toString(contentbytes));
		// 加密
		long lStart = System.currentTimeMillis();
		String enString = ContentMsg.Encrypt(contentbytes, cKey, cKey);
		System.out.println("加密后的字串是：" + enString);

		long lUseTime = System.currentTimeMillis() - lStart;
		System.out.println("加密耗时：" + lUseTime + "毫秒");
		// 解密
		lStart = System.currentTimeMillis();
		String DeString = ContentMsg.Decrypt("OijSMOGLTm5d6iwvIeJffTyZGGdgQw06CeIwHvR0V/E2SCY4ynBeKEKySJkPKoe9dPewtICUfdzizotTUkLRxzdYNd4CAWtAtAH+XlvRWeaxFf0peISZPc13VQrwpmf6IV1487qu00tyeMWya0MkFNvy9NJrdC3W3e3zh7HKQqyxGQa5QXzpEOt061z6UASqjuqxoK2tNXxEvIhK+fKyIvmOeOpCelPn3PRGrkvINW4RR9hND9+AAP+Jywsr8l3BPYFxc6OqTjkuvyzlspmol60p8YI0rnB7RjRev5kgKW0VRPiBzmjI7ChkJy3ybM54kEfq/REAZYpAfG+LR2QPkA==", cKey, cKey);
		System.out.println("解密后的字串是：" + DeString);
		lUseTime = System.currentTimeMillis() - lStart;
		System.out.println("解密耗时：" + lUseTime + "毫秒");
		

	}



}
