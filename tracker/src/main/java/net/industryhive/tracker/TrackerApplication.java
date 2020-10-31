package net.industryhive.tracker;

import net.industryhive.tracker.connect.Connector;

/**
 * @Author 未央
 * @Create 2020-10-13 9:49
 */
public class TrackerApplication {
    public static void main(String[] args) {
        Connector.connect();
    }
}
