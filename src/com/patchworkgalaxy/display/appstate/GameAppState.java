package com.patchworkgalaxy.display.appstate;

import com.jme3.app.state.AbstractAppState;
import com.jme3.math.Vector2f;
import com.jme3.scene.Spatial;
import com.patchworkgalaxy.PatchworkGalaxy;
import com.patchworkgalaxy.client.ClientManager;
import com.patchworkgalaxy.display.models.Model;
import com.patchworkgalaxy.display.models.ModelFactory;
import com.patchworkgalaxy.display.models.SelectionControl;
import com.patchworkgalaxy.display.oldui.ControlState;
import com.patchworkgalaxy.display.oldui.UX2D;
import com.patchworkgalaxy.display.oldui.uidefs.GameViewer;
import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.state.ClientGameState;
import com.patchworkgalaxy.game.state.GameHistory;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.general.subscriptions.Topic;
import com.patchworkgalaxy.template.TemplateRegistry;
import com.patchworkgalaxy.template.types.ModelTemplate;

public class GameAppState extends AbstractAppState {
    
    private Wrapper _wrapper;
    private boolean _active = true;
    private float _gameOverTimer;
    
    private static final float GAME_OVER_PADDING = 6f;
    private static final float GAME_OVER_DURATION = 11.5f + GAME_OVER_PADDING;
    
    private static GameAppState _instance;
    
    private Player _currentPlayer;
    
    private GameAppState() {}
    
    public static void startGame(GameHistory history) {
	ClientGameState.setupNewInstance(history);
	getInstance().setWrapper(new Wrapper(history));
	GameViewer.gameViewer(getInstance().getGameState());
	PatchworkGalaxy.getBoardCameraControl().lookAtInstantly(getInstance().getGameState().getBoard());
	Topic.GAME_STARTED.update();
	Topic.GAME_CHRONO.update();
    }
    
    private void setWrapper(Wrapper wrapper) {
	_wrapper = wrapper;
    }
    
    public static GameAppState getInstance() {
	if(_instance == null)
	    _instance = new GameAppState();
	return _instance;
    }
    
    public GameState getGameState() {
	if(_wrapper == null)
	    return null;
	return _wrapper.getWrappedGame();
    }
    
    @Override
    public void update(float tpf) {
	if(_wrapper == null)
	    return;
	GameState game = _wrapper.getWrappedGame();
	if(_active) {
	    Player winner = game.getWinner();
	    if(winner != null) {
		_active = false;
		_gameOverTimer = GAME_OVER_DURATION;
		return;
	    }
	}
	else {
	    boolean fmonitor = _gameOverTimer > GAME_OVER_PADDING;
	    _gameOverTimer -= tpf;
	    if(fmonitor && _gameOverTimer <= GAME_OVER_PADDING) {
		flash(_wrapper.getWrappedGame().getWinner().isLocal() ? "Interface/time/victory.png" : "Interface/time/defeat.png");
		Topic.GAME_CHRONO.update();
	    }
	    if(_gameOverTimer < 0) {
		ClientManager.client().onGameStateEnded();
		_instance = null;
		Topic.GAME_ENDED.update();
	    }
	    return;
	}
	Player currentPlayer = game.getCurrentPlayer();
	if(currentPlayer != _currentPlayer) {
	    _currentPlayer = currentPlayer;
	    _instance.flash(Player.isLocal((currentPlayer)) ? "Interface/time/alliedturn.png" : "Interface/time/enemyturn.png");
	}
    }
    
    public void flash(String filename) {
	UX2D.getInstance().createControl(
	    "Time Flash",
	    new ControlState()
		.setDimensions(
		    new Vector2f(1.0f, 0.23f)
		).setBackground(
		    filename
		)
	).changeStateWithDuration(
	    new ControlState()
		.setOpacity(0)
		.invalidate(),
	    2.0f
	);
    }
    
    public Player getPlayer(int id) {
	if(_instance == null || _instance._wrapper == null)
	    return null;
	return _wrapper.getWrappedGame().getPlayer(id);
    }
    
    public static Player getLocalPlayer() {
	if(_instance == null || _instance._wrapper == null)
	    return null;
	return _instance._wrapper.getLocalPlayer();
    }
    
    public static boolean isFriendly(GameComponent component) {
	return component != null && component.getPlayer() == getLocalPlayer();
    }
    
    public Model getModel(GameComponent gameComponent) {
	ModelFactory mf = gameComponent.getGameState().isGraphical() ? ModelFactory.getStandard() : ModelFactory.FAKE;
	ModelTemplate template = TemplateRegistry.MODELS.lookup(gameComponent.getModelName());
	Model model = mf.getModel(template);
	Spatial spatial = model.getSpatial();
	if(spatial != null)
	    spatial.addControl(new SelectionControl(gameComponent));
	return model;
    }
    
    Wrapper getWrapper() {
	return _wrapper;
    }
    
}
