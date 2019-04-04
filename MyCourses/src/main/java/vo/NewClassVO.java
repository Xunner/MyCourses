package vo;

import java.time.LocalDateTime;

/**
 * 新开课VO
 * <br>
 * created on 2019/03/14
 *
 * @author 巽
 **/
public class NewClassVO {
	public Long classId;
	public String name;
	public String teacher;
	public Integer term;
	public Integer classOrder;
	public LocalDateTime startTime;
	public LocalDateTime endTime;

	public NewClassVO(Long classId, String name, Integer term, Integer classOrder, LocalDateTime startTime,
	                  LocalDateTime endTime, String teacher) {
		this.classId = classId;
		this.name = name;
		this.term = term;
		this.classOrder = classOrder;
		this.startTime = startTime;
		this.endTime = endTime;
		this.teacher = teacher;
	}
}
