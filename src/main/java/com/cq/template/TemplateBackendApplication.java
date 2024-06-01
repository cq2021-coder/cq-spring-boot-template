package com.cq.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 编辑器后端应用程序
 *
 * @author cq
 * @since 2024/03/19
 */
@SpringBootApplication
public class TemplateBackendApplication {

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(TemplateBackendApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        System.out.printf("""
                ----------------------------------------------------------
                Application is running! Knife4j online docs:
                Local: \t\thttp://localhost:%s%s/doc.html
                External: \thttp://%s:%s%s/doc.html
                ------------------------------------------------------------
                """, port, path, ip, port, path);
    }

}
