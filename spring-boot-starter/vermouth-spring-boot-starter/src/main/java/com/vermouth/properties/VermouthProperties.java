package com.vermouth.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.vermouth.properties.VermouthProperties.PREFIX;

@Configuration
@ConfigurationProperties(prefix = PREFIX)
public class VermouthProperties {

    public static final String PREFIX = "vermouth";

    private String home;

    private int port;

    private List<String> nodes;

    private int electionTimeoutMillis = 5000;

    private int snapshotIntervalSecond = 30;

    private long requestTimeoutMillis = 5000L;

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    public int getElectionTimeoutMillis() {
        return electionTimeoutMillis;
    }

    public void setElectionTimeoutMillis(int electionTimeoutMillis) {
        this.electionTimeoutMillis = electionTimeoutMillis;
    }

    public int getSnapshotIntervalSecond() {
        return snapshotIntervalSecond;
    }

    public void setSnapshotIntervalSecond(int snapshotIntervalSecond) {
        this.snapshotIntervalSecond = snapshotIntervalSecond;
    }

    public long getRequestTimeoutMillis() {
        return requestTimeoutMillis;
    }

    public void setRequestTimeoutMillis(long requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
    }
}
