package vo;

/**
 * 班级成绩
 * <br>
 * created on 2019/04/02
 *
 * @author 巽
 **/
public class ClassScore {
	public Long studentId;
	public String strId;
	public Double score;

	public ClassScore(Long studentId, String strId, Double score) {
		this.studentId = studentId;
		this.strId = strId;
		this.score = score;
	}
}
