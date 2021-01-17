import React, { Component } from 'react';
import { BrowserRouter as Router, Route, Switch, Link } from 'react-router-dom'
import { faHome, faPlug} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import HeaderComponent   from './HeaderComponent.jsx';
import GameComponent   from './GameComponent.jsx';

class InstructorApp extends Component {

    render() {
        return (
            <>
                <div id="main-content" className="container">
                    <HeaderComponent />
                    <GameComponent />
                </div>
            </>
        )
    }
}

export default InstructorApp