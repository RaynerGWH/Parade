package game;

import java.net.URI;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;

public class GameClientRunnable implements Runnable {
    private URI endpointURI;

    public GameClientRunnable(URI endpointURI) {
        this.endpointURI = endpointURI;
    }

    @Override
    public void run() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            GameClientEndpoint clientEndpoint = new GameClientEndpoint(endpointURI);
            container.connectToServer(clientEndpoint, endpointURI);
            // Additional logic to handle messages or keep the connection alive
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
