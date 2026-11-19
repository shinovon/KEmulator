package emulator.sensor;

import javax.microedition.sensor.Condition;
import javax.microedition.sensor.LimitCondition;
import javax.microedition.sensor.ObjectCondition;
import javax.microedition.sensor.RangeCondition;
import java.util.Vector;

public final class ChannelUrl {
	public ChannelUrl() {
		super();
	}

	public static String createUrl(final ChannelImpl ChannelImpl) {
		final StringBuffer sb;
		(sb = new StringBuffer("channel=")).append(ChannelImpl.getName());
		final Condition[] allConditions;
		if ((allConditions = ChannelImpl.getAllConditions()) != null && allConditions.length > 0) {
			final Vector vector = new Vector<Condition>(allConditions.length);
			for (int i = 0; i < allConditions.length; ++i) {
				if (!(allConditions[i] instanceof ObjectCondition)) {
					boolean found = false;
					for (int size = vector.size(), j = 0; j < size; ++j) {
						if (allConditions[i].equals(vector.elementAt(j))) {
							found = true;
							break;
						}
					}
					if (!found) {
						vector.addElement(allConditions[i]);
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
