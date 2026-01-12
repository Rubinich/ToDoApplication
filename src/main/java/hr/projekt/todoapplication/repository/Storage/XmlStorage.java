package hr.projekt.todoapplication.repository.Storage;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class XmlStorage<T> implements Storage<T> {
    private static final Logger log = LoggerFactory.getLogger(XmlStorage.class);
    private static final Path ACTIVITY_FILE = Path.of("data/activity.log");

    private final Class<T> type;
    private final JAXBContext context;

    public XmlStorage(Class<T> type) {
        this.type = type;
        try{
            this.context = JAXBContext.newInstance(type);
        } catch (JAXBException e) {
            log.error("Greška pri inicijalizaciji JAXB konteksta za {}", type.getName(), e);
            throw new RuntimeException("Nije moguće kreirati JAXB context", e);
        }
    }

    @Override
    public void write(Path path, T object) throws IOException {
        try{
            if(path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(object, path.toFile());
            log.debug("XML uspješno spremljen: {}", path);
        } catch (JAXBException e) {
            log.error("Greška pri spremanju XML-a u {}", path, e);
            throw new IOException("Nije moguće spremiti XML", e);
        }
    }

    @Override
    public Optional<T> read(Path path) throws IOException {
        if(!Files.exists(path)) {
            log.debug("XML datoteka ne postoji");
            return Optional.empty();
        }
        try{
            Unmarshaller unmarshaller = context.createUnmarshaller();
            T object = (T) unmarshaller.unmarshal(path.toFile());
            log.debug("XML uspješno učitan: {}", path);
            return Optional.of(object);
        } catch (JAXBException e) {
            log.error("Greška pri čitanju XML-a iz {}", path, e);
            throw new IOException("Nije moguće učitati XML", e);
        }
    }
}
