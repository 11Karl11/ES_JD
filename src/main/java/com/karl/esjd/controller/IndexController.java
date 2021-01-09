package com.karl.esjd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author karl xie
 * Created on 2021-01-09 13:31
 */
@Controller
public class IndexController {

    @GetMapping({"/","index"})
    public String index() {

        return "index";

    }
}