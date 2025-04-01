package com.vermouth.core;

import com.alipay.sofa.jraft.Closure;
import com.alipay.sofa.jraft.Iterator;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.core.StateMachineAdapter;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotReader;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotWriter;
import com.vermouth.entity.Operation;
import com.vermouth.entity.Request;
import com.vermouth.entity.Response;
import com.vermouth.processor.RequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

public class RaftStateMachine extends StateMachineAdapter {

    private static final Logger log = LoggerFactory.getLogger(RaftStateMachine.class);

    private final AtomicLong leaderTerm = new AtomicLong(-1);

    private final RequestProcessor requestProcessor;

    public RaftStateMachine(RequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    @Override
    public void onApply(Iterator iterator) {
        //参考自Nacos源码：com.alibaba.nacos.core.distributed.raft.NacosStateMachine#onApply
        //记录当前处理的日志条目的索引index和已经成功执行的日志数量applied，当出现异常时进行日志回滚
        //观察发现差值始终为1，感觉单线程处理日志好像没啥用，多线程处理日志可能有用
        int index = 0;
        int applied = 0;
        try {
            while (iterator.hasNext()) {
                Request request = null;
                RaftClosure closure = null;
                Status status = Status.OK();
                try {
                    closure = (RaftClosure) iterator.done();
                    //回调不为null说明是leader提交的任务，直接从回调类中获取request，减少序列化时间
                    if (closure != null) {
                        request = closure.getRequest();
                    } else {
                        //回调为null说明是leader同步到follower的日志
                        ByteBuffer data = iterator.getData();
                        request = Request.parseFrom(data.array());
                        //follower忽略读操作
                        if (request.getOperation() == Operation.READ) {
                            applied++;
                            index++;
                            iterator.next();
                            continue;
                        }
                    }
                    Response response = requestProcessor.process(request);
                    if (closure != null) {
                        closure.setResponse(response);
                    }
                } catch (Exception e) {
                    index++;
                    status.setError(RaftError.UNKNOWN, e.toString());
                    if (closure != null) {
                        closure.setThrowable(e);
                    }
                    throw e;
                } finally {
                    if (closure != null) {
                        closure.run(status);
                    }
                }
                applied++;
                index++;
                iterator.next();
            }
        } catch (Exception e) {
            log.error("State machine execute error.", e);
            Status status = new Status(RaftError.ESTATEMACHINE, String.format("State machine execute error: %s", e.getMessage()));
            iterator.setErrorAndRollback(index - applied, status);
        }
    }

    @Override
    public void onSnapshotSave(SnapshotWriter writer, Closure done) {
        Status status = Status.OK();
        try {
            requestProcessor.snapshotOperation().save(writer);
        } catch (Exception e) {
            log.error("State machine save snapshot error.", e);
            status.setError(RaftError.ESTATEMACHINE, String.format("State machine save snapshot error: %s", e.getMessage()));
        }
        done.run(status);
    }

    @Override
    public boolean onSnapshotLoad(SnapshotReader reader) {
        boolean success = true;
        try {
            requestProcessor.snapshotOperation().load(reader);
        } catch (Exception e) {
            log.error("State machine load snapshot error.", e);
            success = false;
        }
        return success;
    }

    @Override
    public void onLeaderStart(long term) {
        this.leaderTerm.set(term);
        super.onLeaderStart(term);
    }

    @Override
    public void onLeaderStop(Status status) {
        this.leaderTerm.set(-1);
        super.onLeaderStop(status);
    }

    public boolean isLeader() {
        return leaderTerm.get() > 0;
    }
}
