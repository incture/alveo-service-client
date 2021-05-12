package com.ap.menabev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.AllocationDto;
import com.ap.menabev.dto.NonPoTemplateHIDto;
import com.ap.menabev.dto.NonPoTemplateItemsDto;
import com.ap.menabev.service.NonPoTemplateService;
import com.ap.menabev.dto.ResponseDto;

@RestController
@RequestMapping("/NonPoTemplate")
public class NonPoTemplateController {
    @Autowired
    NonPoTemplateService nonPoTemplateService;
    
    @PostMapping("/save")
	public ResponseDto save(@RequestBody NonPoTemplateHIDto dto) {
		return nonPoTemplateService.saveNonPoTemplate(dto);
	}
    
    @PutMapping("/update")
    public ResponseDto update(@RequestBody NonPoTemplateHIDto dto){
    	return nonPoTemplateService.updateNonPoTemplateHI(dto);
    }

//    @GetMapping("/getTemplate/{templateId}")
//    public NonPoTemplateHIDto
    
	@GetMapping("/getAll")
	public List<NonPoTemplateHIDto> get() {
		return nonPoTemplateService.get();
	}
	@GetMapping("/getAll/{limit}/{offset}")
	public List<NonPoTemplateHIDto> get(@PathVariable int limit,@PathVariable int offset) {
		return nonPoTemplateService.get(limit,offset);
	}
	@DeleteMapping("/delete")
	public ResponseDto delete(@RequestBody List<String> templateId ){
		return nonPoTemplateService.delete(templateId);
		
	}
	@GetMapping("/getItemsByTemplateId/{templateId}")
	public List<NonPoTemplateItemsDto> getItems(@PathVariable String templateId){
		return nonPoTemplateService.getNonPoTemplateItems(templateId);
	}
	
	@GetMapping("/selectNonPoTemplate")
	public List<AllocationDto> selectNonPoTemplate(){
		return nonPoTemplateService.selectNonPoTemplate();
	}
    
}
