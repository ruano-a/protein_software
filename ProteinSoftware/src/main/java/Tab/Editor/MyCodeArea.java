package Tab.Editor;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.TwoDimensional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class MyCodeArea extends CodeArea {

    public class Word{
        public int start;
        public int end;
        public int size;
        public TwoDimensional.Position pos;

        public Word(int s, int e, TwoDimensional.Position p){
            this.start = s;
            this.end = e;
            this.size = e - s;
            this.pos = p;
        }

        @Override
        public String
        toString() {
            return "Word{" +
                    "start=" + start +
                    ", end=" + end +
                    ", size=" + size +
                    '}';
        }
    }

    private StringProperty findWord;
    private ObservableList<Word> wordsFinded;

    public MyCodeArea(){
        super();
        this.findWord = new SimpleStringProperty();
        this.wordsFinded = null;
        this.findWord.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (wordsFinded != null && newValue != null && !newValue.isEmpty()) {
                    wordsFinded.clear();
                    computeFind(getText());
                }
            }
        });
        this.richChanges().subscribe(change -> {
            if (wordsFinded != null && findWord.get() != null && !findWord.get().isEmpty()){
                wordsFinded.clear();
                computeFind(getText());
            }
//            setStyleSpans(0, computeHighlighting(codeArea.getText()));
        });

    }

    @Override
    public void copy() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        if (this.getSelectedText() != null && !this.getSelectedText().equals("")){
            content.putString(this.getSelectedText());
        } else {
            content.putString(this.getText(this.getCurrentParagraph()));
        }
        clipboard.setContent(content);
    }

    @Override
    public void cut() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        if (this.getSelectedText() != null && !this.getSelectedText().equals("")){
            content.putString(this.getSelectedText());
        } else {
            content.putString(this.getText(this.getCurrentParagraph()));
            this.selectLine();
        }
        this.replaceSelection("");
        clipboard.setContent(content);
    }

    @Override
    public void paste() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String content = clipboard.getString();
        this.replaceSelection(content);
    }

    public void setCaretOnPos(int pos){
        this.moveTo(pos);
    }

    public void replaceWord(Word w, String text){
        this.replaceText(w.start, w.end, text);
    }



    public void find(StringProperty word, ObservableList<Word> wordList){
        this.findWord.unbind();
        if (word != null) {
            this.findWord.bindBidirectional(word);
        }
        this.wordsFinded = wordList;
        if (this.wordsFinded != null && this.findWord != null && !this.findWord.get().isEmpty()){
            this.wordsFinded.clear();
            this.computeFind(this.getText());
        }
    }

    private void computeFind(String text){
        Pattern findPattern = Pattern.compile(this.findWord.get());
        Matcher matcher = findPattern.matcher(text);

        while (matcher.find()){
            wordsFinded.add(new Word(matcher.start(), matcher.end(), offsetToPosition(matcher.start(), TwoDimensional.Bias.Forward)));
        }
    }

}
