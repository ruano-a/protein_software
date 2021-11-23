package Tab.Editor;

import API.Interfaces.IWorkspace;
import Manager.Popup.PopupManager;
import Manager.Workspacetmp.Tab;
import Protein.Core;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.PopupAlignment;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.fxmisc.wellbehaved.event.EventHandlerHelper;

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
public class CodeTab extends Tab {

    private MyCodeArea  codeArea;
    private ContextMenu codeAreaContextMenu;

    private StringProperty LANGUE = new SimpleStringProperty();
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
    private static final String USER_TYPE = "(?s)(?<=[ |\\t|<|\\(])[A-Z]\\w*\\b(?=.*$)";

    private static final String HTML_PATTERN = "<[\\/]{0,1}\\W*(?'tag'\\w+)|(?'key'\\w+)(?=\\s*\\=)|(?'string'(\".*\")|('.*'))";
    private static final String HTML_TAG = "<[\\/]{0,1}\\W*(?<TAG>\\w+)";
    private static final String HTML_KEY = "(?<key>\\w+)(?=\\s*=)";

    private Pattern PATTERN;

    private static char enders[] = {'\n', ' ', '\t', ',', '.', '(', ')', '{', '}', '[', ']', ';', ':', '"', '\'', '\0'};

    private static VBox popupContent;
    private static Popup popup;
    private static List<String> words;
    private static int focused;

    private static ExecutorService executor;

    public CodeTab(IWorkspace workspace){
        super(workspace);
        this.init();
        this.setLanguageFromFile();
    }

    public CodeTab(IWorkspace workspace, String title){
        super(workspace, title, null);
        this.init();
        this.setLanguageFromFile();
    }

    public CodeTab(IWorkspace workspace, File file){
        super(workspace, file, null);
        this.init();
        this.openFile(file);
        this.setLanguageFromFile();
    }

    public void openFile(File file){
        if (file != null && !file.isDirectory()){
            this.file = file;
            this.codeArea.appendText(Core.getFileManager().getFileContent(file));
        }
    }

    private void init(){
        this.codeArea = new MyCodeArea();
        this.setContent(this.codeArea);
        this.codeAreaContextMenu = new ContextMenu();
        this.codeAreaContextMenu.getItems().addAll(this.workspace.getController().getContextMenu());
        this.codeArea.setContextMenu(this.codeAreaContextMenu);

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
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        popupContent.getStyleClass().add("atcp-popup");
        LANGUE.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (oldValue != null && !oldValue.isEmpty()) {
                    codeArea.getStylesheets().remove("/Styles/"+ oldValue +".css");
                }
                if (newValue != null && !newValue.isEmpty()) {
                    codeArea.getStylesheets().add("/Styles/"+ newValue +".css");
                    Core.getDatabaseManager().loadDictionary(newValue);
                    updatePattern();
                }
                System.out.println("LOG: "+newValue);
            }
        });
        initCodeArea();
    }

    private void setLanguageFromFile(){
        if (file != null){
            String path = file.getAbsolutePath();
            String ext = path.substring(path.lastIndexOf(".") + 1);
            switch (ext){
                case "cpp":
                    break;
                case "java":
                    break;
                case "js":
                    ext = "javascript";
                    break;
                case "php":
                    break;
                case "py":
                    ext = "python";
                    break;
                case "xml":
                    break;
                case "html":
                    break;
                default:
                    ext = "";
                    break;
            }
            LANGUE.set(ext);
        } else {
            LANGUE.set("java");
        }
    }

    private String getWordPattern(){
        ResultSet groupSet = Core.getDatabaseManager().get("SELECT type FROM keyword WHERE lang == '"+ LANGUE.get() +"' GROUP BY type;");
        ResultSet wordSet;
        String res = "";
        try {
            while (groupSet.next()){
                GROUPS.add(groupSet.getString("type"));
            }
            groupSet.close();
            for (String gr : GROUPS){
                res += "(?<" + gr.toUpperCase() + ">\\b(";
                wordSet = Core.getDatabaseManager().get("SELECT word FROM keyword WHERE lang == '"+ LANGUE.get() +"' AND type == '" + gr + "';");
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
        ResultSet rs = Core.getDatabaseManager().get("SELECT word FROM keyword WHERE lang == '"+ LANGUE.get() +"' AND word LIKE '"  + word.replaceAll("_", "\\\\_") + "%' ESCAPE '\\' AND word != '"+ word +"' ORDER BY LENGTH(word) ASC;");
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

    private void updatePattern(){
        try{
            GROUPS.clear();
            String Res = getWordPattern();
            if (!Res.isEmpty()){
                Res += "|";
            }
            if (!LANGUE.get().equals("html")){
                Res += "(?<PAREN>" + PAREN_PATTERN + ")"
                        + "|(?<BRACEOPEN>" + BRACE_OPEN_PATTERN + ")"
                        + "|(?<BRACECLOSE>" + BRACE_CLOSE_PATTERN + ")"
                        + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                        + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                        + "|(?<STRING>" + STRING_PATTERN + ")"
                        + "|(?<SINGLELINECOMMENT>" + SL_COMMENT_PATTERN + ")"
                        + "|(?<MULTILINECOMMENT>" + ML_COMMENT_PATTERN + ")"
                        + "|(?<FUNCTION>" + FUNCTION_PATTERN + ")";
                GROUPS.add("function");
                GROUPS.add("multilinecomment");
                GROUPS.add("singlelinecomment");
                GROUPS.add("string");
            }
            else {
                Res += HTML_KEY
                        + HTML_TAG;
                GROUPS.add("tag");
                GROUPS.add("key");
            }
            System.out.println(Res);

            PATTERN = Pattern.compile(Res);
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }

    private void initCodeArea(){
        initAutoCompletion();
        if (executor == null){
            executor = Executors.newSingleThreadExecutor();
        }
        //TODO Mettre ça dans une fonction ou une classe.
        updatePattern();
        codeArea.richChanges().subscribe(change -> {
//            codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
        });

        codeArea.richChanges()
            .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
            .successionEnds(Duration.ofMillis(500))
            .supplyTask(this::computeHighlightingAsync)
            .awaitLatest(codeArea.richChanges())
            .filterMap(t -> {
                if(t.isSuccess()) {
                    return Optional.of(t.get());
                } else {
                    t.getFailure().printStackTrace();
                    return Optional.empty();
                }
            })
            .subscribe(this::applyHighlighting);



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

    public String getText(){
        return this.codeArea.getText();
    }

    public static void stop(){
        if (executor != null){
            executor.shutdown();
        }
    }

    public static CodeTab create(IWorkspace workspace, String title){
        return new CodeTab(workspace, title);
    }
    public static CodeTab create(IWorkspace workspace, File file){
        return new CodeTab(workspace, file);
    }
}
