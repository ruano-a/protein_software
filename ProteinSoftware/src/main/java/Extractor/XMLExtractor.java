package Extractor;

import Manager.Notification.NotificationManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class XMLExtractor {
    public static String getQueryFromXML(String filepath){
        String res = null;
        try {
            File inputFile = new File(filepath);
            if (inputFile.exists()){
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(inputFile);
                doc.getDocumentElement().normalize();
                Element e = (Element) doc.getFirstChild();
                if (e.getAttribute("type").equals("lang")){
                    res = getLangQueryFromXML(e);
                } else if (e.getAttribute("type").equals("lang_utils")){
                    res = getLangUtilsFromXML(e);
                }
            }
            else {
                System.err.println("[getQueryFromXML] Manager.File doesn't exist and can't be loaded: " + filepath + ".");
                NotificationManager.pop("[getQueryFromXML] Error", "Manager.File doesn't exist and can't be loaded: " + filepath + ".", "error");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static String getLangQueryFromXML(Element rootElement){
        String res = "INSERT INTO keyword (word, lang, type) VALUES";
        try {
            boolean first = true;
            NodeList groupList, wordList;

            String group, lang, word;
            int groupLen, wordLen;

            lang = rootElement.getAttribute("lang");

            groupList = rootElement.getElementsByTagName("Group");
            groupLen = groupList.getLength();
            for (int k = 0; k < groupLen; k++){
                Element XMLGroup = (Element) groupList.item(k);
                group = XMLGroup.getAttribute("name");

                wordList = XMLGroup.getElementsByTagName("word");
                wordLen = wordList.getLength();
                for (int l = 0; l < wordLen; l++){
                    Element XMLWord = (Element) wordList.item(l);
                    word = XMLWord.getTextContent();
                    if (!first){
                        res += ",";
                    } else {
                        first = false;
                    }
                    String query = " ('" + word + "', '" + lang + "', '" + group + "')";
                    res += query;
                }
            }
            res += ";";
        } catch (Exception e){
            res = null;
            e.printStackTrace();
        }
        return res;
    }

    public static String getLangUtilsFromXML(Element rootElement) {
        String res = "INSERT INTO lang_utils (code, lang, sentence) VALUES";
        try {
            boolean first = true;
            NodeList groupList, sentenceList;
            String code, lang, sentence;
            int groupLen, sentenceLen;

            groupList = rootElement.getElementsByTagName("Group");
            groupLen = groupList.getLength();
            for (int k = 0; k < groupLen; k++) {
                Element XMLGroup = (Element) groupList.item(k);

                lang = XMLGroup.getAttribute("name");

                sentenceList = XMLGroup.getElementsByTagName("sentence");
                sentenceLen = sentenceList.getLength();
                for (int l = 0; l < sentenceLen; l++) {
                    Element XMLSentence = (Element) sentenceList.item(l);
                    code = XMLSentence.getAttribute("code");
                    sentence = XMLSentence.getTextContent();
                    if (!first){
                        res += ",";
                    } else {
                        first = false;
                    }
                    String query = " ('" + code + "', '" + lang + "', \"" + sentence + "\")";
                    res += query;
                }
            }
            res += ";";
        } catch (Exception e) {
            res = null;
            e.printStackTrace();
        }
        return res;
    }

}
