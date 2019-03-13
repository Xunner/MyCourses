package vo;

/**
 * 班级人数VO
 * <br>
 * created on 2019/03/14
 *
 * @author 巽
 **/
public class ClassNumberVO {
	public Integer current;
	public Integer max;

	public ClassNumberVO(Integer current, Integer max) {
		this.current = current;
		this.max = max;
	}
}
