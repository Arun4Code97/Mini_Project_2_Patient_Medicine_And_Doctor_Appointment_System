package com.healthcare.doctor_consultation_medicine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
//    This setup allows Spring to correctly handle the method override and route the request to the appropriate handler method.
@Configuration
public class WebConfig {

//    Used the _method hidden field in my all forms (template) to simulate PUT or DELETE requests as browsers support only GET & post req.
//    HiddenHttpMethodFilter: This filter looks for a request parameter named _method. If it finds it, it overrides the HTTP method of the request with the value of this parameter.
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}