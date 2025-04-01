package com.vermouth.serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class HessianSerializer implements Serializer {

    @Override
    public byte[] serialize(Serializable object) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Hessian2Output ho = new Hessian2Output(bos);
            ho.writeObject(object);
            ho.close();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] data) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
            Hessian2Input hi = new Hessian2Input(bis);
            Object object = hi.readObject();
            hi.close();
            return (T) object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        return cls.cast(deserialize(data));
    }
}
