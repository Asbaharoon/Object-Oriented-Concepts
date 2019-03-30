
// CSE 522: Assignment 1, Part 1

class BST_Part1 {

  public static void main(String args[]) {
    Tree tr;
    tr = new Tree(100);
    tr.insert(50);
    tr.insert(125);
    tr.insert(150);
    tr.insert(20);
    tr.insert(75);
    tr.insert(20);
    tr.insert(90);
    tr.delete(20);
    tr.delete(20);
    tr.delete(125);
    tr.delete(150);
    tr.delete(100);
    tr.delete(50);
    tr.delete(75);
    tr.delete(25);
    tr.delete(90);

  }
}

class Tree { // Defines one node of a binary search tree

  public Tree(int n) {
    value = n;
    left = null;
    right = null;
  }

  public void insert(int n) {
    if (value == n)
      return;
    if (value < n)
      if (right == null)
      {
        right = new Tree(n);
        right.parent=this;

      }
      else
        right.insert(n);
    else if (left == null)
    {
      left = new Tree(n);
      left.parent=this;

    }
    else
      left.insert(n);
  }


  public Tree min() {
    // returns the Tree node with the minimum value;
    Tree currentNode=this;
    while (currentNode.left != null)
    {
      currentNode = currentNode.left;
    }
    return currentNode;

  }


  public Tree max() {
    // returns the Tree node with the maximum value;
    Tree currentNode=this;
    while (currentNode.right != null)
    {
      currentNode = currentNode.right;
    }
    return currentNode;
  }


  public Tree find(int n) {
    // returns the Tree node with value n;
    // returns null if n is not present in the tree;
    Tree currentNode=this;
    while(true) {
      if (currentNode==null || currentNode.value==n)
        return currentNode;

      // value is greater than root's key
      if (this.value > n)
        currentNode=currentNode.left;
      else
      // value is less than root's key
      currentNode=currentNode.right;
    }
  }

  public void delete(int n) {
    //
    // *** do not modify this method ***
    //
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

  private void case1(Tree t) {
    // remove leaf node t;
    if(t.parent.left==t) {
      t.parent.left=null;
    }
    else
    {
      t.parent.right=null;
    }
  }

  private void case2(Tree t) {
    // remove internal node t;
    if(t.left==null) {
      t.value = t.right.value;
      t.right = null;
    }
    else {
      t.value = t.left.value;
      t.left = null;
    }
  }

  private void case3L(Tree t) {
    // replace t.value with the largest value, v, in
    // t's left subtree; then delete value v from tree;
    Tree maxNode=t.left.max();
    t.value=maxNode.value;
    maxNode.delete(maxNode.value);
  }

  private void case3R(Tree t) {
    // replace t.value with the smallest value, v, in
    // t's right subtree; then delete value v from tree;
    Tree minNode=t.right.min();
    t.value=minNode.value;
    minNode.delete(minNode.value);
  }

  protected int value;
  protected Tree left;
  protected Tree right;
  protected Tree parent;
}


