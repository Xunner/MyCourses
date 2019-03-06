package po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 主管
 * <br>
 * created on 2019/03/06
 *
 * @author 巽
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("A")
public class AdminPO extends UserPO {
}
