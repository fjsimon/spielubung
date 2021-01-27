import React, { Component, useState, useEffect} from 'react';
import { faHome, faSignInAlt, faSignOutAlt, faRedo } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

import MessageList from "./MessageList.jsx";
import EndeMessage from "./EndeMessage.jsx";
import RestartButton from "./RestartButton.jsx";
import ConnectButton from "./ConnectButton.jsx";
import DisconnectButton from "./DisconnectButton.jsx";

import { connect } from "react-redux";
import { addMessage, clearMessages, gameOver, setConnected, setSpieler, setGegenspieler} from "../actions/index";

function mapDispatchToProps(dispatch) {
  return {
    addMessage: message => dispatch(addMessage(message)),
    clearMessages: message => dispatch(clearMessages()),
    gameOver: payload => dispatch(gameOver(payload)),
    setConnected: payload => dispatch(setConnected(payload)),
    setSpieler: payload => dispatch(setSpieler(payload)),
    setGegenspieler: payload => dispatch(setGegenspieler(payload))
  };
}

class ConnectedGame extends Component {

    constructor(props) {
        super(props)

        this.state = {
            username: '',
            error: false,
            errorMessage: ''
        }

        this.client = null;

        this.handleChange = this.handleChange.bind(this);
        this.connect = this.connect.bind(this);
        this.disconnect = this.disconnect.bind(this);
        this.restart = this.restart.bind(this);
    }

    componentDidMount() {
      window.addEventListener("beforeunload", this.disconnect)
    }

    componentWillUnmount() {
       window.removeEventListener("beforeunload", this.disconnect)
    }

    handleChange(event) {
        this.setState({ username: event.target.value });
        this.props.setSpieler({ spieler: event.target.value });
    }

    disconnect(event) {

        if (this.client !== null) {
            this.client.disconnect();
            this.props.clearMessages();
            this.props.gameOver( { ende: false } );
            this.props.setConnected( { connected: false } );
            this.props.setSpieler({ spieler: ''});
            this.props.setGegenspieler({ gegenspieler: ''});
        }
    }

    restart(event) {

        this.props.clearMessages();
        this.props.gameOver({ende: false});
        this.client.send('/app/start', {});
    }

    connect(event) {

        var self = this;
        var socket = new SockJS('http://localhost:8080/spielubung');
        var stompClient = Stomp.over(socket);
        this.client = stompClient;
        stompClient.reconnect_delay = 0;
        stompClient.debug = function (str) {};

        stompClient.connect({
            username: this.state.username
        }, function (frame) {

            console.log(frame);
            self.props.setConnected({ connected: true });
            stompClient.send('/app/start', {});

            stompClient.subscribe('/user/queue/notifications', function (response) {
                var gameMessage = JSON.parse(response.body);

                switch (gameMessage.spielStatus) {
                    case 'WARTEN':

                        break;
                    case 'STARTEN':

                        self.props.setGegenspieler({ gegenspieler: gameMessage.gegenspieler });
                        self.props.clearMessages();
                        self.props.gameOver({ ende: false });
                        if(gameMessage.primary) {
                            var message = {
                                value: Math.floor(Math.random() * 1000) + 1
                            };

                            stompClient.send('/app/number', {}, JSON.stringify(message));
                        }
                        break;
                    case 'SPIELEN':

                        var value = gameMessage.value;
                        var addition = self.getNumberToMakeValueDivisibleByThree(value);
                        var message = 'Received ' + value + ', addition ' + addition +
                            ', result = (' + value + '' + (addition < 0 ?'':'+') + addition + ')/3 = ' + (value + addition)/3;
                        self.props.addMessage(message);
                        stompClient.send('/app/play', {}, JSON.stringify({ value: value, move: addition }));
                        break;
                    case 'SPIEL_ENDE':

                        self.props.gameOver( { ende: true , winner: gameMessage.winner } );
                        break;
                   case 'TRENNEN':

                        self.props.setGegenspieler( { gegenspieler: '' } );
                        self.props.clearMessages();
                        self.props.gameOver( { ende: false } );
                        break;
               }
            });

            stompClient.subscribe('/user/queue/exceptions', function (response) {
                self.setState({ error: true , errorMessage: response});
            });

        }, this.showError);

    }

    getNumberToMakeValueDivisibleByThree(value) {
        var modulo = value % 3;
        switch (modulo) {
            case 0:
                return 0;
            case 1:
                return -1;
            default:
                return 1;
        }
    }

    usernameInput = () => (
        <input type="text" id="username"
            value={this.state.username}
            disabled={this.state.connected}
            onChange={this.handleChange}
            placeholder="Your username here..." />
    )

    error = () => (
        <div className="alert alert-warning col-md-12" id="errorMessage"> {this.state.errorMessage} </div>
    )

    render() {
        return (
            <div className="login col-md-20">
                <div className="col-md-15">
                    <this.usernameInput />
                    <ConnectButton onConnectClicked={this.connect} />
                    <DisconnectButton onDisconnectClicked={this.disconnect} />
                    <RestartButton onRestartClicked={this.restart} />
                </div>

                { this.state.error ? <this.error /> : null }

                <MessageList />
                <EndeMessage />
            </div>
        )
    }
}

const GameComponent = connect(null, mapDispatchToProps)(ConnectedGame);

export default GameComponent;