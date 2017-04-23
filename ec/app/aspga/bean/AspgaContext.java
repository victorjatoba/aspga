package ec.app.aspga.bean;

import java.util.List;

/**
 * Created by flaviokreis on 30/12/14.
 */
public class AspgaContext {

    private static AspgaContext INSTANCE;

    private Subject[]       subjects;
    private Student         student;
    private PeriodAvailable dayPeriodAvailable;
    private PeriodAvailable intelectualAvailable;

    private AspgaContext(){ /* no code */ }

    public static AspgaContext getInstance(){
        if( INSTANCE == null ){
            INSTANCE = new AspgaContext();
        }

        return INSTANCE;
    }

    public Subject[] getSubjects() {
        return subjects;
    }

    public void setSubjects(Subject[] subjects) {
        this.subjects = subjects;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public PeriodAvailable getDayPeriodAvailable() {
        return dayPeriodAvailable;
    }

    public void setDayPeriodAvailable(PeriodAvailable dayPeriodAvailable) {
        this.dayPeriodAvailable = dayPeriodAvailable;
    }

    public PeriodAvailable getIntelectualAvailable() {
        return intelectualAvailable;
    }

    public void setIntelectualAvailable(PeriodAvailable intelectualAvailable) {
        this.intelectualAvailable = intelectualAvailable;
    }
}
