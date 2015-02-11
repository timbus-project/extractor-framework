/**
 * Copyright (c) 2013, Caixa Magica Software Lda (CMS).
 * The work has been developed in the TIMBUS Project and the above-mentioned are Members of the TIMBUS Consortium.
 * TIMBUS is supported by the European Union under the 7th Framework Programme for research and technological
 * development and demonstration activities (FP7/2007-2013) under grant agreement no. 269940.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:   http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including without
 * limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTIBITLY, or FITNESS FOR A PARTICULAR
 * PURPOSE. In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise,
 * unless required by applicable law or agreed to in writing, shall any Contributor be liable for damages, including
 * any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this
 * License or out of the use or inability to use the Work.
 * See the License for the specific language governing permissions and limitation under the License.
 */
package net.timbusproject.extractors.pojo;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.osgi.service.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: miguel
 * Date: 22-11-2013
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
public class CallBack {

    @Autowired
    LogService log;

    private String fromMail;
    private String smtp;
    private String port;
    private String password;
    private String socketFactoryClass;
    private String mailAuth;

    public CallBack() {
    }

    public synchronized void doCallBack(long key, RequestExtractionList extractionList, boolean success) throws URISyntaxException, IOException {
        CallBackInfo info = extractionList.getCallbackInfo();
        System.out.println("IN CALLBACK. REQUEST TYPE: " + info.getFinalOriginRequestType());
        if (info == null)
            System.out.println("No callback information provided");
        else {
            if (info.getMails() != null) {
                setMailConfiguration();
                for (String a : info.getMails()) {
                    System.out.println("Email adress is: " + a);
                    if (isValidEmailAddress(a)) {
                        sendEmail(a, "End of extraction request",
                                "The extractions were completed. Please, check out /extractors/api/requests/ to view the results");
                        System.out.println("Sent email: " + a);
                    }
                }
            } else
                System.out.println("No e-mail adress(es) provided");
            if (info.getEndPoints() != null) {
                for (String a : info.getEndPoints()) {
                    HttpResponse response;
                    if (info.requestType != null && info.requestType.toLowerCase().equals("post")) {
                        try {
                            JSONObject jsonResult = new JSONObject().put("id", key);
                            if (success)
                                jsonResult.put("result", "completed");
                            else
                                jsonResult.put("result", "failed");
                            DefaultHttpClient httpClient = new DefaultHttpClient();
                            HttpPost postRequest = new HttpPost(a);
                            postRequest.setEntity(new StringEntity(jsonResult.toString(), ContentType.create("application/json")));
                            response = httpClient.execute(postRequest);
                            System.out.println("Sent POST request to " + a);
                            System.out.println("Endpoint " + a + " says: " + response.getStatusLine());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Sending GET request to: " + a);
                        String uri = a;
                        if (!uri.startsWith("http://"))
                            uri = "http://" + uri;
                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
                        response = httpClient.execute(new HttpGet(uri));
                        System.out.println("Sent GET request to " + uri);
                        System.out.println("Endpoint " + a + " says: " + response.getStatusLine());
                    }
                }
            }
            else{
                System.out.println("No foreign endpoints for callback provided");
            }
            if (info.getOriginEndpoint() != null) {
                HttpResponse response;
                if (info.getFinalOriginRequestType().equals("post")) {
                    System.out.println("Preparing POST request for endpoint " + "http://" + info.getOriginEndpoint());
                    try {
                        JSONObject jsonResult = new JSONObject().put("id", key);
                        if (success)
                            jsonResult.put("result", "completed");
                        else
                            jsonResult.put("result", "failed");
                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        HttpPost postRequest = new HttpPost("http://" + info.getOriginEndpoint());
                        postRequest.setEntity(new StringEntity(jsonResult.toString(), ContentType.create("application/json")));
                        response = httpClient.execute(postRequest);
                        System.out.println("Sent POST request to " + "http://" + info.getOriginEndpoint());
                        System.out.println("Endpoint " + "http://" + info.getOriginEndpoint() + " says: " + response.getStatusLine());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Preparing GET request for endpoint " + "http://" + info.getOriginEndpoint());
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
                    response = httpClient.execute(new HttpGet("http://" + info.getOriginEndpoint()));
                }
            }
            else{
                System.out.println("No port or path provided for origin endpoint callback");
            }
        }
    }

    private boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        if (result)
            System.out.println("The e-mail " + email + " adress is valid.");
        else
            System.out.println("The e-mail " + email + " adress is not valid");
        return result;
    }

    public void sendEmail(String to, String subject, String body) {
        final String fromUser = fromMail;
        final String passUser = password;
        Properties props = new Properties();
        props.put("mail.smtp.host", smtp);
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class", socketFactoryClass);
        props.put("mail.smtp.auth", mailAuth);
        props.put("mail.smtp.port", port);
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromUser, passUser);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromUser));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMailConfiguration() {
        Scanner s = new Scanner(CallBack.class.getResourceAsStream("/default/callback"));
        s.findInLine("mail:");
        fromMail = s.nextLine().trim().toLowerCase();
        while (s.hasNextLine()) {
            switch (s.next().trim().replace(":", "")) {
                case "smtp":
                    smtp = s.nextLine().trim();
                    break;
                case "password":
                    password = s.nextLine().trim();
                    break;
                case "port":
                    port = s.nextLine().trim();
                    break;
                case "socketfactoryclass":
                    socketFactoryClass = s.nextLine().trim();
                    break;
                case "mailauthentication":
                    mailAuth = s.nextLine().trim();
                    break;
                default:
                    s.nextLine();
                    break;
            }
        }
    }
}

