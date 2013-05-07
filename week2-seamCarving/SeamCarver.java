import java.awt.Color;

public class SeamCarver {
    private static final boolean DEBUG = false;
    private static final int BORDER_ENERGY = 195075;
    private static final int  ENERGY_METHOD_DOUBLE_GRADIENT = 1;
    private static final int CURRENTENERTYMETHOD = ENERGY_METHOD_DOUBLE_GRADIENT;
    private int width;
    private int height;

    private int[][] energyMatrix;

    private int[][] seamVPathTo;
    private int[][] seamVDestTo;


    private int[][]  pixelMatrix;

    public SeamCarver(Picture picture) {
        width = picture.width();
        height = picture.height();
        energyMatrix = new int[height][width];
        pixelMatrix = new int[height][width];
        setupPictureMatrix(picture, pixelMatrix);
        initEnergyMatrix();

        if (DEBUG)
            StdOut.println("width: " + width + " height " + height);
    }

    private void setupPictureMatrix(Picture p, int[][] pixels)
    {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++)
                pixels[i][j] = p.get(j, i).getRGB();      // ??? because the
                                                 // pixel of image is
                                                 // different form
                                                 // bi,j.
        }
    }

    private void initEnergyMatrix()
    {
        int i, j;
        
        for (i = 0; i < width; i++)
            energyMatrix[0][i] = BORDER_ENERGY;

        for (i = 0; i < width; i++)
            energyMatrix[height - 1][i] = BORDER_ENERGY;

        for (i = 0; i < height; i++)
            energyMatrix[i][0] = BORDER_ENERGY;

        for (i = 0; i < height; i++)
            energyMatrix[i][width - 1] = BORDER_ENERGY;

        for (int h = 1; h < height - 1; h++)
            for (int w = 1; w < width - 1; w++)
                energyMatrix[h][w] = deltaX(h, w) + deltaY(h, w);
    }


    private int getRed(int rgb) { return (rgb >> 16) & 0x0ff; }
    private int getGreen(int rgb) { return (rgb >> 8) & 0x0ff; }
    private int getBlue(int rgb) { return (rgb) & 0x0ff; }
    
    // calc the deltaX of column x, and row y...
    private int deltaX(int y, int x)
    {
       int x1y = pixelMatrix[y][x - 1];
       int x11y = pixelMatrix[y][x + 1];

        int r = Math.abs(getRed(x11y) - getRed(x1y));
        int g = Math.abs(getGreen(x11y) - getGreen(x1y));
        int b = Math.abs(getBlue(x11y) - getBlue(x1y));
        return ((r * r) + (g * g) + (b * b));
    }

    private int deltaY(int y, int x)
    {
        int x1 = pixelMatrix[y - 1][x];
        int x2 = pixelMatrix[y + 1][x];
        
        int r = Math.abs(getRed(x1) - getRed(x2));
        int g = Math.abs(getGreen(x1) - getGreen(x2));
        int b = Math.abs(getBlue(x1) - getBlue(x2));
        return ((r * r) + (g * g) + (b * b));
    }

    // current picture
    public Picture picture()
    {
        Picture p = new Picture(width, height);
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                p.set(j, i, new Color(pixelMatrix[i][j]));
            }
        return p;
    }

    // width of current picture
    public int width()                        
    {
        return width;
    }
    
    // height of current pciture
    public     int height()     
    {
        return height;
    }

    // energy of pixel at column x and row y
    public  double energy(int x, int y)
    {
        if (x < 0 || x >= width || y < 0 || y >= height)
            throw new IndexOutOfBoundsException("out of bounds");

        if (CURRENTENERTYMETHOD ==  ENERGY_METHOD_DOUBLE_GRADIENT)
                return (double) energyMatrix[y][x];

        return 0.0f;
    }

    // sequence of
    // indices for
    // horizontal seam

    private void initSeamPathArrays(int [][]distTo,
                                    int    [][]pathTo,
                                    int    pheight,
                                    int    pwidth)
    {

        if (DEBUG)
            StdOut.printf("init seam with height:%d width:%d\n", pheight, pwidth);
        
        for (int j = 0; j < pheight; j++)
            for (int i = 0; i < pwidth; i++)
                pathTo[j][i] = 0;

        for (int j = 0; j < pheight; j++)
            for (int i = 0; i < pwidth; i++)
                distTo[j][i] = 0x7fffffff;

        for (int i = 1; i < pwidth; i++)
            distTo[0][i] = (int) energyMatrix[0][i];

    }

    private void initSeamPathSpace() {
        if (seamVPathTo == null || seamVDestTo == null) {
            int sz = Math.max(height, width);
            seamVDestTo = new int[sz][sz];
            seamVPathTo = new int[sz][sz];
        }
    }

    private int[] doFindSeam(int [][]pathTo, int [][]distTo,
                             int pheight, int pwidth)
    {

        for (int h = 0; h < pheight - 1; h++)
            for (int w = 1; w < pwidth - 1; w++)
                relax(h, w, distTo, pathTo, pheight, pwidth);

        //        dumpDistMap(pwidth, pheight, distTo);
        //        dumpDistMap(pwidth, pheight, pathTo);

        // find last - 1 line, min weight index.
        int minidx = findMinValue(distTo, pheight - 2, pwidth);

        Stack<Integer> stack = new Stack<Integer>();

        stack.push(minidx);

        int idx = minidx;
        for (int i = pheight - 1; i > 0; i--) {
            stack.push(pathTo[i][idx]);
            idx = pathTo[i][idx];
        }

        int []a = new int[pheight];
        for (int i = 0; i < pheight; i++)
            a[i] = stack.pop();
        
        return a;
    }

    private int findMinValue(int[][]distTo, int row, int pwidth) {
        int temp = 0x7fffffff;
        int minidx = -1;
        for (int i = 0; i < pwidth; i++) {
            int val = distTo[row][i];
            if (val < temp) {
                temp = val;
                minidx = i;
            }
        }
        return minidx;
    }

    // sequence of indices for vertical seam
    public   int[] findVerticalSeam()
    {
        if (DEBUG) 
            StdOut.println("findVerticalSeam");
        initSeamPathSpace();

        initSeamPathArrays(seamVDestTo, seamVPathTo, height, width);

        return doFindSeam(seamVPathTo, seamVDestTo, height, width);
    }

    private int[][] getRotated90Matrix(int[][] matrix, int h, int w) {

            int[][] targetMatrix = new int[w][h];
            for (int i = 0; i < w; i++)
                    for (int j = 0; j < h; j++)
                            targetMatrix[i][j] = matrix[j][i];
            return targetMatrix;
    }

