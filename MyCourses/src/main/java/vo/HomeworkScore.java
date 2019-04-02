package vo;

/**
 * 作业成绩VO
 * <br>
 * created on 2019/04/01
 *
 * @author 巽
 **/
public class HomeworkScore {
	public Long submissionId;
	public String studentId;
	public Double score;

	public HomeworkScore(Long submissionId, String studentId, Double score) {
		this.submissionId = submissionId;
		this.studentId = studentId;
		this.score = score;
	}
}
