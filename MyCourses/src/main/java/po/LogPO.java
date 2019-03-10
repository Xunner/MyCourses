package po;

import enums.OperationType;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 日志
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
@Data
@Entity
@Table(name = "log")
public class LogPO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime time;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "object_id")
	private Long objectId;

	/** xxPO.class.getName() */
	@Column(name = "object_class")
	private String objectClass;

	@Enumerated
	@Column(name = "operation_type")
	private OperationType operationType;

	public LogPO() {
	}

	public LogPO(LocalDateTime time, Long userId, Long objectId, String objectClass, OperationType operationType) {
		this.time = time;
		this.userId = userId;
		this.objectId = objectId;
		this.objectClass = objectClass;
		this.operationType = operationType;
	}
}
