package Common;

import javafx.animation.Transition;
import javafx.stage.Popup;
import javafx.util.Duration;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class PopupTranslateTransition extends Transition {
    private Popup       popup;
    private Duration    duration;
    private boolean     startX;
    private boolean     startY;
    private boolean     onX;
    private boolean     onY;
    private double      fromX;
    private double      toX;
    private double      fromY;
    private double      toY;
    private double      difX;
    private double      difY;

    public PopupTranslateTransition(Duration d, Popup p){
        popup = p;
        duration = d;
        onX = false;
        onY = false;
        startX = false;
        startY = false;
        fromX = 0;
        toX = 0;
        fromY = 0;
        toY = 0;
        difX = 0;
        difY = 0;
        setCycleDuration(d);
    }

    public Popup getPopup() {
        return popup;
    }

    public void setPopup(Popup p) {
        this.popup = p;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration d) {
        this.duration = d;
    }

    public double getFromX() {
        return fromX;
    }

    public void setFromX(double start){
        fromX = start;
        startX = true;
    }

    public double getFromY() {
        return fromY;
    }

    public void setFromY(double v) {
        this.fromY = v;
        startY = true;
    }

    public double getToX() {
        return toX;
    }

    public void setToX(double v) {
        this.toX = v;
        if (startX){
            onX = true;
            difX = toX - fromX;
        }
    }

    public double getToY() {
        return toY;
    }

    public void setToY(double v) {
        this.toY = v;
        if (startY){
            onY = true;
            difY = toY - fromY;
        }
    }

    @Override
    protected void interpolate(double frac) {
        if (onX){
            popup.setX(fromX + difX * frac);
        }
        if (onY){
            popup.setY(fromY + difY * frac);
        }
    }
}
