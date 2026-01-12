package hr.projekt.todoapplication.repository;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDateTime;

@XmlRootElement(name = "logEntry")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogEntry implements Serializable {
    @XmlElement(required = true)
    private String time;
    @XmlElement(required = true)
    private String username;
    @XmlElement(required = true)
    private String action;
    @XmlElement(required = true)
    private String details;

    public LogEntry() {}

    public LogEntry(String username, String action, String details) {
        this.time = LocalDateTime.now().toString();
        this.username = username;
        this.action = action;
        this.details = details;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s %s", time, username, action, details != null  && !details.isEmpty() ? "(" + details + ")" : "");
    }
}
