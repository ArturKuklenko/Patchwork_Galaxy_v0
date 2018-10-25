package com.patchworkgalaxy.display.models;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.patchworkgalaxy.game.component.Ship;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class AnimationStaged extends Animation {

    private final LinkedList<Stage> _stages;
    private float _currentTime, _nextStageTime;
    private final float _endTime;
    private boolean fire = false;
    AnimationStaged(String trigger, Mode mode, List<Stage> stages) {
	super(trigger, mode, null);
	_stages = new LinkedList<>(stages);
	float end = 0;
	for(Stage stage : stages) {
	    end = Math.max(end, stage.end);
	}
	_endTime = end;
    }
    
    AnimationStaged(String trigger, Mode mode, List<Stage> stages, float delay) {
	this(trigger, mode, stages);
	_currentTime = -delay;
    }
    
    @Override
    protected void initialize() {
	_nextStageTime = _stages.getFirst().start;
    }

    @Override
    protected void teardown() {
       /* MemoryOfShipHashCodes.clearNewest();
          try{
           for(int i=0;i<spatial.getParent().getQuantity();i++){
            String shipname = spatial.getParent().getChild(i).getName();
            try{switch (shipname) {
               case "headquarters-geom-0":
               case "righteousvengeance-geom-0":
               case "dreadnought-geom-0":
               case "destroyer-geom-0":
               case "cruiser-geom-0":
               case "frigate-geom-0":
               int shipHashCode = spatial.getParent().getChild(i).hashCode();
               MemoryOfShipHashCodes.addShipToNewest(shipHashCode);
               System.out.println("new ships added to Newest");
                  break;
               default:
                  break;
               }}catch(NullPointerException e){}
           }               
       }catch(NullPointerException e){e.printStackTrace();}
                for(int i : MemoryOfShipHashCodes.HashCodesAllShips){
			if(MemoryOfShipHashCodes.HashCodesAllShipsNewest.indexOf(i)==-1){
			MemoryOfShipHashCodes.HashCodesShipForRemovingReverseFireballs.add(i);
                        MemoryOfShipHashCodes.HashCodesAllShips.remove(i);
			}
		}
         try{
          for(int w=0;w<spatial.getParent().getQuantity();w++){
           String shipname = spatial.getParent().getChild(w).getName();
           if(shipname.equals("Reverse Fireball")){
              ParticleEmitterReverseFireball reverseFireball =
              (ParticleEmitterReverseFireball)spatial.getParent().getChild(w);
              int shipHashCode = reverseFireball.getShipHashCode();
              for(int i : MemoryOfShipHashCodes.HashCodesShipForRemovingReverseFireballs){
                  if(shipHashCode==i){
                     spatial.getParent().detachChildAt(w);
                  }
              }
           }
                   
        }}catch(NullPointerException e){}  
        
        MemoryOfShipHashCodes.HashCodesShipForRemovingReverseFireballs.clear();*/
        
        
        
        
      
    }

    @Override
    protected boolean isDoneImpl() {
	return _currentTime >= _endTime;
    }

    @Override
    protected void animate(float tpf) {
        
	_currentTime += tpf;
	if(_currentTime >= _nextStageTime && !_stages.isEmpty()){
	    checkStages();
        }
    }
    
    private void checkStages() {
	Iterator<Stage> i = _stages.iterator();
	float nextTime = Float.NaN;
	Stage stage;
	while(i.hasNext()) {
	    stage = i.next();
	    if(stage.start < _currentTime) {
                    stage.animation.getAnimation(model).start();
		if(stage.special)
		    special();
		i.remove();
	    }
	    else {
		nextTime = cmpTime(nextTime, stage.start);
	    }
	}
	_nextStageTime = nextTime;   
    }
    
    private float cmpTime(float a, float b) {
	if(Float.isNaN(a))
	    return b;
	return Math.min(a, b);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
}
