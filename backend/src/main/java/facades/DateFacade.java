/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author marcg
 */
public class DateFacade {

    private static SimpleDateFormat sdf;
    private static DateFacade instance;

    public static DateFacade getDateFacade(String format) {
        if (instance == null) {
            sdf = new SimpleDateFormat(format);
            instance = new DateFacade();
        }
        return instance;
    }

    public String makeDate(int years, int months, int days, int hours, int minutes, int seconds) {
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.YEAR, years);
        c.add(Calendar.MONTH, months);
        c.add(Calendar.DATE, days); //same with c.add(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.HOUR, hours);
        c.add(Calendar.MINUTE, minutes);
        c.add(Calendar.SECOND, seconds);

        Date currentDatePlusOne = c.getTime();

        return sdf.format(currentDatePlusOne);
    }
    
    public Date getDate(String date) throws ParseException{
        return sdf.parse(date);
    }

}
