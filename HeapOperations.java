import java.util.*;

public class HeapOperations {

    FibonacciHeap maxPointer = null;

    /* Insertion into root of heap in a doubly linked list */
    FibonacciHeap insertIntoRoot(FibonacciHeap obj){
        maxPointer = InputOutputParser.maxPointer;
        if(maxPointer == null){        // Insert object into an empty heap
            maxPointer = obj;
            return obj;
        }

        /* Operations to add a singleton node into the doubly linked list */
        obj.prev = maxPointer.prev;
        maxPointer.prev = obj;
        obj.next = maxPointer;
        obj.prev.next = obj;

        if(maxPointer == null)          // Update max in case of first element insertion
            maxPointer = obj;
        else if(obj.element > maxPointer.element)   // Update max if new element > previous max
            maxPointer = obj;
        return maxPointer;
    }

    /* Function to perform delete maximum operation */
    FibonacciHeap removeMax(){
        maxPointer = InputOutputParser.maxPointer;
        deleteMaxFromHeap();                // Deletes the maximum element
        if(maxPointer != null){
            maxPointer = pairwiseCombine();  // Pairwise combining the root elements according to degree
        }
        return maxPointer;
    }

    /* Function to delete maximum element from root list and
     * clear corresponding child fields
     */
    void deleteMaxFromHeap(){
        FibonacciHeap temp = maxPointer;

        if(maxPointer.prev == maxPointer && maxPointer.child == null){      // In case only one element is present in heap
            maxPointer = null;
        }
        else if(maxPointer.child != null && maxPointer.prev == maxPointer){ // in case only one element is present in root
            maxPointer = maxPointer.child;
        }
        else{
            /* Removing element from doubly linked list */
            maxPointer.prev.next = maxPointer.next;
            maxPointer.next.prev = maxPointer.prev;
            maxPointer = maxPointer.next;       // Updating maxPointer to a random node
        }

        /* Clearing the parent field of the max node's children */
        if(temp.child != null){
            FibonacciHeap temp1 = temp.child;
            do{
                temp1.parent = null;
                temp1 = temp1.next;
            }while(temp1 != temp.child);
        }

        /* Combining the children of deleted MaxNode with root list */
        mergeTwoLinkedList(maxPointer,temp.child);
    }

    /* Function to merge all of the roots so that there is only one
     * tree of each degree
     */
    FibonacciHeap pairwiseCombine(){
        /* To keep track of degrees of trees present in the root of heap */
        List<FibonacciHeap> degreeOfTrees = new ArrayList<>();

        FibonacciHeap temp = maxPointer;

        /* Adding all root values to list
         * To keep track of the unique root values
         * while looping through them */
        List<FibonacciHeap> rootValues = new ArrayList<>();
        do{
            rootValues.add(temp);
            temp = temp.next;
        }while(temp != maxPointer);

        FibonacciHeap value = maxPointer;
        while(rootValues.get(0) != value || rootValues.isEmpty()){
            rootValues.add(value);
            value = value.next;
        }

        for(FibonacciHeap val : rootValues){
            for(;;){
                while (val.degree >= degreeOfTrees.size())          // Adding a new null to array
                    degreeOfTrees.add(null);
                if (degreeOfTrees.get(val.degree) == null) {        // If degree does not exist
                    degreeOfTrees.set(val.degree, val);
                    break;
                }

                /* Following operations are performed if degree exists in array
                 * The corresponding value in the degree table is cleared
                 * and merge operation for the two sub trees is performed */
                FibonacciHeap val1 = degreeOfTrees.get(val.degree);
                degreeOfTrees.set(val.degree, null);
                FibonacciHeap minTree, maxTree;
                if(val.element < val1.element) {
                    minTree = val;
                    maxTree = val1;
                }
                else {
                    minTree = val1;
                    maxTree = val;
                }

                /* making minTree the child of maxTree
                 * First minTree is removed from the root */
                minTree.next.prev = minTree.prev;
                minTree.prev.next = minTree.next;
                minTree.next = minTree.prev = minTree;

                /* Node is merged in the linked list of maxTree's child */
                mergeTwoLinkedList(maxTree.child, minTree);

                /* Parent and child pointers are updated */
                maxTree.child = minTree;
                minTree.parent = maxTree;

                /* childCut is changed to false for the minTree
                since it is no longer in the root and maxTree degree is increased*/
                minTree.childCut = false;
                maxTree.degree += 1;
                val = maxTree;  // to check for same degree elements further in the list
            }
            if (val.element >= maxPointer.element) maxPointer = val; // Updating maxPointer
        }
        return maxPointer;
    }

    /* Given two nodes or linked lists, function performs
     * merging the two into a single list
     */
    void mergeTwoLinkedList(FibonacciHeap node1, FibonacciHeap node2){
        if(node1 != null && node2 != null){
            FibonacciHeap temp = node1.next;
            node1.next = node2.next;
            node1.next.prev = node1;
            node2.next = temp;
            node2.next.prev = node2;
        }
    }

    /* Function to perform increase key operation.
     * In case the node is a root node, only the max pointer is updated
     * no cascading cut is performed
     */
    FibonacciHeap increaseKey(FibonacciHeap node, int value){
        maxPointer = InputOutputParser.maxPointer;
        node.element = value;

        /* When node is not a root node and the updated value is
         * still lesser than the value of it's parent
         */
        if(node.parent != null && node.element < node.parent.element){
            return maxPointer;
        }

        /* When node is not a root node and the updated value is
         * greater than the value of it's parent, child cut operation is performed
         */
        if (node.parent != null && node.element > node.parent.element){
            recursiveCascadingCut(node);
        }

        /* Updating the maxPointer corresponding to the updated node */
        if(node.element > maxPointer.element)
            maxPointer = node;
        return maxPointer;
    }

    /* Function to perform cascading cut */
    private void recursiveCascadingCut(FibonacciHeap node) {
        node.childCut = false;

        /* Terminates when parent is null */
        if (node.parent == null)
            return;

        /* if parent has it's child pointer on the node
         * update the child pointer
         */
        if (node.parent.child == node) {
            if (node.next != node)      // update child pointer to next child
                node.parent.child = node.next;
            else                        // if node is the only child update pointer to null
                node.parent.child = null;
        }

        /* Convert into singleton node */
        if (node.next != node) {
            node.next.prev = node.prev;
            node.prev.next = node.next;
        }

        /* merge node the root list */
        node.parent.degree += -1;
        node.prev = node.next = node;
        mergeTwoLinkedList(maxPointer, node);

        /* Check if the parent has already lost a child
         * perform child cut again on parent */
        if (node.parent.childCut)
            recursiveCascadingCut(node.parent);
        else
            node.parent.childCut = true;

        node.parent = null;
    }
}
