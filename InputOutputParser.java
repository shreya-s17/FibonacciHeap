import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class InputOutputParser {

    /* Pointer for maximum value in heap */
    static FibonacciHeap maxPointer = null;
    /* HashMap stores the input string of keyword as key
     * and value is the pointer to the corresponding node
     */
    static HashMap<String,FibonacciHeap> map = new HashMap<>();
    HeapOperations h = new HeapOperations();

    /* Function to read the input file line by line and perform the corresponding operation
     * Can be either insert, deleteMax or increase key
     * Can be writing output to the file
     * Can be exit() the program if encountered 'stop'
     */
    void readFile(BufferedWriter bw, String input_name) throws Exception{
        try{
            /* Relative path to the input file */
            File file = new File(input_name);
            Scanner sc = new Scanner(file);

            while(sc.hasNextLine()){
				String s = sc.nextLine();
				if(s.isEmpty())
					throw new Exception("Found empty string");
                s = s.trim();
                if("stop".equals(s.toLowerCase())){
                    bw.close();         //To close the output file if encountered stop
                    System.exit(0);
                }
                /* To perform max lookups on encountering a query */
                else if(s.charAt(0) != '$'){
                    int num;
                    try{
                        num = Integer.parseInt(s);
                    }
                    catch(Exception e){
                        throw new Exception("Could not parse '"+s + "' to integer or $ sign is missing.");
                    }

                    /* To handle negative queries */
                    if(num <= 0)
                        throw new Exception("Incorrect query. Can only be positive: " + num);

                    /* Failure case */
                    if(num > map.size())
                        throw new Exception("Values to be retrieved are greater than values present in map. "
                                + "Max values to be retrieved="+num+", size of map=" + map.size());

                    /* List to store deleted values for re-insertion
                     * after execution of query is complete.
                     */
                    List<String> removedMax = new ArrayList<>();
                    String data = "";
                    for(int i=0; i<num; i++){
                        removedMax.add(findmax());          // Finds the maximum using FibonacciHeap
                        String append = i==0 ? "":",";
                        //data += append + removedMax.get(i) + map.get(removedMax.get(i)).element;
                        data += append + removedMax.get(i);
                    }

                    /* Writing the output strings into a file */
                    writeFile(data, bw);

                    /* Re-insertion of deleted max elements into the root of heap
                     * The insertion is done like a singleton insert into the root
                     */
                    for(int i=0; i<num; i++){
                        FibonacciHeap node = map.get(removedMax.get(i));

                        /* To create a singleton node and clear all the fields except element */
                        node.next = node.prev = node;
                        node.parent = node.child = null;
                        node.childCut = false;
                        node.degree = 0;

                        /* Updating the heapPointer */
                        maxPointer = h.insertIntoRoot(node);
                    }
                }
                else{           // To perform insertion or increaseKey operation into hashMap and heap
                    String key;
                    int value;

                    /* Parsing input string into desired format */
                    try{
                        int idx = s.indexOf(" ");
                        key = s.substring(1,idx);
                        value  = Integer.parseInt(s.substring(idx + 1, s.length()));
                    }
                    catch(Exception e){
                        throw new Exception(e.getMessage()+": String is not in the specified format: "+s);
                    }

                    /* To check for negative frequencies */
                    if(value <= 0)
                        throw new Exception("Incorrect frequency. Can only be positive: " + value);

                    if(!checkDuplicateandUpdate(key, value)){       // Check if keyword already exists
                        FibonacciHeap obj = new FibonacciHeap(value);   // Creating a new object incase keyword doesn't exist
                        maxPointer = h.insertIntoRoot(obj); // Heap insertion
                        insertIntoMap(s, obj);  // Map insertion
                    }
                }
            }
        }
        catch(Exception e){
            throw new Exception("Exception occurred in ReadFile:" + e.getMessage());
        }
    }

    /* Function to check if keyword exists
     * Function performs the increase key operation if keyword exists
     */
    private boolean checkDuplicateandUpdate(String key, int value){
        if(map.containsKey(key)){
            FibonacciHeap tempObj = map.get(key);
            maxPointer = h.increaseKey(tempObj, tempObj.element + value);
            return true;
        }
        return false;
    }

    /* Helper function for inserting new keywords into map */
    void insertIntoMap(String s, FibonacciHeap obj){
        int idx = s.indexOf(" ");
        String key = s.substring(1,idx);
        map.put(key, obj);
    }

    /* Function to write output to a file */
    void writeFile(String data, BufferedWriter bw) throws IOException{
        bw.write(data);
        bw.newLine();
        bw.flush();
    }

    /* Function to find maximum value using Fibonacci Heap operations */
    private String findmax(){
        /* To store keyword corresponding to object whose value is maximum */
        String maxName = "";
        for(Map.Entry<String, FibonacciHeap> entry : map.entrySet()){
            if(entry.getValue() == maxPointer){
                maxName = entry.getKey();
                break;
            }
        }
        /* Remove max operation */
        maxPointer = h.removeMax();
        return maxName;
    }
}
