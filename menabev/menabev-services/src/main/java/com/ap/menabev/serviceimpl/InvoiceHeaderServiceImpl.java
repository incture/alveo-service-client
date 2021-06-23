package com.ap.menabev.serviceimpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ap.menabev.dto.AcountOrProcessLeadDetermination;
import com.ap.menabev.dto.ActivityLogDto;
import com.ap.menabev.dto.AttachmentDto;
import com.ap.menabev.dto.ClaimAndReleaseDto;
import com.ap.menabev.dto.ClaimResponseDto;
import com.ap.menabev.dto.CommentDto;
import com.ap.menabev.dto.CostAllocationDto;
import com.ap.menabev.dto.CreateInvoiceHeaderDto;
import com.ap.menabev.dto.HeaderCheckDto;
import com.ap.menabev.dto.HeaderMessageDto;
import com.ap.menabev.dto.InboxOutputDto;
import com.ap.menabev.dto.InboxResponseOutputDto;
import com.ap.menabev.dto.InvoiceChangeIndicator;
import com.ap.menabev.dto.InvoiceHeaderDashBoardDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.InvoiceItemAcctAssignmentDto;
import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.dto.InvoiceSubmitDto;
import com.ap.menabev.dto.ItemMessageDto;
import com.ap.menabev.dto.OdataOutPutPayload;
import com.ap.menabev.dto.OdataResponseDto;
import com.ap.menabev.dto.OdataResultObject;
import com.ap.menabev.dto.PoDocumentItem;
import com.ap.menabev.dto.PurchaseOrderRemediationInput;
import com.ap.menabev.dto.RemediationUser;
import com.ap.menabev.dto.RemediationUserDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.RuleInputDto;
import com.ap.menabev.dto.RuleInputProcessLeadDto;
import com.ap.menabev.dto.TriggerWorkflowContext;
import com.ap.menabev.dto.WorkflowContextDto;
import com.ap.menabev.dto.WorkflowTaskOutputDto;
import com.ap.menabev.entity.ActivityLogDo;
import com.ap.menabev.entity.AttachmentDo;
import com.ap.menabev.entity.CommentDo;
import com.ap.menabev.entity.CostAllocationDo;
import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.entity.InvoiceItemAcctAssignmentDo;
import com.ap.menabev.entity.InvoiceItemDo;
import com.ap.menabev.invoice.ActivityLogRepository;
import com.ap.menabev.invoice.AttachmentRepository;
import com.ap.menabev.invoice.CommentRepository;
import com.ap.menabev.invoice.CostAllocationRepository;
import com.ap.menabev.invoice.InvoiceHeaderRepoFilter;
import com.ap.menabev.invoice.InvoiceHeaderRepository;
import com.ap.menabev.invoice.InvoiceItemAcctAssignmentRepository;
import com.ap.menabev.invoice.InvoiceItemRepository;
import com.ap.menabev.invoice.ReasonForRejectionRepository;
import com.ap.menabev.invoice.StatusConfigRepository;
import com.ap.menabev.service.InvoiceHeaderService;
import com.ap.menabev.service.InvoiceItemService;
import com.ap.menabev.service.RuleConstants;
import com.ap.menabev.service.SequenceGeneratorService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.DestinationReaderUtil;
import com.ap.menabev.util.HelperClass;
import com.ap.menabev.util.MenabevApplicationConstant;
import com.ap.menabev.util.ObjectMapperUtils;
import com.ap.menabev.util.OdataHelperClass;
import com.ap.menabev.util.ServiceUtil;
import com.ap.menabev.util.WorkflowConstants;
import com.google.gson.Gson;
@Transactional(rollbackFor = Exception.class)
@Service
public class InvoiceHeaderServiceImpl implements InvoiceHeaderService {

	private static final Logger logger = LoggerFactory.getLogger(InvoiceHeaderServiceImpl.class);

	@Autowired
	InvoiceItemServiceImpl invoiceItemServiceImpl;

	@Autowired
	InvoiceHeaderRepoFilter invoiceHeaderRepoFilter;
	
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
	ReasonForRejectionRepository rejectionRepository;

	@Autowired
	InvoiceItemAcctAssignmentRepository invoiceItemAcctAssignmentRepository;
	@Autowired
	InvoiceItemAcctAssignmentServiceImpl invoiceItemAcctAssignmentServiceImpl;
	
	@Autowired
	ActivityLogRepository activityLogRepo;

	@Autowired
	private SequenceGeneratorService seqService;
	@Autowired
	CostAllocationServiceImpl costAllocationServiceImpl;

	public static final String CONTENT_TYPE = "Content-Type";
	public static final String AUTHORIZATION = "Authorization";

	

