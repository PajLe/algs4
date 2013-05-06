/*************************************************************************
 *  Compilation:  javac KdTreeVisualizer.java
 *  Execution:    java KdTreeVisualizer
 *  Dependencies: StdDraw.java Point2D.java KdTree.java
 *
 *  Add the points that the user clicks in the standard draw window
 *  to a kd-tree and draw the resulting kd-tree.
 *
 *************************************************************************/

public class KdTreeVisualizer {

    public static void main(String[] args) {
        StdDraw.show(0);
        KdTree kdtree = new KdTree();

        if (args.length > 0) {
            String filename = args[0];
            In in = new In(filename);
            StdDraw.show(0);

            while (!in.isEmpty()) {
                double x = in.readDouble();
                double y = in.readDouble();
                Point2D p = new Point2D(x, y);
                kdtree.insert(p);
            }

            kdtree.draw();

            StdOut.printf("size:%d \n", kdtree.size());
            
            while (true) StdDraw.show(50);
        } else {
            while (true) {
                if (StdDraw.mousePressed()) {
                    double x = StdDraw.mouseX();
                    double y = StdDraw.mouseY();
                    System.out.printf("%8.6f %8.6f\n", x, y);
                Point2D p = new Point2D(x, y);
                kdtree.insert(p);
                StdDraw.clear();
                kdtree.draw();
                }
                StdDraw.show(50);
            }
        }

    }
}
