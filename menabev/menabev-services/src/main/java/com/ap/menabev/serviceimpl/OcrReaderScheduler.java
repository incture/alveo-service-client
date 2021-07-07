package com.ap.menabev.serviceimpl;
import java.util.Date;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;

import com.ap.menabev.dto.SchedulerLogDto;
import com.ap.menabev.dto.SchedulerResponseDto;
import com.ap.menabev.entity.SchedulerConfigurationDo;
import com.ap.menabev.entity.SchedulerRunDo;
import com.ap.menabev.invoice.SchedulerConfigurationRepository;
import com.ap.menabev.invoice.SchedulerRunRepository;
import com.ap.menabev.service.AutomationService;
import com.ap.menabev.service.SchedulerLogService;
import com.ap.menabev.util.ServiceUtil;
public class OcrReaderScheduler implements Runnable {
	@Autowired
	SchedulerRunRepository schedulerRunRepository;

	private static final Logger logger = LoggerFactory.getLogger(EmailScheduler.class);
	@Autowired
	SchedulerConfigurationRepository schedulerConfigurationRepository;
	@Autowired
	AutomationService automationService;
	@Autowired
	SchedulerLogService schedulerLogService;

	@Override
	public void run() {
		logger.error("Dynamic run :::" + System.currentTimeMillis());
		// after db call if the current time is more than end date stop the
		// scheduler.

		SchedulerConfigurationDo entity = schedulerConfigurationRepository.getSchedulerData(
				"Email Scheduler Configuration", ServiceUtil.getFormattedDateinString("yyyy-MM-dd"));
		logger.error("SchedulerConfigurationDo entity" + entity);
		if (!ServiceUtil.isEmpty(entity)) {
			automationService.extractInvoiceFromSharedEmailBoxInScheduler(entity);
		} else {
			// complete the sch run
			this.scheduledFuture.cancel(true);
		}

		
	}

	@SuppressWarnings("rawtypes")
	ScheduledFuture scheduledFuture;

	TaskScheduler taskScheduler;

	// this method will kill previous scheduler if exists and will create a new
	// scheduler with given cron expression
	public void reSchedule(Date startTime, long period, SchedulerConfigurationDo entity) {

		SchedulerRunDo schedulerRunDo = schedulerRunRepository.getBySchedulerConfigID(entity.getScId());

		if (ServiceUtil.isEmpty(schedulerRunDo)) {
			schedulerRunDo = new SchedulerRunDo();
			schedulerRunDo.setSchedulerRunID(UUID.randomUUID().toString());
		}

		if (taskScheduler == null) {
			this.taskScheduler = new ConcurrentTaskScheduler();
		}
		if (this.scheduledFuture != null) {
			this.scheduledFuture.cancel(true);
			// complete the sch run

			schedulerRunDo.setDatetimeSwitchedOFF(ServiceUtil.getFormattedDateinString("yyyy-MM-dd hh:mm:ss"));
			schedulerRunDo.setSwichtedOFFby(entity.getCreatedBy());

			logger.error("Scheduler Stopped");
		}
		if (entity.getIsActive()) {
			schedulerRunDo.setDatetimeSwitchedON(ServiceUtil.getFormattedDateinString("yyyy-MM-dd hh:mm:ss"));
			schedulerRunDo.setSwichtedONby(entity.getCreatedBy());
			logger.error("Scheduler Started at:::" + startTime);
			this.scheduledFuture = this.taskScheduler.scheduleAtFixedRate(this, startTime, period);

		}
		schedulerRunDo.setSchedulerConfigID(entity.getScId());
		schedulerRunDo.setSchedulerName("Email Reader");
		SchedulerRunDo schedulerRunEntity = schedulerRunRepository.save(schedulerRunDo);

	}

}

