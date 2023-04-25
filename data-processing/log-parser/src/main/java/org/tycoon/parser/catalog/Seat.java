package org.tycoon.parser.catalog;

import java.util.Objects;

public class Seat {
    private Integer _num; // Number of the seat in current table
    private String _id_player; // Id of the Player placed on this Seat
    private Float _initial_stack; // Number of chips that player has at the begining
    private Float _final_stack; // Number of chips that player has at the end
    private String _cards; // If are Known, the  player's hole cards
    private Boolean _win; // If are Known, the  player's hole cards
    private Boolean _itm; // If are Known, the  player's hole cards

    public Seat() {
        _num = -1;
        _id_player = null;
        _initial_stack = 0f;
        _final_stack = 0f;
        _cards = null;
        _win = false;
        _itm = false;
    }

    public Seat(
            Integer _num,
            String _id_player,
            Float _initial_stack,
            Float _final_stack,
            String _cards,
            Boolean _win,
            Boolean _itm
    ) {
        this._num = _num;
        this._id_player = _id_player;
        this._initial_stack = _initial_stack;
        this._final_stack = _final_stack;
        this._cards = _cards != null ? _cards.trim() : null;
        this._win = _win;
        this._itm = _itm;
    }

    public Seat(
            Integer _num,
            String _id_player,
            Float _initial_stack,
            Float _final_stack,
            String _cards
    ) {
        this._num = _num;
        this._id_player = _id_player;
        this._initial_stack = _initial_stack;
        this._final_stack = _final_stack;
        this._cards = _cards != null ? _cards.trim() : null;
        this._win = false;
        this._itm = false;
    }

    // Getters
    public Integer getNum() {
        return _num;
    }

    public String getIdPlayer() {
        return _id_player;
    }

    public Float getInitialStack() {
        return _initial_stack;
    }

    public Float getFinalStack() {
        return _final_stack;
    }

    public String getCards() {
        return _cards;
    }

    public Boolean isItm() {
        return _itm;
    }

    public Boolean isWin() {
        return _win;
    }

    // Setters
    public void setNum(Integer num) {
        _num = num;
    }

    public void setIdPlayer(String id_player) {
        _id_player = id_player;
    }

    public void setInitialStack(Float initial_stack) {
        _initial_stack = initial_stack;
    }

    public void setFinalStack(Float final_stack) {
        _final_stack = final_stack;
    }

    public void setCards(String cards) {
        _cards = cards != null ? cards.trim() : null;
    }

    public void setItm(Boolean itm) {
        _itm = itm;
    }

    public void setWin(Boolean win) {
        _win = win;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Seat) {
            Seat that = (Seat) o;
            return Objects.equals(this._num, that._num) &&
                    Objects.equals(this._id_player, that._id_player) &&
                    Objects.equals(this._initial_stack, that._initial_stack) &&
                    Objects.equals(this._final_stack, that._final_stack) &&
                    Objects.equals(this._cards, that._cards) &&
                    this._win == that._win &&
                    this._itm == that._itm;
        }
        return false;
    }

    // toString
    @Override
    public String toString() {
        return "Seat {\n" +
                "NUM:\t\t\t" + getNum() + "\n" +
                "ID_PLAYER:\t\t" + getIdPlayer() + "\n" +
                "INI_STACK:\t\t" + getInitialStack() + "\n" +
                "FIN_STACK:\t\t" + getFinalStack() + "\n" +
                "CARDS:\t\t\t" + getCards() + "\n" +
                "WIN:\t\t\t" + isWin() + "\n" +
                "ITM:\t\t\t" + isItm() + "\n" +
                "}\n";
    }
}