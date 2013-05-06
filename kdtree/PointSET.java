/*
 Write a mutable data type PointSET.java that represents a set of
 points in the unit square. Implement the following API by using a
 red-black BST (using either SET from algs4.jar or java.util.TreeSet).
 */

// Your implementation should support insert() and contains() in time
// proportional to the logarithm of the number of points in the set in
// the worst case; it should support nearest() and range() in time
// proportional to the number of points in the set.

public class PointSET {
    private SET<Point2D> set;

    
   public PointSET()
   // construct an empty set of points
    {
        set = new SET<Point2D>();
    }
   public boolean isEmpty()
   // is the set empty?
    {
        return set.isEmpty();
    }
   public int size()
   // number of points in the set
    {
        return set.size();
    }
   public void insert(Point2D p)
   // add the point p to the set (if it is not already in the set)
    {
        if (!set.contains(p))
            set.add(p);
    }
   public boolean contains(Point2D p)
   // does the set contain the point p?
    {
        return set.contains(p);
    }
   public void draw()
   // draw all of the points to standard draw
    {
        StdDraw.setPenRadius(.01);
        for (Point2D p : set)
            p.draw();
    }
   public Iterable<Point2D> range(RectHV rect)
   // all points in the set that are inside the rectangle
    {
        SET<Point2D> s = new SET<Point2D>();
        for (Point2D p : set)
            if (rect.contains(p))
                s.add(p);

        return s;
    }
   public Point2D nearest(Point2D p)
   // a nearest neighbor in the set to p; null if set is empty
    {
        if (p == null)
            throw new RuntimeException("");
        Point2D min = null;
        double number = 999999;

        for (Point2D tp : set) {
            double t = p.distanceSquaredTo(tp);
            if (t < number) {
                number = t;
                min = tp;
            }
        }
        return min;
    }
}
