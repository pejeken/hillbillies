package hillbillies.activities;

import hillbillies.model.Unit;

/**
 * Abstract base class for each Activity a Unit can perform
 * @author Kenneth & Bram
 * @version 1.0
 */
public abstract class Activity {

    /**
     * Variable registering this activity's progress.
     */
    protected double activityProgress;
    /**
     * Variable registering whether the default behaviour of this unit is activated.
     * Variable registering whether the current activity is being executed or not.
     */
    private boolean isDefault, isActive;
    /**
     * Final variable referencing the Unit this Activity is bound to.
     */
    protected final Unit unit;
    /**
     * Final variable referencing the Unit's ActivityController.
     */
    protected final Unit.ActivityController controller;

    /**
     * Initialize a new Activity which is bound to the given Unit.
     * This means that this Activity can only be executed by the given Unit.
     * @param unit The Unit this new Activity is bound to.
     */
    public Activity(Unit unit){
        this.activityProgress = 0d;
        this.unit = unit;
        this.controller = unit.activityController;
        this.isDefault = false;
        this.isActive = false;
    }

    /**
     * Start this Activity with default mode disabled.
     */
    public final void start(){
        this.start(false);
    }

    /**
     * Start this Activity in the specified default mode.
     * @param isDefault Enables/Disables the default mode
     * @throws IllegalStateException
     *          When this Unit is not able to execute this Activity at this moment and
     *          when default mode is not enabled. Or when this Activity is not set as
     *          the Unit's current Activity.
     *          | (!isDefault && !isAbleTo()) || !controller.isCurrentActivity(this)
     */
    public final void start(boolean isDefault) throws IllegalStateException{
        if(!isDefault && !isAbleTo())
            throw new IllegalStateException("This unit cannot " + this.toString() + " at this moment");
        if(!controller.isCurrentActivity(this))// Sort of extra check to assure this is only called from within ActivityController
            throw new IllegalStateException("This unit's current activity is not set to this activity!");
        //else if(isDefault)
        //    stopDoingDefault();// TODO: fix this
        // this.stateDefault -= 1; (if stateDefault == 3) ??
        this.activityProgress = 0d;
        this.startActivity();
        this.isActive = true;
    }

    /**
     * Activity specific code which is called when the Activity is started.
     */
    protected abstract void startActivity();

    /**
     * Stop this Activity
     * @param nextActivity The Activity which will be started after this one has stopped. (OR this activity if requestFinish is called)
     * @throws IllegalStateException
     *          When this Activity should not stop for nextActivity and nextActivity is not this Activity (=> requestFinish is not called)
     *          | nextActivity!=this && !shouldStopFor(nextActivity)
     */
    public final void stop(Activity nextActivity) throws IllegalStateException{
        if(nextActivity!=this && !shouldStopFor(nextActivity))// Activities can always be stopped by themselves (through requestFinish)
            throw new IllegalStateException("This Activity cannot be stopped by the next Activity.");
        this.interruptActivity();// First interrupt and then stop activity
        this.stopActivity();
        this.activityProgress = 0d;
        this.isActive = false;
    }

    /**
     * Activity specific code which is called when the Activity is stopped.
     */
    protected abstract void stopActivity();

    /**
     * Interrupt this Activity
     * @param nextActivity The Activity which will be started after this one is interrupted.
     * @throws IllegalStateException
     *          When this Activity should not interrupt for nextActivity
     *          | !shouldInterruptFor(nextActivity)
     */
    public final void interrupt(Activity nextActivity) throws IllegalStateException{
        if(!shouldInterruptFor(nextActivity))
            throw new IllegalStateException("This Activity cannot be interrupted by the next Activity");
        this.interruptActivity();
        this.isActive = false;
    }

    /**
     * Activity specific code which is called when the Activity is interrupted.
     * This code is also called before stopActivity when the Activity is stopped.
     */
    protected abstract void interruptActivity();

    /**
     * Advance the game-time of this Activity
     * @param dt The amount of game-time to progress with
     */
    public final void advanceTime(double dt){
        this.advanceActivity(dt);
        this.activityProgress += dt;
    }

    /**
     * Activity specific code which is called when advanceTime of this Activity is called.
     */
    protected abstract void advanceActivity(double dt);

    /**
     * Activity specific code to check whether this Activity can be started.
     * @return True if this Activity can be started as the nextActivity of the currently active Activity.
     */
    public abstract boolean isAbleTo();

    /**
     * Activity specific code to check whether this Activity can be stopped by nextActivity.
     * @param nextActivity The Activity which will be started when this Activity stops.
     * @return True if this Activity should stop for nextActivity.
     */
    protected abstract boolean shouldStopFor(Activity nextActivity);

    /**
     * Activity specific code to check whether this Activity can be interrupted by nextActivity.
     * @param nextActivity The Activity which will be started when this Activity is interrupted.
     * @return True if this Activity should be interrupted for nextActivity.
     */
    protected abstract boolean shouldInterruptFor(Activity nextActivity);

    /**
     * Set the default mode.
     * @param enable The new state of the default mode.
     * @post The new state of the default mode equals enable
     *          | new.isDefault()==enable
     */
    public void setDefault(boolean enable){
        this.isDefault = enable;
    }

    /**
     * Check whether default mode is enabled.
     * @return True if default mode is enabled.
     */
    public boolean isDefault(){
        return this.isDefault;
    }

    /**
     * Check whether this Activity is active.
     * @return True when this Activity is active.
     */
    public boolean isActive(){
        return this.isActive;
    }

    /**
     * Get this Activity's progress.
     * @return The Activity's progress.
     */
    public double getActivityProgress(){
        return this.activityProgress;
    }

    /**
     * Get the Id of the Unit this Activity is bound to.
     * @return The Id of the Unit this Activity is bound to.
     */
    public long getUnitId(){
        return this.unit.getId();
    }

    /**
     * Request this Activity's finish. This will stop the current Activity, if possible, and
     * resume the previous Activity in stack.
     * @see hillbillies.model.Unit.ActivityController#requestActivityFinish(Activity)
     */
    protected void requestFinish(){
        this.controller.requestActivityFinish(this);
    }
}