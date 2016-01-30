package com.visa.vdp.api.client;

import java.io.IOException;
import java.net.InetAddress;
import java.security.SignatureException;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visa.vdp.util.JSONWebUtility;
import com.visa.vdp.util.XPayTokenGeneration;

/**
 * VTS APIs provide merchants with flexible and scalable ways to securely issue tokens and enable their use in e-commerce and m-commerce purchases. 
 * VTS APIs allow a merchant to enroll a PAN, generate a token and a cryptogram for authorization at the time of processing a transaction. 
 * VTS APIs are specifically meant for merchants who already have customers card information and want to tokenize them and use in payment transactions.
 * @author VISA
 * 
 */
public class VisaTokenServiceClient extends AbstractClient {

	final static Logger logger = Logger.getLogger(VisaTokenServiceClient.class);
	
	private static String API_KEY = "{put your api key here}";
	private static String SHARED_SECRET ="{put your shared secret here}8";
	
	// Login into https://developer.digital.visa.com/self-service-ic/login
	private static final String ENCRYPTION_API_KEY ="{put your encrypted api key here}"; 
	private static final String ENCRYPTION_SHARED_SECRET ="{put your encrypted shared secret here}";
	
	  
	public static final String CARD_VERIFICATION_VALUE = "{put CVV value}"; //cvv 3 or 4 digit number <provide CVV number>
	
	public static final String EMAIL_ID = "{put emailID here}"; // 
	
	public static final String PROFILE_APP_ID  =   "{put your appId from deverloper.digital}";
			// "98765432";//
	
	public static final String WALLET_ACCOUNT_ID = RandomStringUtils.random(5, true, true); // wallet Account Id
	
	public static final String ISSUER_AUTH_CODE = "{put Issuer Auth Code}";
	
	public static final String VTS_API_RES_BASE_URI =  "https://sandbox.api.visa.com/" ;

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		
		// set the pem file
		System.setProperty("javax.net.ssl.trustStore", "C:\\Apps\\VDP\\security\\VDP.jks");
		
		String contextURI = "vts/";
		String resourcePath = "provisionedTokens";
		String apiNameURI = VTS_API_RES_BASE_URI + contextURI + resourcePath; 
		String queryString = "?apikey="+API_KEY;
		
		String pan = getCreditCardNumber();

		
		String pcndata = "{\"accountNumber\":\"" + pan
				+ "\", \"cvv2\":\""+CARD_VERIFICATION_VALUE+"\", \"billingAddress\":{ \"line1\":\"801MetroCenterBlvd\", \"city\":\"FosterCity\", "
				+ "\"state\":\"CA\", \"zip\":\"94404\", \"country\":\"US\" }, \"name\":\"BillEvans\", \"expirationDate\":{ "
				+ "\"month\":\""+getCurrentMonth()+"\", \"year\":\""+getNextYear()+"\" } }";

		logger.debug("paymentInstrument: "+pcndata);
		
		String riskData = "[{\"name\":\"encCvv2\",\"value\":\""+JSONWebUtility.createJwe(CARD_VERIFICATION_VALUE, ENCRYPTION_API_KEY, ENCRYPTION_SHARED_SECRET)+"\"}]";
		logger.debug("riskData: "+riskData);
		
		
		
		
		String provisionTokenForGivenPAN_payload = "{\"locale\":\"en_US\",\"issuerAuthCode\":\""+ISSUER_AUTH_CODE+"\",\"clientAppID\":\""+PROFILE_APP_ID+"\",\"clientWalletAccountID\":\""+WALLET_ACCOUNT_ID+"\","
				+ "\"panSource\":\"MANUALLYENTERED\",\"presentationType\":[\"ECOM\"],"
				+ "\"encPaymentInstrument\":\""+JSONWebUtility.createJwe(pcndata, ENCRYPTION_API_KEY, ENCRYPTION_SHARED_SECRET)+"\","
				+ "\"consumerEntryMode\":\"KEYENTERED\",\"protectionType\":\"CLOUD\",\"clientWalletAccountEmailAddress\":\""+EMAIL_ID+"\","
				+ "\"clientWalletAccountEmailAddressHash\":\""+Base64.encodeBase64URLSafeString(DigestUtils.sha256(EMAIL_ID))+"\",\"location\":\"123.12345678/-09878768761\","
				+ "\"ip4address\":\""+getIPAddress()+"\","
				+ "\"encRiskDataInfo\":\""+JSONWebUtility.createJwe(riskData, ENCRYPTION_API_KEY, ENCRYPTION_SHARED_SECRET)+"\"}";
		
