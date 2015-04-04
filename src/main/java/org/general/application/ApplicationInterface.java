package org.general.application;

import java.util.Map;

public interface ApplicationInterface {
    
    public ApplicationDatagram respondToAction(ApplicationAction action);
    
    public enum ApplicationResult {
        SUCCESS, INVALID_PARAMETERS, INVALID_PATH
    }
    
    public static class ApplicationDatagram {
        private ApplicationResult result;
        private String body;
        public ApplicationDatagram(String body, ApplicationResult result) {
            this.result = result;
            this.body = body;
        }
        public ApplicationResult getResult() {
            return result;
        }
        public String getBody() {
            return body;
        }
    }

    public static class ApplicationAction {
        private String httpMethod;
        private String path;
        private Map<String, String> params;
        
        public ApplicationAction(String httpMethod, String path, Map<String, String> params) {
            this.httpMethod = httpMethod;
            this.path = path;
            this.params = params;
        }
        
        public Map<String, String> getParams() {
            return this.params;
        }
        
        public String getAddress() {
            return httpMethod + " " + path;
        }
    }
}
