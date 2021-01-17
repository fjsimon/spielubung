import React, { Component } from 'react'
import { Link, withRouter } from 'react-router-dom'
import { faHome, faPlug} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

class HeaderComponent extends Component {

    render() {
        return (
                <div className="row">
                    <div className="col-md-12">
                        <header id="header">
                            <h2>SpielUbung</h2>
                        </header>
                    </div>
                </div>
        )
    }
}

export default HeaderComponent