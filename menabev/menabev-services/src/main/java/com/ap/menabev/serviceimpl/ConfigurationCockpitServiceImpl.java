package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
				configurationRepository.save(currentConfig);

			}
			String configId = UUID.randomUUID().toString();
			ConfigurationDto config = dto.getConfigurationDto();
			config.setConfigurationId(configId);
			config.setVersion("CURRENT");

			configurationRepository.save(mapper.map(config, ConfigurationDo.class));

			List<VendorDetailsDto> vendorDetailsDtoList = dto.getVendorDetailsDto();

			for (VendorDetailsDto vendorDetailsDto : vendorDetailsDtoList) {
				vendorDetailsDto.setVendorId(UUID.randomUUID().toString());
				vendorDetailsDto.setConfigurationId(configId);
				vendorDetailsRepository.save(mapper.map(vendorDetailsDto, VendorDetailsDo.class));

			}

			List<MailTemplateDto> mailTemplateDtoList = dto.getMailTemplateDto();

			List<MailTemplateDo> mailTemplateDoList = new ArrayList<>();

			for (MailTemplateDto mailTemplateDto : mailTemplateDtoList) {
				mailTemplateDto.setMailTemplateId(UUID.randomUUID().toString());
				mailTemplateDto.setConfigurationId(configId);
				mailtemplateRepository.save(mapper.map(mailTemplateDto, MailTemplateDo.class));
			}

			List<EmailTeamAPDto> emailTeamApDtoList = dto.getEmailTeamDto();

			// my
			for (EmailTeamAPDto emailTeamAPDto : emailTeamApDtoList) {
				EmailTeamAPDto emaildto = emailTeamAPDto;
				// dto.getEmailTeamDto();

				List<String> emailIds = emaildto.getEmailId();
				List<EmailTeamAPDo> emailDoList = new ArrayList<>();

				for (String emailId : emailIds) {
					EmailTeamAPDo emailteamDo = new EmailTeamAPDo();

					emailteamDo.setActionType(emaildto.getActionType());
					emailteamDo.setConfigurationId(configId);
					emailteamDo.setEmailId(emailId);
					// emailteamDo.setEmailTeamApid("3");
					emailteamDo.setEmailTeamApid(UUID.randomUUID().toString());
					emailteamDo.setIsActive(emaildto.getIsActive());

					emailteamRepository.save(emailteamDo);
				}

			}

			List<SchedulerConfigurationDto> schedulerConfigurationDtoList = dto.getSchedulerConfigurationdto();

			List<SchedulerConfigurationDo> schedulerConfigurationDoList = new ArrayList<>();

			for (SchedulerConfigurationDto schedulerConfigurationDto : schedulerConfigurationDtoList) {
				// schedulerConfigurationDto.setId("4");;
				schedulerConfigurationDto.setScId(UUID.randomUUID().toString());
				schedulerConfigurationDto.setConfigurationId(configId);
				schedulerconfigurationRepository
						.save(mapper.map(schedulerConfigurationDto, SchedulerConfigurationDo.class));
			}

			// SchedulerConfigurationDto
			// scheduler=dto.getSchedulerConfigurationdto();

			// emailteamRepository.save(mapper.map(emaildto,
			// EmailTeamAPDo.class));

			// schedulerconfigurationRepository.save(mapper.map(dto,
			// SchedulerConfigurationDo.class));

			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(ApplicationConstants.CREATED_SUCCESS);
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

			List<EmailTeamAPDto> emailDtoList = new ArrayList<EmailTeamAPDto>();
			List<EmailTeamAPDo> mailDoList = emailteamRepository.getEmailTeamAP(configId,
					"Accounts Payablle Mailbox Id");
			List<EmailTeamAPDo> scanDoList = emailteamRepository.getEmailTeamAP(configId,
					"Accounts Payable Scanning Team");

			EmailTeamAPDto mailDto = mapper.map(mailDoList.get(0), EmailTeamAPDto.class);
			EmailTeamAPDto scanDto = mapper.map(scanDoList.get(0), EmailTeamAPDto.class);

			logger.error("MailDto start " + mailDto);
			logger.error("scanDto start " + scanDto);

			List<String> emailIds = new ArrayList<String>();
			for (EmailTeamAPDo mailDo : mailDoList) {
				emailIds.add(mailDo.getEmailId());
			}

			logger.error("Mail emailId " + emailIds);

			mailDto.setEmailId(emailIds);

			emailDtoList.add(mailDto);
			logger.error("emailDtoList " + emailDtoList);
			mailDto = null;
			emailIds = new ArrayList<String>();

			for (EmailTeamAPDo scanDo : scanDoList) {
				emailIds.add(scanDo.getEmailId());
			}

			logger.error("Mail emailId " + emailIds);

			scanDto.setEmailId(emailIds);

			emailDtoList.add(scanDto);

			logger.error("emailDtoList " + emailDtoList);
			configCockpitDto.setEmailTeamDto(emailDtoList);

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