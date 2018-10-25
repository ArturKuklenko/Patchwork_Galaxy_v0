package com.patchworkgalaxy.template;

import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.lang.Localizable;
import com.patchworkgalaxy.general.lang.Localizer;

public abstract class Template<T> implements Localizable {
    
    protected final GameProps props;
    private final String[] _namespaces;
    
    public Template(GameProps props, String localizationNamespaceRoot) {
	this.props = props;
	if(localizationNamespaceRoot == null)
	    _namespaces = new String[] {};
	else {
	    localizationNamespaceRoot = localizationNamespaceRoot.toLowerCase();
	    String namespaceSub = props.getString("Name");
	    if(namespaceSub != null) {
		namespaceSub = namespaceSub.toLowerCase();
		_namespaces = new String[] {
		    localizationNamespaceRoot + "." + namespaceSub.toLowerCase(),
		    localizationNamespaceRoot
		};
	    }
	    else
		_namespaces = new String[] {};
	}
    }
    
    public GameProps getProps() {
	return props.immutable();
    }
    
    public String getName() {
	return getString("Name");
    }
    
    public String getLocalizedString(String key) {
	return Localizer.getLocalizedString(this, key);
    }
    
    public String getDisplayName() {
	return getLocalizedString("name");
    }
    
    public String getDescription() {
	return getLocalizedString("description");
    }
    
    public String getString(String key) {
	return props.getString(key);
    }
    
    public int getInt(String key) {
	return props.getInt(key);
    }
    
    public float getFloat(String key) {
	return props.getFloat(key);
    }
    
    public boolean getBoolean(String key) {
	return props.getBoolean(key);
    }
    
    public void setId(int id) {
	props.set("TemplateId", id);
    }
    
    public abstract T instantiate(Object... params);

    @Override public String[] getLocalizationNamespaces() {
	return _namespaces;
    }
    
}
