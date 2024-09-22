package com.healthcare.doctor_consultation_medicine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
public class WebConfig {
//    Create a WebConfig class with a method that returns a HiddenHttpMethodFilter bean.
//    Use the _method hidden field in your forms (template) to simulate PUT or DELETE requests as browsers support only GET & post req.
//    This setup allows Spring to correctly handle the method override and route the request to the appropriate handler method.

//    HiddenHttpMethodFilter: This filter looks for a request parameter named _method. If it finds it, it overrides the HTTP method of the request with the value of this parameter.
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}