package Manager.File;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

import java.io.File;
import java.util.List;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class FileEvent extends Event {
    public static EventType<FileEvent> FILE_ANY = new EventType<>(ANY, "FILE-ANY");
    public static EventType<FileEvent> FILE_NEW = new EventType<>(FILE_ANY, "FILE_NEW");
    public static EventType<FileEvent> FILE_OPEN = new EventType<>(FILE_ANY, "FILE_OPEN");
    public static EventType<FileEvent> DIR_OPEN = new EventType<>(FILE_ANY, "DIR_OPEN");
    public static EventType<FileEvent> FILE_CLOSE = new EventType<>(FILE_ANY, "FILE_CLOSE");
    public static EventType<FileEvent> FILE_SAVED = new EventType<>(FILE_ANY, "FILE_SAVED");
    public static EventType<FileEvent> FILE_DELETED = new EventType<>(FILE_ANY, "FILE_DELETED");

    private File        file;
    private String      content;

    public FileEvent(EventType<FileEvent> evtType, File f){
        super(evtType);
        file = f;
    }

    public FileEvent(Object source, EventTarget target, EventType<FileEvent> eventType, File f){
        super(source, target, eventType);
    }

    public FileEvent(Object source, EventTarget target, EventType<FileEvent> eventType, List<File> fs){
        super(source, target, eventType);
    }

    public File getFile(){
        return file;
    }

    public void setContent(String c){
        content = c;
    }

    public String getContent(){
        return content;
    }

}
