import React from "react";
import { connect } from "react-redux";
import { faRedo } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

const mapStateToProps = (state, props) => {
    return { connected: state.connected, ende: state.ende, onRestartClicked: props.onRestartClicked};
};

const ConnectedRestartButton = ({ connected, ende, onRestartClicked }) => {

    return  <button id="restart"
                className="btn btn-default"
                disabled={!connected || !ende}
                onClick={onRestartClicked}>
                    <FontAwesomeIcon icon={faRedo} /> Restart
            </button>
};

const RestartButton = connect(mapStateToProps)(ConnectedRestartButton);

export default RestartButton;