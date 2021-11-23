package Common.MaterialDesignComponents;

import javafx.animation.*;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class MDBase {
    private Circle circleRipple;
    private Rectangle rippleClip;

    private Duration rippleDuration;

    private double lastRippleHeight = 0;
    private double lastRippleWidth = 0;

    private Control control;

    private Color rippleColor;

    public MDBase(Control ctrl){
        control = ctrl;
        rippleClip = new Rectangle();
        rippleDuration =  Duration.millis(350);

        rippleColor = new Color(0, 0, 0, 0.11);
        createRippleEffect();
    }


    private void createRippleEffect() {
        circleRipple = new Circle(0.1, rippleColor);
        circleRipple.setOpacity(0.0);
        // Optional box blur on ripple - smoother ripple effect
//        circleRipple.setEffect(new BoxBlur(3, 3, 2));

        // Fade effect bit longer to show edges on the end
        final FadeTransition fadeTransition = new FadeTransition(rippleDuration, circleRipple);
        fadeTransition.setInterpolator(Interpolator.EASE_OUT);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);

        final Timeline scaleRippleTimeline = new Timeline();

        final SequentialTransition parallelTransition = new SequentialTransition();
        parallelTransition.getChildren().addAll(
                scaleRippleTimeline,
                fadeTransition
        );

        parallelTransition.setOnFinished(event1 -> {
            circleRipple.setOpacity(0.0);
            circleRipple.setRadius(0.1);
        });

        control.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            parallelTransition.stop();
            parallelTransition.getOnFinished().handle(null);

            circleRipple.setCenterX(event.getX());
            circleRipple.setCenterY(event.getY());

            // Recalculate ripple size if size of button from last time was changed
            if (control.getWidth() != lastRippleWidth || control.getHeight() != lastRippleHeight)
            {
                lastRippleWidth = control.getWidth();
                lastRippleHeight = control.getHeight();

                rippleClip.setWidth(lastRippleWidth);
                rippleClip.setHeight(lastRippleHeight);

                try {
                    rippleClip.setArcHeight(control.getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius());
                    rippleClip.setArcWidth(control.getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius());
                    circleRipple.setClip(rippleClip);
                } catch (Exception e) {

                }

                // Getting 45% of longest button's length, because we want edge of ripple effect always visible! Changed to 100%
                double circleRippleRadius = Math.max(control.getHeight(), control.getWidth()) * 1;
                final KeyValue keyValue = new KeyValue(circleRipple.radiusProperty(), circleRippleRadius, Interpolator.EASE_BOTH);
                final KeyFrame keyFrame = new KeyFrame(rippleDuration, keyValue);
                scaleRippleTimeline.getKeyFrames().clear();
                scaleRippleTimeline.getKeyFrames().add(keyFrame);
            }

            parallelTransition.playFromStart();
        });
    }

    public Circle getCircleRipple(){
        return circleRipple;
    }

    public void setRippleColor(Color color) {
        circleRipple.setFill(color);
    }

}
