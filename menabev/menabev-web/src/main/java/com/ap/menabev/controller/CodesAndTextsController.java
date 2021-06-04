package com.ap.menabev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.CodesAndTextsDto;
import com.ap.menabev.service.CodesAndTextsService;

@RestController
@RequestMapping("/codesAndtexts")
public class CodesAndTextsController {
	
	@Autowired
	CodesAndTextsService codesAndTextsService;
	
	@PostMapping("/saveOrUpdate")
	ResponseDto saveOrUpdate(@RequestBody CodesAndTextsDto dto){
		return codesAndTextsService.saveOrUpdate(dto);
	}
	
	@GetMapping("/get/uuId={uuId}/statusCode={statusCode}/type={type}/language={language}")
	List<CodesAndTextsDto> get(@PathVariable(name = "uuId", required = false) String uuId,@PathVariable(name = "statusCode" ,required = false) String statusCode,@PathVariable(name ="type" , required = false) String type,@PathVariable(name = "language", required = false) String language){
		return codesAndTextsService.get(uuId, statusCode, type, language);
	}

}
