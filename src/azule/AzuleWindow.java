package azule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AzuleWindow extends JPanel {
	
	Box window = Box.createVerticalBox();
	
	Box buttonBox = Box.createHorizontalBox();
	JButton compile = new JButton("R");
	
	JTabbedPane files = new JTabbedPane();
	JTextArea fileEditor = new JTextArea();
	JScrollPane jspf = new JScrollPane(fileEditor, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	JTabbedPane consoleTabs = new JTabbedPane();
	JTextArea console = new JTextArea();
	JScrollPane jspc = new JScrollPane(console, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	public AzuleWindow() {
		setLayout(new GridLayout());
		fileEditor.setFont(new Font("SansSerif", Font.PLAIN, 16));
		console.setFont(new Font("SansSerif", Font.PLAIN, 16));
		compile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String code = fileEditor.getText();
				AzuleCompiler.compile(code, console, 1, true);
			}
		});
		buttonBox.add(compile);
		window.add(buttonBox, BorderLayout.NORTH);
		files.addTab("File", jspf);
		window.add(files);
		consoleTabs.addTab("Console", jspc);
		window.add(consoleTabs);
		add(window);
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Azule Compiler");
		frame.add(new AzuleWindow());
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 1000);
	}

}
