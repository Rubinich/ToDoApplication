package hr.projekt.todoapplication.util;

import hr.projekt.todoapplication.model.XmlLog;
import hr.projekt.todoapplication.repository.collection.XmlCollection;
import hr.projekt.todoapplication.repository.storage.XmlStorage;

import java.io.IOException;
import java.util.Optional;

public class XmlLogger {
    private static XmlLogger instance;
    private final XmlStorage<XmlCollection> storage;

    private XmlLogger() {
        this.storage = new XmlStorage<>(XmlCollection.class);
    }

    public static XmlLogger getInstance() {
        if(Optional.ofNullable(instance).isEmpty())
            instance = new XmlLogger();
        return instance;
    }

    public void log(String des) {
        Thread.startVirtualThread(() -> {
            try{
                XmlCollection collection = storage.read(null).orElse(new XmlCollection());
                XmlLog log = new XmlLog(des);

                collection.addLog(log);
                storage.write(collection);

            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
