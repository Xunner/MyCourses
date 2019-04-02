package vo;

/**
 * 课件VO
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
public class CoursewareVO implements Comparable<CoursewareVO> {
	public Long id;
	public String name;

	public CoursewareVO(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public int compareTo(CoursewareVO o) {
		return (int) (id - o.id);
	}
}
