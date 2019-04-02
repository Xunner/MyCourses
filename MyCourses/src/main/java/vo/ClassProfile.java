package vo;

import java.util.List;

/**
 * 班次简介
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
public class ClassProfile {
	public Long classId;
	public String name;
	public List<HomeworkProfile> homework;

	public ClassProfile(Long classId, String name, List<HomeworkProfile> homework) {
		this.classId = classId;
		this.name = name;
		this.homework = homework;
	}
}
