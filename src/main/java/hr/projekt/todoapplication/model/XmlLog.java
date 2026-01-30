package hr.projekt.todoapplication.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@XmlRootElement(name="action")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlLog {
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private String action;
    private String timestamp;

    public XmlLog() {
    }

    public XmlLog(String action) {
        this.action = action;
        this.timestamp = LocalDateTime.now().format(DTF);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
