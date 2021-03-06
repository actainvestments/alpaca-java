package io.github.mainstringargs.alpaca.websocket.client;

import io.github.mainstringargs.abstracts.websocket.client.AbstractWebsocketClientEndpoint;
import io.github.mainstringargs.abstracts.websocket.client.WebsocketClient;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * The type Alpaca websocket client endpoint.
 */
@ClientEndpoint
public class AlpacaWebsocketClientEndpoint extends AbstractWebsocketClientEndpoint<byte[]> {

    /**
     * Instantiates a new Alpaca websocket client endpoint.
     *
     * @param websocketClient the websocket client
     * @param endpointURI     the endpoint uri
     */
    public AlpacaWebsocketClientEndpoint(WebsocketClient websocketClient, URI endpointURI) {
        super(websocketClient, endpointURI, "AlpacaWebsocketThread");
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
    public void onMessageAnnotated(byte[] message) {
        super.onMessage(new String(message, StandardCharsets.UTF_8));
    }
}
