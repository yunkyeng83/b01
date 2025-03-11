package org.zerock.b01.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CustomServletConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);

        registry.addResourceHandler("/js/**")  // url patternf
                .addResourceLocations("classpath:/static/js/");  // 실제 하드디스크의 파일 위치

        registry.addResourceHandler("/fonts/**")  // url patternf
                .addResourceLocations("classpath:/static/fonts/");  // 실제 하드디스크의 파일 위치

        registry.addResourceHandler("/css/**")  // url patternf
                .addResourceLocations("classpath:/static/css/");  // 실제 하드디스크의 파일 위치

        registry.addResourceHandler("/assets/**")  // url patternf
                .addResourceLocations("classpath:/static/assets/");  // 실제 하드디스크의 파일 위치

    }
}
