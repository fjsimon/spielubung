import React, { Component } from 'react'
import { connect } from "react-redux";
import { Link, withRouter } from 'react-router-dom'
import { faHome, faPlug} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

const mapStateToProps = (state, props) => {
    return { connected: state.connected, spieler: state.spieler, gegenspieler: state.gegenspieler};
};

const ConnectedHeaderComponent = ({ connected, spieler, gegenspieler }) => {

    return (
            <div>
                <div className="row">
                    <div className="col-md-12">
                        <header id="header">
                            <h2>SpielUbung</h2>
                        </header>
                    </div>
                </div>
                { connected ? <div className="row"><div id="connectedAs">Logged as {spieler}</div></div> : null }
                { connected && gegenspieler ? <div className="row"><div id="playAgainst">Playing against {gegenspieler}</div></div> : null }
            </div>
        )

};

const HeaderComponent = connect(mapStateToProps)(ConnectedHeaderComponent);

export default HeaderComponent;