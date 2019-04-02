package vo;

import java.time.LocalDateTime;

/**
 * 待选/已选未定课程
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
public class ClassToTakeVO {
	public Long classId;
	public String name;
	public String teacher;
	public Integer grade;
	public Integer term;
	public Integer classOrder;
	public LocalDateTime startTime;
	public LocalDateTime endTime;
	public ClassNumberVO number;
	public boolean canTake;

	public ClassToTakeVO(Long classId, String name, String teacher, Integer grade, Integer term, Integer classOrder, LocalDateTime startTime, LocalDateTime endTime, ClassNumberVO number, boolean canTake) {
		this.classId = classId;
		this.name = name;
		this.teacher = teacher;
		this.grade = grade;
		this.term = term;
		this.classOrder = classOrder;
		this.startTime = startTime;
		this.endTime = endTime;
		this.number = number;
		this.canTake = canTake;
	}
}
