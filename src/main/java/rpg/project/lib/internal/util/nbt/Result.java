package rpg.project.lib.internal.util.nbt;

public record Result(	
	Operator operator,
	String comparator, 
	String comparison) {

	public boolean compares() {
		switch (operator) {
		case EQUALS: {
			return comparator.equals(comparison);
		}
		case GREATER_THAN: {
			return Double.valueOf(comparator) < Double.valueOf(comparison);
		}
		case LESS_THAN: {
			return Double.valueOf(comparator) > Double.valueOf(comparison);
		}
		case GREATER_THAN_OR_EQUAL: {
			return Double.valueOf(comparator) <= Double.valueOf(comparison);
		}
		case LESS_THAN_OR_EQUAL: {
			return Double.valueOf(comparator) >= Double.valueOf(comparison);
		}
		case EXISTS: {
			return !comparison.isEmpty();
		}
		case CONTAINS: {
			return comparison.contains(comparator);
		}
		default: return false;}
	}
}