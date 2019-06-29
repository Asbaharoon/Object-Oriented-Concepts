import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

class TreeGUIDriver {
	public static void main(String[] x) {
		new TreeGUI();
	}
}

public class TreeGUI extends JFrame {
	private static final long serialVersionUID = 1L;

	AbsTree tree;
	String nodeValue;

	TreeMemento tm = new TreeMemento();

	boolean is_new_tree = true;

	Choice tree_kind, element_kind;

	public static Font font = new Font("Comic Sans MS", Font.BOLD, 24);
	JTextField input_elem_text, input_elem_text2, min_text, max_text;

	JButton insertButton, deleteButton, undoButton;
	JButton minButton, maxButton, clearButton;

	JPanel inputPanel;
	OutputPanel outputPanel;

	int last_x, last_y;

	public TreeGUI() {

		super("GUI for Tree Operations");
		Label tree_kind_label;
		JPanel input1, input2;

		// The JPanel input1 details follow

		input1 = new JPanel(new FlowLayout()); // FlowLayout default

		tree_kind_label = new Label("Tree Kind:");
		tree_kind_label.setFont(font);
		input1.add(tree_kind_label);
		tree_kind = new Choice();
		tree_kind.setFont(font);
		tree_kind.addItem("Normal Tree");
		tree_kind.setFont(font);
		tree_kind.addItem("Dup Tree");
		tree_kind.setFont(font);
		input1.add(tree_kind);

		input_elem_text = new JTextField("integer");
		input_elem_text.setFont(font);
		input_elem_text.requestFocus(true);
		input_elem_text.selectAll();
		input_elem_text.setEditable(true);
		insertButton = new JButton("Insert");
		insertButton.setFont(font);
		input1.add(insertButton);
		input1.add(input_elem_text);

		input_elem_text2 = new JTextField("integer");
		input_elem_text2.setFont(font);
		input_elem_text2.requestFocus(true);
		input_elem_text2.selectAll();
		input_elem_text2.setEditable(true);
		deleteButton = new JButton("Delete");
		deleteButton.setFont(font);
		input1.add(deleteButton);
		input1.add(input_elem_text2);

		undoButton = new JButton("Undo");
		undoButton.setFont(font);
		input1.add(undoButton);

		// The JPanel input2 details follow

		input2 = new JPanel(new FlowLayout()); // FlowLayout default

		minButton = new JButton("Minimum");
		minButton.setFont(font);
		input2.add(minButton);
		min_text = new JTextField(10);
		min_text.setFont(font);
		min_text.setEditable(false);
		input2.add(min_text);

		maxButton = new JButton("Maximum");
		maxButton.setFont(font);
		input2.add(maxButton);
		max_text = new JTextField(10);
		max_text.setFont(font);
		max_text.setEditable(false);
		input2.add(max_text);

		clearButton = new JButton("Clear");
		clearButton.setFont(font);
		input2.add(clearButton);

		// The JPanels inputPanel and outputPanel details follow

		inputPanel = new JPanel(new BorderLayout());
		outputPanel = new OutputPanel();

		inputPanel.add("North", input1);
		inputPanel.add("South", input2);

		// Add Button Listeners here for:
		// min, max, clear, insert, delete, undo

		minButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				min_text.setText(Integer.toString(tree.min().value));
			}
		});

		maxButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				max_text.setText(Integer.toString(tree.max().value));
			}
		});

		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				is_new_tree = true;
				input_elem_text.setText("");
				min_text.setText("");
				max_text.setText("");
				outputPanel.clearPanel();
				tm.clear();
			}
		});

		insertButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean flag = true;
				String s = input_elem_text.getText();
				if (is_new_tree) {
					try {
						if (tree_kind.getSelectedIndex() == 0)
							tree = new Tree(Integer.parseInt(s));
						else
							tree = new DupTree(Integer.parseInt(s));
					} catch (NumberFormatException e2) {
						JOptionPane.showMessageDialog(null, "Bad integer: " + s + ". Please re-enter.");
						return;
					}
					is_new_tree = false;
					tm.set_state(tree);
				} // end of if is_new_tree
				else {
					try {
						if (tree == null) {
							JOptionPane.showMessageDialog(null, "Tree is empty. Must insert a value");
							is_new_tree = true;
							return;
						} else
							flag = tree.insert(Integer.parseInt(s));
					} catch (NumberFormatException e2) {
						JOptionPane.showMessageDialog(null, "Bad integer: " + s + ". Please re-enter.");
						return;
					}
					if (flag)
						tm.set_state(tree);
				}

				outputPanel.drawTree(tree);
				input_elem_text.selectAll();
			}
		});

		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = input_elem_text2.getText();
				int n = 0;
				try {
					n = Integer.parseInt(s);
				} catch (NumberFormatException e2) {
					JOptionPane.showMessageDialog(null, "Bad integer: " + s + ". Please re-enter.");
					return;
				}
				if (tree == null) {
					JOptionPane.showMessageDialog(null, "Tree is empty. Must insert a value.");
					is_new_tree = true;
					return;
				}
				if (tree.value == n && tree.right == null && tree.left == null && tree.get_count()==1) {
					tm.set_state(tree);

					outputPanel.clearPanel();

					return;
				} else {
					boolean b = tree.delete(n);
					if (b)
						tm.set_state(tree);
					else
						JOptionPane.showMessageDialog(null, "Unable to delete. Not in tree " + n);
				}

				outputPanel.drawTree(tree);
				input_elem_text2.selectAll();
			}
		});
		undoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// implement this method

				tm.get_state();
				if (tree != null) {
					tree = tm.get_state();
					if (tree != null) {
						outputPanel.drawTree(tree);
						tm.set_state(tree);
					}

					else {
						is_new_tree = true;
						outputPanel.clearPanel();
						return;
					}
				} else {
					JOptionPane.showMessageDialog(null, "Tree is empty. Must insert a value.");
					is_new_tree = true;
				}
			}
		});

		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		add("North", inputPanel);
		add("Center", outputPanel);

		setSize(1400, 1000); // for the frame
		setVisible(true);
	}

}

