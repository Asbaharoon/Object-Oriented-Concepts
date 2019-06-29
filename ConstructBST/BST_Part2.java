//CSE 522: Assignment 1, Part 2

class BST_Part2 {

	public static void main(String[] args) {
		AbsTree tr = new DupTree(100);
		tr.insert(50);
		tr.insert(125);
		tr.insert(150);
		tr.insert(20);
		tr.insert(75);
		tr.insert(20);
		tr.insert(90);
		tr.insert(50);
		tr.insert(125);
		tr.insert(150);
		tr.insert(75);
		tr.insert(90);

		tr.delete(20);
		tr.delete(20);
		tr.delete(20);
		tr.delete(150);
		tr.delete(100);
		tr.delete(150);
		tr.delete(125);
		tr.delete(125);
		tr.delete(50);
		tr.delete(50);
		tr.delete(50);
		tr.delete(75);
		tr.delete(90);
		tr.delete(75);
		tr.delete(90);
	}
}

abstract class AbsTree {

	public AbsTree(int n) {
		value = n;
		left = null;
		right = null;
	}

	public void insert(int n) {
		if (value == n) {
			count_duplicates();
		} else if (value < n) {
			if (right == null) {
				right = add_node(n);
				right.parent = this;
				// System.out.println("node added");
			} else
				right.insert(n);
		} else if (left == null) {
			left = add_node(n);
			left.parent = this;
		} else {
			left.insert(n);
		}
	}

	public void delete(int n) {
		// adapt Part 1 solution and use here
		AbsTree t = find(n);
		if (t == null) { // n is not in the tree
			System.out.println("Unable to delete " + n + " -- not in the tree!");
			return;
		} else if (t instanceof DupTree) {
			((DupTree) t).count--;
			if (((DupTree) t).count == 0) {

				if (t.left == null && t.right == null) {
					// n is at a leaf position
					if (t != this) // if t is not the root of the tree
						case1(t);

					else
						System.out.println("Unable to delete " + n + " -- tree will become empty!");

					return;
				} else if (t.left == null || t.right == null) {
					if (t != this) { // if t is not the root of the tree
						case2(t);
						return;
					} else { // t is the root of the tree with one subtree
						if (t.left != null)
							case3L(t);
						else
							case3R(t);
						return;
					}
				} else
					// t has two subtrees; replace n with the smallest value in t's right subtree
					case3R(t);
			}
		}
	}

	protected void case1(AbsTree t) {
		//if(t.left!=null) {
		if (t.parent.left == t) {
			t.parent.left = null;

		} else {
			t.parent.right = null;

		}
		//}
	}

	protected void case2(AbsTree t) {

		if (t.left == null) {
			t.value = t.right.value;
			t.right = null;
		} else {
			t.value = t.left.value;
			t.left = null;
		}
		// adapt Part 1 solution and use here
	}

	protected void case3L(AbsTree t) {

		AbsTree maxNode = t.left.max();
		t.value = maxNode.value;
		// System.out.println(t.value);
		maxNode.delete(maxNode.value);

	}

	protected void case3R(AbsTree t) {
		// adapt Part 1 solution and use here
		AbsTree minNode = t.right.min();
		t.value = minNode.value;
		minNode.delete(minNode.value);
		// System.out.println(t.value);
	}

	private AbsTree find(int n) {
		// adapt Part 1 solution and use here
		AbsTree currentNode = this;
		while (true) {
			if (currentNode == null || currentNode.value == n)
				return currentNode;

			// value is greater than root's key
			if (this.value > n)
				currentNode = currentNode.left;
			else
				// value is less than root's key
				currentNode = currentNode.right;
		}
	}

	public AbsTree min() {
		// adapt Part 1 solution and use here
		AbsTree currentNode = this;
		while (currentNode.left != null) {
			currentNode = currentNode.left;
		}
		return currentNode;
	}

	public AbsTree max() {
		// adapt Part 1 solution and use here
		AbsTree currentNode = this;
		while (currentNode.right != null) {
			currentNode = currentNode.right;
		}
		return currentNode;
	}

	protected int value;
	protected AbsTree left;
	protected AbsTree right;
	protected AbsTree parent;

	protected abstract AbsTree add_node(int n);

	protected abstract void count_duplicates();

	// Additional protected abstract methods, as needed
}

class DupTree extends AbsTree {

	public DupTree(int n) {
		super(n);
		count = 1;

	};

	protected AbsTree add_node(int n) {

		return new DupTree(n);
	}

	protected void count_duplicates() {

		count++;

	}

	protected int count;

}

class Tree extends AbsTree {

	public Tree(int n) {
		super(n);
	}

	protected AbsTree add_node(int n) {
		return new Tree(n);
	}

	protected void count_duplicates() {
		;
	}

	// define additional protected methods here, as needed

}
