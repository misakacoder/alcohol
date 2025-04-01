package com.vermouth.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetUtil {

    public static final String LOCAL_IP = "127.0.0.1";

    private static final Logger log = LoggerFactory.getLogger(NetUtil.class);

    public static List<String> ip() {
        try {
            List<String> ipList = new ArrayList<>();
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();
                    String hostAddress = inetAddress.getHostAddress();
                    if (inetAddress instanceof Inet4Address && !LOCAL_IP.equals(hostAddress)) {
                        ipList.add(hostAddress);
                    }
                }
            }
            return ipList;
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }
}
