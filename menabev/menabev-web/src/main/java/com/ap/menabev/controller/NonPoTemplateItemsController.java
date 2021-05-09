package com.ap.menabev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.NonPoTemplateItemsDto;
import com.ap.menabev.service.NonPoTemplateItemsService;
import com.ap.menabev.dto.ResponseDto;

@RestController
@RequestMapping("/NonPoTemplateItems")
public class NonPoTemplateItemsController {
	 @Autowired
	    NonPoTemplateItemsService nonPoTemplateItemsService;
	    
	    @PostMapping("/save")
		public ResponseDto save(@RequestBody NonPoTemplateItemsDto dto) {
			return nonPoTemplateItemsService.saveNonPoTemplateItems(dto);
		}

	    
	    @PutMapping("/update")
		public ResponseDto update(@RequestBody NonPoTemplateItemsDto dto) {
			return nonPoTemplateItemsService.updateNonPoTemplateItems(dto);
		}
		@GetMapping("/getAll")
		public List<NonPoTemplateItemsDto> get() {
			return nonPoTemplateItemsService.get();
		}
		@DeleteMapping("/delete")
		public ResponseDto delete(@RequestBody NonPoTemplateItemsDto dto){
			return nonPoTemplateItemsService.delete(dto);
			
		}
}
