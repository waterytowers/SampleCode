package com.visa.vdp.api.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


public class AbstractClient {
	
	final static Logger logger = Logger.getLogger(AbstractClient.class);

	static final String[] VISA_PREFIX_LIST = new String[] { "4539","4556", "4916", "4532", "4929", "40240071", "4485", "4716", "4" };
	
	protected static void logRequestBody(String payload, String URI, String xpaytoken, String crId){
		ObjectMapper mapper = getObectMapperInstance();
		JsonNode tree;
		logger.info("URI: "+URI);
		logger.info("X-PAY-TOKEN: "+xpaytoken);
		logger.info("X-CORRELATION-ID: "+crId);
		if(!StringUtils.isEmpty(payload)) {
			try {
				tree = mapper .readTree(payload);
				logger.info("requestBody: "+mapper.writeValueAsString(tree));
			} catch (JsonProcessingException e) {
				// Ignore any Exceptions
				logger.error(e.getMessage());
			} catch (IOException e) {
				// Ignore any Exceptions
				logger.error(e.getMessage());
			}
		}
		
	}
	
	protected static void logResponseBody(String payload) {
		if(!StringUtils.isEmpty(payload)) {
			ObjectMapper mapper = getObectMapperInstance();
			JsonNode tree;
			try {
				tree = mapper .readTree(payload);
				logger.info("responseBody: "+mapper.writeValueAsString(tree));
			} catch (JsonProcessingException e) {
				// Ignore any Exceptions
				logger.error(e.getMessage());
			} catch (IOException e) {
				// Ignore any Exceptions
				logger.error(e.getMessage());
			}
		}
	}
	
	/**
	 * Get New Instance of ObjectMapper
	 * @return
	 */
	protected static ObjectMapper getObectMapperInstance() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true); // format json
		return mapper;
	}
	
	
	protected static String getCreditCardNumber() {
		String pan = credit_card_number(VISA_PREFIX_LIST, 16, 1)[0];
		for(;;) {
			if (Integer.parseInt(StringUtils.substring(pan, -2)) < 60 && modNineCheck(pan)) {
				break;
			}
			pan = credit_card_number(VISA_PREFIX_LIST, 16, 1)[0];
		}
		return pan;
	}
	
	
	/*
	 * 'prefix' is the start of the CC number as a string, any number of digits.
	 * 'length' is the length of the CC number to generate. Typically 13 or 16
	 */
	private static String completed_number(String prefix, int length) {

		String ccnumber = prefix;

		// generate digits

		while (ccnumber.length() < (length - 1)) {
			ccnumber += new Double(Math.floor(Math.random() * 10)).intValue();
		}

		// reverse number and convert to int

		String reversedCCnumberString = strrev(ccnumber);

		List<Integer> reversedCCnumberList = new Vector<Integer>();
		for (int i = 0; i < reversedCCnumberString.length(); i++) {
			reversedCCnumberList.add(new Integer(String
					.valueOf(reversedCCnumberString.charAt(i))));
		}

		// calculate sum

		int sum = 0;
		int pos = 0;

		Integer[] reversedCCnumber = reversedCCnumberList
				.toArray(new Integer[reversedCCnumberList.size()]);
		while (pos < length - 1) {

			int odd = reversedCCnumber[pos] * 2;
			if (odd > 9) {
				odd -= 9;
			}

			sum += odd;

			if (pos != (length - 2)) {
				sum += reversedCCnumber[pos + 1];
			}
			pos += 2;
		}

		// calculate check digit

		int checkdigit = new Double(
				((Math.floor(sum / 10) + 1) * 10 - sum) % 10).intValue();
		ccnumber += checkdigit;

		return ccnumber;

	}
	
	
	private static String strrev(String str) {
		if (str == null)
			return "";
		String revstr = "";
		for (int i = str.length() - 1; i >= 0; i--) {
			revstr += str.charAt(i);
		}

		return revstr;
	}
	
	
	
	// Encryption
	  /**
		 * Generate check digit for a number string.
		 * 
		 * @param numberString
		 * @param noCheckDigit
		 *            Whether check digit is present or not. True if no check Digit
		 *            is appended.
		 * @return
		 */
		public static boolean modNineCheck(String card) {
	        int[] digits = new int[card.length()];
	        for(int i =0 ; i<card.length();i++){
	               digits[i] = Integer.parseInt(card.charAt(i)+"");
	        }
	         int sum = 0;
	         int length = digits.length;
	         for (int i = 0; i < length; i++) {
	  
	             // get digits in reverse order
	             int digit = digits[length - i - 1];
	  
	             // every 2nd number multiply with 2
	             if (i % 2 == 1) {
	                 digit *= 2;
	            }
	             sum += digit > 9 ? digit - 9 : digit;
	         }
	         return sum % 9 == 0;
	     }
	
	private static String[] credit_card_number(String[] prefixList, int length,
			int howMany) {

		Stack<String> result = new Stack<String>();
		for (int i = 0; i < howMany; i++) {
			int randomArrayIndex = (int) Math.floor(Math.random()
					* prefixList.length);
			String ccnumber = prefixList[randomArrayIndex];
			result.push(completed_number(ccnumber, length));
		}

		return result.toArray(new String[result.size()]);
	
	}
	
	protected static String getResponseForXPayToken(String endpoint,
	        String xpaytoken, String payload, String method, String crId) throws IOException {
		
		 logRequestBody(payload, endpoint, xpaytoken, crId);
		HttpsURLConnection conn = null;
		OutputStream os;
		BufferedReader br = null;
		InputStream is;
		String output;
		String op = "";

		URL url1 = new URL(endpoint);
	//	getCertificate();
		
		conn = (HttpsURLConnection) url1.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod(method);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("x-request-id", "1234");
		conn.setRequestProperty("x-pay-token", xpaytoken);
		conn.setRequestProperty("X-CORRELATION-ID", crId);
		
		if (!StringUtils.isEmpty(payload)) {
			os = conn.getOutputStream();
			os.write(payload.getBytes());
			os.flush();
		}
		if (conn.getResponseCode() >= 400) {
			is = conn.getErrorStream();			
		} else {
			is = conn.getInputStream();
		}
		if (is!=null) {
			br = new BufferedReader(new InputStreamReader(is));
			while ((output = br.readLine()) != null) {
				op += output;
			}
		}
		
		// Log the response Headers
		Map<String, List<String>> map = conn.getHeaderFields();
		//for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			logger.info("Response Headers: " + map.toString());
		//}
		
		conn.disconnect();
		logResponseBody(op);
		
		return op;
	
	}
	
	protected static String getCurrentMonth(){
		SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMM" );
		String today = formatter.format( new java.util.Date() );
		return today.substring(4, 6);
	}
	
	protected static String getNextYear(){
		SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMM" );
		String today = formatter.format( new java.util.Date() );
		int nextYear =  Integer.parseInt(today.substring(0, 4)) + 1;
		return String.valueOf(nextYear);
	}

}
