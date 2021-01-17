import React, { Component, useState, useEffect} from 'react';
import { faHome, faSignInAlt, faSignOutAlt} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

class GameComponent extends Component {

    constructor(props) {
        super(props)

        this.state = {
            username: '',
            gegenspieler: '',
            connected: false,
            messages: [],
            spielende: false,
            winner: '',
            error: false,
            errorMessage: ''
        }

        this.client = null;

        this.handleChange = this.handleChange.bind(this)
        this.connect = this.connect.bind(this)
        this.disconnect = this.disconnect.bind(this)
    }

    componentDidMount() {
      window.addEventListener("beforeunload", this.disconnect)
    }

    componentWillUnmount() {
       window.removeEventListener("beforeunload", this.disconnect)
    }

    handleChange(event) {
        this.setState({ username: event.target.value });
    }

    disconnect(event) {

        if (this.client !== null) {
            this.client.disconnect();
            this.setState({ connected: false, username: '', messages: [], spielende: false });
        }
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
            self.setState({ connected: true });
            stompClient.send('/app/start', {});

            stompClient.subscribe('/user/queue/notifications', function (response) {
                var gameMessage = JSON.parse(response.body);

                switch (gameMessage.spielStatus) {
                    case 'WARTEN':
                        break;
                    case 'STARTEN':

                        self.setState({ gegenspieler: gameMessage.gegenspieler });
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
                        self.setState({messages: [...self.state.messages,  message ]});

                        var message = {
                            value: value,
                            move: addition
                        };

                        stompClient.send('/app/play', {}, JSON.stringify(message));
                        break;
                    case 'SPIEL_ENDE':

                        var resultMessage = gameMessage.winner ?
                            <div className="alert alert-success col-md-12" id="win"> You are the winner </div> :
                            <div className="alert alert-danger col-md-12" id="lost"> You lost the game </div>;

                        self.setState({ spielende: true , winner: resultMessage });
                        break;
                   case 'TRENNEN':

                        self.setState({ gegenspieler: '', messages: [], spielende: false});
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

    connectButton = () => (
        <button id="connect"
            className="btn btn-default"
            disabled={this.state.connected}
            onClick={this.connect}>
            <FontAwesomeIcon icon={faSignInAlt} /> Connect
        </button>
    )

    disconnectButton = () => (
        <button id="disconnect"
            className="btn btn-default"
            disabled={!this.state.connected}
            onClick={this.disconnect}>
            <FontAwesomeIcon icon={faSignOutAlt} />  Disconnect
        </button>
    )

    connectedAs = () => (
        <div id="connectedAs">Logged as {this.state.username}</div>
    )

    playAgainst = () => (
        <div id="playAgainst">Playing against {this.state.gegenspieler}</div>
    )

    finalResult = () => (
        <div>{this.state.winner}</div>
    )

    error = () => (
        <div className="alert alert-warning col-md-12" id="errorMessage"> {this.state.errorMessage} </div>
    )

    render() {
        return (
            <div className="login col-md-20">

                { this.state.connected ? <this.connectedAs /> : null }
                { this.state.connected && this.state.gegenspieler ? <this.playAgainst /> : null }

                <div className="col-md-15">
                        <this.usernameInput />
                        <this.connectButton />
                        <this.disconnectButton />
                </div>

                { this.state.error ? <this.error /> : null }

                <div className="col-md-15">
                    { this.state.messages.map( (item, index) => (<p className="alert alert-info" key={index} >{item}</p>) ) }
                </div>

                <div className="col-md-15">
                    { this.state.spielende ? <this.finalResult /> : null }
                </div>

            </div>
        )
    }
}

export default GameComponent