package com.karl.esjd.controller;

import com.karl.esjd.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author karl xie
 * Created on 2021-01-09 14:52
 */
@RestController
public class ContentController {


    @Autowired
    private ContentService contentService;

    @GetMapping("/parse/{keywords}")
    public Boolean parse(@PathVariable String keywords) throws Exception {
        return contentService.parseContent(keywords);
    }


    @GetMapping("/search/{keywords}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> search(@PathVariable String keywords,
                                           @PathVariable int pageNo,
                                           @PathVariable int pageSize) throws Exception {
        return contentService.searchPageHigh(keywords,pageNo,pageSize);
    }


}