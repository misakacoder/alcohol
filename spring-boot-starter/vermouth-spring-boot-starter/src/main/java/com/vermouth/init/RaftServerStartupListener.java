package com.vermouth.init;

import com.vermouth.core.RaftServer;

public interface RaftServerStartupListener {

    void beforeStartup(RaftServer raftServer);

    void afterStartup(RaftServer raftServer);
}
