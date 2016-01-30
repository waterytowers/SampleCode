using System;
using System.Linq;
using System.Threading.Tasks;
using System.IO;
using System.Net;
using System.Text;
using System.Security.Cryptography.X509Certificates;

namespace Visa
{
    public class VisaDirectSample
    {
		private static string USER_ID = "{put your user id here}"; // Set user ID for App from VDP Portal
		private static string PASSWORD = "{put your password here}"; // Set password for App from VDP Portal

		// P12 File settings
		// Follow instructions in README for generating P12 file
		private static string P12_FILE_PATH = @"{put the path to the P12 file here}";
		private static string P12_FILE_PASSWORD = @"{put the P12 file password}";

		public static void Main (string[] args) {
			string baseUri = @"visadirect/";
			string resourcePath = @"fundstransfer/v1/pushfundstransactions/";
			string url = @"https://sandbox.api.visa.com/" + baseUri + resourcePath;

			string body = "{\"systemsTraceAuditNumber\":350420,\"retrievalReferenceNumber\":\"401010350420\",\"localTransactionDateTime\":\"2021-10-26T21:32:52\",\"acquiringBin\":409999,\"acquirerCountryCode\":\"101\",\"senderAccountNumber\":\"1234567890123456\",\"senderCountryCode\":\"USA\",\"transactionCurrencyCode\":\"USD\",\"senderName\":\"John Smith\",\"senderAddress\":\"44 Market St.\",\"senderCity\":\"San Francisco\",\"senderStateCode\":\"CA\",\"recipientName\":\"Adam Smith\",\"recipientPrimaryAccountNumber\":\"4957030420210454\",\"amount\":\"112.00\",\"businessApplicationId\":\"AA\",\"transactionId\":234234322342343,\"merchantCategoryCode\":6012,\"sourceOfFundsCode\":\"03\",\"cardAcceptor\":{\"name\":\"John Smith\",\"terminalId\":\"13655392\",\"idCode\":\"VMT200911026070\",\"address\":{\"state\":\"CA\",\"county\":\"081\",\"country\":\"USA\",\"zipCode\":\"94105\"}},\"feeProgramIndicator\":\"123\"}";

			string responseBody = makeVisaDirectAPICall (url,
				body,
				USER_ID,
				PASSWORD,
				P12_FILE_PATH,
				P12_FILE_PASSWORD
			);
		}
		
        /// <summary>
		/// Makes a POST call to Visa Direct API.
		/// </summary>
		/// <returns>Response body</returns>
		/// <param name="requestURL">Request URL eg https://sandbox.api.visa.com/visadirect/fundstransfer/v1/pushfundstransactions</param>
		/// <param name="requestBodyString">Request body string.</param>
		/// <param name="userId">userId assigned to your application by VDP. You can get these from the App details on https://vdp.visa.com </param>
		/// <param name="password">password assigned to your application by VDP. You can get these from the App details on https://vdp.visa.com </param>
		/// <param name="certificatePath">Path to p12 file.</param>
		/// <param name="certificatePassword">p12 file password.</param>
		public static string makeVisaDirectAPICall(string requestURL,
			string requestBodyString,
			string userId,
			string password,
			string certificatePath,
			string certificatePassword)
		{
			// Create the POST request object
			HttpWebRequest request = WebRequest.Create (requestURL) as HttpWebRequest;
			request.Method = "POST";
			request.ContentType = "application/json";
			request.Accept = "application/json";

			// Add headers
			string authString = userId + ":" + password;
			var authStringBytes = System.Text.Encoding.UTF8.GetBytes (authString);
			string authHeaderString = Convert.ToBase64String (authStringBytes);
			request.Headers ["Authorization"] = "Basic " + authHeaderString;

			// Load the body for the post request
			var requestStringBytes = System.Text.Encoding.UTF8.GetBytes (requestBodyString);
			request.GetRequestStream ().Write (requestStringBytes, 0, requestStringBytes.Length);

			// Add certificate
			var certificate = new X509Certificate2(certificatePath, certificatePassword);
			request.ClientCertificates.Add(certificate);

			string responseBody = "";
			try {
				// Make the call
				using (HttpWebResponse response = request.GetResponse () as HttpWebResponse) {
                    var encoding = ASCIIEncoding.ASCII;
					if (response.StatusCode != HttpStatusCode.OK) {
						throw new Exception (String.Format (
							"Server error (HTTP {0}: {1}).",
							response.StatusCode,
							response.StatusDescription));
					}
					
					Console.WriteLine("Response Headers: \n" + response.Headers.ToString());					
					
					using (var reader = new StreamReader (response.GetResponseStream(), encoding)) {
						responseBody = reader.ReadToEnd ();
					}
					
					Console.WriteLine("Response Body: \n" + responseBody);			
				}
			} catch (WebException e) {
				Console.WriteLine (e.Message);
			}
			return responseBody;
		}
    }
}