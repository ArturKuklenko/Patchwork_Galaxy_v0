package com.patchworkgalaxy;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.InputManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.patchworkgalaxy.client.PWGClient;
import com.patchworkgalaxy.display.appstate.GameAppState;
import com.patchworkgalaxy.display.models.BoardCameraControl;
import com.patchworkgalaxy.display.models.SelectionControl;
import com.patchworkgalaxy.display.oldui.uidefs.MainMenu;
import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.general.lang.Localizer;
import com.patchworkgalaxy.general.util.Utils;
import com.patchworkgalaxy.network.server.PatchworkGalaxyServer;
import com.patchworkgalaxy.template.TemplateRegistry;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class PatchworkGalaxy extends SimpleApplication {
    
    public final float APP_RESOLUTION_SCALE;
    
    private static final PatchworkGalaxy _instance = new PatchworkGalaxy();
    
    BoardCameraControl boardCameraControl;
    Node cameraControlNode;
    boolean stateInitialized = false;
    
    private PatchworkGalaxy() {
        setPauseOnLostFocus(false);
        setShowSettings(false);
        AppSettings s = new AppSettings(true);
	s.setSamples(4);
	s.setTitle("Patchwork Galaxy");
	APP_RESOLUTION_SCALE = initDimensions(s);
        setSettings(s);
    }
    
    @Override protected BitmapFont loadGuiFont() {
        return assetManager.loadFont("Interface/Fonts/ZektonFree.fnt");
    }
    
    private float initDimensions(AppSettings s) {
	DisplayMode display = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
	int width = display.getWidth();
	int height = display.getHeight();
	float wscale, hscale;
	if(width > Definitions.DEFAULT_RESOLUTION_X * Definitions.LARGE_RESOLUTION_SCALE)
	    wscale = Definitions.LARGE_RESOLUTION_SCALE;
	else if(width < Definitions.DEFAULT_RESOLUTION_X)
	    wscale = Definitions.SMALL_RESOLUTION_SCALE;
	else
	    wscale = 1f;
        if(height > Definitions.DEFAULT_RESOLUTION_X)
	    hscale = Definitions.LARGE_RESOLUTION_SCALE;
	else if(height < Definitions.DEFAULT_RESOLUTION_Y)
	    hscale = Definitions.SMALL_RESOLUTION_SCALE;
	else
	    hscale = 1f;
	float scale = Math.min(wscale, hscale);
        s.setResolution((int)(Definitions.DEFAULT_RESOLUTION_X * scale), (int)(Definitions.DEFAULT_RESOLUTION_Y * scale));
	return scale;
    }
    
    public static Vector2f getDimensions() {
	float scale = _instance.APP_RESOLUTION_SCALE;
	return new Vector2f(Definitions.DEFAULT_RESOLUTION_X * scale, Definitions.DEFAULT_RESOLUTION_Y * scale);
    }
    
    @Override public void simpleInitApp() {
	boardCameraControl = new BoardCameraControl(cam);
	cameraControlNode = new Node();
	cameraControlNode.addControl(boardCameraControl);
	rootNode.attachChild(cameraControlNode);
        flyCam.setEnabled(false);
        setDisplayFps(false);
        setDisplayStatView(false);
        inputManager.deleteMapping(INPUT_MAPPING_EXIT);
        TemplateRegistry.init();
	MainMenu.mainMenu();
	sun();
    }

    @Override public void simpleUpdate(float tpf) {
        if(GameAppState.getInstance() != null) {
            if(!stateInitialized) {
		rootNode.attachChild(cameraControlNode);
                stateManager.attach(GameAppState.getInstance());
                stateInitialized = true;
            }
        }
	Collisions.update(tpf);
	Efforts.check();
	PWGClient.tick();
    }

    @Override public void simpleRender(RenderManager rm) {}
    
    @Override public void destroy() { System.exit(0); }
    
    private void sun() {
	DirectionalLight sun = new DirectionalLight();
	sun.setColor(ColorRGBA.White);
	sun.setDirection(new Vector3f(0, 1, -1));
	_instance.getRootNode().addLight(sun);
	AmbientLight ambient = new AmbientLight();
	ambient.setColor(ColorRGBA.White.mult(.1f));
    }
    
    public static BitmapFont getGuiFont() {
	return _instance.guiFont;
    }
    
    public static GameState getGameState() {
	return GameAppState.getInstance().getGameState();
    }
    
    public static Camera camera() {
	return _instance.cam;
    }
    
    public static BoardCameraControl getBoardCameraControl() {
	return _instance.boardCameraControl;
    }
    
    @Override public void handleError(String errorMessage, Throwable t) {
	writeException(errorMessage, t);
	super.handleError(errorMessage, t);
    }
    
    public static void writeException(Throwable t) {
	writeException(t.getLocalizedMessage(), t);
    }
    
    public static void writeException(String errorMessage, Throwable t) {
	String filename = (t.getClass().getSimpleName() + "_" + new Date()).replaceAll("\\W", "_") + ".txt";
	try {
	    try (PrintWriter writer = new PrintWriter(filename, "UTF-8")) {
		writer.println(errorMessage);
		t.printStackTrace(writer);
	    }
	}
	catch(Throwable t2) {
	    System.exit(1);
	}	
    }
    
    public static AppStateManager stateManager() {
	return _instance.stateManager;
    }
    
    public static Node guiNode() {
	return _instance.getGuiNode();
    }
    
    public static Node rootNode() {
	return _instance.getRootNode();
    }
    
    public static AssetManager assetManager() {
	return _instance.getAssetManager();
    }
    
    public static InputManager inputManager() {
	return _instance.getInputManager();
    }
    
    public static Vector2f getScreenCoordinates(Spatial s) {
	Vector3f vec3 = _instance.cam.getScreenCoordinates(s.getWorldTranslation());
	float x = 2 * (vec3.x / (Definitions.DEFAULT_RESOLUTION_X * _instance.APP_RESOLUTION_SCALE) - .5f);
	float y = 2 * (vec3.y / (Definitions.DEFAULT_RESOLUTION_Y * _instance.APP_RESOLUTION_SCALE) - .5f);
	return new Vector2f(x, y);
    }
    
    public static <V> Future<V> schedule(Callable<V> callable) {
	return _instance.enqueue(callable);
    }
    
    public static void schedule(final Runnable runnable) {
	_instance.enqueue(new Callable<Void>() {
	    @Override public Void call() {
		runnable.run();
		return null;
	    }
	});
    }
    
    public static boolean isMouseOver(Vector2f mouse2d, Spatial spatial) {
	CollisionResults results = new CollisionResults();
	Vector3f click3d = _instance.cam.getWorldCoordinates(new Vector2f(mouse2d.x, mouse2d.y), 0.0F).clone();
	Vector3f dir = _instance.cam.getWorldCoordinates(new Vector2f(mouse2d.x, mouse2d.y), 1.0F).subtractLocal(click3d).normalizeLocal();
	Ray ray = new Ray(click3d, dir);
	_instance.getRootNode().collideWith(ray, results);
	for(int i = 0; i < results.size(); ++i) {
	    if(spatial.equals(results.getCollision(i).getGeometry()))
		return true;
	}
	return false;
    }

    public static Tile tileFromScreenCoordinates(Vector2f click2d) {
	CollisionResults results = new CollisionResults();
	Vector3f click3d = _instance.cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0.0F).clone();
	Vector3f dir = _instance.cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1.0F).subtractLocal(click3d).normalizeLocal();
	Ray ray = new Ray(click3d, dir);
	_instance.getRootNode().collideWith(ray, results);
	for (int i = 0; i < results.size(); ++i) {
	    Geometry s = results.getCollision(i).getGeometry();
	    SelectionControl sc = s.getControl(SelectionControl.class);
	    if (sc != null) {
		GameComponent gc = sc.getComponent();
		if (gc instanceof Tile)
		    return (Tile)gc;
		else if(gc instanceof Ship) 
		    return ((Ship)gc).getPosition();
	    }
	}
	return null;
    }
    
    public static float getAppResolutionScale() {
	return _instance.APP_RESOLUTION_SCALE;
    }
    
    public static void main(String[] args) {
	Utils.initSerialization();
	Localizer.setLocalization("en_us.txt");
        if(args.length == 0 || args[0].equals("client")) {
	    _instance.start();
	}
        else if(args[0].equals("server"))
	    PatchworkGalaxyServer.getInstance().start(JmeContext.Type.Headless);
    }
    
}