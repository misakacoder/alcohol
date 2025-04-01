package com.vermouth.processor;

import com.vermouth.entity.Request;
import com.vermouth.entity.Response;
import com.vermouth.snapshot.RaftSnapshotOperation;

public interface RequestProcessor {

    Response process(Request request);

    String group();

    RaftSnapshotOperation snapshotOperation();
}
