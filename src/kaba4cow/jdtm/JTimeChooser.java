package kaba4cow.jdtm;

import java.awt.GridLayout;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A panel containing spinners to select a specific time (hours, minutes, and
 * seconds). Provides options to customize step sizes for each time unit and to
 * receive notifications when the selected time changes.
 */
public class JTimeChooser extends JPanel implements ChangeListener {

	private static final long serialVersionUID = 1L;

	public static final int HOURS_MINUTES = 0;
	public static final int HOURS_MINUTES_SECONDS = 1;
	public static final int MINUTES_SECONDS = 2;

	private final List<TimeChooserListener> listeners;

	private final int form;

	private final SpinnerNumberModel hourModel;
	private final JSpinner hourSpinner;

	private final SpinnerNumberModel minuteModel;
	private final JSpinner minuteSpinner;

	private final SpinnerNumberModel secondModel;
	private final JSpinner secondSpinner;

	private int hourStepSize;
	private int minuteStepSize;
	private int secondStepSize;

	private boolean ignoreChangeEvent;

	/**
	 * Constructs a new JTimeChooser with the specified time format.
	 *
	 * @param form The time format to use. Valid values are HOURS_MINUTES,
	 *             HOURS_MINUTES_SECONDS, or MINUTES_SECONDS.
	 */
	public JTimeChooser(int form) {
		super();
		this.form = form;
		this.hourStepSize = 1;
		this.minuteStepSize = 1;
		this.secondStepSize = 1;
		this.ignoreChangeEvent = false;
		setLayout(new GridLayout(1, 0));
		listeners = new ArrayList<>();

		hourModel = new SpinnerNumberModel(12, -hourStepSize, 24, hourStepSize);
		hourSpinner = new JSpinner(hourModel);
		hourSpinner.addChangeListener(this);
		if (form == HOURS_MINUTES || form == HOURS_MINUTES_SECONDS)
			add(hourSpinner);

		minuteModel = new SpinnerNumberModel(0, -minuteStepSize, 60, minuteStepSize);
		minuteSpinner = new JSpinner(minuteModel);
		minuteSpinner.addChangeListener(this);
		add(minuteSpinner);

		secondModel = new SpinnerNumberModel(0, -secondStepSize, 60, secondStepSize);
		secondSpinner = new JSpinner(secondModel);
		secondSpinner.addChangeListener(this);
		if (form == HOURS_MINUTES_SECONDS || form == MINUTES_SECONDS)
			add(secondSpinner);
	}

