package com.ap.menabev.serviceimpl;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import javax.mail.Message;
import javax.mail.Multipart;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ap.menabev.abbyy.ABBYYIntegration;
import com.ap.menabev.abbyy.ABBYYJSONConverter;
import com.ap.menabev.dto.AcountOrProcessLeadDetermination;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.SchedulerResponseDto;
import com.ap.menabev.dto.TriggerWorkflowContext;
import com.ap.menabev.dto.WorkflowContextDto;
import com.ap.menabev.dto.WorkflowTaskOutputDto;
import com.ap.menabev.email.Email;
import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.entity.SchedulerConfigurationDo;
import com.ap.menabev.entity.SchedulerCycleDo;
import com.ap.menabev.entity.SchedulerCycleLogDo;
import com.ap.menabev.entity.SchedulerRunDo;
import com.ap.menabev.invoice.AttachmentRepository;
import com.ap.menabev.invoice.CommentRepository;
import com.ap.menabev.invoice.CostAllocationRepository;
import com.ap.menabev.invoice.InvoiceHeaderRepository;
import com.ap.menabev.invoice.InvoiceItemAcctAssignmentRepository;
import com.ap.menabev.invoice.InvoiceItemRepository;
import com.ap.menabev.invoice.ReasonForRejectionRepository;
import com.ap.menabev.invoice.SchedulerCycleLogRepository;
import com.ap.menabev.invoice.SchedulerCycleRepository;
import com.ap.menabev.invoice.SchedulerRunRepository;
import com.ap.menabev.invoice.StatusConfigRepository;
import com.ap.menabev.service.AutomationService;
import com.ap.menabev.service.InvoiceHeaderService;
import com.ap.menabev.service.InvoiceItemService;
import com.ap.menabev.service.PurchaseDocumentHeaderService;
import com.ap.menabev.service.SequenceGeneratorService;
import com.ap.menabev.sftp.SFTPChannelUtil;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ObjectMapperUtils;
import com.ap.menabev.util.ServiceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
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
	PurchaseDocumentHeaderService purchaseHeaderService;
	
	@Autowired
	ReasonForRejectionRepository rejectionRepository;

	@Autowired
	InvoiceItemAcctAssignmentRepository invoiceItemAcctAssignmentRepository;
	@Autowired
	InvoiceItemAcctAssignmentServiceImpl invoiceItemAcctAssignmentServiceImpl;

	@Autowired
	SchedulerRunRepository schedulerRunRepository;
	@Autowired
	SchedulerCycleRepository schedulerCycleRepository;
	@Autowired
	SchedulerCycleLogRepository schedulerCycleLogRepository;

	@Autowired
	private SequenceGeneratorService seqService;
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AutomationServiceImpl.class);

	public ResponseDto extractInvoiceFromEmail() {
		ResponseDto response = null;
		try {
			response = new ResponseDto("Success", "200", "Uploaded files to abbyy and moved messages to folders",null);
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
							response = new ResponseDto("Done", "200", "No PDF in the Attacment found ",null);
						}
					} else {
						response = new ResponseDto("Done", "200", "No Attacment found ",null);
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
							response = new ResponseDto("Done", "200", "No PDF in the Attacment found ",null);
						}
					} else {
						response = new ResponseDto("Done", "200", "No Attacment found ",null);
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
			response = new ResponseDto("Failed", "500", e.getMessage(),null);
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
		ResponseDto response = null;
		ResponseDto responseAutoPosting  = null;
		try {
			for (InvoiceHeaderDto invoiceHeaderDto : headerList) {
				invoiceHeaderDto.setChannelType(ApplicationConstants.CHANEL_TYPE_EMAIL);
				invoiceHeaderDto.setRequest_created_at(ServiceUtil.getEpocTime());
				invoiceHeaderDto.setRequest_created_by("SYSTEM");
				List<InvoiceItemDto> invoiceItemList = invoiceHeaderDto.getInvoiceItems();
				for (InvoiceItemDto invoiceItemDto : invoiceItemList) {
					invoiceItemDto.setCurrency(invoiceHeaderDto.getCurrency());
				}
				response = invoiceHeaderService.saveOrUpdate(invoiceHeaderDto);
				System.err.println("InvoiceHeader "+response );
				
				// call autoposting method 
				InvoiceHeaderDto    invoiceHeaderAutoPost =  purchaseHeaderService.autoPostApi((InvoiceHeaderDto) response.getObject());
				// calling rule file and worklfow 
				AcountOrProcessLeadDetermination determination = new AcountOrProcessLeadDetermination();
				determination.setCompCode(invoiceHeaderAutoPost.getCompCode());
				determination.setProcessLeadCheck("Accountant");
				determination.setVednorId(invoiceHeaderAutoPost.getVendorId());
				ResponseEntity<?> responseRules = invoiceHeaderService.triggerRuleService(determination);
				System.err.println("responseRules Scheduler "+ responseRules);
				@SuppressWarnings("unchecked")
				List<ApproverDataOutputDto> lists = (List<ApproverDataOutputDto>) responseRules.getBody();
				System.err.println("ApproverList scheduler"+lists);
				// trigger workflow 
				TriggerWorkflowContext context = new TriggerWorkflowContext();
				context.setRequestId(invoiceHeaderAutoPost.getRequestId());
				context.setInvoice_ref_number(invoiceHeaderAutoPost.getInvoice_ref_number());
				context.setNonPo(false);
				context.setManualNonPo(false);
				context.setAccountantUser(lists.get(0).getUserOrGroup());
				context.setAccountantGroup(invoiceHeaderAutoPost.getTaskGroup());
				context.setProcessLead(lists.get(0).getUserOrGroup());
				context.setAccountantAction("");
				context.setInvoiceStatus(invoiceHeaderAutoPost.getInvoiceStatus());
				context.setInvoiceStatusText(invoiceHeaderAutoPost.getInvoiceStatusText());
				context.setInvoiceType(invoiceHeaderAutoPost.getInvoiceType());
				context.setProcessLeadAction("");
				context.setRemediationUser("");
				context.setRemediationUserAction("");
				ResponseEntity<?> responseWorkflow = invoiceHeaderService.triggerWorkflow((WorkflowContextDto) context,
						"triggerresolutionworkflow.triggerresolutionworkflow");
				System.err.println("response of workflow Trigger scheduler" + response);
				WorkflowTaskOutputDto taskOutputDto = (WorkflowTaskOutputDto) responseWorkflow.getBody();
				// save invoice header
				invoiceHeaderDto.setWorkflowId(taskOutputDto.getId());
				invoiceHeaderDto.setTaskStatus("READY");
				responseAutoPosting = invoiceHeaderService.saveOrUpdate(invoiceHeaderAutoPost);
				System.err.println("invoiceHeaderAutoPost responseAutoPosting ="+responseAutoPosting);
				response.setObject(invoiceHeaderDto);
			}
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ResponseDto("500", "0", "Error " + e.getMessage(),null);

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
			response = new ResponseDto("Success", "200", "Uploaded files to abbyy and moved messages to folders",null);
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
							System.err.println("330 files count= "+files.size());
							if (!ServiceUtil.isEmpty(files)) {
								// Step 3 : Put the files into abbyy.
								try {
									abbyyIntegration.putInvoiceInAbbyy(files,
											ApplicationConstants.ABBYY_REMOTE_INPUT_FILE_DIRECTORY, channelSftp);
									messageMap.put(message, ApplicationConstants.PROCESSED_FOLDER);
								} catch (Exception e) {
									System.err.println("AutomationServiceImpl.extractInvoiceFromEmail()");
									logger.error(
											"Error while upload file from message AutomationServiceImpl.extractInvoiceFromEmail() "
													+ e.getMessage());
									e.printStackTrace();
									messageMap.put(message, ApplicationConstants.UNPROCESSED_FOLDER);

								}
							} else {
								response = new ResponseDto("Done", "200", "No PDF in the Attacment found ",null);
							}
						} else {
							response = new ResponseDto("Done", "200", "No Attacment found ",null);
						}

					}
				} else {
					response = new ResponseDto("Failed", "400", "Message is null or empty",null);
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
							response = new ResponseDto("Done", "200", "No PDF in the Attacment found ",null);
						}
					} else {
						response = new ResponseDto("Done", "200", "No Attacment found ",null);
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
			response = new ResponseDto("Failed", "500", e.getMessage(),null);
		}
		return response;
	}

	@Override
	public void extractInvoiceFromSharedEmailBoxInScheduler(SchedulerConfigurationDo entity) {
		// TODO Auto-generated method stub
		SchedulerCycleDo cycleEntity = new SchedulerCycleDo();
		cycleEntity.setStartDateTime(ServiceUtil.getFormattedDateinString("yyyy-MM-dd hh:mm:ss"));
		SchedulerResponseDto response = new SchedulerResponseDto();
		List<SchedulerCycleLogDo> schedulerCycleLogDoList = new ArrayList<>();
		int noOfEmailspicked = 0;
		int noOfAttachement = 0;
		int noOfEmailsReadSuccessfully = 0;
		int noOfJSONFiles = 0;
		int noOfPDFs = 0;
		int logMsgNo = 0;
		response.setMessage("Uploaded files to abbyy and moved messages to folders");
		response.setNoOfAttachement(noOfAttachement);
		response.setNoOfEmailspicked(noOfEmailspicked);
		response.setNoOfEmailsReadSuccessfully(noOfEmailsReadSuccessfully);
		response.setNoOfPDFs(noOfPDFs);
		try {
			// SchedulerRunDo schedulerRunDo = new SchedulerRunDo();
			// schedulerRunDo.setSchedulerConfigID(entity.getScId());
			// schedulerRunDo.setSchedulerRunID(UUID.randomUUID().toString());
			// schedulerRunDo.setDatetimeSwitchedON(new
			// Date(entity.getCreatedAt()));
			// schedulerRunDo.setSchedulerName("Email Reader");
			// schedulerRunDo.setSwichtedONby(entity.getCreatedBy());
			// schedulerRunDo = schedulerRunRepository.save(schedulerRunDo);

			Message[] emailMessages = email.fetchUnReadMessagesFromSharedMailBox(ApplicationConstants.OUTLOOK_HOST,
					ApplicationConstants.OUTLOOK_PORT, ApplicationConstants.SHARED_MAIL_ID_ALLIAS,
					ApplicationConstants.ACCPAY_EMAIL_PASSWORD, ApplicationConstants.INBOX_FOLDER,
					ApplicationConstants.UNSEEN_FLAGTERM, ApplicationConstants.EMAIL_FROM);

			noOfEmailspicked = emailMessages.length;

			logger.error("noOfEmailspicked" + noOfEmailspicked);

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
						noOfEmailsReadSuccessfully = noOfEmailsReadSuccessfully ++;
						String contentType = message.getContentType();
						if (contentType.contains("multipart")) {
							Multipart multiPart = (Multipart) message.getContent();
							noOfAttachement = multiPart.getCount();
							files = email.getAttachmentFromEmail(message);
							if (!ServiceUtil.isEmpty(files)) {
								// Step 3 : Put the files into abbyy.
								noOfPDFs = files.size();
								try {
									abbyyIntegration.putInvoiceInAbbyy(files,
											ApplicationConstants.ABBYY_REMOTE_INPUT_FILE_DIRECTORY, channelSftp);
									messageMap.put(message, ApplicationConstants.PROCESSED_FOLDER);
									SchedulerCycleLogDo cycleLogDo = new SchedulerCycleLogDo();
									cycleLogDo.setUuId(UUID.randomUUID().toString());
									cycleLogDo.setLogMsgText("File uploaded Successfully from message "
											+ message.getFrom()[0] + " with message subject :" + message.getSubject());
									schedulerCycleLogDoList.add(cycleLogDo);
								} catch (Exception e) {
									SchedulerCycleLogDo cycleLogDo = new SchedulerCycleLogDo();
									cycleLogDo.setUuId(UUID.randomUUID().toString());
									cycleLogDo.setLogMsgText("Error while upload file from message"
											+ message.getFrom()[0] + " with message subject :" + message.getSubject());
									schedulerCycleLogDoList.add(cycleLogDo);
									System.out.println("AutomationServiceImpl.extractInvoiceFromEmail()");
									logger.error(
											"Error while upload file from message AutomationServiceImpl.extractInvoiceFromEmail() "
													+ e.getMessage());
									e.printStackTrace();
									messageMap.put(message, ApplicationConstants.UNPROCESSED_FOLDER);

								}
							} else {
								SchedulerCycleLogDo cycleLogDo = new SchedulerCycleLogDo();
								cycleLogDo.setUuId(UUID.randomUUID().toString());
								cycleLogDo.setLogMsgText("No PDF in the Attacment found for " + message.getFrom()[0]
										+ " with message subject :" + message.getSubject());
								schedulerCycleLogDoList.add(cycleLogDo);
								response.setMessage("No PDF in the Attacment found for " + message.getFrom()[0]
										+ " with message subject :" + message.getSubject());
								response.setNoOfAttachement(noOfAttachement);
								response.setNoOfEmailspicked(noOfEmailspicked);
								response.setNoOfEmailsReadSuccessfully(noOfEmailsReadSuccessfully);
								response.setNoOfPDFs(noOfPDFs);
							}
						} else {
							SchedulerCycleLogDo cycleLogDo = new SchedulerCycleLogDo();
							cycleLogDo.setUuId(UUID.randomUUID().toString());
							cycleLogDo.setLogMsgText("No Attacment found for " + message.getFrom()[0]
									+ " with message subject :" + message.getSubject());
							schedulerCycleLogDoList.add(cycleLogDo);
							response.setMessage("No Attacment found for " + message.getFrom()[0]
									+ " with message subject :" + message.getSubject());
							response.setNoOfAttachement(noOfAttachement);
							response.setNoOfEmailspicked(noOfEmailspicked);
							response.setNoOfEmailsReadSuccessfully(noOfEmailsReadSuccessfully);
							response.setNoOfPDFs(noOfPDFs);
						}

					}
				} else {
					SchedulerCycleLogDo cycleLogDo = new SchedulerCycleLogDo();
					cycleLogDo.setUuId(UUID.randomUUID().toString());
					cycleLogDo.setLogMsgText("Message is null or empty");
					schedulerCycleLogDoList.add(cycleLogDo);
					response.setMessage("Message is null or empty");
					response.setNoOfAttachement(noOfAttachement);
					response.setNoOfEmailspicked(noOfEmailspicked);
					response.setNoOfEmailsReadSuccessfully(noOfEmailsReadSuccessfully);
					response.setNoOfPDFs(noOfPDFs);
				}

			} else {
				channelSftp = SFTPChannelUtil.getJschChannel(session);
				for (Message message : emailMessages) {
					noOfEmailsReadSuccessfully = noOfEmailsReadSuccessfully + 1;
					String contentType = message.getContentType();
					if (contentType.contains("multipart")) {
						Multipart multiPart = (Multipart) message.getContent();
						noOfAttachement = multiPart.getCount();
						files = email.getAttachmentFromEmail(message);
						if (!ServiceUtil.isEmpty(files)) {
							noOfPDFs = files.size();
							// Step 3 : Put the files into abbyy.
							try {
								abbyyIntegration.putInvoiceInAbbyy(files,
										ApplicationConstants.ABBYY_REMOTE_INPUT_FILE_DIRECTORY, channelSftp);
								messageMap.put(message, ApplicationConstants.PROCESSED_FOLDER);
								SchedulerCycleLogDo cycleLogDo = new SchedulerCycleLogDo();
								cycleLogDo.setUuId(UUID.randomUUID().toString());
								cycleLogDo.setLogMsgText("File uploaded Successfully from message "
										+ message.getFrom()[0] + " with message subject :" + message.getSubject());
								schedulerCycleLogDoList.add(cycleLogDo);
							} catch (Exception e) {
								SchedulerCycleLogDo cycleLogDo = new SchedulerCycleLogDo();
								cycleLogDo.setUuId(UUID.randomUUID().toString());
								cycleLogDo.setLogMsgText("Error while upload file from message" + message.getFrom()[0]
										+ " with message subject :" + message.getSubject());
								schedulerCycleLogDoList.add(cycleLogDo);
								System.out.println("AutomationServiceImpl.extractInvoiceFromEmail()");
								logger.error(
										"Error while upload file from message AutomationServiceImpl.extractInvoiceFromEmail() "
												+ e.getMessage());
								e.printStackTrace();
								messageMap.put(message, ApplicationConstants.UNPROCESSED_FOLDER);

							}
						} else {
							SchedulerCycleLogDo cycleLogDo = new SchedulerCycleLogDo();
							cycleLogDo.setUuId(UUID.randomUUID().toString());
							cycleLogDo.setLogMsgText("No PDF in the Attacment found for " + message.getFrom()[0]
									+ " with message subject :" + message.getSubject());
							schedulerCycleLogDoList.add(cycleLogDo);
							response.setMessage("No PDF in the Attacment found for " + message.getFrom()[0]
									+ " with message subject :" + message.getSubject());
							response.setNoOfAttachement(noOfAttachement);
							response.setNoOfEmailspicked(noOfEmailspicked);
							response.setNoOfEmailsReadSuccessfully(noOfEmailsReadSuccessfully);
							response.setNoOfPDFs(noOfPDFs);
						}
					} else {

						SchedulerCycleLogDo cycleLogDo = new SchedulerCycleLogDo();
						cycleLogDo.setUuId(UUID.randomUUID().toString());
						cycleLogDo.setLogMsgText("No Attacment found for " + message.getFrom()[0]
								+ " with message subject :" + message.getSubject());
						schedulerCycleLogDoList.add(cycleLogDo);
						response.setMessage("No Attacment found for " + message.getFrom()[0] + " with message subject :"
								+ message.getSubject());
						response.setNoOfAttachement(noOfAttachement);
						response.setNoOfEmailspicked(noOfEmailspicked);
						response.setNoOfEmailsReadSuccessfully(noOfEmailsReadSuccessfully);
						response.setNoOfPDFs(noOfPDFs);

					}

				}

			}
			// disconnect the channel
			SFTPChannelUtil.disconnect(session, channelSftp);
			// NOW MOVE THE MESSAGES TO THE RESPECTIVE FOLDERS.
			for (Map.Entry<Message, String> entry : messageMap.entrySet()) {
				try {
					if (ApplicationConstants.PROCESSED_FOLDER.equalsIgnoreCase(entry.getValue())) {
						email.moveMessage(entry.getKey(), ApplicationConstants.INBOX_FOLDER, entry.getValue());
						SchedulerCycleLogDo cycleLogDo = new SchedulerCycleLogDo();
						cycleLogDo.setUuId(UUID.randomUUID().toString());
						cycleLogDo.setLogMsgText("Email from " + entry.getKey().getFrom()[0] + " with message subject :"
								+ entry.getKey().getSubject() + " moved to " + ApplicationConstants.PROCESSED_FOLDER);
						schedulerCycleLogDoList.add(cycleLogDo);
					} else {
						email.moveMessage(entry.getKey(), ApplicationConstants.INBOX_FOLDER, entry.getValue());
						SchedulerCycleLogDo cycleLogDo = new SchedulerCycleLogDo();
						cycleLogDo.setUuId(UUID.randomUUID().toString());
						cycleLogDo.setLogMsgText("Email from " + entry.getKey().getFrom()[0] + " with message subject :"
								+ entry.getKey().getSubject() + " moved to " + entry.getValue());
						schedulerCycleLogDoList.add(cycleLogDo);
					}
				} catch (Exception e) {
					e.printStackTrace();
					SchedulerCycleLogDo cycleLogDo = new SchedulerCycleLogDo();
					cycleLogDo.setUuId(UUID.randomUUID().toString());
					cycleLogDo.setLogMsgText("Error occured while moving Email from " + entry.getKey().getFrom()[0]
							+ " with message subject :" + entry.getKey().getSubject() + "  to " + entry.getValue()
							+ " folder");
					schedulerCycleLogDoList.add(cycleLogDo);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AutomationServiceImpl.extractInvoiceFromEmail():::" + e.getMessage());
			response.setMessage("Error : " + e.getMessage());
			response.setNoOfAttachement(noOfAttachement);
			response.setNoOfEmailspicked(noOfEmailspicked);
			response.setNoOfEmailsReadSuccessfully(noOfEmailsReadSuccessfully);
			response.setNoOfPDFs(noOfPDFs);
		}

		SchedulerRunDo schedulerRun = schedulerRunRepository.getBySchedulerConfigID(entity.getScId());
//		List<Object[]> oldCycle = schedulerCycleRepository.getMaxResultsBySchedulerRunId(schedulerRun.getSchedulerRunID());
//		int savedNoOfEmailspicked = 0;
//		int saveNoOfAttachements = 0;
//		int saveNoOfEmailsReadSuccessfully = 0;
//		int saveNoOfPDFs = 0;
//		int savenoOfJSONFiles = 0;
//		for (Object[] oldCycleEntity:oldCycle) {
//			savedNoOfEmailspicked = (int) oldCycleEntity[0];
//			saveNoOfEmailsReadSuccessfully = (int) oldCycleEntity[1];
//			saveNoOfAttachements = (int) oldCycleEntity[2];
//			saveNoOfPDFs = (int) oldCycleEntity[3];
//			savenoOfJSONFiles = (int) oldCycleEntity[4];
//		}
		cycleEntity.setNOfEmailspicked(noOfEmailspicked);
		cycleEntity.setNoOfAttachements(noOfAttachement);
		cycleEntity.setNoOfEmailsReadSuccessfully(noOfEmailsReadSuccessfully);
		cycleEntity.setNoOfPDFs(noOfPDFs);
		cycleEntity.setSchedulerRunID(schedulerRun.getSchedulerRunID());
		cycleEntity.setEndDatetime(ServiceUtil.getFormattedDateinString("yyyy-MM-dd hh:mm:ss"));
		cycleEntity.setSchedulerCycleID(UUID.randomUUID().toString());
		cycleEntity.setNoOfJSONFiles(noOfJSONFiles);
		SchedulerCycleDo savedCycleEntity = schedulerCycleRepository.save(cycleEntity);
		for (SchedulerCycleLogDo cycleLogDo : schedulerCycleLogDoList) {
			cycleLogDo.setLogMsgNo(String.valueOf(logMsgNo+1));
			cycleLogDo.setCycleID(savedCycleEntity.getSchedulerCycleID());
			cycleLogDo.setRunID(schedulerRun.getSchedulerRunID());
		}

		schedulerCycleLogRepository.saveAll(schedulerCycleLogDoList);
		schedulerRun.setNoOfCycles((schedulerRun.getNoOfCycles()== null ? 0 : schedulerRun.getNoOfCycles())+1);
		schedulerRunRepository.save(schedulerRun);
	}

	@Override
	public List<String> getNames() {
		List<String> ret = new ArrayList<String>();
		try {
			Session session = SFTPChannelUtil.getSession();
			ChannelSftp channelSftp = null;
			channelSftp = SFTPChannelUtil.getJschChannel(session);
			channelSftp.connect();
			logger.error("Errorrrrrrrrrrrr" + ServiceUtil.isEmpty(channelSftp));
			channelSftp.cd("\\Output\\");
			Vector<String> files = channelSftp.ls("*");
			for (int i = 0; i < files.size(); i++)
			{
			    Object obj = files.elementAt(i);
			    logger.error("Objet  "+obj.toString());
			    if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry)
			    {
			        LsEntry entry = (LsEntry) obj;
			        if (true && !entry.getAttrs().isDir())
			        {
			        	logger.error("line 1"+entry.getFilename());
			            ret.add(entry.getFilename());
			        }
			        if (true && entry.getAttrs().isDir())
			        {
			        	
			            if (!entry.getFilename().equals(".") && !entry.getFilename().equals(".."))
			            {
			            	logger.error("line 2"+entry.getFilename());
			            	
			            	if(entry.getFilename().contains("Batch")){
			            		logger.error("INSIDE Batch" );
//			            		channelSftp.cd(entry.getFilename()+"\\");
//			            		channelSftp.
			            		Vector<ChannelSftp.LsEntry> filelist = channelSftp.ls("*.json");
			            		for (ChannelSftp.LsEntry fileEntry : filelist) {
			    					try {
			    						logger.error("INSIDE JSON " + fileEntry.getFilename());
			    						if (fileEntry.getFilename().contains(".json")) {
			    							logger.error("INSIDE JSON ");
			    							InputStream is = channelSftp.get(fileEntry.getFilename());
			    							ObjectMapper mapper = new ObjectMapper();
			    							Map<String, Object> jsonMap = mapper.readValue(is, Map.class);
			    							JSONObject jsonObject = new JSONObject(jsonMap);
			    							if (jsonObject.has("Documents")) {
			    								InvoiceHeaderDto output = ABBYYJSONConverter.abbyyJSONOutputToInvoiceObject(jsonObject);
			    								logger.error("InvoiceHeaderDto  :"+output);
			    							}
			    							is.close();
			    						}
			    					} catch (Exception e) {
			    						e.printStackTrace();
			    						

			    					}
			    				}
			            	}
			                ret.add(entry.getFilename());
			            }
			        }
			    }
			}
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("Error in "+e.getMessage());
			return null;
		}
	}
}
