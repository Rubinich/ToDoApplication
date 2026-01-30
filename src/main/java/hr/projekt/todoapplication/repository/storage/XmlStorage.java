package hr.projekt.todoapplication.repository.storage;

import hr.projekt.todoapplication.exceptions.StorageException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class XmlStorage<T> implements Storage<T> {
    private static final Path path = Path.of("data/logger.xml");
    private final Class<T> type;

    public XmlStorage(Class<T> type){
        this.type = type;
    }

    public void write(T object) throws IOException {
        write(path, object);
    }

    @Override
    public void write(Path path, T object) throws IOException {
        try{
            if(!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            JAXBContext context = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(object , path.toFile());

        } catch (JAXBException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public Optional<T> read(Path usersFile) throws IOException, ClassNotFoundException {
        try {
            if(!Files.exists(path)){
                return Optional.empty();
            }
            JAXBContext context = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            T object = (T) unmarshaller.unmarshal(path.toFile());
            return Optional.of(object);

        } catch (JAXBException e) {
            throw new StorageException(e);
        }
    }
}
