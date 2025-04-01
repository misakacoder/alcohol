package com.bourbon.vermouth;

import com.bourbon.service.BourbonService;
import com.bourbon.vermouth.entity.ConfigPublisher;
import com.vermouth.entity.Request;
import com.vermouth.entity.Response;
import com.vermouth.persistence.MemoryRepository;
import com.vermouth.persistence.Repository;
import com.vermouth.processor.RequestProcessor;
import com.vermouth.serializer.SerializerFactory;
import com.vermouth.snapshot.RaftSnapshotOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfigPublisherRequestProcessor implements RequestProcessor {

    public static final String GROUP = "bourbon";

    private final Repository<String, ConfigPublisher> repository = new MemoryRepository<>();

    private final RaftSnapshotOperation snapshotOperation = new RaftSnapshotOperation(repository);

    @Autowired
    private BourbonService bourbonService;

    @Override
    public Response process(Request request) {
        byte[] data = request.getData().toByteArray();
        if (data != null && data.length > 0) {
            ConfigPublisher configPublisher = SerializerFactory.get().deserialize(data, ConfigPublisher.class);
            bourbonService.publish(configPublisher.getFilename(), configPublisher.getAppName(), configPublisher.getProfile());
        }
        return Response.newBuilder().build();
    }

    @Override
    public String group() {
        return GROUP;
    }

    @Override
    public RaftSnapshotOperation snapshotOperation() {
        return snapshotOperation;
    }
}
