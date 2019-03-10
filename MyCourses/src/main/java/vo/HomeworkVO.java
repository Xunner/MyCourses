package vo;

import java.time.LocalDateTime;

/**
 * 作业VO
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
public class HomeworkVO {
	public Long id;
	public String name;
	public String description;
	public LocalDateTime deadline;
	public boolean submitted;
	public Integer sizeLimit;
	public String typeRestriction;

	public HomeworkVO(Long id, String name, String description, LocalDateTime deadline, boolean submitted, Integer sizeLimit, String typeRestriction) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.deadline = deadline;
		this.submitted = submitted;
		this.sizeLimit = sizeLimit;
		this.typeRestriction = typeRestriction;
	}
}
