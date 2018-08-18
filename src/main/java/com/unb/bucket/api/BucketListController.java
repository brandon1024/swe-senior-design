package com.unb.bucket.api;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BucketListController {

    @RequestMapping("/")
    public String home() {
        LOG.debug("hello world");
        return "Hello World!";
    }
}