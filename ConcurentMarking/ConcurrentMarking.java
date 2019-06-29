import java.util.Set;
import java.util.TreeSet;

public class ConcurrentMarking {
	
	public static void main(String[] args) {
		Tree tr = new Tree(100);
		Mutator m = new Mutator(tr);
		Collector c = new Collector(tr);
		try {
			m.start();
			c.start();
			m.join();
			c.join();
		} catch (Exception e) {}
			
		System.out.println("\nValues in Tree:");
		tr.print();
			
		c.re_mark_print();
		} 
}

class Collector extends Thread {
	
	Tree root;
	
	public static Set<Integer> re_mark_true = new TreeSet<Integer>();
	public static Set<Integer> re_mark_false = new TreeSet<Integer>();
	
	public Collector(Tree root) {
		this.root = root;
	}
	
	public void run() {
		System.out.println("Start Concurrent Marking ...");
		try { sleep(150); }
		catch (Exception e) {}
		mark(root);
	}
	
	protected void mark(Tree root) {
		if (root == null)
			return;
		root.mark_bit = true;
		try { sleep(75); }
		catch (Exception e) {}
		mark(root.left);
		try { sleep(50); }
		catch (Exception e) {}
		mark(root.right);
	}
	
    public void re_mark_print() {
    	System.out.println();
		System.out.println("\nNodes created after collector visited parent:");
		for (int i: re_mark_true) 
				System.out.print(i + " ");
		
    	System.out.println();
		System.out.println("\nNodes deleted after collector marked it:");
		for (int i: re_mark_false) 
				System.out.print(i + " ");
	}
	
}

class Mutator extends Thread {
	Tree tr;
	public Mutator(Tree tr) {
		this.tr = tr;
	}
	public void run() {
		System.out.println("Starting Mutator ...");
		insertions();
		deletions();
	}
	void insertions() {
		tr.insert(50);
		tr.insert(125);
		tr.insert(150);
		tr.insert(20);
		tr.insert(30);
		tr.insert(26);
		tr.insert(33);
		tr.insert(75);
		tr.insert(20);
		tr.insert(55);
		tr.insert(90);
		tr.insert(140);
		tr.insert(160);
		tr.insert(25);
		tr.insert(80);
		tr.insert(95);
		tr.insert(133);
		tr.insert(163);
		tr.insert(81);
		tr.insert(97);
	}
	
	void deletions() {
		tr.delete(20);
		tr.delete(125);
		tr.delete(150);
		tr.delete(100);
		tr.delete(50);
		tr.delete(75);
		tr.delete(140);
		tr.delete(90);
	}
}


class Tree {

	public Tree(int n) {
		value = n; 
		// left == right == parent == null, by default
	}

	protected void insert(int n) {
		if (value == n)
			return;
		if (value < n)
			if (right == null) {
				right = new Tree(n);
				right.parent = this;
				if(mark_bit == true)
				{
					right.mark_bit=true;
				    Collector.re_mark_true.add(n);
				}
				// Write GC code here
			}
			else
				right.insert(n);

		else if (left == null) {
			left = new Tree(n);
			left.parent = this;
			if(mark_bit == true)
			{
				left.mark_bit=true;
			    Collector.re_mark_true.add(n);
			}
			// Write GC code here
		}
		else
			left.insert(n);
	}
	

	void print() {
		if (left != null)
			left.print();
		System.out.print(value + " ");
		if (right != null)
			right.print();
	}
	
	public Tree min() {
		if (left == null)
			return this;
		else
			return left.min();
	}

	public Tree max() {
		if (right == null)
			return this;
		else
			return right.max();
	}

	public Tree find(int n) {
		if (value == n)
			return this;
		else if (value < n)
			if (right == null)
				return null;
			else
				return right.find(n);
		else if (left == null)
			return null;
		else
			return left.find(n);
	}

	public void delete(int n) {
		Tree t = find(n);
		if (t == null) { // n is not in the tree
			System.out.println("Unable to delete " + n + " -- not in the tree!");
			return;
		} else if (t.left == null && t.right == null) { // n is at a leaf position
			if (t != this) // if t is not the root of the tree
				case1(t);
			else
				System.out.println("Unable to delete " + n + " -- tree will become empty!");
			return;
		} else if (t.left == null || t.right == null) {
			// t has one subtree only
			if (t != this) { // if t is not the root of the tree
				case2(t);
				return;
			} else { // t is the root of the tree with one subtree
				if (t.right == null)
					case3L(t);
				else
					case3R(t);
				return;
			}
		} else 
			// t has two subtrees; replace n with the smallest value in t's right subtree
			case3R(t);
	}

	protected void case1(Tree t) { // remove the leaf
		if (t.value > t.parent.value)
			t.parent.right = null;
		else
			t.parent.left = null;
		t.parent = null;
		if(t.mark_bit==true)
		{
			t.mark_bit=false;
		    Collector.re_mark_false.add(t.value);
		}
		// Write GC code here
	}

	protected void case2(Tree t) { // remove internal node
		Tree next;
		if (t.right == null)
			next = t.left;
		else
			next = t.right;
		if (t.value > t.parent.value)
			t.parent.right = next;
		else
			t.parent.left = next;
		next.parent = t.parent;
		
		// for clarity
		t.parent = null; t.left = null; t.right = null;
		if(t.mark_bit==true)
		{
			t.mark_bit=false;
		    Collector.re_mark_false.add(t.value);
		}
		// Write GC code here
		 
	}

	protected void case3L(Tree t) { // replace t.value
		Tree max_left_t = t.left.max();
		if (max_left_t.left == null && max_left_t.right == null)
			case1(max_left_t); // max_left_t is a leaf node
		else
			case2(max_left_t); // max_left_t is a non-leaf node
		t.value = max_left_t.value;
	}

	protected void case3R(Tree t) { // replace t.value
		Tree min_right_t = t.right.min();
		if (min_right_t.left == null && min_right_t.right == null)
			case1(min_right_t); // min_right_t is a leaf node
		else
			case2(min_right_t); // min_right_t is a non-leaf node
		t.value = min_right_t.value;
	}
	
    protected boolean mark_bit; // = false, by default
	protected int value;
	protected Tree left, right, parent;
	
}
 




