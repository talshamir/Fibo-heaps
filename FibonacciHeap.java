/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
    private HeapNode first;
    private HeapNode minRoot;
    private int size;
    private int numOfTrees; //keeps the number of roots update
    private int numOfMarkedNodes; //keeps the number of marked nodes update
    private static int tLinks;
    private static int tCuts;

    /**
     * public boolean isEmpty()
     *
     * Returns true if and only if the heap is empty.
     * Complexity O(1)
     */
    public boolean isEmpty()
    {
        return this.minRoot==null;
    }

    /**
     * public HeapNode insert(int key)
     *
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     * The added key is assumed not to already belong to the heap.
     * this function calls the insertTree func which has a complexity of O(1).
     * Complexity O(1)
     * Returns the newly created node.
     */
    public HeapNode insert(int key)
    {   HeapNode x = new HeapNode(key);
        insertTree(x);
        this.size++;
        return x;
    }
   /**
    * private void insertTree(HeapNode x)
    * concatenating the new heapNode inside the heap
    * perform a constant time of work by changing the pointers
    * Complexity O(1)
    */
    private void insertTree(HeapNode x)
    {
        this.numOfTrees++;
        if(this.isEmpty()){
            this.minRoot = x;
            this.first = x;
        }
        else {
            if(this.first.getPrevious()!=this.first) {
                HeapNode last = this.first.getPrevious();
                last.setNext(x);
                x.setPrevious(last);
            }
            else {
                x.setPrevious(this.first);
                this.first.setNext(x);
            }
            x.setNext(this.first);
            this.first.setPrevious(x);
            if (x.getKey() < this.minRoot.getKey()) {
                this.minRoot = x;
            }
        }
        this.first = x;
    }
    /**
     * public void deleteMin()
     *
     * Deletes the node containing the minimum key.
     *
     * the function disconnect the minRoot which holds the minimum key of the heap.
     * keep the heap with correct order of pointers
     * use the function consolidating which has a complexity of O(n)(W.C), Amortized - O(log(n))
     * Complexity - O(n)(W.C), Amortized - O(log(n))
     */
    public void deleteMin()
    {
        this.size--;
        if(size == 0)
        {
            this.minRoot = null;
            this.first = null;
            this.numOfTrees = 0;
            this.numOfMarkedNodes = 0;
            return;
        }
        HeapNode deletedNode = this.minRoot;
        HeapNode deletedChild = this.minRoot.getChild();
        HeapNode deletedNodeLeftNgb = this.minRoot.getPrevious();
        HeapNode deletedNodeRightNgb = this.minRoot.getNext();
        if (deletedChild!=null) // has children
        {
            if(deletedNodeLeftNgb == deletedNode) //has children and has no brothers
            {
                this.first = deletedChild;
            }
            else // has children and has brothers
            {
                if (this.first == deletedNode) //the deletedNode is the first tree
                {
                    this.first = deletedChild;
                }
                HeapNode deletedChildLast = deletedChild.getPrevious();
                deletedNodeLeftNgb.setNext(deletedChild);
                deletedChild.setPrevious(deletedNodeLeftNgb);
                deletedNodeRightNgb.setPrevious(deletedChildLast);
                deletedChildLast.setNext(deletedNodeRightNgb);
                deletedChild.setFather(null);
                deletedNode.setChild(null);
            }
        }

        else //has no child but has brothers
        {
            if(deletedNode == this.first){
                this.first = deletedNode.getNext();
            }
            deletedNodeLeftNgb.setNext(deletedNodeRightNgb);
            deletedNodeRightNgb.setPrevious(deletedNodeLeftNgb);
        }
        this.consolidating();


    }
    /**
     * private void consolidating()
     * the function doing successive linking until there is no 2 trees of the same size.
     * create a new heapNode array in size of log base 2 of m + 2 to contain all possible tree ranks
     * the function fill the array using the function addToArray - Complexity - W.C : O(log(n)).
     * after the array is filled its takes 2 loops over the array to concatenating the heap as a circle heap.
     * to fill the array in the w.c we call addToArray n times and perform a successive linking that sums to O(n)
     * using the multiplication sum formula.
     * Complexity - W.C: O(n)
     */
    private void consolidating() {
        this.numOfTrees = 0;
        HeapNode[] ranks = new HeapNode[(int) (Math.log(size) / Math.log(2)) + 2];
        HeapNode last = this.first.getPrevious();
        HeapNode current = this.first;
        last.setNext(null); //break the circle
        int minKey = Integer.MAX_VALUE;
        int minRankTree = -1;
        int maxRankTree = -1;
        int j = 0;
        while (current != null) { //We create the Array of trees with uniq ranks for each tree.
            HeapNode temp = current.getNext();
            current.setNext(current);
            current.setPrevious(current);
            addToArray(ranks,current);
            current = temp;
        }
        // We're concatenating the trees in the Array
        for(int i = 0; i<ranks.length;i++){
            if(ranks[i] != null)
            {
                this.numOfTrees++; // count each tree;
                if(minRankTree == -1)
                {
                    this.first = ranks[i];
                    minRankTree = i;
                }
                j = i + 1;
                while(j<ranks.length && ranks[j] == null){
                    j++;
                }
                if(j<ranks.length && i!=j && ranks[j] != null) {
                    ranks[i].setNext(ranks[j]);
                    ranks[j].setPrevious(ranks[i]);
                }
                i=j-1;
            }
        }
        for(int i =0; i<ranks.length;i++) //find the highest degree
        {
            if(ranks[i] != null) {
                if(ranks[i].getKey() < minKey)
                {
                    minKey = ranks[i].getKey();
                    this.minRoot = ranks[i];
                }
                if (maxRankTree < i) {
                    maxRankTree = i;
                    last = ranks[i];
                }
            }
        }
        if(last != this.first){ //closing the circle
            last.setNext(this.first);
            this.first.setPrevious(last);
        }
    }
    /**
     * private void addToArray
     * the function receive array, in the i' cell there is a root of a tree from rank i.
     * the function receive a root to insert in the array.
     * while the ceil in the array which the root ranks belong to is all ready fill the function calls linking and
     * make a recursive call for the new root.
     * the function returns the Array with one rank for each tree after perform successive linking.
     * every link takes O(1), there is at maximum O(log(n)) trees.
     * Complexity - W.C: O(log(n))
     */
    private void addToArray(HeapNode[] ranks, HeapNode root){
        if (ranks[root.getRank()] == null) {
            ranks[root.getRank()] = root;
            return;
        }
        else {
            HeapNode currentTree = ranks[root.getRank()];
            ranks[root.getRank()] = null;
            addToArray(ranks,this.linking(root, currentTree));
        }
    }

    /**
     * private HeapNode linking(HeapNode root1, HeapNode root2)
     *
     * links 2 trees into one and return the new root
     *
     * complexity - O(1)
     */
    private HeapNode linking(HeapNode root1, HeapNode root2){
        tLinks++;
        HeapNode bigger;
        HeapNode smaller;
        HeapNode previousOfChild;
        if(root1.getKey() > root2.getKey()){
            bigger = root1;
            smaller = root2;
        }
        else {
            bigger = root2;
            smaller = root1;
        }
        if (smaller.getChild() != null) {
            previousOfChild = smaller.getChild().getPrevious();
            previousOfChild.setNext(bigger);
            bigger.setPrevious(previousOfChild);
            bigger.setNext(smaller.getChild());
            smaller.getChild().setPrevious(bigger);
        }
        smaller.setChild(bigger);
        bigger.setFather(smaller);
        smaller.setRank(smaller.getRank()+1);
        return smaller;
    }
    /**
     * public HeapNode findMin()
     *
     * Returns the node of the heap whose key is minimal, or null if the heap is empty.
     * Complexity - O(1)
     */
    public HeapNode findMin()
    {
        return this.minRoot;
    }

    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Melds heap2 with the current heap.
     *
     * the function concatenating the 2 heaps by changing pointers.
     *
     * Complexity - O(1)
     */
    public void meld (FibonacciHeap heap2)
    {
        if(heap2.isEmpty()){
            return;
        }

        this.numOfTrees += heap2.numOfTrees;
        this.numOfMarkedNodes = this.numOfMarkedNodes + heap2.numOfMarkedNodes;

        if (this.isEmpty()){
            this.minRoot = heap2.findMin();
            this.size = heap2.size();
            this.first = heap2.getFirst();
            return;
        }
        this.first.getPrevious().setNext(heap2.getFirst());
        heap2.getFirst().setPrevious(this.first.getPrevious());
        if(this.minRoot.key > heap2.findMin().key){
            this.minRoot = heap2.findMin();
        }
        this.size = heap2.size() + this.size();
    }
    public HeapNode getFirst(){
        return this.first;
    }
    /**
     * public int size()
     *
     * Returns the number of elements in the heap.
     *
     * Complexity O(1)
     */
    public int size()
    {
        return this.size;
    }

    /**
     * public int[] countersRep()
     *
     * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
     * Note: The size of  the array depends on the maximum order of a tree, and an empty heap returns an empty array.
     * Define the array size with the function highestTreeRank -W.C- O(log(n))
     * the iterating over the heap roots and using get.rank update the i-th entry in the array. O(log(n))
     * Complexity O(log(n))
     */
    public int[] countersRep()
    {
        int[] arr = new int[highestTreeRank()+1];
        if(this.isEmpty())
        {
            return arr;
        }
        HeapNode current = this.first.getNext();
        arr[this.first.getRank()] = arr[this.first.getRank()] +1;
        while(current != this.first){
            arr[current.getRank()] = arr[current.getRank()] +1;
            current = current.getNext();
        }
        return arr;
    }
    private int highestTreeRank(){
        if(this.isEmpty())
        {
            return 0;
        }
        int highestRank = 0;
        highestRank = this.first.getRank();
        HeapNode current = this.first.getNext();
        while(current != this.first){
            if(current.getRank()>highestRank){
                highestRank = current.getRank();
            }
            current = current.getNext();
        }
        return highestRank;
    }

    /**
     * public void delete(HeapNode x)
     *
     * Deletes the node x from the heap.
     * It is assumed that x indeed belongs to the heap.
     * decreaseKey - O(log(n)) and deleteMin() - O(n)
     * complexity - O(n)
     */
    public void delete(HeapNode x)
    {
        decreaseKey(x,Integer.MAX_VALUE);
        deleteMin();
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     *
     * Decreases the key of the node x by a non-negative value delta.
     * if needed the function call cascadingCut - W.C O(log(n)).
     * Complexity - W.C O(log(n))
     */
    public void decreaseKey(HeapNode x, int delta)
    {
        x.setKey(x.getKey()-delta);
        if(x.getKey()< this.minRoot.getKey()){
            this.minRoot = x;
        }
        if(x.getFather() != null)//x is not a root check if he needs to be cut and perform cascadingCuts
        {
            if(x.getFather().getKey() > x.getKey()){
                this.cascadingCut(x,x.getFather());
            }
        }
    }
    /**
     * private void cut(HeapNode x, HeapNode y)
     *
     * cuts the node x from is father y.
     * x become the first tree in the heap.
     * Complexity - O(1)
     */
    private void cut(HeapNode x, HeapNode y){
        this.tCuts++;
        x.setFather(null);
        if (x.getMark()){
            this.numOfMarkedNodes--;
        }
        x.mark = false;
        y.setRank(y.getRank()-1);
        if(x.getNext() == x){
            y.setChild(null);
        }
        else{
            x.getPrevious().setNext(x.getNext());
            x.getNext().setPrevious(x.getPrevious());
            if(y.getChild()==x){
                y.setChild(x.getNext());
            }
        }
        insertTree(x); //Move the cutted tree to be the first tree.
    }
    /**
     * private void cascadingCut(HeapNode x, HeapNode y)
     *
     * cuts the node x from is father y.
     * if father is marked perform a cut to the father using recursive call.
     * maximum recursive calls - O(log(n)), the height of the highest tree bounded by O(log(n))
     * Complexity - O(log(n))
     */
    private void cascadingCut(HeapNode x, HeapNode y){
        cut(x,y);
        if(y.getFather() != null){
            if(!y.getMark()){
                y.setMark(true);
                this.numOfMarkedNodes++;
            }
            else
            {
                cascadingCut(y,y.getFather());
            }
        }
    }
    /**
     * public int potential()
     *
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     *
     * In words: The potential equals to the number of trees in the heap
     * plus twice the number of marked nodes in the heap.
     * Complexity - O(1)
     */
    public int potential()
    {
        return this.numOfTrees + 2*this.numOfMarkedNodes;
    }

    /**
     * public static int totalLinks()
     *
     * This static function returns the total number of link operations made during the
     * run-time of the program. A link operation is the operation which gets as input two
     * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
     * tree which has larger value in its root under the other tree.
     * Complexity - O(1)
     */
    public static int totalLinks()
    {
        return tLinks;
    }

    /**
     * public static int totalCuts()
     *
     * This static function returns the total number of cut operations made during the
     * run-time of the program. A cut operation is the operation which disconnects a subtree
     * from its parent (during decreaseKey/delete methods).
     * Complexity - O(1)
     */
    public static int totalCuts()
    {
        return tCuts;
    }
    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     *
     * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
     * The function create new FibonacciHeap() helper. we insert the min of H using findMin -O(1) and then insert to helper
     * the child of the minimum with his brothers.
     * to prevent any change in H we insert a new node with the same key of the next minimum in H, and add to the inserted
     * node a reference to the real node.
     * we repeat this action for k times and after inserting the new children of the last minimum we insert the minimum to the
     * array and perform deleteMin()
     * after K rep's, which every rep is bounded by the number of sons the new minimum has.
     * each minimum is a root of a tree from smaller degree than his father (we proof that invariant in the class).
     * Complexity - W.C O(k*deg(H))
     */
    public static int[] kMin(FibonacciHeap H, int k)
    {
        int[] arr = new int[k];
        HeapNode first;
        if(k == 0)
            return arr;
        FibonacciHeap helper = new FibonacciHeap();
        HeapNode nodeOfH = H.findMin();
        helper.insert(nodeOfH.getKey()).setReference(nodeOfH);
        for (int i = 0; i < k; i++) {
            arr[i] = helper.findMin().getKey();
            nodeOfH = helper.findMin().getReference().getChild();
            first = nodeOfH;
            helper.deleteMin();
            if (nodeOfH != null){
                do{
                    helper.insert(nodeOfH.getKey()).setReference(nodeOfH);
                    nodeOfH = nodeOfH.getNext();
                }
                while(nodeOfH != null && nodeOfH != first);
            }
        }
        return arr;
    }


    /**
     * public class HeapNode
     *
     * If you wish to implement classes other than FibonacciHeap
     * (for example HeapNode), do it in this file, not in another file.
     *
     */
    public static class HeapNode {

        public int key;
        private int rank;
        private boolean mark;
        private HeapNode child;
        private HeapNode father;
        private HeapNode next;
        private HeapNode previous;
        private HeapNode reference; //This field save a reference for another Heap, it's used for kMin Func.

        public HeapNode(int key) {
            this.key = key;
            this.rank = 0;
            this.mark = false;
            this.next = this;
            this.previous=this;
            this.reference=this;
        }

        public int getKey() {
            return this.key;
        }
        public int getRank() {
            return this.rank;
        }
        public boolean getMark(){ return this.mark;}
        public HeapNode getNext(){return this.next;}
        public HeapNode getPrevious(){return this.previous;}
        public HeapNode getChild(){return this.child;}
        public HeapNode getFather(){return this.father;}

        public void setChild(HeapNode child) {
            this.child = child;
        }

        public void setFather(HeapNode father) {
            this.father = father;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public void setNext(HeapNode next) {
            this.next = next;
        }

        public void setMark(boolean mark) {
            this.mark = mark;
        }

        public void setPrevious(HeapNode previous) {
            this.previous = previous;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }
        public HeapNode getReference(){
            return this.reference;
        }
        public void setReference(HeapNode x){
            this.reference = x;
        }
    }
}
