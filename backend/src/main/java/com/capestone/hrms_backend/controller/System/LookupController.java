package com.capestone.hrms_backend.controller.System;

import com.capestone.hrms_backend.entity.travel.DocType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lookups")
public class LookupController {
    @GetMapping("/document-types")
    public List<String> getDocumentTypes(){
        return Arrays.stream(DocType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
