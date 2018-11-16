/* POJO class for Fibonacci Heap object */
public class FibonacciHeap {
    public FibonacciHeap next;
    public FibonacciHeap prev;
    public FibonacciHeap parent;
    public FibonacciHeap child;
    public boolean childCut;
    public int degree;
    public int element;

    /* Default constructor */
    FibonacciHeap(){
    }

    /*Create a new initialized node */
    FibonacciHeap(int elem ){
        next = prev = this;
        element = elem;
        parent = child = null;
        childCut = false;
        degree = 0;
    }
}
