package kaba4cow.example;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.LocalTime;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import kaba4cow.jdtm.JCalendarPanel;
import kaba4cow.jdtm.JClockPanel;
import kaba4cow.jdtm.JTimeChooser;
import kaba4cow.jdtm.JTimeChooser.TimeChooserListener;

public class Example extends JFrame {

	private static final long serialVersionUID = 1L;

	public Example() {
		super();
		setTitle("Example");
		setLayout(new BorderLayout());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		JCalendarPanel calendarPanel = new JCalendarPanel();
		calendarPanel.setBorder(BorderFactory.createTitledBorder("CalendarPanel"));
		add(calendarPanel, BorderLayout.NORTH);

		JClockPanel clockPanel = new JClockPanel();
		clockPanel.setPreferredSize(new Dimension(170, 170));
		clockPanel.setRadius(0.9d);
		clockPanel.setBorder(BorderFactory.createTitledBorder("ClockPanel"));
		add(clockPanel, BorderLayout.CENTER);

		JPanel timePanel = new JPanel();
		timePanel.setBorder(BorderFactory.createTitledBorder("TimeChooser"));
		timePanel.setLayout(new BorderLayout());
		{
			JCheckBox realTimeCheckBox = new JCheckBox("Real Time");
			realTimeCheckBox.setSelected(true);
			timePanel.add(realTimeCheckBox, BorderLayout.WEST);

			JTimeChooser timeChooser = new JTimeChooser(JTimeChooser.HOURS_MINUTES_SECONDS);
			timeChooser.setTime(LocalTime.now());
			timeChooser.setEnabled(false);
			timeChooser.setStepSizes(1, 5, 10);
			timeChooser.enableToolTips();
			timePanel.add(timeChooser, BorderLayout.EAST);

			realTimeCheckBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (realTimeCheckBox.isSelected()) {
						timeChooser.setEnabled(false);
						clockPanel.setClockTime(null);
					} else {
						timeChooser.setEnabled(true);
						clockPanel.setClockTime(timeChooser.getTime());
					}
				}
			});

			timeChooser.addTimeChooserListener(new TimeChooserListener() {
				@Override
				public void onTimeChanged(JTimeChooser source, LocalTime newTime) {
					clockPanel.setClockTime(newTime);
				}
			});
		}
		add(timePanel, BorderLayout.SOUTH);

		pack();
		setFocusable(true);
		setResizable(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Example());
	}

}
