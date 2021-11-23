package Extractor;

import Manager.Notification.NotificationManager;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * SVGExtractor est une classe qui permet d'extraire un SVGPath a partir d'un ficher SVG.
 * @author paull
 * @version 0.1
 */
public class SVGExtractor {

    /**
     * Fonction qui prend un chemin de fichier en argument et qui va extraire un SVGPath d'apres ce fichier.
     * @param uri Le chemin vers le ficher SVG
     * @return Le SVGPath extrait du fichier.
     */
    public static SVGPath GetSVGPath(String uri){
        SVGPath ico = new SVGPath();
        String path = "";
        String fill;

        Path filePath = Paths.get(uri);
        InputStream is = SVGExtractor.class.getResourceAsStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;

        boolean started = false;

        try{
            while ((line = reader.readLine()) != null){
                if (started){
                    path += line;
                    if (line.contains("/>")){
                        started = false;
                    }
                } else{
                    if (line.contains("<path")){
                        started = true;
                        path += line;
                    }
                }
            }
            fill = path.substring(path.indexOf("fill=\"") + 6, path.indexOf("\" d=\""));
            path = path.substring(path.indexOf(" d=\"") + 4, path.indexOf("\"/>"));
            path = path.replaceAll("\t", "");
            ico.setContent(path);
            ico.setFill(Paint.valueOf(fill));
        } catch (IOException e){
            e.printStackTrace();
            NotificationManager.pop("Fichier Manquant", "Le fichier " + filePath + " est introuvable.", "error");
            System.out.println("Erreur IO");
        }
        return ico;
    }
}
