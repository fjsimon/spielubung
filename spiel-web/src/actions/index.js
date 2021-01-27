import { ADD_MESSAGE,
        CLEAR_MESSAGES,
        GAME_OVER,
        SET_SPIELER,
        SET_GEGENSPIELER,
        SET_CONNECTED } from "../constants/action-types";

export function addMessage(payload) {
  return { type: ADD_MESSAGE, payload }
};

export function clearMessages(payload) {
  return { type: CLEAR_MESSAGES, payload }
};

export function gameOver(payload) {
  return { type: GAME_OVER, payload }
};

export function setSpieler(payload) {
  return { type: SET_SPIELER, payload }
};

export function setGegenspieler(payload) {
  return { type: SET_GEGENSPIELER, payload }
};

export function setConnected(payload) {
  return { type: SET_CONNECTED, payload }
};