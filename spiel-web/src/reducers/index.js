import { ADD_MESSAGE, CLEAR_MESSAGES, GAME_OVER } from "../constants/action-types";

const initialState = {
    messages: [],
    response: {},
    ende: false,
    winner: false,
    connected: true
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

    return state;
}

export default rootReducer;