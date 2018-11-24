package com.unb.beforeigo.api;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/")
@Slf4j
public class BeforeIGoController {

    /**
     * Endpoint used to test that the server is live.
     *
     * @return The String "Success".
     * */
    @ApiOperation(value = "Endpoint used to test that the server is live.", response = String.class)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String createBucket() {
        return "Success";
    }

    /**
     * Alternate endpoint for swagger API documentation.
     * */
    @ApiOperation(value = "Alternate endpoint for swagger API documentation.")
    @RequestMapping(value = "/api-docs", method = RequestMethod.GET)
    public void apiDocumentation(final HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }
}
