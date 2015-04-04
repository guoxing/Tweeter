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
    
    /**
     * Helper class that simply wraps together the fields from an HTTP request
     * that are necessary to route a request to a controller's method. The
     * httpMethod combined with the path make up what is referred to as an
     * address. This is then used by the route method in a switch statement
     * to take actions corresponding to the address.
     * @author marcelpuyat
     *
     */
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
