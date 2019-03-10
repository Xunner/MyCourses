package vo;

import java.util.List;

/**
 * 课程简介
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
public class CourseProfile {
	public String name;
	public List<HomeworkProfile> homework;

	public CourseProfile(String name, List<HomeworkProfile> homework) {
		this.name = name;
		this.homework = homework;
	}
}