		String panEnrollment_payload = "{\"locale\":\"en_US\",\"clientAppID\":\""+PROFILE_APP_ID+"\",\"clientWalletAccountID\":\""+WALLET_ACCOUNT_ID+"\","
				+ "\"panSource\":\"MANUALLYENTERED\","
				+ "\"encPaymentInstrument\":\""+JSONWebUtility.createJwe(pcndata, ENCRYPTION_API_KEY, ENCRYPTION_SHARED_SECRET)+"\","
				+ "\"consumerEntryMode\":\"KEYENTERED\""
				+"}";
		
		String provisiontoken_with_panEnrollmentId_payload = "{\"clientAppID\":\""+PROFILE_APP_ID+"\",\"clientWalletAccountID\":\""+WALLET_ACCOUNT_ID+"\","
				+ "\"accountType\":\"WALLET\",\"presentationType\":[\"ECOM\"],\"locationSource\": \"WIFI\","
				+ "\"protectionType\":\"CLOUD\",\"clientWalletAccountEmailAddress\":\""+EMAIL_ID+"\","
				+ "\"clientWalletAccountEmailAddressHash\":\""+Base64.encodeBase64URLSafeString(DigestUtils.sha256(EMAIL_ID))+"\",\"location\":\"123.12345678/-09878768761\","
				+ "\"ip4address\":\""+getIPAddress()+"\",\"termsAndConditions\": {\"id\": \"34324343100001\",\"date\": \""+System.currentTimeMillis()/1000+"\" },"
				+ "\"encRiskDataInfo\":\""+JSONWebUtility.createJwe(riskData, ENCRYPTION_API_KEY, ENCRYPTION_SHARED_SECRET)+"\"}";
				
				
		String paymentdata_payload = "{\"clientPaymentDataID\":\"123456789123456789123456789123456789\",\"paymentRequest\":{\"transactionType\":\"ECOM\"}}";
		String lcm_suspend_delete_payload = "{\"updateReason\":{\"reasonCode\":\"FRAUD\",\"reasonDesc\":\"Unknown transactions\"}}";
		String lcm_resume_payload = "{\"updateReason\":{\"reasonCode\":\"CUSTOMER_CONFIRMED\",\"reasonDesc\":\"Customer called\"}}";
		String xpaytoken = "";
		String crId = RandomStringUtils.random(15, true, true); 
		
