package com.cq.template.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * *
 * Knife4j 配置
 *
 * @author 程崎
 * @since 2023/10/29
 */
@Configuration
public class Knife4jConfig {


    @Bean
    public OpenAPI evaluationOpenApi() {
        Contact authorContact = new Contact()
                .name("程崎")
                .email("2972084238@qq.com");
        Info info = new Info()
                .title("后端模板 API")
                .version("V1.0")
                .description("完美的后端模板")
                .contact(authorContact);
        return new OpenAPI().info(info);
    }


}
