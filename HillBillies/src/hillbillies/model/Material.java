package hillbillies.model;

import be.kuleuven.cs.som.annotate.*;
import hillbillies.utils.Vector;

import static hillbillies.utils.Utils.randInt;

/**
 * A class representing a raw material
 * @author Kenneth & Bram
 * @version 1.0
 *
 * @invar Each Material can have its world as world.
 * | canHaveAsWorld(this.getWorld())
 * @invar The owner of each Material must be a valid owner for any
 * Material.
 * | isValidOwner(getOwner())
 * @invar Each Material can have its weight as weight.
 * | canHaveAsWeight(this.getWeight())
 *
 */
public abstract class Material implements IWorldObject {

    /**
     * Constant reflecting the minimum weight of each Material.
     */
    public static final int MIN_WEIGHT = 10;
    /**
     * Constant reflecting the maximum weight of each Material.
     */
    public static final int MAX_WEIGHT = 50;

    /**
     * Variable registering the world this Material belongs to.
     */
    private final World world;
    /**
     * Variable registering the Material's current owner.
     */
    private WorldObject owner;
    /**
     * Variable registering the weight of this Material.
     */
    private final int weight;
    /**
     * Variable registering whether this Material is terminated.
     */
    private boolean isTerminated = false;
    /**
     * Variable registering the Material's position while it's falling.
     */
    private Vector fallingPosition;

    /**
     * Initialize this new Material in the given world with the given owner.
     * @param world The world for this new Material.
     * @param owner The owner for this new Material
     * @post The world this new Material belongs to, is equal to the given
     * world.
     * | new.getWorld() == world
     * @effect The owner of this new Material is set to
     * the given owner.
     * | this.setOwner(owner)
     * @post The weight of this new Material is a random
     * integer between MIN_WEIGHT and MAX_WEIGHT.
     * | MIN_WEIGHT <= new.getWeight() <= MAX_WEIGHT
     * @throws IllegalArgumentException When the owner is not a valid owner.
     * 		| ! isValidOwner(owner)
     * @throws IllegalStateException When the owner has reached its maximum number of owned Materials
     * | owner.getMaxNbOwnedMaterials()!=-1 && owner.getNbOwnedMaterials()>=getMaxNbOwnedMaterials()
     * @throws NullPointerException When the world is not an effective world 
     * 			or when the owner is not effective.
     * |world == null || owner == null
     */
    public Material(World world, WorldObject owner) throws IllegalArgumentException, IllegalStateException, NullPointerException{
        if(world == null)
        	throw new NullPointerException("The given world is not effective");
        if(owner == null)
        	throw new NullPointerException("The given owner is not effective");
    	this.world = world;
        this.setOwner(owner);
        world.addMaterial(this);
        this.weight = randInt(MIN_WEIGHT, MAX_WEIGHT);
    }

    @Override
    public void advanceTime(double dt) {
    	if (isTerminated())
    		return;
        if(!this.hasValidPosition() && this.getOwner()!=null){
            this.fallingPosition = this.getPosition();
            this.setOwner(null);
        }
        if(this.getOwner() == null) {
            Vector cPos = this.getPosition();
            Vector cPosCube = cPos.getCubeCenterCoordinates();
            if (cPos.equals(cPosCube) && this.isValidPosition(cPos)) {
                Cube newOwner = this.getWorld().getCube(cPos.getCubeCoordinates());
                this.setOwner(newOwner);
            } else {
                double speed = 3;
                Vector nextPos = cPos.add(new Vector(0, 0, -speed * dt));
                if (this.hasValidPosition() && ((cPosCube.isInBetween(2, cPos, nextPos) || cPos.Z() <= cPosCube.Z())))
                    this.fallingPosition = cPosCube;
                else if (nextPos.getCubeCenterCoordinates().isInBetween(2, cPos, nextPos) && isValidPosition(nextPos))
                    this.fallingPosition = nextPos.getCubeCenterCoordinates();
                else
                    this.fallingPosition = nextPos;
            }
        }
    }

