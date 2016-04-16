package hillbillies.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hillbillies.utils.Vector;

/**
 * Created by Bram on 2-4-2016.
 */
public interface IWorld {

    /**
     * Check whether the given position is a valid position
     * for any IWorldObject in this world.
     * @param position The position to check
     * @return True when the given position is a valid position
     */
    public boolean isValidPosition(Vector position);

    /**
     * Get the minimum position in this world.
     * This is the position of the most bottom left back cube.
     */
    public Vector getMinPosition();

    /**
     * Get the maximum position in this world.
     * This is the position of the most up right front cube.
     */
    public Vector getMaxPosition();

    /**
     * Add the given unit to the set of units of this world.
     *
     * @param unit
     * The unit to be added.
     * @pre The given unit is effective and already references
     * this world.
     * | (unit != null) && (unit.getWorld() == this)
     */
    public void addUnit(Unit unit);
    
    public boolean isCubePassable(Vector vector);
    
    public Vector getSpawnPosition();

    public Set<Cube> getDirectlyAdjacentCubes(Vector position);

    public Set<Cube> getNeighbouringCubes(Vector position);

    public List<Vector> getDirectlyAdjacentCubesPositions(Vector cubeCoordinates);

    public List<Vector> getNeighbouringCubesPositions(Vector cubeCoordinates);
    
    //final Set<Unit> units = new HashSet <>();// Voor wat dient dees?
    
    public Set<Unit> getUnitsInCube(Cube cube);

    public Cube getCube(Vector position);

    public void collapse(Vector coordinate);// TODO: change collapse to Cube
}