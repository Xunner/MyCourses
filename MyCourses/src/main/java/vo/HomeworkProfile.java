package vo;

import java.time.LocalDateTime;

/**
 * 作业简介
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
public class HomeworkProfile {
	public String name;
	public LocalDateTime deadline;

	public HomeworkProfile(String name, LocalDateTime deadline) {
		this.name = name;
		this.deadline = deadline;
	}
}
