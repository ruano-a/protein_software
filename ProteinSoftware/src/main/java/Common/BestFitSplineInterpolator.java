package Common;

import javafx.animation.Interpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */

public class BestFitSplineInterpolator extends Interpolator {

    final PolynomialSplineFunction f;

    public BestFitSplineInterpolator(double[] x, double[] y) {
        f = new SplineInterpolator().interpolate(x, y);
    }

    @Override protected double curve(double t) {
        return f.value(t);
    }
}