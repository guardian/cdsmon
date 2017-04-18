import React from 'react';
import {render} from 'react-dom';
import axios from 'axios';
import strftime from 'strftime';

class LogView extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            nextPageNumber: 0,
            logLines: [],
            isLoading: false
        };
        this.pageSize = this.props.pageSize ? this.props.pageSize : 10;
        this.eagerPages = 10;
    }

    componentWillMount() {
        this.getNextPage();
    }

    getNextPage() {
        const jobId = this.props.match.params.externalid;
        const startAt = this.state.nextPageNumber * this.pageSize;
        const urlstring = '/logapi/' + jobId + '?startAt=' + startAt + '&size=' + this.pageSize;

        this.setState({isLoading: true});

        axios.get(urlstring).then((response)=>{
            this.setState({
                nextPageNumber: this.state.nextPageNumber + 1,
                logLines: this.state.logLines.concat(response.data),
                isLoading: false
            });
            if(this.state.nextPageNumber<this.eagerPages) this.getNextPage();
        }).catch((error)=>{
            console.error(error);
            this.setState({
                isLoading: false
            })
        })
    }

    render() {
        return (<span>Not yet implemented</span>)
    }
}

export default LogView;