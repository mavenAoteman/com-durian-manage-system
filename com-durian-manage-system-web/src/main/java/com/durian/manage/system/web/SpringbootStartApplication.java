package com.durian.manage.system.web;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "com.durian.manage.system.*",
        scanBasePackageClasses = SpringbootStartApplication.class, exclude = {DataSourceAutoConfiguration.class})
public class SpringbootStartApplication extends SpringBootServletInitializer {


    /**
     * 兼容tomcat部署模式
     *
     * @param application spring环境
     * @return SpringApplicationBuilder
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpringbootStartApplication.class);
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(SpringbootStartApplication.class, args);
        System.out.println("/**********************APP-STARTED****************************/");
    }
}
