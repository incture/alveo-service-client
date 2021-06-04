package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.SchedulerCycleDto;
import com.ap.menabev.dto.SchedulerCycleLogDto;
import com.ap.menabev.dto.SchedulerLogDto;
import com.ap.menabev.dto.SchedulerRunDto;
import com.ap.menabev.entity.SchedulerCycleDo;
import com.ap.menabev.entity.SchedulerCycleLogDo;
import com.ap.menabev.entity.SchedulerRunDo;
import com.ap.menabev.invoice.SchedulerCycleLogRepository;
import com.ap.menabev.invoice.SchedulerCycleRepository;
import com.ap.menabev.invoice.SchedulerRunRepository;
import com.ap.menabev.service.SchedulerLogService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;

@Service
public class SchedulerLogServiceImpl implements SchedulerLogService {

	@Autowired
	SchedulerRunRepository schedulerRunRepository;
	@Autowired
	SchedulerCycleRepository schedulerCycleRepository;
	@Autowired
	SchedulerCycleLogRepository schedulerCycleLogRepository;

	@Override
	public List<SchedulerLogDto> getAll() {
		ModelMapper modelMapper = new ModelMapper();
		List<SchedulerLogDto> returnDto = new ArrayList<>();
		List<SchedulerRunDo> schedulerRunDos = schedulerRunRepository.findAll();
		for (SchedulerRunDo rDo : schedulerRunDos) {
			List<SchedulerCycleDto> schedulerCycleDtos = new ArrayList<>();
			SchedulerLogDto sDto = new SchedulerLogDto();
			sDto.setSchedulerRunDto(modelMapper.map(rDo, SchedulerRunDto.class));
			List<SchedulerCycleDo> cycleDos = schedulerCycleRepository
					.getAllBySchedulerRunId(sDto.getSchedulerRunDto().getSchedulerRunID());
			for (SchedulerCycleDo cycleDo : cycleDos) {
				SchedulerCycleDto schedulerCycledto = modelMapper.map(cycleDo, SchedulerCycleDto.class);
				List<SchedulerCycleLogDo> dos = schedulerCycleLogRepository
						.findAllById(schedulerCycledto.getSchedulerCycleID());
				List<SchedulerCycleLogDto> cycleLogDtos = new ArrayList<>();
				for (SchedulerCycleLogDo cycleLogDo : dos) {
					SchedulerCycleLogDto cycleLogdto = modelMapper.map(cycleLogDo, SchedulerCycleLogDto.class);
					cycleLogDtos.add(cycleLogdto);
				}
				schedulerCycledto.setSchedulerCycleLogs(cycleLogDtos);
				schedulerCycleDtos.add(schedulerCycledto);
			}
			sDto.setSchedulerCycle(schedulerCycleDtos);
			returnDto.add(sDto);
		}
		return returnDto;
	}

	@Override
	public ResponseDto saveOrUpdate(SchedulerLogDto dto) {
		ModelMapper modelMapper = new ModelMapper();
		ResponseDto response = new ResponseDto();
		if (!ServiceUtil.isEmpty(dto)) {
			try {
				SchedulerRunDo schedulerRunDo = modelMapper.map(dto.getSchedulerRunDto(), SchedulerRunDo.class);
				for (SchedulerCycleDto cycleDto : dto.getSchedulerCycle()) {
					SchedulerCycleDo cycleDo = modelMapper.map(cycleDto, SchedulerCycleDo.class);
					for (SchedulerCycleLogDto cycleLogDto : cycleDto.getSchedulerCycleLogs()) {
						SchedulerCycleLogDo cycleLogDo = modelMapper.map(cycleLogDto, SchedulerCycleLogDo.class);
						if (ServiceUtil.isEmpty(cycleLogDto.getUuId())) {
							cycleLogDo.setUuId(UUID.randomUUID().toString());
						}
						schedulerCycleLogRepository.save(cycleLogDo);
					}
					schedulerCycleRepository.save(cycleDo);
				}
				schedulerRunRepository.save(schedulerRunDo);
				response.setCode(ApplicationConstants.CODE_SUCCESS);
				response.setMessage(ApplicationConstants.SUCCESS);
				response.setStatus(
						dto.getSchedulerRunDto().getSchedulerName() + " " + ApplicationConstants.CREATED_SUCCESS);
			} catch (Exception e) {
				response.setCode(ApplicationConstants.CODE_FAILURE);
				response.setMessage(ApplicationConstants.FAILURE);
				response.setStatus("Saved Failed Due to " + e.getMessage());
			}
		}
		return response;
	}

}
