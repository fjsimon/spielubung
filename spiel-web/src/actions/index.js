import { ADD_MESSAGE } from "../constants/action-types";
import { CLEAR_MESSAGES } from "../constants/action-types";
import { GAME_OVER } from "../constants/action-types";

export function addMessage(payload) {
  return { type: ADD_MESSAGE, payload }
};

export function clearMessages(payload) {
  return { type: CLEAR_MESSAGES, payload }
};

export function gameOver(payload) {
  return { type: GAME_OVER, payload }
};