package com.vermouth.core;

import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.RaftGroupService;
import com.vermouth.processor.RequestProcessor;

public class RaftGroup {

    private final Node node;

    private final RaftGroupService raftGroupService;

    private final RaftStateMachine raftStateMachine;

    private final RequestProcessor requestProcessor;

    public RaftGroup(Node node, RaftGroupService raftGroupService, RaftStateMachine raftStateMachine, RequestProcessor requestProcessor) {
        this.node = node;
        this.raftGroupService = raftGroupService;
        this.raftStateMachine = raftStateMachine;
        this.requestProcessor = requestProcessor;
    }

    public Node getNode() {
        return node;
    }

    public RaftGroupService getRaftGroupService() {
        return raftGroupService;
    }

    public RaftStateMachine getRaftStateMachine() {
        return raftStateMachine;
    }

    public RequestProcessor getRequestProcessor() {
        return requestProcessor;
    }
}
