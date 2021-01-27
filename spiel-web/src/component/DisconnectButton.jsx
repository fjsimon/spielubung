import React from "react";
import { connect } from "react-redux";
import { faSignOutAlt } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

const mapStateToProps = (state, props) => {
    return { connected: state.connected, onDisconnectClicked: props.onDisconnectClicked};
};

const ConnectedDisconnectButton = ({ connected, onDisconnectClicked }) => {

    return  <button id="disconnect"
                className="btn btn-default"
                disabled={!connected}
                onClick={onDisconnectClicked}>
                    <FontAwesomeIcon icon={faSignOutAlt} /> Disconnect
            </button>
};

const DisconnectButton = connect(mapStateToProps)(ConnectedDisconnectButton);

export default DisconnectButton;