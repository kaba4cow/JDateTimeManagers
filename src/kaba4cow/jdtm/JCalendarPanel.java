package kaba4cow.jdtm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * A panel that displays a calendar view with selectable dates. Supports
 * navigation through months and weeks, and provides event notification when a
 * calendar date is changed.
 */
public class JCalendarPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final List<CalendarListener> listeners = new ArrayList<>();

	private final JLabel monthLabel;
	private final JCalendarDayLabel[] dayLabels;

	private LocalDate selectedDate;

	/**
	 * Constructs a new JCalendarPanel. The initial view is set to the current month
	 * and the current date is selected.
	 */
	public JCalendarPanel() {
		super(new BorderLayout());

		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BorderLayout());
		{
			JButton clockButton = new JButton();
			clockButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					selectDate(LocalDate.now());
				}
			});
			Timer timer = new Timer(500, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					clockButton.setText(JDateTimeUtils.formatTitleTime(LocalDateTime.now()));
				}
			});
			timer.setInitialDelay(0);
			timer.start();
			titlePanel.add(clockButton, BorderLayout.NORTH);

			monthLabel = new JLabel();
			monthLabel.setHorizontalAlignment(JLabel.CENTER);
			monthLabel.setVerticalAlignment(JLabel.CENTER);
			titlePanel.add(monthLabel, BorderLayout.CENTER);

			JButton prevMonthButton = new JButton("<");
			prevMonthButton.setMargin(new Insets(1, 1, 1, 1));
			prevMonthButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					selectDate(selectedDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()));
				}
			});
			titlePanel.add(prevMonthButton, BorderLayout.WEST);

			JButton nextMonthButton = new JButton(">");
			nextMonthButton.setMargin(new Insets(1, 1, 1, 1));
			nextMonthButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					selectDate(selectedDate.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth()));
				}
			});
			titlePanel.add(nextMonthButton, BorderLayout.EAST);
		}
		add(titlePanel, BorderLayout.NORTH);

		JPanel daysPanel = new JPanel();
		daysPanel.setLayout(new GridLayout(JDateTimeUtils.getNumberOfWeeks() + 1, JDateTimeUtils.getNumberOfDays()));
		daysPanel.setBorder(BorderFactory.createLineBorder(SystemColor.textInactiveText));
		{
			for (int i = 0; i < JDateTimeUtils.getNumberOfDays(); i++) {
				JLabel weekDayLabel = new JLabel(JDateTimeUtils.getDayName(i));
				weekDayLabel.setHorizontalAlignment(JLabel.CENTER);
				weekDayLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SystemColor.textText));
				daysPanel.add(weekDayLabel);
			}
			dayLabels = new JCalendarDayLabel[JDateTimeUtils.getNumberOfWeeks() * JDateTimeUtils.getNumberOfDays()];
			for (int i = 0; i < dayLabels.length; i++) {
				dayLabels[i] = new JCalendarDayLabel(this);
				daysPanel.add(dayLabels[i]);
			}
		}
		add(daysPanel, BorderLayout.CENTER);

		setFocusable(true);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				switch (event.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					selectDate(selectedDate.minusDays(1));
					return;
				case KeyEvent.VK_RIGHT:
					selectDate(selectedDate.plusDays(1));
					return;
				case KeyEvent.VK_UP:
					selectDate(selectedDate.minusWeeks(1));
					return;
				case KeyEvent.VK_DOWN:
					selectDate(selectedDate.plusWeeks(1));
					return;
				}
			}
		});

		selectDate(LocalDate.now());
	}

	/**
	 * Selects the specified date in the calendar and updates the display. This
	 * method will notify the listeners about the change.
	 *
	 * @param date The date to select.
	 */
	public void selectDate(LocalDate date) {
		monthLabel.setText(JDateTimeUtils.formatMonthYearDate(date));
		LocalDate start = date//
				.with(TemporalAdjusters.firstDayOfMonth())//
				.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
		LocalDate end = date//
				.with(TemporalAdjusters.lastDayOfMonth())//
				.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
		end = end.plusWeeks(JDateTimeUtils.getNumberOfWeeks() - ChronoUnit.DAYS.between(start, end) / 7 - 1)
				.plusDays(1);
		Object[] dates = start.datesUntil(end).toArray();
		for (int i = 0; i < dates.length; i++)
			dayLabels[i].selectDate(date, (LocalDate) dates[i]);
		requestFocus();
		boolean update = !date.equals(selectedDate);
		selectedDate = date;
		if (update)
			for (CalendarListener listener : listeners)
				listener.onDateSelected(date);
	}

	/**
	 * Adds a listener to receive notifications when selected date is changed.
	 *
	 * @param listener The listener to add.
	 */
	public void addListener(CalendarListener listener) {
		listeners.add(listener);
	}

	/**
	 * Listener interface for receiving notifications when JCalendarPanel date is
	 * changed.
	 */
	public static interface CalendarListener {

		/**
		 * Called when selected date is changed in the associated JCalendarPanel
		 * component.
		 *
		 * @param newDate The date selected in JCalendarPanel.
		 */
		public void onDateSelected(LocalDate newDate);

	}

}
