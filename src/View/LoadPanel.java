package View;

import View.OthelloGUI;

import javax.swing.*;
import java.awt.*;

class LoadPanel extends JPanel {
	private final OthelloGUI gui;
	private final JProgressBar progressBar;
	private final Timer timer;
	private int progress = 0;

	public LoadPanel(OthelloGUI gui) {
		this.gui = gui;
		setLayout(new GridBagLayout());
		setBackground(new Color(34, 139, 34));

		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(gui.getWidth() / 2, gui.getWidth() / 20));
		progressBar.setFont(new Font("Monospaced", Font.BOLD, 14));

		JLabel titleLabel = new JLabel("Othello Game");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
		titleLabel.setForeground(Color.WHITE);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 30, 0);
		add(titleLabel, gbc);

		gbc.gridy = 1;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(progressBar, gbc);

		timer = new Timer(30, e -> updateProgress());
		startProgress();
	}

	public void startProgress() {
		progress = 0;
		progressBar.setValue(0);
		timer.start();
	}

	private void updateProgress() {
		progress++;
		progressBar.setValue(progress);

		int dots = progress % 4;
		StringBuilder loadStr = new StringBuilder("Loading");
		for (int i = 0; i < dots; i++) loadStr.append('.');
		String loadString = String.format("%-10s%3d%%", loadStr, (Object) progress);
		progressBar.setString(loadString);

		if (progress >= 100) {
			timer.stop();
			Timer transitionTimer = new Timer(500, e -> {
				gui.showHome();
				((Timer) e.getSource()).stop();
			});
			transitionTimer.setRepeats(false);
			transitionTimer.start();
		}
	}
}
