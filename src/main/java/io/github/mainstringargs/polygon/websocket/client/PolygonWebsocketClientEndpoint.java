package io.github.mainstringargs.polygon.websocket.client;

import io.github.mainstringargs.abstracts.websocket.client.AbstractWebsocketClientEndpoint;
import io.github.mainstringargs.abstracts.websocket.client.WebsocketClient;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.net.URI;

/**
 * The type Polygon websocket client endpoint.
 */
@ClientEndpoint
public class PolygonWebsocketClientEndpoint extends AbstractWebsocketClientEndpoint<String> {

    /**
     * Instantiates a new Polygon websocket client endpoint.
     *
     * @param websocketClient the websocket client
     * @param endpointURI     the endpoint uri
     */
    public PolygonWebsocketClientEndpoint(WebsocketClient websocketClient, URI endpointURI) {
        super(websocketClient, endpointURI, "PolygonWebsocketThread");
    }

    @OnOpen
    @Override
    public void onOpenAnnotated(Session userSession) {
        super.onOpen(userSession);
    }

    @OnClose
    @Override
    public void onCloseAnnotated(Session userSession, CloseReason reason) {
        super.onClose(userSession, reason);
    }

    @OnMessage
    @Override
    public void onMessageAnnotated(String message) {
        super.onMessage(message);
    }
}
