package com.apigee.callout;

import com.apigee.flow.execution.Action;
import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
//import com.apigee.flow.execution.IOIntensive;
import com.apigee.flow.execution.spi.Execution;
import com.apigee.flow.message.MessageContext;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaCallout implements Execution {
    private Map<String,String> properties; // read-only
    private static final String _varPrefix = "prefix_";

    public JavaCallout(Map properties) {
        // convert the untyped Map to a generic map
        Map<String,String> m = new HashMap<String,String>();
        Iterator iterator = properties.keySet().iterator();
        while(iterator.hasNext()){
            Object key = iterator.next();
            Object value = properties.get(key);
            if ((key instanceof String) && (value instanceof String)) {
                m.put((String) key, (String) value);
            }
        }
        this.properties = m;
    }

    public ExecutionResult execute (MessageContext msgCtxt, ExecutionContext exeCtxt) {
        try {
            String username = getProperty("username");
            msgCtxt.setVariable("flw.apigee.username", addPrefix(username));
            String toMatch = getProperty("toMatch");
            String regex = getProperty("regex");
            findPattern(toMatch, regex, msgCtxt);
            Map<String, Collection<String>> headers = getHeaders(msgCtxt);

            if(headers != null) {
                findPatternInMap(regex, headers, msgCtxt);
            }

            msgCtxt.setVariable("flw.apigee.status", "success");
            String patternFound = msgCtxt.getVariable("flw.apigee.patternFound");

            if(patternFound.equals("") || patternFound == null){
                msgCtxt.setVariable("flw.apigee.patternNotFound", "The supplied pattern was not found.");
            }
        }
        catch (Exception ex) {
            ExecutionResult executionResult = new ExecutionResult(false, Action.ABORT);
            executionResult.setErrorResponse(ex.getMessage());
            executionResult.addErrorResponseHeader("x-Exception-Class", ex.getClass().getName());
            msgCtxt.setVariable("flw.apigee.status", "unsuccessful");
            return executionResult;
        }
        return ExecutionResult.SUCCESS;
    }

    private static final String addPrefix(String s) {
        if(s == null) throw new IllegalStateException("Property is null");
        return _varPrefix + s; }

    /*
   Retrieve a variable from the message context.
    */
    private String getMsgCtxtProperty(String property, MessageContext msgCtxt) {
        if (property != null) {
            String value = msgCtxt.getVariable(property);
            return value;
        } else {
            throw new IllegalStateException("Invalid property.");
        }
    }

    /*
    Retrieve a property from the properties collection.
     */
    private String getProperty(String property) {
        String result = this.properties.get(property);
        if(result == null){
            throw new IllegalStateException(property + " was not supplied in the Java Callout.");
        } else {
            return result;
        }
    }


    /*
    This method searches for the regex pattern in the map in the toMatch field.
     */
    private void findPattern(String toMatch, String regex, MessageContext msgCtxt) {
        String fieldToUpdate = getMsgCtxtProperty(toMatch, msgCtxt);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fieldToUpdate);
        boolean matched = matcher.find();
        String result = "";
        if (matched) {
            msgCtxt.setVariable("flw.apigee.patternFound", pattern.pattern());
            msgCtxt.setVariable("flw.apigee.match", matcher.group());
        } else {
            msgCtxt.setVariable("flw.apigee.patternNotFound", "The pattern " + pattern.pattern() + " was not found in " + toMatch);
        }
    }


    /*
    This method searches for the regex within the headers or it could be used to search
    in query parameters as well.  It stops when the first match is found. If there is no
    match then it doesn't return any error or set any flow variables.
     */
    private void findPatternInMap(String regex, Map<String, Collection<String>> collection, MessageContext msgCtxt) {

        Pattern pattern = Pattern.compile(regex);
        for(Map.Entry<String, Collection<String>> entry : collection.entrySet()) {
            for(String entryValue : entry.getValue()){
                Matcher matcher = pattern.matcher(entryValue);
                boolean matched = matcher.find();
                String result = "";
                if (matched) {
                    msgCtxt.setVariable("flw.apigee.patternFound", pattern.pattern());
                    msgCtxt.setVariable("flw.apigee.match", matcher.group());
                    return;
                }
            }
        }
    }

    private Map<String, Collection<String>> getHeaders(MessageContext msgCtxt) {
        Map<String, Collection<String>> hdrs = null;

        if ("true".equalsIgnoreCase(getProperty("checkHeaders"))) {
            int numHdrs = msgCtxt.getVariable("request.headers.count");
            hdrs = new HashMap<String, Collection<String>>(numHdrs);
            for(String hdr : (Collection<String>)msgCtxt.getVariable("request.headers.names")) {
                Collection<String> hdrvalues = msgCtxt.getVariable("request.header."+hdr+".values");
                hdrs.put(hdr, hdrvalues);
            }
        }
        return hdrs;
    }

}
