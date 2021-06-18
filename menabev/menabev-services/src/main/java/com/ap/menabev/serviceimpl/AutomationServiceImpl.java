package com.ap.menabev.serviceimpl;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.mail.Message;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.abbyy.ABBYYIntegration;
import com.ap.menabev.abbyy.ABBYYJSONConverter;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.email.Email;
import com.ap.menabev.invoice.AttachmentRepository;
import com.ap.menabev.invoice.CommentRepository;
import com.ap.menabev.invoice.CostAllocationRepository;
import com.ap.menabev.invoice.InvoiceHeaderRepository;
import com.ap.menabev.invoice.InvoiceItemAcctAssignmentRepository;
import com.ap.menabev.invoice.InvoiceItemRepository;
import com.ap.menabev.invoice.ReasonForRejectionRepository;
import com.ap.menabev.invoice.StatusConfigRepository;
import com.ap.menabev.service.AutomationService;
import com.ap.menabev.service.InvoiceHeaderService;
import com.ap.menabev.service.InvoiceItemService;
import com.ap.menabev.service.SequenceGeneratorService;
import com.ap.menabev.sftp.SFTPChannelUtil;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;

@Service
public class AutomationServiceImpl implements AutomationService {
	@Autowired
	Email email;
	@Autowired
	ABBYYIntegration abbyyIntegration;
	@Autowired
	InvoiceHeaderRepository invoiceHeaderRepository;
	@Autowired
	InvoiceItemRepository invoiceItemRepository;
	@Autowired
	CostAllocationRepository costAllocationRepository;

	@Autowired
	EmailServiceImpl emailServiceImpl;

	@Autowired
	InvoiceItemService itemService;

	@Autowired
	ReasonForRejectionRepository reasonForRejectionRepository;

	@Autowired
	AttachmentRepository attachmentRepository;

	@Autowired
	AttachmentServiceImpl attachmentServiceImpl;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	CommentServiceImpl commentServiceImpl;

	@Autowired
	StatusConfigRepository statusConfigRepository;

	@Autowired
	InvoiceItemService invoiceItemService;

	@Autowired
	InvoiceHeaderService invoiceHeaderService;
	@Autowired
	ReasonForRejectionRepository rejectionRepository;

	@Autowired
	InvoiceItemAcctAssignmentRepository invoiceItemAcctAssignmentRepository;
	@Autowired
	InvoiceItemAcctAssignmentServiceImpl invoiceItemAcctAssignmentServiceImpl;

