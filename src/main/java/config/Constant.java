package config;

/**
 * Created by Dandoh on 6/29/17.
 */
public class Constant {
    public static final int SWITCH_DELAY = 0;
    // Link bandwidth, set default to 1Gps
    public static final long LINK_BANDWIDTH = 1000000000;

    public static final int LINK_LENGTH = 5;

    public static final double PROPAGATION_VELOCITY = 1.0 / 40;

    public static final int HOST_DELAY = 100;
    public static final int PACKET_SIZE = 8000000; // 1Mb
    public static final int MAX_TIME = (int) 1e9;
    public static final int PACKET_INTERVAL = PACKET_SIZE;
}
