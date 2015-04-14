package org.general.application;

import java.util.Map;

public interface ApplicationInterface {
    
    public AppResponse respondToAction(AppRequest action);
    
    public enum AppResponseStatus {
        SUCCESS, INVALID_PARAMETERS, INVALID_DESTINATION, INTERNAL_ERROR
    }
    
    public static class AppResponse {
        private AppResponseStatus responseStatus;
        private String responseBody;
        public AppResponse(String body, AppResponseStatus responseStatus) {
            this.responseStatus = responseStatus;
            this.responseBody = body;
        }
        public AppResponseStatus getResponseStatus() {
            return responseStatus;
        }
        public String getBody() {
            return responseBody;
        }
    }

    public static class AppRequest {
        private String requestAddress;
        private Map<String, String> params;
        
        public AppRequest(String requestAddress, Map<String, String> params) {
            this.requestAddress = requestAddress;
            this.params = params;
        }
        
        public Map<String, String> getParams() {
            return this.params;
        }
        
        public String getAddress() {
            return requestAddress;
        }
    }
}
