package com.patchworkgalaxy.display.models;

import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import static com.patchworkgalaxy.display.models.MemoryOfShipHashCodes.HashCodesAllShips;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.template.types.SpewerTemplate;

class AnimationSpewer extends Animation {

    private SpewerTemplate _emitterPrototype;
    private ParticleEmitter _emitter;
    private ParticleEmitterReverseFireball _emitterRFireball;
    
    private boolean _active;
    private float _rate;
    private long currentDuration;
    private boolean StopExplosion = false;
    
    AnimationSpewer(String trigger, Mode mode, SpewerTemplate spewer) {
	super(trigger, Mode.BACKGROUND, null);
	_emitterPrototype = spewer;
    }
    
    @Override
    protected void initialize() {
	_emitter = _emitterPrototype.instantiate(scale);
        if(_emitter.getName().equals("Reverse Fireball")){

          // System.out.println((char)27+"[30;43mAnimationSpewer="+_emitter.getName());
            try{
                    for(int i=0;i<spatial.getParent().getQuantity();i++){
                        Class<? extends Spatial> str = spatial.getParent().getChild(i).getClass();
                        String shipname = spatial.getParent().getChild(i).getName();
                        //System.out.println((char)27+"[30;43m"+str.getSimpleName()+" hashcode="+spatial.getParent().getChild(i).hashCode()+" name="+spatial.getParent().getChild(i).getName());
                        if(shipname!=null)
                        try{switch (shipname) {
                            case "headquarters-geom-0":
                            case "righteousvengeance-geom-0":
                            case "dreadnought-geom-0":
                            case "destroyer-geom-0":
                            case "cruiser-geom-0":
                            case "frigate-geom-0":
                                Vector3f _target = new Vector3f(model.getTarget());
                                Vector3f _ship = new Vector3f(spatial.getParent().getChild(i).getLocalTranslation());
                                //System.out.println((char)27+"[30;43mtarget="+_target+" ship="+_ship);
                                int shipHashCode = spatial.getParent().getChild(i).hashCode();
                                MemoryOfShipHashCodes.addShip(shipHashCode);
                                if(_target.equals(_ship)){
                                
                                _emitterRFireball = _emitterPrototype.instantiateRFireball(shipHashCode, scale);
                                
                                }
                                break;
                            default:
                                break;
                        }}catch(NullPointerException e){e.printStackTrace();}
                        
                        
                        
                        
                        }      
              }catch(NullPointerException e){e.printStackTrace();}
                                if(_emitterRFireball!=null){
                                _emitter = null;
                                spatial.getParent().attachChild(_emitterRFireball);
                                _emitterRFireball.setLocalTranslation(attachment);
                                _rate = _emitterRFireball.getParticlesPerSec();
                                currentDuration=(System.currentTimeMillis()+700);
                                }
        } else{
                                spatial.getParent().attachChild(_emitter);
                                _emitter.setLocalTranslation(attachment);
                                _rate = _emitter.getParticlesPerSec();
                                currentDuration=(System.currentTimeMillis()+700);
        }
    }
    public ParticleEmitter getEmitter(){
        return _emitter;
    }
    @Override
    protected void teardown() {
	float duration = _emitter.getLowLife();
	_rate = .0000001f;
        _emitter.setParticlesPerSec(.0000001f);
	_emitter.addControl(new Teardown2(duration,this,_emitter.getName()));
    }

    @Override
    protected boolean isDoneImpl() {
        if(StopExplosion==true){
            return true;
        }else {
            return false;
        }

	
    }

    @Override
    protected void animate(float tpf) {
        if(System.currentTimeMillis()>currentDuration){
            if(_emitter!=null)
            takeExplosion(_emitter.getName());
        }
	updatePosition();
	checkActivity();
    }
    
    private void updatePosition() {
	Quaternion rotation = spatial.getLocalRotation();
        if(_emitter!=null){
	_emitter.setLocalRotation(rotation);
	_emitter.setLocalTranslation(spatial.getLocalTranslation());
	_emitter.move(rotation.mult(attachment));
        }
        if(_emitterRFireball!=null){
        _emitterRFireball.setLocalRotation(rotation);
	_emitterRFireball.setLocalTranslation(spatial.getLocalTranslation());
	_emitterRFireball.move(rotation.mult(attachment));    
        }
    }
    
