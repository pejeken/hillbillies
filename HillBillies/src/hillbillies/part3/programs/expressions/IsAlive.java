/**
 * 
 */
package hillbillies.part3.programs.expressions;

import hillbillies.model.Unit;

/**
 * Class representing the IsAlive Boolean Expression
 * @author Kenneth & Bram
 * @version 1.0
 */
public class IsAlive extends UnaryExpression<Unit, Boolean> {

	/**
	 * 
	 */
	public IsAlive(Expression<Unit> unit) throws IllegalArgumentException {
		super(Boolean.class, Unit.class, unit);
	}

	/**
	 * Compute the value to be returned by this expression, given the value
	 * of its child expression.
	 *
	 * @param unit The value of the child expression. This value is guaranteed
	 *              to be not null.
	 * @return | !unit.isTerminated()
	 */
	@Override
	protected Boolean compute(Unit unit) {
		return !unit.isTerminated();
	}

}