	@Override
	public void setEnabled(boolean enabled) {
		hourSpinner.setEnabled(enabled);
		minuteSpinner.setEnabled(enabled);
		secondSpinner.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	/**
	 * Adds a listener to receive notifications when the time changes.
	 *
	 * @param listener The listener to add.
	 */
	public void addTimeChooserListener(TimeChooserListener listener) {
		listeners.add(listener);
	}

	private void fireTimeChanged() {
		LocalTime newTime = getTime();
		for (TimeChooserListener listener : listeners)
			listener.onTimeChanged(this, newTime);
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		if (ignoreChangeEvent)
			return;
		ignoreChangeEvent = true;
		Integer hour = (Integer) hourSpinner.getValue();
		Integer minute = (Integer) minuteSpinner.getValue();
		Integer second = (Integer) secondSpinner.getValue();
		if (second < 0) {
			minute -= minuteStepSize;
			second = 50;
		} else if (second >= 60) {
			minute += minuteStepSize;
			second = 0;
		}
		if (minute < 0) {
			hour -= hourStepSize;
			minute = 50;
		} else if (minute >= 60) {
			hour += hourStepSize;
			minute = 0;
		}
		if (hour < 0)
			hour = 23;
		else if (hour > 23)
			hour = 0;
		setHour(hour);
		setMinute(minute);
		setSecond(second);
		fireTimeChanged();
		ignoreChangeEvent = false;
	}

	private void setHour(int hour) {
		hourSpinner.setValue(hour - hour % hourStepSize);
	}

	private void setMinute(int minute) {
		minuteSpinner.setValue(minute - minute % minuteStepSize);
	}

	private void setSecond(int second) {
		secondSpinner.setValue(second - second % secondStepSize);
	}

	/**
	 * Sets the current time of the time chooser.
	 *
	 * @param time The new time to set.
	 */
	public void setTime(LocalTime time) {
		setSecond(time.getSecond());
		setMinute(time.getMinute());
		setHour(time.getHour());
		fireTimeChanged();
	}

	/**
	 * Gets the current selected time from the time chooser.
	 *
	 * @return The current selected time.
	 */
	public LocalTime getTime() {
		int hour = (form == HOURS_MINUTES || form == HOURS_MINUTES_SECONDS) ? (int) hourSpinner.getValue() : 0;
		int minute = (int) minuteSpinner.getValue();
		int second = (form == HOURS_MINUTES_SECONDS || form == MINUTES_SECONDS) ? (int) secondSpinner.getValue() : 0;
		return LocalTime.of(hour, minute, second);
	}

	/**
	 * Sets the step size for hours.
	 *
	 * @param hourStepSize The new step size for hours.
	 * @throws IllegalArgumentException if the step size is less than or equal to 0
	 *                                  or greater than 24.
	 */
	public void setHourStepSize(int hourStepSize) {
		if (hourStepSize <= 0)
			throw new IllegalArgumentException("Hour step size must be > 0");
		if (hourStepSize > 24)
			throw new IllegalArgumentException("Hour step size must be <= 24");
		this.hourStepSize = hourStepSize;
		this.hourModel.setMinimum(-hourStepSize);
		this.hourModel.setStepSize(hourStepSize);
		setHour((int) hourSpinner.getValue());
	}

	/**
	 * Sets the step size for minutes.
	 *
	 * @param minuteStepSize The new step size for minutes.
	 * @throws IllegalArgumentException if the step size is less than or equal to 0
	 *                                  or greater than 60.
	 */
	public void setMinuteStepSize(int minuteStepSize) {
		if (minuteStepSize <= 0)
			throw new IllegalArgumentException("Minute step size must be > 0");
		if (minuteStepSize > 60)
			throw new IllegalArgumentException("Minute step size must be <= 60");
		this.minuteStepSize = minuteStepSize;
		this.minuteModel.setMinimum(-minuteStepSize);
		this.minuteModel.setStepSize(minuteStepSize);
		setMinute((int) minuteSpinner.getValue());
	}

	/**
	 * Sets the step size for seconds.
	 *
	 * @param secondStepSize The new step size for seconds.
	 * @throws IllegalArgumentException if the step size is less than or equal to 0
	 *                                  or greater than 60.
	 */
	public void setSecondStepSize(int secondStepSize) {
		if (secondStepSize <= 0)
			throw new IllegalArgumentException("Second step size must be > 0");
		if (secondStepSize > 60)
			throw new IllegalArgumentException("Second step size must be <= 60");
		this.secondStepSize = secondStepSize;
		this.secondModel.setMinimum(-secondStepSize);
		this.secondModel.setStepSize(secondStepSize);
		setSecond((int) secondSpinner.getValue());
	}

	/**
	 * Sets the step sizes for hours, minutes, and seconds.
	 *
	 * @param hourStepSize   The new step size for hours.
	 * @param minuteStepSize The new step size for minutes.
	 * @param secondStepSize The new step size for seconds.
	 * @throws IllegalArgumentException if hour step size is less than or equal to 0
	 *                                  or greater than 24, or if minute step size
	 *                                  is less than or equal to 0 or greater than
	 *                                  60, or if second step size is less than or
	 *                                  equal to 0 or greater than 60
	 */
	public void setStepSizes(int hourStepSize, int minuteStepSize, int secondStepSize) {
		setHourStepSize(hourStepSize);
		setMinuteStepSize(minuteStepSize);
		setSecondStepSize(secondStepSize);
	}

	/**
	 * Enables tooltips for the spinners. Hour spinner will display "Hours", minute
	 * spinner will display "Minutes" and second spinner will display "Seconds"
	 */
	public void enableToolTips() {
		hourSpinner.setToolTipText("Hours");
		minuteSpinner.setToolTipText("Minutes");
		secondSpinner.setToolTipText("Seconds");
	}

	/**
	 * Disables tooltips for the spinners.
	 */
	public void disableToolTips() {
		hourSpinner.setToolTipText(null);
		minuteSpinner.setToolTipText(null);
		secondSpinner.setToolTipText(null);
	}

	/**
	 * Listener interface for receiving notifications when the time changes in a
	 * JTimeChooser.
	 */
	public static interface TimeChooserListener {

		/**
		 * Called when the time changes in the associated JTimeChooser component.
		 *
		 * @param source  The JTimeChooser component where the time change occurred.
		 * @param newTime The time selected in the JTimeChooser.
		 */
		public void onTimeChanged(JTimeChooser source, LocalTime newTime);

	}

}