    //region Setters
    /**
     * Set the owner of this Material to the given owner.
     *
     * @param owner
     * The new owner for this Material.
     * @post The owner of this new Material is equal to the given owner.
     * | new.getOwner() == owner
     * @throws IllegalArgumentException
     * The given owner is not a valid owner for this material.
     * | ! isValidOwner(getOwner())
     */
    @Raw
    public void setOwner(WorldObject owner) throws IllegalArgumentException {
    	if(isTerminated())
    		throw new IllegalArgumentException("This Material is terminated");
        if (! isValidOwner(owner))
            throw new IllegalArgumentException("The given owner is not valid for this Material");
        if(owner!=null && owner.hasAsOwnedMaterial(this))
            throw new IllegalArgumentException("The given owner already has this material as an owned Material.");
        WorldObject oldOwner = this.getOwner();
        this.owner = owner;// Set new owner
        if(oldOwner != null)
            oldOwner.removeOwnedMaterial(this);// Remove this material from old owner
        if(owner != null)
            owner.addOwnedMaterial(this);// Add this material to new owner
    }
    //endregion

    //region Getters
    /**
     * Return the position of this Material.
     * @return The position of this Material.
     *          This position equals the position of this Material's current owner.
     */
    @Override
    public Vector getPosition(){
        if(this.getOwner()!=null){
        	if(this.getOwner() instanceof Cube)
        		return this.owner.getPosition().getCubeCenterCoordinates();
            return this.owner.getPosition();
        }
        else
            return fallingPosition;
    }

    /**
     * Return the world this Material belongs to.
     */
    @Basic
    @Raw
    @Immutable
    @Override
    public World getWorld() {
        return this.world;
    }
    /**
     * Return the owner of this Material.
     */
    @Basic
    @Raw
    public WorldObject getOwner() {
        return this.owner;
    }

    /**
     * Return the weight of this Material.
     * @return The weight is a valid weight for any material.
     * |canHaveAsWeight(result)
    */
    @Basic
    @Raw
    @Immutable
    public int getWeight() {
        return this.weight;
    }
    //endregion

    //region Checkers

    /**
     * Check whether the given owner is a valid owner for
     * any Material.
     *
     * @param owner
     * The owner to check.
     * @return
     * | result == (owner==null || owner.getWorld()==this.world) && 
     * 	|	(!this.isTerminated() || !owner.isTerminated())
     */
    public boolean isValidOwner(WorldObject owner) {
        if(this.isTerminated()) return false;
        if(owner == null) return true;
        if(!owner.isTerminated() && owner.getWorld() == this.world) return true;
        return false;
    }

    /**
     * Check whether this Material can have the given weight as its weight.
     *
     * @param weight
     * The weight to check.
     * @return
     * | result == (MIN_WEIGHT <= weight <= MAX_WEIGHT)
     */
    @Raw
    public static boolean canHaveAsWeight(int weight) {
        return MIN_WEIGHT <= weight && weight <= MAX_WEIGHT;
    }

    /**
     * Check whether this Material has a valid position.
     * @return True when the material is owned by a Unit or by nothing or
     *          its position is valid.
     *          | result == (this.getOwner() instanceof Unit) ||
     *          |               isValidPosition(this.getPosition())
     */
    private boolean hasValidPosition(){
        if(this.getOwner() instanceof Unit) return true;
        return isValidPosition(this.getPosition());
    }

    /**
     * Check whether the given position is a valid position for
     * this Material when it's not carried by a Unit.
     * @param position The position to check
     * @return True if the position's z-cube coordinate is 0
     *          (Material lies on the bottom of the world) or
     *          the cube beneath the material isn't passable.
     *          | result == (position.cubeZ()==0) ||
     *          |               !getWorld().getCube(position.getCubeCoordinates().add(new Vector(0,0,-1)).isPassable()
     */
    private boolean isValidPosition(Vector position){
        return position.cubeZ()==0 || !getWorld().getCube(position.getCubeCoordinates().add(new Vector(0,0,-1))).isPassable();
    }
    //endregion

    //region Destructor
    /**
     * Terminate this Material.
     *
     * @post This Material is terminated.
     * | new.isTerminated()
     * @post The owner's number of materials is decreased by 1.
     * | new.getOwner().getNbOwnedMaterials()
     * @post This material has no longer an owner.
     * | new.getOwner() == null
     */
    @Override
    public void terminate() {
    	if(!this.isTerminated()){
    		this.setOwner(null);
    		this.isTerminated = true;
    	}
    }
    /**
     * Return a boolean indicating whether or not this Material
     * is terminated.
     */
    @Basic
    @Raw
    @Override
    public boolean isTerminated() {
        return this.isTerminated;
    }
    //endregion

}
