import React from 'react';
import {render} from 'react-dom';
import axios from 'axios';
import strftime from 'strftime';

class JobComponentView extends React.Component {
    constructor(props) {
        super(props);
        const holdingString="(loading)";
        this.state = {
            error: null,
            jobStatus: {
                currentOperation: holdingString,
                externalId: holdingString,
                lastError: holdingString,
                lastOperation: holdingString,
                lastOperationStatus: holdingString,
                routeStatus:holdingString
            },
            jobFiles: []
        };
    }

    componentWillMount() {
        const joburl = "/jobs/" + this.props.externalId + "/status";
        axios.get(joburl).then((response)=>
            this.setState({jobStatus: response.data})
        ).catch((error)=>{
            console.error(error);
            this.setState({error: error.toString()});
        });

        const filesUrl = "/jobs/" + this.props.internalId + "/files";
        axios.get(filesUrl).then((response)=>
            this.setState({jobFiles: response.data})
        ).catch((error)=>{
            console.error(error);
            this.setState({error: error.toString()});
        });
    }

    dateFormatter(datetime){
        return strftime("%a %b %o %H:%M:%S %Y", new Date(datetime))
    }

    statusFormatter(status){
        return (<span className={"status_"+status}>{status}</span>)
    }

    render() {
        return(<tr>
            <td>
                <span style={ {fontSize: "1em"}}>{this.props.routeName}</span><br/>
                <span style={ {fontSize: "0.6em"}}>{this.dateFormatter(this.props.created)}</span>
            </td>
            <td>
                {this.statusFormatter(this.state.jobStatus.routeStatus)}
            </td>
            <td>
                {this.state.jobStatus.currentOperation}
            </td>
            <td>
                {this.state.jobStatus.lastOperation}
            </td>
            <td>
                {this.statusFormatter(this.state.jobStatus.lastOperationStatus)}
            </td>
            <td>
                <ul className="file_list">
                {
                    this.state.jobFiles.map((fileName)=><li key={fileName}>{fileName}</li>)
                }
                </ul>
            </td>
            <td>
                {this.state.jobStatus.lastError}
            </td>
            <td>
                <span style={{fontSize: "1em"}}>{this.props.hostname}</span><br/>
                <span style={{fontSize: "0.7em"}}>{this.props.hostip}</span><br/>
            </td>
        </tr>)
    }
}

export default JobComponentView;