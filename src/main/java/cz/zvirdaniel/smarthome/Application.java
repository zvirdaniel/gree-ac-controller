package cz.zvirdaniel.smarthome;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import cz.zvirdaniel.smarthome.configs.BooleanDeserializer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.DatagramSocket;
import java.net.SocketException;

@Slf4j
@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan
public class Application {
    public static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
                                                               .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                                                               .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                                               .addModule(BooleanDeserializer.asModule())
                                                               .build();

    @Value("${gree.timeout:30000}")
    private Integer socketTimeout;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public DatagramSocket datagramSocket() {
        try {
            final DatagramSocket clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(socketTimeout);
            return clientSocket;
        } catch (SocketException e) {
            throw new RuntimeException("Can't create datagram socket!");
        }
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Smart Home API"));
    }
}