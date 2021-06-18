package com.ap.menabev.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.APMailBoxDto;
import com.ap.menabev.dto.APScanningTeamDto;
import com.ap.menabev.dto.ConfigurationCockpitDto;
import com.ap.menabev.dto.ConfigurationDto;
import com.ap.menabev.dto.EmailTeamAPDto;
import com.ap.menabev.dto.MailTemplateDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.SchedulerConfigurationDto;
import com.ap.menabev.dto.VendorDetailsDto;
import com.ap.menabev.entity.ConfigurationDo;
import com.ap.menabev.entity.EmailTeamAPDo;
import com.ap.menabev.entity.MailTemplateDo;
import com.ap.menabev.entity.SchedulerConfigurationDo;
import com.ap.menabev.entity.VendorDetailsDo;
import com.ap.menabev.invoice.ConfigurationRepository;
import com.ap.menabev.invoice.EmailTeamApRepository;
import com.ap.menabev.invoice.MailTemplateRepository;
import com.ap.menabev.invoice.SchedulerConfigurationRepository;
import com.ap.menabev.invoice.VendorDetailsRepository;
import com.ap.menabev.service.ConfigurationCockpitService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;

@Service
public class ConfigurationCockpitServiceImpl implements ConfigurationCockpitService {
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationCockpitServiceImpl.class);

	@Autowired
	VendorDetailsRepository vendorDetailsRepository;

	@Autowired
	EmailTeamApRepository emailteamRepository;

	@Autowired
	MailTemplateRepository mailtemplateRepository;

	@Autowired
	ConfigurationRepository configurationRepository;

	@Autowired
	SchedulerConfigurationRepository schedulerconfigurationRepository;

	@Autowired
	EmailScheduler emailScheduler;

	public ResponseDto save(ConfigurationCockpitDto dto) {
		ResponseDto response = new ResponseDto();
		try {

			ModelMapper mapper = new ModelMapper();

			ConfigurationDo currentConfig = configurationRepository.getVersion("CURRENT");
			if (!ServiceUtil.isEmpty(currentConfig)) {
				String version = "V";
				String vSeq = configurationRepository.getVersionNum();
				version = version + vSeq;
				currentConfig.setVersion(version);
				currentConfig.setUpdatedAt(ServiceUtil.getEpocTime());
				configurationRepository.save(currentConfig);

			}
			String configId = UUID.randomUUID().toString();
			ConfigurationDto config = dto.getConfigurationDto();
			config.setConfigurationId(configId);
			config.setVersion("CURRENT");
			config.setCreatedAt(ServiceUtil.getEpocTime());
			configurationRepository.save(mapper.map(config, ConfigurationDo.class));

			List<VendorDetailsDto> vendorDetailsDtoList = dto.getVendorDetailsDto();

			for (VendorDetailsDto vendorDetailsDto : vendorDetailsDtoList) {
				vendorDetailsDto.setVendorId(UUID.randomUUID().toString());
				vendorDetailsDto.setConfigurationId(configId);
				vendorDetailsDto.setCreatedAt(ServiceUtil.getEpocTime());
				vendorDetailsRepository.save(mapper.map(vendorDetailsDto, VendorDetailsDo.class));

			}

			List<MailTemplateDto> mailTemplateDtoList = dto.getMailTemplateDto();

			// List<MailTemplateDo> mailTemplateDoList = new ArrayList<>();

			for (MailTemplateDto mailTemplateDto : mailTemplateDtoList) {
				mailTemplateDto.setMailTemplateId(UUID.randomUUID().toString());
				mailTemplateDto.setConfigurationId(configId);
				mailtemplateRepository.save(mapper.map(mailTemplateDto, MailTemplateDo.class));
			}
			// -----------------------------------------------------------------------------------/////////
			// List<EmailTeamAPDto> emailTeamApDtoList = dto.getEmailTeamDto();
			// for (EmailTeamAPDto emailTeamAPDto : emailTeamApDtoList) {
			// EmailTeamAPDto emaildto = emailTeamAPDto;
			// // dto.getEmailTeamDto();
			// List<String> emailIds = emaildto.getEmailId();
			// List<EmailTeamAPDo> emailDoList = new ArrayList<>();
			// for (String emailId : emailIds) {
			// EmailTeamAPDo emailteamDo = new EmailTeamAPDo();
			// emailteamDo.setActionType(emaildto.getActionType());
			// emailteamDo.setConfigurationId(configId);
			// emailteamDo.setEmailId(emailId);
			// // emailteamDo.setEmailTeamApid("3");
			// emailteamDo.setEmailTeamApid(UUID.randomUUID().toString());
			// emailteamDo.setIsActive(emaildto.getIsActive());
			// emailteamRepository.save(emailteamDo);
			// }
			//
			// }
			List<EmailTeamAPDo> entityList = new ArrayList<>();
			List<APMailBoxDto> apMailBoxList = dto.getAccountsPayableMailbox();
			if (!ServiceUtil.isEmpty(apMailBoxList)) {
				for (APMailBoxDto apMailBoxDto : apMailBoxList) {
					EmailTeamAPDo emailteamDo = new EmailTeamAPDo();
					emailteamDo.setActionType("APMailBox");
					emailteamDo.setConfigurationId(configId);
					emailteamDo.setEmailId(apMailBoxDto.getEmailId());
					emailteamDo.setEmailTeamApid(UUID.randomUUID().toString());
					emailteamDo.setIsActive(apMailBoxDto.getIsActive());
					entityList.add(emailteamDo);

				}
			}
			List<APScanningTeamDto> apScanningTeamList = dto.getAccountsPayableScanningTeam();
			if (!ServiceUtil.isEmpty(apScanningTeamList)) {
				for (APScanningTeamDto apScanningTeam : apScanningTeamList) {
					EmailTeamAPDo emailteamDo = new EmailTeamAPDo();
					emailteamDo.setActionType("APScanningTeam");
					emailteamDo.setConfigurationId(configId);
					emailteamDo.setEmailId(apScanningTeam.getEmailId());
					emailteamDo.setEmailTeamApid(UUID.randomUUID().toString());
					emailteamDo.setIsActive(apScanningTeam.getIsActive());
					entityList.add(emailteamDo);
				}
			}
			if (!ServiceUtil.isEmpty(entityList))
				emailteamRepository.saveAll(entityList);

			// -----------------------------------------------------------------------------------/////////
			List<SchedulerConfigurationDto> schedulerConfigurationDtoList = dto.getSchedulerConfigurationdto();

			// List<SchedulerConfigurationDo> schedulerConfigurationDoList = new
			// ArrayList<>();

			for (SchedulerConfigurationDto schedulerConfigurationDto : schedulerConfigurationDtoList) {
				boolean isEmailScheduler = !ServiceUtil.isEmpty(schedulerConfigurationDto.getActionType())
						&& "Email Scheduler Configuration".equalsIgnoreCase(schedulerConfigurationDto.getActionType());
				boolean isOcrScheduler = false;
				boolean isGRNScheduler = false;
				boolean conditionIsActive = !ServiceUtil.isEmpty(schedulerConfigurationDto.getIsActive())
						&& schedulerConfigurationDto.getIsActive();
				if (isEmailScheduler) {// if email sch
					SchedulerConfigurationDo entity = null;
						
						if(ServiceUtil.isEmpty(schedulerConfigurationDto.getScId())){
							//new sch rec
							schedulerConfigurationDto.setScId(UUID.randomUUID().toString());
							schedulerConfigurationDto.setConfigurationId(configId);
							entity = schedulerconfigurationRepository
									.save(mapper.map(schedulerConfigurationDto, SchedulerConfigurationDo.class));
						}else{
							// update the record with new fields
							schedulerConfigurationDto.setConfigurationId(configId);
							entity = schedulerconfigurationRepository
									.save(mapper.map(schedulerConfigurationDto, SchedulerConfigurationDo.class));
						}
					
					//then anyways start/stop the scheduler  for email
						schedulerconfigurationRepository.resetFlagIsActive(configId,entity.getScId());
					// TODO
					logger.error("Inside Email Scheduler Configuration" + entity);
					Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(entity.getStartDate());
					Long period = null;
					if ("min".equalsIgnoreCase(entity.getFrequencyUnit())) {
						Integer milliSecond = entity.getFrequencyNumber() * 60000;
						if (!ServiceUtil.isEmpty(milliSecond) && milliSecond != 0) {

							period = Long.valueOf(milliSecond);
							logger.error("Inside Email Scheduler Configuration period:::" + period);
						}
					}
					logger.error("Inside Email Scheduler Configuration period:::is empty "
							+ ServiceUtil.isEmpty(startDate) + " is empty period" + ServiceUtil.isEmpty(period));
					if (!ServiceUtil.isEmpty(startDate) && !ServiceUtil.isEmpty(period))
						emailScheduler.reSchedule(startDate, period, entity);
					
					
					
				} else if (isOcrScheduler) {
					if (conditionIsActive) {

					} else {

					}
				} else if (isGRNScheduler) {
					if (conditionIsActive) {

					} else {

					}
				}
//
//				schedulerConfigurationDto.setScId(UUID.randomUUID().toString());
//				schedulerConfigurationDto.setConfigurationId(configId);
//				SchedulerConfigurationDo entity = schedulerconfigurationRepository
//						.save(mapper.map(schedulerConfigurationDto, SchedulerConfigurationDo.class));
//				logger.error("Befor  Email Scheduler Configuration entity" + entity);
//				if (!ServiceUtil.isEmpty(schedulerConfigurationDto.getActionType()) && "Email Scheduler Configuration"
//						.equalsIgnoreCase(schedulerConfigurationDto.getActionType())) {
//					// TODO
//					logger.error("Inside Email Scheduler Configuration" + entity);
//					Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(entity.getStartDate());
//					Long period = null;
//					if ("min".equalsIgnoreCase(entity.getFrequencyUnit())) {
//						Integer milliSecond = entity.getFrequencyNumber() * 60000;
//						if (!ServiceUtil.isEmpty(milliSecond) && milliSecond != 0) {
//
//							period = Long.valueOf(milliSecond);
//							logger.error("Inside Email Scheduler Configuration period:::" + period);
//						}
//					}
//					logger.error("Inside Email Scheduler Configuration period:::is empty "
//							+ ServiceUtil.isEmpty(startDate) + " is empty period" + ServiceUtil.isEmpty(period));
//					if (!ServiceUtil.isEmpty(startDate) && !ServiceUtil.isEmpty(period))
//						emailScheduler.reSchedule(startDate, period, entity);
//
//				}
//				if (!ServiceUtil.isEmpty(schedulerConfigurationDto.getActionType()) && "Email Scheduler Configuration"
//						.equalsIgnoreCase(schedulerConfigurationDto.getActionType())) {
//
//				}

			}

			
			
			
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(ApplicationConstants.UPDATE_SUCCESS);
			return response;
		} catch (Exception e) {
			System.out.println("error :" + e);
			logger.error(e.getMessage());
			// System.out.println(e);
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.CREATE_FAILURE);
			return response;
		}

	}

	/*
	 * 
	 * @Override public ConfigurationCockpitDto get() {
	 * 
	 * 
	 * ConfigurationCockpitDto configurationCockpitDto=new
	 * ConfigurationCockpitDto();
	 * 
	 * ModelMapper modelMapper = new ModelMapper(); try {
	 * 
	 * List<VendorDetailsDo> vendors =vendorDetailsRepository.findAll(); for
	 * (VendorDetailsDto vendorDetailsDto : vendors) {
	 * configurationCockpitDto.setVendorDetailsDto(mapper.map(vendorDetailsDto,
	 * VendorDetailsDo.class));
	 * 
	 * //(List<VendorDetailsDto>)
	 * (modelMapper.map(vendorDetailsDo,VendorDetailsDo.class))); }
	 * 
	 * }
	 * 
	 * } } catch (Exception e) { System.err.println("Exception :" +
	 * e.getMessage()); }
	 * 
	 * 
	 * 
	 * return configurationCockpitDto;
	 * 
	 * }
	 * 
	 * 
	 */

	@Override
	public ResponseDto delete(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigurationCockpitDto get(String version) {
		ConfigurationCockpitDto configCockpitDto = new ConfigurationCockpitDto();

		ModelMapper mapper = new ModelMapper();
		try {
			ConfigurationDo configDo = configurationRepository.getVersion(version);
			configCockpitDto.setConfigurationDto(mapper.map(configDo, ConfigurationDto.class));
			String configId = configDo.getConfigurationId();
			List<SchedulerConfigurationDo> schedulerConfigurationDoList = schedulerconfigurationRepository
					.getSchedulerConfiguration(configId);
			List<SchedulerConfigurationDto> schedulerConfigurationDtoList = new ArrayList<SchedulerConfigurationDto>();
			for (SchedulerConfigurationDo sDo : schedulerConfigurationDoList) {
				SchedulerConfigurationDto sDto = mapper.map(sDo, SchedulerConfigurationDto.class);
				schedulerConfigurationDtoList.add(sDto);
			}
			configCockpitDto.setSchedulerConfigurationdto(schedulerConfigurationDtoList);

			List<VendorDetailsDo> vDoList = vendorDetailsRepository.getVendorDetails(configId);
			List<VendorDetailsDto> vDtoList = new ArrayList<VendorDetailsDto>();
			for (VendorDetailsDo vDo : vDoList) {
				VendorDetailsDto vDto = mapper.map(vDo, VendorDetailsDto.class);
				vDtoList.add(vDto);
			}

			configCockpitDto.setVendorDetailsDto(vDtoList);

			List<MailTemplateDo> mDoList = mailtemplateRepository.getMailTemplate(configId);
			List<MailTemplateDto> mDtoList = new ArrayList<MailTemplateDto>();
			for (MailTemplateDo mDo : mDoList) {
				MailTemplateDto mDto = mapper.map(mDo, MailTemplateDto.class);
				mDtoList.add(mDto);
			}
			configCockpitDto.setMailTemplateDto(mDtoList);

			List<APMailBoxDto> accountsPayableMailbox = new ArrayList<>();
			List<APScanningTeamDto> accountsPayableScanningTeam = new ArrayList<>();

			List<EmailTeamAPDto> emailDtoList = new ArrayList<EmailTeamAPDto>();
			List<EmailTeamAPDo> emailTeamDoList = emailteamRepository.getEmailTeamAP(configId,
					ApplicationConstants.APMAILBOX, ApplicationConstants.APSCANNINGTEAM);
			logger.error("emailTeamDoList:" + emailTeamDoList.size());
			for (EmailTeamAPDo emailTeamAPDo : emailTeamDoList) {
				logger.error("emailTeamAPDo:" + emailTeamAPDo.toString());
				if (ApplicationConstants.APMAILBOX.equalsIgnoreCase(emailTeamAPDo.getActionType())) {
					APMailBoxDto dto = new APMailBoxDto();
					dto.setConfigurationId(emailTeamAPDo.getConfigurationId());
					dto.setEmailId(emailTeamAPDo.getEmailId());
					dto.setEmailTeamApid(emailTeamAPDo.getEmailTeamApid());
					accountsPayableMailbox.add(dto);
				} else if (ApplicationConstants.APSCANNINGTEAM.equalsIgnoreCase(emailTeamAPDo.getActionType())) {
					APScanningTeamDto dto = new APScanningTeamDto();
					dto.setConfigurationId(emailTeamAPDo.getConfigurationId());
					dto.setEmailId(emailTeamAPDo.getEmailId());
					dto.setEmailTeamApid(emailTeamAPDo.getEmailTeamApid());
					accountsPayableScanningTeam.add(dto);
				}
			}

			configCockpitDto.setAccountsPayableMailbox(accountsPayableMailbox);
			configCockpitDto.setAccountsPayableScanningTeam(accountsPayableScanningTeam);
			// EmailTeamAPDto mailDto = mapper.map(mailDoList.get(0),
			// EmailTeamAPDto.class);
			// EmailTeamAPDto scanDto = mapper.map(scanDoList.get(0),
			// EmailTeamAPDto.class);
			//
			// logger.error("MailDto start " + mailDto);
			// logger.error("scanDto start " + scanDto);
			//
			// List<String> emailIds = new ArrayList<String>();
			// for (EmailTeamAPDo mailDo : mailDoList) {
			// emailIds.add(mailDo.getEmailId());
			// }
			//
			// logger.error("Mail emailId " + emailIds);
			//
			// mailDto.setEmailId(emailIds);
			//
			// emailDtoList.add(mailDto);
			// logger.error("emailDtoList " + emailDtoList);
			// mailDto = null;
			// emailIds = new ArrayList<String>();
			//
			// for (EmailTeamAPDo scanDo : scanDoList) {
			// emailIds.add(scanDo.getEmailId());
			// }
			//
			// logger.error("Mail emailId " + emailIds);
			//
			// scanDto.setEmailId(emailIds);
			//
			// emailDtoList.add(scanDto);
			//
			// logger.error("emailDtoList " + emailDtoList);
			// configCockpitDto.setEmailTeamDto(emailDtoList);

		} catch (Exception e) {
			System.err.println("Exception :" + e.getMessage());
		}
		return configCockpitDto;
	}

	@Override
	public List<String> getDistinctVersions() {
		// TODO Auto-generated method stub

		List<String> versions = configurationRepository.getVersion();
		return versions;
	}

}
