package org.tycoon.parser.catalog;

import org.tycoon.parser.catalog.Phase;
import org.tycoon.parser.catalog.Seat;

import java.util.Date;
import java.util.ArrayList;
import java.util.Objects;

public class Hand {
    private String _id;

    private String _game_type; // No Limit Hold'em, Pot Limit Omaha...
    private String _currency; // EUR, USD, SC ...

    // Only Tournaments
    private String _tournament_id;
    private Float _entry_price;
    private Integer _level;

    // Table Data
    private String _table_id;
    private Integer _table_max_players;
    private ArrayList<Seat> _seats;
    private Integer _dealer_index; // Which element of _seats is the dealer
    private Integer _dealer_seat_num;

    // Mandatory bets status
    private Float _big_blind; // big blind amount
    private Float _small_blind; // small blind amount
    private Float _ante; // null if no ante

    // Hand's Phases
    private ArrayList<Phase> _phases;

    private Date _date;
    private String _time_zone;

    public Hand() {
        _id = null;
        _game_type = null;
        _currency = null;
        _tournament_id = null;
        _entry_price = 0f;
        _level = 0;
        _table_id = null;
        _table_max_players = 0;
        _seats = new ArrayList<Seat>();
        _dealer_index = 0;
        _dealer_seat_num = 0;
        _big_blind = 0f;
        _small_blind = 0f;
        _ante = 0f;
        _phases = new ArrayList<Phase>();
        _date = null;
        _time_zone = null;
    }

    public Hand(String _id, String _game_type, String _currency, String _tournament_id, Float _entry_price, Integer _level, String _table_id, Integer _table_max_players, ArrayList<Seat> _seats, Integer _dealer_index, Integer _dealer_seat_num, Float _big_blind, Float _small_blind, Float _ante, ArrayList<Phase> _phases, Date _date, String _time_zone) {
        this._id = _id;
        this._game_type = _game_type;
        this._currency = _currency;
        this._tournament_id = _tournament_id;
        this._entry_price = _entry_price;
        this._level = _level;
        this._table_id = _table_id;
        this._table_max_players = _table_max_players;
        this._seats = _seats;
        this._dealer_index = _dealer_index;
        this._dealer_seat_num = _dealer_seat_num;
        this._big_blind = _big_blind;
        this._small_blind = _small_blind;
        this._ante = _ante;
        this._phases = _phases;
        this._date = _date;
        this._time_zone = _time_zone;
    }

    // Getters
    public String getId() {
        return _id;
    }

    public String getGameType() {
        return _game_type;
    }

    public String getCurrency() {
        return _currency;
    }

    public String getTournamentId() {
        return _tournament_id;
    }

    public Float getEntryPrice() {
        return _entry_price;
    }

    public Integer getLevel() {
        return _level;
    }

    public String getTableId() {
        return _table_id;
    }

    public Integer getTableMaxPlayers() {
        return _table_max_players;
    }

    public ArrayList<Seat> getSeats() {
        return _seats;
    }

    public Integer getDealerIndex() {
        return _dealer_index;
    }

    public Integer getDealerSeatNum() {
        return _dealer_seat_num;
    }

    public Float getBigBlind() {
        return _big_blind;
    }

    public Float getSmallBlind() {
        return _small_blind;
    }

    public Float getAnte() {
        return _ante;
    }

    public ArrayList<Phase> getPhases() {
        return _phases;
    }

    public Date getDate() {
        return _date;
    }

    public String getTimeZone() {
        return _time_zone;
    }

    // Additional getters
    public Seat getSeatAt(Integer idx) {
        return _seats.isEmpty() ? null : _seats.get(idx);
    }

    public Seat getDealer() {
        return _seats.get(_dealer_index);
    }

    // Setters
    public void setId(String id) {
        _id = id;
    }

    public void setGameType(String game_type) {
        _game_type = game_type;
    }

    public void setCurrency(String currency) {
        _currency = currency;
    }

    public void setTournamentId(String tournament_id) {
        _tournament_id = tournament_id;
    }