	@Autowired
	private SequenceGeneratorService seqService;
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AutomationServiceImpl.class);

	public ResponseDto extractInvoiceFromEmail() {
		ResponseDto response = null;
		try {
			response = new ResponseDto("Success", "200", "Uploaded files to abbyy and moved messages to folders");
			// Step 1 : Get unread emails/messages from specific sender !
			// input parameters email host,email id,password,email ids,flag,
			Message[] emailMessages = email.readEmail(ApplicationConstants.OUTLOOK_HOST,
					ApplicationConstants.ACCPAY_EMAIL_ID, ApplicationConstants.ACCPAY_EMAIL_PASSWORD,
					ApplicationConstants.INBOX_FOLDER, ApplicationConstants.UNSEEN_FLAGTERM);

			// Step 2 : Get the attachment pdf file from a message
			List<File> files = new ArrayList<File>();
			// map to move messages to processed/unprocessed folder
			Map<Message, String> messageMap = new HashMap<>();

			// setup channel connect channel pass the same channel for every put
			// get session for SFTP
			Session session = SFTPChannelUtil.getSession();

			ChannelSftp channelSftp = null;
			channelSftp = SFTPChannelUtil.getJschChannel(session);
			if (!ServiceUtil.isEmpty(channelSftp)) {
				channelSftp.connect();
				for (Message message : emailMessages) {
					String contentType = message.getContentType();
					if (contentType.contains("multipart")) {
						files = email.getAttachmentFromEmail(message);
						if (!ServiceUtil.isEmpty(files)) {
							// Step 3 : Put the files into abbyy.
							try {
								abbyyIntegration.putInvoiceInAbbyy(files,
										ApplicationConstants.ABBYY_REMOTE_INPUT_FILE_DIRECTORY, channelSftp);
								messageMap.put(message, ApplicationConstants.PROCESSED_FOLDER);
							} catch (Exception e) {
								System.out.println("AutomationServiceImpl.extractInvoiceFromEmail()");
								logger.error(
										"Error while upload file from message AutomationServiceImpl.extractInvoiceFromEmail() "
												+ e.getMessage());
								e.printStackTrace();
								messageMap.put(message, ApplicationConstants.UNPROCESSED_FOLDER);

							}
						} else {
							response = new ResponseDto("Done", "200", "No PDF in the Attacment found ");
						}
					} else {
						response = new ResponseDto("Done", "200", "No Attacment found ");
					}

				}

			} else {
				channelSftp = SFTPChannelUtil.getJschChannel(session);
				for (Message message : emailMessages) {
					String contentType = message.getContentType();
					if (contentType.contains("multipart")) {
						files = email.getAttachmentFromEmail(message);
						if (!ServiceUtil.isEmpty(files)) {
							// Step 3 : Put the files into abbyy.
							try {
								abbyyIntegration.putInvoiceInAbbyy(files,
										ApplicationConstants.ABBYY_REMOTE_INPUT_FILE_DIRECTORY, channelSftp);
								messageMap.put(message, ApplicationConstants.PROCESSED_FOLDER);
							} catch (Exception e) {
								System.out.println("AutomationServiceImpl.extractInvoiceFromEmail()");
								logger.error(
										"Error while upload file from message AutomationServiceImpl.extractInvoiceFromEmail() "
												+ e.getMessage());
								e.printStackTrace();
								messageMap.put(message, ApplicationConstants.UNPROCESSED_FOLDER);

							}
						} else {
							response = new ResponseDto("Done", "200", "No PDF in the Attacment found ");
						}
					} else {
						response = new ResponseDto("Done", "200", "No Attacment found ");
					}

				}

			}
			// disconnect the channel
			SFTPChannelUtil.disconnect(session, channelSftp);
			// NOW MOVE THE MESSAGES TO THE RESPECTIVE FOLDERS.
			for (Map.Entry<Message, String> entry : messageMap.entrySet()) {
				if (ApplicationConstants.PROCESSED_FOLDER.equalsIgnoreCase(entry.getValue())) {
					email.moveMessage(entry.getKey(), ApplicationConstants.INBOX_FOLDER, entry.getValue());
				} else {
					email.moveMessage(entry.getKey(), ApplicationConstants.INBOX_FOLDER, entry.getValue());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AutomationServiceImpl.extractInvoiceFromEmail():::" + e.getMessage());
			response = new ResponseDto("Failed", "500", e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseDto downloadFilesFromSFTPABBYYServer() {
		ResponseDto response = new ResponseDto();
		List<InvoiceHeaderDto> headerList = new ArrayList<>();

		List<JSONObject> fileNmaes = new ArrayList<>();

		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		try {
			Session session = SFTPChannelUtil.getSession();
			ChannelSftp channelSftp = null;
			channelSftp = SFTPChannelUtil.getJschChannel(session);
			channelSftp.connect();
			try {
				logger.error("Errorrrrrrrrrrrr" + ServiceUtil.isEmpty(channelSftp));
				channelSftp.cd("\\Output\\");
				// Vector<ChannelSftp.LsEntry> filelist =
				// channelSftp.ls("*.json");
				Vector<ChannelSftp.LsEntry> filelist = channelSftp.ls("*.json");
				logger.error("Vector<ChannelSftp.LsEntry> filelist" + filelist.size());
				for (ChannelSftp.LsEntry entry : filelist) {
					try {
						logger.error("INSIDE JSON " + entry.getFilename());
						if (entry.getFilename().contains(".json")) {
							logger.error("INSIDE JSON ");
							InputStream is = channelSftp.get(entry.getFilename());
							ObjectMapper mapper = new ObjectMapper();
							Map<String, Object> jsonMap = mapper.readValue(is, Map.class);
							JSONObject jsonObject = new JSONObject(jsonMap);
							if (jsonObject.has("Documents")) {
								InvoiceHeaderDto output = ABBYYJSONConverter.abbyyJSONOutputToInvoiceObject(jsonObject);
								headerList.add(output);
							}
							is.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
						response.setMessage("Error while reading json" + e.getMessage());

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				response.setMessage("Error while opening sftp" + e.getMessage());
			}

			// fileNmaes = abbyyIntegration.getJsonOutputFromSFTP(channelSftp);

			SFTPChannelUtil.disconnect(session, channelSftp);

			response = saveAllInvoiceDetails(headerList);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	private ResponseDto saveAllInvoiceDetails(List<InvoiceHeaderDto> headerList) {
		// TODO Auto-generated method stub
		ResponseDto response = null;
		try {
			for (InvoiceHeaderDto invoiceHeaderDto : headerList) {
				invoiceHeaderDto.setLifecycleStatus(ApplicationConstants.LIFE_CYCLE_STATUS_OPEN);
				invoiceHeaderDto.setChannelType(ApplicationConstants.CHANEL_TYPE_EMAIL);
				invoiceHeaderDto.setCreatedAtInDB(ServiceUtil.getEpocTime());
				invoiceHeaderDto.setCreatedByInDb("SYSTEM");
				List<InvoiceItemDto> invoiceItemList = invoiceHeaderDto.getInvoiceItems();
				for (InvoiceItemDto invoiceItemDto : invoiceItemList) {
					invoiceItemDto.setCreatedByInDb("SYSTEM");
					invoiceItemDto.setCreatedAtInDB(ServiceUtil.getEpocTime());
					invoiceItemDto.setCurrency(invoiceHeaderDto.getCurrency());
				}

				response = invoiceHeaderService.saveOrUpdate(invoiceHeaderDto);

			}
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ResponseDto("500", "0", "Error " + e.getMessage());

		}

	}

	@Override
	public ResponseDto extractInvoiceFromSharedEmailBox() {
		// TODO Auto-generated method stub
		ResponseDto response = null;
		try {
			Message[] emailMessages = email.fetchUnReadMessagesFromSharedMailBox(ApplicationConstants.OUTLOOK_HOST,
					ApplicationConstants.OUTLOOK_PORT, ApplicationConstants.SHARED_MAIL_ID_ALLIAS,
					ApplicationConstants.ACCPAY_EMAIL_PASSWORD, ApplicationConstants.INBOX_FOLDER,
					ApplicationConstants.UNSEEN_FLAGTERM, ApplicationConstants.EMAIL_FROM);

			logger.error(emailMessages.length + "After reading email");
			response = new ResponseDto("Success", "200", "Uploaded files to abbyy and moved messages to folders");
			// Step 2 : Get the attachment pdf file from a message
			List<File> files = new ArrayList<File>();
			// map to move messages to processed/unprocessed folder
			Map<Message, String> messageMap = new HashMap<>();

			// setup channel connect channel pass the same channel for every put
			// get session for SFTP
			Session session = SFTPChannelUtil.getSession();

			ChannelSftp channelSftp = null;
			channelSftp = SFTPChannelUtil.getJschChannel(session);
			if (!ServiceUtil.isEmpty(channelSftp)) {
				channelSftp.connect();
				if (!ServiceUtil.isEmpty(emailMessages)) {

					for (Message message : emailMessages) {
						String contentType = message.getContentType();
						if (contentType.contains("multipart")) {
							files = email.getAttachmentFromEmail(message);
							if (!ServiceUtil.isEmpty(files)) {
								// Step 3 : Put the files into abbyy.
								try {
									abbyyIntegration.putInvoiceInAbbyy(files,
											ApplicationConstants.ABBYY_REMOTE_INPUT_FILE_DIRECTORY, channelSftp);
									messageMap.put(message, ApplicationConstants.PROCESSED_FOLDER);
								} catch (Exception e) {
									System.out.println("AutomationServiceImpl.extractInvoiceFromEmail()");
									logger.error(
											"Error while upload file from message AutomationServiceImpl.extractInvoiceFromEmail() "
													+ e.getMessage());
									e.printStackTrace();
									messageMap.put(message, ApplicationConstants.UNPROCESSED_FOLDER);

								}
							} else {
								response = new ResponseDto("Done", "200", "No PDF in the Attacment found ");
							}
						} else {
							response = new ResponseDto("Done", "200", "No Attacment found ");
						}

					}
				} else {
					response = new ResponseDto("Failed", "400", "Message is null or empty");
				}

			} else {
				channelSftp = SFTPChannelUtil.getJschChannel(session);
				for (Message message : emailMessages) {
					String contentType = message.getContentType();
					if (contentType.contains("multipart")) {
						files = email.getAttachmentFromEmail(message);
						if (!ServiceUtil.isEmpty(files)) {
							// Step 3 : Put the files into abbyy.
							try {
								abbyyIntegration.putInvoiceInAbbyy(files,
										ApplicationConstants.ABBYY_REMOTE_INPUT_FILE_DIRECTORY, channelSftp);
								messageMap.put(message, ApplicationConstants.PROCESSED_FOLDER);
							} catch (Exception e) {
								System.out.println("AutomationServiceImpl.extractInvoiceFromEmail()");
								logger.error(
										"Error while upload file from message AutomationServiceImpl.extractInvoiceFromEmail() "
												+ e.getMessage());
								e.printStackTrace();
								messageMap.put(message, ApplicationConstants.UNPROCESSED_FOLDER);

							}
						} else {
							response = new ResponseDto("Done", "200", "No PDF in the Attacment found ");
						}
					} else {
						response = new ResponseDto("Done", "200", "No Attacment found ");
					}

				}

			}
			// disconnect the channel
			SFTPChannelUtil.disconnect(session, channelSftp);
			// NOW MOVE THE MESSAGES TO THE RESPECTIVE FOLDERS.
			for (Map.Entry<Message, String> entry : messageMap.entrySet()) {
				if (ApplicationConstants.PROCESSED_FOLDER.equalsIgnoreCase(entry.getValue())) {
					email.moveMessage(entry.getKey(), ApplicationConstants.INBOX_FOLDER, entry.getValue());
				} else {
					email.moveMessage(entry.getKey(), ApplicationConstants.INBOX_FOLDER, entry.getValue());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AutomationServiceImpl.extractInvoiceFromEmail():::" + e.getMessage());
			response = new ResponseDto("Failed", "500", e.getMessage());
		}
		return response;
	}
}