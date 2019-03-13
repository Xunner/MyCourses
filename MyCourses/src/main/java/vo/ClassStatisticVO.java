package vo;

/**
 * 课程统计VO
 * <br>
 * created on 2019/03/11
 *
 * @author 巽
 **/
public class ClassStatisticVO {
	public Integer grade;
	public Integer term;
	public String name;
	public String teacher;
	public Double score;
	public boolean isQuit;

	public ClassStatisticVO(Integer grade, Integer term, String name, String teacher, Double score, boolean isQuit) {
		this.grade = grade;
		this.term = term;
		this.name = name;
		this.teacher = teacher;
		this.score = score;
		this.isQuit = isQuit;
	}
}
