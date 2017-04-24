package ec.app.aspga.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.intelectin.myt.enums.DayPeriod;

import ec.app.aspga.DayPlanGene;

/**
 * Esta eh a classe que contem todos os planos diarios
 */
public class StudyPlanGenerated implements StudyPlan {
	List<DayPlanGene> dpgs;

	private StudyPlanGenerated(DayPlanGene[] dpgs) {
		super();
		this.dpgs = new ArrayList<DayPlanGene>(Arrays.asList(dpgs));
	}

	public static StudyPlanGenerated newInstance(DayPlanGene[] dpgs) {
		return new StudyPlanGenerated(dpgs);
	}

	@Override
	public String toString() {
		return printWeeklyHtmlStudyPlan();
	}

	@SuppressWarnings("null")
	private String printWeeklyHtmlStudyPlan() {
		String output = new String("");
		output += "<ul>"; // semana
		for (DayPlanGene dayPlanGene : dpgs) {
			output += "<li>"; // dia
			output += "" + ("<h3>" + dayPlanGene.getDay().getName() + "</h3>");

			output += "<ul>"; // periodo
			output += "<li>"; // periodo 1
			output += "" + ("<h4>" + DayPeriod.MORNING.getName() + ":</h4>");
			SubjectWorkload[] subjectWorkloads = dayPlanGene.getMorning();
			if (subjectWorkloads != null && subjectWorkloads.length != 0) {
				output += "<ul>";
				for (SubjectWorkload subjectWorkload : subjectWorkloads) {
					output += "<li>" + transformSubjectWorkloadInHours(subjectWorkload) + "</li>";
				}
				output += "</ul>";
			}
			output += "</li>"; // periodo 1

			output += "<li>";// periodo 2
			output += "" + ("<h4>" + DayPeriod.AFTERNOON.getName() + ":</h4>");
			subjectWorkloads = dayPlanGene.getAfternoon();
			if (subjectWorkloads != null && subjectWorkloads.length != 0) {
				output += "<ul>";
				for (SubjectWorkload subjectWorkload : subjectWorkloads) {
					output += "<li>" + transformSubjectWorkloadInHours(subjectWorkload) + "</li>";
				}
				output += "</ul>";
			}
			output += "</li>"; // periodo 2

			output += "<li>"; // periodo 3
			output += "" + ("<h4>" + DayPeriod.NIGHT.getName() + ":</h4>");
			subjectWorkloads = dayPlanGene.getNight();
			if (subjectWorkloads != null && subjectWorkloads.length != 0) {
				output += "<ul>";
				for (SubjectWorkload subjectWorkload : subjectWorkloads) {
					output += "<li>" + transformSubjectWorkloadInHours(subjectWorkload) + "</li>";
				}
				output += "</ul>";
			}
			output += "</li>"; // periodo 3

			output += "</ul>"; // periodo
			output += "</li>"; // dia
		}
		output += "</ul>"; // semana

		return output;
	}

	private String printWeeklyStudyPlan() {
		String output = new String("");

		for (DayPlanGene dayPlanGene : dpgs) {
			output += "" + ("\n-----" + dayPlanGene.getDay().getName() + "-----\n");

			output += "" + ("\n[" + DayPeriod.MORNING.getName() + "]:\n");
			for (SubjectWorkload subjectWorkload : dayPlanGene.getMorning()) {
				output += "" + transformSubjectWorkloadInHours(subjectWorkload) + "\n";
			}

			output += "" + ("\n[" + DayPeriod.AFTERNOON.getName() + "]:\n");
			for (SubjectWorkload subjectWorkload : dayPlanGene.getAfternoon()) {
				output += "" + transformSubjectWorkloadInHours(subjectWorkload) + "\n";
			}

			output += "" + ("\n[" + DayPeriod.NIGHT.getName() + "]:\n");
			for (SubjectWorkload subjectWorkload : dayPlanGene.getNight()) {
				output += "" + transformSubjectWorkloadInHours(subjectWorkload) + "\n";
			}

		}

		return output;
	}

	/**
	 * Transform the subject and workload to calendar hours format. <br/>
	 * <br/>
	 * 
	 * SubjectX 1h<br/>
	 * or <br/>
	 * SubjectX 30min<br/>
	 * 
	 * @param output
	 * @param subjectWorkload
	 * @return
	 */
	private String transformSubjectWorkloadInHours(SubjectWorkload subjectWorkload) {
		String formated = "";

		float workload = ((float) subjectWorkload.getWorkload()) / (float) 2;
		String time = workload < 1 ? "30min" : workload + "h";
		formated += "" + (subjectWorkload.getSubject().getName() + " " + time);
		return formated;
	}

	public List<DayPlanGene> getDpgs() {
		return dpgs;
	}

	public void setDpgs(List<DayPlanGene> dpgs) {
		this.dpgs = dpgs;
	}

}
