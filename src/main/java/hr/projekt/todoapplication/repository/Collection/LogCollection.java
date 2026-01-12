package hr.projekt.todoapplication.repository.Collection;

import hr.projekt.todoapplication.repository.LogEntry;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "logs")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogCollection implements Serializable {
    @XmlElement(name = "logEntry")
    private List<LogEntry> logs = new ArrayList<>();

    public LogCollection() {}

    public List<LogEntry> getLogEntries() {
        return logs;
    }

    public void setEntries(List<LogEntry> logs) {
        this.logs = logs;
    }

    public void addEntry(LogEntry log) {
        this.logs.add(log);
    }
}
