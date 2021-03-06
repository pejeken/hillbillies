package hillbillies.part3.programs.expressions;

import hillbillies.utils.Vector;

/**
 * Class representing the IsPassable Boolean Expression
 * @author Kenneth & Bram
 * @version 1.0
 */
public class IsPassable extends UnaryExpression<Vector, Boolean> {

	/**
	 * 
	 */
	public IsPassable(Expression<Vector> position) throws IllegalArgumentException {
		super(Boolean.class, Vector.class, position);
	}

	/**
	 * Compute the value to be returned by this expression, given the value
	 * of its child expression.
	 *
	 * @param position The value of the child expression. This value is guaranteed
	 *              to be not null.
	 * @return True if the cube, associated to the given position, in the executing
	 * 			unit's world is passable. False otherwise
	 */
	@Override
	protected Boolean compute(Vector position) {
		return this.getRunner().getExecutingWorld().getCube(position).isPassable();
	}

}
