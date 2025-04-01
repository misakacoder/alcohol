package com.vermouth.snapshot;

import com.alipay.sofa.jraft.storage.snapshot.SnapshotReader;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotWriter;
import com.vermouth.persistence.Repository;

import java.io.*;

public class RaftSnapshotOperation {

    private static final String SNAPSHOT_NAME = "snapshot.data";

    private final Repository<?, ?> repository;

    public RaftSnapshotOperation(Repository<?, ?> repository) {
        this.repository = repository;
    }

    public void save(SnapshotWriter writer) {
        saveSnapshot(writer, repository.dump());
    }

    public boolean load(SnapshotReader reader) {
        repository.load(loadSnapshot(reader));
        return true;
    }

    protected void saveSnapshot(SnapshotWriter writer, Object object) {
        File snapshot = new File(writer.getPath(), SNAPSHOT_NAME);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(snapshot))) {
            oos.writeObject(object);
            writer.addFile(SNAPSHOT_NAME);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Object loadSnapshot(SnapshotReader reader) {
        File snapshot = new File(reader.getPath(), SNAPSHOT_NAME);
        if (snapshot.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(snapshot))) {
                return ois.readObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
