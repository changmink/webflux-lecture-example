import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JavaNIONonBlockingServer {
    @SneakyThrows
    public static void main(String[] args) {
        log.info("start server");
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress("localhost", 8080));
            serverSocketChannel.configureBlocking(false);

            while (true) {
                SocketChannel clientSocketChannel = serverSocketChannel.accept();
                if (clientSocketChannel == null) {
                    continue;
                }

                ByteBuffer requestByteBuffer = ByteBuffer.allocateDirect(1024);
                while (clientSocketChannel.read(requestByteBuffer) == 0) {
                    log.info("Reading...");
                }

                requestByteBuffer.flip();
                String requestBody = StandardCharsets.UTF_8.decode(requestByteBuffer).toString();
                log.info("request: {}", requestBody);

                ByteBuffer responseByteBuffer = ByteBuffer.wrap("This is server".getBytes());
                clientSocketChannel.write(responseByteBuffer);
                clientSocketChannel.close();
                log.info("end client connect");
            }
        }

    }
}
