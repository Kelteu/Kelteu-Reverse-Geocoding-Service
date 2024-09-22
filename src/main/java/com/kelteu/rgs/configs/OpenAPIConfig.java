package com.kelteu.rgs.configs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {
    @Autowired
    ApplicationConfigs applicationConfigs;
    
    @Bean
    public OpenAPI customOpenAPI() {
        List<String> servers = applicationConfigs.getOpenApiServers();
        List<Server> openApiServers = new ArrayList<>();
        if (servers != null && !servers.isEmpty()) {
            for (String serverString : servers) {
                Server server = new Server();
                server.setUrl(serverString);
                openApiServers.add(server);
            }
        } else {
            Server server = new Server();
            server.setUrl("http://localhost");
            openApiServers.add(server);
        }
        return new OpenAPI().servers(openApiServers);
    }
}
