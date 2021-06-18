package com.ap.menabev.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
//import org.apache.commons.codec.binary.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;

public class ServiceUtil {

	private static final Logger logger = LoggerFactory.getLogger(ServiceUtil.class);

	public static final String SAVE_DIRECTORY = "D:\\emailreader";

	public static Double stringToDouble(String in) {
		if (in.matches("-?\\d+(\\.\\d+)?"))
			return new Double(in);
		else {
			throw new NumberFormatException();
		}
	}

	public static boolean checkString(String s) {
		if (s == null || s.equals("") || s.trim().isEmpty() || s.matches("") || s.equals(null)) {
			return true;
		}
		return false;
	}
	public static String getFormattedDateinString(String format){
		String formattedDate =  null;
		Date date = new Date();
		SimpleDateFormat formatter =null;
		try {
			formatter = new SimpleDateFormat(format);
			formattedDate = formatter.format(date);
		} catch (Exception e) {
			// TODO: handle exception
			formatter = new SimpleDateFormat("yyyy-MM-dd");
			formattedDate = formatter.format(date);
			e.printStackTrace();
		}
		return formattedDate;
	}
	

	// public static ResponseDto updateWorkflowTaskStatus(String taskId, String
	// recipientUsers, String taskStatus,
	// WorkflowContext context,String token) {
	// try {
	// if (!ServiceUtil.checkString(taskId)) {
	// HttpClient client = HttpClientBuilder.create().build();
	//
	//
	// logger.error("Token Came : " + token);
	// // Map<String, String> map = DestinationReaderUtil
	// // .getDestination(DkshConstants.WORKFLOW_CLOSE_TASK_DESTINATION);
	// String url =
	// ApplicationConstants.WORKFLOW_FETCH_USERTASKS_PO_CONFIRMATION_URL;
	//
	// // Setting Task Input
	// UpdateWorkflowTaskInputDto updateWorkflowTaskInputDto = new
	// UpdateWorkflowTaskInputDto();
	// if (!ServiceUtil.checkString(recipientUsers)) {
	// updateWorkflowTaskInputDto.setRecipientUsers(recipientUsers);
	// }
	// if (!ServiceUtil.checkString(taskStatus)) {
	// updateWorkflowTaskInputDto.setStatus(taskStatus);
	// }
	// if (context != null) {
	// updateWorkflowTaskInputDto.setContext(context);
	// }
	// ObjectMapper objectMapper = new ObjectMapper();
	// String payload =
	// objectMapper.writeValueAsString(updateWorkflowTaskInputDto);
	// logger.error(payload);
	//
	// HttpPatch httpPatch = new HttpPatch(url + "/" + taskId);
	// httpPatch.addHeader("Authorization", "Bearer " + token);
	// httpPatch.addHeader("Content-Type", "application/json");
	//
	// StringEntity entity = new StringEntity(payload);
	// entity.setContentType("application/json");
	// httpPatch.setEntity(entity);
	// HttpResponse response = client.execute(httpPatch);
	//
	// if (response.getStatusLine().getStatusCode() ==
	// HttpStatus.NO_CONTENT.value()) {
	//
	// return new ResponseDto(ApplicationConstants.SUCCESS,
	// ApplicationConstants.CODE_SUCCESS,
	// "Task Status Updated Successfully");
	//
	// } else {
	// return new ResponseDto(ApplicationConstants.FAILURE,
	// ApplicationConstants.CODE_FAILURE,
	// getDataFromStream(response.getEntity().getContent()));
	// }
	// } else {
	// return new ResponseDto(ApplicationConstants.FAILURE,
	// ApplicationConstants.CODE_FAILURE,
	// "Task Id field is mandatory");
	// }
	// } catch (IOException e) {
	// logger.error(e.getMessage());
	// return new ResponseDto(ApplicationConstants.FAILURE,
	// ApplicationConstants.CODE_FAILURE,
	// e.getCause().getCause().getLocalizedMessage());
	//
	// } catch (Exception e) {
	// logger.error(e.getMessage());
	// return new ResponseDto(ApplicationConstants.FAILURE,
	// ApplicationConstants.CODE_FAILURE, e.toString());
	// }
	// }
	//
	// public static String generateTokenForOauth(HttpClient client) throws
	// Exception {
	//
	// // Map<String, String> map = DestinationReaderUtil
	// // .getDestination(DkshConstants.OAUTH_TOKEN_GENERATION_FROM_CLIENT);
	//
	// HttpPost httpPost = new
	// HttpPost(ApplicationConstants.WORKFLOW_TOKEN_PO_CONFIRMATION_URL);
	// httpPost.addHeader("Content-Type", "application/json");
	//
	// // Encoding username and password
	// String auth =
	// encodeUsernameAndPassword(ApplicationConstants.WORKFLOW_TOKEN_USERNAME,
	// ApplicationConstants.WORKFLOW_TOKEN_PASSWORD);
	// httpPost.addHeader("Authorization", auth);
	//
	// try {
	// HttpResponse response = client.execute(httpPost);
	//
	// String dataFromStream =
	// getDataFromStream(response.getEntity().getContent());
	// if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
	// JSONObject json = new JSONObject(dataFromStream);
	// if (!json.isNull("access_token")) {
	// return (String) json.get("access_token");
	// }
	// }
	// return dataFromStream;
	// } catch (IOException e) {
	// logger.error(e.getMessage());
	// throw new ExecutionFault(e.getCause().getCause().getLocalizedMessage());
	// }
	//
	// }

