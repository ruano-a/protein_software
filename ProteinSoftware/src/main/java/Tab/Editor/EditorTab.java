package Tab.Editor;

import Manager.Popup.PopupManager;
import Manager.Workspace.WorkspaceTab;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.fxmisc.richtext.*;
import org.fxmisc.wellbehaved.event.EventHandlerHelper;
import Protein.Core;

import java.io.File;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javafx.scene.input.KeyCode.*;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class EditorTab extends WorkspaceTab{
    private String LANGUE = "java";
    private List<String> GROUPS = new ArrayList<String>(){};

    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_OPEN_PATTERN = "\\{";
    private static final String BRACE_CLOSE_PATTERN = "\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String SL_COMMENT_PATTERN = "//[^\n]*";
    private static final String ML_COMMENT_PATTERN = "/\\*(.|\\R)*?\\*/";
    private static final String FUNCTION_PATTERN = 	"(?ms)(?<!new)(?<=^[\\s\\w|<|>|\\[|\\]]*)\\b[ |\\t]*\\w*\\b(?=[ |\\t]*\\(.*\\))";
    private static final String HTML_PATTERN = "<[\\/]{0,1}\\W*(?'tag'\\w+)|(?'key'\\w+)(?=\\s*\\=)|(?'string'(\".*\")|('.*'))";
    private static final String USER_TYPE = "(?s)(?<=[ |\\t|<|\\(])[A-Z]\\w*\\b(?=.*$)";
    private static Pattern      PATTERN;

    private static char enders[] = {'\n', ' ', '\t', ',', '.', '(', ')', '{', '}', '[', ']', ';', ':', '"', '\'', '\0'};

    private MyCodeArea codeArea;
    private static VBox popupContent;
    private static Popup popup;
    private static List<String> words;
    private static int focused;
    private String wordToFind;
    private boolean findActive;

    private ExecutorService executor;

    private ObservableList<Word> _observableWordsFinded;

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

    //TODO Mettre au propre !
    //TODO Finir PATTERN pour fonction et vérifier tout les autres.

    public EditorTab(File f){
        super(f);
        this.isEditor = true;
        this.codeArea = new MyCodeArea();
        if (popupContent == null){
            popupContent = new VBox();
            popupContent.setPrefWidth(200);
        }
        if (popup == null){
            popup = PopupManager.getPopup();
            popup.getContent().add(popupContent);
        }
        if (words == null){
            words = new ArrayList<>();
        }

        this.findActive = false;

        Core.getDatabaseManager().loadDictionary(LANGUE);

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        popupContent.getStyleClass().add("atcp-popup");
        initCodeArea();
        codeArea.getStylesheets().add("/Styles/"+ LANGUE +".css");
        this.setContent(this.codeArea);
    }

    public void setText(String t){
        codeArea.insertText(0, t);
        setModified(false);
    }

    public void appendText(String t){
        codeArea.paste();
    }

    public String getText(){
        return codeArea.getText();
    }


    private String getWordPattern(){
        ResultSet groupSet = Core.getDatabaseManager().get("SELECT type FROM keyword WHERE lang == '"+ LANGUE +"' GROUP BY type;");
        ResultSet wordSet;
        String res = "";
        try {
            while (groupSet.next()){
                GROUPS.add(groupSet.getString("type"));
            }
            groupSet.close();
            for (String gr : GROUPS){
                res += "(?<" + gr.toUpperCase() + ">\\b(";
                wordSet = Core.getDatabaseManager().get("SELECT word FROM keyword WHERE lang == '"+ LANGUE +"' AND type == '" + gr + "';");
                while (wordSet.next()){
                    res += wordSet.getString("word") + "|";
                }
                if (gr.equalsIgnoreCase("type")){
                    res += USER_TYPE + "|";
                }
                res = res.substring(0, res.length() - 1) + ")\\b)|";
                wordSet.close();
            }
            res = res.substring(0, res.length() - 1);
        } catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return res;
    }

    //Fonctions de test pour auto-completion
    private boolean isEnder(char c){
        for (int j = 0; j < enders.length; j++){
            if (c == enders[j]){
                return true;
            }
        }
        return false;
    }

    private String getWord(String prg, int x){
        String word = "";
        boolean loop = true;
        int start = 0;
        int end = 0;

        for (int i = x - 1; i >= 0 && i < prg.length() && loop; i--){
            char tmp = prg.charAt(i);
            if (isEnder(tmp)){
                loop = false;
                start = i + 1;
            }
        }
        loop = true;
        for (int i = start; i < prg.length() && loop; i++){
            char tmp = prg.charAt(i);
            if (isEnder(tmp)){
                end = i;
                loop = false;
            }
        }
        if (end == 0 && prg.length() > 0 && start < prg.length() && !isEnder(prg.charAt(start))){
            end = prg.length();
        }
        if (end > start && start >= 0 && start <= prg.length() && end >= 0 && end <= prg.length())
            word = prg.substring(start, end);
        return word;
    }

    private String getWordAfterCaret(String prg, int x){
        String word = "";
        boolean loop = true;
        int start = x;
        int end = 0;

        for (int i = start; i < prg.length() && loop; i++){
            char tmp = prg.charAt(i);
            if (isEnder(tmp)){
                end = i;
                loop = false;
            }
        }
        if (end == 0 && prg.length() > 0 && start < prg.length() && !isEnder(prg.charAt(start))){
            end = prg.length();
        }
        if (end > start && start >= 0 && start <= prg.length() && end >= 0 && end <= prg.length())
            word = prg.substring(start, end);
        return word;
    }


    private void removeLastWord(){
        caretChange();
    }

    private String getStringToAdd(String word, String complete){
        String res = complete.substring(word.length());
        return res;
    }

    private void caretChange(){
        int x = codeArea.getCaretColumn();
        int par = codeArea.getCurrentParagraph();
        String prg = codeArea.getParagraph(par).toString();
        String word = getWord(prg, x);
        if (!word.isEmpty()){
            populatePopup(word);
        }
        if ((word.isEmpty() || popupContent.getChildren().size() == 0) && popup.isShowing()){
            popup.hide();
        }
        else if (!word.isEmpty() && popupContent.getChildren().size() > 0 &&  !popup.isShowing()){
            popup.show(Core.getWindow());
            focused = -1;
        }
    }

    private void populatePopup(String word){
        int x = codeArea.getCaretColumn();
        String prg = codeArea.getParagraph(codeArea.getCurrentParagraph()).toString();
        String tmpword = getWordAfterCaret(prg, x);
        popupContent.getChildren().clear();
        words.clear();
        ResultSet rs = Core.getDatabaseManager().get("SELECT word FROM keyword WHERE lang == '"+ LANGUE +"' AND word LIKE '"  + word.replaceAll("_", "\\\\_") + "%' ESCAPE '\\' AND word != '"+ word +"' ORDER BY LENGTH(word) ASC;");
        try {
            while(rs.next()){
                String newWord = rs.getString("word");
                Button tmp = new Button(newWord);
                words.add(tmp.getText());
                tmp.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        setFocused(words.indexOf(newWord));
                    }
                });
                tmp.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        codeArea.insertText(codeArea.getCaretPosition() + tmpword.length(), getStringToAdd(word, newWord));
                    }
                });
                tmp.setMnemonicParsing(false);
                tmp.setMaxWidth(Integer.MAX_VALUE);
                popupContent.getChildren().add(tmp);
            }
        } catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    private void setFocused(int e){
        if (focused >= 0 && focused < popupContent.getChildren().size()){
            popupContent.getChildren().get(focused).getStyleClass().remove("active");
        }
        focused = e;
        if (e >= words.size()){
            e = 0;
        }
        else if (e < 0){
            e = words.size() - 1;
        }
        focused = e;
        if (popupContent.getChildren().size() > 0 && focused < popupContent.getChildren().size()){
            popupContent.getChildren().get(focused).getStyleClass().add("active");
        }
    }

    private void handlePopupKey(KeyEvent event){
        switch (event.getCode()){
            case TAB:
                if (words.size() == 1 && focused == 0){
                    ((Button) popupContent.getChildren().get(0)).getOnAction().handle(new ActionEvent());
                }
                else {
                    setFocused(focused + 1);
                }
                break;
            case DOWN:
                setFocused(focused + 1);
                break;
            case UP:
                setFocused(focused - 1);
                break;
            case ENTER:
                if (focused >= 0){
                    ((Button) popupContent.getChildren().get(focused)).getOnAction().handle(new ActionEvent());
                }
                else {
                    codeArea.replaceSelection("\n");
                }
                break;
            default:
                break;
        }
    }
    //Fins de fonctions de tests

    private void initAutoCompletion(){
        codeArea.setPopupWindow(popup);
        codeArea.setPopupAlignment(PopupAlignment.SELECTION_BOTTOM_CENTER);
        codeArea.setPopupAnchorOffset(new Point2D(4, 4));
        codeArea.caretPositionProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                caretChange();
            }
        });
    }

    private void initCodeArea(){
        initAutoCompletion();
        executor = Executors.newSingleThreadExecutor();

        //TODO Mettre ça dans une fonction ou une classe.
        try{
            String Res = getWordPattern();
            Res += "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACEOPEN>" + BRACE_OPEN_PATTERN + ")"
                    + "|(?<BRACECLOSE>" + BRACE_CLOSE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<SINGLELINECOMMENT>" + SL_COMMENT_PATTERN + ")"
                    + "|(?<MULTILINECOMMENT>" + ML_COMMENT_PATTERN + ")"
                    + "|(?<FUNCTION>" + FUNCTION_PATTERN + ")";
            System.out.println(Res);
            GROUPS.add("function");
            GROUPS.add("multilinecomment");
            GROUPS.add("singlelinecomment");
            GROUPS.add("string");

            PATTERN = Pattern.compile(Res);

        }
        catch (Exception e){
            System.out.println(e.toString());
        }

        codeArea.richChanges().subscribe(change -> {
            if (!modified){
                this.setModified(true);
            }
            if (findActive && _observableWordsFinded != null){
                _observableWordsFinded.clear();
                computeFind(this.wordToFind, codeArea.getText());
            }
//            codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
        });

        EventHandler<? super KeyEvent> tabHandler = EventHandlerHelper
                .on(keyPressed(TAB)).act(event -> codeArea.replaceSelection("   ")).create();
        EventHandler<? super KeyEvent> ppTabHandler = EventHandlerHelper
                .on(keyPressed(TAB)).act(event -> handlePopupKey(event))
                .create();
        EventHandler<? super KeyEvent> downHandler = EventHandlerHelper
                .on(keyPressed(DOWN)).act(event -> handlePopupKey(event))
                .create();
        EventHandler<? super KeyEvent> upHandler = EventHandlerHelper
                .on(keyPressed(UP)).act(event -> handlePopupKey(event))
                .create();
        EventHandler<? super KeyEvent> ppEnterHandler = EventHandlerHelper
                .on(keyPressed(ENTER)).act(event -> handlePopupKey(event))
                .create();

        KeyCombination ctrlsupr = new KeyCodeCombination(KeyCode.BACK_SPACE, KeyCombination.CONTROL_DOWN);

        codeArea.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (ctrlsupr.match(event)){
                removeLastWord();
            }
        });

        EventHandlerHelper.install(codeArea.onKeyPressedProperty(), tabHandler);
        EventHandlerHelper.install(popupContent.onKeyPressedProperty(), ppTabHandler);
        EventHandlerHelper.install(popupContent.onKeyPressedProperty(), downHandler);
        EventHandlerHelper.install(popupContent.onKeyPressedProperty(), upHandler);
        EventHandlerHelper.install(popupContent.onKeyPressedProperty(), ppEnterHandler);
    }

    public void setWordToFind(String text){
        this.wordToFind = text;
        populateWordList(text);
    }

    public void set_observableWordsFinded(ObservableList<Word> list){
        this._observableWordsFinded = list;
    }

    public void populateWordList(String toFind){
        this._observableWordsFinded.clear();
        if (toFind != null && toFind.length() > 0){
            this.wordToFind = toFind;
            computeFind(toFind, this.codeArea.getText());
        }
    }

    public ObservableList<Word> getWordList(String toFind){
        _observableWordsFinded.clear();
        if (toFind != null && toFind.length() > 0){
            this.wordToFind = toFind;
            computeFind(toFind, this.codeArea.getText());
        }
        return _observableWordsFinded;
    }

    public void setCaretOnPos(int pos){
        this.codeArea.moveTo(pos);
    }

    public void replaceWord(Word w, String text){
        this.codeArea.replaceText(w.start, w.end, text);
    }

    private void computeFind(String toFind, String text){
        Pattern findPattern = Pattern.compile(toFind);
        Matcher matcher = findPattern.matcher(text);

        while (matcher.find()){
            _observableWordsFinded.add(new Word(matcher.start(), matcher.end(), codeArea.offsetToPosition(matcher.start(), TwoDimensional.Bias.Forward)));
        }
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        codeArea.setStyleSpans(0, highlighting);
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass = null;
            int i = 0;
            while (styleClass == null && i < GROUPS.size()) {
                styleClass = matcher.group(GROUPS.get(i).toUpperCase()) != null ? GROUPS.get(i) : null;
                i++;
            }
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public boolean isFindActive() {
        return findActive;
    }

    public void setFindActive(boolean value) {
        this.findActive = value;
    }
}
