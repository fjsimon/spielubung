import { ADD_MESSAGE,
    CLEAR_MESSAGES,
    GAME_OVER,
    SET_SPIELER,
    SET_GEGENSPIELER,
    SET_CONNECTED } from "../constants/action-types";

const initialState = {
    messages: [],
    response: {},
    ende: false,
    winner: false,
    connected: false,
    spieler: '',
    gegenspieler: ''
};

function rootReducer(state = initialState, action) {

    if (action.type === ADD_MESSAGE) {
        return Object.assign({}, state, {
            messages: state.messages.concat(action.payload)
        });
    }

    if (action.type === CLEAR_MESSAGES) {
        return Object.assign({}, state, {
            messages: []
        });
    }

    if (action.type === GAME_OVER) {
        return Object.assign({}, state, {
            ende: action.payload.ende,
            winner: action.payload.winner
        });
    }

    if (action.type === SET_CONNECTED) {
        return Object.assign({}, state, {
            connected: action.payload.connected
        });
    }

    if (action.type === SET_SPIELER) {
        return Object.assign({}, state, {
            spieler: action.payload.spieler
        });
    }

    if (action.type === SET_GEGENSPIELER) {
        return Object.assign({}, state, {
            gegenspieler: action.payload.gegenspieler
        });
    }

    return state;
}

export default rootReducer;