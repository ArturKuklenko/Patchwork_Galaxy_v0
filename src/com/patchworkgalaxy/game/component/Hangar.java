package com.patchworkgalaxy.game.component;

import com.patchworkgalaxy.template.TemplateRegistry;
import com.patchworkgalaxy.template.parser.CanBeEvent;
import com.patchworkgalaxy.template.types.ShipTemplate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

class Hangar {

    private final Ship _ship;
    private final Set<Ship> _ships;
    private final Set<String> _compatibility;
    private final int _capacity;
    //adolfnew
    private int _buildtype;
    //adolfnew

    Hangar(int capacity, Set<String> compatibility, Ship ship) {
        _ship = ship;
        _ships = new HashSet<>();
        _compatibility = new HashSet<>(compatibility);
        _capacity = capacity;
        _buildtype = 0;

    }
    //adolfnew
    ShipTemplate getCompatibleTemplate() {
       Random rand = new Random();
            int i =rand.nextInt(_compatibility.size());
            System.out.println("RandomShipSPace = " + i);
            List<String> list = new ArrayList<String>(_compatibility);
            String shipName= list.get(i);
            ShipTemplate randomShipTemplate = TemplateRegistry.SHIPS.lookup(shipName);
            return randomShipTemplate;
    }
    //adolfnewEnd

    boolean canAcceptShip(Ship ship) {
        if (ship == null) {
            return false;
        }
        if (ship.getPlayer() != _ship.getPlayer()) {
            return false;
        }
        if (!_compatibility.contains(ship.getName())) {
            return false;
        }
        if (_ships.size() >= _capacity) {
            return false;
        }
        return true;
    }

    boolean acceptShip(Ship ship) {
        if (!canAcceptShip(ship)) {
            return false;
        }
        _ships.add(ship);
        ship.setPosition(null);
        return true;
    }

    boolean launchShip(Ship ship) {
        return _ships.remove(ship);
    }

    void dispatchHangarEvent(GameComponent sender, String eventName, GameEvent cause) {
        CanBeEvent template = getEventTemplate(eventName);
        for (Ship ship : _ships) {
            GameEvent event = template.toEvent(sender, ship, cause);
            event.enqueue();
        }
    }

    Set<Ship> getShips() {
        return new HashSet<>(_ships);
    }

    private CanBeEvent getEventTemplate(String eventName) {
        return ((CanBeEvent) (_ship.getGameState().lookup(eventName)));
    }
    //adolfnewStart

    public Integer getHangarCapacity() {
        return _capacity;
    }
    //adolfnewEnd
}
