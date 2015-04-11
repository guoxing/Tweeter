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
        public AppResponse(String body, AppResponseStatus result) {
            this.responseStatus = result;
            this.responseBody = body;
        }
        public AppResponseStatus getResult() {
            return responseStatus;
        }
        public String getBody() {
            return responseBody;
        }
    }

    public static class AppRequest {
        private String uniqueRequestAddress;
        private Map<String, String> params;
        
        public AppRequest(String uniqueRequestAddress, Map<String, String> params) {
            this.uniqueRequestAddress = uniqueRequestAddress;
            this.params = params;
        }
        
        public Map<String, String> getParams() {
            return this.params;
        }
        
        public String getAddress() {
            return uniqueRequestAddress;
        }
    }
}
