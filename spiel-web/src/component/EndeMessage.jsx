import React from "react";
import { connect } from "react-redux";

const mapStateToProps = state => {
    return { ende: state.ende, winner: state.winner };
};

const ConnectedMessage = ({ ende, winner }) => {

    var endeMessage = winner ?
        <div className="alert alert-success col-md-12" id="win"> You are the winner </div> :
        <div className="alert alert-danger col-md-12" id="lost"> You lost the game </div>;

    return ende ? endeMessage : null
};

const EndeMessage = connect(mapStateToProps)(ConnectedMessage);

export default EndeMessage;