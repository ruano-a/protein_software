package Manager.Notification;

import Common.PopupTranslateTransition;
import Manager.Popup.PopupManager;
import Manager.Popup.ProteinPopup;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class NotificationManager {
    private class Manager{
        private List<Notification>  notifications;
        private List<Notification>  waitingNotifs;
        private List<ProteinPopup>  popups;
        private int                 maxPopups;
        private float               notifTimeOut;
        private ParallelTransition  activeTransition;

        public Manager(){
            maxPopups = 4;
            popups = new ArrayList<>();
            waitingNotifs = new ArrayList<>();
            notifications = new ArrayList<>();
        }

        public void pop(String title, String message, String state){
            Notification n = new Notification(title, message, state);
            if (popups.size() < maxPopups){
                notifications.add(n);
                addPopup(n);
            }
            else {
                waitingNotifs.add(n);
            }
        }

        public void remove(Notification n){
            removePopup(n);
        }

        private void checkWatingNotifications(){
            if (popups.size() < maxPopups && waitingNotifs.size() > 0){
                Notification tmp = waitingNotifs.get(0);
                waitingNotifs.remove(tmp);
                notifications.add(tmp);
                addPopup(tmp);
            }
        }

        public void addPopup(Notification n){
            ProteinPopup p = PopupManager.getPopup();
            p.setHideOnEscape(false);
            p.getContent().add(n.getLayout());
            n.setPopup(p);

            popups.add(p);

            /* Get the display transitions */
            ParallelTransition pt = new ParallelTransition();
            pt.getChildren().addAll(getShowTransitions(n));

            /* Define the time that a notification is displayed */
            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.setOnFinished( event -> remove(n));
            n.setDelay(delay);

            /* When the displays animations are done launch the delay before the popup close */
            pt.setOnFinished(event -> delay.playFromStart());

            p.setOnShown(event -> {
                System.out.println("adding" + n.getTitle());
                /* Set the popup X and Y */
                p.setX(Screen.getPrimary().getVisualBounds().getMaxX() - p.getWidth());
                p.setY(computePopupY(p));

                /* Launch the animation */
                pt.playFromStart();
            });
            PopupManager.show(p);
        }

        public void removePopup(Notification n){
            System.out.println("Removing " + n.getTitle());
            n.getDelay().stop();
            ParallelTransition pt = new ParallelTransition();
            if (activeTransition != null){
                activeTransition.stop();
                activeTransition.getOnFinished().handle(new ActionEvent());
            }
            pt.getChildren().addAll(getHideTransitions(n));
            activeTransition = pt;
            pt.playFromStart();
            pt.setOnFinished(event -> {
                PopupManager.removePopup(n.getPopup());
                popups.remove(n.getPopup());
                checkWatingNotifications();
            });
        }

        private double computePopupY(Popup p){
            boolean loop = true;
            double pos = Screen.getPrimary().getVisualBounds().getMaxY() + 4;
            for (int i = 0; i < popups.size() && loop; i++){
                pos -= popups.get(i).getHeight() + 4;
                if (popups.get(i) == p){
                    loop = false;
                }
            }
            return pos;
        }

        private double computeHidePopupY(Popup p, Popup toHide){
            boolean loop = true;
            double pos = Screen.getPrimary().getVisualBounds().getMaxY() + 4;
            for (int i = 0; i < popups.size() && loop; i++){
                if (popups.get(i) != toHide){
                    pos -= popups.get(i).getHeight() + 4;
                    if (popups.get(i) == p){
                        loop = false;
                    }
                }
            }
            return pos;
        }

        private List<Transition> getShowTransitions(Notification n){
            List<Transition> transitions = new ArrayList<>();
            FadeTransition ft = new FadeTransition(Duration.seconds(0.25), n.getLayout());
            ScaleTransition sc = new ScaleTransition(Duration.seconds(0.25), n.getLayout());

            ft.setInterpolator(Interpolator.EASE_OUT);
            ft.setFromValue(0);
            ft.setToValue(1);

            sc.setInterpolator(Interpolator.EASE_OUT);
            sc.setFromX(0.25);
            sc.setToX(1);
            sc.setFromY(0.25);
            sc.setToY(1);
            transitions.add(ft);
            transitions.add(sc);
            return transitions;
        }

        private List<Transition> getHideTransitions(Notification n){
            List<Transition> transitions = new ArrayList<>();
            int idx = popups.indexOf(n.getPopup());
            ScaleTransition sc = new ScaleTransition(Duration.seconds(0.25));
            sc.setInterpolator(Interpolator.EASE_OUT);
            sc.setNode(n.getLayout());
            sc.setFromX(1);
            sc.setToX(0);
            sc.setFromY(1);
            sc.setToY(0);
            transitions.add(sc);
            for (int i = idx + 1; i < popups.size(); i++){
                PopupTranslateTransition ptt = new PopupTranslateTransition(Duration.seconds(0.25), popups.get(i));
                ptt.setInterpolator(Interpolator.EASE_OUT);
                ptt.setFromY(popups.get(i).getY());
//                ptt.setToY(popups.get(i).getY() + n.getPopup().getHeight() + 4);
                ptt.setToY(computeHidePopupY(popups.get(i), n.getPopup()));
                transitions.add(ptt);
            }
            return transitions;
        }
    }

    private static Manager _manager;

    public NotificationManager(){
        _manager = new Manager();
    }

    public static void pop(String title, String message, String state){
        _manager.pop(title, message, state);
    }

    public void hide(){

    }

    public static void remove(Notification n){
        _manager.remove(n);
    }
}
