package com.vermouth.listener;

import com.vermouth.core.RaftServer;

public interface RaftServerStartupListener {

    default void before(RaftServer raftServer) {

    }

    default void after(RaftServer raftServer) {

    }
}
