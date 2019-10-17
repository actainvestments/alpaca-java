package io.github.mainstringargs.polygon.nats;

import com.google.common.collect.Sets;
import io.github.mainstringargs.polygon.enums.ChannelType;
import io.github.mainstringargs.polygon.nats.message.ChannelMessage;
import io.github.mainstringargs.polygon.properties.PolygonProperties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class PolygonNatsTestDriver {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        PolygonNatsClient client = new PolygonNatsClient(PolygonProperties.KEY_ID_VALUE,
                PolygonProperties.POLYGON_NATS_SERVERS_VALUE);

        Map<String, Set<ChannelType>> subscribedTypes = new HashMap<>();

        subscribedTypes.put("SNAP", Sets.newHashSet(ChannelType.values()));
        subscribedTypes.put("AMZN",
                Sets.newHashSet(ChannelType.AGGREGATE_PER_MINUTE, ChannelType.AGGREGATE_PER_SECOND));

        PolygonStreamListener listener1 = new PolygonStreamListenerAdapter(subscribedTypes) {

            @Override
            public void streamUpdate(String ticker, ChannelType channelType, ChannelMessage message) {
                System.out.println(ticker + " " + channelType + " " + message);
            }

        };

        client.addListener(listener1);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        PolygonStreamListener listener2 = new PolygonStreamListenerAdapter(
                new HashSet<String>(Arrays.asList("SNAP", "AMZN")), ChannelType.values()) {

            @Override
            public void streamUpdate(String ticker, ChannelType channelType, ChannelMessage message) {
                System.out.println(ticker + " " + channelType + " " + message);
            }

        };

        client.addListener(listener2);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        System.out.println("remove one");
        client.removeListener(listener2);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        System.out.println("remove two");


        client.removeListener(listener2);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        System.out.println("remove three");


        client.removeListener(listener1);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        System.out.println("add one");

        client.addListener(listener2);

        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
