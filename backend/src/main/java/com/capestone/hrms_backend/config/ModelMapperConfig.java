package com.capestone.hrms_backend.config;

import com.capestone.hrms_backend.dto.response.TravelDocumentResponseDto;
import com.capestone.hrms_backend.dto.response.TravelPlanResponseDto;
import com.capestone.hrms_backend.entity.travel.TravelDocument;
import com.capestone.hrms_backend.entity.travel.TravelPlan;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        //HR id from:: TravelPlan to TravelResponseDto
        modelMapper.typeMap(TravelPlan.class, TravelPlanResponseDto.class)
                .addMappings(m-> {
                    m.map(plan -> plan.getCreatedBy().getId(), TravelPlanResponseDto::setCreatedBy);
                    m.map(TravelPlan::getDepatureDate,TravelPlanResponseDto::setDepartureDate);
                });
        return modelMapper;
    }
}