// **************** The OutputPanel class *************************

// The output area, where the tree is drawn
class OutputPanel extends Panel {
	private static final long serialVersionUID = 1L;
	Image treeImage; // buffer to keep the latest tree representation
	int imageWidth, imageHeight;

	// draw the tree on the output panel
	public void drawTree(AbsTree tree) {
		Graphics g;
		Dimension d = this.size();
		if (imageWidth != d.width || imageHeight != d.height) {
			treeImage = createImage(d.width, d.height);
			imageWidth = d.width;
			imageHeight = d.height;
		}
		;
		// Clear the image
		g = treeImage.getGraphics();
		g.setFont(TreeGUI.font);
		g.setColor(getBackground());
		g.fillRect(0, 0, d.width, d.height);
		g.setColor(Color.black);

		drawNode(g, imageWidth / 2, tree, imageWidth / 2, 0);
		getGraphics().drawImage(treeImage, 0, 0, this);
	}

	private void drawNode(Graphics g, int subtreeW, AbsTree tree, int x, int y) {
		AbsTree left, right;
		g.drawString(tree.get_value(), x - 15, y + 15);
		left = tree.left;
		right = tree.right;
		if (left != null) {
			g.drawLine(x, y + 15, x - subtreeW / 2, y + 115);
			drawNode(g, subtreeW / 2, left, x - subtreeW / 2, y + 115);
		}
		;
		if (right != null) {
			g.drawLine(x, y + 15, x + subtreeW / 2, y + 115);
			drawNode(g, subtreeW / 2, right, x + subtreeW / 2, y + 115);
		}
	}

	public void clearPanel() {
		Graphics g;
		// Clear the image
		g = treeImage.getGraphics();
		g.setColor(getBackground());
		g.fillRect(0, 0, imageWidth, imageHeight);
		g.setColor(Color.black);
		getGraphics().drawImage(treeImage, 0, 0, this);
	}

}

//**************** The TreeMemento class *************************

class TreeMemento {
	private Stack<AbsTree> state = new Stack<AbsTree>();

	public void set_state(AbsTree t) {
		state.push(t.clone());
	}

	public void clear() {
		while (!state.isEmpty()) {
			state.pop();

		}
	}

	public AbsTree get_state() {
		if (state.isEmpty())
			return null; // must handle this null on calling side
		AbsTree tree = state.pop();
		return tree;
	}
}

// *********************************************************
//  Binary Search Tree (of integers) with Duplicates in Java
// *********************************************************

abstract class AbsTree implements Cloneable {
	public AbsTree(int n) {
		value = n;
		left = null;
		right = null;
	}

// insert() calls protected abstract add_node()

