package kaba4cow.jdtm;

import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

/**
 * A panel that displays a clock with hour, minute, and second hands. The clock
 * can display either real-time or a specified time.
 */
public class JClockPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private boolean realTime;
	private LocalTime clockTime;
	private double radius;

	/**
	 * Constructs a JClockPanel that displays the current time.
	 */
	public JClockPanel() {
		super();
		realTime = true;
		clockTime = LocalTime.now();
		radius = 1d;
		Timer timer = new Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (realTime) {
					clockTime = LocalTime.now();
					repaint();
				}
			}
		});
		timer.setInitialDelay(0);
		timer.start();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D graphics = (Graphics2D) g;

		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;
		double radius = this.radius * Math.min(centerX, centerY);

		double hourDistance = 0.9d;
		double hourArrowRadius = 0.5d * hourDistance;
		double minuteArrowRadius = 0.7d * hourDistance;
		double secondArrowRadius = 0.9d * hourDistance;

		double delta12 = 1d / 12d;
		double delta60 = 1d / 60d;

		graphics.setStroke(new BasicStroke(1f));
		for (int minute = 0; minute < 60; minute++)
			if (minute % 5 != 0) {
				double angle = calculateAngle(minute * delta60);
				int x = centerX + (int) (hourDistance * radius * Math.cos(angle));
				int y = centerY + (int) (hourDistance * radius * Math.sin(angle));
				graphics.setColor(UIManager.getColor("textInactiveText"));
				graphics.fillOval(x, y, 3, 3);
			}
		for (int hour = 1; hour <= 12; hour++) {
			double angle = calculateAngle(hour * delta12);
			int x = (int) (hourDistance * radius * Math.cos(angle));
			int y = (int) (hourDistance * radius * Math.sin(angle));
			graphics.setColor(UIManager.getColor("textText"));
			drawCenteredString(graphics, Integer.toString(hour), centerX + x, centerY + y);
		}

		double secondValue = clockTime.getSecond() * delta60;
		double minuteValue = (clockTime.getMinute() + secondValue) * delta60;
		double hourValue = (clockTime.getHour() + minuteValue) * delta12;
		graphics.setColor(UIManager.getColor("textText"));
		{
			double angle = calculateAngle(secondValue);
			int x = (int) (secondArrowRadius * radius * Math.cos(angle));
			int y = (int) (secondArrowRadius * radius * Math.sin(angle));
			graphics.setStroke(new BasicStroke(1f));
			graphics.drawLine(centerX, centerY, centerX + x, centerY + y);
		}
		{
			double angle = calculateAngle(minuteValue);
			int x = (int) (minuteArrowRadius * radius * Math.cos(angle));
			int y = (int) (minuteArrowRadius * radius * Math.sin(angle));
			graphics.setStroke(new BasicStroke(1.5f));
			graphics.drawLine(centerX, centerY, centerX + x, centerY + y);
		}
		{
			double angle = calculateAngle(hourValue);
			int x = (int) (hourArrowRadius * radius * Math.cos(angle));
			int y = (int) (hourArrowRadius * radius * Math.sin(angle));
			graphics.setStroke(new BasicStroke(2f));
			graphics.drawLine(centerX, centerY, centerX + x, centerY + y);
		}
		graphics.fillOval(centerX - 3, centerY - 3, 6, 6);
	}

	private double calculateAngle(double value) {
		return 2d * Math.PI * value - 0.5d * Math.PI;
	}

	private void drawCenteredString(Graphics graphics, String text, int textX, int textY) {
		FontMetrics metrics = graphics.getFontMetrics();
		int x = textX - metrics.stringWidth(text) / 2;
		int y = textY - metrics.getHeight() / 2 + metrics.getAscent();
		graphics.drawString(text, x, y);
	}

	/**
	 * Gets the current time displayed on the clock.
	 *
	 * @return The current time.
	 */
	public LocalTime getClockTime() {
		return clockTime;
	}

	/**
	 * Sets the time to be displayed on the clock. If null is provided, the clock
	 * will display the current real-time.
	 *
	 * @param time The time to display on the clock.
	 */
	public void setClockTime(LocalTime time) {
		if (time == null) {
			realTime = true;
			clockTime = LocalTime.now();
		} else {
			realTime = false;
			clockTime = time;
		}
		repaint();
	}

	/**
	 * Sets the scale of the clock.
	 *
	 * @param radius The radius of the clock.
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

}