	public static String getUserDetails(String id) {

		// IdpUserDetailsDto idpUserIdDto = new IdpUserDetailsDto();
		// StringBuilder userName = new StringBuilder();
		String base = "https://aiiha1kww.accounts.ondemand.com/service/scim/Users"; // base
																					// url
																					// of
																					// the
																					// IDP
		String username = "T000012";// user name provided by BASIS
		String password = "incTM@8543";// password --Provided by BASIS
		String path = "";
		path = "/" + id;
		String response = "";

		StringBuilder data = new StringBuilder();
		JsonObject convertedObject = null;
		try {
			URL url = new URL(base + path);
			System.out.println(base + path);
			URLConnection urlConnection = url.openConnection();
			String authString = username + ":" + password;
			String authStringEnc = new String(Base64.getEncoder().encode(authString.getBytes()));

			System.out.println("BASIC " + authStringEnc);
			urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				data.append(line);
			}
			reader.close();
			response = data.toString();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return response;
	}

	public static String getDateByEpoc(Long epoc) {
		Date date = new Date(epoc);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String formatted = format.format(date);
		return formatted;
	}

	public static Long getEpocTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:sss");
		Date dateToBeFormatted = new Date();
		formatter.format(dateToBeFormatted);
		Long epoc = dateToBeFormatted.getTime();
		return epoc;

	}

	public static String getRequiredDate(String date, int i) {
		try {
			final Calendar cal = Calendar.getInstance();
			cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
			cal.add(Calendar.DATE, i);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.format(cal.getTime());
		} catch (Exception ex) {
			ex.getMessage();
		}
		return date;
	}

	public static String convertFileToBase64(File inputFile) throws FileNotFoundException, IOException {
		try {
			byte[] encoded = Base64.getEncoder().encode(FileUtils.readFileToByteArray(inputFile));
			return new String(encoded, StandardCharsets.US_ASCII);
		} catch (Exception e) {
			e.getMessage();
		}
		return null;
	}

	public static String sendEmail(String content, String to) {
		try {
			String host = "smtp.office365.com";
			String user = "Dipanjan.Baidya@incture.com";
			String pass = "Jaiburakali@123";

			String from = "Dipanjan.Baidya@incture.com";
			String subject = "Manual OCR Review";

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "outlook.office365.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, pass);
				}
			});
			Message msg = new MimeMessage(session);
			//
			msg.setFrom(new InternetAddress(user, false));

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

			msg.setSubject(subject);
			msg.setContent(content, "text/html");
			msg.setSentDate(new Date());

			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(content, "text/html");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			msg.setContent(multipart);
			Transport.send(msg);

			return "Email send successfully";

		} catch (Exception ex) {
			return "Error";
		}
	}

	public static File convertBase64ToFile(String base64) throws FileNotFoundException, IOException {
		try {
			File file = File.createTempFile("NEW", "." + "xml"); // Format
																	// -----xml/pdf/xlsx
			byte[] data = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
			// byte[] data = base64.getBytes(StandardCharsets.UTF_8);
			try (OutputStream stream = new FileOutputStream(file)) {
				stream.write(data);
			}
			return file;
		} catch (Exception e) {
			e.getMessage();
		}
		return null;
	}

	public static File convertBase64ToPdfFile(String base64) {
		try {
			File file = File.createTempFile("NEW", "." + "pdf"); // Format----xml/pdf/xlsx
			byte[] data = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
			// byte[] data = base64.getBytes(StandardCharsets.UTF_8);
			try (OutputStream stream = new FileOutputStream(file)) {
				stream.write(data);
			}
			return file;
		} catch (Exception e) {
			e.getMessage();
		}
		return null;
	}

	public static File convertBase64ToPdfFile(String fileName, String base64) {
		try {
			File file = File.createTempFile(fileName, "." + "pdf"); // Format----xml/pdf/xlsx
			byte[] data = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
			// byte[] data = base64.getBytes(StandardCharsets.UTF_8);
			try (OutputStream stream = new FileOutputStream(file)) {
				stream.write(data);
			}
			return file;
		} catch (Exception e) {
			e.getMessage();
		}
		return null;
	}

	public static String getDataFromStream(InputStream stream) throws IOException {
		StringBuilder dataBuffer = new StringBuilder();
		BufferedReader inStream = new BufferedReader(new InputStreamReader(stream));
		String data = "";

		while ((data = inStream.readLine()) != null) {
			dataBuffer.append(data);
		}
		inStream.close();
		return dataBuffer.toString();
	}

	public static String encodeUsernameAndPassword(String username, String password) {
		String encodeUsernamePassword = username + ":" + password;
		return "Basic " + DatatypeConverter.printBase64Binary(encodeUsernamePassword.getBytes());
	}

	public static String appendLeadingCharacters(char c, int len, String val) {
		if (!isEmpty(val)) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < len - val.length(); i++) {
				sb.append(c);
			}
			sb.append(val);
			return sb.toString();
		}
		return null;

	}

	public static boolean compare(Object value1, Object value2) {
		if (value1 == null && value2 == null)
			return true;
		else if ((value1 == null && value2 != null) || (value1 != null && value2 == null))
			return false;
		else if (value1.equals(value2))
			return true;
		else
			return false;
	}

	public static boolean isEmpty(Object obj) {
		if (obj == null || obj.equals("NULL"))
			return true;
		else if (obj.toString().equals(""))
			return true;
		return false;
	}

	public static boolean isEmpty(Object[] objs) {
		if (objs == null || objs.length == 0) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(String str) {
		if (str == null || str.trim().isEmpty()) {
			return true;
		}
		return false;
	}

	public static String extractStr(Object o) {
		return o == null ? "" : o.toString();
	}

	public static boolean isEmpty(Collection<?> o) {
		if (o == null || o.isEmpty()) {
			return true;
		}
		return false;
	}

	public static Long daysBetweenStringDates(String inputString1, String inputString2) {
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

		try {
			if (!ServiceUtil.isEmpty(inputString2)) {
				Date date1 = myFormat.parse(inputString1);
				Date date2 = myFormat.parse(inputString2);
				Long diff = date2.getTime() - date1.getTime();
				return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
			} else {
				return new Long("-1");
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return new Long("-1");
		}
	}

	public static Date stringToDate(String dateString) {
		Date date1 = null;
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date1;
	}

	public static boolean isValidJson(String json) {
		try {
			new JSONObject(json);
		} catch (JSONException ex) {
			try {
				new JSONArray(json);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}

	public static String formBatchContents(Set<String> poNumbers) {
		String requestPayload = "";

		String batchContents = "";
		int i = 0;
		for (String poNum : poNumbers) {

			logger.error("poNum in formBatchContents method= " + poNum);
			batchContents += "--batch_zmybatch" + "\n" + "Content-Type: application/http" + "\n"
					+ "Content-Transfer-Encoding: binary" + "\n" + "Accept: application/json" + "\n" + "\n"
					+ "GET Header_DataSet?$filter=Purchaseorder+eq+'" + poNum
					+ "'&$expand=HTH,HTI,HTHIS,HTHT,HTSCH,HTR&$format=json HTTP/1.1" + "\n" + "\n" + "\n";
			if (i == poNumbers.size() - 1)
				batchContents += "--batch_zmybatch--";

			i++;

		}
		requestPayload = batchContents;

		return requestPayload;
	}

	public static Header[] generateXcsrfToken(HttpClient client, String url, String username, String password)
			throws IOException {

		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("x-csrf-token", "Fetch");
		httpGet.setHeader("Authorization", ServiceUtil.encodeUsernameAndPassword(username, password));
		HttpResponse httpResponse = client.execute(httpGet);

		return httpResponse.getAllHeaders();

	}

	public static File multipartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	public static void appendParamInUrl(StringBuilder url, String key, String value) {
		if (url.length() > 0) {
			url.append("&" + key + "=" + value);
		} else {
			url.append(key + "=" + value);
		}

	}

	static JsonNode convertJsonFormat(JSONObject json) {
		ObjectNode ret = JsonNodeFactory.instance.objectNode();

		@SuppressWarnings("unchecked")
		Iterator<String> iterator = json.keys();
		for (; iterator.hasNext();) {
			String key = iterator.next();
			Object value;
			try {
				value = json.get(key);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
			if (json.isNull(key))
				ret.putNull(key);
			else if (value instanceof String)
				ret.put(key, (String) value);
			else if (value instanceof Integer)
				ret.put(key, (Integer) value);
			else if (value instanceof Long)
				ret.put(key, (Long) value);
			else if (value instanceof Double)
				ret.put(key, (Double) value);
			else if (value instanceof Boolean)
				ret.put(key, (Boolean) value);
			else if (value instanceof JSONObject)
				ret.put(key, convertJsonFormat((JSONObject) value));
			else if (value instanceof JSONArray)
				ret.put(key, convertJsonFormat((JSONArray) value));
			else
				throw new RuntimeException("not prepared for converting instance of class " + value.getClass());
		}
		return ret;
	}

	static JsonNode convertJsonFormat(JSONArray json) {
		ArrayNode ret = JsonNodeFactory.instance.arrayNode();
		for (int i = 0; i < json.length(); i++) {
			Object value;
			try {
				value = json.get(i);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
			if (json.isNull(i))
				ret.addNull();
			else if (value instanceof String)
				ret.add((String) value);
			else if (value instanceof Integer)
				ret.add((Integer) value);
			else if (value instanceof Long)
				ret.add((Long) value);
			else if (value instanceof Double)
				ret.add((Double) value);
			else if (value instanceof Boolean)
				ret.add((Boolean) value);
			else if (value instanceof JSONObject)
				ret.add(convertJsonFormat((JSONObject) value));
			else if (value instanceof JSONArray)
				ret.add(convertJsonFormat((JSONArray) value));
			else
				throw new RuntimeException("not prepared for converting instance of class " + value.getClass());
		}
		return ret;
	}

	// public static WorkflowTaskUIDto fetchTasksWithFilterParam(WorkflowTaskDto
	// dto) {
	//
	// WorkflowTaskUIDto workflowTaskUIDto = new WorkflowTaskUIDto();
	// try {
	// Map<String, Integer> count = new HashMap<>();
	// HttpClient client = HttpClientBuilder.create().build();
	// String token = generateTokenForOauth(client);
	//
	// logger.error("Token Came : " + token);
	//
	// // making a genric call for getting the total record count.
	// if (!ServiceUtil.isEmpty(dto.getRecipientUsers())) {
	// StringBuilder url = new StringBuilder();
	// appendParamInUrl(url,
	// ApplicationConstants.WORKFLOW_TASK_RECIPIENT_USER_KEY,
	// String.join(",", dto.getRecipientUsers()));
	// url.insert(0,
	// ApplicationConstants.WORKFLOW_FETCH_USERTASKS_AP_PROCESS_URL + "?");
	// HttpGet httpGet = new HttpGet(url.toString());
	// httpGet.addHeader("Authorization", "Bearer " + token);
	// HttpResponse response = client.execute(httpGet);
	// if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
	// String responseFromTriggeringWF =
	// getDataFromStream(response.getEntity().getContent());
	//
	// logger.error("responseFromTriggeringWF : " + responseFromTriggeringWF);
	//
	// JSONArray json = new JSONArray(responseFromTriggeringWF);
	//
	// if (!json.isEmpty()) {
	// Map<String, Integer> count1 = new HashMap<>();
	// List<WorkflowTaskDto> workflowTaskDtoList = new ArrayList<>();
	//// List<String> returnOrderNumberList = new ArrayList<>();
	// for (int i = 0; i < json.length(); i++) {
	// JSONObject jsonObj = json.getJSONObject(i);
	// // workflowTaskDtoList.add(mapper.map(jsonObj,
	// // WorkflowTaskDto.class));
	//
	// JsonNode jsonNode = convertJsonFormat(jsonObj);
	// ObjectMapper mapper = new ObjectMapper();
	// WorkflowTaskDto workflowTaskDto = mapper.readValue(new
	// TreeTraversingParser(jsonNode),
	// WorkflowTaskDto.class);
	//
	// String status = workflowTaskDto.getStatus();
	// Integer number = count1.get(status);
	// count1.put(status, number == null ? 1 : number + 1);
	// workflowTaskDtoList.add(workflowTaskDto);
	// }
	// workflowTaskUIDto.setCount(count1);
	// workflowTaskUIDto.setRecordCount(workflowTaskDtoList.size());
	//
	// }
	// }else{
	//
	// workflowTaskUIDto.setResponse(new
	// ResponseDto(ApplicationConstants.FAILURE,
	// ApplicationConstants.CODE_FAILURE,
	// getDataFromStream(response.getEntity().getContent())));
	// return workflowTaskUIDto;
	//
	// }
	// }
	//
	// StringBuilder url = new StringBuilder();
	//
	// if (!ServiceUtil.isEmpty(dto.getRecipientUsers()))
	// appendParamInUrl(url,
	// ApplicationConstants.WORKFLOW_TASK_RECIPIENT_USER_KEY,
	// String.join(",", dto.getRecipientUsers()));
	// if (!ServiceUtil.isEmpty(dto.getStatus()))
	// appendParamInUrl(url, ApplicationConstants.WORKFLOW_TASK_STATUS_KEY,
	// dto.getStatus());
	// if (!ServiceUtil.isEmpty(dto.getSubject()))
	// appendParamInUrl(url, ApplicationConstants.WORKFLOW_TASK_SUBJECT_KEY,
	// dto.getSubject());
	// if (!ServiceUtil.isEmpty(dto.getWorkflowDefinitionId()))
	// appendParamInUrl(url,
	// ApplicationConstants.WORKFLOW_TRIGGER_AP_PROCESS_DEFINITION_ID,
	// dto.getWorkflowDefinitionId());
	// appendParamInUrl(url, ApplicationConstants.WORKFLOW_TASK_SORTING_KEY,
	// ApplicationConstants.WORKFLOW_TASK_SORTING_ON_CREATED_AT_VALUE);
	//// if (!ServiceUtil.isEmpty(pageCount) && !pageCount.equals(0)) {
	//// appendParamInUrl(url, ApplicationConstants.WORKFLOW_TASK_COUNTS_KEY,
	// pageCount.toString());
	//// Integer startIndexNum = (pageNum * pageCount);
	////
	//// if (!ServiceUtil.isEmpty(startIndexNum) && !startIndexNum.equals(0))
	//// appendParamInUrl(url, ApplicationConstants.WORKFLOW_TASK_PAGE_NUM_KEY,
	// startIndexNum.toString());
	////
	//// }
	//// if (!ServiceUtil.isEmpty(createdFrom) &&
	// !ServiceUtil.isEmpty(createdUpTo)) {
	//// appendParamInUrl(url,
	// ApplicationConstants.WORKFLOW_TASK_CREATED_FROM_KEY, createdFrom);
	//// appendParamInUrl(url,
	// ApplicationConstants.WORKFLOW_TASK_CREATED_UPTO_KEY, createdUpTo);
	//// }
	// // Starting task index number
	//
	// url.insert(0,
	// ApplicationConstants.WORKFLOW_FETCH_USERTASKS_AP_PROCESS_URL + "?");
	//
	// logger.error("url : " + url.toString());
	//
	// HttpGet httpGet = new HttpGet(url.toString());
	// httpGet.addHeader("Authorization", "Bearer " + token);
	//
	// HttpResponse response = client.execute(httpGet);
	//
	// if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
	// String responseFromTriggeringWF =
	// getDataFromStream(response.getEntity().getContent());
	//
	// logger.error("responseFromTriggeringWF : " + responseFromTriggeringWF);
	//
	// JSONArray json = new JSONArray(responseFromTriggeringWF);
	//
	// if (!json.isEmpty()) {
	//
	// List<WorkflowTaskDto> workflowTaskDtoList = new ArrayList<>();
	// List<String> returnOrderNumberList = new ArrayList<>();
	// for (int i = 0; i < json.length(); i++) {
	// JSONObject jsonObj = json.getJSONObject(i);
	// // workflowTaskDtoList.add(mapper.map(jsonObj,
	// // WorkflowTaskDto.class));
	//
	// JsonNode jsonNode = convertJsonFormat(jsonObj);
	// ObjectMapper mapper = new ObjectMapper();
	// WorkflowTaskDto workflowTaskDto = mapper.readValue(new
	// TreeTraversingParser(jsonNode),
	// WorkflowTaskDto.class);
	////
	//// String status = workflowTaskDto.getStatus();
	//// Integer number = count.get(status);
	//// count.put(status, number == null ? 1 : number + 1);
	//
	// workflowTaskDtoList.add(workflowTaskDto);
	//
	// // returnOrderNumberList.add(json.getJSONObject(i).getString("subject"));
	//
	// }
	// workflowTaskUIDto.setWorkflowTaskDto(workflowTaskDtoList);
	// workflowTaskUIDto.setResponse(new
	// ResponseDto(ApplicationConstants.SUCCESS,
	// ApplicationConstants.CODE_SUCCESS, "Tasks Fetched"));
	//// workflowTaskUIDto.setCount(count);
	//// workflowTaskUIDto.setRecordCount(workflowTaskDtoList.size());
	// return workflowTaskUIDto;
	//
	// } else {
	// workflowTaskUIDto.setResponse(new
	// ResponseDto(ApplicationConstants.FAILURE,
	// ApplicationConstants.CODE_FAILURE, "No tasks are available at a given
	// input."));
	// return workflowTaskUIDto;
	// }
	//
	// } else {
	// workflowTaskUIDto.setResponse(new
	// ResponseDto(ApplicationConstants.FAILURE,
	// ApplicationConstants.CODE_FAILURE,
	// getDataFromStream(response.getEntity().getContent())));
	// return workflowTaskUIDto;
	// }
	// } catch (IOException e) {
	// logger.error(e.getMessage());
	// workflowTaskUIDto.setResponse(new
	// ResponseDto(ApplicationConstants.FAILURE,
	// ApplicationConstants.CODE_FAILURE,
	// e.getCause().getCause().getLocalizedMessage()));
	// return workflowTaskUIDto;
	//
	// } catch (Exception e) {
	// logger.error(e.getMessage());
	// workflowTaskUIDto.setResponse(
	// new ResponseDto(ApplicationConstants.FAILURE,
	// ApplicationConstants.CODE_FAILURE, e.toString()));
	// return workflowTaskUIDto;
	// }
	//
	// }
	public static void sendmail(String emailTo, String subject, String content, File file)
			throws AddressException, MessagingException, IOException {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "outlook.office365.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(ApplicationConstants.ACCPAY_EMAIL_ID,
						ApplicationConstants.ACCPAY_EMAIL_PASSWORD);
			}
		});
		Message msg = new MimeMessage(session);
		//
		msg.setFrom(new InternetAddress(ApplicationConstants.ACCPAY_EMAIL_ID, false));

		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo));

		msg.setSubject(subject);
		msg.setContent(content, "text/html");
		msg.setSentDate(new Date());

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(content, "text/html");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		if (!isEmpty(file)) {

			MimeBodyPart attachPart = new MimeBodyPart();
			attachPart.attachFile(file);
			multipart.addBodyPart(attachPart);
		}
		msg.setContent(multipart);
		Transport.send(msg);
	}

	public static File moveAndStoreFile(MultipartFile file, String name, String path) throws IOException {
		String url = path + name;
		File fileToSave = new File(url);
		fileToSave.createNewFile();
		FileOutputStream fos = new FileOutputStream(fileToSave);
		fos.write(file.getBytes());
		fos.close();
		return fileToSave;
	}

	// to write content on a file.
	public static File writeByte(String base64String, String fileNamePath) throws IOException {
		File file = new File(fileNamePath);
		byte[] bytes = base64String.getBytes();
		byte[] decodedBytes = Base64.getDecoder().decode(new String(bytes).getBytes("UTF-8"));
		OutputStream os = new FileOutputStream(file);
		os.write(decodedBytes);
		os.close();
		return file;
	}

	// delay execution code.

	/*
	 * public void delay() { ScheduledExecutorService executorService =
	 * Executors.newSingleThreadScheduledExecutor(); // here DoSome is the class
	 * name and meth in the function to be delayed to execute. Object qwe;
	 * executorService.schedule((Runnable) new DoSome(), 30, TimeUnit.SECONDS);
	 * // after the delayed job done we have shut the ScheduledExecutorService
	 * down. executorService.shutdown(); }
	 */
	
	
}
