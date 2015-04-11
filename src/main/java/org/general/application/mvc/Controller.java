package org.general.application.mvc;

import java.security.InvalidParameterException;
import java.util.Map;

import org.general.application.ApplicationInterface.AppResponse;
import org.general.application.ApplicationInterface.AppResponseStatus;

public abstract class Controller {
    
    public static AppResponse generateSuccessResponse(String body) {
        return new AppResponse(body, AppResponseStatus.SUCCESS);
    }
    
    public static AppResponse generateInvalidParamResponse(String message) {
        return new AppResponse(message, AppResponseStatus.INVALID_PARAMETERS);
    }
    
    public static AppResponse generateInternalErrorResponse() {
        return new AppResponse("Internal error.", AppResponseStatus.INTERNAL_ERROR);
    }
    
    public static String getRequiredStringParam(String paramKey, Map<String, String> params)
            throws InvalidParameterException {
        String val = params.get(paramKey);
        if (val == null) {
            throw new InvalidParameterException(paramKey + " is a required parameter");
        }
        return val;
    }
    
    public static Long getRequiredLongParam(String paramKey, Map<String, String> params)
            throws InvalidParameterException {
        
        if (params.get(paramKey) == null) {
            throw new InvalidParameterException(paramKey + " is a required parameter");
        }
        
        Long val = null;
        try {
            val = Long.parseLong(params.get(paramKey));
        } catch (NumberFormatException e) {
            throw new InvalidParameterException(paramKey + " must be a 64-bit integer");
        }
        
        return val;
    }
    
    public static Long getOptionalLongParam(String paramKey, Map<String, String> params)
            throws InvalidParameterException {
        Long val = null;
        try {
            val = Long.parseLong(params.get(paramKey));
        } catch (NumberFormatException e) {
            throw new InvalidParameterException(paramKey + " must be a 64-bit integer");
        }
        
        return val;
    }
}
