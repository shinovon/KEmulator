package emulator.sensor;

import javax.microedition.sensor.Condition;
import javax.microedition.sensor.LimitCondition;
import javax.microedition.sensor.ObjectCondition;
import javax.microedition.sensor.RangeCondition;
import java.util.Vector;
// https://github.com/hbao/phonemefeaturedevices/blob/78b194c67b3b21a9ec6a847972e6bf5dbafdde04/VirtualMachine/phoneme_feature/jsr256/src/share/classes/com/sun/javame/sensor/ChannelUrl.java
public final class ChannelUrl {
	public ChannelUrl() {
		super();
	}

	public static String createUrl(final ChannelImpl ChannelImpl) {
		final StringBuffer sb;
		(sb = new StringBuffer("channel=")).append(ChannelImpl.getName());
		final Condition[] method274;
		if ((method274 = ChannelImpl.getAllConditions()) != null && method274.length > 0) {
			final Vector vector = new Vector<Condition>(method274.length);
			for (int i = 0; i < method274.length; ++i) {
				if (!(method274[i] instanceof ObjectCondition)) {
					boolean b = false;
					for (int size = vector.size(), j = 0; j < size; ++j) {
						if (method274[i].equals(vector.elementAt(j))) {
							b = true;
							break;
						}
					}
					if (!b) {
						vector.addElement(method274[i]);
					}
				}
			}
			final int scale = ChannelImpl.getScale();
			for (int size2 = vector.size(), l = 0; l < size2; ++l) {
				sb.append('&');
				final Condition condition;
				if ((condition = (Condition) vector.elementAt(l)) instanceof LimitCondition) {
					final LimitCondition limitCondition = (LimitCondition) condition;
					sb.append("limit=");
					sb.append(Double.toString(ConditionHelpers.resolve(limitCondition.getLimit(), scale)));
					sb.append('&');
					sb.append("op=");
					sb.append(limitCondition.getOperator());
				} else {
					final RangeCondition rangeCondition = (RangeCondition) condition;
					sb.append("lowerLimit=");
					sb.append(Double.toString(ConditionHelpers.resolve(rangeCondition.getLowerLimit(), scale)));
					sb.append("&lowerOp=");
					sb.append(rangeCondition.getLowerOp());
					sb.append("&upperLimit=");
					sb.append(Double.toString(ConditionHelpers.resolve(rangeCondition.getUpperLimit(), scale)));
					sb.append("&upperOp=");
					sb.append(rangeCondition.getUpperOp());
				}
			}
		}
		return sb.toString();
	}
}
