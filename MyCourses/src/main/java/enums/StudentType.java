package enums;

/**
 * 学生类型
 * <br>
 * created on 2019/03/07
 *
 * @author 巽
 **/
public enum StudentType {
	UNDERGRADUATE("本科生"), GRADUATE("研究生");

	StudentType(String value) {
		this.value = value;
	}

	String value;

	public String getValue() {
		return value;
	}
}