	@Override
	public ResponseEntity<?> deleteDraft(List<String> requestId) {

		try {
			requestId.stream().forEach(reqId -> {
				invoiceHeaderRepository.deleteInvoiceHeader(reqId);
				invoiceItemRepository.deleteTotalItems(reqId);
				costAllocationRepository.deleteCostAllocationDo(reqId);
				invoiceItemAcctAssignmentRepository.deleteByRequestIdItemId(reqId);
			});

			ResponseDto reponse = new ResponseDto();
			reponse.setCode("200");
			reponse.setMessage("Deleted Draft SucessFully");
			reponse.setStatus("Sucess");
			return new ResponseEntity<ResponseDto>(reponse, HttpStatus.OK);
		} catch (Exception e) {

			ResponseDto reponse = new ResponseDto();
			reponse.setCode("500");
			reponse.setMessage("Deleted Failed " + e.toString());
			reponse.setStatus("Sucess");
			return new ResponseEntity<ResponseDto>(reponse, HttpStatus.OK);
		}

	}


@Override
	public InvoiceHeaderDto saveAPI(InvoiceHeaderDto dto) {
		InvoiceChangeIndicator changeIndicators = dto.getChangeIndicators();
		List<HeaderMessageDto> headerMessageDtoList = new ArrayList<HeaderMessageDto>();
		String requestId = null;
		try {
			if (!ServiceUtil.isEmpty(changeIndicators)) {
				boolean isHeaderChange = changeIndicators.isHeaderChange();
				boolean isAttachementsChange = changeIndicators.isAttachementsChange();
				boolean isActivityLog = changeIndicators.isActivityLog();
				boolean isCommentChange = changeIndicators.isCommentChange();
				boolean isCostAllocationChange = changeIndicators.isCostAllocationChange();
				boolean isItemChange = changeIndicators.isItemChange();

				if (!(isActivityLog && isAttachementsChange && isHeaderChange && isCommentChange
						&& isCostAllocationChange && isItemChange)) {
					HeaderMessageDto headerMessageDto = new HeaderMessageDto();
					headerMessageDto.setMessageId(1);
					headerMessageDto.setMessageNumber("01");
					headerMessageDto.setMessageText("No Changes were made to DB");
					headerMessageDto.setMessageType("");
					headerMessageDto.setMsgClass("Invoice Header");
					headerMessageDtoList.add(headerMessageDto);

				}

				if (isHeaderChange) {
					HeaderMessageDto headerMessageDto = new HeaderMessageDto();

					if (!ServiceUtil.isEmpty(dto.getRequestId())) {
						requestId = dto.getRequestId();
						InvoiceHeaderDo invoiceHeaderDo = ObjectMapperUtils.map(dto, InvoiceHeaderDo.class);
						invoiceHeaderDo.setUpdatedAt(ServiceUtil.getEpocTime());
						InvoiceHeaderDo invoiceSavedDo = invoiceHeaderRepository.save(invoiceHeaderDo);
						headerMessageDto.setMessageText("Header Fileds updated");
						headerMessageDto.setMessageType("Updated");
					} else {
						requestId = seqService.getSequenceNoByMappingId(MenabevApplicationConstant.INVOICE_SEQUENCE,
								"INV");
						InvoiceHeaderDo invoiceHeaderDo = ObjectMapperUtils.map(dto, InvoiceHeaderDo.class);
						invoiceHeaderDo.setRequestId(requestId);
						invoiceHeaderDo.setGuid(UUID.randomUUID().toString());
						if (!ServiceUtil.isEmpty(dto.getInvoiceStatus())) {
							invoiceHeaderDo.setInvoiceStatus("OPEN");
						}
						InvoiceHeaderDo invoiceSavedDo = invoiceHeaderRepository.save(invoiceHeaderDo);
						headerMessageDto.setMessageText("Header Fileds saved");
						headerMessageDto.setMessageType("Saved");
					}
					headerMessageDto.setMessageId(2);
					headerMessageDto.setMessageNumber("02");

					headerMessageDto.setMsgClass("Invoice Header");
					dto.setHeaderMessages(headerMessageDtoList);
				}
				if (isItemChange) {
					// call 3-way match
					// check in if if three way matched
					List<InvoiceItemDto> itemList = dto.getInvoiceItems();
					if (!ServiceUtil.isEmpty(itemList)) {
						for (InvoiceItemDto invoiceItemDto : itemList) {
							InvoiceItemDo itemDo = ObjectMapperUtils.map(invoiceItemDto, InvoiceItemDo.class);
							if (!ServiceUtil.isEmpty(itemDo.getGuid())) {
								logger.error("line no 263 of InvoiceHeaderServiceImpl.saveOrUpdate()");
								itemDo.setUpdatedAt(ServiceUtil.getEpocTime());
								invoiceItemRepository.save(itemDo);
							} else {
								requestId = dto.getRequestId();
								logger.error("line no 267 of InvoiceHeaderServiceImpl.saveOrUpdate()");
								itemDo.setRequestId(requestId);
								itemDo.setGuid(UUID.randomUUID().toString());
								itemDo.setItemCode(invoiceItemServiceImpl.getItemId(requestId));
								// itemDo.setItem(invoiceItemServiceImpl.getItemId());
								logger.error("line no 271 of InvoiceHeaderServiceImpl.saveOrUpdate()");
								logger.error("line no 273 of InvoiceHeaderServiceImpl.saveOrUpdate()"
										+ itemDo.getItemCode());
								invoiceItemRepository.save(itemDo);
							}
							List<InvoiceItemAcctAssignmentDto> itemAccAssignmentList = invoiceItemDto
									.getInvItemAcctDtoList();
							if (!ServiceUtil.isEmpty(itemAccAssignmentList)) {

								for (InvoiceItemAcctAssignmentDto accDto : itemAccAssignmentList) {
									InvoiceItemAcctAssignmentDo accDo = ObjectMapperUtils.map(accDto,
											InvoiceItemAcctAssignmentDo.class);
									if (!ServiceUtil.isEmpty(accDo.getAccountAssgnGuid())) {
										invoiceItemAcctAssignmentRepository.save(accDo);
									} else {
										accDo.setAccountAssgnGuid(UUID.randomUUID().toString());
										accDo.setItemId(itemDo.getItemCode());
										accDo.setSerialNo(invoiceItemAcctAssignmentServiceImpl.getSerialNo(requestId,
												itemDo.getItemCode()));
										accDo.setRequestId(requestId);
										// accDo.setCreatedOn(ServiceUtil.getEpocTime());
										invoiceItemAcctAssignmentRepository.save(accDo);
									}
								}
							}

						}

					}

				}
				if (isCommentChange) {
					HeaderMessageDto headerMessageDto = null;
					headerMessageDto = new HeaderMessageDto();
					if (!ServiceUtil.isEmpty(dto.getRequestId())) {
						requestId = dto.getRequestId();
						InvoiceHeaderDo invoiceHeaderDo = ObjectMapperUtils.map(dto, InvoiceHeaderDo.class);
						invoiceHeaderDo.setUpdatedAt(ServiceUtil.getEpocTime());
						InvoiceHeaderDo invoiceSavedDo = invoiceHeaderRepository.save(invoiceHeaderDo);
						headerMessageDto.setMessageText("Header Updated");
						headerMessageDto.setMessageType("Updated");
						headerMessageDto.setMessageId(2);
						headerMessageDto.setMessageNumber("02");
						headerMessageDto.setMsgClass("Header");
						headerMessageDtoList.add(headerMessageDto);
					} else {
						requestId = seqService.getSequenceNoByMappingId(MenabevApplicationConstant.INVOICE_SEQUENCE,
								"INV");
						InvoiceHeaderDo invoiceHeaderDo = ObjectMapperUtils.map(dto, InvoiceHeaderDo.class);
						invoiceHeaderDo.setRequestId(requestId);
						invoiceHeaderDo.setGuid(UUID.randomUUID().toString());
						if (!ServiceUtil.isEmpty(dto.getInvoiceStatus())) {
							invoiceHeaderDo.setInvoiceStatus("OPEN");
						}
						InvoiceHeaderDo invoiceSavedDo = invoiceHeaderRepository.save(invoiceHeaderDo);
						headerMessageDto.setMessageText("Header Saved");
						headerMessageDto.setMessageType("Saved");
						headerMessageDto.setMessageId(2);
						headerMessageDto.setMessageNumber("02");
						headerMessageDto.setMsgClass("Header");
						headerMessageDtoList.add(headerMessageDto);

					}

					List<CommentDto> commentsList = dto.getComment();
					if (!ServiceUtil.isEmpty(commentsList)) {
						headerMessageDto = new HeaderMessageDto();
						for (CommentDto commentDto : commentsList) {
							if (!ServiceUtil.isEmpty(commentDto.getCommentId())) {
								commentDto.setUpdatedAt(ServiceUtil.getEpocTime());
								CommentDo commentDo = ObjectMapperUtils.map(commentDto, CommentDo.class);
								commentRepository.save(commentDo);
								headerMessageDto.setMessageText("Comments Updated");
								headerMessageDto.setMessageType("Updated");
								headerMessageDto.setMessageId(9);
								headerMessageDto.setMessageNumber("09");
								headerMessageDto.setMsgClass("Comments");
								headerMessageDtoList.add(headerMessageDto);
							} else {
								commentDto.setCommentId(UUID.randomUUID().toString());
								commentDto.setCreatedAt(ServiceUtil.getEpocTime());
								commentDto.setRequestId(requestId);
								CommentDo commentDo = ObjectMapperUtils.map(commentDto, CommentDo.class);
								commentRepository.save(commentDo);
								headerMessageDto.setMessageText("Comments Saved");
								headerMessageDto.setMessageType("Saved");
								headerMessageDto.setMessageId(9);
								headerMessageDto.setMessageNumber("09");
								headerMessageDto.setMsgClass("Comments");
								headerMessageDtoList.add(headerMessageDto);
							}
						}

					}
				}

				changeIndicators.setActivityLog(false);
				changeIndicators.setAttachementsChange(false);
				changeIndicators.setCommentChange(false);
				changeIndicators.setCostAllocationChange(false);
				changeIndicators.setHeaderChange(false);
				changeIndicators.setItemChange(false);
				dto.setChangeIndicators(changeIndicators);

			} else {

				HeaderMessageDto headerMessageDto = new HeaderMessageDto();
				headerMessageDto.setMessageId(1);
				headerMessageDto.setMessageNumber("01");
				headerMessageDto.setMessageText("No Changes were made to DB");
				headerMessageDto.setMessageType("");
				headerMessageDto.setMsgClass("Invoice Header");
				headerMessageDtoList.add(headerMessageDto);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			HeaderMessageDto headerMessageDto = new HeaderMessageDto();
			headerMessageDto.setMessageId(0);
			headerMessageDto.setMessageNumber("00");
			headerMessageDto.setMessageText(e.getMessage());
			headerMessageDto.setMessageType("Error in save api");
			headerMessageDto.setMsgClass("Error");
			headerMessageDtoList.add(headerMessageDto);
		}
		dto.setHeaderMessages(headerMessageDtoList);
		return dto;
	}

	@Override
	public ResponseDto saveOrUpdate(InvoiceHeaderDto dto) {
		ResponseDto response = new ResponseDto();
		StringBuilder messageBuilder = new StringBuilder();
		try {
			String requestId = null;
			if (!ServiceUtil.isEmpty(dto.getRequestId())) {
				requestId = dto.getRequestId();
				InvoiceHeaderDo invoiceHeaderDo = ObjectMapperUtils.map(dto, InvoiceHeaderDo.class);
				invoiceHeaderDo.setUpdatedAt(ServiceUtil.getEpocTime());
				InvoiceHeaderDo invoiceSavedDo = invoiceHeaderRepository.save(invoiceHeaderDo);
			} else {
				requestId = seqService.getSequenceNoByMappingId(MenabevApplicationConstant.INVOICE_SEQUENCE, "INV");
				InvoiceHeaderDo invoiceHeaderDo = ObjectMapperUtils.map(dto, InvoiceHeaderDo.class);
				invoiceHeaderDo.setRequestId(requestId);
				invoiceHeaderDo.setGuid(UUID.randomUUID().toString());
				if (!ServiceUtil.isEmpty(dto.getInvoiceStatus())) {
					invoiceHeaderDo.setInvoiceStatus("OPEN");
				}
				InvoiceHeaderDo invoiceSavedDo = invoiceHeaderRepository.save(invoiceHeaderDo);
			}

			// save invoice header.

			// save item
			List<InvoiceItemDto> itemList = dto.getInvoiceItems();
			if (!ServiceUtil.isEmpty(itemList)) {
				for (InvoiceItemDto invoiceItemDto : itemList) {
					InvoiceItemDo itemDo = ObjectMapperUtils.map(invoiceItemDto, InvoiceItemDo.class);
					if (!ServiceUtil.isEmpty(itemDo.getGuid())) {
						logger.error("line no 263 of InvoiceHeaderServiceImpl.saveOrUpdate()");
						itemDo.setUpdatedAt(ServiceUtil.getEpocTime());
						invoiceItemRepository.save(itemDo);
					} else {
						logger.error("line no 267 of InvoiceHeaderServiceImpl.saveOrUpdate()");
						itemDo.setRequestId(requestId);
						itemDo.setGuid(UUID.randomUUID().toString());
						itemDo.setItemCode(invoiceItemServiceImpl.getItemId(requestId));
						//itemDo.setItem(invoiceItemServiceImpl.getItemId());
						logger.error("line no 271 of InvoiceHeaderServiceImpl.saveOrUpdate()");
						logger.error("line no 273 of InvoiceHeaderServiceImpl.saveOrUpdate()"+itemDo.getItemCode());
						invoiceItemRepository.save(itemDo);
					}
					List<InvoiceItemAcctAssignmentDto> itemAccAssignmentList = invoiceItemDto.getInvItemAcctDtoList();
					if (!ServiceUtil.isEmpty(itemAccAssignmentList)) {

						for (InvoiceItemAcctAssignmentDto accDto : itemAccAssignmentList) {
							InvoiceItemAcctAssignmentDo accDo = ObjectMapperUtils.map(accDto,
									InvoiceItemAcctAssignmentDo.class);
							if (!ServiceUtil.isEmpty(accDo.getAccountAssgnGuid())) {
								invoiceItemAcctAssignmentRepository.save(accDo);
							} else {
								accDo.setAccountAssgnGuid(UUID.randomUUID().toString());
								accDo.setItemId(itemDo.getItemCode());
								accDo.setSerialNo(invoiceItemAcctAssignmentServiceImpl.getSerialNo(requestId,
										itemDo.getItemCode()));
								accDo.setRequestId(requestId);
								//accDo.setCreatedOn(ServiceUtil.getEpocTime());
								invoiceItemAcctAssignmentRepository.save(accDo);
							}
						}
					}

				}

			}
			List<CostAllocationDto> costAllocationDtoList = dto.getCostAllocation();

			if (!ServiceUtil.isEmpty(costAllocationDtoList)) {
				// save cost allocation

				for (CostAllocationDto costAllocationDto : costAllocationDtoList) {
					if (!ServiceUtil.isEmpty(costAllocationDto.getCostAllocationId())) {
						CostAllocationDo costAllocationDo = ObjectMapperUtils.map(costAllocationDto,
								CostAllocationDo.class);
						costAllocationRepository.save(costAllocationDo);
					} else {
						CostAllocationDo costAllocationDo = ObjectMapperUtils.map(costAllocationDto,
								CostAllocationDo.class);
						costAllocationDo.setCostAllocationId(UUID.randomUUID().toString());
						costAllocationDo.setItemId(costAllocationServiceImpl.getItemID(requestId));
						costAllocationDo.setRequestId(requestId);
						costAllocationRepository.save(costAllocationDo);
					}

				}

			}

			List<AttachmentDto> attachemenListDto = dto.getAttachment();

			if (!ServiceUtil.isEmpty(attachemenListDto)) {
				for (AttachmentDto attachmentDto : attachemenListDto) {
					if (!ServiceUtil.isEmpty(attachmentDto.getAttachmentId())) {
						attachmentDto.setUpdatedAt(ServiceUtil.getEpocTime());
						AttachmentDo attachmentDo = ObjectMapperUtils.map(attachmentDto, AttachmentDo.class);
						attachmentRepository.save(attachmentDo);
					} else {
						attachmentDto.setAttachmentId(UUID.randomUUID().toString());
						attachmentDto.setRequestId(requestId);
						attachmentDto.setCreatedAt(ServiceUtil.getEpocTime());
						AttachmentDo attachmentDo = ObjectMapperUtils.map(attachmentDto, AttachmentDo.class);
						attachmentRepository.save(attachmentDo);
					}
				}

			}
			List<CommentDto> commentsList = dto.getComment();
			if (!ServiceUtil.isEmpty(commentsList)) {

				for (CommentDto commentDto : commentsList) {
					if (!ServiceUtil.isEmpty(commentDto.getCommentId())) {
						commentDto.setUpdatedAt(ServiceUtil.getEpocTime());
						CommentDo commentDo = ObjectMapperUtils.map(commentDto, CommentDo.class);
						commentRepository.save(commentDo);
					} else {
						commentDto.setCommentId(UUID.randomUUID().toString());
						commentDto.setCreatedAt(ServiceUtil.getEpocTime());
						commentDto.setRequestId(requestId);
						CommentDo commentDo = ObjectMapperUtils.map(commentDto, CommentDo.class);
						commentRepository.save(commentDo);
					}
				}
			}
			
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage("Success");
			return response;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage("Error"+e.getMessage());
			return response;
		}

	}

	// create invoice for PO/NON po
	@Override
	public ResponseEntity<?> save(CreateInvoiceHeaderDto invoiceDto) {
		try {

			String requestId = null;
			String guid = null;
			InvoiceItemDo itemDo = null;
			// Generate Request Id
			if (invoiceDto.getInvoiceHeaderDto().getRequestId() != null
					&& !invoiceDto.getInvoiceHeaderDto().getRequestId().isEmpty()) {
				requestId = invoiceDto.getInvoiceHeaderDto().getRequestId();
				guid = invoiceDto.getInvoiceHeaderDto().getGuid();
			} else {
				requestId = seqService.getSequenceNoByMappingId(MenabevApplicationConstant.INVOICE_SEQUENCE, "INV");
				guid = UUID.randomUUID().toString();
				
			}
			// save header
			 invoiceDto.getInvoiceHeaderDto().setRequestId(requestId);
			 invoiceDto.getInvoiceHeaderDto().setGuid(guid);
			if (ServiceUtil.isEmpty(invoiceDto.getInvoiceHeaderDto().getInvoiceStatus())) {
				invoiceDto.getInvoiceHeaderDto().setInvoiceStatus("DRAFT");
				invoiceDto.getInvoiceHeaderDto().setTaskStatus("DRAFT");
			}
			DecimalFormat decim = new DecimalFormat("#.##");
			Double price2 = Double.parseDouble(decim.format(invoiceDto.getInvoiceHeaderDto().getInvoiceTotal().doubleValue()));
			invoiceDto.getInvoiceHeaderDto().setInvoiceTotal(price2);
			InvoiceHeaderDo invoiceHeaderDo = ObjectMapperUtils.map(invoiceDto.getInvoiceHeaderDto(),
					InvoiceHeaderDo.class);
			System.err.println("line no 371 of InvoiceHeaderServiceImpl.saveOrUpdate()"+invoiceHeaderDo);
			InvoiceHeaderDo invoiceSavedDo = invoiceHeaderRepository.save(invoiceHeaderDo);
			// save invoice item
			if (invoiceDto.getInvoiceHeaderDto().getInvoiceItems() != null
					&& !invoiceDto.getInvoiceHeaderDto().getInvoiceItems().isEmpty()) {
				List<InvoiceItemDto> itemlists = invoiceDto.getInvoiceHeaderDto().getInvoiceItems();
				for (InvoiceItemDto invoiceItemDto : itemlists) {
					
					if (!ServiceUtil.isEmpty(invoiceItemDto.getGuid())) {
						 itemDo = ObjectMapperUtils.map(invoiceItemDto, InvoiceItemDo.class);
					System.err.println("line no 380 of InvoiceHeaderServiceImpl.saveOrUpdate()"+itemDo);
						itemDo.setUpdatedAt(ServiceUtil.getEpocTime());
						invoiceItemRepository.save(itemDo);
					} else {
					
						invoiceItemDto.setRequestId(requestId);
						invoiceItemDto.setGuid(UUID.randomUUID().toString());
						invoiceItemDto.setItemCode(invoiceItemServiceImpl.getItemId(requestId));
						//itemDo.setItem(invoiceItemServiceImpl.getItemId());
						 itemDo = ObjectMapperUtils.map(invoiceItemDto, InvoiceItemDo.class);
						System.err.println("line no 271 of InvoiceHeaderServiceImpl.saveOrUpdate()"+itemDo);
						logger.error("line no 273 of InvoiceHeaderServiceImpl.saveOrUpdate()"+itemDo.getItemCode());
						itemDo = invoiceItemRepository.save(itemDo);
					}
					if (invoiceDto.getInvoiceHeaderDto().getInvoiceItems() != null
							&& !invoiceDto.getInvoiceHeaderDto().getInvoiceItems().isEmpty()){
								List<InvoiceItemAcctAssignmentDto> itemAccAssignmentList = invoiceItemDto.getInvItemAcctDtoList();
						for (InvoiceItemAcctAssignmentDto accDto : itemAccAssignmentList) {
							
							if (!ServiceUtil.isEmpty(accDto.getAccountAssgnGuid())) {
								InvoiceItemAcctAssignmentDo accDo = ObjectMapperUtils.map(accDto,
										InvoiceItemAcctAssignmentDo.class);
								invoiceItemAcctAssignmentRepository.save(accDo);
							} else {
								accDto.setAccountAssgnGuid(UUID.randomUUID().toString());
								accDto.setItemId(itemDo.getItemCode());
								accDto.setSerialNo(invoiceItemAcctAssignmentServiceImpl.getSerialNo(requestId,
										itemDo.getItemCode()));
								accDto.setRequestId(requestId);
								System.err.println("line no 407 of InvoiceHeaderServiceImpl.saveOrUpdate()"+accDto);		
								InvoiceItemAcctAssignmentDo accDo = ObjectMapperUtils.map(accDto,
										InvoiceItemAcctAssignmentDo.class);
								invoiceItemAcctAssignmentRepository.save(accDo);
							}
						}
					}
			}
			}
			if (invoiceDto.getInvoiceHeaderDto().getCostAllocation() != null && !invoiceDto.getInvoiceHeaderDto().getCostAllocation().isEmpty()) {
				
				List<CostAllocationDto> costAllocationDtoList = invoiceDto.getInvoiceHeaderDto().getCostAllocation();

				if (!ServiceUtil.isEmpty(costAllocationDtoList)) {
					// save cost allocation

					for (CostAllocationDto costAllocationDto : costAllocationDtoList) {
						if (!ServiceUtil.isEmpty(costAllocationDto.getCostAllocationId())) {
							CostAllocationDo costAllocationDo = ObjectMapperUtils.map(costAllocationDto,
									CostAllocationDo.class);
							costAllocationRepository.save(costAllocationDo);
						} else {
							
							costAllocationDto.setCostAllocationId(UUID.randomUUID().toString());
							costAllocationDto.setItemId(costAllocationServiceImpl.getItemID(requestId));
							costAllocationDto.setRequestId(requestId);
							
							CostAllocationDo costAllocationDo = ObjectMapperUtils.map(costAllocationDto,
									CostAllocationDo.class);
							System.err.println("line no 432 of InvoiceHeaderServiceImpl.saveOrUpdate()"+costAllocationDo);	
							costAllocationRepository.save(costAllocationDo);
						}
					}
				}
			}
			if (invoiceDto.getInvoiceHeaderDto().getAttachment() != null
					&& !invoiceDto.getInvoiceHeaderDto().getAttachment().isEmpty()) {
				List<AttachmentDto> attachemenListDto = invoiceDto.getInvoiceHeaderDto().getAttachment();
					for (AttachmentDto attachmentDto : attachemenListDto) {
						if (!ServiceUtil.isEmpty(attachmentDto.getAttachmentId())) {
							attachmentDto.setUpdatedAt(ServiceUtil.getEpocTime());
							AttachmentDo attachmentDo = ObjectMapperUtils.map(attachmentDto, AttachmentDo.class);
							attachmentRepository.save(attachmentDo);
						} else {
							attachmentDto.setAttachmentId(UUID.randomUUID().toString());
							attachmentDto.setRequestId(requestId);
							attachmentDto.setCreatedAt(ServiceUtil.getEpocTime());
							AttachmentDo attachmentDo = ObjectMapperUtils.map(attachmentDto, AttachmentDo.class);
							System.err.println("line no 451 of InvoiceHeaderServiceImpl.saveOrUpdate()"+attachmentDo);
							attachmentRepository.save(attachmentDo);
						}
					}
			}
			if (invoiceDto.getInvoiceHeaderDto().getComment() != null
					&& !invoiceDto.getInvoiceHeaderDto().getComment().isEmpty()) {
				
				List<CommentDto> commentsList = invoiceDto.getInvoiceHeaderDto().getComment();
				if (!ServiceUtil.isEmpty(commentsList)) {

					for (CommentDto commentDto : commentsList) {
						if (!ServiceUtil.isEmpty(commentDto.getCommentId())) {
							commentDto.setUpdatedAt(ServiceUtil.getEpocTime());
							CommentDo commentDo = ObjectMapperUtils.map(commentDto, CommentDo.class);
							commentRepository.save(commentDo);
						} else {
							commentDto.setCommentId(UUID.randomUUID().toString());
							commentDto.setCreatedAt(ServiceUtil.getEpocTime());
							commentDto.setRequestId(requestId);
							CommentDo commentDo = ObjectMapperUtils.map(commentDto, CommentDo.class);
							System.err.println("line no 472 of InvoiceHeaderServiceImpl.saveOrUpdate()"+commentDo);
							commentRepository.save(commentDo);
						}
					}
				}
			}
			invoiceDto.getInvoiceHeaderDto().setRequestId(requestId);
			invoiceDto.getInvoiceHeaderDto().setGuid(guid);
			invoiceDto.setResponseStatus("Invoice " + invoiceSavedDo.getRequestId() + " saved as draft.");
			invoiceDto.setStatus(200);
			return new ResponseEntity<CreateInvoiceHeaderDto>(invoiceDto, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed due to " + e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Submit invoice
	@Override
	public ResponseEntity<?> submitForNonPo(CreateInvoiceHeaderDto invoiceDto) {
		try {
			// Flow is for Non PO Accountant created invoice
			// Determine RUle for Process lead
			String requestId = null;
			String guid = "";
			AcountOrProcessLeadDetermination determination = new AcountOrProcessLeadDetermination();
			determination.setCompCode("1010");
			determination.setProcessLeadCheck("Process Lead");
			determination.setVednorId(invoiceDto.getInvoiceHeaderDto().getVendorId());
			ResponseEntity<?> responseRules = triggerRuleService(determination);
			InvoiceItemDo itemDo = null;
			System.err.println("responseFrom Rules " + responseRules);
			if (responseRules.getStatusCodeValue() == 200) {
				@SuppressWarnings("unchecked")
				List<ApproverDataOutputDto> lists = (List<ApproverDataOutputDto>) responseRules.getBody();

				// Generate Request Id
				if (invoiceDto.getInvoiceHeaderDto().getRequestId() != null
						&& !invoiceDto.getInvoiceHeaderDto().getRequestId().isEmpty()) {
					requestId = invoiceDto.getInvoiceHeaderDto().getRequestId();
					guid = invoiceDto.getInvoiceHeaderDto().getGuid();
				} else {
					requestId = seqService.getSequenceNoByMappingId(MenabevApplicationConstant.INVOICE_SEQUENCE, "INV");
					guid = UUID.randomUUID().toString();
					
				}
				// Trigger worklfow , which will direct to process lead
				TriggerWorkflowContext context = new TriggerWorkflowContext();
				context.setRequestId(requestId);
				context.setInvoiceReferenceNumber(invoiceDto.getInvoiceHeaderDto().getExtInvNum());
				context.setNonPo(true);
				context.setManualNonPo(true);
				context.setAccountantUser(invoiceDto.getInvoiceHeaderDto().getTaskOwner());
				context.setAccountantGroup(invoiceDto.getInvoiceHeaderDto().getTaskGroup());
				context.setProcessLead(lists.get(0).getUserOrGroup());

				ResponseEntity<?> response = triggerWorkflow((WorkflowContextDto) context,
						"triggerresolutionworkflow.triggerresolutionworkflow");
				System.err.println("response of workflow Trigger " + response);
				WorkflowTaskOutputDto taskOutputDto = (WorkflowTaskOutputDto) response.getBody();
				// save invoice header
				
				invoiceDto.getInvoiceHeaderDto().setRequestId(requestId);
				invoiceDto.getInvoiceHeaderDto().setGuid(guid);
				invoiceDto.getInvoiceHeaderDto().setInvoiceStatus("OPEN");
				invoiceDto.getInvoiceHeaderDto().setTaskStatus("READY");
				invoiceDto.getInvoiceHeaderDto().setWorkflowId(taskOutputDto.getId());
				invoiceDto.getInvoiceHeaderDto().setRequest_created_at((ServiceUtil.getEpocTime()));
				invoiceDto.getInvoiceHeaderDto().setTaskOwner(lists.get(0).getUserOrGroup());
				DecimalFormat decim = new DecimalFormat("#.##");
				Double price2 = Double.parseDouble(decim.format(invoiceDto.getInvoiceHeaderDto().getInvoiceTotal().doubleValue()));
				invoiceDto.getInvoiceHeaderDto().setInvoiceTotal(price2);
				InvoiceHeaderDo invoiceHeaderDo = ObjectMapperUtils.map(invoiceDto.getInvoiceHeaderDto(),
						InvoiceHeaderDo.class);
				InvoiceHeaderDo invoiceSavedDo = invoiceHeaderRepository.save(invoiceHeaderDo);
				// save invoice item
				if (invoiceDto.getInvoiceHeaderDto().getInvoiceItems() != null
						&& !invoiceDto.getInvoiceHeaderDto().getInvoiceItems().isEmpty()) {
					List<InvoiceItemDto> itemlists = invoiceDto.getInvoiceHeaderDto().getInvoiceItems();
					for (InvoiceItemDto invoiceItemDto : itemlists) {
						
						if (!ServiceUtil.isEmpty(invoiceItemDto.getGuid())) {
							 itemDo = ObjectMapperUtils.map(invoiceItemDto, InvoiceItemDo.class);
							itemDo.setUpdatedAt(ServiceUtil.getEpocTime());
							invoiceItemRepository.save(itemDo);
						} else {
							invoiceItemDto.setRequestId(requestId);
							invoiceItemDto.setGuid(UUID.randomUUID().toString());
							invoiceItemDto.setItemCode(invoiceItemServiceImpl.getItemId(requestId));
							itemDo = ObjectMapperUtils.map(invoiceItemDto, InvoiceItemDo.class);
							//itemDo.setItem(invoiceItemServiceImpl.getItemId());
							invoiceItemRepository.save(itemDo);
						}
						List<InvoiceItemAcctAssignmentDto> itemAccAssignmentList = invoiceItemDto.getInvItemAcctDtoList();
						if (!ServiceUtil.isEmpty(itemAccAssignmentList)) {
							for (InvoiceItemAcctAssignmentDto accDto : itemAccAssignmentList) {
								InvoiceItemAcctAssignmentDo accDo = ObjectMapperUtils.map(accDto,
										InvoiceItemAcctAssignmentDo.class);
								if (!ServiceUtil.isEmpty(accDo.getAccountAssgnGuid())) {
									invoiceItemAcctAssignmentRepository.save(accDo);
								} else {
									accDo.setAccountAssgnGuid(UUID.randomUUID().toString());
									accDo.setItemId(itemDo.getItemCode());
									accDo.setSerialNo(invoiceItemAcctAssignmentServiceImpl.getSerialNo(requestId,
											itemDo.getItemCode()));
									accDo.setRequestId(requestId);
									invoiceItemAcctAssignmentRepository.save(accDo);
								}
							}
						}

					}
				}
				if (invoiceDto.getInvoiceHeaderDto().getCostAllocation() != null && !invoiceDto.getInvoiceHeaderDto().getCostAllocation().isEmpty()) {
					
					List<CostAllocationDto> costAllocationDtoList = invoiceDto.getInvoiceHeaderDto().getCostAllocation();

						// save cost allocation

						for (CostAllocationDto costAllocationDto : costAllocationDtoList) {
							if (!ServiceUtil.isEmpty(costAllocationDto.getCostAllocationId())) {
								CostAllocationDo costAllocationDo = ObjectMapperUtils.map(costAllocationDto,
										CostAllocationDo.class);
								costAllocationRepository.save(costAllocationDo);
							} else {
								CostAllocationDo costAllocationDo = ObjectMapperUtils.map(costAllocationDto,
										CostAllocationDo.class);
								costAllocationDo.setCostAllocationId(UUID.randomUUID().toString());
								costAllocationDo.setItemId(costAllocationServiceImpl.getItemID(requestId));
								costAllocationDo.setRequestId(requestId);
								costAllocationRepository.save(costAllocationDo);
							}
						}
				}
				if (invoiceDto.getInvoiceHeaderDto().getAttachment() != null
						&& !invoiceDto.getInvoiceHeaderDto().getAttachment().isEmpty()) {
					List<AttachmentDto> attachemenListDto = invoiceDto.getInvoiceHeaderDto().getAttachment();
						for (AttachmentDto attachmentDto : attachemenListDto) {
							if (!ServiceUtil.isEmpty(attachmentDto.getAttachmentId())) {
								attachmentDto.setUpdatedAt(ServiceUtil.getEpocTime());
								AttachmentDo attachmentDo = ObjectMapperUtils.map(attachmentDto, AttachmentDo.class);
								attachmentRepository.save(attachmentDo);
							} else {
								attachmentDto.setAttachmentId(UUID.randomUUID().toString());
								attachmentDto.setRequestId(requestId);
								attachmentDto.setCreatedAt(ServiceUtil.getEpocTime());
								AttachmentDo attachmentDo = ObjectMapperUtils.map(attachmentDto, AttachmentDo.class);
								attachmentRepository.save(attachmentDo);
							}
						}
				}
				if (invoiceDto.getInvoiceHeaderDto().getComment() != null
						&& !invoiceDto.getInvoiceHeaderDto().getComment().isEmpty()) {
					
					List<CommentDto> commentsList = invoiceDto.getInvoiceHeaderDto().getComment();
				

						for (CommentDto commentDto : commentsList) {
							if (!ServiceUtil.isEmpty(commentDto.getCommentId())) {
								commentDto.setUpdatedAt(ServiceUtil.getEpocTime());
								CommentDo commentDo = ObjectMapperUtils.map(commentDto, CommentDo.class);
								commentRepository.save(commentDo);
							} else {
								commentDto.setCommentId(UUID.randomUUID().toString());
								commentDto.setCreatedAt(ServiceUtil.getEpocTime());
								commentDto.setRequestId(requestId);
								CommentDo commentDo = ObjectMapperUtils.map(commentDto, CommentDo.class);
								commentRepository.save(commentDo);
							}
						}
				}
				// Update the Activity Log table
				//createActivityLog(requestId);
				if (lists.get(0).getUserType().equals("Default")) {
					invoiceDto.setResponseStatus("Invoice " + invoiceSavedDo.getRequestId()
							+ " has been created & task is available to Default User " + lists.get(0).getUserOrGroup());
				} else {
					invoiceDto.setResponseStatus("Invoice " + invoiceSavedDo.getRequestId()
							+ " has been created & task is available to User " + lists.get(0).getUserOrGroup());
				}
				invoiceDto.setStatus(200);
				return new ResponseEntity<CreateInvoiceHeaderDto>(invoiceDto, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(responseRules, HttpStatus.OK);
			}

		} catch (Exception e) {

			return new ResponseEntity<String>("Failed due to " + e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/*public ResponseEntity<?> createActivityLog(String requestId,InvoiceHeaderDto invoice) {
		
		ActivityLogDto activity = new ActivityLogDto();
		activity.setActionCode("S");
		activity.setActionCodeText("CREATED");
		activity.setActivityId(UUID.randomUUID().toString());
		activity.setCreatedAt(invoice.getRequest_created_at());
		activity.setCreatedBy(invoice.getRequest_created_by());
		activity.setInvoiceStatusCode("");
		
		return null;
	}*/


	@Override
	public ResponseEntity<?> getInboxTaskWithMultipleSearch(FilterMultipleHeaderSearchDto filterDto) {
		try {
			if (filterDto.getSkip()!=0 && filterDto.getTop() != 0) {
				if (!ServiceUtil.isEmpty(filterDto.getAssignedTo())) {
					List<InvoiceHeaderDo> lists = null;
					ResponseEntity<?> responseFromSapApi = fetchWorkflowUserTaksListMultiple(filterDto);
					if (responseFromSapApi.getStatusCodeValue() == HttpStatus.OK.value()) {
						@SuppressWarnings("unchecked")
						List<WorkflowTaskOutputDto> listOfWorkflowTasks = (List<WorkflowTaskOutputDto>) responseFromSapApi
								.getBody();
						logger.error("listOfWorkflowTasks : " + listOfWorkflowTasks);
						System.err.println("listOfWorkflowTasks :" + listOfWorkflowTasks.size());
						// along with his user tasks
						if (!listOfWorkflowTasks.isEmpty()
								|| (listOfWorkflowTasks.isEmpty() && filterDto.getRoleOfUser().equals("Accountant"))) {
							if (filterDto.getRoleOfUser().equals("Accountant")) {
								// than add the requesId with Draft as docStatus
								if (filterDto.getRequestId() != null && !filterDto.getRequestId().isEmpty()) {
									
									lists = invoiceHeaderRepository
											.getInvoiceHeaderDocStatusByUserId(filterDto.getAssignedTo());
									System.err.println("list of drafts for accountant = " + lists);
								}
								if (lists != null && !lists.isEmpty()) {
									for (int i = 0; i < lists.size(); i++) {

										WorkflowTaskOutputDto workflow = new WorkflowTaskOutputDto();
										workflow.setSubject(lists.get(i).getRequestId());
										workflow.setStatus("Draft");
										listOfWorkflowTasks.add(workflow);
									}
								}
							}
							if (!listOfWorkflowTasks.isEmpty() && listOfWorkflowTasks != null) {

								System.err.println("list of workflowIDs  for accountant = " + listOfWorkflowTasks);

								ResponseEntity<?> response = new ResponseEntity<>(
										fetchInvoiceDocHeaderDtoListFromRequestNumberMultiple(listOfWorkflowTasks,
												filterDto),
										HttpStatus.OK);
								return response;

							} else {
								ResponseEntity<?> response = new ResponseEntity<String>("No tasks are available.",
										HttpStatus.OK);
								return response;
							}
						} else {
							ResponseEntity<?> response = new ResponseEntity<String>("No tasks are available.",
									HttpStatus.OK);
							return response;
						}

					} else {
						return responseFromSapApi;
					}
				} else {

					ResponseEntity<?> response = new ResponseEntity<>(
							"INVALID_INPUT" + "Please provide login in user id ", HttpStatus.BAD_REQUEST);
					return response;

				}
			} else {
				ResponseEntity<?> response = new ResponseEntity<>(
						"INVALID_INPUT" + " Provide valid index num and count.", HttpStatus.BAD_REQUEST);
				return response;
			}
		} catch (Exception e) {
			ResponseEntity<?> response = new ResponseEntity<>("EXCEPTION_POST_MSG" + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public ResponseEntity<?> getInboxUserTask(FilterMultipleHeaderSearchDto filterDto){
		
		if (filterDto.getSkip() >=0 && filterDto.getTop()> 0) {
			// check for the filter parameter 
		boolean filterNeeded = 	checkForFilterPrameter(filterDto);
		    List<InvoiceHeaderDo> filteredrequestIds = Collections.EMPTY_LIST;
		    List<InvoiceHeaderDo>  draftLists = Collections.EMPTY_LIST;
		    List<InvoiceHeaderDto>  draftDtoList = Collections.EMPTY_LIST;
		    System.err.println("filterNeeded ="+filterNeeded);
		if(filterNeeded){
			try {
			// call the invoiceHeaderFilter to get all the requestId with filters and Draft , userID loggedin 
			ResponseEntity<?>  responseFilterRequestIds  = filterInvoicesAndGetRequesIds(filterDto);
			if(responseFilterRequestIds.getStatusCodeValue()==200){
				filteredrequestIds  = (List<InvoiceHeaderDo>) responseFilterRequestIds.getBody();
				
				System.err.println("filteredrequestIds ="+filteredrequestIds);
				if(filteredrequestIds !=null && !filteredrequestIds.isEmpty()){
				List<String>   requestIds = filteredrequestIds.stream().map(InvoiceHeaderDo::getRequestId).collect(Collectors.toList());
				if( requestIds!=null && !requestIds.isEmpty()){
			// call the worklfow Api 
			ResponseEntity<?> responseFromSapApi = fetchWorkflowUserTaksListByRequestId(filterDto,requestIds);			
			if (responseFromSapApi.getStatusCodeValue() == HttpStatus.OK.value()){
				List<WorkflowTaskOutputDto> listOfWorkflowTasks = (List<WorkflowTaskOutputDto>) responseFromSapApi
						.getBody();
				System.err.println("listOfWorkflowTasks : filter" + listOfWorkflowTasks);
			// call the invoice table get all task details for above requestIds and as well Draft of users
				if (!listOfWorkflowTasks.isEmpty()
						|| (listOfWorkflowTasks.isEmpty() && filterDto.getRoleOfUser().equals("Accountant"))) {
					if (filterDto.getRoleOfUser().equals("Accountant")) {
						// than add the requesId with Draft as docStatus
							draftLists = filterInvoicesAndGetRequesIdsByDraft(filterDto);
							// cahnges to be done call only to get draft by filter also 
							System.err.println("list of drafts by requestID  by filter " + draftLists);
							if (draftLists != null && !draftLists.isEmpty()) {
							for (int i = 0; i < draftLists.size(); i++) {

								WorkflowTaskOutputDto workflow = new WorkflowTaskOutputDto();
								workflow.setSubject(draftLists.get(i).getRequestId());
								workflow.setStatus("DRAFT");
								listOfWorkflowTasks.add(workflow);
							}
						}
					}
					if (listOfWorkflowTasks != null && !listOfWorkflowTasks.isEmpty() ) {
						
						 List<InboxOutputDto>	paginatedDraftLists = Collections.emptyList();
						System.err.println("listOfWorkflowTasks ="+listOfWorkflowTasks);
						// filter Invoice Header based on listOfWorklfowTasks 
						List<InvoiceHeaderDo>  filteredFromWorklfowTask = filteredrequestIds.stream().filter(f->listOfWorkflowTasks.stream().anyMatch(l->f.getRequestId().equals(l.getSubject()))).collect(Collectors.toList());    
						System.err.println("filteredFromWorklfowTask ="+filteredFromWorklfowTask);
						
						InboxResponseOutputDto response = new InboxResponseOutputDto();
						List<InvoiceHeaderDto> filterDtoList = ObjectMapperUtils.mapAll(filteredFromWorklfowTask,
								InvoiceHeaderDto.class);
						System.err.println("FilterDtoList "+filterDtoList);
						// list of invocie ready reserved 
						
						if(filterDtoList!=null && !filterDtoList.isEmpty()){
						List<InvoiceHeaderDto> readyAndReservedInvoiceList = filterDtoList.stream()
								.filter(w -> !w.getInvoiceStatus().equals("DRAFT")).collect(Collectors.toList());
						System.err.println("readyAndReservedTaskList after filter"+readyAndReservedInvoiceList);
							// formMapfor claim
							Map<String, WorkflowTaskOutputDto> map = checkForClaim(listOfWorkflowTasks);
							List<InboxOutputDto> inboxOutputList = new ArrayList<InboxOutputDto>();
							List<InboxOutputDto> draftList = new ArrayList<InboxOutputDto>();
							for (int i = 0; i < readyAndReservedInvoiceList.size(); i++) {
								InboxOutputDto inbox = new InboxOutputDto();
								WorkflowTaskOutputDto workflowOutPut = map.get(readyAndReservedInvoiceList.get(i).getRequestId());
								inbox = ObjectMapperUtils.map(workflowOutPut, InboxOutputDto.class);
								inbox.setTaskId(workflowOutPut.getId());
								inbox.setInvoiceTotal(String.format("%.2f", readyAndReservedInvoiceList.get(i).getInvoiceTotal()));
								inbox.setDueDate( readyAndReservedInvoiceList.get(i).getDueDate());
								inbox.setExtInvNum( readyAndReservedInvoiceList.get(i).getExtInvNum());
								inbox.setInvoiceDate( readyAndReservedInvoiceList.get(i).getInvoiceDate());
								inbox.setInvoiceType(readyAndReservedInvoiceList.get(i).getInvoiceType());
								inbox.setRequestId(readyAndReservedInvoiceList.get(i).getRequestId());
								inbox.setSapInvoiceNumber(readyAndReservedInvoiceList.get(i).getSapInvoiceNumber());
								inbox.setValidationStatus(readyAndReservedInvoiceList.get(i).getInvoiceStatus());
								inbox.setVendorId(readyAndReservedInvoiceList.get(i).getVendorId());
								inbox.setVendorName(readyAndReservedInvoiceList.get(i).getVendorName());
								inboxOutputList.add(inbox);
							}
							if (filterDto.getRoleOfUser().equals("Accountant")) {

								 draftDtoList =  ObjectMapperUtils.mapAll(draftLists,
										InvoiceHeaderDto.class);
								System.err.println("draftDtoList "+draftDtoList);
							if(!ServiceUtil.isEmpty(draftDtoList)){
							for (int i = 0; i < draftDtoList.size(); i++) {
								InboxOutputDto inbox = new InboxOutputDto();
								WorkflowTaskOutputDto workflowOutPut = map.get(draftDtoList.get(i).getRequestId());
								inbox = ObjectMapperUtils.map(workflowOutPut, InboxOutputDto.class);
								inbox.setTaskId(workflowOutPut.getId());
								inbox.setInvoiceTotal(String.format("%.2f", draftDtoList.get(i).getInvoiceTotal()));
								inbox.setDueDate( draftDtoList.get(i).getDueDate());
								inbox.setExtInvNum( draftDtoList.get(i).getExtInvNum());
								inbox.setInvoiceDate( draftDtoList.get(i).getInvoiceDate());
								inbox.setInvoiceType(draftDtoList.get(i).getInvoiceType());
								inbox.setRequestId(draftDtoList.get(i).getRequestId());
								inbox.setSapInvoiceNumber(draftDtoList.get(i).getSapInvoiceNumber());
								inbox.setValidationStatus(draftDtoList.get(i).getInvoiceStatus());
								inbox.setVendorId(draftDtoList.get(i).getVendorId());
								inbox.setVendorName(draftDtoList.get(i).getVendorName());
								draftList.add(inbox);
							}
							// top and skip for draft 
							if(draftDtoList.size()>=(int)filterDto.getTop()){
							int startIndex = (int)filterDto.getSkip();
							System.err.println("startIndex "+startIndex);
							int endIndex = (int)(filterDto.getSkip()+filterDto.getTop());
							System.err.println("endIndex "+endIndex);
						     paginatedDraftLists = draftList.subList(startIndex,endIndex);
							response.setDraftList(paginatedDraftLists);
							}else {
								int startIndex = (int)filterDto.getSkip();
								System.err.println("startIndex "+startIndex);
								int endIndex = draftDtoList.size();
								System.err.println("endIndex "+endIndex);
							     paginatedDraftLists = draftList.subList(startIndex,endIndex);
								response.setDraftList(paginatedDraftLists);	
								}}
							}
							response.setDraftCount(draftDtoList.size());
							response.setSkip(filterDto.getSkip());
							response.setTop(filterDto.getTop());
							response.setSkip(filterDto.getSkip());
							response.setTaskList(inboxOutputList);
							response.setTotalCount(readyAndReservedInvoiceList.size());
							response.setMessage("SUCCESS");
							response.setStatusCodeValue(200);
							System.err.println("response of outPut" + response);
							return new ResponseEntity<InboxResponseOutputDto>(response, HttpStatus.OK);
					} else {
						InboxResponseOutputDto responseFailed = new  InboxResponseOutputDto();
						responseFailed.setMessage("No tasks are available.");
						return new ResponseEntity<>(responseFailed,
								HttpStatus.OK);
					}
				} else {
					InboxResponseOutputDto responseFailed = new  InboxResponseOutputDto();
					responseFailed.setMessage("No tasks are available.");
					return new ResponseEntity<>(responseFailed,
							HttpStatus.OK);
				}
			}
				}else {
					InboxResponseOutputDto responseFailed = new  InboxResponseOutputDto();
					responseFailed.setMessage("No tasks are available.");
					return new ResponseEntity<>(responseFailed,
							HttpStatus.OK);
				}
				}else {
					InboxResponseOutputDto responseFailed = new  InboxResponseOutputDto();
					responseFailed.setMessage("No tasks are available.");
					return new ResponseEntity<>(responseFailed,
							HttpStatus.OK);
				}
			}else {
				InboxResponseOutputDto responseFailed = new  InboxResponseOutputDto();
				responseFailed.setMessage("No tasks are available.");
				return new ResponseEntity<>(responseFailed,
						HttpStatus.OK);
			}
			}else {
				InboxResponseOutputDto responseFailed = new  InboxResponseOutputDto();
				responseFailed.setMessage("No tasks are available.");
				return new ResponseEntity<>(responseFailed,
						HttpStatus.OK);
			}
			// else if there are no filterss
		}catch (Exception e) {
			InboxResponseOutputDto responseFailed = new  InboxResponseOutputDto();
			responseFailed.setMessage("EXCEPTION_POST_MSG_FILTER =" + e.getMessage());
			return new ResponseEntity<>(responseFailed,
					HttpStatus.INTERNAL_SERVER_ERROR);
		     }
		}else {
			try{
			if (filterDto.getSkip()>= 0 && filterDto.getTop()>0) {
				if (!ServiceUtil.isEmpty(filterDto.getAssignedTo())) {
		// call the sap worklfow api with top and skip , userId logged in and fetch the token for count.
			ResponseEntity<?> responseFromSapApi = fetchWorkflowUserTaksListMultiple(filterDto);
			if (responseFromSapApi.getStatusCodeValue() == HttpStatus.OK.value()) {
			@SuppressWarnings("unchecked")
			List<WorkflowTaskOutputDto> listOfWorkflowTasks = (List<WorkflowTaskOutputDto>) responseFromSapApi
					.getBody();
			System.err.println("listOfWorkflowTasks : not in filter" + listOfWorkflowTasks);
		// call the invoice table get all task details for above requestIds and as well Draft of users
					List<InvoiceHeaderDo> lists = Collections.EMPTY_LIST;
						// check condition for accountant role to fetch draft
						// along with his user tasks
						if (!listOfWorkflowTasks.isEmpty()
								|| (listOfWorkflowTasks.isEmpty() && filterDto.getRoleOfUser().equals("Accountant"))) {
							if (filterDto.getRoleOfUser().equals("Accountant")) {
								// than add the requesId with Draft as docStatus
									if (filterDto.getRequestId() != null && !filterDto.getRequestId().isEmpty()) {
										draftLists = invoiceHeaderRepository
												.getInvoiceHeaderDocStatusByUserIdAndRequestId(filterDto.getAssignedTo(),filterDto.getRequestId());
										System.err.println("list of drafts by without Filter by requestID " + draftLists);
									}else {
										draftLists = invoiceHeaderRepository
												.getInvoiceHeaderDocStatusByUserId(filterDto.getAssignedTo());
										System.err.println("list of drafts by without filter by userid" + draftLists);
									}
								}
								if (lists != null && !lists.isEmpty()) {
									for (int i = 0; i < lists.size(); i++) {

										WorkflowTaskOutputDto workflow = new WorkflowTaskOutputDto();
										workflow.setSubject(lists.get(i).getRequestId());
										workflow.setStatus("DRAFT");
										listOfWorkflowTasks.add(workflow);
									}
								}
							if (!listOfWorkflowTasks.isEmpty() && listOfWorkflowTasks != null) {

								System.err.println("list of tasks  not in filter" + listOfWorkflowTasks);
       							         return fetchInvoiceDocHeaderDtoListFromRequestNumberMultiple(listOfWorkflowTasks,
												filterDto);
										
							} else {
								InboxResponseOutputDto response = new  InboxResponseOutputDto();
								response.setMessage("No tasks are available.");
								return new ResponseEntity<>(response,
										HttpStatus.OK);
							}
						} else {
							InboxResponseOutputDto response = new  InboxResponseOutputDto();
							response.setMessage("No tasks are available.");
							return new ResponseEntity<>(response,
									HttpStatus.OK);
						}
					} else {
						return responseFromSapApi;
					}
				} else {
					InboxResponseOutputDto response = new  InboxResponseOutputDto();
					response.setMessage("INVALID_INPUT =" + "Please provide login in user id ");
					return new ResponseEntity<>(response,
							HttpStatus.OK);
	        } 
			}else {		
				InboxResponseOutputDto response = new  InboxResponseOutputDto();
				response.setMessage("INVALID_INPUT =" + "Please provide valid top and skip");
				return  new ResponseEntity<>(response , HttpStatus.BAD_REQUEST);
			
		}
		}catch (Exception e) {
			
			InboxResponseOutputDto response = new  InboxResponseOutputDto();
			response.setMessage("EXCEPTION_POST_MSG =" + e.getMessage());
			return  new ResponseEntity<>(response,
					HttpStatus.INTERNAL_SERVER_ERROR);
		               }
	         }
		} else {
		InboxResponseOutputDto response = new  InboxResponseOutputDto();
		response.setMessage("INVALID_INPUT = "+"Please provide valid Top and Skip.");
		return  new ResponseEntity<>(response,
				HttpStatus.OK);
		}
		InboxResponseOutputDto response = new  InboxResponseOutputDto();
		response.setMessage("No tasks are available.");
		return new ResponseEntity<>(response,
				HttpStatus.OK);
		
	}		
	
	public static boolean   checkForFilterPrameter(FilterMultipleHeaderSearchDto filterDto){
		System.err.println("filterInputDto "+filterDto);
				if(ServiceUtil.isEmpty(filterDto.getValidationStatus())&&
						 ServiceUtil.isEmpty(filterDto.getInvoiceType())&&
				          ServiceUtil.isEmpty(filterDto.getVendorId())&&
				           ServiceUtil.isEmpty(filterDto.getDueDateFrom())&&
				                  ServiceUtil.isEmpty(filterDto.getDueDateTo())&&
				                  ServiceUtil.isEmpty(filterDto.getInvoiceDateFrom())&&
				                  ServiceUtil.isEmpty(filterDto.getInvoiceDateTo())&&
				                 ServiceUtil.isEmpty(filterDto.getInvoiceValueFrom())&&
				                 ServiceUtil.isEmpty(filterDto.getInvoiceValueTo())&&
				                ServiceUtil.isEmpty(filterDto.getExtInvNum())&&
				                ServiceUtil.isEmpty(filterDto.getRequestId())){
		return false;
		}
		return true;
	}
	// for multiple
	public ResponseEntity<?> fetchWorkflowUserTaksListMultiple(FilterMultipleHeaderSearchDto filter) {
		try {
			if (!ServiceUtil.isEmpty(filter.getAssignedTo())) {
				 long  totalCoun= 0;
				List<WorkflowTaskOutputDto> listOfWorkflowTasks = new ArrayList<>();
					String jwToken = DestinationReaderUtil.getJwtTokenForAuthenticationForSapApi();
					HttpClient client = HttpClientBuilder.create().build();
					StringBuilder url = new StringBuilder();
					appendParamInUrl(url, WorkflowConstants.WORKFLOW_DEFINATION_ID_KEY,
							WorkflowConstants.WORKFLOW_DEFINATION_ID_VALUE);
					String status =  formStatusValue(filter.getTaskStatus());
					appendParamInUrl(url, WorkflowConstants.STATUS_OF_APPROVAL_TASKS_KEY,status);
					String userID =  formIdValue(filter.getAssignedTo());
					appendParamInUrl(url, WorkflowConstants.RECIPIENT_USER_KEY, userID);
					
					if (filter.getRequestId() != null && !filter.getRequestId().isEmpty()) {
						appendParamInUrl(url, WorkflowConstants.SUBJECT, filter.getRequestId());
					}
					appendParamInUrl(url, WorkflowConstants.FIND_COUNT_OF_TASKS_KEY,
							WorkflowConstants.FIND_COUNT_OF_TASKS_VALUE);
					appendParamInUrl(url, WorkflowConstants.TOP_KEY, filter.getTop()+"");
					appendParamInUrl(url, WorkflowConstants.PAGE_NUM_KEY, filter.getSkip()+"");
					url.insert(0, (MenabevApplicationConstant.WORKFLOW_REST_BASE_URL + "/v1/task-instances?"));
					System.err.println("URL : " + url);
					HttpGet httpGet = new HttpGet(url.toString());
					httpGet.addHeader("Content-Type", "application/json");
					httpGet.addHeader("Authorization", "Bearer " + jwToken);
					HttpResponse response = client.execute(httpGet);
					String dataFromStream = getDataFromStream(response.getEntity().getContent());
					if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
					org.apache.http.Header[] headerCountProperty = response.getHeaders("x-total-count");                                                                                                                                                          
					if (headerCountProperty.length != 0) {
						for (org.apache.http.Header header : headerCountProperty) {
							if (header.getName().equalsIgnoreCase("x-total-count")) {
								totalCoun = Long.parseLong(header.getValue());
								logger.error("token --- " + totalCoun);
							}
						}
					}
						JSONArray jsonArray = new JSONArray(dataFromStream);
						for(int i=0;i<jsonArray.length();i++){
					          Object  jsonObject =  jsonArray.get(i);
 							WorkflowTaskOutputDto taskDto = new Gson().fromJson(jsonObject.toString(),
									WorkflowTaskOutputDto.class);
							taskDto.setRequestIds(taskDto.getSubject());
							taskDto.setTotalCount(totalCoun);
							listOfWorkflowTasks.add(taskDto);
						}
						if (!listOfWorkflowTasks.isEmpty()) {
							return new ResponseEntity<>(listOfWorkflowTasks, HttpStatus.OK);
						} else {
							return new ResponseEntity<>(listOfWorkflowTasks, HttpStatus.OK);
						}
					} else {
						return new ResponseEntity<String>("FETCHING FAILED", HttpStatus.CONFLICT);

					}
			} else {
				return new ResponseEntity<>("INVALID_INPUT_PLEASE_RETRY" + " with provide USER ID.",
						HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			return new ResponseEntity<>("EXCEPTION_POST_MSG" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	
	public ResponseEntity<?> fetchWorkflowUserTaksListByRequestId(FilterMultipleHeaderSearchDto filter,List<String> requesIds) {
		try {
			if (!ServiceUtil.isEmpty(filter.getAssignedTo())){
				 long  totalCoun= 0;
				List<WorkflowTaskOutputDto> listOfWorkflowTasks = new ArrayList<>();
					String jwToken = DestinationReaderUtil.getJwtTokenForAuthenticationForSapApi();

					HttpClient client = HttpClientBuilder.create().build();

					StringBuilder url = new StringBuilder();

					appendParamInUrl(url, WorkflowConstants.WORKFLOW_DEFINATION_ID_KEY,
							WorkflowConstants.WORKFLOW_DEFINATION_ID_VALUE);
					String status =  formStatusValue(filter.getTaskStatus());
					appendParamInUrl(url, WorkflowConstants.STATUS_OF_APPROVAL_TASKS_KEY,status);
					String userID =  formIdValue(filter.getAssignedTo());
						appendParamInUrl(url, WorkflowConstants.RECIPIENT_USER_KEY, userID);
						             String requetId =  formIdValue(requesIds);
					if (filter.getRequestId() != null && !filter.getRequestId().isEmpty()) {
						appendParamInUrl(url, WorkflowConstants.SUBJECT, requetId);
					}
					appendParamInUrl(url, WorkflowConstants.FIND_COUNT_OF_TASKS_KEY,
							WorkflowConstants.FIND_COUNT_OF_TASKS_VALUE);
					appendParamInUrl(url, WorkflowConstants.TOP_KEY, filter.getTop()+"");
					appendParamInUrl(url, WorkflowConstants.PAGE_NUM_KEY, filter.getSkip()+"");
					url.insert(0, (MenabevApplicationConstant.WORKFLOW_REST_BASE_URL + "/v1/task-instances?"));
					System.err.println("URL : " + url);
					HttpGet httpGet = new HttpGet(url.toString());
					httpGet.addHeader("Content-Type", "application/json");
					httpGet.addHeader("Authorization", "Bearer " + jwToken);
					HttpResponse response = client.execute(httpGet);
					String dataFromStream = getDataFromStream(response.getEntity().getContent());
					if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
					org.apache.http.Header[] headerCountProperty = response.getHeaders("x-total-count");                                                                                                                                                          
					if (headerCountProperty.length != 0) {
						for (org.apache.http.Header header : headerCountProperty) {
							if (header.getName().equalsIgnoreCase("x-total-count")) {
								totalCoun = Long.parseLong(header.getValue());
								logger.error("token --- " + totalCoun);
							}
						}
					}
						JSONArray jsonArray = new JSONArray(dataFromStream);
						for(int i=0;i<jsonArray.length();i++){
					          Object  jsonObject =  jsonArray.get(i);
 							WorkflowTaskOutputDto taskDto = new Gson().fromJson(jsonObject.toString(),
									WorkflowTaskOutputDto.class);
							taskDto.setRequestIds(taskDto.getSubject());
							taskDto.setTotalCount(totalCoun);
							listOfWorkflowTasks.add(taskDto);
						}
						if (!listOfWorkflowTasks.isEmpty()) {
							return new ResponseEntity<>(listOfWorkflowTasks, HttpStatus.OK);
						} else {
							return new ResponseEntity<>(listOfWorkflowTasks, HttpStatus.OK);
						}
					} else {
						return new ResponseEntity<String>("FETCHING FAILED = ", HttpStatus.CONFLICT);
					}
			} else {
				return new ResponseEntity<>("INVALID_INPUT_PLEASE_RETRY = " + " with provide USER ID.",
						HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			return new ResponseEntity<>("EXCEPTION_POST_MSG = " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// add to this Helper Class
	public static boolean checkString(String s) {
		if (s == null || s.equals("") || s.trim().isEmpty() || s.matches("") || s.equals(null)) {
			return true;
		}
		return false;
	}
	
	public static String formStatusValue(List<String> requestId){
		
		String requestIds="";
	for(int i = 0;i<requestId.size();i++){
		if(i ==requestId.size()-1){
		requestIds = requestIds + requestId.get(i).toUpperCase();
		}else{
			requestIds = requestIds + requestId.get(i) +",";
		}
	
		}
		return requestIds;
	}
	public static String formIdValue(List<String> requestId){
		
		String requestIds="";
	for(int i = 0;i<requestId.size();i++){
		if(i ==requestId.size()-1){
		requestIds = requestIds + requestId.get(i);
		}else{
			requestIds = requestIds+ requestId.get(i) +",";
		}
	
		}
		return requestIds;
	}

	public static void appendParamInUrl(StringBuilder url, String key, String value) {
		if (url.length() > 0) {
			url.append("&" + key + "=" + value);
		} else {
			url.append(key + "=" + value);
		}
	}	
	public static void appendParamInOdataUrl(StringBuilder url, String key, String value) {
			url.append( key + "=" + value);
	}
	public static void appendInOdataUrl(StringBuilder url, String key, String value) {
		url.append( key + "" + value);
}

	
	
	public static void appendValuesInOdataUrl(StringBuilder url,String key, List<String> value){
		for(int i = 0; i<value.size();i++){
			if(value.size()==1){
			url.append(key+"%20eq%20"+"%27"+value.get(i)+"%27");
			System.out.println("26 ");
			}
			else if(i==value.size()-1){
				url.append(key+"%20eq%20"+"%27"+value.get(i)+"%27");
				System.out.println("30 ");
			}else{
				url.append(key+"%20eq%20"+"%27"+value.get(i)+"%27"+"%20or%20");
				System.out.println("29 ");
				
			}
		}
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private ResponseEntity<?> fetchInvoiceDocHeaderDtoListFromRequestNumberMultiple(
			List<WorkflowTaskOutputDto> invoiceWorkflowRequestIdList, FilterMultipleHeaderSearchDto filterDto) {

		List<InvoiceHeaderDo> listOfInvoiceOrders = Collections.EMPTY_LIST;
		 List<InboxOutputDto>	paginatedDraftLists = Collections.emptyList();
		ResponseEntity<?> responseFromFilter = null;
		// workflowResponse with list of requestIds
		List<String> requestIds = invoiceWorkflowRequestIdList.stream().map(WorkflowTaskOutputDto::getSubject)
				.collect(Collectors.toList());
		System.err.println("requestId " + requestIds);
		// List of requestIds fetched for user from user task
		responseFromFilter = filterInvoicesMultiple(requestIds, filterDto);
		if (responseFromFilter.getStatusCodeValue() == 200) {
			listOfInvoiceOrders = (List<InvoiceHeaderDo>) responseFromFilter.getBody();
			System.err.println("lisOFInvoice by requestIds " + listOfInvoiceOrders);
			if (listOfInvoiceOrders != null && !listOfInvoiceOrders.isEmpty()) {
				InboxResponseOutputDto response = new InboxResponseOutputDto();
				List<InvoiceHeaderDto> filterDtoList = ObjectMapperUtils.mapAll(listOfInvoiceOrders,
						InvoiceHeaderDto.class);
				// Draft List form 
				// list of invocie ready reserved 
				List<InvoiceHeaderDto> readyAndReservedInvoiceList = filterDtoList.stream()
						.filter(w -> !w.getInvoiceStatus().equals("Draft")).collect(Collectors.toList());
				System.err.println("readyAndReservedTaskList withoutFilter "+readyAndReservedInvoiceList);
					// formMapfor claim
					Map<String, WorkflowTaskOutputDto> map = checkForClaim(invoiceWorkflowRequestIdList);
					List<InboxOutputDto> inboxOutputList = new ArrayList<InboxOutputDto>();
					List<InboxOutputDto>  draftList = new ArrayList<InboxOutputDto>();
					for (int i = 0; i < readyAndReservedInvoiceList.size(); i++) {
						InboxOutputDto inbox = new InboxOutputDto();
						WorkflowTaskOutputDto workflowOutPut = map.get(readyAndReservedInvoiceList.get(i).getRequestId());
						inbox = ObjectMapperUtils.map(workflowOutPut, InboxOutputDto.class);
						inbox.setTaskId(workflowOutPut.getId());
						inbox.setInvoiceTotal(String.format("%.2f", readyAndReservedInvoiceList.get(i).getInvoiceTotal()));
						inbox.setDueDate( readyAndReservedInvoiceList.get(i).getDueDate());
						inbox.setExtInvNum( readyAndReservedInvoiceList.get(i).getExtInvNum());
						inbox.setInvoiceDate( readyAndReservedInvoiceList.get(i).getInvoiceDate());
						inbox.setInvoiceType(readyAndReservedInvoiceList.get(i).getInvoiceType());
						inbox.setRequestId(readyAndReservedInvoiceList.get(i).getRequestId());
						inbox.setSapInvoiceNumber(readyAndReservedInvoiceList.get(i).getSapInvoiceNumber());
						inbox.setVendorId(readyAndReservedInvoiceList.get(i).getVendorId());
						inbox.setVendorName(readyAndReservedInvoiceList.get(i).getVendorName());
						inboxOutputList.add(inbox);
					}
					if (filterDto.getRoleOfUser().equals("Accountant")) {
						List<InvoiceHeaderDto> draftDtoList = filterDtoList.stream()
								.filter(w -> w.getInvoiceStatus().equals("DRAFT")).collect(Collectors.toList());
						System.err.println("Draft list withoutFilter "+ draftDtoList);
						if(!ServiceUtil.isEmpty(draftList)){
						for (int i = 0; i < draftDtoList.size(); i++) {
							InboxOutputDto inbox = new InboxOutputDto();
							WorkflowTaskOutputDto workflowOutPut = map.get(draftDtoList.get(i).getRequestId());
							inbox = ObjectMapperUtils.map(workflowOutPut, InboxOutputDto.class);
							inbox.setTaskId(workflowOutPut.getId());
							inbox.setInvoiceTotal(String.format("%.2f", draftDtoList.get(i).getInvoiceTotal()));
							inbox.setDueDate( draftDtoList.get(i).getDueDate());
							inbox.setExtInvNum( draftDtoList.get(i).getExtInvNum());
							inbox.setInvoiceDate( draftDtoList.get(i).getInvoiceDate());
							inbox.setInvoiceType(draftDtoList.get(i).getInvoiceType());
							inbox.setRequestId(draftDtoList.get(i).getRequestId());
							inbox.setSapInvoiceNumber(draftDtoList.get(i).getSapInvoiceNumber());
							inbox.setValidationStatus(draftDtoList.get(i).getInvoiceStatus());
							inbox.setVendorId(draftDtoList.get(i).getVendorId());
							inbox.setVendorName(draftDtoList.get(i).getVendorName());
							draftList.add(inbox);
						}
						// top and skip for draft 
						if(draftDtoList.size()>=(int)filterDto.getTop()){
							int startIndex = (int)filterDto.getSkip();
							System.err.println("startIndex "+startIndex);
							int endIndex = (int)(filterDto.getSkip()+filterDto.getTop());
							System.err.println("endIndex "+endIndex);
						     paginatedDraftLists = draftList.subList(startIndex,endIndex);
							response.setDraftList(paginatedDraftLists);
							}else {
								int startIndex = (int)filterDto.getSkip();
							System.err.println("startIndex "+startIndex);
							int endIndex = draftDtoList.size();
							System.err.println("endIndex "+endIndex);
						     paginatedDraftLists = draftList.subList(startIndex,endIndex);
							response.setDraftList(paginatedDraftLists);	
								response.setDraftList(paginatedDraftLists);	
								}
						}
						}
					response.setDraftCount(draftList.size());
					response.setSkip(filterDto.getSkip());
					response.setTop(filterDto.getTop());
					response.setSkip(filterDto.getSkip());
					response.setTaskList(inboxOutputList);
					response.setTotalCount(invoiceWorkflowRequestIdList.get(0).getTotalCount());
					response.setMessage("SUCCESS");
					response.setStatusCodeValue(200);
					System.err.println("response of outPut" + response);
					return new ResponseEntity<InboxResponseOutputDto>(response, HttpStatus.OK);
				} else {
					InboxResponseOutputDto response = new InboxResponseOutputDto();
					response.setMessage("No Task Available.");
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
			} else {
				InboxResponseOutputDto response  =  new InboxResponseOutputDto();
				response.setMessage("No Task Available.");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} 
	

	private ResponseEntity<?> filterInvoicesAndGetRequesIds(FilterMultipleHeaderSearchDto dto) {
		List<InvoiceHeaderDo> invoiceOrderList = null;
		StringBuffer query = new StringBuffer();
		Map<String, String> filterQueryMap = new HashMap<String, String>();
		query.append("SELECT * FROM INVOICE_HEADER RR WHERE");
		if (dto.getRequestId()!=null && !dto.getRequestId().isEmpty()) {
			filterQueryMap.put(" RR.REQUEST_ID =", "'"+dto.getRequestId() + "'");
			// correct
		}
		if (dto.getVendorId() != null && !dto.getVendorId().isEmpty()) {
			StringBuffer rqstId = new StringBuffer();
			for (int i = 0; i < dto.getVendorId().size(); i++) {
				if (i < dto.getVendorId().size() - 1) {
					rqstId.append("'" + dto.getVendorId().get(i) + "'" + ",");
				} else {
					rqstId.append("'" + dto.getVendorId().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.VENDOR_ID IN", "(" + rqstId + ")");
		}
		if (dto.getDueDateFrom() != null && dto.getDueDateTo() != null && dto.getDueDateFrom() != 0
				&& dto.getDueDateTo() != 0) {
			filterQueryMap.put(" RR.DUE_DATE BETWEEN ", +dto.getDueDateFrom() + " AND " + dto.getDueDateTo());
			// is empty
		}
		if (dto.getDueDateFrom() != null && dto.getDueDateFrom() != 0) {
			filterQueryMap.put(" RR.DUE_DATE =", "'"+dto.getDueDateFrom() + "'");
			// correct
		}
		if (dto.getInvoiceDateFrom() != null && dto.getInvoiceDateFrom() != null && dto.getInvoiceDateTo() != 0
				&& dto.getInvoiceDateTo() != 0) {
			filterQueryMap.put(" RR.INVOICE_DATE BETWEEN ", dto.getInvoiceDateFrom() + " AND " + dto.getInvoiceDateTo());
			// correct
		}
		if (dto.getInvoiceDateFrom() != null && dto.getInvoiceDateFrom() != 0) {
			filterQueryMap.put(" RR.INVOICE_DATE =", dto.getInvoiceDateFrom() + "");
			// correct
		}
		if (dto.getInvoiceType() != null && !dto.getInvoiceType().isEmpty()) {
			StringBuffer rqstId = new StringBuffer();
			for (int i = 0; i < dto.getInvoiceType().size(); i++) {
				if (i < dto.getInvoiceType().size() - 1) {
					rqstId.append("'" + dto.getInvoiceType().get(i) + "'" + ",");
				} else {
					rqstId.append("'" + dto.getInvoiceType().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.INVOICE_TYPE IN", "(" + rqstId + ")");
			// checked
		}
		/*if (dto.getAssignedTo() != null && !dto.getAssignedTo().isEmpty()) {

			StringBuffer rqstId = new StringBuffer();
			for (int i = 0; i < dto.getAssignedTo().size(); i++) {
				if (i < dto.getAssignedTo().size() - 1) {
					rqstId.append("'" + dto.getAssignedTo().get(i) + "'" + ",");
				} else {
					rqstId.append("'" + dto.getAssignedTo().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.TASK_OWNER IN", "(" + rqstId + ")");
			// is EMpty
		}*/
		if (dto.getValidationStatus() != null && !dto.getValidationStatus().isEmpty()) {
			StringBuffer status = new StringBuffer();
			for (int i = 0; i < dto.getValidationStatus().size(); i++) {
				if (i < dto.getValidationStatus().size() - 1) {
					status.append("'" + dto.getValidationStatus().get(i) + "'" + ",");
				} else {
					status.append("'" + dto.getValidationStatus().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.INVOICE_STATUS IN", "(" + status + ")");// procss
		}
		if (dto.getExtInvNum() != null && !dto.getExtInvNum().isEmpty()) {
			filterQueryMap.put(" RR.EXT_INV_NUM =", "'" + dto.getExtInvNum() + "'");
		}
		if (!(ServiceUtil.isEmpty(dto.getInvoiceValueFrom()) && ServiceUtil.isEmpty(dto.getInvoiceValueTo()))) {
			filterQueryMap.put(" RR.INVOICE_TOTAL BETWEEN ", dto.getInvoiceValueFrom() + " AND " + dto.getInvoiceValueTo());
			// correct
		}
		if (!ServiceUtil.isEmpty(dto.getInvoiceValueFrom())) {
			filterQueryMap.put(" RR.INVOICE_TOTAL =", dto.getInvoiceDateFrom() + "");
			// correct
		}
		int lastAppendingAndIndex = filterQueryMap.size() - 1;
		AtomicInteger count = new AtomicInteger(0);
		System.err.println("lastAppendingAndIndex " + lastAppendingAndIndex);

		filterQueryMap.forEach((k, v) -> {
			System.out.println("label : " + k + " value : " + v);
			query.append(k);
			query.append(v);
			if (filterQueryMap.size() > 1) {
				if (count.getAndIncrement() < filterQueryMap.size() - 1) {
					query.append(" AND ");
				}
			} else {
				query.append(" ORDER BY RR.REQUEST_CREATED_AT DESC");
				query.append(";");
			}
		});
		if (filterQueryMap.size() > 1) {
			query.append(" ORDER BY RR.REQUEST_CREATED_AT DESC");
			query.append(";");
		}
		System.err.println("Query : Check " + query.toString());
		invoiceOrderList = invoiceHeaderRepoFilter.getFilteredRequestIds(query.toString());
		if (invoiceOrderList != null && !invoiceOrderList.isEmpty()) {
			return new ResponseEntity<List<InvoiceHeaderDo>>(invoiceOrderList, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("No Data Found For Searched Criteria", HttpStatus.NO_CONTENT);
		}
	}
	// filter for Draft 
	private List<InvoiceHeaderDo>  filterInvoicesAndGetRequesIdsByDraft(FilterMultipleHeaderSearchDto dto) {
		List<InvoiceHeaderDo> invoiceOrderList = null;
		StringBuffer query = new StringBuffer();
		Map<String, String> filterQueryMap = new HashMap<String, String>();
		query.append("SELECT * FROM INVOICE_HEADER RR WHERE");
		if (dto.getRequestId()!=null && !dto.getRequestId().isEmpty()) {
			filterQueryMap.put(" RR.REQUEST_ID =", "'"+dto.getRequestId() + "'");
			// correct
		}
		if (dto.getVendorId() != null && !dto.getVendorId().isEmpty()) {
			StringBuffer rqstId = new StringBuffer();
			for (int i = 0; i < dto.getVendorId().size(); i++) {
				if (i < dto.getVendorId().size() - 1) {
					rqstId.append("'" + dto.getVendorId().get(i) + "'" + ",");
				} else {
					rqstId.append("'" + dto.getVendorId().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.VENDOR_ID IN", "(" + rqstId + ")");
		}
		if (dto.getDueDateFrom() != null && dto.getDueDateTo() != null && dto.getDueDateFrom() != 0
				&& dto.getDueDateTo() != 0) {
			filterQueryMap.put(" RR.DUE_DATE BETWEEN ", +dto.getDueDateFrom() + " AND " + dto.getDueDateTo());
			// is empty
		}
		if (dto.getDueDateFrom() != null && dto.getDueDateFrom() != 0) {
			filterQueryMap.put(" RR.DUE_DATE =", "'"+dto.getDueDateFrom() + "'");
			// correct
		}
		if (dto.getInvoiceDateFrom() != null && dto.getInvoiceDateFrom() != null && dto.getInvoiceDateTo() != 0
				&& dto.getInvoiceDateTo() != 0) {
			filterQueryMap.put(" RR.INVOICE_DATE BETWEEN ", dto.getInvoiceDateFrom() + " AND " + dto.getInvoiceDateTo());
			// correct
		}
		if (dto.getInvoiceDateFrom() != null && dto.getInvoiceDateFrom() != 0) {
			filterQueryMap.put(" RR.INVOICE_DATE =", dto.getInvoiceDateFrom() + "");
			// correct
		}
		
			filterQueryMap.put(" RR.INVOICE_TYPE IN", "('" +"NON-PO"+ "')");
			// checked
		
		if (dto.getAssignedTo() != null && !dto.getAssignedTo().isEmpty()) {

			StringBuffer rqstId = new StringBuffer();
			for (int i = 0; i < dto.getAssignedTo().size(); i++) {
				if (i < dto.getAssignedTo().size() - 1) {
					rqstId.append("'" + dto.getAssignedTo().get(i) + "'" + ",");
				} else {
					rqstId.append("'" + dto.getAssignedTo().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.TASK_OWNER IN", "(" + rqstId + ")");
			// is EMpty
		}
		
			filterQueryMap.put(" RR.INVOICE_STATUS IN", "('" +"DRAFT" + "')");// procss
		
		if (dto.getExtInvNum() != null && !dto.getExtInvNum().isEmpty()) {
			filterQueryMap.put(" RR.EXT_INV_NUM =", "'" + dto.getExtInvNum() + "'");
		}
		if (!(ServiceUtil.isEmpty(dto.getInvoiceValueFrom()) && ServiceUtil.isEmpty(dto.getInvoiceValueTo()))) {
			filterQueryMap.put(" RR.INVOICE_TOTAL BETWEEN ", dto.getInvoiceValueFrom() + " AND " + dto.getInvoiceValueTo());
			// correct
		}
		if (!ServiceUtil.isEmpty(dto.getInvoiceValueFrom())) {
			filterQueryMap.put(" RR.INVOICE_TOTAL =", dto.getInvoiceDateFrom() + "");
			// correct
		}
		int lastAppendingAndIndex = filterQueryMap.size() - 1;
		AtomicInteger count = new AtomicInteger(0);
		System.err.println("lastAppendingAndIndex " + lastAppendingAndIndex);

		filterQueryMap.forEach((k, v) -> {
			System.out.println("label : " + k + " value : " + v);
			query.append(k);
			query.append(v);
			if (filterQueryMap.size() > 1) {
				if (count.getAndIncrement() < filterQueryMap.size() - 1) {
					query.append(" AND ");
				}
			} else {
				query.append(" ORDER BY RR.REQUEST_CREATED_AT DESC");
				query.append(";");
			}
		});
		if (filterQueryMap.size() > 1) {
			query.append(" ORDER BY RR.REQUEST_CREATED_AT DESC");
			query.append(";");
		}
		System.err.println("Query : Check " + query.toString());
		invoiceOrderList = invoiceHeaderRepoFilter.getFilteredRequestIds(query.toString());
		if (invoiceOrderList != null && !invoiceOrderList.isEmpty()) {
		      return invoiceOrderList;
		} else {
			return invoiceOrderList = Collections.emptyList();
		}
	}
	
	private ResponseEntity<?> filterInvoicesMultiple(List<String> requestId, FilterMultipleHeaderSearchDto dto) {
		List<InvoiceHeaderDo> invoiceOrderList = null;
		StringBuffer query = new StringBuffer();
		Map<String, String> filterQueryMap = new HashMap<String, String>();

		query.append("SELECT * FROM INVOICE_HEADER RR WHERE");
		if (requestId != null && !requestId.isEmpty()) {
			StringBuffer rqstId = new StringBuffer();
			for (int i = 0; i < requestId.size(); i++) {
				if (i < requestId.size() - 1) {
					rqstId.append("'" + requestId.get(i) + "'" + ",");
				} else {
					rqstId.append("'" + requestId.get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.REQUEST_ID IN", "(" + rqstId + ")");
		}
		
		if (dto.getVendorId() != null && !dto.getVendorId().isEmpty()) {
			StringBuffer rqstId = new StringBuffer();
			for (int i = 0; i < dto.getVendorId().size(); i++) {
				if (i < dto.getVendorId().size() - 1) {
					rqstId.append("'" + dto.getVendorId().get(i) + "'" + ",");
				} else {
					rqstId.append("'" + dto.getVendorId().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.VENDOR_ID IN", "(" + rqstId + ")");
		}
		if (dto.getDueDateFrom() != null && dto.getDueDateTo() != null && dto.getDueDateFrom() != 0
				&& dto.getDueDateTo() != 0) {
			filterQueryMap.put(" RR.DUE_DATE BETWEEN ", +dto.getDueDateFrom() + " AND " + dto.getDueDateTo());
			// is empty
		}
		if (dto.getDueDateFrom() != null && dto.getDueDateFrom() != 0) {
			filterQueryMap.put(" RR.DUE_DATE =", dto.getDueDateFrom() + "");
			// correct
		}
		if (!(ServiceUtil.isEmpty(dto.getInvoiceDateFrom()) && ServiceUtil.isEmpty(dto.getInvoiceDateFrom()))) {
			filterQueryMap.put(" RR.INVOICE_DATE BETWEEN ", dto.getInvoiceDateFrom() + " AND " + dto.getInvoiceDateTo());
			// correct
		}
		if (!ServiceUtil.isEmpty(dto.getInvoiceDateFrom())) {
			filterQueryMap.put(" RR.INVOICE_DATE =", dto.getInvoiceDateFrom() + "");
			// correct
		}
		if (dto.getInvoiceType() != null && !dto.getInvoiceType().isEmpty()) {

			StringBuffer rqstId = new StringBuffer();
			for (int i = 0; i < dto.getInvoiceType().size(); i++) {
				if (i < dto.getInvoiceType().size() - 1) {
					rqstId.append("'" + dto.getInvoiceType().get(i) + "'" + ",");
				} else {
					rqstId.append("'" + dto.getInvoiceType().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.INVOICE_TYPE IN", "'" + rqstId + "'");
			// checked
		}
		/*if (dto.getAssignedTo() != null && !dto.getAssignedTo().isEmpty()) {

			StringBuffer rqstId = new StringBuffer();
			for (int i = 0; i < dto.getAssignedTo().size(); i++) {
				if (i < dto.getAssignedTo().size() - 1) {
					rqstId.append("'" + dto.getAssignedTo().get(i) + "'" + ",");
				} else {
					rqstId.append("'" + dto.getAssignedTo().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.TASK_OWNER IN", "(" + rqstId + ")");
			// is EMpty
		}*/
		if (dto.getValidationStatus() != null && !dto.getValidationStatus().isEmpty()) {
			StringBuffer status = new StringBuffer();
			for (int i = 0; i < dto.getValidationStatus().size(); i++) {
				if (i < dto.getValidationStatus().size() - 1) {
					status.append("'" + dto.getValidationStatus().get(i) + "'" + ",");
				} else {
					status.append("'" + dto.getValidationStatus().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.INVOICE_STATUS IN", "(" + status + ")");// procss
																				// status
		}
		if (dto.getExtInvNum() != null && !dto.getExtInvNum().isEmpty()) {
			filterQueryMap.put(" RR.EXT_INV_NUM =", "'" + dto.getExtInvNum() + "'");
		}
		if (!(ServiceUtil.isEmpty(dto.getInvoiceValueFrom()) && ServiceUtil.isEmpty(dto.getInvoiceValueTo()))) {
			filterQueryMap.put(" RR.INVOICE_TOTAL BETWEEN ", dto.getInvoiceValueFrom() + " AND " + dto.getInvoiceValueTo());
			// correct
		}
		if (!ServiceUtil.isEmpty(dto.getInvoiceValueFrom())) {
			filterQueryMap.put(" RR.INVOICE_TOTAL =", dto.getInvoiceDateFrom() + "");
			// correct
		}
		int lastAppendingAndIndex = filterQueryMap.size() - 1;
		AtomicInteger count = new AtomicInteger(0);
		System.err.println("lastAppendingAndIndex " + lastAppendingAndIndex);

		filterQueryMap.forEach((k, v) -> {
			System.out.println("label : " + k + " value : " + v);
			query.append(k);
			query.append(v);
			if (filterQueryMap.size() > 1) {
				if (count.getAndIncrement() < filterQueryMap.size() - 1) {
					query.append(" AND ");
				}
			} else {
				query.append(" ORDER BY RR.REQUEST_CREATED_AT DESC");
				query.append(";");
			}
		});
		if (filterQueryMap.size() > 1) {
			query.append(" ORDER BY RR.REQUEST_CREATED_AT DESC");
			query.append(";");
		}
		System.err.println("Query : Check " + query.toString());
		invoiceOrderList = invoiceHeaderRepoFilter.getFilterDetails(query.toString());
		if (invoiceOrderList != null && !invoiceOrderList.isEmpty()) {
			return new ResponseEntity<List<InvoiceHeaderDo>>(invoiceOrderList, HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("No Data Found For Searched Criteria", HttpStatus.NO_CONTENT);
		}
	}

	private Map<String, WorkflowTaskOutputDto> checkForClaim(
			List<WorkflowTaskOutputDto> invoiceRequestIdListPaginated) {
		Map<String, WorkflowTaskOutputDto> map = new HashMap<String, WorkflowTaskOutputDto>();

		for (WorkflowTaskOutputDto w : invoiceRequestIdListPaginated) {
			map.put(w.getSubject(), w);
		}

		return map;
	}

	// getInvoiceDetails based on requestId
	@Override
	public ResponseEntity<?> getInvoiceDetail(String requestId) {
		try {
			InvoiceHeaderDto invoiceHeaderDto = new InvoiceHeaderDto();
			// get InvoiceHeader
			InvoiceHeaderDo invoiceHeaderDo = invoiceHeaderRepository.fetchInvoiceHeader(requestId);
			if(!ServiceUtil.isEmpty(invoiceHeaderDo)){
			invoiceHeaderDto = ObjectMapperUtils.map(invoiceHeaderDo, InvoiceHeaderDto.class);
			// get InvoiceItem
			List<InvoiceItemDo> invoiceItemDo = invoiceItemRepository.getInvoiceItemDos(requestId);
			List<InvoiceItemDto> invoiceItemDtoList = ObjectMapperUtils.mapAll(invoiceItemDo, InvoiceItemDto.class);
			for (int i = 0; i < invoiceItemDtoList.size(); i++) {
				// get AccountAssignment
				List<InvoiceItemAcctAssignmentDo> invoiceItemAcctAssignmentdoList = invoiceItemAcctAssignmentRepository
						.get(requestId, invoiceItemDtoList.get(0).getItemCode());
				List<InvoiceItemAcctAssignmentDto> invoiceItemAcctAssignmentdtoList = ObjectMapperUtils
						.mapAll(invoiceItemAcctAssignmentdoList, InvoiceItemAcctAssignmentDto.class);
				invoiceItemDtoList.get(i).setInvItemAcctDtoList(invoiceItemAcctAssignmentdtoList);
				// get InvoiceItem Message
				List<ItemMessageDto> itemMessage = new ArrayList<ItemMessageDto>();
				invoiceItemDtoList.get(i).setInvoiceItemMessages(itemMessage);
			}
			// get CostAllocation
			List<CostAllocationDo> costAllocationDo = costAllocationRepository.getAllOnRequestId(requestId);
			List<CostAllocationDto> costAllocationDto = ObjectMapperUtils.mapAll(costAllocationDo,
					CostAllocationDto.class);
			// get Attachements
			List<AttachmentDo> attachementDo = attachmentRepository.getAllAttachmentsForRequestId(requestId);
			List<AttachmentDto> AttachementDto = ObjectMapperUtils.mapAll(attachementDo, AttachmentDto.class);
			// get Comments
			List<CommentDo> commentDo = commentRepository.getCommentsByRequestIdAndUser(requestId);
			List<CommentDto> commentDto = ObjectMapperUtils.mapAll(commentDo, CommentDto.class);
			//get Activity Log 
			List<ActivityLogDo> activityLogDo =  activityLogRepo.getAllActivityForRequestId(requestId);
			List<ActivityLogDto> activityDto = ObjectMapperUtils.mapAll(activityLogDo, ActivityLogDto.class);
			// get Message
			List<HeaderMessageDto> messageLists = new ArrayList<HeaderMessageDto>();
			// get Header check api 
			
			
			// set validation status  code 
		
			
			invoiceHeaderDto.setInvoiceItems(invoiceItemDtoList);
			invoiceHeaderDto.setCostAllocation(costAllocationDto);
			invoiceHeaderDto.setAttachment(AttachementDto);
			invoiceHeaderDto.setComment(commentDto);
			invoiceHeaderDto.setHeaderMessages(messageLists);
			invoiceHeaderDto.setActivityLog(activityDto);
			return new ResponseEntity<InvoiceHeaderDto>(invoiceHeaderDto, HttpStatus.OK);
			}
			else {
				ResponseDto response = new  ResponseDto();
				response.setMessage("No Invocie Found for the RequestID ");
				return new ResponseEntity<>(response,HttpStatus.OK);
			}
		} catch (Exception e) {
			ResponseDto response = new  ResponseDto();
			response.setMessage("Failed due to " + e);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@Override
	public ResponseEntity<?> getInvoiceItemDetail(String requestId) {
		InvoiceHeaderDo invoiceHeaderDo = invoiceHeaderRepository.fetchInvoiceHeader(requestId);
		InvoiceHeaderDto invoiceHeaderDto = ObjectMapperUtils.map(invoiceHeaderDo, InvoiceHeaderDto.class);
		// get InvoiceItem
		List<InvoiceItemDo> invoiceItemDo = invoiceItemRepository.getInvoiceItemDos(requestId);
		List<InvoiceItemDto> invoiceItemDtoList = ObjectMapperUtils.mapAll(invoiceItemDo, InvoiceItemDto.class);
		invoiceHeaderDto.setInvoiceItems(invoiceItemDtoList);
		return new ResponseEntity<>(invoiceHeaderDto, HttpStatus.OK);
	}
	@Override
	public ResponseEntity<?> getCostAllocationDetail(String requestId) {
		List<CostAllocationDo> costAllocationDo = costAllocationRepository.getAllOnRequestId(requestId);
		List<CostAllocationDto> costAllocationDto = ObjectMapperUtils.mapAll(costAllocationDo, CostAllocationDto.class);
		return new ResponseEntity<>(costAllocationDto, HttpStatus.OK);
	}
	@Override
	public ResponseEntity<?> getInvoiceAcctAssinment(String requestId) {
		List<InvoiceItemAcctAssignmentDo> invoiceItemAcctAssignmentdoList = invoiceItemAcctAssignmentRepository
				.getByRequestId(requestId);
		List<InvoiceItemAcctAssignmentDto> invoiceItemAcctAssignmentdtoList = ObjectMapperUtils
				.mapAll(invoiceItemAcctAssignmentdoList, InvoiceItemAcctAssignmentDto.class);
		return new ResponseEntity<>(invoiceItemAcctAssignmentdtoList, HttpStatus.OK);
	}
	@Override
	public ResponseEntity<?> getInvoiceAttachment(String requestId) {
		// get Attachements
		List<AttachmentDo> attachementDo = attachmentRepository.getAllAttachmentsForRequestId(requestId);
		List<AttachmentDto> AttachementDto = ObjectMapperUtils.mapAll(attachementDo, AttachmentDto.class);
		return new ResponseEntity<>(AttachementDto, HttpStatus.OK);
	}
	@Override
	public ResponseEntity<?> getInvoiceComments(String requestId) {
		// get Comments
		List<CommentDo> commentDo = commentRepository.getCommentsByRequestIdAndUser(requestId);
		List<CommentDto> commentDto = ObjectMapperUtils.mapAll(commentDo, CommentDto.class);
		return new ResponseEntity<>(commentDto, HttpStatus.OK);
	}
	@Override
	public ResponseEntity<?> getActivityLog(String requestId) {
		List<ActivityLogDto> activity = new ArrayList<ActivityLogDto>();
		return new ResponseEntity<>(activity, HttpStatus.OK);
	}
	@Override
	public List<InvoiceHeaderDto> getAll() {
		List<InvoiceHeaderDto> list = new ArrayList<>();
		String rejectionText = "";
		try {
			ModelMapper mapper = new ModelMapper();
			// Sort sort = new Sort(Sort.Direction.DESC, "requestId");
			// List<InvoiceHeaderDo> entityList =
			// invoiceHeaderRepository.findAll(sort);// .findAll();
			//List<InvoiceHeaderDo> entityList = invoiceHeaderRepository.getAllInvoiceHeader("09");
			//for (InvoiceHeaderDo invoiceHeaderDo : entityList) {
			/*	if (invoiceHeaderDo.getLifecycleStatus().equalsIgnoreCase(ApplicationConstants.REJECTED)) {
					rejectionText = reasonForRejectionRepository
							.getRejectionTextbyRejectionId(invoiceHeaderDo.getReasonForRejection(), "EN");
				}*/
				//String lifecycleStatusText = statusConfigRepository.text(invoiceHeaderDo.getLifecycleStatus(), "EN");
				logger.error("rejectionText : " + rejectionText);
				//logger.error("lifecycleStatusText : " + lifecycleStatusText);
				//InvoiceHeaderDto dto = (mapper.map(invoiceHeaderDo, InvoiceHeaderDto.class));
				//dto.setRejectionText(rejectionText);
				//dto.setLifecycleStatusText(lifecycleStatusText);
				//list.add(dto);
			//}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}
	@Override
	public ResponseDto delete(String id) {
		ResponseDto response = new ResponseDto();
		try {
			invoiceHeaderRepository.deleteInvoiceHeader(id);
			response.setCode("200");
			response.setStatus("Success");
			response.setMessage("Deleted");
			return response;
		} catch (Exception e) {
			response.setCode("500");
			response.setStatus("Failed");
			response.setMessage("Error");
			return response;
		}
	}
/*	@Override
	public InboxDto findPaginated(int pageNo, int limit) {
		InboxDto dto = new InboxDto();
		List<InvoiceHeaderDto> dtoList = new ArrayList<>();

		ModelMapper mapper = new ModelMapper();
		try {
			Long count = invoiceHeaderRepository.count();
			dto.setCount(count);
			int offset = 0;
			if (pageNo > 0) {
				offset = (limit * pageNo);
			}
			List<StatusConfigDo> statusList = statusConfigRepository.findAll();
			Map<String, String> statusMap = new HashMap<String, String>();
			for (StatusConfigDo statusConfigDo : statusList) {
				statusMap.put(statusConfigDo.getLifeCycleStatus(), statusConfigDo.getText());
			}
			List<InvoiceHeaderDo> doEntity = invoiceHeaderRepository.getAllByLimit(limit, offset);
			for (InvoiceHeaderDo invoiceHeaderDo : doEntity) {
				InvoiceHeaderDto headerDto = mapper.map(invoiceHeaderDo, InvoiceHeaderDto.class);
				headerDto.setLifecycleStatusText(statusMap.get(invoiceHeaderDo.getLifecycleStatus()));
				dtoList.add(headerDto);
			}
			dto.setHeaderList(dtoList);
		} catch (Exception e) {
			dto.setHeaderList(dtoList);
			dto.setCount(0L);

		}
		return dto;

	}*/

	/*@Override
	public InvoiceHeaderDetailsDto getAllInvoiceDetailsOnRequestId(String requestId) {
		ModelMapper mapper = new ModelMapper();
		List<Object[]> objList = invoiceHeaderRepository.getAllInvoiceDetailsOnRequestId(requestId);
		InvoiceHeaderDetailsDto dto = new InvoiceHeaderDetailsDto();
		if (!ServiceUtil.isEmpty(objList)) {
			List<InvoiceItemDto> invoiceItemList = new ArrayList<>();
			InvoiceHeaderDo invoiceHeaderDo = null;
			InvoiceHeaderDto headerDto = null;
			try {
				for (int i = 0; i < objList.size(); i++) {
					Object[] objArr = objList.get(i);
					invoiceHeaderDo = (InvoiceHeaderDo) objArr[0];
					if (i <= 0)
						headerDto = mapper.map(invoiceHeaderDo, InvoiceHeaderDto.class);
					InvoiceItemDo invoiceItemDo = (InvoiceItemDo) objArr[1];
					InvoiceItemDto itemDto = mapper.map(invoiceItemDo, InvoiceItemDto.class);
					invoiceItemList.add(itemDto);
				}
				if (!ServiceUtil.isEmpty(headerDto)) {
					dto.setChannelType(headerDto.getChannelType());
					dto.setClerkEmail(headerDto.getClerkEmail());
					dto.setClerkId(headerDto.getClerkId());
					dto.setCompCode(headerDto.getCompCode());
					dto.setCreatedAt(headerDto.getCreatedAt());
					dto.setCurrency(headerDto.getCurrency());
					dto.setEmailFrom(headerDto.getEmailFrom());
					dto.setExtInvNum(headerDto.getExtInvNum());
					dto.setFiscalYear(headerDto.getFiscalYear());
					dto.setInvoiceTotal(headerDto.getInvoiceTotal());
					dto.setInvoiceType(headerDto.getInvoiceType());
					dto.setLifecycleStatus(headerDto.getLifecycleStatus());
					dto.setPaymentTerms(headerDto.getPaymentTerms());
					dto.setRefDocCat(headerDto.getRefDocCat());
					dto.setRefDocNum(headerDto.getRefDocNum());
					dto.setRequestId(headerDto.getRequestId());
					dto.setSapInvoiceNumber(headerDto.getSapInvoiceNumber());
					dto.setShippingCost(headerDto.getShippingCost());
					// dto.setStatus(headerDto.getStatus());
					dto.setTaxAmount(headerDto.getTaxAmount());
					dto.setVendorId(headerDto.getVendorId());
					dto.setVersion(headerDto.getVersion());
					dto.setBalance(headerDto.getBalance());
					dto.setGrossAmount(headerDto.getGrossAmount());
				}
				dto.setInvoiceItems(invoiceItemList);

			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		return dto;

	}*/
	@Override
	public List<InvoiceHeaderDto> filterByKeys(InvoiceHeaderDto dto) {
		ModelMapper mapper = new ModelMapper();
		List<InvoiceHeaderDto> dtoList = new ArrayList<>();
		try {
			Example<InvoiceHeaderDo> example = Example.of(importDto(dto));
			List<InvoiceHeaderDo> entityList = invoiceHeaderRepository.findAll(example);
			for (InvoiceHeaderDo invoiceHeaderDo : entityList) {
				dtoList.add(mapper.map(invoiceHeaderDo, InvoiceHeaderDto.class));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dtoList;
	}
	private InvoiceHeaderDo importDto(InvoiceHeaderDto dto) {
		InvoiceHeaderDo entity = new InvoiceHeaderDo();
		if (!ServiceUtil.isEmpty(dto.getChannelType()))
			entity.setChannelType(dto.getChannelType());
		if (!ServiceUtil.isEmpty(dto.getCurrency()))
			entity.setCurrency(dto.getCurrency());
		if (!ServiceUtil.isEmpty(dto.getExtInvNum()))
			entity.setExtInvNum(dto.getExtInvNum());
		if (!ServiceUtil.isEmpty(dto.getFiscalYear()))
			entity.setFiscalYear(dto.getFiscalYear());
		if (!ServiceUtil.isEmpty(dto.getInvoiceTotal()))
			entity.setInvoiceTotal(new Double(dto.getInvoiceTotal()));
		if (!ServiceUtil.isEmpty(dto.getInvoiceType()))
			entity.setInvoiceType(dto.getInvoiceType());
		if (!ServiceUtil.isEmpty(dto.getPaymentTerms()))
			entity.setPaymentTerms(dto.getPaymentTerms());
		if (!ServiceUtil.isEmpty(dto.getRequestId()))
			entity.setRequestId(dto.getRequestId());
		// if (!ServiceUtil.isEmpty(dto.getTaskStatus()))
		// entity.setTaskStatus(dto.getTaskStatus());
		if (!ServiceUtil.isEmpty(dto.getCompCode()))
			entity.setCompCode(dto.getCompCode());
		if (!ServiceUtil.isEmpty(dto.getSapInvoiceNumber()))
			// entity.setSapInvoiceNumber(dto.getSapInvoiceNumber());
			if (!ServiceUtil.isEmpty(dto.getShippingCost()))
				entity.setShippingCost(dto.getShippingCost());
		if (!ServiceUtil.isEmpty(dto.getTaxAmount()))
			entity.setTaxAmount(dto.getTaxAmount());
		if (!ServiceUtil.isEmpty(dto.getVersion()))
			entity.setVersion(dto.getVersion());
		return entity;
	}
	private String getSubStringForQuery(InvoiceHeaderDto dto) {

		String subString = "";
		String str = "";

		if (!ServiceUtil.isEmpty(dto.getRequestId()))
			subString += "REQUEST_ID = '" + dto.getRequestId() + "' and ";

		if (!ServiceUtil.isEmpty(dto.getExtInvNum()))
			subString += "EXT_INV_NUM = '" + dto.getExtInvNum() + "' and ";


		if (!ServiceUtil.isEmpty(dto.getVendorId()))
			subString += "VENDOR_ID LIKE '%" + dto.getVendorId() + "%' and ";

		if (!ServiceUtil.isEmpty(dto.getVendorName()))
			subString += "VENDOR_NAME = '" + dto.getVendorName() + "' and ";
		// if (!ServiceUtil.isEmpty(dto.getTaskStatus()))
		// subString += "TASK_STATUS = '" + dto.getTaskStatus() + "' and "; //

		if (!ServiceUtil.isEmpty(dto.getInvoiceType()))
			subString += "INVOICE_TYPE = '" + dto.getInvoiceType() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getCompCode())) {
			subString += "COMP_CODE LIKE '%" + dto.getCompCode() + "' and ";
		}
		if (!ServiceUtil.isEmpty(dto.getSapInvoiceNumber()))
			subString += "SAP_INVOICE_NUMBER = '" + dto.getSapInvoiceNumber() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getFiscalYear()))
			subString += "FISCAL_YEAR = '" + dto.getFiscalYear() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getCurrency()))
			subString += "CURRENCY = '" + dto.getCurrency() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getPaymentTerms()))
			subString += "PAYMENT_TERMS = '" + dto.getPaymentTerms() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getTaxAmount()))
			subString += "TAX_AMOUNT = '" + dto.getTaxAmount() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getShippingCost()))
			subString += "SHIPPING_COST = '" + dto.getShippingCost() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getVersion()))
			subString += "VERSION = '" + dto.getVersion() + "' and ";

		if (!ServiceUtil.isEmpty(dto.getPaymentBlock()))
			subString += "PAYMENT_BLOCK = '" + dto.getPaymentBlock() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getPaymentBlockDesc()))
			subString += "PAYMENT_BLOCK_DESC = '" + dto.getPaymentBlockDesc() + "' and ";

		if (subString.length() > 0)
			str = subString.substring(0, subString.length() - 4);
		logger.error("Final query " + str);
		return str;
	}


	@Override
	public String getVendorId(String requestId) {
		return null;
		// return invoiceHeaderRepository.getVendorId(requestId);
	}

	@Override
	public ResponseDto reasonForRejection(InvoiceHeaderDto dto) {
		// TODO Auto-generated method stub
		ResponseDto response = new ResponseDto();
		InvoiceHeaderDo invoiceHeader = invoiceHeaderRepository.fetchInvoiceHeader(dto.getRequestId());
		// invoiceHeader.setRequestId(requestId);
		invoiceHeader.setReasonForRejection(dto.getReasonForRejection());
		try {
			invoiceHeaderRepository.save(invoiceHeader);
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(ApplicationConstants.REJECT_SUCCESS);
			return response;
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.REJECT_FAILURE);
			return response;
		}

	}
	@Override
	public InvoiceHeaderDashBoardDto headerCheck(HeaderCheckDto headerCheckDto) {
		return null;
	}
	@Override
	public ResponseDto updateHeader(InvoiceHeaderDto dto) {
		InvoiceHeaderDo invoiceHeader = invoiceHeaderRepository.fetchInvoiceHeader(dto.getRequestId());
		invoiceHeader.setVendorId(dto.getVendorId());
		invoiceHeader.setVendorName(dto.getVendorName());
		invoiceHeader.setExtInvNum(dto.getExtInvNum());
		invoiceHeader.setOcrBatchId(dto.getOcrBatchId());
		invoiceHeader.setCurrency(dto.getCurrency());
		invoiceHeader.setGrossAmount(dto.getGrossAmount());
		invoiceHeader.setPostingDate(dto.getPostingDate());
		invoiceHeader.setInvoiceDate(dto.getInvoiceDate());
		invoiceHeaderRepository.save(invoiceHeader);
		return null;
	}
	private static Double stringToDouble(String in) {
		if (ServiceUtil.isEmpty(in)) {
			in = "0";
		}
		if (in.matches("-?\\d+(\\.\\d+)?"))
			return new Double(in);
		else {
			return Double.valueOf(0.0);
		}
	}
	public ResponseEntity<?> triggerRuleService(AcountOrProcessLeadDetermination determination)
			throws ClientProtocolException, IOException, URISyntaxException {
		List<ApproverDataOutputDto> lists = null;
		try {
			RuleInputProcessLeadDto ruleInput = new RuleInputProcessLeadDto();
			ruleInput.setCompCode(determination.getCompCode());
			ruleInput.setProcessLeadCheck(determination.getProcessLeadCheck());
			ruleInput.setVendorId(determination.getVednorId());
			System.err.println("ruleInput " + ruleInput.toRuleInputString(RuleConstants.menaBevRuleService));
			ResponseEntity<?> response = execute((RuleInputDto) ruleInput, RuleConstants.menaBevRuleService);
			System.err.println("1477 responseFromRulesTrigger" + response);
			// call convert statement and than get the ouput and return it .
			if (response.getStatusCodeValue() == 201) {
				String node = (String) response.getBody();
				lists = convertFromJSonNodeRo(node, "Not-Default");

				if (lists.isEmpty() || lists == null) {
					RuleInputProcessLeadDto Default = new RuleInputProcessLeadDto();
					Default.setCompCode("Default");
					Default.setProcessLeadCheck("Default");
					Default.setVendorId("Default");

					ResponseEntity<?> responseDefault = execute((RuleInputDto) Default,
							RuleConstants.menaBevRuleService);
					if (responseDefault.getStatusCodeValue() == 201) {
						String nodeDefault = (String) responseDefault.getBody();
						lists = convertFromJSonNodeRo(nodeDefault, "Default");
						if (!lists.isEmpty() && lists != null) {
							return new ResponseEntity<>(lists, HttpStatus.OK);
						} else {
							return new ResponseEntity<>("Defualt Reccord is missing in Rule file", HttpStatus.CONFLICT);
						}
					} else {
						return new ResponseEntity<>(response, HttpStatus.CONFLICT);
					}
				} else {
					return new ResponseEntity<>(lists, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity<>(response, HttpStatus.CONFLICT);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<ApproverDataOutputDto> convertFromJSonNodeRo(String node, String checkDefault) {
		List<ApproverDataOutputDto> approverList = new ArrayList<>();
		JSONObject jObj = new JSONObject(node);
		JSONArray arr = jObj.getJSONArray("Result");
		JSONObject innerObject = null;
		if (!arr.isNull(0))
			innerObject = arr.getJSONObject(0).getJSONObject("AcctOrProcessLeadDeterminationResult");
		System.err.println("innerObj " + innerObject);
		if (!innerObject.isEmpty() && innerObject != null) {
			// for (int i = 0; i < innerArray.length(); i++) {
			ApproverDataOutputDto approverDto = new ApproverDataOutputDto();
			approverDto.setUserOrGroup(innerObject.get("UserOrGroup").toString());
			approverDto.setUserType(checkDefault);
			approverList.add(approverDto);

			// }
			System.err.println("list of approveDto " + approverList);
			return approverList;
		}
		System.err.println("list of approveDto " + approverList);
		return approverList;
	}
	protected ResponseEntity<?> execute(RuleInputDto input, String rulesServiceId)
			throws ClientProtocolException, IOException, URISyntaxException {
		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, new BasicCookieStore());
		HttpPost httpPost = null;
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = null;
		httpClient = getHTTPClient();
		System.err.println("1561 RuleInput " + input);
		// need to set still the ruleServiceId and than , process the output.
		String jwToken = getJwtTokenForAuthenticationForRulesSapApi();
		System.err.println("map for rulesToken : " + jwToken);
		httpPost = new HttpPost(RuleConstants.RULE_BASE_URL);
		httpPost.addHeader(CONTENT_TYPE, "application/json");
		httpPost.addHeader(AUTHORIZATION, "Bearer " + jwToken); // header
		String ruleInputString = input.toRuleInputString(rulesServiceId);
		StringEntity stringEntity = new StringEntity(ruleInputString);
		System.err.println("stringEntity " + ruleInputString);
		httpPost.setEntity(stringEntity);
		response = httpClient.execute(httpPost);
		System.err.println("response : " + response);
		System.err.println("RuleTriggerResponse =" + response);
		if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String dataFromStream = getDataFromStream(response.getEntity().getContent());
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
			if (response != null) {
				response.close();
			}
			if (httpClient != null) {
				httpClient.close();
			}
			return new ResponseEntity<>(dataFromStream, HttpStatus.CREATED);
		} else {
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
			if (response != null) {
				response.close();
			}
			if (httpClient != null) {
				httpClient.close();
			}
			return new ResponseEntity<>(getDataFromStream(response.getEntity().getContent()), HttpStatus.BAD_REQUEST);
		}
	}

	public static String getJwtTokenForAuthenticationForRulesSapApi() throws URISyntaxException, IOException {
		System.err.println("77 destination");
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(RuleConstants.OAUTH_TOKEN_URL);
		httpPost.addHeader("Content-Type", "application/json");
		// Encoding username and password
		String auth = HelperClass.encodeUsernameAndPassword(RuleConstants.RULE_CLIENT_ID,
				RuleConstants.RULE_CLIENT_SECRETE);
		httpPost.addHeader("Authorization", auth);
		HttpResponse res = client.execute(httpPost);
		System.err.println(" 92 rest" + res);
		String data = HelperClass.getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String jwtToken = new JSONObject(data).getString("access_token");
			System.err.println("jwtProxyToken " + jwtToken);
			return jwtToken;
		}
		return null;
	}

	private static CloseableHttpClient getHTTPClient() {
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		CloseableHttpClient httpClient = clientBuilder.build();
		return httpClient;
	}

	// Trigger Workflow
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseEntity<?> triggerWorkflow(WorkflowContextDto dto, String definitionId) {
		try {
			String token = DestinationReaderUtil.getJwtTokenForAuthenticationForSapApi();

			System.err.println("1125 " + token);
			HttpClient client = HttpClientBuilder.create().build();
			String url = MenabevApplicationConstant.WORKFLOW_REST_BASE_URL + "/v1/workflow-instances";
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("Authorization", "Bearer " + token);
			httpPost.addHeader("Content-Type", "application/json");
			try {
				String worklfowInputString = dto.toRuleInputString(definitionId);
				StringEntity entity = new StringEntity(worklfowInputString);
				entity.setContentType("application/json");
				httpPost.setEntity(entity);
				HttpResponse response = client.execute(httpPost);
				System.err.println("WorkflowTriggerResponse =" + response);
				if (response.getStatusLine().getStatusCode() == HttpStatus.CREATED.value()) {
					String dataFromStream = getDataFromStream(response.getEntity().getContent());
					System.err.println("dataStream =" + dataFromStream);
					JSONObject jsonObject = new JSONObject(dataFromStream);
					System.err.println(" data stram JsonObject " + jsonObject);
					WorkflowTaskOutputDto taskDto = new Gson().fromJson(jsonObject.toString(),
							WorkflowTaskOutputDto.class);
					return new ResponseEntity(taskDto, HttpStatus.CREATED);
				} else {
					return new ResponseEntity(getDataFromStream(response.getEntity().getContent()),
							HttpStatus.BAD_REQUEST);
				}

			} catch (IOException e) {
				logger.error(e.getMessage());
				return new ResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}
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

	@SuppressWarnings("unchecked")
	@Override
	public ResponseEntity<?> claimTaskOfUser(ClaimAndReleaseDto dto) {
		try {
			
			System.err.println("claimAndReleaseDto "+ dto);
			String token = DestinationReaderUtil.getJwtTokenForAuthenticationForSapApi();
			HttpClient client = HttpClientBuilder.create().build();
			String payload = null;
			String url = MenabevApplicationConstant.WORKFLOW_REST_BASE_URL + "/v1/task-instances/" + dto.getTaskID();
			if (ServiceUtil.isEmpty(dto.getUserId())) {
				// if user is empty and null , the task will be released.
				payload = "{\"processor\":\"" + "" + "\"}";
				System.err.println("payload=" + payload);
			} else {
				// if user is not empty and not null task will be claimed.
				payload = "{\"processor\":\"" + dto.getUserId() + "\"}";
				System.err.println("payload=" + payload);
			}
			HttpPatch httpPatch = new HttpPatch(url);
			httpPatch.addHeader("Authorization", "Bearer " + token);
			httpPatch.addHeader("Content-Type", "application/json");
			try {
				StringEntity entity = new StringEntity(payload);
				System.err.println("inputEntity " + entity);
				entity.setContentType("application/json");
				httpPatch.setEntity(entity);
				HttpResponse response = client.execute(httpPatch);
				if (response.getStatusLine().getStatusCode() == HttpStatus.NO_CONTENT.value()) {
					if (!ServiceUtil.isEmpty(dto.getUserId())) {
						ClaimResponseDto claim = new ClaimResponseDto();
						InboxOutputDto inboxDto = ObjectMapperUtils.map(dto.getInvoice(),InboxOutputDto.class);
						claim.setInbox(inboxDto);
						claim.getInbox().setProcessor(dto.getUserId());
						claim.getInbox().setStatus("RESERVED");
						claim.setMessage("Task is been claimed by user "+dto.getUserId());
						return new ResponseEntity<ClaimResponseDto>(claim,HttpStatus.CREATED);
					} else {
						ClaimResponseDto claim = new ClaimResponseDto();
						InboxOutputDto inboxDto = ObjectMapperUtils.map(dto.getInvoice(),InboxOutputDto.class);
						claim.setInbox(inboxDto);
						claim.getInbox().setProcessor(dto.getUserId());
						claim.getInbox().setStatus("READY");
						claim.setMessage("Task is been Released");
						return new ResponseEntity<ClaimResponseDto>(claim,HttpStatus.CREATED);
					}
				} else {
					return new ResponseEntity<String>(
							"Failed due " + getDataFromStream(response.getEntity().getContent()),
							HttpStatus.BAD_REQUEST);
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
				return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	// get Remediation user details 
		@Override
		public ResponseEntity<?> getRemediationUserDetails(List<PurchaseOrderRemediationInput> dtoList,String userListNeeded) throws URISyntaxException, IOException{
			
			List<RemediationUser> remediationUserOutput = new ArrayList<RemediationUser>();
			if(userListNeeded.equals("BUYER")){
			// get the BUYER details based on poCreatedName in invoiceHeader filed 
	             ResponseEntity<?>  responseOfBuyer =  odataGetRemediationDetailsForBuyerAndGrnWithCreatedByAndPurchGrp(dtoList,userListNeeded,"/PurchOrdDetailsSet?","PurchaseOrderCreator");			
	            	 RemediationUser grnUsers = (RemediationUser) responseOfBuyer.getBody();
	            	 remediationUserOutput.add(grnUsers);
			}else if(userListNeeded.equals("GRN")){
				// check if purchaseReq  is empty , than call purchaseGroup.
				List<PoDocumentItem> itemList = dtoList.stream().flatMap(e -> e.getPoDocumentItem().stream()).collect(Collectors.toList());
			  if(!itemList.isEmpty() && itemList!=null){
				  List<List<String>>   listOfprNumAndprItem = checkPurReqNumAndPurReqItem(dtoList);
                  List<String>   prNumList = listOfprNumAndprItem.get(0);
                  List<String>   prItemList = listOfprNumAndprItem.get(1);
				if((prNumList.isEmpty() || prNumList==null)&& (prItemList.isEmpty()||prItemList==null)){
					 ResponseEntity<?>  responseOfGrnPurGrp =  odataGetRemediationDetailsForBuyerAndGrnWithCreatedByAndPurchGrp(dtoList,userListNeeded,"/PurchGrpDetailsSet?","PurchasingGroup");
					System.err.println("responseOfPurGroup "+responseOfGrnPurGrp);
					 // Convert the json response 
		            	 RemediationUser grnUsers = (RemediationUser) responseOfGrnPurGrp.getBody();
		            	 remediationUserOutput.add(grnUsers);
				}
				else{
	 		// get the GRN details based on with requisitionNum and requisitionItm 
					 ResponseEntity<?>  responseOfGrnPurReqAndItem =	odataGetRemediationDetailsForGrnByPurchReqAndPurchReqItem(prNumList,prItemList,userListNeeded,"/PurchReqDetailsSet?");
					 System.err.println("responseOfGrnPurReqAndItem "+responseOfGrnPurReqAndItem);
		            	 RemediationUser grnUsers = (RemediationUser) responseOfGrnPurReqAndItem.getBody();
		            	 remediationUserOutput.add(grnUsers);
		             }
				}
			  else {
				  // return no item available for po
				  ResponseDto response = new ResponseDto();
	     			response.setCode("400");
	     			response.setMessage("No Po Item");
	     			response.setStatus("Failed");
	     		return new ResponseEntity<ResponseDto>(response,HttpStatus.BAD_REQUEST);
			  }
			}else if(userListNeeded.equals("PURCHASELEAD")){
			
				// get Purchase Lead Details 
			}
			else {
				// get the BUYER details based on poCreatedName in invoiceHeader filed 
	            ResponseEntity<?>  responseOfBuyer =  odataGetRemediationDetailsForBuyerAndGrnWithCreatedByAndPurchGrp(dtoList,"BUYER","/PurchOrdDetailsSet?","PurchaseOrderCreator");			
	           	 RemediationUser buyerUser = (RemediationUser) responseOfBuyer.getBody();
	           	 remediationUserOutput.add(buyerUser);
				// get the GRN details based on with no purchaseRequisationNumber 
	                   List<List<String>>   listOfprNumAndprItem = checkPurReqNumAndPurReqItem(dtoList);
	                   List<String>   prNumList = listOfprNumAndprItem.get(0);
	                   List<String>   prItemList = listOfprNumAndprItem.get(1);
 	            if((prNumList.isEmpty() || prNumList==null)&& (prItemList.isEmpty()||prItemList==null)){
					 ResponseEntity<?>  responseOfGrnPurGrp =  odataGetRemediationDetailsForBuyerAndGrnWithCreatedByAndPurchGrp(dtoList,"GRN","/PurchGrpDetailsSet?","PurchasingGroup");
					System.err.println("responseOfPurGroup "+responseOfGrnPurGrp);
					 // conver the json response 
		            	 RemediationUser grnUsers = (RemediationUser) responseOfGrnPurGrp.getBody();
		            	 remediationUserOutput.add(grnUsers);
				}
				else{
			    // get the GRN details based on with requisitionNum and requisitionItm 
					 ResponseEntity<?>  responseOfGrnPurReqAndItem =	odataGetRemediationDetailsForGrnByPurchReqAndPurchReqItem(prNumList,prItemList,"GRN","/PurchReqDetailsSet?");
					 System.err.println("responseOfGrnPurReqAndItem "+responseOfGrnPurReqAndItem);
		            	 RemediationUser grnUsers = (RemediationUser) responseOfGrnPurReqAndItem.getBody();
		            	 remediationUserOutput.add(grnUsers);
		             }
				// get the Details of PurchaseLead
 	            
	            
			}
			return new ResponseEntity<List<RemediationUser>>(remediationUserOutput,HttpStatus.OK);
		}
		 @SafeVarargs
		private static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors) 
		  {
		    final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();
		    return t -> 
		    {
		      final List<?> keys = Arrays.stream(keyExtractors)
		                  .map(ke -> ke.apply(t))
		                  .collect(Collectors.toList());
		      return seen.putIfAbsent(keys, Boolean.TRUE) == null;
		    };
		  }
		 
		
	    @Override
		public ResponseEntity<?> odataGetRemediationDetailsForBuyerAndGrnWithCreatedByAndPurchGrp(List<PurchaseOrderRemediationInput> dtoList,String userListNeeded,String entitySet,String param) throws URISyntaxException, IOException {
			if(!dtoList.isEmpty()&& dtoList!=null){
				List<String>   createdBy = dtoList.stream().filter(c->!c.getCreatedBy().isEmpty()).map(PurchaseOrderRemediationInput::getCreatedBy).collect(Collectors.toList());
				List<String>     purchasingGroup = dtoList.stream().filter(c->!c.getCreatedBy().isEmpty()).map(PurchaseOrderRemediationInput::getPurchGroup).collect(Collectors.toList());
				String endPointurl = null;
				// form url
				if((!createdBy.isEmpty() && createdBy!=null)||(!purchasingGroup.isEmpty() && purchasingGroup!=null)){
	              
					if(userListNeeded.equals("BUYER")){
					 endPointurl = formInputUrl(createdBy,entitySet,param) ;}
					else{
					endPointurl = formInputUrl(purchasingGroup,entitySet,param);	
					}
	              // call Destination Service 
	              Map<String, Object> destinationInfo = OdataHelperClass.getDestination("SD4_DEST");
				// call odata service 
	            ResponseEntity<?> responseFromOdata =   OdataHelperClass.consumingOdataService(endPointurl, null,"GET", destinationInfo);	
	            if(responseFromOdata.getStatusCodeValue()==200){
	               String jsonOutputString = (String) responseFromOdata.getBody();
	               RemediationUser  remediationUsers =  formOutPutSuccessResponse(jsonOutputString,userListNeeded);  
		             System.err.println("convertedResponse "+responseFromOdata);
	            // convert OuputResponse                
	              return new ResponseEntity<RemediationUser>(remediationUsers,HttpStatus.OK);
	            }else {
	            	  String jsonOutputString = (String) responseFromOdata.getBody();
	            	  RemediationUser  errorMessage =  formOutPutFailureResponse(jsonOutputString,userListNeeded);
	            	  System.err.println("convertedRrrorResponse "+responseFromOdata);
	            	  return new ResponseEntity<RemediationUser>(errorMessage,HttpStatus.BAD_REQUEST);
	                   }
				}else{
	    	   RemediationUser  response = new RemediationUser();
				response.setMessage("No created By found in the pos.");
			return new ResponseEntity<RemediationUser>(response,HttpStatus.BAD_REQUEST);
	            }
			}else{
				 RemediationUser  response = new RemediationUser();
				response.setMessage("Please provide the po list.");
			return new ResponseEntity<RemediationUser>(response,HttpStatus.BAD_REQUEST);
			}
	    }

	    
	    @Override
	   	public ResponseEntity<?> odataGetRemediationDetailsForGrnByPurchReqAndPurchReqItem(List<String> purchaseReqList,List<String> purchaseReqItemList ,String userListNeeded,String entitySet) throws URISyntaxException, IOException {
	   			// form url
	                 String endPointurl = formInputUrlForPuchaseReqAndItem(purchaseReqList,purchaseReqItemList,entitySet);
	            // call Destination Service 
	                 Map<String, Object> destinationInfo = OdataHelperClass.getDestination("SD4_DEST");
	   			// call odata service 
	              ResponseEntity<?> responseFromOdata =   OdataHelperClass.consumingOdataService(endPointurl, null,"GET", destinationInfo);	
	               if(responseFromOdata.getStatusCodeValue()==200){
	                  String jsonOutputString = (String) responseFromOdata.getBody();
	                  System.err.println("ECCResponse "+jsonOutputString);
	               // convert OuputResponse  
	                  RemediationUser  remediationUsers =  formOutPutSuccessResponse(jsonOutputString,userListNeeded);
	   	             System.err.println("convertedResponse "+remediationUsers);
	                 return new ResponseEntity<RemediationUser>(remediationUsers,HttpStatus.OK);
	               }else {
		            	  String jsonOutputString = (String) responseFromOdata.getBody();
		             RemediationUser  errorMessage =  formOutPutFailureResponse(jsonOutputString,userListNeeded);	  
		            	  System.err.println("convertedRrrorResponse "+responseFromOdata);
	       		return new ResponseEntity<RemediationUser>(errorMessage,HttpStatus.BAD_REQUEST);
	            }
	    }
		
	    // form input url for createdBy and PurchaseGroup
		public String formInputUrl(List<String> valueList,String entitySet,String param){
			StringBuilder urlForm = new StringBuilder();
			appendParamInOdataUrl(urlForm, "&$filter","" );
			appendValuesInOdataUrl(urlForm,param,valueList);
			appendParamInOdataUrl(urlForm, "&$format","json" );
			urlForm.insert(0, ("/sap/opu/odata/sap/ZP2P_API_GRNUSERDETAILS_SRV"+entitySet));
			System.err.println("url"+urlForm.toString());
	                return urlForm.toString();		
		}
		
		// form input url for PurchseReq and PurchsaeItem
		public String formInputUrlForPuchaseReqAndItem(List<String> prucReqList,List<String> purchItemReqList,String entitySet){
			StringBuilder urlForm = new StringBuilder();
			appendParamInOdataUrl(urlForm, "&$filter","(" );
			appendValuesInOdataUrl(urlForm,"PurchaseRequisitionNumber",prucReqList);
			appendInOdataUrl(urlForm, ")and","(" );
			appendValuesInOdataUrl(urlForm,"PurchaseRequisitionItem",purchItemReqList);
			appendParamInOdataUrl(urlForm, ")&$format","json" );
			urlForm.insert(0, ("/sap/opu/odata/sap/ZP2P_API_GRNUSERDETAILS_SRV"+entitySet));
			System.err.println("url"+urlForm.toString());
	                return urlForm.toString();		
		}
		// get odata success body
		public OdataResponseDto convertStringToJsonForOdataSuccess(String json){
			OdataResponseDto  taskDto = new Gson().fromJson(json.toString(),
					OdataResponseDto.class);
			return taskDto;
		}
		// get odata Failure  body 
		public OdataErrorResponseDto convertStringToJsonForOdataFailure(String json){
			OdataErrorResponseDto response = new Gson().fromJson(json.toString(),
					OdataErrorResponseDto.class);
			return response;
		}
		// Success
		public RemediationUser formOutPutSuccessResponse(String jsonOutputString,String type)
		{
			 RemediationUser remediation = new RemediationUser();
             remediation.setType(type);
             remediation.setMessage("Success");
             List<RemediationUserDto> remediList = new ArrayList<RemediationUserDto>();
             OdataResponseDto  taskDto =     convertStringToJsonForOdataSuccess(jsonOutputString);
             OdataOutPutPayload   outResp =  taskDto.getD();
                     List<OdataResultObject> resultList = outResp.getResults();
                     resultList.stream().forEach(r->{
                  	   RemediationUserDto reme = new RemediationUserDto();
                  	   reme.setUser(r.getEmailID());
                  	   remediList.add(reme);
                     });
                     remediation.setUsers(remediList); 
                     return remediation;
		}
		//Faiure
		public RemediationUser formOutPutFailureResponse(String jsonOutputString,String type)
		{
		  RemediationUser  errorMessage = new RemediationUser();
    	  OdataErrorResponseDto response =  convertStringToJsonForOdataFailure(jsonOutputString);
    	  errorMessage.setType(type);
    	  errorMessage.setMessage(response.getError().getMessage().getValue());
    	  errorMessage.setUsers(Collections.emptyList());
		        return errorMessage;
		}
		public List<List<String>> checkPurReqNumAndPurReqItem(List<PurchaseOrderRemediationInput> dtoList){
			      
			List<List<String>>    listOutput = new ArrayList<List<String>>();
			List<PoDocumentItem> itemList = dtoList.stream().flatMap(e -> e.getPoDocumentItem().stream()).collect(Collectors.toList());
  			List<PoDocumentItem>	 itemUnqiueList = itemList .stream().filter(distinctByKeys(PoDocumentItem::getPreqNum, PoDocumentItem::getPreqItem)).collect(Collectors.toList());
  			List<String> prNumList = new ArrayList<String>();
  			List<String> prItemList = new ArrayList<String>();
   			itemUnqiueList.stream().forEach(prNum->{
   			      if(!prNum.getPreqNum().isEmpty() && prNum.getPreqNum()!=null){ prNumList.add(prNum.getPreqNum());}
   			      if(!prNum.getPreqItem().isEmpty() && prNum.getPreqItem()!=null){ prItemList.add(prNum.getPreqItem());  
   			      }});
   			listOutput.add(prNumList);
   			listOutput.add(prItemList);
   			return listOutput;
		}
		
		
		
		// AccountantSubmitApi 
		@Override
		public ResponseEntity<?>  accountantInvoiceSubmit(InvoiceSubmitDto invoiceSubmit){
		// save api for invoice 
		/*	
			// check for action Code
			if(!ServiceUtil.isEmpty(invoiceSubmit.getPurchaseOrders()){
			// if Accountant Submit For Remediation 
			if(invoiceSubmit.getActionCode().equals(ApplicationConstants.ACCOUNTANT_SUBMIT_FOR_REMEDIATION)){
				
				if(invoiceSubmit.getInvoice().getInvoiceStatus().equals(ApplicationConstants.PO_MISSING_OR_INVALID )){
	         //If invoiceStatusCode=PO Missing-01 or ItemMismatch, Price Mismatch, price/qty mismatch then call getRemediationUsersAPI with buyer flag
					// call remediation for BUYER
				ResponseEntity<?>	 responseBuyer    = odataGetRemediationDetailsForBuyerAndGrnWithCreatedByAndPurchGrp(create, "BUYER",
							"/PurchOrdDetailsSet?", "PurchaseOrderCreator");
				//  get data of the remediation user with the success check 
				
				}
				if(invoiceSubmit.getInvoice().getInvoiceStatus().equals(ApplicationConstants.NO_GRN )){
					// call remediation for GRN by group 
					 List<List<String>>   listOfprNumAndprItem = checkPurReqNumAndPurReqItem(dtoList);
	                  List<String>   prNumList = listOfprNumAndprItem.get(0);
	                  List<String>   prItemList = listOfprNumAndprItem.get(1);
					if((prNumList.isEmpty() || prNumList==null)&& (prItemList.isEmpty()||prItemList==null)){
					
			    ResponseEntity<?> responsePurchGrp	= odataGetRemediationDetailsForBuyerAndGrnWithCreatedByAndPurchGrp(create, "GRN",
								"/PurchGrpDetailsSet?", "PurchasingGroup");
			    // get data of the remediation GRN user by purchase Group  with success Check 
			
					}else {
						// get data of the remediation GRN user by request Id and Request Num 
						ResponseEntity<?>  responseOfGrnPurReqAndItem =	odataGetRemediationDetailsForGrnByPurchReqAndPurchReqItem(prNumList,prItemList,"GRN","/PurchReqDetailsSet?");
						 System.err.println("responseOfGrnPurReqAndItem "+responseOfGrnPurReqAndItem);
						// get data for remediation GRN user by purReqNum and purReqItem
						 
						 
					}
					
				   }
				}
				// update the remediation user list in invoice Object 
			   return null;
			}
			// Accountant Submit For Approval
			else if(invoiceSubmit.getActionCode().equals(ApplicationConstants.ACCOUNTANT_SUBMIT_FOR_APPROVAL)){
				
			        if(invoiceSubmit.getInvoice().getInvoiceStatus().equals(ApplicationConstants.READY_TO_POST)){
					
			        	// get process lead user 
			        	
				}
				
			    // update the Process lead user list in Invoice Object 
				return null;
			}
			else {
				
				// Accountant Submit For Reject
				
		            // get process lead user for reject 
				
				// update the process lead user list in invocieObject 
				
				return null;
			}
			
		} 
		else {
			invoiceSubmit.set
			return ResponseEntity<invoiceSubmit>()
		}*/
			return null;
         }
			
}
