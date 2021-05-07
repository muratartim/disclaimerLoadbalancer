package com.artim.disclaimerloadbalancer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

@RestController
@RibbonClient(
        name = "ping-a-server",
        configuration = RibbonConfiguration.class)
public class ServerLocationApp {

    @LoadBalanced
    @Bean
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/")
    public String readCookie(@CookieValue(value = "disclaimer") String username) {

        // cookie exists, route to backend
        return this.restTemplate.getForObject(
                "http://ping-server/", String.class);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    private ResponseEntity<Void> noCookie(HttpServletResponse response){

        // create a cookie
        Cookie cookie = new Cookie("disclaimer", "accepted");

        // expires in 7 days
        cookie.setMaxAge(7 * 24 * 60 * 60);

        //add cookie to response
        response.addCookie(cookie);

        // redirect to disclaimer
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("http://www.equinox-digital-twin.com/test/disclaimer.html")).build();
    }
}
