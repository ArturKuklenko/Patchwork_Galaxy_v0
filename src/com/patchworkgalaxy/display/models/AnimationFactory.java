package com.patchworkgalaxy.display.models;

import com.jme3.math.ColorRGBA;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.template.types.SpewerTemplate;
import java.util.ArrayList;
import java.util.List;

public class AnimationFactory extends AnimationDef {
    
    private final GameProps _props;
    private final String _type, _trigger;
    private final Animation.Mode _mode;
    
    public AnimationFactory(GameProps props) {
	super(null);
	_props = props;
	_type = props.getString("Type");
	_trigger = props.getString("Trigger");
	String mode = props.getString("Mode");
	if(mode == null)
	    _mode = Animation.Mode.STANDARD;
	else
	    _mode = Animation.Mode.valueOf(mode);
	
    }
    
    Animation create(Model model) {
	Animation animation = create0();
	animation.pointname = _props.getString("Point");
	animation.scale = model.getScale();
	if(_props.hasKey("Scale"))
	    animation.scale *= _props.getFloat("Scale");
	return animation.withModel(model);
    }
    
    private Animation create0() {
	switch(_type) {
	case "Move":
	    return new AnimationMove(_trigger, _mode);
	case "Turn":
	    return new AnimationTurn(_trigger, _mode, 1);
	case "Spew":
	case "Spewer":
	    SpewerTemplate spewer = _props.get(SpewerTemplate.class, "Spewer");
	    return new AnimationSpewer(_trigger, _mode, spewer);
	case "Support":
	    @SuppressWarnings("unchecked")
	    List<AnimationDef> defs = new ArrayList(_props.get(List.class, "Animation"));
	    AnimationDef main = defs.remove(0);
	    AnimationDef[] secondary = new AnimationDef[defs.size()];
	    return new AnimationSupport(_trigger, _mode, main, defs.toArray(secondary));
	case "Staged":
	    return createStagedAnimation();
	case "Timer":
	    return new AnimationTimer(_trigger, _mode, _props.getFloat("Duration"));
	case "Visibility":
	    return new AnimationVisibility(_trigger, _props.getBoolean("Visible!"));
	case "PatchIn":
	    return new AnimationPatchIn(_trigger, _mode);
	case "Tint":
	    ColorRGBA color = _props.get(ColorRGBA.class, "Color");
	    boolean immediate = _props.getBoolean("Immediate!");
	    return new AnimationTint(_trigger, _mode, color, immediate);
	default:
	    throw new IllegalArgumentException("Unknown animation type " + _type);
	}
    }
    
    private Animation createStagedAnimation() {
	@SuppressWarnings("unchecked")
	List<Stage> stages = _props.get(List.class, "Stages");
	float delay = _props.getFloat("Delay");
	float rdelay = _props.getFloat("DelayVariance");
	if(rdelay > 0) {
	    delay += Math.random() * rdelay;
	}
	return new AnimationStaged(_trigger, _mode, stages, delay);
    }
    
    @Override
    Animation getAnimation(Model model) {
	return create(model);
    }
    
    @Override
    String getTrigger() {
	return _trigger;
    }
    
}
