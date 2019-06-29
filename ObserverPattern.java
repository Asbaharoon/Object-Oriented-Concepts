
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ObserverPatternExample {

	public static void main(String args[]) {
		Sales sales = new Sales();
		Table table = new Table();
		PieChart pieChart = new PieChart();
		BarChart barChart = new BarChart();
		sales.addObserver(table);
		sales.addObserver(pieChart);
		sales.addObserver(barChart);
		sales.generate();

	}
}


class Sales extends Observable {

	int[] values = { 10000, 20000, 30000, 40000, 50000 }; // initial data

	void generate() {
		for (int i = 0; i < 12; i++) { // iterate once for each month

			int temp = values[0];
			for (int j = 0; j < 5; j++)
				values[j] = values[(j + 1) % 5];
			values[4] = temp;

			setChanged();
			notifyObservers();

			try {
				Thread.sleep(5); // takes time to generate new data
			} catch (Exception e) {
			}
		}
	}
}

// ****************** TABLE ************************


@SuppressWarnings("serial")
class Table extends JFrame implements Observer {

	String[] columns = new String[] { "Region", "Sales" };
	Object[][] data = new Object[][] { { "North", 0 }, { "South", 0 }, { "Central", 0 }, { "East", 0 }, { "West", 0 } };

	DefaultTableModel model = new DefaultTableModel(data, columns);
	JTable table = new JTable(model);

	public Table() {
		table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 24));
		add(new JScrollPane(table));
		setTitle("Table Example");
		setBounds(1800, 600, 500, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public void update(Observable obj, Object arg) {

		Sales salesObj = (Sales) obj;
		for (int i = 0; i < 5; i++)
			table.setValueAt(salesObj.values[i], i, 1);
	}
}

// ****************** CHART ************************


class Chart extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;

	String title = "Regional Sales Data";

	int[] values = new int[] { 0, 0, 0, 0, 0 };
	String[] labels = new String[] { "North", "South", "Central", "East", "West" };
	Color[] colors = new Color[] { Color.red, Color.orange, Color.yellow, Color.green, Color.blue };
	JFrame frame = new JFrame();

	public Chart(int x, int y, int w, int h) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setBounds(x, y, w, h);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.setVisible(true);
	}

	public void update(Observable obj, Object arg) {

		Sales salesObj = (Sales) obj;
		values = salesObj.values;
		repaint();

	}
}

// ****************** PIE CHART ************************


@SuppressWarnings("serial")
class PieChart extends Chart {
	public PieChart() {
		super(1300, 600, 500, 500);
		frame.setTitle("Pie Chart View");
	}

	public void paint(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		Rectangle area = getBounds();

		double total = 0.0D;

		for (int i = 0; i < values.length; i++) {
			total += values[i];
		}
		double curValue = 0.0D;
		int startAngle = 0;
		for (int i = 0; i < values.length; i++) {
			startAngle = (int) (curValue * 360 / total);
			int arcAngle = (int) (values[i] * 360 / total);
			g2d.setColor(colors[i]);
			g2d.fillArc(area.x, area.y, area.width, area.height, startAngle, arcAngle);
			curValue += values[i];
		}
	}
}

//****************** BAR CHART ************************



@SuppressWarnings("serial")
class BarChart extends Chart {

	public BarChart() {
		super(800, 600, 500, 500);
		frame.setTitle("Bar Chart View");
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (values == null || values.length == 0) {
			return;
		}
		double minValue = 0;
		double maxValue = 0;
		for (int i = 0; i < values.length; i++) {
			if (minValue > values[i])
				minValue = values[i];
			if (maxValue < values[i])
				maxValue = values[i];
		}

		Dimension dim = getSize();
		int panelWidth = dim.width;
		int panelHeight = dim.height;
		int barWidth = panelWidth / values.length;

		Font titleFont = new Font("Arial", Font.BOLD, 24);
		FontMetrics titleFontMetrics = g.getFontMetrics(titleFont);

		Font labelFont = new Font("Arial", Font.BOLD, 24);
		FontMetrics labelFontMetrics = g.getFontMetrics(labelFont);

		int titleWidth = titleFontMetrics.stringWidth(title);
		int stringHeight = titleFontMetrics.getAscent();
		int stringWidth = (panelWidth - titleWidth) / 2;
		g.setFont(titleFont);
		g.drawString(title, stringWidth, stringHeight);

		int top = titleFontMetrics.getHeight();
		int bottom = labelFontMetrics.getHeight();
		if (maxValue == minValue) {
			return;
		}
		double scale = (panelHeight - top - bottom) / (maxValue - minValue);
		stringHeight = panelHeight - labelFontMetrics.getDescent();
		g.setFont(labelFont);

		for (int j = 0; j < values.length; j++) {
			int valueP = j * barWidth + 1;
			int valueQ = top;
			int height = (int) (values[j] * scale);
			if (values[j] >= 0) {
				valueQ += (int) ((maxValue - values[j]) * scale);
			} else {
				valueQ += (int) (maxValue * scale);
				height = -height;
			}
			g.setColor(colors[j]);
			g.fillRect(valueP, valueQ, barWidth - 2, height);
			g.setColor(Color.black);
			g.drawRect(valueP, valueQ, barWidth - 2, height);
			int labelWidth = labelFontMetrics.stringWidth(labels[j]);
			stringWidth = j * barWidth + (barWidth - labelWidth) / 2;
			g.drawString(labels[j], stringWidth, stringHeight);
		}
	}
}
