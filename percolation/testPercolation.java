public class testPercolation
{

    public static void main(String[] args)
    {
        Percolation s = new Percolation(10);

        s.isFull(-1, 11);
        s.isFull(5, 11);

    }
}
