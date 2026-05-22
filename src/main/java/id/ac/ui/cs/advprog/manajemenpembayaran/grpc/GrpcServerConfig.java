package id.ac.ui.cs.advprog.manajemenpembayaran.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@ConditionalOnProperty(prefix = "grpc", name = "enabled", havingValue = "true", matchIfMissing = true)
public class GrpcServerConfig {
    private Server server;

    @Bean(initMethod = "start")
    Server paymentGrpcServer(PaymentGrpcProperties properties, PaymentInternalGrpcService paymentService) {
        server = ServerBuilder.forPort(properties.getPort())
                .addService(paymentService)
                .build();
        return new ServerLifecycle(server);
    }

    @PreDestroy
    void shutdown() {
        if (server != null) {
            server.shutdown();
        }
    }

    static class ServerLifecycle extends Server {
        private final Server delegate;

        ServerLifecycle(Server delegate) {
            this.delegate = delegate;
        }

        @Override
        public Server start() throws IOException {
            return delegate.start();
        }

        @Override
        public Server shutdown() {
            return delegate.shutdown();
        }

        @Override
        public Server shutdownNow() {
            return delegate.shutdownNow();
        }

        @Override
        public boolean isShutdown() {
            return delegate.isShutdown();
        }

        @Override
        public boolean isTerminated() {
            return delegate.isTerminated();
        }

        @Override
        public boolean awaitTermination(long timeout, java.util.concurrent.TimeUnit unit) throws InterruptedException {
            return delegate.awaitTermination(timeout, unit);
        }

        @Override
        public void awaitTermination() throws InterruptedException {
            delegate.awaitTermination();
        }

        @Override
        public int getPort() {
            return delegate.getPort();
        }
    }
}
