package org.yearup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class EasyshopApplication
{

    public static void main(String[] args) {
        ApplicationContext AC = SpringApplication.run(EasyshopApplication.class, args);
        for(String name : AC.getBeanDefinitionNames()){
            System.out.println(name);
        }
    }

}
