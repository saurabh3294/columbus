package com.proptiger.data.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.pojo.response.APIResponse;

@Controller
@RequestMapping("data/v1/log")
public class LogController {
    private static Logger logger = LoggerFactory.getLogger(LogController.class);

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public APIResponse logMessages(@RequestParam(defaultValue="INFO") String logLevel, @RequestBody Object object) {
        if (object == null || object.toString().equals("")) {
            return new APIResponse(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.LOG_MESSAGE_ERROR);
        }
        switch (logLevel) {
            case "DEBUG":
                logger.debug(object.toString());
                break;
            case "INFO":
                logger.info(object.toString());
                break;
            case "WARN":
                logger.warn(object.toString());
                break;
            case "ERROR":
                logger.error(object.toString());
                break;
            default :
                return new APIResponse(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.LOG_MESSAGE_ERROR);
        }
        return new APIResponse(object);
    }
}
