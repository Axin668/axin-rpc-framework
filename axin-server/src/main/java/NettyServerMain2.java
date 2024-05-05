import com.axinstar.rpc.HelloService;
import com.axinstar.rpc.entity.RpcServiceProperties;
import com.axinstar.rpc.serviceImpl.HelloServiceImpl;
import com.axinstar.rpc.provider.ServiceProviderImpl;
import com.axinstar.rpc.remoting.transport.netty.server.NettyServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Server: Manually register the service
 *
 * @author axin
 * @since 2024/04/13
 */
public class NettyServerMain2 {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyServer nettyServer = applicationContext.getBean(NettyServer.class);
        nettyServer.start();
        ServiceProviderImpl serviceProvider = new ServiceProviderImpl();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test")
                .version("1")
                .build();
        serviceProvider.publishService(helloService, rpcServiceProperties);
    }
}
