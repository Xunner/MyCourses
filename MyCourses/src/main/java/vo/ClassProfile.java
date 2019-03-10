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
	public String name;
	public List<HomeworkProfile> homework;

	public ClassProfile(String name, List<HomeworkProfile> homework) {
		this.name = name;
		this.homework = homework;
	}
}
