package kaba4cow.jdtm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class JCalendarDayLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	private LocalDate dayDate;

	public JCalendarDayLabel(JCalendarPanel calendarPanel) {
		super();
		setHorizontalAlignment(CENTER);
		setVerticalAlignment(CENTER);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent event) {
				if (event.getButton() == MouseEvent.BUTTON1)
					calendarPanel.selectDate(dayDate);
			}
		});
	}

	public void selectDate(LocalDate selectedDate, LocalDate currentDate) {
		dayDate = currentDate;
		boolean isCurrentDay = dayDate.equals(LocalDate.now());
		boolean isSelectedMonth = dayDate.getMonthValue() == selectedDate.getMonthValue();
		if (dayDate.equals(selectedDate))
			select(isCurrentDay, isSelectedMonth);
		else
			deselect(isCurrentDay, isSelectedMonth);
		setText(Integer.toString(dayDate.getDayOfMonth()));
	}

	private void select(boolean isCurrentDay, boolean isSelectedMonth) {
		if (isCurrentDay || isSelectedMonth) {
			setOpaque(true);
			setBorder(BorderFactory.createDashedBorder(UIManager.getColor("textHighlightText"), 1f, 1f));
			setBackground(UIManager.getColor("activeCaption"));
			setForeground(UIManager.getColor("textHighlightText"));
		} else {
			setOpaque(false);
			setBorder(BorderFactory.createLineBorder(UIManager.getColor("activeCaptionBorder"), 1));
			setForeground(UIManager.getColor("textText"));
		}
	}

	private void deselect(boolean isCurrentDay, boolean isSelectedMonth) {
		if (isCurrentDay) {
			setOpaque(false);
			setBorder(BorderFactory.createLineBorder(UIManager.getColor("activeCaption"), 1));
			setForeground(UIManager.getColor("textText"));
		} else {
			setOpaque(false);
			setBorder(null);
			setForeground(isSelectedMonth ? UIManager.getColor("textText") : UIManager.getColor("textInactiveText"));
		}
	}

}
