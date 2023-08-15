import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JavaNIOBlockingServer {
    @SneakyThrows
    public static void main(String[] args) {
        log.info("start server");
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress("localhost", 8080));

            while (true) {
                SocketChannel clientSocketChannel = serverSocketChannel.accept();
                ByteBuffer requestByteBuffer = ByteBuffer.allocateDirect(1024);
                clientSocketChannel.read(requestByteBuffer);
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
