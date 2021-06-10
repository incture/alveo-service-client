package com.ap.menabev.dms.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.client.util.ContentStreamUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.mapping.Array;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.ap.menabev.dms.dto.DmsGetResponseDto;
import com.ap.menabev.dms.dto.DmsResponseDto;
import com.ap.menabev.dms.service.DocumentManagementService;
import com.ap.menabev.dto.DashBoardDetailsDto;
import com.ap.menabev.dto.InvoiceBPDto;
import com.ap.menabev.dto.InvoiceHeaderDashBoardDto;
import com.ap.menabev.dto.InvoiceItemDashBoardDto;
import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.dto.NonPoTemplateItemsDto;
import com.ap.menabev.dto.ResponseDto;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

@Service
public class DocumentManagementServiceImpl implements DocumentManagementService {
	private static final Logger logger = LoggerFactory.getLogger(DocumentManagementServiceImpl.class);

	@Override
	public DmsResponseDto uploadDocument(File file, String requestId) {
		DmsResponseDto dmsResponse = new DmsResponseDto();
		dmsResponse.setDocumentName(file.getName());
		logger.error("[Menabev][DocumentManagement][ServiceImpl][File] = "
				+ file.getName().replace("." + getFileExtension(file.getName()), ""));
		ResponseDto response = new ResponseDto();
		try {
			SessionFactory factory = SessionFactoryImpl.newInstance();
			Map<String, String> parameter = getRepositorySessionConnection();
			Session session = factory.getRepositories(parameter).get(0).createSession();
			Folder root = session.getRootFolder();
			Folder parent = null;
			ObjectId parentId = null;
			String DMS_FOLDER = ApplicationConstants.FOLDER;
			System.out.println("DMS FOLDER::::::"+ DMS_FOLDER);
			parent = (Folder) session.getObject(DMS_FOLDER);
			// TO CHECK THE FOLDER IS AVAILABLE OR NOT
			ItemIterable<QueryResult> results = session.query(
					"SELECT COUNT(*) FROM cmis:folder where cmis:name like '%" + requestId + "'", false);
			System.out.println(results.getTotalNumItems());
			Object value = null;
			for (QueryResult hit : results) {
				if (!ServiceUtil.isEmpty(hit)) {
					for (PropertyData<?> property : hit.getProperties()) {
						if (!ServiceUtil.isEmpty(property)) {
							String queryName = property.getQueryName();
							value = property.getFirstValue();

							System.out.println(queryName + ": " + value);

						}

					}
					System.out.println("--------------------------------------");
				}

			}
			String extention = getFileExtension(file.getName());
			String mimeType = getMIMEtype(extention);

			InputStream targetStream = new FileInputStream(file);
			ContentStream contentStream = new ContentStreamImpl(file.getName(), BigInteger.valueOf(file.length()),
					mimeType, targetStream);

			String folderId = null;
			// If the folder is not present we are creating the folder and
			// uploading the document.
			if (Integer.parseInt(value.toString()) == 0) {
				Map<String, Object> properties = new HashMap<String, Object>();
				properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
				properties.put(PropertyIds.NAME, requestId); // folder
				// name
				System.out.println("PARENT::::" + parent);
				Folder newFolderId = parent.createFolder(properties);
				folderId = parent.getId();
				System.out.println(parent);

				Map<String, Object> properties2 = new HashMap<String, Object>();
				properties2.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
				properties2.put(PropertyIds.NAME, file.getName());
				// create a major version
				Document newDoc = newFolderId.createDocument(properties2, contentStream, VersioningState.MAJOR);
				response.setCode(ApplicationConstants.CODE_SUCCESS);
				response.setStatus(ApplicationConstants.SUCCESS);
				response.setMessage("Document Uploaded Successfully");
				dmsResponse.setDocumentId(newDoc.getId());
				System.out.println(dmsResponse);
				dmsResponse.setResponse(response);
				return dmsResponse;
			} else {
				String setParentByFolderId = null;
				ItemIterable<QueryResult> folderIdByQuerry = session.query(
						"SELECT cmis:objectId FROM cmis:folder where cmis:name like '%" + requestId + "'",
						false);
				System.out.println(folderIdByQuerry.getTotalNumItems());
				for (QueryResult hit : folderIdByQuerry) {
					if (!ServiceUtil.isEmpty(hit)) {
						for (PropertyData<?> property : hit.getProperties()) {
							if (!ServiceUtil.isEmpty(property)) {
								String queryName = property.getQueryName();
								value = property.getFirstValue();

								System.out.println(queryName + ": " + value);
								break;

							}

						}
						System.out.println("--------------------------------------");
					}

				}
				setParentByFolderId = String.valueOf(value);
				ItemIterable<QueryResult> docCount = session
						.query("SELECT COUNT(*) FROM cmis:document where cmis:name like '"
								+ file.getName().replace("." + getFileExtension(file.getName()), "")
								+ "%' and  IN_TREE('" + value + "')", false);
				System.out.println(docCount.getTotalNumItems());

				for (QueryResult hit : docCount) {
					if (!ServiceUtil.isEmpty(hit)) {
						for (PropertyData<?> property : hit.getProperties()) {
							if (!ServiceUtil.isEmpty(property)) {
								String queryName = property.getQueryName();
								value = property.getFirstValue();

								System.out.println(queryName + ": " + value);

							}

						}
						System.out.println("--------------------------------------");
					}

				}
				if (Integer.parseInt(value.toString()) > 0) {

					Map<String, Object> properties2 = new HashMap<String, Object>();
					properties2.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
					properties2.put(PropertyIds.NAME, getFileExtensionDeleted(file.getName()) + "("
							+ (Integer.parseInt(String.valueOf(value))) + ")." + extention);

					System.out.println("Inside");

					CmisObject folder = session.getObject(setParentByFolderId);
					System.out.println(folder.getName());
					parent = (Folder) folder;

					// create a major version
					Document newDoc = parent.createDocument(properties2, contentStream, VersioningState.MAJOR);
					dmsResponse.setDocumentId(newDoc.getId());
					System.out.println(newDoc.getId());
				} else {
					Map<String, Object> properties2 = new HashMap<String, Object>();
					properties2.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
					properties2.put(PropertyIds.NAME, file.getName());

					CmisObject folder = session.getObject(setParentByFolderId);
					System.out.println(folder.getName());
					parent = (Folder) folder;
					System.out.println(file.getName());
					// create a major version
					Document newDoc = parent.createDocument(properties2, contentStream, VersioningState.MAJOR);
					System.out.println("New DocumentId" + newDoc.getId());
					dmsResponse.setDocumentId(newDoc.getId());
				}
			}

			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage("Document Uploaded Successfully");
			dmsResponse.setResponse(response);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println(dmsResponse);
		return dmsResponse;
	}

	@Override
	public DmsGetResponseDto downloadDocument(String fileId) {

		DmsGetResponseDto response = new DmsGetResponseDto();
		try {
			SessionFactory factory = SessionFactoryImpl.newInstance();
			Map<String, String> parameter = getRepositorySessionConnection();

			List<Repository> repositories = factory.getRepositories(parameter);
			Session session = factory.getRepositories(parameter).get(0).createSession();
			Folder root = session.getRootFolder();
			ItemIterable<CmisObject> insideRootFolder = root.getChildren();
			Document doc = (Document) session.getObject(fileId);
			System.out.println();
			ContentStream contentStream = doc.getContentStream(); // returns

			if (contentStream != null) {

				InputStream is = contentStream.getStream();
				byte[] bytes = IOUtils.toByteArray(is);
				String encoded = Base64.getEncoder().encodeToString(bytes);
				response.setBase64(encoded);
				response.setMimeType(contentStream.getMimeType());
				response.setDocumentName(doc.getName());
				response.setFileAvailability(true);
				System.out.println(encoded);
			} else {
				response.setBase64(null);
				response.setMimeType(null);
				response.setFileAvailability(false);
			}

		} catch (Exception e) {
			response.setBase64(null);
			response.setMimeType(null);
			response.setFileAvailability(false);

		}
		System.out.println("DMS Get Response::::::" + response);
		return response;
	}

	@Override
	public ResponseDto deleteDocument(String fileId) {

		ResponseDto response = new ResponseDto();
		try {
			SessionFactory factory = SessionFactoryImpl.newInstance();
			Map<String, String> parameter = getRepositorySessionConnection();

			List<Repository> repositories = factory.getRepositories(parameter);
			Session session = factory.getRepositories(parameter).get(0).createSession();
			Folder root = session.getRootFolder();
			ItemIterable<CmisObject> insideRootFolder = root.getChildren();
			Document doc = (Document) session.getObject(fileId);
			doc.delete(true);
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage("Document deleted Succesfully");
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage("Document deletion failed");
		}
		return response;
	}

	// This method will return the necessary parameter to establish the
	// connection to repository
	private Map<String, String> getRepositorySessionConnection() {
		Map<String, String> parameter = new HashMap<String, String>();

		// connection settings
		parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
		parameter.put(SessionParameter.BROWSER_URL, "https://api-sdm-di.cfapps.eu20.hana.ondemand.com/browser");
		parameter.put(SessionParameter.OAUTH_ACCESS_TOKEN, getAccessToken());
		parameter.put(SessionParameter.AUTH_HTTP_BASIC, "false");
		parameter.put(SessionParameter.AUTH_SOAP_USERNAMETOKEN, "false");
		parameter.put(SessionParameter.AUTH_OAUTH_BEARER, "true");
		parameter.put(SessionParameter.USER_AGENT,
				"OpenCMIS-Workbench/1.1.0 Apache-Chemistry-OpenCMIS/1.1.0 (Java 1.8.0_271; Windows 10 10.0)");

		return parameter;

	}

	// Method the get the accessToken by using clientId and clientSecret from
	// DMS service key
	private String getAccessToken() {
		/* HTTPCLIENT AND HTTPPOST OOBJECT */
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(ApplicationConstants.DMS_TOKEN_ENDPOINT);

		/* AUTHENTICATION CREDENTIALS ENCODING */
		String base64Credentials = Base64.getEncoder().encodeToString(
				(ApplicationConstants.DMS_CLIENT_ID + ":" + ApplicationConstants.DMS_CLIENT_SECRET).getBytes());

		/* HEADER INFO */
		httpPost.addHeader("Authorization", "Basic " + base64Credentials);
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

		// /* PROXY CONFIG */
		// HttpHost target = new HttpHost("proxy", 8080, "http");
		// RequestConfig config =
		// RequestConfig.custom().setProxy(target).build();
		// httpPost.setConfig(config);

		/* OAUTH PARAMETERS ADDED TO BODY */
		StringEntity input = null;
		try {
			input = new StringEntity("grant_type=" + ApplicationConstants.DMS_GRANT_TYPE);
			httpPost.setEntity(input);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		/* SEND AND RETRIEVE RESPONSE */
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* RESPONSE AS STRING */
		String result = null;
		try {
			result = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject o = new JSONObject(result);
		System.out.println(o.getString("access_token").toString());
		System.out.println(o.getString("access_token").toString());
		return o.getString("access_token").toString();

	}

	private String getMIMEtype(String extension) {
		Map map = new HashMap();
		map.put("3gp", "video/3gpp");
		map.put("3g2", "video/3gpp2");
		map.put("7z", "application/x-7z-compressed");
		map.put("aac", "audio/aac");
		map.put("abw", "application/x-abiword");
		map.put("arc", "application/x-freearc");
		map.put("avi", "video/x-msvideo");
		map.put("azw", "application/vnd.amazon.ebook");
		map.put("bin", "application/octet-stream");
		map.put("bmp", "image/bmp");
		map.put("bz", "application/x-bzip");
		map.put("bz2", "application/x-bzip2");
		map.put("csh", "application/x-csh");
		map.put("css ", "text/css");
		map.put("csv", "text/csv");
		map.put("doc", "application/msword");
		map.put("docx", "application/vnd.openxmlformats officedocument.wordprocessingml.document");
		map.put("eot", "application/vnd.ms-fontobject");
		map.put("epub", "application/epub+zip");
		map.put("gif", "image/gif");
		map.put("htm/.html", "text/html");
		map.put("ico", "image/vnd.microsoft.icon");
		map.put("ics", "text/calendar");
		map.put("jar", "application/java-archive");
		map.put("jpg/.jpeg", "image/jpeg");
		map.put("js", "text/javascript");
		map.put("json", "application/json");
		map.put("mid/.midi", "audio/midi, audio/x-midi");
		map.put("mjs", "text/javascript");
		map.put("mp3", "audio/mpeg");
		map.put("mpeg", "video/mpeg");
		map.put("mpkg", "application/vnd.apple.installer+xml");
		map.put("odp", "application/vnd.oasis.opendocument.presentation");
		map.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
		map.put("odt", "application/vnd.oasis.opendocument.text");
		map.put("oga", "audio/ogg");
		map.put("ogv", "video/ogg");
		map.put("ogx", "application/ogg");
		map.put("otf", "font/otf");
		map.put("png", "image/png");
		map.put("pdf", "application/pdf");
		map.put("ppt", "application/vnd.ms-powerpoint");
		map.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
		map.put("rar", "application/x-rar-compressed");
		map.put("rtf", "application/rtf");
		map.put("sh", "application/x-sh");
		map.put("svg", "image/svg+xml");
		map.put("swf", "application/x-shockwave-flash");
		map.put("tar", "application/x-tar");
		map.put("tif", "image/tiff");
		map.put("ttf", "font/ttf");
		map.put("txt", "text/plain");
		map.put("vsd", "application/vnd.visio");
		map.put("wav", "audio/wav");
		map.put("weba", "audio/webm");
		map.put("webm", "video/webm");
		map.put("webp", "image/webm");
		map.put("woff", "font/woff");
		map.put("woff2", "font/woff2");
		map.put("xhtml", "application/xhtml+xml");
		map.put("xls", "application/vnd.ms-excel");
		map.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		map.put("xml", "application/xml");
		map.put("xul", "application/vnd.mozilla.xul+xml");
		map.put("zip", "application/zip");
		return map.get(extension).toString();
	}

	private static String getFileExtension(String fullName) {
		String fileName = new File(fullName).getName();
		int dotIndex = fileName.lastIndexOf('.');
		return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);

	}

	private static String getFileExtensionDeleted(String fullName) {
		String fileName = new File(fullName).getName();
		String S;
		System.out.println(fileName);
		try {

			int pos = fileName.indexOf(".");
			S = fileName.substring(0, pos);
		} catch (Exception e) {
			S = fileName;
		}
		System.out.println(S);
		return S;
	}

	private static String getContentAsString(ContentStream stream) throws IOException {
		StringBuilder sb = new StringBuilder();
		Reader reader = new InputStreamReader(stream.getStream(), "UTF-8");

		try {
			final char[] buffer = new char[4 * 1024];
			int b;
			while (true) {
				b = reader.read(buffer, 0, buffer.length);
				if (b > 0) {
					sb.append(buffer, 0, b);
				} else if (b == -1) {
					break;
				}
			}
		} finally {
			reader.close();
		}

		return sb.toString();
	}

	@Override
	public DashBoardDetailsDto extraxtXml(File file) throws SAXException, IOException {
		System.out.println(file.length());
		DashBoardDetailsDto dto = new DashBoardDetailsDto();
		InvoiceHeaderDashBoardDto invoiveHeader = new InvoiceHeaderDashBoardDto();
		List<InvoiceItemDashBoardDto> listItem = new ArrayList<>();
		InvoiceBPDto bp = new InvoiceBPDto();
		DocumentBuilder documentBuilder;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			// an instance of builder to parse the specified xml file
			DocumentBuilder db = dbf.newDocumentBuilder();
			org.w3c.dom.Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
			NodeList nodeList = doc.getElementsByTagName("_Invoice:_Invoice");

			for (int itr = 0; itr < nodeList.getLength(); itr++) {
				Node node = nodeList.item(itr);
				System.out.println("\nNode Name :" + node.getNodeName());
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;
					if (!ServiceUtil.isEmpty((eElement.getElementsByTagName("_BUId").item(0).getTextContent()))) {
						invoiveHeader.setCompCode((eElement.getElementsByTagName("_BUId").item(0).getTextContent()));
					}
					if (!ServiceUtil.isEmpty((eElement.getElementsByTagName("_VendorId").item(0).getTextContent()))) {
						invoiveHeader
								.setVendorId((eElement.getElementsByTagName("_VendorId").item(0).getTextContent()));
						bp.setId((eElement.getElementsByTagName("_VendorId").item(0).getTextContent()));
					}
					if (!ServiceUtil.isEmpty((eElement.getElementsByTagName("_Street").item(0).getTextContent()))) {
						bp.setStreet((eElement.getElementsByTagName("_Street").item(0).getTextContent()));
					}
					if (!ServiceUtil.isEmpty((eElement.getElementsByTagName("_ZIP").item(0).getTextContent()))) {
						bp.setPostalCode((eElement.getElementsByTagName("_ZIP").item(0).getTextContent()));
					}
					if (!ServiceUtil.isEmpty((eElement.getElementsByTagName("_City").item(0).getTextContent()))) {
						bp.setCity((eElement.getElementsByTagName("_City").item(0).getTextContent()));
					}
					if (!ServiceUtil.isEmpty((eElement.getElementsByTagName("_Country").item(0).getTextContent()))) {
						bp.setCountry((eElement.getElementsByTagName("_Country").item(0).getTextContent()));
					}
					if (!ServiceUtil.isEmpty((eElement.getElementsByTagName("_Name").item(0).getTextContent()))) {
						bp.setPartnerName((eElement.getElementsByTagName("_Name").item(0).getTextContent()));
					}
					if (!ServiceUtil.isEmpty((eElement.getElementsByTagName("_State").item(0).getTextContent()))) {
						// bp.set((eElement.getElementsByTagName("_State").item(0).getTextContent()));
					}
					if (!ServiceUtil
							.isEmpty((eElement.getElementsByTagName("_InvoiceNumber").item(0).getTextContent()))) {
						invoiveHeader.setExtInvNum(
								(eElement.getElementsByTagName("_InvoiceNumber").item(0).getTextContent()));
					}
					if (!ServiceUtil
							.isEmpty((eElement.getElementsByTagName("_InvoiceDate").item(0).getTextContent()))) {
						invoiveHeader.setInvoiceDate(
								(eElement.getElementsByTagName("_InvoiceDate").item(0).getTextContent()));
					}
					if (!ServiceUtil
							.isEmpty((eElement.getElementsByTagName("_ABBYY_Batch_Name").item(0).getTextContent()))) {
						// invoiveHeader.setCompCode((eElement.getElementsByTagName("_ABBYY_Batch_Name").item(0).getTextContent()));
					}
					if (!ServiceUtil
							.isEmpty((eElement.getElementsByTagName("_AccountNumber").item(0).getTextContent()))) {
						invoiveHeader.setAccountNumber(
								(eElement.getElementsByTagName("_AccountNumber").item(0).getTextContent()));
					}
					if (!ServiceUtil
							.isEmpty((eElement.getElementsByTagName("_VATRegistrationNum").item(0).getTextContent()))) {
						// invoiveHeader.s((eElement.getElementsByTagName("_VATRegistrationNum").item(0).getTextContent()));
					}
					if (!ServiceUtil
							.isEmpty((eElement.getElementsByTagName("_InvoiceType").item(0).getTextContent()))) {
						invoiveHeader.setInvoiceType(
								(eElement.getElementsByTagName("_InvoiceType").item(0).getTextContent()));
					}
					// if(!ServiceUtil.isEmpty((eElement.getElementsByTagName("_VATRegistrationNum").item(0).getTextContent()))){
					// invoiveHeader.setCompCode((eElement.getElementsByTagName("_VATRegistrationNum").item(0).getTextContent()));
					// }
					if (!ServiceUtil
							.isEmpty((eElement.getElementsByTagName("_TransactionType").item(0).getTextContent()))) {
						// invoiveHeader.setChannelType((eElement.getElementsByTagName("_TransactionType").item(0).getTextContent()));
					}
					if (!ServiceUtil
							.isEmpty((eElement.getElementsByTagName("_DeliveryNote").item(0).getTextContent()))) {
						invoiveHeader.setHeaderText(
								(eElement.getElementsByTagName("_DeliveryNote").item(0).getTextContent()));
					}
					if (!ServiceUtil
							.isEmpty((eElement.getElementsByTagName("_AmountBeforeTax").item(0).getTextContent()))) {
						// invoiveHeader.sets((eElement.getElementsByTagName("_AmountBeforeTax").item(0).getTextContent()));
					}
					if (!ServiceUtil
							.isEmpty((eElement.getElementsByTagName("_TaxPercentage").item(0).getTextContent()))) {
						// invoiveHeader.setp((eElement.getElementsByTagName("_TaxPercentage").item(0).getTextContent()));
					}
					if (!ServiceUtil.isEmpty((eElement.getElementsByTagName("_TaxValue").item(0).getTextContent()))) {
						invoiveHeader
								.setTaxAmount((eElement.getElementsByTagName("_TaxValue").item(0).getTextContent()));
					}
					if (!ServiceUtil.isEmpty((eElement.getElementsByTagName("_Surcharge").item(0).getTextContent()))) {
						// invoiveHeader.setCompCode((eElement.getElementsByTagName("_Surcharge").item(0).getTextContent()));
					}
					if (!ServiceUtil
							.isEmpty((eElement.getElementsByTagName("_FreightValue").item(0).getTextContent()))) {
						// invoiveHeader.setp((eElement.getElementsByTagName("_FreightValue").item(0).getTextContent()));
					}
					if (!ServiceUtil
							.isEmpty((eElement.getElementsByTagName("_InvoiceTotal").item(0).getTextContent()))) {
						invoiveHeader.setInvoiceTotal(
								(eElement.getElementsByTagName("_InvoiceTotal").item(0).getTextContent()));
					}
					if (!ServiceUtil.isEmpty((eElement.getElementsByTagName("_Currency").item(0).getTextContent()))) {
						invoiveHeader
								.setCurrency((eElement.getElementsByTagName("_Currency").item(0).getTextContent()));
					}

					System.out.println(invoiveHeader);

				}
			}

			List<InvoiceItemDashBoardDto> itemList = new ArrayList<>();

			NodeList lineItems = doc.getElementsByTagName("_LineItems");
			for (int temp = 0; temp < lineItems.getLength(); temp++) {
				InvoiceItemDashBoardDto item = new InvoiceItemDashBoardDto();

				Node nodeItems = lineItems.item(temp);

				if (nodeItems.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) nodeItems;
					if (!ServiceUtil.isEmpty((element.getElementsByTagName("_OrderNumber").item(0).getTextContent()))) {
						item.setMatchDocNum(Long
								.parseLong((element.getElementsByTagName("_OrderNumber").item(0).getTextContent())));
					}
					if (!ServiceUtil.isEmpty((element.getElementsByTagName("_Position").item(0).getTextContent()))) {
						// ((element.getElementsByTagName("_Position").item(0).getTextContent()))
						// ???????
					}
					if (!ServiceUtil
							.isEmpty((element.getElementsByTagName("_ArticleNumber").item(0).getTextContent()))) {
						// item.setMatchDocNum((Long.parseLong((element.getElementsByTagName("_ArticleNumber").item(0).getTextContent()))));
					}
					if (!ServiceUtil
							.isEmpty((element.getElementsByTagName("_MaterialNumber").item(0).getTextContent()))) {
						item.setPoMaterialNum(
								(element.getElementsByTagName("_MaterialNumber").item(0).getTextContent()));
					}
					if (!ServiceUtil.isEmpty((element.getElementsByTagName("_Description").item(0).getTextContent()))) {
						item.setItemText(((element.getElementsByTagName("_Description").item(0).getTextContent())));
					}
					if (!ServiceUtil.isEmpty((element.getElementsByTagName("_Quantity").item(0).getTextContent()))) {
						item.setInvQty((element.getElementsByTagName("_Quantity").item(0).getTextContent()));
					}
					// if(!ServiceUtil.isEmpty((element.getElementsByTagName("_UOM_OPU").item(0).getTextContent()))){
					//// BigDecimal value = new
					// BigDecimal((element.getElementsByTagName("_UOM_OPU").item(0).getTextContent()).toString());
					//// item.setUnitPriceOPU(value);
					// }
					if (!ServiceUtil.isEmpty((element.getElementsByTagName("_UnitPrice").item(0).getTextContent()))) {
						BigDecimal value = new BigDecimal(
								(element.getElementsByTagName("_UnitPrice").item(0).getTextContent()).toString());
						item.setUnitPriceOPU(value);
					}
					if (!ServiceUtil.isEmpty((element.getElementsByTagName("_PriceUnit").item(0).getTextContent()))) {
						item.setPricingUnit((Integer
								.parseInt((element.getElementsByTagName("_PriceUnit").item(0).getTextContent()))));
					}
					// if(!ServiceUtil.isEmpty((element.getElementsByTagName("_AmountBeforeTax").item(0).getTextContent()))){
					//// item.setam((element.getElementsByTagName("_AmountBeforeTax").item(0).getTextContent()));
					// }
					if (!ServiceUtil.isEmpty((element.getElementsByTagName("_TaxValue").item(0).getTextContent()))) {
						item.setTaxAmt(
								Integer.parseInt((element.getElementsByTagName("_TaxValue").item(0).getTextContent())));
					}
					if (!ServiceUtil
							.isEmpty((element.getElementsByTagName("_TaxPercentage").item(0).getTextContent()))) {
						item.setTaxPer(Integer
								.parseInt((element.getElementsByTagName("_TaxPercentage").item(0).getTextContent())));
					}
					if (!ServiceUtil
							.isEmpty((element.getElementsByTagName("_DiscountPercentage").item(0).getTextContent()))) {
						item.setDisPer((element.getElementsByTagName("_DiscountPercentage").item(0).getTextContent()));
					}
					if (!ServiceUtil
							.isEmpty((element.getElementsByTagName("_DiscountValue").item(0).getTextContent()))) {
						// item.val((element.getElementsByTagName("_DiscountValue").item(0).getTextContent()));
					}

					if (!ServiceUtil
							.isEmpty((element.getElementsByTagName("_ItemNetWorth").item(0).getTextContent()))) {
						item.setNetWorth((element.getElementsByTagName("_ItemNetWorth").item(0).getTextContent()));
					}

					// get staff's attribute

				}
				itemList.add(item);
			}
			System.out.println(itemList);

			dto.setBillTo(bp);
			dto.setInvoiceHeader(invoiveHeader);
			dto.setInvoiceItems(itemList);

		} catch (

		ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dto;
	}

	@Override
	public String getTest() {
		// TODO Auto-generated method stub
		return "Inside DMS";
	}

//	public static void main(String[] args) throws FileNotFoundException, IOException, SAXException {
//		DocumentManagementServiceImpl dms = new DocumentManagementServiceImpl();
//		// dms.getAccessToken();
//		File file = new File("C:\\Users\\Lakhu D\\Downloads\\exampleXml (1).xlsx");
//		 dms.uploadDocument(file,"APA-05142021-00000004");
////		 dms.downloadDocument("jBwOUb1oXWQMnm_D_RNryrVPH9_aheSqT-vrHj5qteM");
//		// dms.deleteDocument("qtpIma2YOLbRxsvFpYeD26ujU2EkWk_qsc7YCAWqd4s");
//		// dms.extraxtXml(file);
////		dms.uploadXml(file);
//	}

}
