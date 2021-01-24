import React from "react";
import { connect } from "react-redux";

const mapStateToProps = state => {
    return { messages: state.messages };
};

const ConnectedList = ({ messages }) => {
    return messages.map( (item, index) =>
       (<p className="alert alert-info" key={index} >{item}</p>)
    )
};

const MessageList = connect(mapStateToProps)(ConnectedList);

export default MessageList;