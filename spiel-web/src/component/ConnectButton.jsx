import React from "react";
import { connect } from "react-redux";
import { faSignInAlt } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

const mapStateToProps = (state, props) => {
    return { connected: state.connected, onConnectClicked: props.onConnectClicked};
};

const ConnectedConnectButton = ({ connected, onConnectClicked }) => {

    return  <button id="connect"
                className="btn btn-default"
                disabled={connected}
                onClick={onConnectClicked}>
                    <FontAwesomeIcon icon={faSignInAlt} /> Connect
            </button>
};

const ConnectButton = connect(mapStateToProps)(ConnectedConnectButton);

export default ConnectButton;