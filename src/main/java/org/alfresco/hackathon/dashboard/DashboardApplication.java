package org.alfresco.hackathon.dashboard;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(DashboardApplication.class,
                              args);
    }
}

@RestController
class ELKController {

    private static final Logger LOG = Logger.getLogger(ELKController.class.getName());

    @Autowired
    RestTemplate restTemplate;

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @RequestMapping(value = "/elkdemo")
    public String helloWorld() {
        String response = "Hello user ! " + new Date();
        LOG.log(Level.INFO,
                "/elkdemo - &gt; " + response);

        return response;
    }

    @RequestMapping(value = "/elk")
    public String helloWorld1() {

        String response = (String) restTemplate.exchange("http://localhost:8080/elkdemo",
                                                         HttpMethod.GET,
                                                         null,
                                                         new ParameterizedTypeReference() {
                                                }).getBody();
        LOG.log(Level.INFO,
                "/elk - &gt; " + response);

        try {
            String exceptionrsp = (String) restTemplate.exchange("http://localhost:8080/exception",
                                                                 HttpMethod.GET,
                                                                 null,
                                                                 new ParameterizedTypeReference() {
                                                        }).getBody();
            LOG.log(Level.INFO,
                    "/elk trying to print exception - &gt; " + exceptionrsp);
            response = response + " === " + exceptionrsp;
        } catch (Exception e) {
            // exception should not reach here. Really bad practice 🙂
        }

        return response;
    }

    @RequestMapping(value = "/exception")
    public String exception() {
        String rsp = "";
        try {
            int i = 1 / 0;
            // should get exception
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            LOG.error("Exception As String :: - &gt; " + sStackTrace);

            rsp = sStackTrace;
        }

        return rsp;
    }
}