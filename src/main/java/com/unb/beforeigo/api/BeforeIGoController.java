package com.unb.beforeigo.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Slf4j
public class BeforeIGoController {

    /**
     * Endpoint used to test that the server is live.
     *
     * @return the string "Success"
     * */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String createBucket() {
        return "Success";
    }
}
