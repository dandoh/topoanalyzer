package config;

/**
 * Created by Dandoh on 6/29/17.
 */
public class Constant {
    public static final int SWITCH_DELAY = 0;
    public static final int LINK_DELAY = 12000; // time to transfer a packet of 12000 bit
                                                // = 12000 nanosecond
    public static final int HOST_DELAY = 0;
    public static final int PACKET_SIZE = 12000; // bit
    public static final int MAX_TIME = (int) 1e6;
    public static final int PACKET_INTERVAL = PACKET_SIZE;
}
