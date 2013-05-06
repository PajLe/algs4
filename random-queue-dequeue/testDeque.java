
// test code of my deque

public class testDeque {
   /**
     * A test client.
     */
    public static void main(String[] args) {
        Deque<String> q = new Deque<String>();
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            if (!item.equals("-")) {
                q.addFirst(item);
                StdOut.printf("----------------------\n");
                q.dump();
            }
            else if (!q.isEmpty()) StdOut.print(q.removeLast() + " ");
        }
        StdOut.println("(" + q.size() + " left on queue)");

        StdOut.printf("----------------------\n");
        q.dump();
        for (String s : q) {
            StdOut.printf("%s \n", s);
        }
    }
}
