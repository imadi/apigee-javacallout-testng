package com.apigee.callout;

import com.apigee.flow.execution.Action;
import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.message.*;
import mockit.MockUp;
import mockit.Mock;
import org.apache.commons.lang3.StringEscapeUtils;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;
import java.util.regex.Pattern;

import static org.fest.reflect.core.Reflection.method;

/**
 * Created by seanwilliams on 10/5/16.
 */
public class JavaCalloutTest {

    MessageContext msgCtxt;
    ExecutionContext exeCtxt;
    String pattern1 = StringEscapeUtils.unescapeXml("(?i)(&lt;\\s*script\\b[^>]*>[^&lt;]+&lt;\\s*.+\\s*[s][c][r][i][p][t]\\s*>)");


    @BeforeMethod
    public void setUp() throws Exception {
        //Mock the MessageContext
        msgCtxt = new MockUp<MessageContext>() {
            private Map variables;
            public void $init() {
                variables = new HashMap();
            }

            @Mock()
            public <T> T getVariable(final String name){
                if (variables == null) {
                    variables = new HashMap();
                }
                return (T) variables.get(name);
            }

            @Mock()
            public boolean setVariable(final String name, final Object value) {
                if (variables == null) {
                    variables = new HashMap();
                }
                variables.put(name, value);
                return true;
            }

            @Mock()
            public boolean removeVariable(final String name) {
                if (variables == null) {
                    variables = new HashMap();
                }
                if (variables.containsKey(name)) {
                    variables.remove(name);
                }
                return true;
            }

        }.getMockInstance();

        //Mock the ExecutionContext
        exeCtxt = new MockUp<ExecutionContext>(){}.getMockInstance();
    }

    @AfterMethod
    public void tearDown() throws Exception {

    }

    /*
    Test the private method varName().
    It should always add a prefix to the property passed to it.
     */
    @Test
    public void testAddPrefix() throws Exception {
        Map<String, String> properties = new HashMap<>();

        // GIVEN
        String testVariable = "myvar";
        JavaCallout callout = new JavaCallout(properties);

        // WHEN
        String result = method("addPrefix")
                .withReturnType(String.class)
                .withParameterTypes(String.class)
                .in(callout)
                .invoke(testVariable);
        // THEN
        Assert.assertEquals(result, "prefix_myvar");
    }

    /*
    Test the private method varName().
    It should throw an IllegalStateException when the property is null.
     */
    @Test(expectedExceptions = IllegalStateException.class)
    public void testVarNameIllegalStateException() throws Exception {
        Map<String, String> properties = new HashMap<>();

        // GIVEN
        String testVariable = null;
        JavaCallout callout = new JavaCallout(properties);

        // WHEN
        String result = method("addPrefix")
                .withReturnType(String.class)
                .withParameterTypes(String.class)
                .in(callout)
                .invoke(testVariable);

    }

    /*
        Test the private method getProperty()
        This should return the value stored in the toMatch property, which is request.content
     */
    @Test
    public void testGetProperty() throws Exception {
        Map<String, String> properties = new HashMap<>();
        properties.put("toMatch", "request.content");
        properties.put("regex", pattern1);

        msgCtxt.setVariable("request.content", "<script>console.log(test)</script>");

        // GIVEN
        String testVariable = "toMatch";
        JavaCallout callout = new JavaCallout(properties);
        ExecutionResult calloutResult = callout.execute(msgCtxt, exeCtxt);

        // WHEN
        String result = method("getProperty")
                .withReturnType(String.class)
                .withParameterTypes(String.class)
                .in(callout)
                .invoke(testVariable);

        // THEN
        Assert.assertEquals(result, "request.content");
    }

    /*
        Test the private method getProperty()
        This should throw an IllegalStateException because request.content is not included in the toMatch field.
     */
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetPropertyIllegalStateException() throws Exception {
        Map<String, String> properties = new HashMap<>();
        properties.put("regex", pattern1);

        msgCtxt.setVariable("request.content", "<script>console.log(test)</script>");

        // GIVEN
        String testVariable = "toMatch";
        JavaCallout callout = new JavaCallout(properties);
        ExecutionResult calloutResult = callout.execute(msgCtxt, exeCtxt);

        // WHEN
        String result = method("getProperty")
                .withReturnType(String.class)
                .withParameterTypes(String.class)
                .in(callout)
                .invoke(testVariable);

    }