		try {
			
			/*
			 * VTS Provision Token API allows a "Card-On-File" (COF) merchant to provision a token for a given PAN. The token is always bound to a known consumer
			 *  (identified by wallet provider as clientWalletAccountID), input Visa branded payment instrument, a known device (stable device id specified 
			 *  by wallet provider as clientDeviceID and an allowable presentation method (ECOM etc.)
			 */
			logger.info("================================================================================");
			logger.info("						1.Provision Token										");
			logger.info("URI(POST): https://sandbox.api.visa.com/vts/provisionedTokens?apikey={apiKey}");
			logger.info("================================================================================");
			xpaytoken = XPayTokenGeneration.generateXpaytoken(provisionTokenForGivenPAN_payload, contextURI + resourcePath, API_KEY, SHARED_SECRET);
			String response_Payload = getResponseForXPayToken(apiNameURI+queryString, xpaytoken,provisionTokenForGivenPAN_payload,"POST",crId);
			
			
			ObjectMapper mapper = getObectMapperInstance();
			Map<String,Object> enrollPanRes = mapper.readValue(response_Payload, Map.class);
			String vProvisionedTokenID = (String)enrollPanRes.get("vProvisionedTokenID");
			Map<String,Object> tokenInfo = (Map<String, Object>) enrollPanRes.get("tokenInfo");
			String encryptedToken = (String)tokenInfo.get("encTokenInfo");
			// decrypt the token;
			String decryptedValue = JSONWebUtility.decryptJwe(encryptedToken, ENCRYPTION_SHARED_SECRET);
			logger.debug("Unencrypted encTokenInfo : "+decryptedValue);
					
			/*
			 * VTS PaymentData API allows clients' wallet providers to provision a token and cryptogram for a Given Token.
			 * It is only applicable to Ecommerce track.
			 */
			logger.info("================================================================================");
			logger.info("						2.Payment Data										");
			logger.info("URI(POST): https://sandbox.api.visa.com/vts/provisionedTokens/{vProvisionedTokenID}/paymentData?apikey={apiKey}");
			logger.info("================================================================================");
			crId = RandomStringUtils.random(15, true, true); 
			resourcePath =  "provisionedTokens/"+vProvisionedTokenID+"/paymentData";
			xpaytoken = XPayTokenGeneration.generateXpaytoken(paymentdata_payload, contextURI + resourcePath, API_KEY, SHARED_SECRET);
			apiNameURI =  VTS_API_RES_BASE_URI +  contextURI + resourcePath+ "?apikey=" + API_KEY;
			response_Payload = getResponseForXPayToken(apiNameURI, xpaytoken,paymentdata_payload,"POST",crId);
			
			
			logger.info("================================================================================");
			logger.info("						3.Get Notifications										");
			logger.info("URI(GET): https://sandbox.api.visa.com/vts/provisionedTokens/{vProvisionedTokenID}?apikey={apiKey}");
			logger.info("================================================================================");
			crId = RandomStringUtils.random(15, true, true); 
				resourcePath = "provisionedTokens/"+vProvisionedTokenID;
			xpaytoken = XPayTokenGeneration.generateXpaytoken("", contextURI + resourcePath, API_KEY, SHARED_SECRET);
			apiNameURI = VTS_API_RES_BASE_URI + contextURI + resourcePath;
			response_Payload = getResponseForXPayToken(apiNameURI+queryString, xpaytoken,null,"GET",crId);
			
			
			
			/*
			 * As part of token lifecycle management, suspends a token.
			 */
			logger.info("================================================================================");
			logger.info("						4.LCM - Suspend Token									");
			logger.info("URI(PUT): https://sandbox.api.visa.com/vts/provisionedTokens/{vProvisionedTokenID}/suspend?apikey={apiKey}");
			logger.info("================================================================================");
			crId = RandomStringUtils.random(15, true, true); 
				resourcePath = "provisionedTokens/"+vProvisionedTokenID+"/suspend";
			xpaytoken = XPayTokenGeneration.generateXpaytoken(lcm_resume_payload, contextURI + resourcePath, API_KEY, SHARED_SECRET);
			apiNameURI = VTS_API_RES_BASE_URI + contextURI + resourcePath;
			response_Payload = getResponseForXPayToken(apiNameURI+queryString, xpaytoken,lcm_resume_payload,"PUT",crId);
			
			
			
			logger.info("================================================================================");
			logger.info("						3.Get Notifications										");
			logger.info("URI(GET): https://sandbox.api.visa.com/vts/provisionedTokens/{vProvisionedTokenID}?apikey={apiKey}");
			logger.info("================================================================================");
			crId = RandomStringUtils.random(15, true, true); 
				resourcePath = "provisionedTokens/"+vProvisionedTokenID;
				xpaytoken = XPayTokenGeneration.generateXpaytoken("", contextURI + resourcePath, API_KEY, SHARED_SECRET);
				apiNameURI = VTS_API_RES_BASE_URI + contextURI + resourcePath;
				response_Payload = getResponseForXPayToken(apiNameURI+queryString, xpaytoken,null,"GET",crId);
				
			
			/*
			 * As part of token lifecycle management, resumes a suspended a token.
			 */
			logger.info("================================================================================");
			logger.info("						5.LCM - Resume Token									");
			logger.info("URI(PUT): https://sandbox.api.visa.com/vts/provisionedTokens/{vProvisionedTokenID}/resume?apikey={apiKey}");
			logger.info("================================================================================");
			resourcePath = "provisionedTokens/"+vProvisionedTokenID+"/resume";
			crId = RandomStringUtils.random(15, true, true); 	
			xpaytoken = XPayTokenGeneration.generateXpaytoken(lcm_suspend_delete_payload, contextURI + resourcePath, API_KEY, SHARED_SECRET);
			apiNameURI = VTS_API_RES_BASE_URI + contextURI + resourcePath;
			response_Payload = getResponseForXPayToken(apiNameURI+queryString, xpaytoken,lcm_suspend_delete_payload,"PUT",crId);
			
			
			/*
			 * As part of token lifecycle management, deletes a token.
			 */
			logger.info("================================================================================");
			logger.info("						6.LCM - Delete  Token									");
			logger.info("URI(PUT): https://sandbox.api.visa.com/vts/provisionedTokens/{vProvisionedTokenID}/delete?apikey={apiKey}");
			logger.info("================================================================================");
			crId = RandomStringUtils.random(15, true, true); 
			resourcePath = "provisionedTokens/"+vProvisionedTokenID+"/delete";
			xpaytoken = XPayTokenGeneration.generateXpaytoken(lcm_suspend_delete_payload, contextURI + resourcePath, API_KEY, SHARED_SECRET);
			apiNameURI = VTS_API_RES_BASE_URI + contextURI + resourcePath;
			response_Payload = getResponseForXPayToken(apiNameURI+queryString, xpaytoken,lcm_suspend_delete_payload,"PUT",crId);
			
			
			logger.info("================================================================================");
			logger.info("						3.Get Notifications										");
			logger.info("URI(GET): https://sandbox.api.visa.com/vts/provisionedTokens/{vProvisionedTokenID}?apikey={apiKey}");
			logger.info("================================================================================");
				resourcePath = "provisionedTokens/"+vProvisionedTokenID;
				crId = RandomStringUtils.random(15, true, true); 
				xpaytoken = XPayTokenGeneration.generateXpaytoken("", contextURI + resourcePath, API_KEY, SHARED_SECRET);
				apiNameURI = VTS_API_RES_BASE_URI + contextURI + resourcePath;
				response_Payload = getResponseForXPayToken(apiNameURI+queryString, xpaytoken,null,"GET",crId);
				
			
			/*
			 * Enroll PAN API is to enroll a payment instrument.  Once the payment instrument is enrolled, consumer does not need to 
			 * re-enter the credentials to request future tokens. The enrolled PAN can also be used to track updates to PAN including PAN meta-data updates
			 */
			logger.info("================================================================================");
			logger.info("						7.Enroll PAN											");
			logger.info("URI(POST): https://sandbox.api.visa.com/vts/panEnrollments?apikey={apiKey}");
			logger.info("================================================================================");
			
			resourcePath = "panEnrollments";
			apiNameURI = VTS_API_RES_BASE_URI + contextURI + resourcePath;
			xpaytoken = XPayTokenGeneration.generateXpaytoken(panEnrollment_payload, contextURI + resourcePath, API_KEY, SHARED_SECRET);
			response_Payload = getResponseForXPayToken(apiNameURI+queryString, xpaytoken,panEnrollment_payload,"POST",crId);
			
			
			mapper = getObectMapperInstance();
			enrollPanRes = mapper.readValue(response_Payload, Map.class);
			vProvisionedTokenID = (String)enrollPanRes.get("vPanEnrollmentID");
			
			/*
			 * This method allows wallet provider to provision a token for a given payment instrument. 
			 * The token is always bound to a known consumer (identified by wallet provider as clientWalletAccountID), 
			 * input Visa branded payment instrument, a known device (stable device id specified by wallet provider as clientDeviceID 
			 * and an allowable presentation method (ECOM etc.).
			 * 
			 * Out of two variants of this API, the developer would use this variant 
			 * if they were keeping track of vPanEnrollmentID.  
			 * The benefit of keeping track of this ID is following: 
			 * 			Once the payment instrument is enrolled, you can use the vPanEnrollmentID to request further tokens.
			 * 			Visa Token Service will send notifications on this vPanEnrollmentId if the underlying meta-data changes 
			 * 			including PAN last-4, PAN status updates, etc.   Not all these notifications will be available immediately.
			 */
			logger.info("================================================================================");
			logger.info("						8.	Provision Token for Given PAN Enrollment ID 			");
			logger.info("URI(POST): https://sandbox.api.visa.com/vts/panEnrollments/{vPanEnrollmentID}/provisionedTokens?apikey={apiKey}");
			logger.info("================================================================================");
			resourcePath =  "panEnrollments/"+vProvisionedTokenID+"/provisionedTokens";
			crId = RandomStringUtils.random(15, true, true); 
			xpaytoken = XPayTokenGeneration.generateXpaytoken(provisiontoken_with_panEnrollmentId_payload, contextURI + resourcePath, API_KEY, SHARED_SECRET);
			apiNameURI =  VTS_API_RES_BASE_URI +  contextURI + resourcePath+ "?apikey=" + API_KEY;
			response_Payload = getResponseForXPayToken(apiNameURI, xpaytoken,provisiontoken_with_panEnrollmentId_payload,"POST",crId);
			
			
			/*
			 * Get Card MetaData API allows clients to retrieve meta data related to the PAN
			 */
			logger.info("================================================================================");
			logger.info("						9.Get Card MetaData for PAN Enrollment ID 			");
			logger.info("URI(GET): https://sandbox.api.visa.com/vts/panEnrollments/{vPanEnrollmentID}?apikey={apiKey}");
			logger.info("================================================================================");
			resourcePath =  "panEnrollments/"+vProvisionedTokenID;
			crId = RandomStringUtils.random(15, true, true); 
			xpaytoken = XPayTokenGeneration.generateXpaytoken("", contextURI + resourcePath, API_KEY, SHARED_SECRET);
			apiNameURI =  VTS_API_RES_BASE_URI +  contextURI + resourcePath+ "?apikey=" + API_KEY;
			response_Payload = getResponseForXPayToken(apiNameURI, xpaytoken,null,"GET",crId);
			
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
			
	private static String getIPAddress() throws Exception{
		InetAddress inetAddr = InetAddress.getLocalHost();
		byte[] addr = inetAddr.getAddress();
		// Convert to dot representation
		String ipAddr = "";
		for (int i = 0; i < addr.length; i++) {
			if (i > 0) {
				ipAddr += ".";
			}
			ipAddr += addr[i] & 0xFF;
		}
		return ipAddr;

	}

}
