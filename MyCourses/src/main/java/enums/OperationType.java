package enums;

/**
 * 操作类型
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
public enum OperationType {
	LOGIN("登录"), DELETE("删除");

	OperationType(String value) {
		this.value = value;
	}

	String value;

	public String getValue() {
		return value;
	}
}
