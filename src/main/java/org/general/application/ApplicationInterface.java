package org.general.application;

import java.util.Map;

/**
 * Interface that allows application logic to act independent of
 * any external protocols.
 * 
 * An application simply responds (with a body and status) to 
 * requests made up of an address and parameters.
 *  
 * @author marcelpuyat
 *
 */
public interface ApplicationInterface {
    
	/**
	 * Responds to a given request with a response.
	 * Caller will probably want to examine status of response.
	 * 
	 * @param request Application request
	 * @return Application response.
	 */
    public AppResponse respondToAppReq(AppRequest request);
    
    /**
     * Class that represents a request made to an application.
     * This consists of an address that uniquely maps to a given
     * action (aka an API endpoint) and a mapping of parameters.
     * @author marcelpuyat
     *
     */
    public static class AppRequest {
    	
    	/**
    	 * Address of request that maps to a particular action
    	 */
        private String requestAddress;
        
        /**
         * Map of parameters of the request
         */
        private Map<String, String> params;
        
        /**
         * Construct AppRequest object with given request address and params
         * @param requestAddress Request address
         * @param params Parameters. Should not be null.
         * @throws NullPointerException if params is null
         */
        public AppRequest(String requestAddress, Map<String, String> params) {
            this.requestAddress = requestAddress;
            if (params == null) throw new NullPointerException("Parameters of AppReq should not be null");
            this.params = params;
        }
        
        /**
         * Returns parameters of app request
         * @return parameters of app request
         */
        public Map<String, String> getParams() {
            return this.params;
        }
        
        /**
         * Returns address of app request
         * @return address of app request
         */
        public String getAddress() {
            return requestAddress;
        }
    }
    
    /**
     * Class that represents an application's response to a given request.
     * Contains a body and a status that indicates success or a kind of error that occurred.
     * @author marcelpuyat
     *
     */
    public static class AppResponse {
    	
    	/**
         * Indicates the status of a response to a given request.
         * @author marcelpuyat
         *
         */
        public enum AppResponseStatus {
            SUCCESS,				// Successful response
            INVALID_PARAMETERS, 	// AppRequest was missing a required parameter or used invalid type
            INVALID_DESTINATION, 	// AppRequest's address was invalid
            INTERNAL_ERROR			// Error whose details should not be exposed to client
        }
    	
        /**
         * Status of response.
         */
        private AppResponseStatus responseStatus;
        
        /**
         * Body of response to client.
         */
        private String responseBody;
        
        /**
         * Construct AppResponse object with given body and status.
         * @param body Body of response
         * @param responseStatus Status of response
         */
        public AppResponse(String body, AppResponseStatus responseStatus) {
            this.responseStatus = responseStatus;
            this.responseBody = body;
        }
        
        /**
         * Returns status of the response
         * @return Response status
         */
        public AppResponseStatus getResponseStatus() {
            return responseStatus;
        }
        
        /**
         * Returns body of the response.
         * @return Response body
         */
        public String getBody() {
            return responseBody;
        }
    }
}
