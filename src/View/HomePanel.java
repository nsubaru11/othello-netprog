package View;

import javax.swing.*;
import java.awt.*;


class HomePanel extends JPanel {
	private final JButton startButton;
	private final JButton finishButton;

	public HomePanel(OthelloGUI gui) {
		Dimension dimension = gui.getSize();
		int width = dimension.width;
		int height = dimension.height;
		setSize(dimension);

		setLayout(new GridBagLayout());
		setBackground(gui.getBackground());

		GridBagConstraints gbc = new GridBagConstraints();

		JLabel titleLabel = new JLabel("Othello Game");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
		titleLabel.setForeground(Color.WHITE);
		gbc.insets = new Insets(10, 10, 200, 10);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 5;
		gbc.gridheight = 1;
		add(titleLabel, gbc);

		finishButton = new JButton("Finish");
		finishButton.setFont(new Font("Arial", Font.BOLD, 14));
		finishButton.setBackground(Color.RED);
		finishButton.setForeground(Color.WHITE);
		gbc.insets = new Insets(10, 10, 10, 200);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.gridheight = 2;
		add(finishButton, gbc);

		startButton = new JButton("Start");
		startButton.setFont(new Font("Arial", Font.BOLD, 14));
		gbc.insets = new Insets(10, 10, 10, 0);
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.gridheight = 2;
		add(startButton, gbc);


	}

}