    private void checkActivity() {
	boolean blocking = model.blocking();
	if(blocking != _active) {
	    _active = blocking;
            if(_emitter!=null){
                if(blocking)
		_emitter.setParticlesPerSec(.0000001f);
                else
		_emitter.setParticlesPerSec(_rate);
            }
            if(_emitterRFireball!=null){
                if(blocking)
		_emitterRFireball.setParticlesPerSec(.0000001f);
                else
		_emitterRFireball.setParticlesPerSec(_rate);    
            }
	}
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
    private static class Teardown extends AbstractControl {

	private float _duration;
	
	Teardown(float duration) {
	    _duration = duration;
	}
	
	@Override
	protected void controlUpdate(float tpf) {
	    _duration -= tpf;
	    if(_duration < 0){
               spatial.removeFromParent();
            }
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {}
	
    }

    public void setStopExplosion(boolean StopExplosion) {
        this.StopExplosion = StopExplosion;
    }
    public void takeExplosion(String spewerName){
        switch (spewerName) {
         case "Minor Explosion":
         case "Painray":
         case "Fireball":
         case "Directed Fireball":
         case "Reverse Fireball":
         case "Flash":
         case "Burst Ray":
         case "Shockwaves":
         case "Sparks":
         case "Kinetic1A": 
             setStopExplosion(true);
             break;
         default:
             break;
     }
    }
        private static class Teardown2 extends AbstractControl {

	private float _duration;
	private AnimationSpewer spewer;
        private String ExplosionName;
	Teardown2(float duration, AnimationSpewer spewer, String ExplosionName) {
	    _duration = duration;
            this.spewer = spewer;
            this.ExplosionName = ExplosionName;
	}
	
	@Override
	protected void controlUpdate(float tpf) {
	    _duration -= tpf;
	    if(_duration < 0){
              
              try{
                    for(int i=0;i<spatial.getParent().getQuantity();i++){
                        spatial.getParent().detachChildNamed("Minor Explosion");
                    }
              }catch(NullPointerException e){}

             /* try{
                    for(int w=0;w<spatial.getParent().getQuantity();w++){
                    spatial.getParent().detachChildNamed("Reverse Fireball");
                    }
              }catch(NullPointerException e){}  */  
              MemoryOfShipHashCodes.clearNewest();
          try{
           for(int i=0;i<spatial.getParent().getQuantity();i++){
            String shipname = spatial.getParent().getChild(i).getName();
            if(shipname!=null)
            try{switch (shipname) {
               case "headquarters-geom-0":
               case "righteousvengeance-geom-0":
               case "dreadnought-geom-0":
               case "destroyer-geom-0":
               case "cruiser-geom-0":
               case "frigate-geom-0":
               int shipHashCode = spatial.getParent().getChild(i).hashCode();
               //System.out.print("ship="+shipname+" hashcode="+shipHashCode+" | ");
               MemoryOfShipHashCodes.addShipToNewest(shipHashCode);
                  break;
               default:
                  break;
               }}catch(NullPointerException e){}
           }               
       }catch(NullPointerException e){}
         //try{System.out.println("HashCodesAllShipsNewest.size="+MemoryOfShipHashCodes.HashCodesAllShipsNewest.size());}catch(NullPointerException e){}
         if(MemoryOfShipHashCodes.HashCodesAllShipsNewest.size()>=3 | MemoryOfShipHashCodes.HashCodesAllShips.size()>=4){
           try{ 
               for(int i : MemoryOfShipHashCodes.HashCodesAllShips){
			if(MemoryOfShipHashCodes.HashCodesAllShipsNewest.indexOf(i)==-1){
                            //System.out.print("Broken ship hashcode="+i);
			
                            try{  
                            for(int w=0;w<spatial.getParent().getQuantity();w++){
                            String shipname = spatial.getParent().getChild(w).getName();
                            if(shipname!=null)
                            if(shipname.equals("Reverse Fireball")){
                            ParticleEmitterReverseFireball reverseFireball =
                            (ParticleEmitterReverseFireball)spatial.getParent().getChild(w);
                            int shipHashCode = reverseFireball.getShipHashCode();
                            //System.out.print(" shipHashCode="+shipHashCode);
                            if(shipHashCode==i){
                            //System.out.print(" Fireball for deleting="+i);
                            spatial.getParent().detachChildAt(w);
                            //try{MemoryOfShipHashCodes.HashCodesAllShips.remove(new Integer(i));}catch(IndexOutOfBoundsException e){e.printStackTrace();}
                            }}
                   
        }}catch(NullPointerException e){e.printStackTrace();} 
                            
                            
                            
                            
                            
                            
                            
                        
			}
		}}catch(IndexOutOfBoundsException e){e.printStackTrace();}
        }
        try{spatial.removeFromParent();}catch(NullPointerException e){}
        
            }
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {}
	
    }
}
