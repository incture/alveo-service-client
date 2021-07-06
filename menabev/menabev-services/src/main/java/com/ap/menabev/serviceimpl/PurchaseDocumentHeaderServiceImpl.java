package com.ap.menabev.serviceimpl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ap.menabev.dto.AddPoInputDto;
import com.ap.menabev.dto.AddPoInputDto.PurchaseOrder;
import com.ap.menabev.dto.AddPoOutputDto;
import com.ap.menabev.dto.InvoiceChangeIndicator;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.InvoiceHeaderObjectDto;
import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.dto.PoDeliveryCostHistoryDto;
import com.ap.menabev.dto.PoHeaderOdata;
import com.ap.menabev.dto.PoHistoryDto;
import com.ap.menabev.dto.PoHistoryTotalsDto;
import com.ap.menabev.dto.PoItemAccountAssignDto;
import com.ap.menabev.dto.PoItemServicesDto;
import com.ap.menabev.dto.PoSchedulesDto;
import com.ap.menabev.dto.PoSearchApiDto;
import com.ap.menabev.dto.PurchaseDocumentHeaderDto;
import com.ap.menabev.dto.PurchaseDocumentItemDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.RootOdata;
import com.ap.menabev.dto.ToDeliveryCostHistoryOata;
import com.ap.menabev.dto.ToHistoryOdata;
import com.ap.menabev.dto.ToHistoryTotalOdata;
import com.ap.menabev.dto.ToItemOdata;
import com.ap.menabev.dto.ToItemOdata.Results;
import com.ap.menabev.dto.TwoWayMatchInputDto;
import com.ap.menabev.entity.PoDeliveryCostHistoryDo;
import com.ap.menabev.entity.PoHistoryDo;
import com.ap.menabev.entity.PoHistoryTotalsDo;
import com.ap.menabev.entity.PoItemAccountAssignDo;
import com.ap.menabev.entity.PoItemServicesDo;
import com.ap.menabev.entity.PoSchedulesDo;
import com.ap.menabev.entity.PurchaseDocumentHeaderDo;
import com.ap.menabev.entity.PurchaseDocumentItemDo;
import com.ap.menabev.invoice.PoDeliveryCostHistoryRepository;
import com.ap.menabev.invoice.PoHistoryRepository;
import com.ap.menabev.invoice.PoHistoryTotalsRepository;
import com.ap.menabev.invoice.PoItemAccountAssignRepository;
import com.ap.menabev.invoice.PoItemServicesRepository;
import com.ap.menabev.invoice.PoSchedulesRepository;
import com.ap.menabev.invoice.PurchaseDocumentHeaderRepository;
import com.ap.menabev.invoice.PurchaseDocumentItemRespository;
import com.ap.menabev.service.PurchaseDocumentHeaderService;
import com.ap.menabev.util.AccountAssignmentDto;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;
import com.google.gson.Gson;

@Service
public class PurchaseDocumentHeaderServiceImpl implements PurchaseDocumentHeaderService {

	private static final Logger logger = LoggerFactory.getLogger(PurchaseDocumentHeaderServiceImpl.class);

	@Autowired
	PurchaseDocumentHeaderRepository poHeaderRepository;

	@Autowired
	PurchaseDocumentItemRespository purchaseDocumentItemRespository;

	@Autowired
	PoDeliveryCostHistoryRepository poDeliveryCostHistoryRepository;

	@Autowired
	PoHistoryRepository poHistoryRepository;

	@Autowired
	PoHistoryTotalsRepository poHistoryTotalsRepository;

	@Autowired
	PoSchedulesRepository poSchedulesRepository;

	@Autowired
	PoItemAccountAssignRepository poItemAccountAssignRepository;

	@Autowired
	PoItemServicesRepository poItemServicesRepository;

	@Autowired
	EntityManager entityManager;

	@Autowired
	PoSearchApiServiceImpl poSearchApiServiceImpl;

	@Autowired
	DuplicatecheckServiceImpl duplicatecheckServiceImpl;

	@Autowired
	InvoiceHeaderServiceImpl invoiceHeaderServiceImpl;