    public void setEntryPrice(Float entry_price) {
        _entry_price = entry_price;
    }

    public void setLevel(Integer level) {
        _level = level;
    }

    public void setTableId(String table_id) {
        _table_id = table_id;
    }

    public void setTableMaxPlayers(Integer table_max_players) {
        _table_max_players = table_max_players;
    }

    public void setSeats(ArrayList<Seat> seats) {
        _seats = seats;
    }

    public void setDealerIndex(Integer dealer_index) {
        _dealer_index = dealer_index;
    }

    public void setDealerSeatNum(Integer dealer_seat_num) {
        _dealer_seat_num = dealer_seat_num;
    }

    public void setBigBlind(Float big_blind) {
        _big_blind = big_blind;
    }

    public void setSmallBlind(Float small_blind) {
        _small_blind = small_blind;
    }

    public void setAnte(Float ante) {
        _ante = ante;
    }

    public void setPhases(ArrayList<Phase> phases) {
        _phases = phases;
    }

    public void setDate(Date date) {
        _date = date;
    }

    public void setTimeZone(String time_zone) {
        _time_zone = time_zone;
    }

    // Additional setters
    public void addSeat(Seat seat) {
        _seats.add(seat);
    }

    public void addPhase(Phase phase) {
        _phases.add(phase);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Hand) {
            Hand that = (Hand) o;
            return Objects.equals(this._id, that._id) &&
                    Objects.equals(this._game_type, that._game_type) &&
                    Objects.equals(this._currency, that._currency) &&
                    Objects.equals(this._tournament_id, that._tournament_id) &&
                    Objects.equals(this._entry_price, that._entry_price) &&
                    Objects.equals(this._level, that._level) &&
                    Objects.equals(this._table_id, that._table_id) &&
                    Objects.equals(this._table_max_players, that._table_max_players) &&
                    ((this._seats == null && that._seats == null) ||
                            (Objects.requireNonNull(this._seats).containsAll(that._seats) && that._seats.containsAll(this._seats))) &&
                    Objects.equals(this._dealer_index, that._dealer_index) &&
                    Objects.equals(this._dealer_seat_num, that._dealer_seat_num) &&
                    Objects.equals(this._big_blind, that._big_blind) &&
                    Objects.equals(this._small_blind, that._small_blind) &&
                    Objects.equals(this._ante, that._ante) &&
                    ((this._phases == null && that._phases == null) ||
                            (Objects.requireNonNull(this._phases).containsAll(that._phases) && that._phases.containsAll(this._phases))) &&
                    this._date.getTime() == that._date.getTime() &&
                    Objects.equals(this._time_zone, that._time_zone);
        }
        return false;
    }


    // toString
    @Override
    public String toString() {
        return "Hand {\n" +
                "ID:\t\t\t" + getId() + "\n" +
                "GAME_TYPE:\t\t" + getGameType() + "\n" +
                "CURRENCY:\t\t" + getCurrency() + "\n" +
                "TOURNAMENT_ID:\t\t" + getTournamentId() + "\n" +
                "ENTRY_PRICE:\t\t" + getEntryPrice() + "\n" +
                "LEVEL:\t\t\t" + getLevel() + "\n" +
                "TABLE_ID:\t\t" + getTableId() + "\n" +
                "MAX_PLAYERS:\t\t" + getTableMaxPlayers() + "\n" +
                "NUM_SEATS:\t\t" + getSeats().size() + "\n" +
                "DEALER_INDEX:\t\t" + getDealerIndex() + "\n" +
                "DEALER_SEAT:\t\t" + getDealerSeatNum() + "\n" +
                "BIG_BLIND:\t\t" + getBigBlind() + "\n" +
                "SMALL_BLIND:\t\t" + getSmallBlind() + "\n" +
                "ANTE:\t\t\t" + getAnte() + "\n" +
                "NUM_PHASES:\t\t" + getPhases().size() + "\n" +
                "DATE:\t\t\t" + getDate() + "\n" +
                "TIMEZONE:\t\t" + getTimeZone() + "\n" +
                "}\n";
    }
}