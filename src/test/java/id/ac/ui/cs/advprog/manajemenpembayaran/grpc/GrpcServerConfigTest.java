package id.ac.ui.cs.advprog.manajemenpembayaran.grpc;

import id.ac.ui.cs.advprog.manajemenpembayaran.service.PayrollEventService;
import io.grpc.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GrpcServerConfigTest {

    @Test
    void paymentGrpcPropertiesShouldUseDefaultPortAndAllowOverride() {
        PaymentGrpcProperties properties = new PaymentGrpcProperties();

        assertEquals(9095, properties.getPort());

        properties.setPort(9191);

        assertEquals(9191, properties.getPort());
    }

    @Test
    void paymentGrpcServerShouldCreateLifecycleForConfiguredPort() {
        GrpcServerConfig config = new GrpcServerConfig();
        PaymentGrpcProperties properties = new PaymentGrpcProperties();
        properties.setPort(0);
        PaymentInternalGrpcService paymentService = new PaymentInternalGrpcService(mock(PayrollEventService.class));

        Server server = config.paymentGrpcServer(properties, paymentService);

        assertInstanceOf(GrpcServerConfig.ServerLifecycle.class, server);
        config.shutdown();
    }

    @Test
    void shutdownShouldBeSafeWhenServerWasNeverCreated() {
        GrpcServerConfig config = new GrpcServerConfig();

        config.shutdown();
    }

    @Test
    void serverLifecycleShouldDelegateOperations() throws IOException, InterruptedException {
        Server delegate = mock(Server.class);
        GrpcServerConfig.ServerLifecycle lifecycle = new GrpcServerConfig.ServerLifecycle(delegate);
        when(delegate.start()).thenReturn(delegate);
        when(delegate.shutdown()).thenReturn(delegate);
        when(delegate.shutdownNow()).thenReturn(delegate);
        when(delegate.isShutdown()).thenReturn(true);
        when(delegate.isTerminated()).thenReturn(true);
        when(delegate.awaitTermination(5, TimeUnit.SECONDS)).thenReturn(true);
        when(delegate.getPort()).thenReturn(9095);

        assertSame(delegate, lifecycle.start());
        assertSame(delegate, lifecycle.shutdown());
        assertSame(delegate, lifecycle.shutdownNow());
        assertEquals(true, lifecycle.isShutdown());
        assertEquals(true, lifecycle.isTerminated());
        assertEquals(true, lifecycle.awaitTermination(5, TimeUnit.SECONDS));
        assertEquals(9095, lifecycle.getPort());
        lifecycle.awaitTermination();

        verify(delegate).awaitTermination();
    }
}
