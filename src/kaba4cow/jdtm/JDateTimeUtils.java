package kaba4cow.jdtm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for working with date and time formatting.
 */
public final class JDateTimeUtils {

	private static final String[] suffixes = { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
	private static final String[] dayNames = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
	private static final String[] monthNames = { "January", "February", "March", "April", "May", "June", "July",
			"August", "September", "October", "November", "December" };

	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter titleFormatter = DateTimeFormatter.ofPattern("yyyy - HH:mm:ss");

	private static final int numberOfDays = 7;
	private static final int numberOfWeeks = 6;

	private JDateTimeUtils() {
	}

	/**
     * Gets the abbreviated day name for a given index.
     *
     * @param index the index of the day (0 = Mon, 1 = Tue, ..., 6 = Sun)
     * @return the abbreviated day name
     */
	public static String getDayName(int index) {
		return dayNames[index];
	}

	/**
     * Gets the full month name for a given index.
     *
     * @param index the index of the month (1 = January, 2 = February, ..., 12 = December)
     * @return the full month name
     */
	public static String getMonthName(int index) {
		return monthNames[index];
	}

	/**
     * Formats a LocalDateTime object as a title time string.
     *
     * @param time the LocalDateTime object to format
     * @return the formatted title time string
     */
	public static String formatTitleTime(LocalDateTime time) {
		String suffix;
		int dayOfMonth = time.getDayOfMonth();
		switch (dayOfMonth) {
		case 11:
		case 12:
		case 13:
			suffix = "th";
			break;
		default:
			suffix = suffixes[dayOfMonth % 10];
		}
		return String.format("%s %d%s %s", getMonthName(time.getMonthValue() - 1), dayOfMonth, suffix,
				time.format(titleFormatter));
	}

	/**
     * Formats a LocalDate object as a month-year date string.
     *
     * @param date the LocalDate object to format
     * @return the formatted month-year date string
     */
	public static String formatMonthYearDate(LocalDate date) {
		return String.format("%s %d", getMonthName(date.getMonthValue() - 1), date.getYear());
	}

	/**
     * Formats a LocalDate object as a date string.
     *
     * @param date the LocalDate object to format
     * @return the formatted date string
     */
	public static String formatDate(LocalDate date) {
		return date.format(dateFormatter);
	}

	/**
     * Formats a LocalTime object as a time string.
     *
     * @param time the LocalTime object to format
     * @return the formatted time string
     */
	public static String formatTime(LocalTime time) {
		return time.format(timeFormatter);
	}

	/**
     * Gets the number of days in a week.
     *
     * @return the number of days in a week
     */
	public static int getNumberOfDays() {
		return numberOfDays;
	}

	/**
     * Gets the number of weeks in a month (displayed as in calendar).
     *
     * @return the number of weeks in a month
     */
	public static int getNumberOfWeeks() {
		return numberOfWeeks;
	}

}
