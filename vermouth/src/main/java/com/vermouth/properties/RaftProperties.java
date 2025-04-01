package com.vermouth.properties;

public class RaftProperties {

    private String dataDir;

    private int port = 2333;

    private String conf;

    private int electionTimeoutMillis = 5000;

    private int snapshotIntervalSecond = 30;

    private long requestTimeoutMillis = 5000L;

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
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
