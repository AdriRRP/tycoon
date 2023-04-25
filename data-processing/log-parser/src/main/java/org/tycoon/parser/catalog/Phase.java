package org.tycoon.parser.catalog;

import java.util.ArrayList;
import java.util.Objects;

public class Phase {
    // Class Constants
    public static final int HOLECARDS = 0;
    public static final int FLOP = 1;
    public static final int TURN = 2;
    public static final int RIVER = 3;
    public static final int SHOWDOWN = 4;

    private Integer _id; // HOLECARDS, FLOP, TURN, RIVER, SHOWDOWN
    private String _cards; // Comunity cards avaiable
    private Float _pot; // Pot size at the begining of the phase
    private ArrayList<Action> _actions; // Actions done in this hand

    public Phase() {
        _id = null;
        _cards = null;
        _pot = 0f;
        _actions = new ArrayList<Action>();
    }

    public Phase(Integer _id, String _cards, Float _pot, ArrayList<Action> _actions) {
        this._id = _id;
        this._cards = _cards != null ? _cards.trim() : null;;
        this._pot = _pot;
        this._actions = _actions;
    }

    // Getters
    public Integer getId() {
        return _id;
    }

    public String getCards() {
        return _cards;
    }

    public Float getPot() {
        return _pot;
    }

    public ArrayList<Action> getActions() {
        return _actions;
    }

    // Aditional Getters
    public Action getActionAt(Integer idx) {
        return _actions.get(idx);
    }

    // Setters
    public void setId(Integer id) {
        _id = id;
    }

    public void setCards(String cards) {
        _cards = cards;
    }

    public void setPot(Float pot) {
        _pot = pot;
    }

    public void setActions(ArrayList<Action> actions) {
        _actions = actions;
    }

    // Aditional Setters
    public void addAction(Action action) {
        _actions.add(action);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Phase) {
            Phase that = (Phase) o;
            return Objects.equals(this._id, that._id) &&
                    Objects.equals(this._cards, that._cards) &&
                    Objects.equals(this._pot, that._pot) &&
                    ((this._actions == null && that._actions == null) ||
                            (Objects.requireNonNull(this._actions).containsAll(that._actions) && that._actions.containsAll(this._actions)));
        }
        return false;
    }

    // toString
    @Override
    public String toString() {
        return "Phase {\n" +
                "ID:\t\t\t" + getId() + "\n" +
                "CARDS:\t\t\t" + getCards() + "\n" +
                "POT:\t\t\t" + getPot() + "\n" +
                "NUM_ACTIONS:\t\t" + getActions().size() + "\n" +
                "}\n";
    }
}