// rotate the array, then rotate back.
    public   int[] findHorizontalSeam()
    {
        if (DEBUG)
            StdOut.println("findHorizontalSeam");
        int[] result;
        int[][] energyMatrixBack = getRotated90Matrix(energyMatrix, height, width);
        int[][] tp = energyMatrix;
        energyMatrix =  energyMatrixBack;
        initSeamPathSpace();
        initSeamPathArrays(seamVDestTo, seamVPathTo, width, height);
        result = doFindSeam(seamVPathTo, seamVDestTo, width, height);
        energyMatrix = tp;
        return result;
    }


    // private void dumpDistMap(int w, int h, double[][] distTo)
    // {
    //     for (int j = 0; j < h; j++) {
    //         for (int  i = 0; i < w; i++)
    //             StdOut.printf(" \t%.2f ", distTo[j][i]);
    //     StdOut.printf("\n");
    //     }
    // }

    // private void dumpDistMap(int w, int h, int[][] distTo)
    // {
    //     for (int j = 0; j < h; j++) {
    //         for (int  i = 0; i < w; i++)
    //             StdOut.printf(" \t%d ", distTo[j][i]);
    //     StdOut.printf("\n");
    //     }
    // }


    // relax the next layer 's three nodes.
    private void relax(int layer, int pos, int [][] distTo,
                       int [][] edgeTo, int pheight, int pwidth) 
    {
        if (layer + 1 >= pheight)
            throw new IndexOutOfBoundsException("relax too much further, fix it");
        if (pos - 1 >= 0)
            relax0(layer, pos, layer + 1, pos - 1, distTo, edgeTo);
        if (pos + 1 < pwidth)
            relax0(layer, pos, layer + 1, pos + 1, distTo, edgeTo);
        relax0(layer, pos, layer + 1, pos, distTo, edgeTo);
    }

    private void relax0(int layer, int pos, int targetl, int targetp,
                        int [][] distTo, int [][]edgeTo)
    {
        //        StdOut.printf("relax0: old: [%d %d] new [%d %d]\n",
        //                      layer, pos, targetl, targetp);
        // the edge to only store ont col number, since the layer
        // number is know, it was the n - 1 layer.
        int newe = energyMatrix[targetl][targetp];
        if (distTo[targetl][targetp] > distTo[layer][pos] + (int) newe) {
            edgeTo[targetl][targetp] = pos;
            distTo[targetl][targetp] = distTo[layer][pos] + (int) newe;
        }
    }
    
    // remove horizontal seam from picture
    public    void removeHorizontalSeam(int[] a)
    {
        if (a.length != width || width <= 1)
            throw new IllegalArgumentException("");


        int [][] em = getRotated90Matrix(energyMatrix, height, width);
        int [][] pm = getRotated90Matrix(pixelMatrix, height, width);
        removeSeam(a, em, pm);
        energyMatrix = getRotated90Matrix(em, width, height);
        pixelMatrix = getRotated90Matrix(pm, width, height);
        height--;
    }

    private void arrayRemoveItem(int []a, int index)
    {
        for (int i = index; i < a.length - 1; i++) {
            a[i] = a[i+1];
        }
    }

    private void removeSeam(int[] array, int[][] em, int[][] pm)
    {
            int h = 0;
            for (int i : array) {
                    arrayRemoveItem(em[h], i);
                    h++;
            }

            h = 0;
            for (int i : array) {
                    arrayRemoveItem(pm[h], i);
                    h++;
            }
    }

    // remove vertical seam from picture
    public    void removeVerticalSeam(int[] array)
    {
        if (array.length != height || height <= 1)
            throw new IllegalArgumentException("");

        removeSeam(array, energyMatrix, pixelMatrix);
        // then reduce the height;
        width--;
    }
}
