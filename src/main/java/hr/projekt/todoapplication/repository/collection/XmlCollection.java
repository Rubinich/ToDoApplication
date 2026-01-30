package hr.projekt.todoapplication.repository.collection;

import hr.projekt.todoapplication.model.XmlLog;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="userLogs")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlCollection {
    private List<XmlLog> logs = new ArrayList<>();

    public void addLog(XmlLog log) {
        this.logs.add(log);
    }

    public List<XmlLog> getLogs() {
        return logs;
    }

    public void setLogs(List<XmlLog> logs) {
        this.logs = logs;
    }
}
