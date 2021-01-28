import React from "react";
import { connect } from "react-redux";

const mapStateToProps = (state, props) => {
    return { connected: state.connected, username: props.username, onUsernameChanged: props.onUsernameChanged};
};

const ConnectedUsernameInput= ({ connected, username, onUsernameChanged}) => {

    return <input type="text" id="username"
                value={username}
                disabled={connected}
                onChange={onUsernameChanged}
                placeholder="Your username here..." />
};

const UsernameInput = connect(mapStateToProps)(ConnectedUsernameInput);

export default UsernameInput;