import React from 'react';
import {render} from 'react-dom';
import axios from 'axios';
import strftime from 'strftime';

class LogView extends React.Component {
    constructor(props) {
        super(props);
        this.state = {}
    }

    componentWillMount() {
        const jobId = this.props.match.params.externalid;
        console.log(this.props);
        axios.get('/logapi/' + jobId).then((response)=>{

        }).catch((error)=>{

        })
    }

    render() {
        return (<span>Not yet implemented</span>)
    }
}

export default LogView;