package internal_competition;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SimplestFormatter extends Formatter{
	public String format(LogRecord rec) {
        return  rec.getMessage();
    }
}