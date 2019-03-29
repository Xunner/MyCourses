package vo;

import java.time.LocalDate;

/**
 * 群发消息班级VO
 * <br>
 * created on 2019/03/29
 *
 * @author 巽
 **/
public class ClassToMessageVO {
	public Long classId;
	public String courseName;
	public LocalDate startTime;
	public Integer classOrder;

	public ClassToMessageVO(Long classId, String courseName, LocalDate startTime, Integer classOrder) {
		this.classId = classId;
		this.courseName = courseName;
		this.startTime = startTime;
		this.classOrder = classOrder;
	}
}
