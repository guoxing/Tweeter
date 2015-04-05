package org.general.application;

import java.util.Map;

public interface ApplicationInterface {
    
    public ApplicationResponse respondToAction(ApplicationRequest action);
    
    public enum ApplicationResponseStatus {
        SUCCESS, INVALID_PARAMETERS, INVALID_DESTINATION
    }
    
    public static class ApplicationResponse {
        private ApplicationResponseStatus responseStatus;
        private String responseBody;
        public ApplicationResponse(String body, ApplicationResponseStatus result) {
            this.responseStatus = result;
            this.responseBody = body;
        }
        public ApplicationResponseStatus getResult() {
            return responseStatus;
        }
        public String getBody() {
            return responseBody;
        }
    }

    public static class ApplicationRequest {
        private String uniqueRequestAddress;
        private Map<String, String> params;
        
        public ApplicationRequest(String uniqueRequestAddress, Map<String, String> params) {
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