    /*
    This method tests that that the Java Callout executes successfully.
    and finds the supplied regex within the response.content variable.
     */
    @Test
    public void testExecute_Payload_Pattern1() throws Exception {
        //GIVEN
        Map<String, String> properties = new HashMap<>();
        properties.put("toMatch", "request.content");
        properties.put("username", "user@email.com");
        properties.put("checkHeaders", "false");
        properties.put("regex", pattern1);

        //WHEN
        msgCtxt.setVariable("request.content", "<script>this is a test</script>");

        JavaCallout callout = new JavaCallout(properties);
        ExecutionResult result = callout.execute(msgCtxt, exeCtxt);

        //THEN
        Assert.assertEquals(result, ExecutionResult.SUCCESS);
        Assert.assertEquals(msgCtxt.getVariable("flw.apigee.status"), "success");

        Assert.assertEquals(msgCtxt.getVariable("flw.apigee.patternFound"), pattern1);
        Assert.assertEquals(msgCtxt.getVariable("flw.apigee.match"), "<script>this is a test</script>");
        }


    /*
    This method tests that that the Java Callout
    finds the supplied regex within a request header.
    */
    @Test
    public void testExecute_Headers () throws Exception {
        //GIVEN
        Map<String, String> properties = new HashMap<>();
        properties.put("toMatch", "request.content");
        properties.put("checkHeaders", "true");
        properties.put("username", "user@email.com");
        properties.put("regex", pattern1);
        msgCtxt.setVariable("request.content", "this is a test");

        Map<String, String> headers = new HashMap<>();
        headers.put("x-username", "myusername");
        headers.put("x-hacking", "<script>console.log(password)</script>");
        setMsgCtxtHeaders(headers);

        //WHEN
        JavaCallout callout = new JavaCallout(properties);
        ExecutionResult result = callout.execute(msgCtxt, exeCtxt);

        //THEN
        Assert.assertEquals(result, ExecutionResult.SUCCESS);
        Assert.assertEquals(msgCtxt.getVariable("flw.apigee.status"), "success");

        Assert.assertEquals(msgCtxt.getVariable("flw.apigee.patternFound"), pattern1);
        Assert.assertEquals(msgCtxt.getVariable("flw.apigee.match"), "<script>console.log(password)</script>");
    }

    /*
    Test the execute() method
     username property is not included. IllegalStateException should be thrown.
    */
    @Test
    public void testExecuteIllegalStateExeption() throws Exception {
        //GIVEN
        Map<String, String> properties = new HashMap<>();
        properties.put("toMatch", "request.content");
        properties.put("regex", pattern1);

        msgCtxt.setVariable("request.content", "<script>this is a test</script>");

        //WHEN
        JavaCallout callout = new JavaCallout(properties);
        ExecutionResult result = callout.execute(msgCtxt, exeCtxt);

        //THEN
        Assert.assertEquals(result.getAction(), Action.ABORT);
        Assert.assertEquals(result.getErrorResponse(), "username was not supplied in the Java Callout.");
        Assert.assertEquals(result.getErrorResponseHeaders().get("x-Exception-Class"), "java.lang.IllegalStateException");
        Assert.assertEquals(msgCtxt.getVariable("flw.apigee.status"), "unsuccessful");

    }

    /*
    Helper function to set headers for a test case;
    Single value: .put("x-header", "value")
    Multiple values: .put("x-header", "value1,value2,value3")
     */
    public void setMsgCtxtHeaders(Map<String, String> headers){
        msgCtxt.setVariable("request.headers.count", headers.size());
        ArrayList<String> headerNames = new ArrayList<>();

        for(Map.Entry<String, String> headerEntry: headers.entrySet()){
            headerNames.add(headerEntry.getKey());
            String [] valuesArray = headerEntry.getValue().split(",");
            List<String> values = Arrays.asList(valuesArray);
            msgCtxt.setVariable("request.header." + headerEntry.getKey() + ".values", values);
        }
        msgCtxt.setVariable("request.headers.names", Collections.unmodifiableCollection(headerNames));
    }
 }