package vo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业VO
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
public class HomeworkVO {
	public Long id;
	public Long classId;
	public String name;
	public String description;
	public LocalDateTime deadline;
	public boolean submitted;
	public Integer sizeLimit;
	public List<String> typeRestriction;

	public HomeworkVO() {
	}

	public HomeworkVO(Long id, Long classId, String name, String description, LocalDateTime deadline, boolean submitted,
	                  Integer sizeLimit, List<String> typeRestriction) {
		this.id = id;
		this.classId = classId;
		this.name = name;
		this.description = description;
		this.deadline = deadline;
		this.submitted = submitted;
		this.sizeLimit = sizeLimit;
		this.typeRestriction = typeRestriction;
	}
}