	@Override
	public ResponseDto save(PurchaseDocumentHeaderDto dto) {
		ResponseDto response = new ResponseDto();
		ModelMapper mapper = new ModelMapper();
		try {
			poHeaderRepository.save(mapper.map(dto, PurchaseDocumentHeaderDo.class));
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(ApplicationConstants.CREATED_SUCCESS);
			return response;
		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.CREATE_FAILURE);
			return response;
		}
	}

	@Override
	public List<PurchaseDocumentHeaderDto> getAll() {
		List<PurchaseDocumentHeaderDto> list = new ArrayList<>();
		ModelMapper mapper = new ModelMapper();
		try {
			List<PurchaseDocumentHeaderDo> entityList = poHeaderRepository.findAll();
			for (PurchaseDocumentHeaderDo entity : entityList) {
				list.add(mapper.map(entity, PurchaseDocumentHeaderDto.class));
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return list;
		}
	}

	@Override
	public ResponseDto delete(Integer id) {
		ResponseDto response = new ResponseDto();
		try {
			poHeaderRepository.deleteById(id);
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(ApplicationConstants.DELETE_SUCCESS);
			return response;

		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.DELETE_FAILURE);
			return response;
		}
	}

	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public List<ResponseDto> saveOrUpdate(List<PurchaseDocumentHeaderDto> dto) {
		List<ResponseDto> response = new ArrayList<>();
		System.out.println("Size " + dto.size());
		ModelMapper mapper = new ModelMapper();
		try {
			if (!ServiceUtil.isEmpty(dto)) {

				for (PurchaseDocumentHeaderDto getOneHeader : dto) {
					ResponseDto forResponse = new ResponseDto();
					if (!ServiceUtil.isEmpty(getOneHeader.getDocumentNumber())) {

						Query getDocumentQuery = entityManager.createQuery(
								"select documentNumber From PurchaseDocumentHeaderDo where documentNumber = '"
										+ getOneHeader.getDocumentNumber() + "'");
						List<String> documentNumber = getDocumentQuery.getResultList();
						if (!ServiceUtil.isEmpty(documentNumber)) {
							Integer poHeaderDelete = poHeaderRepository
									.deleteByDocumentNumber(getOneHeader.getDocumentNumber());
							Integer poItemDelete = purchaseDocumentItemRespository
									.deleteByDocumentNumber(getOneHeader.getDocumentNumber());
							Integer poDeliveryDelete = poDeliveryCostHistoryRepository
									.deleteByDocumentNumber(getOneHeader.getDocumentNumber());
							Integer poHistoryDelete = poHistoryRepository
									.deleteByDocumentNumber(getOneHeader.getDocumentNumber());
							Integer poHistoryTotalsDelete = poHistoryTotalsRepository
									.deleteByDocumentNumber(getOneHeader.getDocumentNumber());
							Integer poSchedulesDelete = poSchedulesRepository
									.deleteByDocumentNumber(getOneHeader.getDocumentNumber());
							Integer poItemAccountAssignDelete = poItemAccountAssignRepository
									.deleteByDocumentNumber(getOneHeader.getDocumentNumber());
							Integer poPoItemServicesDelete = poItemServicesRepository
									.deleteByDocumentNumber(getOneHeader.getDocumentNumber());
							if (!ServiceUtil.isEmpty(poHeaderDelete)) {
								System.out.println("DELETE VALUE:::::::" + poHeaderDelete);
								System.out.println("CURRENT :::::" + getOneHeader.getDocumentNumber());
								forResponse.setMessage(
										getOneHeader.getDocumentNumber() + " " + ApplicationConstants.UPDATE_SUCCESS);
							}
						} else {
							System.out.println("CURRENT2 :::::" + getOneHeader.getDocumentNumber());
							forResponse.setMessage(
									getOneHeader.getDocumentNumber() + " " + ApplicationConstants.CREATED_SUCCESS);
						}
						PurchaseDocumentHeaderDo purchaseOrder = mapper.map(getOneHeader,
								PurchaseDocumentHeaderDo.class);
						if (!ServiceUtil.isEmpty(getOneHeader.getPoItem())) {
							for (PurchaseDocumentItemDto poItem : getOneHeader.getPoItem()) {
								PurchaseDocumentItemDo pItem = mapper.map(poItem, PurchaseDocumentItemDo.class);
								if (!ServiceUtil.isEmpty(poItem.getPoItemServices())) {
									for (PoItemServicesDto itemService : poItem.getPoItemServices()) {
										PoItemServicesDo iService = mapper.map(itemService, PoItemServicesDo.class);
										iService.setItemServiceUuid(UUID.randomUUID().toString());
										poItemServicesRepository.save(iService);
									}
								}
								if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
									for (PoItemAccountAssignDto itemAccountAssignment : poItem
											.getPoAccountAssigment()) {
										PoItemAccountAssignDo iAccountAssignment = mapper.map(itemAccountAssignment,
												PoItemAccountAssignDo.class);
										iAccountAssignment.setPoAcctAssgnUuid(UUID.randomUUID().toString());
										poItemAccountAssignRepository.save(iAccountAssignment);
									}
								}
								pItem.setItemUuid(UUID.randomUUID().toString());
								if (!ServiceUtil.isEmpty(poItem.getSchedules())) {
									for (PoSchedulesDto poSchedules : poItem.getSchedules()) {
										PoSchedulesDo pSchedules = mapper.map(poSchedules, PoSchedulesDo.class);
										pSchedules.setPoScheduleUuid(UUID.randomUUID().toString());
										poSchedulesRepository.save(pSchedules);
									}
								}
								purchaseDocumentItemRespository.save(pItem);
							}
							if (!ServiceUtil.isEmpty(getOneHeader.getPoDeliveryCostHistory())) {
								for (PoDeliveryCostHistoryDto costHistory : getOneHeader.getPoDeliveryCostHistory()) {
									PoDeliveryCostHistoryDo cDelivery = mapper.map(costHistory,
											PoDeliveryCostHistoryDo.class);
									cDelivery.setPoDelivCostHistoryUuid(UUID.randomUUID().toString());
									poDeliveryCostHistoryRepository.save(cDelivery);
								}
							}
							if (!ServiceUtil.isEmpty(getOneHeader.getPoHistory())) {
								for (PoHistoryDto poHistory : getOneHeader.getPoHistory()) {
									PoHistoryDo pHistory = mapper.map(poHistory, PoHistoryDo.class);
									pHistory.setPoItemHistoryUuid(UUID.randomUUID().toString());
									poHistoryRepository.save(pHistory);
								}
							}
							if (!ServiceUtil.isEmpty(getOneHeader.getPoHistoryTotals())) {
								for (PoHistoryTotalsDto poHistorytotals : getOneHeader.getPoHistoryTotals()) {
									PoHistoryTotalsDo pHistorytotals = mapper.map(poHistorytotals,
											PoHistoryTotalsDo.class);
									pHistorytotals.setPoHistoryTotalUuid(UUID.randomUUID().toString());
									poHistoryTotalsRepository.save(pHistorytotals);
								}
							}
							purchaseOrder.setUuid(UUID.randomUUID().toString());
							poHeaderRepository.save(purchaseOrder);
							forResponse.setCode(ApplicationConstants.CODE_SUCCESS);
							forResponse.setStatus(ApplicationConstants.SUCCESS);

						}

					}
					response.add(forResponse);
				}
			}
		} catch (Exception e) {
			ResponseDto exception = new ResponseDto();
			exception.setCode(ApplicationConstants.CODE_FAILURE);
			exception.setStatus(ApplicationConstants.FAILURE);
			exception.setMessage(ApplicationConstants.CREATE_FAILURE + " Due to " + e.getMessage());
			response.add(exception);

		}
		return response;
	}

	// AddPoApi TODO
	@SuppressWarnings("static-access")
	@Override
	public AddPoOutputDto savePo(AddPoInputDto dto) throws URISyntaxException, IOException, ParseException {

		List<PurchaseDocumentHeaderDto> headerDto = new ArrayList<>();
		AddPoOutputDto addPoReturn = new AddPoOutputDto();
		if (!ServiceUtil.isEmpty(dto)) {
			for (PurchaseOrder poNumber : dto.getPurchaseOrder()) {
				if ("F".equals(poNumber.getDocumentCategory())) {

					Map<String, Object> map = poSearchApiServiceImpl.getDestination("SD4_DEST");
					String url = "/sap/opu/odata/sap/ZP2P_API_PODETAILS_SRV/HeaderSet?"
							+ "$expand=ToHeaderNote,ToHistory,ToHistoryTotal,ToDeliveryCostHistory,ToPartner,"
							+ "ToItem/ToItemNote,ToItem/ToItemAccountAssignment,ToItem/ToItemPricingElement,"
							+ "ToItem/ToItemScheduleLine,ToItem/ToItemService,ToItem/ToItemHistory,"
							+ "ToItem/ToItemHistoryTotal,ToItem/ToItemDeliveryCostHistory"
							+ "&$format=json&$filter=Number%20eq%20%27" + poNumber.getDocumentNumber() + "%27";
					if (!ServiceUtil.isEmpty(map)) {
						ResponseEntity<?> response = poSearchApiServiceImpl.consumingOdataService(url, null, "GET",
								map);
						System.out.println("RESPONSE:::::::::::::::431:::" + response.getBody());
						// String json_string = new Gson().toJson(an_object);
						if (!ServiceUtil.isEmpty(response)) {
							RootOdata obj = new Gson().fromJson(response.getBody().toString(), RootOdata.class);
							System.out.println("JSON OBJ::::::::::::" + obj.toString());
							PurchaseDocumentHeaderDto header = new PurchaseDocumentHeaderDto();
							header = getExtractedHeader(obj);
							headerDto.add(header);
						}

					}

				} else if ("L".equals(poNumber.getDocumentCategory())) {

					Map<String, Object> map = poSearchApiServiceImpl.getDestination("SD4_DEST");
					String url = "/sap/opu/odata/sap/ZP2P_API_SADETAILS_SRV/HeaderSet?$expand=ToHeaderNote,"
							+ "ToHistory,ToHistoryTotal,ToDeliveryCostHistory,ToPartner,ToItem/ToItemNote,"
							+ "ToItem/ToItemAccountAssignment,ToItem/ToItemPricingElement,ToItem/ToItemScheduleLine,"
							+ "ToItem/ToItemHistory,ToItem/ToItemHistoryTotal,ToItem/ToItemDeliveryCostHistory"
							+ "&$format=json&$filter=Number%20eq%20%27" + poNumber.getDocumentNumber() + "%27";
					if (!ServiceUtil.isEmpty(map)) {
						ResponseEntity<?> response = poSearchApiServiceImpl.consumingOdataService(url, null, "GET",
								map);
						System.out.println("RESPONSE:::::::::::::::431:::" + response.getBody());
						// String json_string = new Gson().toJson(an_object);
						if (!ServiceUtil.isEmpty(response)) {
							RootOdata obj = new Gson().fromJson(response.getBody().toString(), RootOdata.class);
							System.out.println("JSON OBJ::::::::::::" + obj.toString());
							PurchaseDocumentHeaderDto header = new PurchaseDocumentHeaderDto();
							header = getExtractedHeader(obj);
							headerDto.add(header);
						}

					}

				}
			}
		}
		// save in HANA DB
		List<ResponseDto> response = saveOrUpdate(headerDto);

		// iv. HeaderCheckAPI by passing venodrChange Indicator
		// v. Perform 2 way and 3way match (only for items that are not yet
		// matched)
		// 1. Loop at invoiceItem where isTwoWayMatched=false.
		System.out.println("360 " + dto);
		List<InvoiceItemDto> updatedItems = new ArrayList<>();
		Boolean twoWayMatch = true;
		if (!ServiceUtil.isEmpty(dto.getInvoiceHeader())) {
			if (!ServiceUtil.isEmpty(dto.getInvoiceHeader().getInvoiceItems())) {
				for (InvoiceItemDto item : dto.getInvoiceHeader().getInvoiceItems()) {

					System.out.println("I am here Auto Match");
					TwoWayMatchInputDto twoWayMatchDto = new TwoWayMatchInputDto();
					twoWayMatchDto.setInvoiceItem(item);
					twoWayMatchDto.setManualVsAuto(ApplicationConstants.AUTO_MATCH);
					twoWayMatchDto.setPurchaseDocumentHeader(headerDto);
					if (!ServiceUtil.isEmpty(dto.getInvoiceHeader().getVendorId())) {
						twoWayMatchDto.setVendorId(dto.getInvoiceHeader().getVendorId());
					}

					InvoiceItemDto twowayMatchUpdatedItem = duplicatecheckServiceImpl.twoWayMatch(twoWayMatchDto);
					System.out.println("MATCH :::: " + twowayMatchUpdatedItem.getIsTwowayMatched());
					if (!twowayMatchUpdatedItem.getIsTwowayMatched()) {
						twoWayMatch = true;
					}

					updatedItems.add(twowayMatchUpdatedItem);

				}

			}

		}

		System.out.println("Before Item Match :::" + dto.getInvoiceHeader());
		if (!ServiceUtil.isEmpty(updatedItems)) {

			System.out.println("Item Update");
			dto.getInvoiceHeader().setInvoiceItems(updatedItems);

		}
		InvoiceHeaderDto headerStatusUpdate = new InvoiceHeaderDto();
        //Determine header Status
		if(!ServiceUtil.isEmpty(dto.getInvoiceHeader())){
			headerStatusUpdate = duplicatecheckServiceImpl.determineHeaderStatus(dto.getInvoiceHeader());
			System.out.println("Header Status Update:::::::" + headerStatusUpdate);
		}
		
		// SaveApi
		if (!ServiceUtil.isEmpty(headerStatusUpdate)) {
			InvoiceChangeIndicator changeIndicators = new InvoiceChangeIndicator();
			changeIndicators.setItemChange(true);
			changeIndicators.setActivityLog(true);
			changeIndicators.setAttachementsChange(true);
			changeIndicators.setCommentChange(true);
			changeIndicators.setCostAllocationChange(true);
			changeIndicators.setHeaderChange(true);
			dto.getInvoiceHeader().setChangeIndicators(changeIndicators);
			invoiceHeaderServiceImpl.saveAPI(headerStatusUpdate);
			addPoReturn.setInvoiceObject(headerStatusUpdate);
		}

		System.out.println("After Item Match :::" + addPoReturn.getInvoiceObject());
		// vi. Call Java service – 3wayMatchAPI by using the payload.
		//
		// vii. Call SaveAPI with itemChangeIndicator.
		//
		// viii. Send the response from 3WayMatchAPI

		addPoReturn.setReferencePo(headerDto);

		return addPoReturn;

	}

	private static PurchaseDocumentHeaderDto getExtractedHeader(RootOdata obj) throws ParseException {
		PurchaseDocumentHeaderDto header = new PurchaseDocumentHeaderDto();
		// Header Etraction
		PoHeaderOdata jsonHeader = obj.getD().getResults().get(0);
		if (!ServiceUtil.isEmpty(jsonHeader.getCompCode())) {
			header.setCompCode(jsonHeader.getCompCode());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getCreatDate())) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date createDate = df.parse(jsonHeader.getCreatDate());
			header.setCreateDate(createDate.getTime());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getCreatedBy())) {
			header.setCreatedBy(jsonHeader.getCreatedBy());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getCurrency())) {
			header.setCurrency(jsonHeader.getCurrency());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getCurrencyIso())) {
			header.setCurrencyISO(jsonHeader.getCurrencyIso());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getDeleteInd())) {
			header.setDetetetionInd(jsonHeader.getDeleteInd());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getDiffInv())) {
			header.setDiffInv(jsonHeader.getDiffInv());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getDocCategory())) {
			header.setDocCat(jsonHeader.getDocCategory());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getDocType())) {
			header.setDoctType(jsonHeader.getDocType());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getDocDate())) {
			System.out.println(jsonHeader.getDocDate());
			String s = jsonHeader.getDocDate();
			s = s.substring(s.indexOf("(") + 1);
			s = s.substring(0, s.indexOf(")"));
			System.out.println("HH::::" + Long.valueOf(s));
			header.setDocumentDate(Long.valueOf(s));
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getNumber())) {
			header.setDocumentNumber(jsonHeader.getNumber());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getDscnt1To())) {
			header.setDscnt1To(Double.valueOf(jsonHeader.getDscnt1To()));
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getDscnt2To())) {
			header.setDscnt2To(Double.valueOf(jsonHeader.getDscnt2To()));
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getDscnt3To())) {
			header.setDscnt3To(Double.valueOf(jsonHeader.getDscnt3To()));
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getDsctPct1())) {
			header.setDsctPct1(Double.valueOf(jsonHeader.getDsctPct1()));
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getDsctPct2())) {
			header.setDsctPct2(Double.valueOf(jsonHeader.getDsctPct2()));
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getItemIntvl())) {
			header.setItmInvl(jsonHeader.getItemIntvl());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getLastChangeDateTime())) {
			String result = jsonHeader.getLastChangeDateTime().substring(0,
					jsonHeader.getLastChangeDateTime().indexOf(".") - 1);
			System.out.println(result);
			Integer length = 14 - result.length();
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					result = result + "0";
				}
			}
			SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMddHHmmss");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			ZoneId destinationTimeZone = ZoneId.of("Asia/Riyadh");
			String date3 = LocalDateTime.parse(result, formatter).atOffset(ZoneOffset.UTC)
					.atZoneSameInstant(destinationTimeZone).format(formatter);
			System.out.println(date3);
			Date date5 = df2.parse(date3);
			System.out.println("aaa" + date5.getTime());

			header.setLasChangedAt(date5.getTime());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getPmnttrms())) {
			header.setPaymentTerms(jsonHeader.getPmnttrms());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getToDeliveryCostHistory())) {
			List<PoDeliveryCostHistoryDto> poDeliveryCostHistory = new ArrayList<>();
			poDeliveryCostHistory = getPoDeliveryCostHistoryExtracted(jsonHeader.getToDeliveryCostHistory());
			header.setPoDeliveryCostHistory(poDeliveryCostHistory);
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getToHistory())) {
			List<PoHistoryDto> poHistory = new ArrayList<>();
			poHistory = getPoHistoryExtracted(jsonHeader.getToHistory());

			header.setPoHistory(poHistory);
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getCompCode())) {
			List<PoHistoryTotalsDto> poHistoryTotals = new ArrayList<>();
			poHistoryTotals = getPoHistoryTotalsExtracted(jsonHeader.getToHistoryTotal());
			header.setPoHistoryTotals(poHistoryTotals);
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getCompCode())) {
			List<PurchaseDocumentItemDto> headerItem = new ArrayList<>();
			headerItem = getHeaderItemExtracted(jsonHeader.getToItem());
			header.setPoItem(headerItem);
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getPoRelInd())) {
			header.setPoRelIndicator(jsonHeader.getPoRelInd());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getDocStatus())) {
			header.setPoRelStatus(jsonHeader.getDocStatus());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getPurGroup())) {
			header.setPurchGroup(jsonHeader.getPurGroup());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getPurchOrg())) {
			header.setPurOrg(jsonHeader.getPurchOrg());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getStatus())) {
			header.setStatus(jsonHeader.getStatus());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getSupplPlnt())) {
			header.setSupplPlant(jsonHeader.getSupplPlnt());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getSupplVend())) {
			header.setSupplVend(jsonHeader.getSupplVend());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getCompCode())) {
			header.setVatCountry(jsonHeader.getVatCntry());
		}
		if (!ServiceUtil.isEmpty(jsonHeader.getCompCode())) {
			header.setVendorId(jsonHeader.getVendor());
		}

		return header;
	}

	private static List<PurchaseDocumentItemDto> getHeaderItemExtracted(ToItemOdata toItem) throws ParseException {
		List<PurchaseDocumentItemDto> returnDto = new ArrayList<>();
		List<Results> objects = toItem.getResults();
		for (Results obj : objects) {
			PurchaseDocumentItemDto item = new PurchaseDocumentItemDto();
			if (!ServiceUtil.isEmpty(obj.getNumber())) {
				item.setDocumentNumber(obj.getNumber());
			}
			if (!ServiceUtil.isEmpty(obj.getProducttype())) {
				item.setProductType(obj.getProducttype());
			}
			if (!ServiceUtil.isEmpty(obj.getInterArticleNum())) {
				item.setInterArticleNum(obj.getInterArticleNum());
			}
			if (!ServiceUtil.isEmpty(obj.getRefItem())) {
				item.setDocumentItem(obj.getRefItem());
			}
			if (!ServiceUtil.isEmpty(obj.getDeleteInd())) {
				item.setDeleteInd(obj.getDeleteInd());
			}
			if (!ServiceUtil.isEmpty(obj.getShortText())) {
				item.setShortText(obj.getShortText());
			}
			if (!ServiceUtil.isEmpty(obj.getMaterial())) {
				item.setMaterial(obj.getMaterial());
			}
			if (!ServiceUtil.isEmpty(obj.getMaterialExternal())) {
				item.setMaterialExt(obj.getMaterialExternal());
			}
			if (!ServiceUtil.isEmpty(obj.getEmaterial())) {
				item.setEMaterial(obj.getEmaterial());
			}
			if (!ServiceUtil.isEmpty(obj.getPlant())) {
				item.setPlant(obj.getPlant());
			}
			if (!ServiceUtil.isEmpty(obj.getStgeLoc())) {
				item.setStorageLoc(obj.getStgeLoc());
			}
			if (!ServiceUtil.isEmpty(obj.getTrackingno())) {
				item.setTrackingNo(obj.getTrackingno());
			}
			if (!ServiceUtil.isEmpty(obj.getMatlGroup())) {
				item.setMaterialGrp(obj.getMatlGroup());
			}
			if (!ServiceUtil.isEmpty(obj.getInfoRec())) {
				item.setInfoRecord(obj.getInfoRec());
			}
			if (!ServiceUtil.isEmpty(obj.getVendMat())) {
				item.setVendMat(obj.getVendMat());
			}
			if (!ServiceUtil.isEmpty(obj.getQuantity())) {
				item.setQuantity(Double.valueOf(obj.getQuantity()));
			}
			if (!ServiceUtil.isEmpty(obj.getPoUnit())) {
				item.setPoUnit(obj.getPoUnit());
			}
			if (!ServiceUtil.isEmpty(obj.getPoUnitIso())) {
				item.setPoUnitISO(obj.getPoUnitIso());
			}
			if (!ServiceUtil.isEmpty(obj.getOrderprUn())) {
				item.setOrderPriceUnit(obj.getOrderprUn());
			}
			if (!ServiceUtil.isEmpty(obj.getOrderprUnIso())) {
				item.setOrderPriceUnitISO(obj.getOrderprUnIso());
			}
			if (!ServiceUtil.isEmpty(obj.getConvDen1())) {
				item.setConvDen1(Double.valueOf(obj.getConvDen1()));
			}
			if (!ServiceUtil.isEmpty(obj.getConvNum1())) {
				item.setConvNum1(Double.valueOf(obj.getConvNum1()));
			}
			if (!ServiceUtil.isEmpty(obj.getNetPrice())) {
				item.setNetPrice(Double.valueOf(obj.getNetPrice()));
			}
			if (!ServiceUtil.isEmpty(obj.getPriceUnit())) {
				item.setPriceUnit(Double.valueOf(obj.getPriceUnit()));
			}
			if (!ServiceUtil.isEmpty(obj.getGrPrTime())) {
				item.setGrPrTime(Double.valueOf(obj.getGrPrTime()));
			}
			if (!ServiceUtil.isEmpty(obj.getTaxCode())) {
				item.setTaxCode(obj.getTaxCode());
			}
			if (!ServiceUtil.isEmpty(obj.getAcctasscat())) {
				item.setNoMoreGr((obj.isNoMoreGr()));
			}
			if (!ServiceUtil.isEmpty(obj.getAcctasscat())) {
				item.setFinalInvInd(obj.isFinalInv());
			}
			if (!ServiceUtil.isEmpty(obj.getItemCat())) {
				item.setItemCategory(obj.getItemCat());
			}
			if (!ServiceUtil.isEmpty(obj.getAcctasscat())) {
				item.setAccountAssCat(obj.getAcctasscat());
			}
			if (!ServiceUtil.isEmpty(obj.getDistrib())) {
				item.setDistribution(obj.getDistrib());
			}
			if (!ServiceUtil.isEmpty(obj.getPartInv())) {
				item.setPartInv(obj.getPartInv());
			}
			if (!ServiceUtil.isEmpty(obj.isGrInd())) {
				item.setGrInd((obj.isGrInd()));
			}
			if (!ServiceUtil.isEmpty(obj.isGrNonVal())) {
				// if("true".equals(Boolean.toString(obj.isGrNonVal()))){
				// item.setGr_non_val("1");
				// }else{
				// item.setGr_non_val("0");
				// }
				item.setGr_non_val(obj.isGrNonVal());
			}
			if (!ServiceUtil.isEmpty(obj.getAcctasscat())) {
				// if("true".equals(Boolean.toString(obj.isIrInd()))){
				// item.setIrInd("1");
				// }else{
				// item.setIrInd("0");
				// }
				item.setIrInd(obj.isIrInd());
			}
			if (!ServiceUtil.isEmpty(obj.getAcctasscat())) {
				// if("true".equals(Boolean.toString(obj.isGrBasediv()))){
				// item.setSrvBsdIVInd("1");
				// }else{
				// item.setSrvBsdIVInd("0");
				// }
				item.setGrBsdIVInd(obj.isGrBasediv());
			}
			if (!ServiceUtil.isEmpty(obj.getAcctasscat())) {
				// if("true".equals(Boolean.toString(obj.isSrvBasedIv()))){
				// item.setSrvBsdIVInd("1");
				// }else{
				// item.setSrvBsdIVInd("0");
				// }
				item.setSrvBsdIVInd(obj.isSrvBasedIv());
			}
			if (!ServiceUtil.isEmpty(obj.getAgreement())) {
				item.setAgreement((obj.getAgreement()));
			}
			if (!ServiceUtil.isEmpty(obj.getAgmtItem())) {
				item.setAgreeemntItm(obj.getAgmtItem());
			}
			if (!ServiceUtil.isEmpty(obj.getTaxjurcode())) {
				item.setTaxJurCode(obj.getTaxjurcode());
			}
			if (!ServiceUtil.isEmpty(obj.getSuppVendor())) {
				item.setSupplVendor(obj.getSuppVendor());
			}
			if (!ServiceUtil.isEmpty(obj.getPreqNo())) {
				item.setPreqNum(obj.getPreqNo());
			}
			if (!ServiceUtil.isEmpty(obj.getPreqItem())) {
				item.setPreqItem(obj.getPreqItem());
			}
			if (!ServiceUtil.isEmpty(obj.getAgreement())) {
				item.setContractNum(obj.getAgreement());
			}
			if (!ServiceUtil.isEmpty(obj.getAgmtItem())) {
				item.setContractItm(obj.getAgmtItem());
			}
			if (!ServiceUtil.isEmpty(obj.getRefDoc())) {
				item.setRefDocNum(obj.getRefDoc());
			}
			if (!ServiceUtil.isEmpty(obj.getRefItem())) {
				item.setRefDocItem(obj.getRefItem());
			}
			if (!ServiceUtil.isEmpty(obj.isDelivCompl())) {
				// if("true".equals(Boolean.toString(obj.isDelivCompl()))){
				// item.setDelivComplete("1");
				// }else{
				// item.setDelivComplete("0");
				// }
				item.setDelivComplete(obj.isDelivCompl());
			}
			if (!ServiceUtil.isEmpty(obj.getPartDeliv())) {
				item.setPartDelv(obj.getPartDeliv());
			}
			if (!ServiceUtil.isEmpty(obj.getItemChangeDate())) {
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				Date createDate = df.parse(obj.getItemChangeDate());
				item.setItemChangedAt(createDate.getTime());
			}
			if (!ServiceUtil.isEmpty(obj.getProducttype())) {
				item.setProductType((obj.getProducttype()));
			}
			if (!ServiceUtil.isEmpty(obj.getInterArticleNum())) {
				item.setInterArticleNum((obj.getInterArticleNum()));
			}

			// Account Assignment
			// Item Service
			List<com.ap.menabev.dto.ToItemAccountAssignment.Results> accountAssign = obj.getToItemAccountAssignment()
					.getResults();
			List<com.ap.menabev.dto.ToItemService.Results> itemService = obj.getToItemService().getResults();

			List<PoItemAccountAssignDto> poAccountAssigment = new ArrayList<>();
			for (com.ap.menabev.dto.ToItemAccountAssignment.Results assObj : accountAssign) {
				PoItemAccountAssignDto accDto = new PoItemAccountAssignDto();

				if (!ServiceUtil.isEmpty(assObj.getNumber())) {
					accDto.setDocumentNumber(assObj.getNumber());
				}
				if (!ServiceUtil.isEmpty(assObj.getItemNo())) {
					accDto.setDocumentItem(assObj.getItemNo());
				}
				if (!ServiceUtil.isEmpty(assObj.getSerialNo())) {
					accDto.setSerialNo(assObj.getSerialNo());
				}
				if (!ServiceUtil.isEmpty(assObj.isDeleteInd())) {
					// if("true".equals(Boolean.toString(assObj.isDeleteInd()))){
					// accDto.setDeleteInd("1");
					// }else{
					// accDto.setDeleteInd("0");
					// }
					accDto.setDeleteInd(assObj.isDeleteInd());
				}
				if (!ServiceUtil.isEmpty(assObj.getCreatDate())) {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					Date createDate = df.parse(assObj.getCreatDate());
					accDto.setCreatDate(Long.valueOf(createDate.getTime()));
				}
				if (!ServiceUtil.isEmpty(assObj.getQuantity())) {
					accDto.setQuantity(Double.valueOf(obj.getQuantity()));
				}
				if (!ServiceUtil.isEmpty(assObj.getDistrPerc())) {
					accDto.setDistPer(Double.valueOf(assObj.getDistrPerc()));
				}
				// TODO
				// if(!ServiceUtil.isEmpty(assObj.())){
				// accDto.setPoUnit(Double.valueOf(assObj.get));
				// }
				// if(!ServiceUtil.isEmpty(assObj.())){
				// accDto.setPoOrderPriceUnit(Double.valueOf(assObj.get));
				// }
				if (!ServiceUtil.isEmpty(assObj.getNetValue())) {
					accDto.setNetValue(Double.valueOf(assObj.getNetValue()));
				}
				if (!ServiceUtil.isEmpty(assObj.getGlAccount())) {
					accDto.setGlAccount(assObj.getGlAccount());
				}
				if (!ServiceUtil.isEmpty(assObj.getCostcenter())) {
					accDto.setCostCenter(assObj.getCostcenter());
				}
				if (!ServiceUtil.isEmpty(assObj.getAssetNo())) {
					accDto.setAssetNo(assObj.getAssetNo());
				}
				if (!ServiceUtil.isEmpty(assObj.getSubNumber())) {
					accDto.setSubNum(assObj.getSubNumber());
				}
				if (!ServiceUtil.isEmpty(assObj.getActtype())) {
					accDto.setActType(assObj.getActtype());
				}
				if (!ServiceUtil.isEmpty(assObj.getServiceDoc())) {
					accDto.setServiceDoc(assObj.getServiceDoc());
				}
				if (!ServiceUtil.isEmpty(assObj.getServiceItem())) {
					accDto.setServiceDocItem(assObj.getServiceItem());
				}
				if (!ServiceUtil.isEmpty(assObj.getServiceDocType())) {
					accDto.setServiceDocType(assObj.getServiceDocType());
				}
				if (!ServiceUtil.isEmpty(assObj.getCoArea())) {
					accDto.setCoArea(assObj.getCoArea());
				}
				if (!ServiceUtil.isEmpty(assObj.getProfitCtr())) {
					accDto.setProfitCtr(assObj.getProfitCtr());
				}
				if (!ServiceUtil.isEmpty(assObj.getOrderid())) {
					accDto.setOrderId(assObj.getOrderid());
				}
				
				poAccountAssigment.add(accDto);

			}
			item.setPoAccountAssigment(poAccountAssigment);

			List<PoItemServicesDto> poItemServices = new ArrayList<>();
			for (com.ap.menabev.dto.ToItemService.Results sobj : itemService) {
				PoItemServicesDto pis = new PoItemServicesDto();
				if (!ServiceUtil.isEmpty(sobj.getNumber())) {
					pis.setDocumentNumber(sobj.getNumber());
				}
				if (!ServiceUtil.isEmpty(sobj.getItemNo())) {
					pis.setDocumentItem(obj.getItemNo());
				}
				if (!ServiceUtil.isEmpty(sobj.getPckgNo())) {
					pis.setPckgNo(sobj.getPckgNo());
				}
				if (!ServiceUtil.isEmpty(sobj.getLineNo())) {
					pis.setLineNo(sobj.getLineNo());
				}
				if (!ServiceUtil.isEmpty(sobj.getExtLine())) {
					pis.setExtLine(sobj.getExtLine());
				}
				if (!ServiceUtil.isEmpty(sobj.getOutlLevel())) {
					pis.setOutlLevel(sobj.getOutlLevel());
				}
				if (!ServiceUtil.isEmpty(sobj.getOutlNo())) {
					pis.setOutlNo(sobj.getOutlNo());
				}
				if (!ServiceUtil.isEmpty(sobj.getOutlInd())) {
					pis.setOutlInd(sobj.getOutlInd());
				}
				if (!ServiceUtil.isEmpty(sobj.getSubpckgNo())) {
					pis.setSubpckgNo(sobj.getSubpckgNo());
				}
				if (!ServiceUtil.isEmpty(sobj.getServiceNo())) {
					pis.setService(sobj.getServiceNo());
				}
				if (!ServiceUtil.isEmpty(sobj.getServType())) {
					pis.setServType(sobj.getServType());
				}
				if (!ServiceUtil.isEmpty(sobj.getEdition())) {
					pis.setEdition(sobj.getEdition());
				}
				if (!ServiceUtil.isEmpty(sobj.getSscItem())) {
					pis.setSscItem(sobj.getSscItem());
				}
				if (!ServiceUtil.isEmpty(sobj.getExtServ())) {
					pis.setExtServ(sobj.getExtServ());
				}
				if (!ServiceUtil.isEmpty(sobj.getQuantity())) {
					pis.setQuantity(Double.valueOf(sobj.getQuantity()));
				}
				if (!ServiceUtil.isEmpty(sobj.getBaseUom())) {
					pis.setBaseUom(sobj.getBaseUom());
				}
				if (!ServiceUtil.isEmpty(sobj.getUomIso())) {
					pis.setUomIso(sobj.getUomIso());
				}
				if (!ServiceUtil.isEmpty(sobj.getOvfTol())) {
					pis.setOvfTol(Double.valueOf(sobj.getOvfTol()));
				}
				// if(!ServiceUtil.isEmpty(sobj.)){
				// pis.setOvfUnlim(obj.getun); TODO
				// }
				if (!ServiceUtil.isEmpty(sobj.getPriceUnit())) {
					pis.setPriceUnit(Double.valueOf(sobj.getPriceUnit()));
				}
				if (!ServiceUtil.isEmpty(sobj.getGrPrice())) {
					pis.setGrPrice(Double.valueOf(sobj.getGrPrice()));
				}
				if (!ServiceUtil.isEmpty(sobj.getFromLine())) {
					pis.setFromLine(sobj.getFromLine());
				}
				if (!ServiceUtil.isEmpty(sobj.getToLine())) {
					pis.setToLine(sobj.getToLine());
				}
				if (!ServiceUtil.isEmpty(sobj.getShortText())) {
					pis.setShortText(sobj.getShortText());
				}
				if (!ServiceUtil.isEmpty(sobj.getDistrib())) {
					pis.setDistrib(sobj.getDistrib());
				}
				if (!ServiceUtil.isEmpty(sobj.getTaxCode())) {
					pis.setTaxCode(sobj.getTaxCode());
				}
				if (!ServiceUtil.isEmpty(sobj.getTaxjurcode())) {
					pis.setTaxjurcode(sobj.getTaxjurcode());
				}
				if (!ServiceUtil.isEmpty(sobj.getNetValue())) {
					pis.setNetValue(Double.valueOf(sobj.getNetValue()));
				}
				poItemServices.add(pis);
			}
			item.setPoItemServices(poItemServices);

			List<PoSchedulesDto> itemSchedules = new ArrayList<>();
			List<com.ap.menabev.dto.ToItemScheduleLine.Results> schedules = obj.getToItemScheduleLine().getResults();
			for (com.ap.menabev.dto.ToItemScheduleLine.Results osc : schedules) {
				PoSchedulesDto isc = new PoSchedulesDto();
				if (!ServiceUtil.isEmpty(osc.getNumber())) {
					isc.setDocumentNumber(osc.getNumber());
				}
				if (!ServiceUtil.isEmpty(osc.getItemNo())) {
					isc.setItemNo(osc.getItemNo());
				}
				if (!ServiceUtil.isEmpty(osc.getSchedLine())) {
					isc.setSchedLine(osc.getSchedLine());
				}
				if (!ServiceUtil.isEmpty(osc.getDelDatcatExt())) {
					isc.setDelDatcatExt(osc.getDelDatcatExt());
				}
				if (!ServiceUtil.isEmpty(osc.getDeliveryDate())) {
					isc.setDeliveryDate(osc.getDeliveryDate());
				}
				if (!ServiceUtil.isEmpty(osc.getQuantity())) {
					isc.setQuantity(Double.valueOf(osc.getQuantity()));
				}
				if (!ServiceUtil.isEmpty(osc.getDelivTime())) {
					String result = osc.getDelivTime().replaceAll("[a-zA-Z]", "");
					// SimpleDateFormat df = new SimpleDateFormat("HHmmss");
					// Date createDate = df.parse(result);
					isc.setDelivTime(Long.valueOf(result));
					// isc.setDelivTime(localTime.get)));
				}
				if (!ServiceUtil.isEmpty(osc.getStatDate())) {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					Date createDate = df.parse(osc.getStatDate());
					isc.setStatDate(createDate.getTime());
				}
				if (!ServiceUtil.isEmpty(osc.getPreqNo())) {
					isc.setPreqNo(osc.getPreqNo());
				}
				if (!ServiceUtil.isEmpty(osc.getPreqItem())) {
					isc.setPreqItem(osc.getPreqItem());
				}
				if (!ServiceUtil.isEmpty(osc.getPoDate())) {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					Date createDate = df.parse(osc.getPoDate());
					isc.setPoDate(createDate.getTime());
				}
				if (!ServiceUtil.isEmpty(osc.getDeleteInd())) {
					isc.setDeleteInd(osc.getDeleteInd());
				}
				// if(!ServiceUtil.isEmpty(osc.())){
				// isc.setReqClosed(osc.get)
				// }
				// TODO
				itemSchedules.add(isc);

			}
			item.setSchedules(itemSchedules);
			returnDto.add(item);
		}

		return returnDto;
	}

	private static List<PoHistoryTotalsDto> getPoHistoryTotalsExtracted(ToHistoryTotalOdata toHistoryTotal) {
		List<PoHistoryTotalsDto> returnDto = new ArrayList<>();
		List<com.ap.menabev.dto.ToHistoryTotalOdata.Results> objects = toHistoryTotal.getResults();
		for (com.ap.menabev.dto.ToHistoryTotalOdata.Results obj : objects) {
			PoHistoryTotalsDto forReturn = new PoHistoryTotalsDto();
			if (!ServiceUtil.isEmpty(obj.getBlockedQy())) {
				forReturn.setBlockedQy(Double.valueOf(obj.getBlockedQy()));
			}
			if (!ServiceUtil.isEmpty(obj.getBlQty())) {
				forReturn.setBlQty(Double.valueOf(obj.getBlQty()));
			}
			if (!ServiceUtil.isEmpty(obj.getBlQtyTotal())) {
				forReturn.setBlQtyTotal(Double.valueOf(obj.getBlQtyTotal()));
			}
			if (!ServiceUtil.isEmpty(obj.getClValFor())) {
				forReturn.setClValFor(Double.valueOf(obj.getClValFor()));
			}
			if (!ServiceUtil.isEmpty(obj.getClValLoc())) {
				forReturn.setClValLoc(Double.valueOf(obj.getClValLoc()));
			}
			if (!ServiceUtil.isEmpty(obj.getBlockedQy())) {
				forReturn.setCurrency(obj.getCurrency());
			}
			if (!ServiceUtil.isEmpty(obj.getCurrencyIso())) {
				forReturn.setCurrencyIso(obj.getCurrencyIso());
			}
			if (!ServiceUtil.isEmpty(obj.getDelivQty())) {
				forReturn.setDelivQty(Double.valueOf(obj.getDelivQty()));
			}
			if (!ServiceUtil.isEmpty(obj.getDlQtyTotal())) {
				forReturn.setDlQtyTotal(Double.valueOf(obj.getDlQtyTotal()));
			}
			if (!ServiceUtil.isEmpty(obj.getDlQtyTrsp())) {
				forReturn.setDlQtyTrsp(Double.valueOf(obj.getDlQtyTrsp()));
			}
			if (!ServiceUtil.isEmpty(obj.getItemNo())) {
				forReturn.setDocumentItem(obj.getItemNo());
			}
			if (!ServiceUtil.isEmpty(obj.getNumber())) {
				forReturn.setDocumentNumber(obj.getNumber());
			}
			if (!ServiceUtil.isEmpty(obj.getDopVlLoc())) {
				forReturn.setDopVlLoc(Double.valueOf(obj.getDopVlLoc()));
			}
			if (!ServiceUtil.isEmpty(obj.getIvQty())) {
				forReturn.setIvQty(Double.valueOf(obj.getIvQty()));
			}
			if (!ServiceUtil.isEmpty(obj.getIvQtyPo())) {
				forReturn.setIvQtyPo(Double.valueOf(obj.getIvQtyPo()));
			}
			if (!ServiceUtil.isEmpty(obj.getIvQtyTotal())) {
				forReturn.setIvQtyTotal(Double.valueOf(obj.getIvQtyTotal()));
			}
			if (!ServiceUtil.isEmpty(obj.getIvvalFor())) {
				forReturn.setIvvalFor(Double.valueOf(obj.getIvvalFor()));
			}
			if (!ServiceUtil.isEmpty(obj.getIvvalLoc())) {
				forReturn.setIvvalLoc(Double.valueOf(obj.getIvvalLoc()));
			}

			if (!ServiceUtil.isEmpty(obj.getPoPrQnt())) {
				forReturn.setPoPrQnt(Double.valueOf(obj.getPoPrQnt()));
			}
			if (!ServiceUtil.isEmpty(obj.getSerialNo())) {
				forReturn.setSerialNo(obj.getSerialNo());
			}

			if (!ServiceUtil.isEmpty(obj.getValGrFor())) {
				forReturn.setValGrFor(Double.valueOf(obj.getValGrFor()));
			}
			if (!ServiceUtil.isEmpty(obj.getValGrLoc())) {
				forReturn.setValGrLoc(Double.valueOf(obj.getValGrLoc()));
			}
			if (!ServiceUtil.isEmpty(obj.getValIvLoc())) {
				forReturn.setValIvLoc(Double.valueOf(obj.getValIvLoc()));
			}
			if (!ServiceUtil.isEmpty(obj.getValIvFor())) {
				forReturn.setValIvFor(Double.valueOf(obj.getValIvFor()));
			}
			if (!ServiceUtil.isEmpty(obj.getBlockedQy())) {
				forReturn.setWithdrQty(Double.valueOf(obj.getWithdrQty()));
			}

			returnDto.add(forReturn);
		}
		return returnDto;
	}

	private static List<PoHistoryDto> getPoHistoryExtracted(ToHistoryOdata toHistory) throws ParseException {
		List<PoHistoryDto> responseDto = new ArrayList<>();
		List<com.ap.menabev.dto.ToHistoryOdata.Results> objects = toHistory.getResults();
		for (com.ap.menabev.dto.ToHistoryOdata.Results obj : objects) {
			PoHistoryDto forResponse = new PoHistoryDto();
			if (!ServiceUtil.isEmpty(obj.getAccountingDocumentCreationTime())) {
				forResponse.setAccountingDocCreationTime(Long.valueOf(obj.getAccountingDocumentCreationTime()));
			}
			// if
			// (!ServiceUtil.isEmpty(obj.getAccountingDocumentCreationDate())) {
			// forResponse.setAccountingDocCreationDate(Long.valueOf(obj.getAccountingDocumentCreationDate()));
			// }
			if (!ServiceUtil.isEmpty(obj.getAccountAssignmentNumber())) {
				forResponse.setAcctAssgnNum(obj.getAccountAssignmentNumber());
			}
			if (!ServiceUtil.isEmpty(obj.getGRIRAcctClrgAmtInCoCodeCrcy())) {
				forResponse.setAmountClForCurr(Double.valueOf(obj.getGRIRAcctClrgAmtInCoCodeCrcy()));
			}
			if (!ServiceUtil.isEmpty(obj.getGRIRAcctClrgAmtInTransacCrcy())) {
				forResponse.setAmountClLocCurr(Double.valueOf(obj.getGRIRAcctClrgAmtInTransacCrcy()));
			}
			if (!ServiceUtil.isEmpty(obj.getInvoiceAmountInFrgnCurrency())) {
				forResponse.setAmountForCurr(Double.valueOf(obj.getInvoiceAmountInFrgnCurrency()));
			}
			if (!ServiceUtil.isEmpty(obj.getInvoiceAmtInCoCodeCrcy())) {
				forResponse.setAmountLocCurr(Double.valueOf(obj.getInvoiceAmtInCoCodeCrcy()));
			}
			if (!ServiceUtil.isEmpty(obj.getVltdGdsRcptBlkdQtyInOrdPrcUnit())) {
				forResponse.setBlockedQtyOPU(Double.valueOf(obj.getVltdGdsRcptBlkdQtyInOrdPrcUnit()));
			}
			if (!ServiceUtil.isEmpty(obj.getVltdGdsRcptBlkdStkQtyInOrdUnit())) {
				forResponse.setBlockedQtyOU(Double.valueOf(obj.getVltdGdsRcptBlkdStkQtyInOrdUnit()));
			}
			if (!ServiceUtil.isEmpty(obj.getCompanyCodeCurrency())) {
				forResponse.setCompanyCodeCurr(obj.getCompanyCodeCurrency());
			}
			if (!ServiceUtil.isEmpty(obj.getReferenceDocument())) {
				forResponse.setContitionDoc(obj.getReferenceDocument());
			}
			if (!ServiceUtil.isEmpty(obj.getCurrency())) {
				forResponse.setCurrency(obj.getCurrency());
			}
			if (!ServiceUtil.isEmpty(obj.getAccountAssignmentNumber())) {
				forResponse.setCurrencyISO(obj.getCurrencyIso());
			}
			if (!ServiceUtil.isEmpty(obj.getDebitCreditCode())) {
				forResponse.setDebitCreditInd(obj.getDebitCreditCode());
			}
			if (!ServiceUtil.isEmpty(obj.getQuantityInDeliveryQtyUnit())) {
				forResponse.setDelivQty(Double.valueOf(obj.getQuantityInDeliveryQtyUnit()));
			}
			if (!ServiceUtil.isEmpty(obj.getDeliveryQuantityUnit())) {
				forResponse.setDelivUnit(obj.getDeliveryQuantityUnit());
			}
			if (!ServiceUtil.isEmpty(obj.getDelivUnitIso())) {
				forResponse.setDelivUnitISO(obj.getDelivUnitIso());
			}
			if (!ServiceUtil.isEmpty(obj.getDocumentDate())) {
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				Date date = df.parse(obj.getDocumentDate());
				forResponse.setDocumentDate(date.getTime());
			}
			if (!ServiceUtil.isEmpty(obj.getItemNo())) {
				forResponse.setDocumentItem(obj.getItemNo());
			}
			if (!ServiceUtil.isEmpty(obj.getNumber())) {
				forResponse.setDocumentNumber(obj.getNumber());
			}
			if (!ServiceUtil.isEmpty(obj.getEntryDate())) {
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				Date date = df.parse(obj.getAccountingDocumentCreationDate());
				forResponse.setEntryDate(date.getTime());
			}
			if (!ServiceUtil.isEmpty(obj.getMaterialExternal())) {
				forResponse.setExtenalMaterial(obj.getMaterialExternal());
			}
			if (!ServiceUtil.isEmpty(obj.getExtRow())) {
				forResponse.setExtRow(obj.getExtRow());
			}
			if (!ServiceUtil.isEmpty(obj.getGoodsMovementType())) {
				forResponse.setGoodsMvmtType(obj.getGoodsMovementType());
			}
			if (!ServiceUtil.isEmpty(obj.getPurchasingHistoryDocument())) {
				forResponse.setHistoryDocument(obj.getPurchasingHistoryDocument());
			}
			if (!ServiceUtil.isEmpty(obj.getPurchasingHistoryCategory())) {
				forResponse.setHistoryCategory(obj.getPurchasingHistoryCategory());
			}
			if (!ServiceUtil.isEmpty(obj.getReferenceDocumentItem())) {
				forResponse.setHistoryDocumentItem(obj.getReferenceDocumentItem());
			}
			if (!ServiceUtil.isEmpty(obj.getPurchasingHistoryDocumentType())) {
				forResponse.setHistoryType(obj.getPurchasingHistoryDocumentType());
			}
			if (!ServiceUtil.isEmpty(obj.getPurchasingHistoryDocumentYear())) {
				forResponse.setHistoryYear(obj.getPurchasingHistoryDocumentYear());
			}
			if (!ServiceUtil.isEmpty(obj.getIntRow())) {
				forResponse.setIntRow(obj.getIntRow());
			}
			if (!ServiceUtil.isEmpty(obj.getInvoiceAmountInFrgnCurrency())) {
				forResponse.setInvoiceAmountForCurr(Double.valueOf(obj.getInvoiceAmountInFrgnCurrency()));
			}
			if (!ServiceUtil.isEmpty(obj.getInvoiceAmtInCoCodeCrcy())) {
				forResponse.setInvoiceAmountLocCurr(Double.valueOf(obj.getInvoiceAmtInCoCodeCrcy()));
			}
			if (!ServiceUtil.isEmpty(obj.getLocCurrIso())) {
				forResponse.setLocCurrISO(obj.getLocCurrIso());
			}
			if (!ServiceUtil.isEmpty(obj.getManufacturerMaterial())) {
				forResponse.setManufMat(obj.getManufacturerMaterial());
			}
			if (!ServiceUtil.isEmpty(obj.getMaterial())) {
				forResponse.setMaterial(obj.getMaterial());
			}
			if (!ServiceUtil.isEmpty(obj.isIsCompletelyDelivered())) {
				// if("true".equals(Boolean.toString(obj.isIsCompletelyDelivered()))){
				// forResponse.setNoMoreGR("1");
				// }else{
				// forResponse.setNoMoreGR("0");
				// }
				forResponse.setNoMoreGR(obj.isIsCompletelyDelivered());
			}
			if (!ServiceUtil.isEmpty(obj.getPackNo())) {
				forResponse.setPackNo(obj.getPackNo());
			}
			if (!ServiceUtil.isEmpty(obj.getPlant())) {
				forResponse.setPlant(obj.getPlant());
			}
			if (!ServiceUtil.isEmpty(obj.getPlnIntRow())) {
				forResponse.setPlnIntRow(obj.getPlnIntRow());
			}
			if (!ServiceUtil.isEmpty(obj.getPlnPackNo())) {
				forResponse.setPlnPackNo(obj.getPlnPackNo());
			}
			if (!ServiceUtil.isEmpty(obj.getPostingDate())) {
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				Date postingDate = df.parse(obj.getPostingDate().toString());
				forResponse.setPostingDate(postingDate.getTime());
			}
			if (!ServiceUtil.isEmpty(obj.getQuantity())) {
				forResponse.setQuantity(Double.valueOf(obj.getQuantity()));
			}
			if (!ServiceUtil.isEmpty(obj.getDocumentReferenceID())) {
				forResponse.setRefDocId(obj.getDocumentReferenceID());
			}
			if (!ServiceUtil.isEmpty(obj.getReferenceDocumentItem())) {
				forResponse.setRefDocItem(obj.getReferenceDocumentItem());
			}
			if (!ServiceUtil.isEmpty(obj.getRefDocNoLong())) {
				forResponse.setRefDocNumLong(obj.getRefDocNoLong());
			}
			if (!ServiceUtil.isEmpty(obj.getReferenceDocument())) {
				forResponse.setRefDocNum(obj.getReferenceDocument());
			}
			if (!ServiceUtil.isEmpty(obj.getReferenceDocumentFiscalYear())) {

				forResponse.setRefDocYear(obj.getReferenceDocumentFiscalYear());
			}
			if (!ServiceUtil.isEmpty(obj.getSrvPos())) {
				forResponse.setSrvpos(obj.getSrvPos());
			}
			if (!ServiceUtil.isEmpty(obj.getTaxCode())) {
				forResponse.setTaxCode(obj.getTaxCode());
			}
			if (!ServiceUtil.isEmpty(obj.getInventoryValuationType())) {
				forResponse.setValuationType(obj.getInventoryValuationType());
			}
			responseDto.add(forResponse);
		}
		return responseDto;
	}

	private static List<PoDeliveryCostHistoryDto> getPoDeliveryCostHistoryExtracted(
			ToDeliveryCostHistoryOata toDeliveryCostHistory) {
		List<com.ap.menabev.dto.ToDeliveryCostHistoryOata.Results> objects = toDeliveryCostHistory.getResults();
		List<PoDeliveryCostHistoryDto> returnDto = new ArrayList<>();
		for (com.ap.menabev.dto.ToDeliveryCostHistoryOata.Results obj : objects) {
			PoDeliveryCostHistoryDto forReturn = new PoDeliveryCostHistoryDto();
			if (!ServiceUtil.isEmpty(obj.getAccDoc())) {
				forReturn.setAccDoc(obj.getAccDoc());
			}
			if (!ServiceUtil.isEmpty(obj.getAccDocCreationDt())) {
				forReturn.setAccDocCreationDt(Long.valueOf(obj.getAccDocCreationDt()));
			}
			if (!ServiceUtil.isEmpty(obj.getAccDocCreationTm())) {
				forReturn.setAccDocCreationTm(Long.valueOf(obj.getAccDocCreationTm()));
			}
			if (!ServiceUtil.isEmpty(obj.getAmtDC())) {
				forReturn.setAmtDC(Double.valueOf(obj.getAmtDC()));
			}
			if (!ServiceUtil.isEmpty(obj.getAmtLC())) {
				forReturn.setAmtLC(Double.valueOf(obj.getAmtLC()));
			}
			if (!ServiceUtil.isEmpty(obj.getConditionCounter())) {
				forReturn.setConditionCounter(obj.getConditionCounter());
			}
			if (!ServiceUtil.isEmpty(obj.getConditionType())) {
				forReturn.setConditionType(obj.getConditionType());
			}
			if (!ServiceUtil.isEmpty(obj.getCondNetVal())) {
				forReturn.setCondNetVal(Double.valueOf(obj.getCondNetVal()));
			}
			if (!ServiceUtil.isEmpty(obj.getCreatorName())) {
				forReturn.setCreatorName(obj.getCreatorName());
			}
			if (!ServiceUtil.isEmpty(obj.getCurrencyKey())) {
				forReturn.setCurrencyKey(obj.getCurrencyKey());
			}
			if (!ServiceUtil.isEmpty(obj.getNumber())) {
				forReturn.setDocumentNumber(obj.getNumber());
			}
			if (!ServiceUtil.isEmpty(obj.getDrCrInd())) {
				forReturn.setDrCrInd(obj.getDrCrInd());
			}
			if (!ServiceUtil.isEmpty(obj.getDrCrInd1())) {
				forReturn.setDrCrInd1(obj.getDrCrInd1());
			}
			if (!ServiceUtil.isEmpty(obj.getExchgRate())) {
				forReturn.setExchgRate(Double.valueOf(obj.getExchgRate()));
			}
			if (!ServiceUtil.isEmpty(obj.getExchgRateDiffAmt())) {
				forReturn.setExchgRateDiffAmt(Double.valueOf(obj.getExchgRateDiffAmt()));
			}
			if (!ServiceUtil.isEmpty(obj.getFiscalYear())) {
				forReturn.setFiscalYear(obj.getFiscalYear());
			}
			if (!ServiceUtil.isEmpty(obj.getGrBillLading())) {
				forReturn.setGrBillLading(obj.getGrBillLading());
			}
			if (!ServiceUtil.isEmpty(obj.getGrBlkBaseUom())) {
				forReturn.setGrBlkBaseUom(Long.valueOf(obj.getGrBlkBaseUom()));
			}
			if (!ServiceUtil.isEmpty(obj.getGrIrClrValLC())) {
				forReturn.setGrIrClrValLC(Double.valueOf(obj.getGrIrClrValLC()));
			}
			if (!ServiceUtil.isEmpty(obj.getGrIrClrValPC())) {
				forReturn.setGrIrClrValPC(Double.valueOf(obj.getGrIrClrValPC()));
			}
			if (!ServiceUtil.isEmpty(obj.getExchgRate())) {
				forReturn.setGrIrClrValTC(Double.valueOf(obj.getGrIrClrValTC()));
			}
			if (!ServiceUtil.isEmpty(obj.getHistoryCategory())) {
				forReturn.setHistoryCategory(obj.getHistoryCategory());
			}
			if (!ServiceUtil.isEmpty(obj.getHistoryType())) {
				forReturn.setHistoryType(obj.getHistoryType());
			}
			if (!ServiceUtil.isEmpty(obj.getInvAmtPC())) {
				forReturn.setInvAmtPC(Double.valueOf(obj.getInvAmtPC()));
			}
			if (!ServiceUtil.isEmpty(obj.getInvValFC())) {
				forReturn.setInvValFC(Double.valueOf(obj.getInvValFC()));
			}
			if (!ServiceUtil.isEmpty(obj.getInvValLC())) {
				forReturn.setInvValLC(Double.valueOf(obj.getInvValLC()));
			}
			if (!ServiceUtil.isEmpty(obj.getItemNo())) {
				forReturn.setItemNo(obj.getItemNo());
			}
			if (!ServiceUtil.isEmpty(obj.getLocCurrency())) {
				forReturn.setLocCurrency(obj.getLocCurrency());
			}
			if (!ServiceUtil.isEmpty(obj.getMatDocItem())) {
				forReturn.setMatDocItem(obj.getMatDocItem());
			}
			if (!ServiceUtil.isEmpty(obj.getMulAccAss())) {
				forReturn.setMulAccAss(obj.getMulAccAss());
			}
			if (!ServiceUtil.isEmpty(obj.getPostingDate())) {
				forReturn.setPostingDate(Long.valueOf(obj.getPostingDate()));
			}
			if (!ServiceUtil.isEmpty(obj.getQtyOpu())) {
				forReturn.setQtyOpu(Double.valueOf(obj.getQtyOpu()));
			}
			if (!ServiceUtil.isEmpty(obj.getQuantity())) {
				forReturn.setQuantity(Double.valueOf(obj.getQuantity()));
			}
			if (!ServiceUtil.isEmpty(obj.getRefDoc())) {
				forReturn.setRefDoc(obj.getRefDoc());
			}
			if (!ServiceUtil.isEmpty(obj.getStepNumber())) {
				forReturn.setStepNumber(obj.getStepNumber());
			}
			if (!ServiceUtil.isEmpty(obj.getVendor())) {
				forReturn.setVendor(obj.getVendor());
			}

			returnDto.add(forReturn);
		}
		return returnDto;
	}

	public static void main(String[] args) throws ParseException {
		String response = "{\"d\":{\"results\":[{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HeaderSet('1000000003')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HeaderSet('1000000003')\",\"type\":\"ZP2P_API_PODETAILS_SRV.Header\"},\"Number\":\"1000000003\",\"CompCode\":\"1010\",\"DocType\":\"ZOPN\",\"DocCategory\":\"\",\"DeleteInd\":\"\",\"Status\":\"9\",\"CreatDate\":\"2021-06-15\",\"CreatedBy\":\"DKASHYAP\",\"ItemIntvl\":\"00010\",\"Vendor\":\"1000031\",\"Langu\":\"EN\",\"LanguIso\":\"EN\",\"Pmnttrms\":\"V007\",\"Dscnt1To\":\"60\",\"Dscnt2To\":\"0\",\"Dscnt3To\":\"0\",\"DsctPct1\":\"0.000\",\"DsctPct2\":\"0.000\",\"PurchOrg\":\"1000\",\"PurGroup\":\"001\",\"Currency\":\"SAR\",\"CurrencyIso\":\"SAR\",\"ExchRate\":\"1.00000\",\"ExRateFx\":false,\"DocDate\":\"\\/Date(1623715200000)\\/\",\"VperStart\":\"\\/Date(1623715200000)\\/\",\"VperEnd\":\"\\/Date(1623974400000)\\/\",\"Warranty\":null,\"Quotation\":\"\",\"QuotDate\":null,\"Ref1\":\"\",\"SalesPers\":\"\",\"Telephone\":\"\",\"SupplVend\":\"\",\"Customer\":\"\",\"Agreement\":\"\",\"GrMessage\":false,\"SupplPlnt\":\"\",\"Incoterms1\":\"CIF\",\"Incoterms2\":\"RYD\",\"CollectNo\":\"\",\"DiffInv\":\"\",\"OurRef\":\"\",\"Logsystem\":\"\",\"Subitemint\":\"00001\",\"PoRelInd\":\"\",\"RelStatus\":\"\",\"VatCntry\":\"SA\",\"VatCntryIso\":\"SA\",\"ReasonCancel\":\"00\",\"ReasonCode\":\"\",\"RetentionType\":\"\",\"RetentionPercentage\":\"0.00\",\"DownpayType\":\"\",\"DownpayAmount\":\"0.0000\",\"DownpayPercent\":\"0.00\",\"DownpayDuedate\":null,\"Memory\":false,\"Memorytype\":\"\",\"Shiptype\":\"\",\"Handoverloc\":\"\",\"Shipcond\":\"\",\"Incotermsv\":\"\",\"Incoterms2l\":\"RYD\",\"Incoterms3l\":\"\",\"ExtSys\":\"\",\"ExtRef\":\"\",\"IntrastatRel\":false,\"IntrastatExcl\":false,\"ExtRevTmstmp\":null,\"DocStatus\":\"1\",\"LastChangeDateTime\":\"20210615133655.5712940\",\"ToHistory\":{\"results\":[{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='00',PurchasingHistoryDocumentType='9',PurchasingHistoryDocumentYear='0000',PurchasingHistoryDocument='1000000039',PurchasingHistoryDocumentItem='0000',PurchasingHistoryCategory='D')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='00',PurchasingHistoryDocumentType='9',PurchasingHistoryDocumentYear='0000',PurchasingHistoryDocument='1000000039',PurchasingHistoryDocumentItem='0000',PurchasingHistoryCategory='D')\",\"type\":\"ZP2P_API_PODETAILS_SRV.History\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"AccountAssignmentNumber\":\"00\",\"PurchasingHistoryDocumentType\":\"9\",\"PurchasingHistoryDocumentYear\":\"0000\",\"PurchasingHistoryDocument\":\"1000000039\",\"PurchasingHistoryDocumentItem\":\"0000\",\"PurchasingHistoryCategory\":\"D\",\"GoodsMovementType\":\"\",\"PostingDate\":\"20210615\",\"Quantity\":\"1.000\",\"PurOrdAmountInCompanyCodeCrcy\":\"14000.0000\",\"PurchaseOrderAmount\":\"14000.0000\",\"Currency\":\"SAR\",\"GRIRAcctClrgAmtInCoCodeCrcy\":\"0.0000\",\"GdsRcptBlkdStkQtyInOrdQtyUnit\":\"0.000\",\"GdsRcptBlkdStkQtyInOrdPrcUnit\":\"0.000\",\"DebitCreditCode\":\"S\",\"InventoryValuationType\":\"\",\"IsCompletelyDelivered\":false,\"DocumentReferenceID\":\"\",\"ReferenceDocumentFiscalYear\":\"2021\",\"ReferenceDocument\":\"1000000039\",\"ReferenceDocumentItem\":\"0000\",\"AccountingDocumentCreationDate\":\"20210615\",\"InvoiceAmtInCoCodeCrcy\":\"0.0000\",\"InvoiceAmountInFrgnCurrency\":\"0.0000\",\"Material\":\"\",\"Plant\":\"\",\"PricingDocument\":\"\",\"TaxCode\":\"\",\"QuantityInDeliveryQtyUnit\":\"0.000\",\"DeliveryQuantityUnit\":\"\",\"ManufacturerMaterial\":\"\",\"CompanyCodeCurrency\":\"SAR\",\"DocumentDate\":\"20210615\",\"CurrencyIso\":\"SAR\",\"LocCurrIso\":\"SAR\",\"DelivUnitIso\":\"\",\"MaterialExternal\":\"\",\"MaterialGuid\":\"\",\"MaterialVersion\":\"\",\"PurMatExternal\":\"\",\"PurMatGuid\":\"\",\"PurMatVersion\":\"\",\"RefDocNoLong\":\"\",\"Batch\":\"\",\"StkSegment\":\"\",\"MaterialLong\":\"\",\"PurMatLong\":\"\",\"StkSegLong\":\"\",\"MoveReas\":\"0000\",\"EntryTime\":\"PT00H00M00S\",\"ConfSer\":\"0000\",\"RvslOfGoodsReceiptIsAllowed\":\"\",\"QtyInPurchaseOrderPriceUnit\":\"0.000\",\"ShipgInstrnSupplierCompliance\":\"\",\"GRIRAcctClrgAmtInTransacCrcy\":\"0.00\",\"QuantityInBaseUnit\":\"0.000\",\"GRIRAcctClrgAmtInOrdTrnsacCrcy\":\"0.00\",\"InvoiceAmtInPurOrdTransacCrcy\":\"0.00\",\"VltdGdsRcptBlkdStkQtyInOrdUnit\":\"0.000\",\"VltdGdsRcptBlkdQtyInOrdPrcUnit\":\"0.000\",\"IsToBeAcceptedAtOrigin\":\"\",\"ExchangeRateDifferenceAmount\":\"0.00\",\"ExchangeRate\":\"0.00000\",\"DeliveryDocument\":\"\",\"EntryDate\":\"\",\"DeliveryDocumentItem\":\"\",\"AccountingDocumentCreationTime\":\"163823\",\"SrvPos\":\"\",\"PackNo\":\"0000000000\",\"IntRow\":\"0000000000\",\"PlnPackNo\":\"0000000000\",\"PlnIntRow\":\"0000000000\",\"ExtRow\":\"0000000000\"},{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='00',PurchasingHistoryDocumentType='9',PurchasingHistoryDocumentYear='0000',PurchasingHistoryDocument='1000000040',PurchasingHistoryDocumentItem='0000',PurchasingHistoryCategory='D')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='00',PurchasingHistoryDocumentType='9',PurchasingHistoryDocumentYear='0000',PurchasingHistoryDocument='1000000040',PurchasingHistoryDocumentItem='0000',PurchasingHistoryCategory='D')\",\"type\":\"ZP2P_API_PODETAILS_SRV.History\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"AccountAssignmentNumber\":\"00\",\"PurchasingHistoryDocumentType\":\"9\",\"PurchasingHistoryDocumentYear\":\"0000\",\"PurchasingHistoryDocument\":\"1000000040\",\"PurchasingHistoryDocumentItem\":\"0000\",\"PurchasingHistoryCategory\":\"D\",\"GoodsMovementType\":\"\",\"PostingDate\":\"20210615\",\"Quantity\":\"1.000\",\"PurOrdAmountInCompanyCodeCrcy\":\"6000.0000\",\"PurchaseOrderAmount\":\"6000.0000\",\"Currency\":\"SAR\",\"GRIRAcctClrgAmtInCoCodeCrcy\":\"0.0000\",\"GdsRcptBlkdStkQtyInOrdQtyUnit\":\"0.000\",\"GdsRcptBlkdStkQtyInOrdPrcUnit\":\"0.000\",\"DebitCreditCode\":\"S\",\"InventoryValuationType\":\"\",\"IsCompletelyDelivered\":false,\"DocumentReferenceID\":\"\",\"ReferenceDocumentFiscalYear\":\"2021\",\"ReferenceDocument\":\"1000000040\",\"ReferenceDocumentItem\":\"0000\",\"AccountingDocumentCreationDate\":\"20210615\",\"InvoiceAmtInCoCodeCrcy\":\"0.0000\",\"InvoiceAmountInFrgnCurrency\":\"0.0000\",\"Material\":\"\",\"Plant\":\"\",\"PricingDocument\":\"\",\"TaxCode\":\"\",\"QuantityInDeliveryQtyUnit\":\"0.000\",\"DeliveryQuantityUnit\":\"\",\"ManufacturerMaterial\":\"\",\"CompanyCodeCurrency\":\"SAR\",\"DocumentDate\":\"20210615\",\"CurrencyIso\":\"SAR\",\"LocCurrIso\":\"SAR\",\"DelivUnitIso\":\"\",\"MaterialExternal\":\"\",\"MaterialGuid\":\"\",\"MaterialVersion\":\"\",\"PurMatExternal\":\"\",\"PurMatGuid\":\"\",\"PurMatVersion\":\"\",\"RefDocNoLong\":\"\",\"Batch\":\"\",\"StkSegment\":\"\",\"MaterialLong\":\"\",\"PurMatLong\":\"\",\"StkSegLong\":\"\",\"MoveReas\":\"0000\",\"EntryTime\":\"PT00H00M00S\",\"ConfSer\":\"0000\",\"RvslOfGoodsReceiptIsAllowed\":\"\",\"QtyInPurchaseOrderPriceUnit\":\"0.000\",\"ShipgInstrnSupplierCompliance\":\"\",\"GRIRAcctClrgAmtInTransacCrcy\":\"0.00\",\"QuantityInBaseUnit\":\"0.000\",\"GRIRAcctClrgAmtInOrdTrnsacCrcy\":\"0.00\",\"InvoiceAmtInPurOrdTransacCrcy\":\"0.00\",\"VltdGdsRcptBlkdStkQtyInOrdUnit\":\"0.000\",\"VltdGdsRcptBlkdQtyInOrdPrcUnit\":\"0.000\",\"IsToBeAcceptedAtOrigin\":\"\",\"ExchangeRateDifferenceAmount\":\"0.00\",\"ExchangeRate\":\"0.00000\",\"DeliveryDocument\":\"\",\"EntryDate\":\"\",\"DeliveryDocumentItem\":\"\",\"AccountingDocumentCreationTime\":\"163825\",\"SrvPos\":\"\",\"PackNo\":\"0000000000\",\"IntRow\":\"0000000000\",\"PlnPackNo\":\"0000000000\",\"PlnIntRow\":\"0000000000\",\"ExtRow\":\"0000000000\"},{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='1',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5000000466',PurchasingHistoryDocumentItem='0001',PurchasingHistoryCategory='E')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='1',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5000000466',PurchasingHistoryDocumentItem='0001',PurchasingHistoryCategory='E')\",\"type\":\"ZP2P_API_PODETAILS_SRV.History\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"AccountAssignmentNumber\":\"01\",\"PurchasingHistoryDocumentType\":\"1\",\"PurchasingHistoryDocumentYear\":\"2021\",\"PurchasingHistoryDocument\":\"5000000466\",\"PurchasingHistoryDocumentItem\":\"0001\",\"PurchasingHistoryCategory\":\"E\",\"GoodsMovementType\":\"101\",\"PostingDate\":\"20210615\",\"Quantity\":\"100.000\",\"PurOrdAmountInCompanyCodeCrcy\":\"10000.0000\",\"PurchaseOrderAmount\":\"10000.0000\",\"Currency\":\"SAR\",\"GRIRAcctClrgAmtInCoCodeCrcy\":\"0.0000\",\"GdsRcptBlkdStkQtyInOrdQtyUnit\":\"0.000\",\"GdsRcptBlkdStkQtyInOrdPrcUnit\":\"0.000\",\"DebitCreditCode\":\"S\",\"InventoryValuationType\":\"\",\"IsCompletelyDelivered\":false,\"DocumentReferenceID\":\"\",\"ReferenceDocumentFiscalYear\":\"2021\",\"ReferenceDocument\":\"1000000039\",\"ReferenceDocumentItem\":\"0001\",\"AccountingDocumentCreationDate\":\"20210615\",\"InvoiceAmtInCoCodeCrcy\":\"0.0000\",\"InvoiceAmountInFrgnCurrency\":\"0.0000\",\"Material\":\"\",\"Plant\":\"2000\",\"PricingDocument\":\"\",\"TaxCode\":\"\",\"QuantityInDeliveryQtyUnit\":\"0.000\",\"DeliveryQuantityUnit\":\"\",\"ManufacturerMaterial\":\"\",\"CompanyCodeCurrency\":\"SAR\",\"DocumentDate\":\"20210615\",\"CurrencyIso\":\"SAR\",\"LocCurrIso\":\"SAR\",\"DelivUnitIso\":\"\",\"MaterialExternal\":\"\",\"MaterialGuid\":\"\",\"MaterialVersion\":\"\",\"PurMatExternal\":\"\",\"PurMatGuid\":\"\",\"PurMatVersion\":\"\",\"RefDocNoLong\":\"\",\"Batch\":\"\",\"StkSegment\":\"\",\"MaterialLong\":\"\",\"PurMatLong\":\"\",\"StkSegLong\":\"\",\"MoveReas\":\"0000\",\"EntryTime\":\"PT00H00M00S\",\"ConfSer\":\"0000\",\"RvslOfGoodsReceiptIsAllowed\":\"\",\"QtyInPurchaseOrderPriceUnit\":\"0.000\",\"ShipgInstrnSupplierCompliance\":\"\",\"GRIRAcctClrgAmtInTransacCrcy\":\"0.00\",\"QuantityInBaseUnit\":\"0.000\",\"GRIRAcctClrgAmtInOrdTrnsacCrcy\":\"0.00\",\"InvoiceAmtInPurOrdTransacCrcy\":\"0.00\",\"VltdGdsRcptBlkdStkQtyInOrdUnit\":\"0.000\",\"VltdGdsRcptBlkdQtyInOrdPrcUnit\":\"0.000\",\"IsToBeAcceptedAtOrigin\":\"\",\"ExchangeRateDifferenceAmount\":\"0.00\",\"ExchangeRate\":\"0.00000\",\"DeliveryDocument\":\"\",\"EntryDate\":\"\",\"DeliveryDocumentItem\":\"\",\"AccountingDocumentCreationTime\":\"163825\",\"SrvPos\":\"\",\"PackNo\":\"0000000284\",\"IntRow\":\"0000000002\",\"PlnPackNo\":\"0000000281\",\"PlnIntRow\":\"0000000002\",\"ExtRow\":\"0000000010\"},{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='1',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5000000466',PurchasingHistoryDocumentItem='0002',PurchasingHistoryCategory='E')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='1',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5000000466',PurchasingHistoryDocumentItem='0002',PurchasingHistoryCategory='E')\",\"type\":\"ZP2P_API_PODETAILS_SRV.History\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"AccountAssignmentNumber\":\"01\",\"PurchasingHistoryDocumentType\":\"1\",\"PurchasingHistoryDocumentYear\":\"2021\",\"PurchasingHistoryDocument\":\"5000000466\",\"PurchasingHistoryDocumentItem\":\"0002\",\"PurchasingHistoryCategory\":\"E\",\"GoodsMovementType\":\"101\",\"PostingDate\":\"20210615\",\"Quantity\":\"20.000\",\"PurOrdAmountInCompanyCodeCrcy\":\"4000.0000\",\"PurchaseOrderAmount\":\"4000.0000\",\"Currency\":\"SAR\",\"GRIRAcctClrgAmtInCoCodeCrcy\":\"0.0000\",\"GdsRcptBlkdStkQtyInOrdQtyUnit\":\"0.000\",\"GdsRcptBlkdStkQtyInOrdPrcUnit\":\"0.000\",\"DebitCreditCode\":\"S\",\"InventoryValuationType\":\"\",\"IsCompletelyDelivered\":false,\"DocumentReferenceID\":\"\",\"ReferenceDocumentFiscalYear\":\"2021\",\"ReferenceDocument\":\"1000000039\",\"ReferenceDocumentItem\":\"0002\",\"AccountingDocumentCreationDate\":\"20210615\",\"InvoiceAmtInCoCodeCrcy\":\"0.0000\",\"InvoiceAmountInFrgnCurrency\":\"0.0000\",\"Material\":\"\",\"Plant\":\"2000\",\"PricingDocument\":\"\",\"TaxCode\":\"\",\"QuantityInDeliveryQtyUnit\":\"0.000\",\"DeliveryQuantityUnit\":\"\",\"ManufacturerMaterial\":\"\",\"CompanyCodeCurrency\":\"SAR\",\"DocumentDate\":\"20210615\",\"CurrencyIso\":\"SAR\",\"LocCurrIso\":\"SAR\",\"DelivUnitIso\":\"\",\"MaterialExternal\":\"\",\"MaterialGuid\":\"\",\"MaterialVersion\":\"\",\"PurMatExternal\":\"\",\"PurMatGuid\":\"\",\"PurMatVersion\":\"\",\"RefDocNoLong\":\"\",\"Batch\":\"\",\"StkSegment\":\"\",\"MaterialLong\":\"\",\"PurMatLong\":\"\",\"StkSegLong\":\"\",\"MoveReas\":\"0000\",\"EntryTime\":\"PT00H00M00S\",\"ConfSer\":\"0000\",\"RvslOfGoodsReceiptIsAllowed\":\"\",\"QtyInPurchaseOrderPriceUnit\":\"0.000\",\"ShipgInstrnSupplierCompliance\":\"\",\"GRIRAcctClrgAmtInTransacCrcy\":\"0.00\",\"QuantityInBaseUnit\":\"0.000\",\"GRIRAcctClrgAmtInOrdTrnsacCrcy\":\"0.00\",\"InvoiceAmtInPurOrdTransacCrcy\":\"0.00\",\"VltdGdsRcptBlkdStkQtyInOrdUnit\":\"0.000\",\"VltdGdsRcptBlkdQtyInOrdPrcUnit\":\"0.000\",\"IsToBeAcceptedAtOrigin\":\"\",\"ExchangeRateDifferenceAmount\":\"0.00\",\"ExchangeRate\":\"0.00000\",\"DeliveryDocument\":\"\",\"EntryDate\":\"\",\"DeliveryDocumentItem\":\"\",\"AccountingDocumentCreationTime\":\"163825\",\"SrvPos\":\"\",\"PackNo\":\"0000000284\",\"IntRow\":\"0000000003\",\"PlnPackNo\":\"0000000281\",\"PlnIntRow\":\"0000000003\",\"ExtRow\":\"0000000020\"},{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='1',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5000000467',PurchasingHistoryDocumentItem='0001',PurchasingHistoryCategory='E')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='1',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5000000467',PurchasingHistoryDocumentItem='0001',PurchasingHistoryCategory='E')\",\"type\":\"ZP2P_API_PODETAILS_SRV.History\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"AccountAssignmentNumber\":\"01\",\"PurchasingHistoryDocumentType\":\"1\",\"PurchasingHistoryDocumentYear\":\"2021\",\"PurchasingHistoryDocument\":\"5000000467\",\"PurchasingHistoryDocumentItem\":\"0001\",\"PurchasingHistoryCategory\":\"E\",\"GoodsMovementType\":\"101\",\"PostingDate\":\"20210615\",\"Quantity\":\"30.000\",\"PurOrdAmountInCompanyCodeCrcy\":\"6000.0000\",\"PurchaseOrderAmount\":\"6000.0000\",\"Currency\":\"SAR\",\"GRIRAcctClrgAmtInCoCodeCrcy\":\"0.0000\",\"GdsRcptBlkdStkQtyInOrdQtyUnit\":\"0.000\",\"GdsRcptBlkdStkQtyInOrdPrcUnit\":\"0.000\",\"DebitCreditCode\":\"S\",\"InventoryValuationType\":\"\",\"IsCompletelyDelivered\":false,\"DocumentReferenceID\":\"\",\"ReferenceDocumentFiscalYear\":\"2021\",\"ReferenceDocument\":\"1000000040\",\"ReferenceDocumentItem\":\"0001\",\"AccountingDocumentCreationDate\":\"20210615\",\"InvoiceAmtInCoCodeCrcy\":\"0.0000\",\"InvoiceAmountInFrgnCurrency\":\"0.0000\",\"Material\":\"\",\"Plant\":\"2000\",\"PricingDocument\":\"\",\"TaxCode\":\"\",\"QuantityInDeliveryQtyUnit\":\"0.000\",\"DeliveryQuantityUnit\":\"\",\"ManufacturerMaterial\":\"\",\"CompanyCodeCurrency\":\"SAR\",\"DocumentDate\":\"20210615\",\"CurrencyIso\":\"SAR\",\"LocCurrIso\":\"SAR\",\"DelivUnitIso\":\"\",\"MaterialExternal\":\"\",\"MaterialGuid\":\"\",\"MaterialVersion\":\"\",\"PurMatExternal\":\"\",\"PurMatGuid\":\"\",\"PurMatVersion\":\"\",\"RefDocNoLong\":\"\",\"Batch\":\"\",\"StkSegment\":\"\",\"MaterialLong\":\"\",\"PurMatLong\":\"\",\"StkSegLong\":\"\",\"MoveReas\":\"0000\",\"EntryTime\":\"PT00H00M00S\",\"ConfSer\":\"0000\",\"RvslOfGoodsReceiptIsAllowed\":\"\",\"QtyInPurchaseOrderPriceUnit\":\"0.000\",\"ShipgInstrnSupplierCompliance\":\"\",\"GRIRAcctClrgAmtInTransacCrcy\":\"0.00\",\"QuantityInBaseUnit\":\"0.000\",\"GRIRAcctClrgAmtInOrdTrnsacCrcy\":\"0.00\",\"InvoiceAmtInPurOrdTransacCrcy\":\"0.00\",\"VltdGdsRcptBlkdStkQtyInOrdUnit\":\"0.000\",\"VltdGdsRcptBlkdQtyInOrdPrcUnit\":\"0.000\",\"IsToBeAcceptedAtOrigin\":\"\",\"ExchangeRateDifferenceAmount\":\"0.00\",\"ExchangeRate\":\"0.00000\",\"DeliveryDocument\":\"\",\"EntryDate\":\"\",\"DeliveryDocumentItem\":\"\",\"AccountingDocumentCreationTime\":\"163837\",\"SrvPos\":\"\",\"PackNo\":\"0000000287\",\"IntRow\":\"0000000084\",\"PlnPackNo\":\"0000000281\",\"PlnIntRow\":\"0000000003\",\"ExtRow\":\"0000000020\"},{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='2',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5105600239',PurchasingHistoryDocumentItem='0001',PurchasingHistoryCategory='Q')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='2',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5105600239',PurchasingHistoryDocumentItem='0001',PurchasingHistoryCategory='Q')\",\"type\":\"ZP2P_API_PODETAILS_SRV.History\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"AccountAssignmentNumber\":\"01\",\"PurchasingHistoryDocumentType\":\"2\",\"PurchasingHistoryDocumentYear\":\"2021\",\"PurchasingHistoryDocument\":\"5105600239\",\"PurchasingHistoryDocumentItem\":\"0001\",\"PurchasingHistoryCategory\":\"Q\",\"GoodsMovementType\":\"\",\"PostingDate\":\"20210617\",\"Quantity\":\"1.000\",\"PurOrdAmountInCompanyCodeCrcy\":\"100.0000\",\"PurchaseOrderAmount\":\"100.0000\",\"Currency\":\"SAR\",\"GRIRAcctClrgAmtInCoCodeCrcy\":\"100.0000\",\"GdsRcptBlkdStkQtyInOrdQtyUnit\":\"0.000\",\"GdsRcptBlkdStkQtyInOrdPrcUnit\":\"0.000\",\"DebitCreditCode\":\"S\",\"InventoryValuationType\":\"\",\"IsCompletelyDelivered\":false,\"DocumentReferenceID\":\"\",\"ReferenceDocumentFiscalYear\":\"2021\",\"ReferenceDocument\":\"1000000039\",\"ReferenceDocumentItem\":\"0001\",\"AccountingDocumentCreationDate\":\"20210617\",\"InvoiceAmtInCoCodeCrcy\":\"100.0000\",\"InvoiceAmountInFrgnCurrency\":\"100.0000\",\"Material\":\"\",\"Plant\":\"2000\",\"PricingDocument\":\"\",\"TaxCode\":\"I1\",\"QuantityInDeliveryQtyUnit\":\"0.000\",\"DeliveryQuantityUnit\":\"\",\"ManufacturerMaterial\":\"\",\"CompanyCodeCurrency\":\"SAR\",\"DocumentDate\":\"20210617\",\"CurrencyIso\":\"SAR\",\"LocCurrIso\":\"SAR\",\"DelivUnitIso\":\"\",\"MaterialExternal\":\"\",\"MaterialGuid\":\"\",\"MaterialVersion\":\"\",\"PurMatExternal\":\"\",\"PurMatGuid\":\"\",\"PurMatVersion\":\"\",\"RefDocNoLong\":\"\",\"Batch\":\"\",\"StkSegment\":\"\",\"MaterialLong\":\"\",\"PurMatLong\":\"\",\"StkSegLong\":\"\",\"MoveReas\":\"0000\",\"EntryTime\":\"PT00H00M00S\",\"ConfSer\":\"0000\",\"RvslOfGoodsReceiptIsAllowed\":\"\",\"QtyInPurchaseOrderPriceUnit\":\"0.000\",\"ShipgInstrnSupplierCompliance\":\"\",\"GRIRAcctClrgAmtInTransacCrcy\":\"0.00\",\"QuantityInBaseUnit\":\"0.000\",\"GRIRAcctClrgAmtInOrdTrnsacCrcy\":\"0.00\",\"InvoiceAmtInPurOrdTransacCrcy\":\"0.00\",\"VltdGdsRcptBlkdStkQtyInOrdUnit\":\"0.000\",\"VltdGdsRcptBlkdQtyInOrdPrcUnit\":\"0.000\",\"IsToBeAcceptedAtOrigin\":\"\",\"ExchangeRateDifferenceAmount\":\"0.00\",\"ExchangeRate\":\"0.00000\",\"DeliveryDocument\":\"\",\"EntryDate\":\"\",\"DeliveryDocumentItem\":\"\",\"AccountingDocumentCreationTime\":\"160311\",\"SrvPos\":\"\",\"PackNo\":\"0000000284\",\"IntRow\":\"0000000002\",\"PlnPackNo\":\"0000000281\",\"PlnIntRow\":\"0000000002\",\"ExtRow\":\"0000000010\"},{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='2',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5105600239',PurchasingHistoryDocumentItem='0002',PurchasingHistoryCategory='Q')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='2',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5105600239',PurchasingHistoryDocumentItem='0002',PurchasingHistoryCategory='Q')\",\"type\":\"ZP2P_API_PODETAILS_SRV.History\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"AccountAssignmentNumber\":\"01\",\"PurchasingHistoryDocumentType\":\"2\",\"PurchasingHistoryDocumentYear\":\"2021\",\"PurchasingHistoryDocument\":\"5105600239\",\"PurchasingHistoryDocumentItem\":\"0002\",\"PurchasingHistoryCategory\":\"Q\",\"GoodsMovementType\":\"\",\"PostingDate\":\"20210617\",\"Quantity\":\"1.000\",\"PurOrdAmountInCompanyCodeCrcy\":\"200.0000\",\"PurchaseOrderAmount\":\"200.0000\",\"Currency\":\"SAR\",\"GRIRAcctClrgAmtInCoCodeCrcy\":\"200.0000\",\"GdsRcptBlkdStkQtyInOrdQtyUnit\":\"0.000\",\"GdsRcptBlkdStkQtyInOrdPrcUnit\":\"0.000\",\"DebitCreditCode\":\"S\",\"InventoryValuationType\":\"\",\"IsCompletelyDelivered\":false,\"DocumentReferenceID\":\"\",\"ReferenceDocumentFiscalYear\":\"2021\",\"ReferenceDocument\":\"1000000039\",\"ReferenceDocumentItem\":\"0002\",\"AccountingDocumentCreationDate\":\"20210617\",\"InvoiceAmtInCoCodeCrcy\":\"200.0000\",\"InvoiceAmountInFrgnCurrency\":\"200.0000\",\"Material\":\"\",\"Plant\":\"2000\",\"PricingDocument\":\"\",\"TaxCode\":\"I1\",\"QuantityInDeliveryQtyUnit\":\"0.000\",\"DeliveryQuantityUnit\":\"\",\"ManufacturerMaterial\":\"\",\"CompanyCodeCurrency\":\"SAR\",\"DocumentDate\":\"20210617\",\"CurrencyIso\":\"SAR\",\"LocCurrIso\":\"SAR\",\"DelivUnitIso\":\"\",\"MaterialExternal\":\"\",\"MaterialGuid\":\"\",\"MaterialVersion\":\"\",\"PurMatExternal\":\"\",\"PurMatGuid\":\"\",\"PurMatVersion\":\"\",\"RefDocNoLong\":\"\",\"Batch\":\"\",\"StkSegment\":\"\",\"MaterialLong\":\"\",\"PurMatLong\":\"\",\"StkSegLong\":\"\",\"MoveReas\":\"0000\",\"EntryTime\":\"PT00H00M00S\",\"ConfSer\":\"0000\",\"RvslOfGoodsReceiptIsAllowed\":\"\",\"QtyInPurchaseOrderPriceUnit\":\"0.000\",\"ShipgInstrnSupplierCompliance\":\"\",\"GRIRAcctClrgAmtInTransacCrcy\":\"0.00\",\"QuantityInBaseUnit\":\"0.000\",\"GRIRAcctClrgAmtInOrdTrnsacCrcy\":\"0.00\",\"InvoiceAmtInPurOrdTransacCrcy\":\"0.00\",\"VltdGdsRcptBlkdStkQtyInOrdUnit\":\"0.000\",\"VltdGdsRcptBlkdQtyInOrdPrcUnit\":\"0.000\",\"IsToBeAcceptedAtOrigin\":\"\",\"ExchangeRateDifferenceAmount\":\"0.00\",\"ExchangeRate\":\"0.00000\",\"DeliveryDocument\":\"\",\"EntryDate\":\"\",\"DeliveryDocumentItem\":\"\",\"AccountingDocumentCreationTime\":\"160311\",\"SrvPos\":\"\",\"PackNo\":\"0000000284\",\"IntRow\":\"0000000003\",\"PlnPackNo\":\"0000000281\",\"PlnIntRow\":\"0000000003\",\"ExtRow\":\"0000000020\"}]},\"ToHistoryTotal\":{\"results\":[{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistoryTotalSet(Number='1000000003',ItemNo='00010',SerialNo='00')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistoryTotalSet(Number='1000000003',ItemNo='00010',SerialNo='00')\",\"type\":\"ZP2P_API_PODETAILS_SRV.HistoryTotal\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"SerialNo\":\"00\",\"WithdrQty\":\"0.000\",\"BlockedQy\":\"0.000\",\"BlQty\":\"0.000\",\"DelivQty\":\"150.000\",\"PoPrQnt\":\"150.000\",\"ValGrLoc\":\"20000.0000\",\"ValGrFor\":\"20000.0000\",\"IvQty\":\"2.000\",\"IvQtyPo\":\"2.000\",\"ValIvLoc\":\"300.0000\",\"ValIvFor\":\"300.0000\",\"ClValLoc\":\"300.0000\",\"ClValFor\":\"300.0000\",\"DopVlLoc\":\"0.0000\",\"IvvalLoc\":\"300.0000\",\"IvvalFor\":\"300.0000\",\"DlQtyTrsp\":\"0.000\",\"BlQtyTotal\":\"0.000\",\"DlQtyTotal\":\"0.000\",\"IvQtyTotal\":\"0.000\",\"Currency\":\"SAR\",\"CurrencyIso\":\"SAR\"},{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistoryTotalSet(Number='1000000003',ItemNo='00010',SerialNo='01')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistoryTotalSet(Number='1000000003',ItemNo='00010',SerialNo='01')\",\"type\":\"ZP2P_API_PODETAILS_SRV.HistoryTotal\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"SerialNo\":\"01\",\"WithdrQty\":\"0.000\",\"BlockedQy\":\"0.000\",\"BlQty\":\"0.000\",\"DelivQty\":\"150.000\",\"PoPrQnt\":\"150.000\",\"ValGrLoc\":\"20000.0000\",\"ValGrFor\":\"20000.0000\",\"IvQty\":\"2.000\",\"IvQtyPo\":\"2.000\",\"ValIvLoc\":\"300.0000\",\"ValIvFor\":\"300.0000\",\"ClValLoc\":\"300.0000\",\"ClValFor\":\"300.0000\",\"DopVlLoc\":\"0.0000\",\"IvvalLoc\":\"300.0000\",\"IvvalFor\":\"300.0000\",\"DlQtyTrsp\":\"0.000\",\"BlQtyTotal\":\"0.000\",\"DlQtyTotal\":\"0.000\",\"IvQtyTotal\":\"0.000\",\"Currency\":\"SAR\",\"CurrencyIso\":\"SAR\"}]},\"ToDeliveryCostHistory\":{\"results\":[]},\"ToPartner\":{\"results\":[]},\"ToItem\":{\"results\":[{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/ItemSet(Number='1000000003',ItemNo='00010')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/ItemSet(Number='1000000003',ItemNo='00010')\",\"type\":\"ZP2P_API_PODETAILS_SRV.Item\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"DeleteInd\":\"\",\"ShortText\":\"Service po\",\"Material\":\"\",\"MaterialExternal\":\"\",\"MaterialGuid\":\"\",\"MaterialVersion\":\"\",\"Ematerial\":\"\",\"EmaterialExternal\":\"\",\"EmaterialGuid\":\"\",\"EmaterialVersion\":\"\",\"Plant\":\"2000\",\"StgeLoc\":\"\",\"Trackingno\":\"\",\"MatlGroup\":\"P001\",\"InfoRec\":\"\",\"VendMat\":\"\",\"Quantity\":\"1.000\",\"PoUnit\":\"AU\",\"PoUnitIso\":\"C62\",\"OrderprUn\":\"AU\",\"OrderprUnIso\":\"C62\",\"ConvNum1\":\"1\",\"ConvDen1\":\"1\",\"NetPrice\":\"20000.000000000\",\"PriceUnit\":\"1\",\"GrPrTime\":\"0\",\"TaxCode\":\"\",\"BonGrp1\":\"\",\"QualInsp\":\"\",\"InfoUpd\":\"\",\"PrntPrice\":true,\"EstPrice\":false,\"Reminder1\":\"0\",\"Reminder2\":\"0\",\"Reminder3\":\"0\",\"OverDlvTol\":\"0.0\",\"UnlimitedDlv\":true,\"UnderDlvTol\":\"0.0\",\"ValType\":\"\",\"NoMoreGr\":false,\"FinalInv\":false,\"ItemCat\":\"9\",\"Acctasscat\":\"K\",\"Distrib\":\"\",\"PartInv\":\"\",\"GrInd\":true,\"GrNonVal\":false,\"IrInd\":true,\"FreeItem\":false,\"GrBasediv\":true,\"AcknReqd\":false,\"AcknowlNo\":\"\",\"Agreement\":\"\",\"AgmtItem\":\"00000\",\"Shipping\":\"\",\"Customer\":\"\",\"CondGroup\":\"\",\"NoDisct\":false,\"PlanDel\":\"0\",\"NetWeight\":\"0.000\",\"Weightunit\":\"\",\"WeightunitIso\":\"\",\"Taxjurcode\":\"\",\"CtrlKey\":\"\",\"ConfCtrl\":\"\",\"RevLev\":\"\",\"Fund\":\"\",\"FundsCtr\":\"\",\"CmmtItem\":\"\",\"Pricedate\":\"4\",\"PriceDate\":\"2021-06-15\",\"GrossWt\":\"0.000\",\"Volume\":\"0.000\",\"Volumeunit\":\"\",\"VolumeunitIso\":\"\",\"Incoterms1\":\"\",\"Incoterms2\":\"\",\"PreVendor\":\"\",\"VendPart\":\"\",\"HlItem\":\"00000\",\"GrToDate\":\"0000-00-00\",\"SuppVendor\":\"\",\"ScVendor\":false,\"KanbanInd\":\"\",\"Ers\":false,\"RPromo\":\"\",\"Points\":\"0.000\",\"PointUnit\":\"\",\"PointUnitIso\":\"\",\"Season\":\"\",\"SeasonYr\":\"\",\"BonGrp2\":\"\",\"BonGrp3\":\"\",\"SettItem\":false,\"Minremlife\":\"0\",\"RfqNo\":\"\",\"RfqItem\":\"00000\",\"PreqNo\":\"\",\"PreqItem\":\"00000\",\"RefDoc\":\"\",\"RefItem\":\"00000\",\"SiCat\":\"\",\"RetItem\":false,\"AtRelev\":\"\",\"OrderReason\":\"\",\"BrasNbm\":\"\",\"MatlUsage\":\"\",\"MatOrigin\":\"\",\"InHouse\":false,\"Indus3\":\"\",\"InfIndex\":\"\",\"UntilDate\":\"0000-00-00\",\"DelivCompl\":false,\"PartDeliv\":\"\",\"ShipBlocked\":false,\"PreqName\":\"\",\"PeriodIndExpirationDate\":\"D\",\"IntObjNo\":\"000000000000000000\",\"PckgNo\":\"0000000280\",\"Batch\":\"\",\"Vendrbatch\":\"\",\"Calctype\":\"\",\"GrantNbr\":\"\",\"CmmtItemLong\":\"\",\"FuncAreaLong\":\"\",\"NoRounding\":false,\"PoPrice\":\"\",\"SupplStloc\":\"\",\"SrvBasedIv\":true,\"FundsRes\":\"\",\"ResItem\":\"000\",\"OrigAccept\":false,\"AllocTbl\":\"\",\"AllocTblItem\":\"00000\",\"SrcStockType\":\"\",\"ReasonRej\":\"\",\"CrmSalesOrderNo\":\"\",\"CrmSalesOrderItemNo\":\"000000\",\"CrmRefSalesOrderNo\":\"\",\"CrmRefSoItemNo\":\"\",\"PrioUrgency\":\"00\",\"PrioRequirement\":\"000\",\"ReasonCode\":\"\",\"FundLong\":\"\",\"LongItemNumber\":\"\",\"ExternalSortNumber\":\"00000\",\"ExternalHierarchyType\":\"\",\"RetentionPercentage\":\"0.00\",\"DownpayType\":\"\",\"DownpayAmount\":\"0.0000\",\"DownpayPercent\":\"0.00\",\"DownpayDuedate\":\"0000-00-00\",\"ExtRfxNumber\":\"\",\"ExtRfxItem\":\"\",\"ExtRfxSystem\":\"\",\"SrmContractId\":\"\",\"SrmContractItm\":\"0000000000\",\"BudgetPeriod\":\"\",\"BlockReasonId\":\"\",\"BlockReasonText\":\"\",\"SpeCrmFkrel\":\"\",\"DateQtyFixed\":\"\",\"GiBasedGr\":false,\"Shiptype\":\"\",\"Handoverloc\":\"\",\"TcAutDet\":\"\",\"ManualTcReason\":\"\",\"FiscalIncentive\":\"\",\"FiscalIncentiveId\":\"\",\"TaxSubjectSt\":\"\",\"ReqSegment\":\"\",\"StkSegment\":\"\",\"SfTxjcd\":\"\",\"Incoterms2l\":\"\",\"Incoterms3l\":\"\",\"MaterialLong\":\"\",\"EmaterialLong\":\"\",\"Serviceperformer\":\"\",\"Producttype\":\"1\",\"Startdate\":\"0000-00-00\",\"Enddate\":\"0000-00-00\",\"ReqSegLong\":\"\",\"StkSegLong\":\"\",\"ExpectedValue\":\"0.000000000\",\"LimitAmount\":\"0.000000000\",\"ExtRef\":\"\",\"GlAccount\":\"\",\"Costcenter\":\"\",\"WbsElement\":\"\",\"CommodityCode\":\"\",\"IntrastatServiceCode\":\"\",\"NetValue\":\"20000.000\",\"GrossValue\":\"20000.000\",\"InterArticleNum\":\"\",\"ItemChangeDate\":\"20210615\",\"ToItemNote\":{\"results\":[]},\"ToItemAccountAssignment\":{\"results\":[{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/AccountAssignmentSet(Number='1000000003',ItemNo='00010',SerialNo='01')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/AccountAssignmentSet(Number='1000000003',ItemNo='00010',SerialNo='01')\",\"type\":\"ZP2P_API_PODETAILS_SRV.AccountAssignment\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"SerialNo\":\"01\",\"DeleteInd\":false,\"CreatDate\":\"2021-06-15\",\"Quantity\":\"1.000\",\"DistrPerc\":\"0.0\",\"NetValue\":\"20000.000000000\",\"GlAccount\":\"5500059\",\"BusArea\":\"\",\"Costcenter\":\"111002\",\"SdDoc\":\"\",\"ItmNumber\":\"000000\",\"SchedLine\":\"0000\",\"AssetNo\":\"\",\"SubNumber\":\"\",\"Orderid\":\"\",\"GrRcpt\":\"\",\"UnloadPt\":\"\",\"CoArea\":\"1000\",\"Costobject\":\"\",\"ProfitCtr\":\"101020\",\"WbsElement\":\"\",\"Network\":\"\",\"RlEstKey\":\"\",\"PartAcct\":\"\",\"CmmtItem\":\"\",\"RecInd\":\"\",\"FundsCtr\":\"\",\"Fund\":\"\",\"FuncArea\":\"\",\"RefDate\":\"0000-00-00\",\"TaxCode\":\"\",\"Taxjurcode\":\"\",\"NondItax\":\"0.000000000\",\"Acttype\":\"\",\"CoBusproc\":\"\",\"ResDoc\":\"\",\"ResItem\":\"000\",\"Activity\":\"\",\"GrantNbr\":\"\",\"CmmtItemLong\":\"\",\"FuncAreaLong\":\"\",\"BudgetPeriod\":\"\",\"FinalInd\":false,\"FinalReason\":\"\",\"ServiceDoc\":\"\",\"ServiceItem\":\"000000\",\"ServiceDocType\":\"\"}]},\"ToItemPricingElement\":{\"results\":[]},\"ToItemScheduleLine\":{\"results\":[{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/ScheduelLineSet(Number='1000000003',ItemNo='00010',SchedLine='0001')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/ScheduelLineSet(Number='1000000003',ItemNo='00010',SchedLine='0001')\",\"type\":\"ZP2P_API_PODETAILS_SRV.ScheduelLine\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"SchedLine\":\"0001\",\"DelDatcatExt\":\"D\",\"DeliveryDate\":\"15.06.2021\",\"Quantity\":\"1.000\",\"DelivTime\":\"PT00H00M00S\",\"StatDate\":\"2021-06-15\",\"PreqNo\":\"\",\"PreqItem\":\"00000\",\"PoDate\":\"2021-06-15\",\"Routesched\":\"\",\"MsDate\":\"0000-00-00\",\"MsTime\":\"PT00H00M00S\",\"LoadDate\":\"0000-00-00\",\"LoadTime\":\"PT00H00M00S\",\"TpDate\":\"0000-00-00\",\"TpTime\":\"PT00H00M00S\",\"GiDate\":\"0000-00-00\",\"GiTime\":\"PT00H00M00S\",\"DeleteInd\":\"\",\"ReqClosed\":false,\"GrEndDate\":\"0000-00-00\",\"GrEndTime\":\"PT00H00M00S\",\"ComQty\":\"0.000\",\"ComDate\":\"0000-00-00\",\"GeoRoute\":\"\",\"Handoverdate\":\"0000-00-00\",\"Handovertime\":\"PT00H00M00S\"}]},\"ToItemService\":{\"results\":[{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/ServiceSet(Number='1000000003',ItemNo='00010',PckgNo='0000000281',LineNo='0000000002')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/ServiceSet(Number='1000000003',ItemNo='00010',PckgNo='0000000281',LineNo='0000000002')\",\"type\":\"ZP2P_API_PODETAILS_SRV.Service\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"PckgNo\":\"0000000281\",\"LineNo\":\"0000000002\",\"ExtLine\":\"0000000010\",\"OutlLevel\":0,\"OutlNo\":\"\",\"OutlInd\":\"\",\"SubpckgNo\":\"0000000000\",\"ServiceNo\":\"\",\"ServType\":\"\",\"Edition\":\"0000\",\"SscItem\":\"\",\"ExtServ\":\"\",\"Quantity\":\"100.000\",\"BaseUom\":\"EA\",\"UomIso\":\"EA\",\"OvfTol\":\"0.0\",\"OvfUnlim\":false,\"PriceUnit\":\"1\",\"GrPrice\":\"100.0000\",\"FromLine\":\"\",\"ToLine\":\"\",\"ShortText\":\"Repairing  charges\",\"Distrib\":\"\",\"PersNo\":\"00000000\",\"Wagetype\":\"\",\"PlnPckg\":\"0000000000\",\"PlnLine\":\"0000000000\",\"ConPckg\":\"0000000000\",\"ConLine\":\"0000000000\",\"TmpPckg\":\"0000000000\",\"TmpLine\":\"0000000000\",\"SscLim\":false,\"LimitLine\":\"0000000000\",\"TargetVal\":\"0.0000\",\"BaslineNo\":\"0000000000\",\"BasicLine\":\"\",\"Alternat\":\"\",\"Bidder\":\"\",\"SuppLine\":\"\",\"OpenQty\":\"\",\"Inform\":\"\",\"Blanket\":\"\",\"Eventual\":\"\",\"TaxCode\":\"\",\"Taxjurcode\":\"\",\"PriceChg\":false,\"MatlGroup\":\"P001\",\"Date\":\"0000-00-00\",\"Begintime\":\"PT00H00M00S\",\"Endtime\":\"PT00H00M00S\",\"ExtpersNo\":\"\",\"Formula\":\"\",\"FormVal1\":\"0.000\",\"FormVal2\":\"0.000\",\"FormVal3\":\"0.000\",\"FormVal4\":\"0.000\",\"FormVal5\":\"0.000\",\"Userf1Num\":\"0000000000\",\"Userf2Num\":\"0.000\",\"Userf1Txt\":\"\",\"Userf2Txt\":\"\",\"HiLineNo\":\"0000000000\",\"Extrefkey\":\"\",\"DeleteInd\":\"\",\"PerSdate\":\"0000-00-00\",\"PerEdate\":\"0000-00-00\",\"ExternalItemId\":\"\",\"ServiceItemKey\":\"0000000000\",\"NetValue\":\"10000.0000\"},{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/ServiceSet(Number='1000000003',ItemNo='00010',PckgNo='0000000281',LineNo='0000000003')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/ServiceSet(Number='1000000003',ItemNo='00010',PckgNo='0000000281',LineNo='0000000003')\",\"type\":\"ZP2P_API_PODETAILS_SRV.Service\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"PckgNo\":\"0000000281\",\"LineNo\":\"0000000003\",\"ExtLine\":\"0000000020\",\"OutlLevel\":0,\"OutlNo\":\"\",\"OutlInd\":\"\",\"SubpckgNo\":\"0000000000\",\"ServiceNo\":\"\",\"ServType\":\"\",\"Edition\":\"0000\",\"SscItem\":\"\",\"ExtServ\":\"\",\"Quantity\":\"50.000\",\"BaseUom\":\"EA\",\"UomIso\":\"EA\",\"OvfTol\":\"0.0\",\"OvfUnlim\":false,\"PriceUnit\":\"1\",\"GrPrice\":\"200.0000\",\"FromLine\":\"\",\"ToLine\":\"\",\"ShortText\":\"testing 2\",\"Distrib\":\"\",\"PersNo\":\"00000000\",\"Wagetype\":\"\",\"PlnPckg\":\"0000000000\",\"PlnLine\":\"0000000000\",\"ConPckg\":\"0000000000\",\"ConLine\":\"0000000000\",\"TmpPckg\":\"0000000000\",\"TmpLine\":\"0000000000\",\"SscLim\":false,\"LimitLine\":\"0000000000\",\"TargetVal\":\"0.0000\",\"BaslineNo\":\"0000000000\",\"BasicLine\":\"\",\"Alternat\":\"\",\"Bidder\":\"\",\"SuppLine\":\"\",\"OpenQty\":\"\",\"Inform\":\"\",\"Blanket\":\"\",\"Eventual\":\"\",\"TaxCode\":\"\",\"Taxjurcode\":\"\",\"PriceChg\":false,\"MatlGroup\":\"P001\",\"Date\":\"0000-00-00\",\"Begintime\":\"PT00H00M00S\",\"Endtime\":\"PT00H00M00S\",\"ExtpersNo\":\"\",\"Formula\":\"\",\"FormVal1\":\"0.000\",\"FormVal2\":\"0.000\",\"FormVal3\":\"0.000\",\"FormVal4\":\"0.000\",\"FormVal5\":\"0.000\",\"Userf1Num\":\"0000000000\",\"Userf2Num\":\"0.000\",\"Userf1Txt\":\"\",\"Userf2Txt\":\"\",\"HiLineNo\":\"0000000000\",\"Extrefkey\":\"\",\"DeleteInd\":\"\",\"PerSdate\":\"0000-00-00\",\"PerEdate\":\"0000-00-00\",\"ExternalItemId\":\"\",\"ServiceItemKey\":\"0000000000\",\"NetValue\":\"10000.0000\"}]},\"ToItemHistory\":{\"results\":[{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='00',PurchasingHistoryDocumentType='9',PurchasingHistoryDocumentYear='0000',PurchasingHistoryDocument='1000000039',PurchasingHistoryDocumentItem='0000',PurchasingHistoryCategory='D')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='00',PurchasingHistoryDocumentType='9',PurchasingHistoryDocumentYear='0000',PurchasingHistoryDocument='1000000039',PurchasingHistoryDocumentItem='0000',PurchasingHistoryCategory='D')\",\"type\":\"ZP2P_API_PODETAILS_SRV.History\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"AccountAssignmentNumber\":\"00\",\"PurchasingHistoryDocumentType\":\"9\",\"PurchasingHistoryDocumentYear\":\"0000\",\"PurchasingHistoryDocument\":\"1000000039\",\"PurchasingHistoryDocumentItem\":\"0000\",\"PurchasingHistoryCategory\":\"D\",\"GoodsMovementType\":\"\",\"PostingDate\":\"20210615\",\"Quantity\":\"1.000\",\"PurOrdAmountInCompanyCodeCrcy\":\"14000.0000\",\"PurchaseOrderAmount\":\"14000.0000\",\"Currency\":\"SAR\",\"GRIRAcctClrgAmtInCoCodeCrcy\":\"0.0000\",\"GdsRcptBlkdStkQtyInOrdQtyUnit\":\"0.000\",\"GdsRcptBlkdStkQtyInOrdPrcUnit\":\"0.000\",\"DebitCreditCode\":\"S\",\"InventoryValuationType\":\"\",\"IsCompletelyDelivered\":false,\"DocumentReferenceID\":\"\",\"ReferenceDocumentFiscalYear\":\"2021\",\"ReferenceDocument\":\"1000000039\",\"ReferenceDocumentItem\":\"0000\",\"AccountingDocumentCreationDate\":\"163823\",\"InvoiceAmtInCoCodeCrcy\":\"0.0000\",\"InvoiceAmountInFrgnCurrency\":\"0.0000\",\"Material\":\"\",\"Plant\":\"\",\"PricingDocument\":\"\",\"TaxCode\":\"\",\"QuantityInDeliveryQtyUnit\":\"0.000\",\"DeliveryQuantityUnit\":\"\",\"ManufacturerMaterial\":\"\",\"CompanyCodeCurrency\":\"SAR\",\"DocumentDate\":\"\",\"CurrencyIso\":\"SAR\",\"LocCurrIso\":\"SAR\",\"DelivUnitIso\":\"\",\"MaterialExternal\":\"\",\"MaterialGuid\":\"\",\"MaterialVersion\":\"\",\"PurMatExternal\":\"\",\"PurMatGuid\":\"\",\"PurMatVersion\":\"\",\"RefDocNoLong\":\"\",\"Batch\":\"\",\"StkSegment\":\"\",\"MaterialLong\":\"\",\"PurMatLong\":\"\",\"StkSegLong\":\"\",\"MoveReas\":\"0000\",\"EntryTime\":\"PT00H00M00S\",\"ConfSer\":\"0000\",\"RvslOfGoodsReceiptIsAllowed\":\"\",\"QtyInPurchaseOrderPriceUnit\":\"0.000\",\"ShipgInstrnSupplierCompliance\":\"\",\"GRIRAcctClrgAmtInTransacCrcy\":\"0.00\",\"QuantityInBaseUnit\":\"0.000\",\"GRIRAcctClrgAmtInOrdTrnsacCrcy\":\"0.00\",\"InvoiceAmtInPurOrdTransacCrcy\":\"0.00\",\"VltdGdsRcptBlkdStkQtyInOrdUnit\":\"0.000\",\"VltdGdsRcptBlkdQtyInOrdPrcUnit\":\"0.000\",\"IsToBeAcceptedAtOrigin\":\"\",\"ExchangeRateDifferenceAmount\":\"0.00\",\"ExchangeRate\":\"0.00000\",\"DeliveryDocument\":\"\",\"EntryDate\":\"\",\"DeliveryDocumentItem\":\"\",\"AccountingDocumentCreationTime\":\"\",\"SrvPos\":\"\",\"PackNo\":\"0000000000\",\"IntRow\":\"0000000000\",\"PlnPackNo\":\"0000000000\",\"PlnIntRow\":\"0000000000\",\"ExtRow\":\"0000000000\"},{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='00',PurchasingHistoryDocumentType='9',PurchasingHistoryDocumentYear='0000',PurchasingHistoryDocument='1000000040',PurchasingHistoryDocumentItem='0000',PurchasingHistoryCategory='D')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='00',PurchasingHistoryDocumentType='9',PurchasingHistoryDocumentYear='0000',PurchasingHistoryDocument='1000000040',PurchasingHistoryDocumentItem='0000',PurchasingHistoryCategory='D')\",\"type\":\"ZP2P_API_PODETAILS_SRV.History\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"AccountAssignmentNumber\":\"00\",\"PurchasingHistoryDocumentType\":\"9\",\"PurchasingHistoryDocumentYear\":\"0000\",\"PurchasingHistoryDocument\":\"1000000040\",\"PurchasingHistoryDocumentItem\":\"0000\",\"PurchasingHistoryCategory\":\"D\",\"GoodsMovementType\":\"\",\"PostingDate\":\"20210615\",\"Quantity\":\"1.000\",\"PurOrdAmountInCompanyCodeCrcy\":\"6000.0000\",\"PurchaseOrderAmount\":\"6000.0000\",\"Currency\":\"SAR\",\"GRIRAcctClrgAmtInCoCodeCrcy\":\"0.0000\",\"GdsRcptBlkdStkQtyInOrdQtyUnit\":\"0.000\",\"GdsRcptBlkdStkQtyInOrdPrcUnit\":\"0.000\",\"DebitCreditCode\":\"S\",\"InventoryValuationType\":\"\",\"IsCompletelyDelivered\":false,\"DocumentReferenceID\":\"\",\"ReferenceDocumentFiscalYear\":\"2021\",\"ReferenceDocument\":\"1000000040\",\"ReferenceDocumentItem\":\"0000\",\"AccountingDocumentCreationDate\":\"163825\",\"InvoiceAmtInCoCodeCrcy\":\"0.0000\",\"InvoiceAmountInFrgnCurrency\":\"0.0000\",\"Material\":\"\",\"Plant\":\"\",\"PricingDocument\":\"\",\"TaxCode\":\"\",\"QuantityInDeliveryQtyUnit\":\"0.000\",\"DeliveryQuantityUnit\":\"\",\"ManufacturerMaterial\":\"\",\"CompanyCodeCurrency\":\"SAR\",\"DocumentDate\":\"\",\"CurrencyIso\":\"SAR\",\"LocCurrIso\":\"SAR\",\"DelivUnitIso\":\"\",\"MaterialExternal\":\"\",\"MaterialGuid\":\"\",\"MaterialVersion\":\"\",\"PurMatExternal\":\"\",\"PurMatGuid\":\"\",\"PurMatVersion\":\"\",\"RefDocNoLong\":\"\",\"Batch\":\"\",\"StkSegment\":\"\",\"MaterialLong\":\"\",\"PurMatLong\":\"\",\"StkSegLong\":\"\",\"MoveReas\":\"0000\",\"EntryTime\":\"PT00H00M00S\",\"ConfSer\":\"0000\",\"RvslOfGoodsReceiptIsAllowed\":\"\",\"QtyInPurchaseOrderPriceUnit\":\"0.000\",\"ShipgInstrnSupplierCompliance\":\"\",\"GRIRAcctClrgAmtInTransacCrcy\":\"0.00\",\"QuantityInBaseUnit\":\"0.000\",\"GRIRAcctClrgAmtInOrdTrnsacCrcy\":\"0.00\",\"InvoiceAmtInPurOrdTransacCrcy\":\"0.00\",\"VltdGdsRcptBlkdStkQtyInOrdUnit\":\"0.000\",\"VltdGdsRcptBlkdQtyInOrdPrcUnit\":\"0.000\",\"IsToBeAcceptedAtOrigin\":\"\",\"ExchangeRateDifferenceAmount\":\"0.00\",\"ExchangeRate\":\"0.00000\",\"DeliveryDocument\":\"\",\"EntryDate\":\"\",\"DeliveryDocumentItem\":\"\",\"AccountingDocumentCreationTime\":\"\",\"SrvPos\":\"\",\"PackNo\":\"0000000000\",\"IntRow\":\"0000000000\",\"PlnPackNo\":\"0000000000\",\"PlnIntRow\":\"0000000000\",\"ExtRow\":\"0000000000\"},{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='1',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5000000466',PurchasingHistoryDocumentItem='0001',PurchasingHistoryCategory='E')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='1',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5000000466',PurchasingHistoryDocumentItem='0001',PurchasingHistoryCategory='E')\",\"type\":\"ZP2P_API_PODETAILS_SRV.History\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"AccountAssignmentNumber\":\"01\",\"PurchasingHistoryDocumentType\":\"1\",\"PurchasingHistoryDocumentYear\":\"2021\",\"PurchasingHistoryDocument\":\"5000000466\",\"PurchasingHistoryDocumentItem\":\"0001\",\"PurchasingHistoryCategory\":\"E\",\"GoodsMovementType\":\"101\",\"PostingDate\":\"20210615\",\"Quantity\":\"100.000\",\"PurOrdAmountInCompanyCodeCrcy\":\"10000.0000\",\"PurchaseOrderAmount\":\"10000.0000\",\"Currency\":\"SAR\",\"GRIRAcctClrgAmtInCoCodeCrcy\":\"0.0000\",\"GdsRcptBlkdStkQtyInOrdQtyUnit\":\"0.000\",\"GdsRcptBlkdStkQtyInOrdPrcUnit\":\"0.000\",\"DebitCreditCode\":\"S\",\"InventoryValuationType\":\"\",\"IsCompletelyDelivered\":false,\"DocumentReferenceID\":\"\",\"ReferenceDocumentFiscalYear\":\"2021\",\"ReferenceDocument\":\"1000000039\",\"ReferenceDocumentItem\":\"0001\",\"AccountingDocumentCreationDate\":\"163825\",\"InvoiceAmtInCoCodeCrcy\":\"0.0000\",\"InvoiceAmountInFrgnCurrency\":\"0.0000\",\"Material\":\"\",\"Plant\":\"2000\",\"PricingDocument\":\"\",\"TaxCode\":\"\",\"QuantityInDeliveryQtyUnit\":\"0.000\",\"DeliveryQuantityUnit\":\"\",\"ManufacturerMaterial\":\"\",\"CompanyCodeCurrency\":\"SAR\",\"DocumentDate\":\"\",\"CurrencyIso\":\"SAR\",\"LocCurrIso\":\"SAR\",\"DelivUnitIso\":\"\",\"MaterialExternal\":\"\",\"MaterialGuid\":\"\",\"MaterialVersion\":\"\",\"PurMatExternal\":\"\",\"PurMatGuid\":\"\",\"PurMatVersion\":\"\",\"RefDocNoLong\":\"\",\"Batch\":\"\",\"StkSegment\":\"\",\"MaterialLong\":\"\",\"PurMatLong\":\"\",\"StkSegLong\":\"\",\"MoveReas\":\"0000\",\"EntryTime\":\"PT00H00M00S\",\"ConfSer\":\"0000\",\"RvslOfGoodsReceiptIsAllowed\":\"\",\"QtyInPurchaseOrderPriceUnit\":\"0.000\",\"ShipgInstrnSupplierCompliance\":\"\",\"GRIRAcctClrgAmtInTransacCrcy\":\"0.00\",\"QuantityInBaseUnit\":\"0.000\",\"GRIRAcctClrgAmtInOrdTrnsacCrcy\":\"0.00\",\"InvoiceAmtInPurOrdTransacCrcy\":\"0.00\",\"VltdGdsRcptBlkdStkQtyInOrdUnit\":\"0.000\",\"VltdGdsRcptBlkdQtyInOrdPrcUnit\":\"0.000\",\"IsToBeAcceptedAtOrigin\":\"\",\"ExchangeRateDifferenceAmount\":\"0.00\",\"ExchangeRate\":\"0.00000\",\"DeliveryDocument\":\"\",\"EntryDate\":\"\",\"DeliveryDocumentItem\":\"\",\"AccountingDocumentCreationTime\":\"\",\"SrvPos\":\"\",\"PackNo\":\"0000000284\",\"IntRow\":\"0000000002\",\"PlnPackNo\":\"0000000281\",\"PlnIntRow\":\"0000000002\",\"ExtRow\":\"0000000010\"},{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='1',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5000000466',PurchasingHistoryDocumentItem='0002',PurchasingHistoryCategory='E')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='1',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5000000466',PurchasingHistoryDocumentItem='0002',PurchasingHistoryCategory='E')\",\"type\":\"ZP2P_API_PODETAILS_SRV.History\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"AccountAssignmentNumber\":\"01\",\"PurchasingHistoryDocumentType\":\"1\",\"PurchasingHistoryDocumentYear\":\"2021\",\"PurchasingHistoryDocument\":\"5000000466\",\"PurchasingHistoryDocumentItem\":\"0002\",\"PurchasingHistoryCategory\":\"E\",\"GoodsMovementType\":\"101\",\"PostingDate\":\"20210615\",\"Quantity\":\"20.000\",\"PurOrdAmountInCompanyCodeCrcy\":\"4000.0000\",\"PurchaseOrderAmount\":\"4000.0000\",\"Currency\":\"SAR\",\"GRIRAcctClrgAmtInCoCodeCrcy\":\"0.0000\",\"GdsRcptBlkdStkQtyInOrdQtyUnit\":\"0.000\",\"GdsRcptBlkdStkQtyInOrdPrcUnit\":\"0.000\",\"DebitCreditCode\":\"S\",\"InventoryValuationType\":\"\",\"IsCompletelyDelivered\":false,\"DocumentReferenceID\":\"\",\"ReferenceDocumentFiscalYear\":\"2021\",\"ReferenceDocument\":\"1000000039\",\"ReferenceDocumentItem\":\"0002\",\"AccountingDocumentCreationDate\":\"163825\",\"InvoiceAmtInCoCodeCrcy\":\"0.0000\",\"InvoiceAmountInFrgnCurrency\":\"0.0000\",\"Material\":\"\",\"Plant\":\"2000\",\"PricingDocument\":\"\",\"TaxCode\":\"\",\"QuantityInDeliveryQtyUnit\":\"0.000\",\"DeliveryQuantityUnit\":\"\",\"ManufacturerMaterial\":\"\",\"CompanyCodeCurrency\":\"SAR\",\"DocumentDate\":\"\",\"CurrencyIso\":\"SAR\",\"LocCurrIso\":\"SAR\",\"DelivUnitIso\":\"\",\"MaterialExternal\":\"\",\"MaterialGuid\":\"\",\"MaterialVersion\":\"\",\"PurMatExternal\":\"\",\"PurMatGuid\":\"\",\"PurMatVersion\":\"\",\"RefDocNoLong\":\"\",\"Batch\":\"\",\"StkSegment\":\"\",\"MaterialLong\":\"\",\"PurMatLong\":\"\",\"StkSegLong\":\"\",\"MoveReas\":\"0000\",\"EntryTime\":\"PT00H00M00S\",\"ConfSer\":\"0000\",\"RvslOfGoodsReceiptIsAllowed\":\"\",\"QtyInPurchaseOrderPriceUnit\":\"0.000\",\"ShipgInstrnSupplierCompliance\":\"\",\"GRIRAcctClrgAmtInTransacCrcy\":\"0.00\",\"QuantityInBaseUnit\":\"0.000\",\"GRIRAcctClrgAmtInOrdTrnsacCrcy\":\"0.00\",\"InvoiceAmtInPurOrdTransacCrcy\":\"0.00\",\"VltdGdsRcptBlkdStkQtyInOrdUnit\":\"0.000\",\"VltdGdsRcptBlkdQtyInOrdPrcUnit\":\"0.000\",\"IsToBeAcceptedAtOrigin\":\"\",\"ExchangeRateDifferenceAmount\":\"0.00\",\"ExchangeRate\":\"0.00000\",\"DeliveryDocument\":\"\",\"EntryDate\":\"\",\"DeliveryDocumentItem\":\"\",\"AccountingDocumentCreationTime\":\"\",\"SrvPos\":\"\",\"PackNo\":\"0000000284\",\"IntRow\":\"0000000003\",\"PlnPackNo\":\"0000000281\",\"PlnIntRow\":\"0000000003\",\"ExtRow\":\"0000000020\"},{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='1',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5000000467',PurchasingHistoryDocumentItem='0001',PurchasingHistoryCategory='E')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='1',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5000000467',PurchasingHistoryDocumentItem='0001',PurchasingHistoryCategory='E')\",\"type\":\"ZP2P_API_PODETAILS_SRV.History\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"AccountAssignmentNumber\":\"01\",\"PurchasingHistoryDocumentType\":\"1\",\"PurchasingHistoryDocumentYear\":\"2021\",\"PurchasingHistoryDocument\":\"5000000467\",\"PurchasingHistoryDocumentItem\":\"0001\",\"PurchasingHistoryCategory\":\"E\",\"GoodsMovementType\":\"101\",\"PostingDate\":\"20210615\",\"Quantity\":\"30.000\",\"PurOrdAmountInCompanyCodeCrcy\":\"6000.0000\",\"PurchaseOrderAmount\":\"6000.0000\",\"Currency\":\"SAR\",\"GRIRAcctClrgAmtInCoCodeCrcy\":\"0.0000\",\"GdsRcptBlkdStkQtyInOrdQtyUnit\":\"0.000\",\"GdsRcptBlkdStkQtyInOrdPrcUnit\":\"0.000\",\"DebitCreditCode\":\"S\",\"InventoryValuationType\":\"\",\"IsCompletelyDelivered\":false,\"DocumentReferenceID\":\"\",\"ReferenceDocumentFiscalYear\":\"2021\",\"ReferenceDocument\":\"1000000040\",\"ReferenceDocumentItem\":\"0001\",\"AccountingDocumentCreationDate\":\"163837\",\"InvoiceAmtInCoCodeCrcy\":\"0.0000\",\"InvoiceAmountInFrgnCurrency\":\"0.0000\",\"Material\":\"\",\"Plant\":\"2000\",\"PricingDocument\":\"\",\"TaxCode\":\"\",\"QuantityInDeliveryQtyUnit\":\"0.000\",\"DeliveryQuantityUnit\":\"\",\"ManufacturerMaterial\":\"\",\"CompanyCodeCurrency\":\"SAR\",\"DocumentDate\":\"\",\"CurrencyIso\":\"SAR\",\"LocCurrIso\":\"SAR\",\"DelivUnitIso\":\"\",\"MaterialExternal\":\"\",\"MaterialGuid\":\"\",\"MaterialVersion\":\"\",\"PurMatExternal\":\"\",\"PurMatGuid\":\"\",\"PurMatVersion\":\"\",\"RefDocNoLong\":\"\",\"Batch\":\"\",\"StkSegment\":\"\",\"MaterialLong\":\"\",\"PurMatLong\":\"\",\"StkSegLong\":\"\",\"MoveReas\":\"0000\",\"EntryTime\":\"PT00H00M00S\",\"ConfSer\":\"0000\",\"RvslOfGoodsReceiptIsAllowed\":\"\",\"QtyInPurchaseOrderPriceUnit\":\"0.000\",\"ShipgInstrnSupplierCompliance\":\"\",\"GRIRAcctClrgAmtInTransacCrcy\":\"0.00\",\"QuantityInBaseUnit\":\"0.000\",\"GRIRAcctClrgAmtInOrdTrnsacCrcy\":\"0.00\",\"InvoiceAmtInPurOrdTransacCrcy\":\"0.00\",\"VltdGdsRcptBlkdStkQtyInOrdUnit\":\"0.000\",\"VltdGdsRcptBlkdQtyInOrdPrcUnit\":\"0.000\",\"IsToBeAcceptedAtOrigin\":\"\",\"ExchangeRateDifferenceAmount\":\"0.00\",\"ExchangeRate\":\"0.00000\",\"DeliveryDocument\":\"\",\"EntryDate\":\"\",\"DeliveryDocumentItem\":\"\",\"AccountingDocumentCreationTime\":\"\",\"SrvPos\":\"\",\"PackNo\":\"0000000287\",\"IntRow\":\"0000000084\",\"PlnPackNo\":\"0000000281\",\"PlnIntRow\":\"0000000003\",\"ExtRow\":\"0000000020\"},{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='2',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5105600239',PurchasingHistoryDocumentItem='0001',PurchasingHistoryCategory='Q')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='2',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5105600239',PurchasingHistoryDocumentItem='0001',PurchasingHistoryCategory='Q')\",\"type\":\"ZP2P_API_PODETAILS_SRV.History\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"AccountAssignmentNumber\":\"01\",\"PurchasingHistoryDocumentType\":\"2\",\"PurchasingHistoryDocumentYear\":\"2021\",\"PurchasingHistoryDocument\":\"5105600239\",\"PurchasingHistoryDocumentItem\":\"0001\",\"PurchasingHistoryCategory\":\"Q\",\"GoodsMovementType\":\"\",\"PostingDate\":\"20210617\",\"Quantity\":\"1.000\",\"PurOrdAmountInCompanyCodeCrcy\":\"100.0000\",\"PurchaseOrderAmount\":\"100.0000\",\"Currency\":\"SAR\",\"GRIRAcctClrgAmtInCoCodeCrcy\":\"100.0000\",\"GdsRcptBlkdStkQtyInOrdQtyUnit\":\"0.000\",\"GdsRcptBlkdStkQtyInOrdPrcUnit\":\"0.000\",\"DebitCreditCode\":\"S\",\"InventoryValuationType\":\"\",\"IsCompletelyDelivered\":false,\"DocumentReferenceID\":\"\",\"ReferenceDocumentFiscalYear\":\"2021\",\"ReferenceDocument\":\"1000000039\",\"ReferenceDocumentItem\":\"0001\",\"AccountingDocumentCreationDate\":\"160311\",\"InvoiceAmtInCoCodeCrcy\":\"100.0000\",\"InvoiceAmountInFrgnCurrency\":\"100.0000\",\"Material\":\"\",\"Plant\":\"2000\",\"PricingDocument\":\"\",\"TaxCode\":\"I1\",\"QuantityInDeliveryQtyUnit\":\"0.000\",\"DeliveryQuantityUnit\":\"\",\"ManufacturerMaterial\":\"\",\"CompanyCodeCurrency\":\"SAR\",\"DocumentDate\":\"\",\"CurrencyIso\":\"SAR\",\"LocCurrIso\":\"SAR\",\"DelivUnitIso\":\"\",\"MaterialExternal\":\"\",\"MaterialGuid\":\"\",\"MaterialVersion\":\"\",\"PurMatExternal\":\"\",\"PurMatGuid\":\"\",\"PurMatVersion\":\"\",\"RefDocNoLong\":\"\",\"Batch\":\"\",\"StkSegment\":\"\",\"MaterialLong\":\"\",\"PurMatLong\":\"\",\"StkSegLong\":\"\",\"MoveReas\":\"0000\",\"EntryTime\":\"PT00H00M00S\",\"ConfSer\":\"0000\",\"RvslOfGoodsReceiptIsAllowed\":\"\",\"QtyInPurchaseOrderPriceUnit\":\"0.000\",\"ShipgInstrnSupplierCompliance\":\"\",\"GRIRAcctClrgAmtInTransacCrcy\":\"0.00\",\"QuantityInBaseUnit\":\"0.000\",\"GRIRAcctClrgAmtInOrdTrnsacCrcy\":\"0.00\",\"InvoiceAmtInPurOrdTransacCrcy\":\"0.00\",\"VltdGdsRcptBlkdStkQtyInOrdUnit\":\"0.000\",\"VltdGdsRcptBlkdQtyInOrdPrcUnit\":\"0.000\",\"IsToBeAcceptedAtOrigin\":\"\",\"ExchangeRateDifferenceAmount\":\"0.00\",\"ExchangeRate\":\"0.00000\",\"DeliveryDocument\":\"\",\"EntryDate\":\"\",\"DeliveryDocumentItem\":\"\",\"AccountingDocumentCreationTime\":\"\",\"SrvPos\":\"\",\"PackNo\":\"0000000284\",\"IntRow\":\"0000000002\",\"PlnPackNo\":\"0000000281\",\"PlnIntRow\":\"0000000002\",\"ExtRow\":\"0000000010\"},{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='2',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5105600239',PurchasingHistoryDocumentItem='0002',PurchasingHistoryCategory='Q')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistorySet(Number='1000000003',ItemNo='00010',AccountAssignmentNumber='01',PurchasingHistoryDocumentType='2',PurchasingHistoryDocumentYear='2021',PurchasingHistoryDocument='5105600239',PurchasingHistoryDocumentItem='0002',PurchasingHistoryCategory='Q')\",\"type\":\"ZP2P_API_PODETAILS_SRV.History\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"AccountAssignmentNumber\":\"01\",\"PurchasingHistoryDocumentType\":\"2\",\"PurchasingHistoryDocumentYear\":\"2021\",\"PurchasingHistoryDocument\":\"5105600239\",\"PurchasingHistoryDocumentItem\":\"0002\",\"PurchasingHistoryCategory\":\"Q\",\"GoodsMovementType\":\"\",\"PostingDate\":\"20210617\",\"Quantity\":\"1.000\",\"PurOrdAmountInCompanyCodeCrcy\":\"200.0000\",\"PurchaseOrderAmount\":\"200.0000\",\"Currency\":\"SAR\",\"GRIRAcctClrgAmtInCoCodeCrcy\":\"200.0000\",\"GdsRcptBlkdStkQtyInOrdQtyUnit\":\"0.000\",\"GdsRcptBlkdStkQtyInOrdPrcUnit\":\"0.000\",\"DebitCreditCode\":\"S\",\"InventoryValuationType\":\"\",\"IsCompletelyDelivered\":false,\"DocumentReferenceID\":\"\",\"ReferenceDocumentFiscalYear\":\"2021\",\"ReferenceDocument\":\"1000000039\",\"ReferenceDocumentItem\":\"0002\",\"AccountingDocumentCreationDate\":\"160311\",\"InvoiceAmtInCoCodeCrcy\":\"200.0000\",\"InvoiceAmountInFrgnCurrency\":\"200.0000\",\"Material\":\"\",\"Plant\":\"2000\",\"PricingDocument\":\"\",\"TaxCode\":\"I1\",\"QuantityInDeliveryQtyUnit\":\"0.000\",\"DeliveryQuantityUnit\":\"\",\"ManufacturerMaterial\":\"\",\"CompanyCodeCurrency\":\"SAR\",\"DocumentDate\":\"\",\"CurrencyIso\":\"SAR\",\"LocCurrIso\":\"SAR\",\"DelivUnitIso\":\"\",\"MaterialExternal\":\"\",\"MaterialGuid\":\"\",\"MaterialVersion\":\"\",\"PurMatExternal\":\"\",\"PurMatGuid\":\"\",\"PurMatVersion\":\"\",\"RefDocNoLong\":\"\",\"Batch\":\"\",\"StkSegment\":\"\",\"MaterialLong\":\"\",\"PurMatLong\":\"\",\"StkSegLong\":\"\",\"MoveReas\":\"0000\",\"EntryTime\":\"PT00H00M00S\",\"ConfSer\":\"0000\",\"RvslOfGoodsReceiptIsAllowed\":\"\",\"QtyInPurchaseOrderPriceUnit\":\"0.000\",\"ShipgInstrnSupplierCompliance\":\"\",\"GRIRAcctClrgAmtInTransacCrcy\":\"0.00\",\"QuantityInBaseUnit\":\"0.000\",\"GRIRAcctClrgAmtInOrdTrnsacCrcy\":\"0.00\",\"InvoiceAmtInPurOrdTransacCrcy\":\"0.00\",\"VltdGdsRcptBlkdStkQtyInOrdUnit\":\"0.000\",\"VltdGdsRcptBlkdQtyInOrdPrcUnit\":\"0.000\",\"IsToBeAcceptedAtOrigin\":\"\",\"ExchangeRateDifferenceAmount\":\"0.00\",\"ExchangeRate\":\"0.00000\",\"DeliveryDocument\":\"\",\"EntryDate\":\"\",\"DeliveryDocumentItem\":\"\",\"AccountingDocumentCreationTime\":\"\",\"SrvPos\":\"\",\"PackNo\":\"0000000284\",\"IntRow\":\"0000000003\",\"PlnPackNo\":\"0000000281\",\"PlnIntRow\":\"0000000003\",\"ExtRow\":\"0000000020\"}]},\"ToItemHistoryTotal\":{\"results\":[{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistoryTotalSet(Number='1000000003',ItemNo='00010',SerialNo='00')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistoryTotalSet(Number='1000000003',ItemNo='00010',SerialNo='00')\",\"type\":\"ZP2P_API_PODETAILS_SRV.HistoryTotal\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"SerialNo\":\"00\",\"WithdrQty\":\"0.000\",\"BlockedQy\":\"0.000\",\"BlQty\":\"0.000\",\"DelivQty\":\"150.000\",\"PoPrQnt\":\"150.000\",\"ValGrLoc\":\"20000.0000\",\"ValGrFor\":\"20000.0000\",\"IvQty\":\"2.000\",\"IvQtyPo\":\"2.000\",\"ValIvLoc\":\"300.0000\",\"ValIvFor\":\"300.0000\",\"ClValLoc\":\"300.0000\",\"ClValFor\":\"300.0000\",\"DopVlLoc\":\"0.0000\",\"IvvalLoc\":\"300.0000\",\"IvvalFor\":\"300.0000\",\"DlQtyTrsp\":\"0.000\",\"BlQtyTotal\":\"0.000\",\"DlQtyTotal\":\"0.000\",\"IvQtyTotal\":\"0.000\",\"Currency\":\"SAR\",\"CurrencyIso\":\"SAR\"},{\"__metadata\":{\"id\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistoryTotalSet(Number='1000000003',ItemNo='00010',SerialNo='01')\",\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/HistoryTotalSet(Number='1000000003',ItemNo='00010',SerialNo='01')\",\"type\":\"ZP2P_API_PODETAILS_SRV.HistoryTotal\"},\"Number\":\"1000000003\",\"ItemNo\":\"00010\",\"SerialNo\":\"01\",\"WithdrQty\":\"0.000\",\"BlockedQy\":\"0.000\",\"BlQty\":\"0.000\",\"DelivQty\":\"150.000\",\"PoPrQnt\":\"150.000\",\"ValGrLoc\":\"20000.0000\",\"ValGrFor\":\"20000.0000\",\"IvQty\":\"2.000\",\"IvQtyPo\":\"2.000\",\"ValIvLoc\":\"300.0000\",\"ValIvFor\":\"300.0000\",\"ClValLoc\":\"300.0000\",\"ClValFor\":\"300.0000\",\"DopVlLoc\":\"0.0000\",\"IvvalLoc\":\"300.0000\",\"IvvalFor\":\"300.0000\",\"DlQtyTrsp\":\"0.000\",\"BlQtyTotal\":\"0.000\",\"DlQtyTotal\":\"0.000\",\"IvQtyTotal\":\"0.000\",\"Currency\":\"SAR\",\"CurrencyIso\":\"SAR\"}]},\"ToItemDeliveryCostHistory\":{\"results\":[]},\"ToHeader\":{\"__deferred\":{\"uri\":\"https:\\/\\/sd4.menabev.com:443\\/sap\\/opu\\/odata\\/sap\\/ZP2P_API_PODETAILS_SRV\\/ItemSet(Number='1000000003',ItemNo='00010')\\/ToHeader\"}}}]},\"ToHeaderNote\":{\"results\":[]}}]}}";
		RootOdata obj = new Gson().fromJson(response, RootOdata.class);
		System.out.println("JSON OBJ::::::::::::" + obj.toString());
		//////// Additional
		if (!ServiceUtil.isEmpty(response)) {
			System.out.println("JSON OBJ::::::::::::" + obj.toString());
			PurchaseDocumentHeaderDto header = new PurchaseDocumentHeaderDto();
			header = getExtractedHeader(obj);
			System.out.println(header);
		}
	}
	//// public static void main(String[] args) throws ParseException {
	//// String date = "2021-06-14";
	//// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	//// Date createDate = df.parse(date);
	//// System.out.println(createDate.getTime());
	////
	// }

	// public static void main(String[] args) {
	//
	// Date date;
	// SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
	// SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMddHHmmss");
	// SimpleDateFormat df3 = new SimpleDateFormat("HHmmss");
	//// df2.setTimeZone(TimeZone.getTimeZone("GMT-2:30"));
	// try {
	// date = df.parse("20210614");
	// System.out.println(date.getTime());
	//
	// Date date2 = df2.parse("2021061513365");
	// System.out.println(date2.getTime());
	//
	//// Date dates = new Date("175052");
	////
	//// String datet = df3.format(dates);
	//// System.err.println(datet);
	//// Time t = Time.valueOf("175052");
	//// Long l = t.getTime();
	//// System.err.println(l);
	//
	// DateTimeFormatter formatter =
	// DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	// ZoneId destinationTimeZone = ZoneId.of("Asia/Riyadh");
	// String date3 = LocalDateTime.parse("20210615133650", formatter)
	// .atOffset(ZoneOffset.UTC)
	// .atZoneSameInstant(destinationTimeZone)
	// .format(formatter);
	// System.out.println(date3);
	// Date date5 = df2.parse(date3);
	// System.out.println("aaa"+date5.getTime());
	// Date d = new Date(Long.parseLong(date3) * 1000);
	// System.out.println(d);
	// String input = "PT00H00M00S";
	// String result = input.replaceAll("[a-zA-Z]", "");
	// System.out.println(result);
	//
	// } catch (ParseException e) {
	// throw new RuntimeException("Failed to parse date: ", e);
	// }
	// }

	@Override
	public List<PurchaseDocumentHeaderDto> referencePoApi(AddPoInputDto dto) {
		List<PurchaseDocumentHeaderDto> poHeaders = new ArrayList<>();
		List<String> purchaseOrder = new ArrayList<>();
		if (ServiceUtil.isEmpty(dto.getPurchaseOrder())) {

			String finalQuery = "SELECT distinct REF_PURCHASE_DOC as documentNumber from INVOICE_HEADER where "
					+ "REQUEST_ID = '" + dto.getRequestId() + "'"
					+ " union select distinct CAST (REF_DOC_NUM AS NVARCHAR(10)) as documentNumber "
					+ "from INVOICE_ITEM where " + "REQUEST_ID = '" + dto.getRequestId() + "'";
			Query queryForOutput = entityManager.createNativeQuery(finalQuery);
			purchaseOrder = queryForOutput.getResultList();
			System.out.println(purchaseOrder);
		} else {
			for (PurchaseOrder po : dto.getPurchaseOrder()) {
				purchaseOrder.add(po.getDocumentNumber());
			}
		}

		try {
			System.out.println("PURCHASE ORDER:::::::" + purchaseOrder);
			ModelMapper mapper = new ModelMapper();
			List<PurchaseDocumentHeaderDo> headerDos = new ArrayList<>();
			headerDos = poHeaderRepository.getPo(purchaseOrder);
			if (!ServiceUtil.isEmpty(headerDos)) {
				for (PurchaseDocumentHeaderDo headerDo : headerDos) {
					PurchaseDocumentHeaderDto headerDto = mapper.map(headerDo, PurchaseDocumentHeaderDto.class);
					List<PurchaseDocumentItemDo> itemDos = new ArrayList<>();
					// Get Items
					List<PurchaseDocumentItemDto> itemDtos = new ArrayList<>();
					if (!ServiceUtil.isEmpty(headerDto.getDocumentNumber())) {

						itemDos = purchaseDocumentItemRespository.getItems(headerDto.getDocumentNumber());
						if (!ServiceUtil.isEmpty(itemDos)) {
							for (PurchaseDocumentItemDo itemDo : itemDos) {
								PurchaseDocumentItemDto itemDto = new PurchaseDocumentItemDto();
								itemDto = mapper.map(itemDo, PurchaseDocumentItemDto.class);
								// Get ItemService
								List<PoItemServicesDo> poItemServicesDos = new ArrayList<>();
								poItemServicesDos = poItemServicesRepository.getServices(headerDto.getDocumentNumber());
								List<PoItemServicesDto> poItemServicesDtos = new ArrayList<>();
								if (!ServiceUtil.isEmpty(poItemServicesDos)) {
									for (PoItemServicesDo poItemServicesDo : poItemServicesDos) {
										PoItemServicesDto poItemServicesDto = new PoItemServicesDto();
										poItemServicesDto = mapper.map(poItemServicesDo, PoItemServicesDto.class);
										poItemServicesDtos.add(poItemServicesDto);

									}
								}
								itemDto.setPoItemServices(poItemServicesDtos);
								// Get ItemAccountAssignment
								List<PoItemAccountAssignDo> poAccountAssignmentDos = new ArrayList<>();
								poAccountAssignmentDos = poItemAccountAssignRepository
										.getAccountAssignmentServices(headerDto.getDocumentNumber());
								List<PoItemAccountAssignDto> poAccountAssignmentDtos = new ArrayList<>();
								if (!ServiceUtil.isEmpty(poAccountAssignmentDos)) {
									for (PoItemAccountAssignDo poAccountAssignmentDo : poAccountAssignmentDos) {
										PoItemAccountAssignDto poAccountAssignmentDto = new PoItemAccountAssignDto();
										poAccountAssignmentDto = mapper.map(poAccountAssignmentDo,
												PoItemAccountAssignDto.class);
										poAccountAssignmentDtos.add(poAccountAssignmentDto);

									}
								}
								itemDto.setPoAccountAssigment(poAccountAssignmentDtos);

								// Get ItemSchedules
								List<PoSchedulesDo> poSchedulesDos = new ArrayList<>();
								poSchedulesDos = poSchedulesRepository.getPoSchedules(headerDto.getDocumentNumber());
								List<PoSchedulesDto> poSchedulesDtos = new ArrayList<>();
								if (!ServiceUtil.isEmpty(poSchedulesDos)) {
									for (PoSchedulesDo poSchedulesDo : poSchedulesDos) {
										PoSchedulesDto poSchedulesDto = new PoSchedulesDto();
										poSchedulesDto = mapper.map(poSchedulesDo, PoSchedulesDto.class);
										poSchedulesDtos.add(poSchedulesDto);

									}
								}
								itemDto.setSchedules(poSchedulesDtos);
								itemDtos.add(itemDto);
							}
						}

						headerDto.setPoItem(itemDtos);
					}
					// Get poHistory
					List<PoHistoryDo> poHistoryDos = new ArrayList<>();
					poHistoryDos = poHistoryRepository.getHistory(headerDto.getDocumentNumber());
					List<PoHistoryDto> poHistoryDtos = new ArrayList<>();
					if (!ServiceUtil.isEmpty(poHistoryDos)) {
						for (PoHistoryDo poHistoryDo : poHistoryDos) {
							PoHistoryDto poHistoryDto = new PoHistoryDto();
							poHistoryDto = mapper.map(poHistoryDo, PoHistoryDto.class);
							poHistoryDtos.add(poHistoryDto);

						}
					}
					headerDto.setPoHistory(poHistoryDtos);
					// Get poHistoryTotals
					List<PoHistoryTotalsDo> poHistoryTotalDos = new ArrayList<>();
					poHistoryTotalDos = poHistoryTotalsRepository.getHistoryTotals(headerDto.getDocumentNumber());
					List<PoHistoryTotalsDto> poHistoryTotalDtos = new ArrayList<>();
					if (!ServiceUtil.isEmpty(poHistoryTotalDos)) {
						for (PoHistoryTotalsDo poHistoryTotalDo : poHistoryTotalDos) {
							PoHistoryTotalsDto poHistoryTotalDto = new PoHistoryTotalsDto();
							poHistoryTotalDto = mapper.map(poHistoryTotalDo, PoHistoryTotalsDto.class);
							poHistoryTotalDtos.add(poHistoryTotalDto);

						}
					}
					headerDto.setPoHistoryTotals(poHistoryTotalDtos);
					// Get poDeliveryCostHistory
					List<PoDeliveryCostHistoryDo> poDeliveryCostHistoryDos = new ArrayList<>();
					poDeliveryCostHistoryDos = poDeliveryCostHistoryRepository
							.getDeliveryCostHistory(headerDto.getDocumentNumber());
					List<PoDeliveryCostHistoryDto> poDeliveryCostHistoryDtos = new ArrayList<>();
					if (!ServiceUtil.isEmpty(poDeliveryCostHistoryDos)) {
						for (PoDeliveryCostHistoryDo poDeliveryCostHistoryDo : poDeliveryCostHistoryDos) {
							PoDeliveryCostHistoryDto poDeliveryCostHistoryDto = new PoDeliveryCostHistoryDto();
							poDeliveryCostHistoryDto = mapper.map(poDeliveryCostHistoryDo,
									PoDeliveryCostHistoryDto.class);
							poDeliveryCostHistoryDtos.add(poDeliveryCostHistoryDto);

						}
					}
					headerDto.setPoDeliveryCostHistory(poDeliveryCostHistoryDtos);

					poHeaders.add(headerDto);
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			return poHeaders;
		}

		return poHeaders;
	}

	@Override
	public InvoiceHeaderDto autoPostApi(InvoiceHeaderDto dto) throws URISyntaxException, IOException, ParseException {

		// a. Take the OCR data and store in HANA DB with
		// invoiceHeader-invoiceStatus=NEW
		//
		// b. Determine VendorId using vendor address data via Odata call.

		List<PoSearchApiDto> forAddPoApi = new ArrayList<>();
		String supplier = null;
		if (!ServiceUtil.isEmpty(dto.getZipCode()) && !ServiceUtil.isEmpty(dto.getVendorName())) {

			String vendorName = dto.getVendorName().replace(" ", "%20");
			// Get Business Partner
			String url = "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartnerAddress?"
					+ "$format=json&$filter=PostalCode%20eq%20%27" + dto.getZipCode() + "%27%20and%20"
					+ "substringof(%27" + vendorName + "%27,%20FullName)%20eq%20true";
			Map<String, Object> map = poSearchApiServiceImpl.getDestination("SD4_DEST");
			ResponseEntity<?> response = poSearchApiServiceImpl.consumingOdataService(url, null, "GET", map);
			System.out.println("RESPONSE:::::::::::::::431:::" + response.getBody());
			JSONObject node = new JSONObject(response.getBody().toString());
			System.out.println("NODE:::::::::::::::::" + node);
			JSONArray resultsArray = node.getJSONObject("d").getJSONArray("results");
			List<String> businessPartner = new ArrayList<>();
			for (int i = 0; i < resultsArray.length(); i++) {

				if (!ServiceUtil.isEmpty(resultsArray.getJSONObject(i).getString("BusinessPartner"))) {
					businessPartner.add(resultsArray.getJSONObject(i).getString("BusinessPartner"));
				}
			}
			System.out.println("BusinessPartner::::::" + businessPartner);

			// Get Supplier
			if (!ServiceUtil.isEmpty(businessPartner)) {
				String filter = "";

				for (String obj : businessPartner) {
					filter = filter + "Supplier%20eq%20%27" + obj + "%27%20or%20";
				}

				String urlSup = "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_Supplier?$format=json&$filter="
						+ filter.substring(0, filter.length() - 11);
				ResponseEntity<?> responseSup = poSearchApiServiceImpl.consumingOdataService(urlSup, null, "GET", map);
				System.out.println("RESPONSE:::::::::::::::431:::" + response.getBody());
				JSONObject nodeSup = new JSONObject(response.getBody().toString());
				System.out.println("NODE:::::::::::::::::" + node);
				JSONArray resultsArraySup = node.getJSONObject("d").getJSONArray("results");

				for (int i = 0; i < resultsArraySup.length(); i++) {
					if (!ServiceUtil.isEmpty(resultsArraySup.getJSONObject(i).getString("BusinessPartner"))) {
						supplier = (resultsArraySup.getJSONObject(i).getString("BusinessPartner"));
						break;
					}
				}
			}
		}

		System.out.println("Supplier::::::" + supplier);
		dto.setVendorId(supplier);
		if (ServiceUtil.isEmpty(dto.getRefpurchaseDoc()) && ServiceUtil.isEmpty(dto.getDeliveryNote())) {
			dto.setInvoiceStatusText("PO Missing/Invalid");
			dto.setInvoiceStatus(ApplicationConstants.PO_MISSING_OR_INVALID);
		} else if (!ServiceUtil.isEmpty(dto.getRefpurchaseDoc())) {
			System.out.println("REF DOC NUM:::::: 1595" + dto.getRefpurchaseDoc());
			// i. Trigger OData service to fetch only PO header data(swarna)
			List<String> documentNumber = new ArrayList<>();
			documentNumber.add(dto.getRefpurchaseDoc());
			PoSearchApiDto poSearchApiDto = new PoSearchApiDto();
			List<PoSearchApiDto> responseOdata = poSearchApiServiceImpl.searchByDocumentNumber(documentNumber,
					poSearchApiDto);
			forAddPoApi = responseOdata;
			System.out.println("Odata Response::::: 1602" + responseOdata);
			for (PoSearchApiDto odata : responseOdata) {
				System.out.println("1604 " + odata);
				if (ServiceUtil.isEmpty(odata.getDocumentNumber())) {
					dto.setInvoiceStatusText("PO Missing/Invalid");
					dto.setInvoiceStatus(ApplicationConstants.PO_MISSING_OR_INVALID);
				} else {
					if (!ServiceUtil.isEmpty(odata.getDocumentCategory())) {
						System.out.println("Document Category ::" + odata.getDocumentCategory());
						if (!("F".equals(odata.getDocumentCategory()) || "L".equals(odata.getDocumentCategory()))) {
							System.out.println("1612");
							dto.setInvoiceStatusText("PO Missing/Invalid");
							dto.setInvoiceStatus(ApplicationConstants.PO_MISSING_OR_INVALID);
						}
					}
				}
			}
		} else if (!ServiceUtil.isEmpty(dto.getDeliveryNote())) {
			System.out.println("DELIVERY NOTE NUMBER::::::" + dto.getDeliveryNote());
			// 1. Extract the Purchase Document(s) from Delivery note items. You
			// can get multiple unique numbers
			PoSearchApiDto poSearchApiDto = new PoSearchApiDto();
			poSearchApiDto.setDeliveryNoteNumber(dto.getDeliveryNote());
			List<PoSearchApiDto> responseOdata = poSearchApiServiceImpl
					.searchByDeliveryNoteNumber(dto.getDeliveryNote(), poSearchApiDto);
			forAddPoApi = responseOdata;
			// 2. Call the OData service in one shot to fetch the purchase
			// document header(s) by passing the PO numbers from step 1 above,
			// and find the purchase document category again.
			if (!ServiceUtil.isEmpty(responseOdata)) {
				for (PoSearchApiDto odataRes : responseOdata) {
					if (!("F".equals(odataRes.getDocumentCategory()) || "L".equals(odataRes.getDocumentCategory()))) {
						dto.setInvoiceStatusText("PO Missing/Invalid");
						dto.setInvoiceStatus(ApplicationConstants.PO_MISSING_OR_INVALID);
					}
				}
			}

		}

		// f. After steps b,c and d,e
		//
		// i. if invoice Status = PO Missing/Invalid
		//
		// 1. Call Duplicate Check API
		//
		// 2. If duplicateCheck Fails, then InvoiceHeader-InvoiceStatus=
		// Duplicate.
		if (ApplicationConstants.PO_MISSING_OR_INVALID.equals(dto.getInvoiceStatus())) {
			InvoiceHeaderObjectDto duplicateCheckDto = new InvoiceHeaderObjectDto();
			if (!ServiceUtil.isEmpty(dto.getCompanyCode())) {
				duplicateCheckDto.setCompanyCode(dto.getCompanyCode());
			}
			if (!ServiceUtil.isEmpty(dto.getInvoiceAmount())) {
				duplicateCheckDto.setInvoiceAmount(dto.getInvoiceAmount().toString());
			}
			if (!ServiceUtil.isEmpty(dto.getInvoiceDate())) {
				duplicateCheckDto.setInvoiceDate(dto.getInvoiceDate());
			}
			if (!ServiceUtil.isEmpty(dto.getRefpurchaseDoc())) {
				duplicateCheckDto.setInvoiceReference(dto.getRefpurchaseDoc());
			}
			if (!ServiceUtil.isEmpty(dto.getInvoiceStatus())) {
				duplicateCheckDto.setInvoiceStatus(dto.getInvoiceStatus());
			}
			if (!ServiceUtil.isEmpty(dto.getRequestId())) {
				duplicateCheckDto.setRequestId(dto.getRequestId());
			}
			if (!ServiceUtil.isEmpty(dto.getVendorId())) {
				duplicateCheckDto.setVendorId(dto.getVendorId());
			}

			InvoiceHeaderObjectDto duplicateCheckResponse = duplicatecheckServiceImpl.duplicateCheck(duplicateCheckDto);

			if (!ServiceUtil.isEmpty(duplicateCheckResponse)) {
				if (duplicateCheckResponse.getIsDuplicate()) {
					System.out.println("Is duplicate 1674 " + duplicateCheckResponse.getIsDuplicate());
					dto.setInvoiceStatus(ApplicationConstants.DUPLICATE_INVOICE);
					dto.setInvoiceStatusText("Duplicate Invoice");
				}
			}
		}
		// ii. If invoice status = New,
		System.out.println("I am here ::" + dto.getInvoiceStatus());
		if (ApplicationConstants.NEW_INVOICE.equals(dto.getInvoiceStatus())) {
			AddPoInputDto addPoApiInput = new AddPoInputDto();
			List<PurchaseOrder> purchaseOrder = new ArrayList<>();
			System.err.println("1685 :::" + forAddPoApi);
			if (!ServiceUtil.isEmpty(forAddPoApi)) {
				for (PoSearchApiDto obj : forAddPoApi) {
					PurchaseOrder setPurchaseOrder = new PurchaseOrder();
					setPurchaseOrder.setDocumentNumber(obj.getDocumentNumber());
					setPurchaseOrder.setDocumentCategory(obj.getDocumentCategory());
					purchaseOrder.add(setPurchaseOrder);
				}
			}

			System.err.println(purchaseOrder);
			System.out.println("Required PO:::" + dto.getRefpurchaseDoc());
			addPoApiInput.setPurchaseOrder(purchaseOrder);
			addPoApiInput.setInvoiceHeader(dto);
			AddPoOutputDto poApiResponse = savePo(addPoApiInput);
			dto = poApiResponse.getInvoiceObject();
		}
		System.out.println("AutoPostApi Response:::::" + dto);
		return dto;
	}

	@Override
	public AddPoOutputDto refreshPoApi(AddPoInputDto dto) throws URISyntaxException, IOException, ParseException {
		AddPoOutputDto poApiResponse = new AddPoOutputDto();
		if (!ServiceUtil.isEmpty(dto.getRequestId())) {
			ResponseEntity<?> response = invoiceHeaderServiceImpl.getInvoiceDetail(dto.getRequestId());
			if (!ServiceUtil.isEmpty(response.getBody())) {
				HashMap<String, String> po = new HashMap<String, String>();
				InvoiceHeaderDto header = (InvoiceHeaderDto) response.getBody();
				if(!ServiceUtil.isEmpty(header.getRefpurchaseDoc())){
					po.put(header.getRefpurchaseDoc(), header.getRefpurchaseDocCat());
				}
				for (InvoiceItemDto invoiceItems : header.getInvoiceItems()) {
					if(!ServiceUtil.isEmpty(invoiceItems.getRefDocNum())){
						po.put(invoiceItems.getRefDocNum().toString(), invoiceItems.getRefDocCat());
					}
					
				}
				AddPoInputDto addPoApiInput = new AddPoInputDto();
				List<PurchaseOrder> purchaseOrder = new ArrayList<>();
				for (String i : po.keySet()) {
					PurchaseOrder setPurchaseOrder = new PurchaseOrder();
					setPurchaseOrder.setDocumentNumber(i);
					if(ServiceUtil.isEmpty(po.get(i))){
						setPurchaseOrder.setDocumentCategory("F");
					}else{
						setPurchaseOrder.setDocumentCategory(po.get(i));
					}
					
					purchaseOrder.add(setPurchaseOrder);
				}
				addPoApiInput.setInvoiceHeader(header);
				addPoApiInput.setPurchaseOrder(purchaseOrder);
			    poApiResponse = savePo(addPoApiInput);
				
			}
		}

		return poApiResponse;
	}

}
