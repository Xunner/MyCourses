package vo;

import java.time.LocalDateTime;

/**
 * 教师开课VO
 * <br>
 * created on 2019/03/14
 *
 * @author 巽
 **/
public class TeacherClassVO {
	public Long classId;
	public Integer term;
	public Integer classOrder;
	public LocalDateTime startTime;
	public LocalDateTime endTime;
	public boolean passReview;

	public TeacherClassVO(Long classId, Integer term, Integer classOrder, LocalDateTime startTime, LocalDateTime endTime, boolean passReview) {
		this.classId = classId;
		this.term = term;
		this.classOrder = classOrder;
		this.startTime = startTime;
		this.endTime = endTime;
		this.passReview = passReview;
	}
}
