package robot.ch3.collaborative.similarity;



public class CosineSimilarityMeasure {

    public double calculate(double[] v1, double[] v2) {
        double a = getDotProduct(v1, v2);
        double b = getNorm(v1) * getNorm(v2);
        return a / b;
    }
    
    private double getDotProduct(double[] v1, double[] v2) {
        double sum = 0.0;
        for(int i = 0, n = v1.length; i < n; i++) {
            sum += v1[i] * v2[i];
        }
        return sum;
    }
    
    private double getNorm(double[] v) {
        double sum = 0.0;
        for( int i = 0, n = v.length; i < n; i++) {
            sum += v[i] * v[i];
        }
        return Math.sqrt(sum);
    }
}
