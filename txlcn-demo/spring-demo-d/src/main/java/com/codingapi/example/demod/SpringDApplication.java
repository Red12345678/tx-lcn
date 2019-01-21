package com.codingapi.example.demod;

import com.codingapi.txlcn.client.config.EnableDistributedTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Description:
 * Date: 2018/12/25
 *
 * @author ujued
 */
@EnableDistributedTransaction
@SpringBootApplication
@EnableEurekaClient
public class SpringDApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDApplication.class, args);

    }
}
