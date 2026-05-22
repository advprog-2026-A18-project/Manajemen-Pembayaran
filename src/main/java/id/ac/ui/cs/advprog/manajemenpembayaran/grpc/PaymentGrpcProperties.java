package id.ac.ui.cs.advprog.manajemenpembayaran.grpc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "grpc")
public class PaymentGrpcProperties {
    private int port = 9095;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
