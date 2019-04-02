package vo;

import enums.PublishMethod;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业VO
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
public class HomeworkVO implements Comparable<HomeworkVO> {
	public Long id;
	public Long classId;
	public String name;
	public String description;
	public LocalDateTime deadline;
	public Long submissionId;
	public Integer sizeLimit;
	public Integer numberSubmitted;
	public PublishMethod publishMethod;
	public List<String> typeRestriction;

	public HomeworkVO() {
	}

	public HomeworkVO(Long id, Long classId, String name, String description, LocalDateTime deadline, Long submissionId,
	                  Integer sizeLimit, List<String> typeRestriction, Integer numberSubmitted, PublishMethod publishMethod) {
		this.id = id;
		this.classId = classId;
		this.name = name;
		this.description = description;
		this.deadline = deadline;
		this.submissionId = submissionId;
		this.sizeLimit = sizeLimit;
		this.typeRestriction = typeRestriction;
		this.numberSubmitted = numberSubmitted;
		this.publishMethod = publishMethod;
	}



	@Override
	public int compareTo(HomeworkVO o) {
		return deadline.isBefore(o.deadline) ? -1 : 1;
	}
}