	public AbsTree clone() {
		AbsTree tr = null;
		try {
			tr = (AbsTree) super.clone();
		} catch (Exception e) {
		}
		if (left != null)
			tr.left = left.clone();
		if (right != null)
			tr.right = right.clone();
		return tr;
	}

	void print() {
		if (left != null)
			left.print();
		print_node();
		if (right != null)
			right.print();
	}

	public boolean insert(int n) {
		if (value == n) {
			boolean result = count_duplicates();
			return result;
		} else if (value < n)
			if (right == null) {
				right = add_node(n);
				return true;
			} else {
				boolean result = right.insert(n);
				return result;
			}
		else if (left == null) {
			left = add_node(n);
			return true;
		} else {
			boolean result = left.insert(n);
			return result;
		}
	}

	public boolean delete(int n) { // assume > 1 nodes in tree
		AbsTree t = find(n);

		if (t == null) { // n is not in the tree
			return false;
		}

		if (t.get_count() > 1) {
			t.set_count(t.get_count() - 1);
			return true;
		}

		if (t.left == null && t.right == null) { // n is a leaf value
			if (t != this) {
				case1(t, this);
				return true;
			} else
				return false;

		}
		if (t.left == null || t.right == null) { // t has one subtree only
			if (t != this) { // check whether t is the root of the tree
				case2(t, this);
				return true;
			} else {
				if (t.right == null)
					case3(t, "left");
				else
					case3(t, "right");
				return true;
			}
		}
		// t has two subtrees; go with smallest in right subtree of t
		case3(t, "right");
		return true;
	}

	protected void case1(AbsTree t, AbsTree root) { // remove the leaf
		if (t.value > root.value)
			if (root.right == t)
				root.right = null;
			else
				case1(t, root.right);
		else if (root.left == t)
			root.left = null;
		else
			case1(t, root.left);
	}

	protected void case2(AbsTree t, AbsTree root) { // remove internal node
		if (t.value > root.value)
			if (root.right == t)
				if (t.right == null)
					root.right = t.left;
				else
					root.right = t.right;
			else
				case2(t, root.right);
		else if (root.left == t)
			if (t.right == null)
				root.left = t.left;
			else
				root.left = t.right;
		else
			case2(t, root.left);
	}

	protected void case3(AbsTree t, String side) { // replace t.value and t.count
		if (side == "right") {
			AbsTree min_right_t = t.right.min();
			if (min_right_t.left == null && min_right_t.right == null)
				case1(min_right_t, this); // min_right_t is a leaf node
			else
				case2(min_right_t, this); // min_right_t is a non-leaf node
			t.value = min_right_t.value;
			t.set_count(min_right_t.get_count());
		} else {
			AbsTree max_left_t = t.left.max();
			if (max_left_t.left == null && max_left_t.right == null)
				case1(max_left_t, this); // max_left_t is a leaf node
			else
				case2(max_left_t, this); // max_left_t is a non-leaf node
			t.value = max_left_t.value;
			t.set_count(max_left_t.get_count());

		}
	}

	private AbsTree find(int n) {
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

	public AbsTree min() {
		if (left != null)
			return left.min();
		else
			return this;
	}

	public AbsTree max() {
		if (right != null)
			return right.max();
		else
			return this;
	}

	protected int value;
	protected AbsTree left;
	protected AbsTree right;

	// Protected Abstract Methods

	protected abstract AbsTree add_node(int n);

	protected abstract boolean count_duplicates();

	protected abstract void print_node();

	protected abstract String get_value();

	protected abstract int get_count();

	protected abstract void set_count(int v);
}

class Tree extends AbsTree {
	public Tree(int n) {
		super(n);
	}

	protected AbsTree add_node(int n) {
		return new Tree(n);
	}

	protected boolean count_duplicates() {
		return false;
	}

	protected int get_count() {
		return 1;
	}

	protected void set_count(int v) {

	}

	public String get_value() {
		return (value + " ");
	}

	protected void print_node() {
		System.out.print(value + "  ");
	}
}

class DupTree extends AbsTree {
	public DupTree(int n) {
		super(n);
		count = 1;
	};

	protected AbsTree add_node(int n) {
		return new DupTree(n);
	}

	protected boolean count_duplicates() {
		count++;
		return true;
	}

	protected void print_node() {
		System.out.print(value + "/" + count + " ");
	}

	public String get_value() {
		return (value + "/" + count);
	}

	protected int get_count() {
		return count;
	}

	protected void set_count(int v) {
		count = v;
	}

	protected int count;
}
