import React from 'react';
import {render} from 'react-dom';
import axios from 'axios';

import RouteNamesFilter from './RouteNamesFilter.jsx';
import RouteStatusFilter from './RouteStatusFilter.jsx';
import JobComponentView from './JobComponentView.jsx';

class OverviewTable extends React.Component {
    constructor(props){
        super(props);
        this.state = {
            'error': null,
            'jobsList': [],
            'routeNames': [],
            'nameFilter': null,
            'statusFilter': null
        };
        this.nameFilterChanged = this.nameFilterChanged.bind(this);
        this.statusFilterChanged = this.statusFilterChanged.bind(this);
    }

    requestUrl(){
        let urlparts = [];
        if(this.state.nameFilter){
            urlparts.push("routename=" + this.state.nameFilter);
        }
        if(this.state.statusFilter){
            urlparts.push("status=" + this.state.statusFilter);
        }
        if(urlparts.length>0) return "/jobs?" + urlparts.join("&");
        return "/jobs";
    }

    componentWillMount() {
        this.loadData();
    }

    componentDidUpdate(nextProps,nextState) {
        console.log(nextState);
        if(nextState.nameFilter!=this.state.nameFilter || nextState.statusFilter !=this.state.statusFilter) this.loadData();
    }

    loadData() {
        axios.get(this.requestUrl()).then((response)=>{
            this.setState({jobsList: response.data})
        }).catch(function(error){
            console.error("Got error in request to /jobs");
            if (error.response) {
                console.log("error came from server: ");
                console.log(error.response);
                console.log(this);
                // The request was made and the server responded with a status code
                // that falls out of the range of 2xx
                this.setState({error: error.response});
            } else if (error.request) {
                // The request was made but no response was received
                // `error.request` is an instance of XMLHttpRequest in the browser and an instance of
                // http.ClientRequest in node.js
                console.error(error.request);
                this.setState({error: {data: "No response received from server"}});
            } else {
                // Something happened in setting up the request that triggered an Error
                this.setState({error: {data: "Clientside error: " + error.message}});
                console.error('Error', error.message);
            }
            console.error(error.config);
        }.bind(this));
    }

    nameFilterChanged(newname){
        console.log("Name filter changed to " + newname);
        if(newname=="(all)"){
            this.setState({nameFilter: null});
        } else {
            this.setState({nameFilter: newname});
        }
    }

    statusFilterChanged(newstatus){
        console.log("Status filter changed to "+newstatus);
        if(newstatus=="(all)"){
            this.setState({statusFilter: null});
        } else {
            this.setState({statusFilter: newstatus});
        }
    }

    render(){
        if(this.state.error) return(<span className="error">{this.state.error.data}</span>);

        return(
            <div>
                <div id="filterbar">
                    <RouteNamesFilter routeNames={this.state.routeNames} onChanged={this.nameFilterChanged}/>
                    <RouteStatusFilter onChanged={this.statusFilterChanged}/>
                </div>
                <div id="content">
                    <table className="dashboardpanel">
                        <thead>
                        <tr className="dashboardheader">
                            <td>Route & Start time</td>
                            <td>Status</td>
                            <td>Current Operation</td>
                            <td>Last Operation</td>
                            <td>Last Operation status</td>
                            <td>Files</td>
                            <td>Last Error</td>
                            <td>Server</td>
                        </tr>
                        </thead>

                        <tbody>
                        {
                            this.state.jobsList.map((cdsJobData)=> {
                                    return (<JobComponentView
                                        key={cdsJobData.internalId}
                                        internalId={cdsJobData.internalId}
                                        externalId={cdsJobData.externalId}
                                        created={cdsJobData.created}
                                        routeName={cdsJobData.routeName}
                                        status={cdsJobData.status}
                                        hostname={cdsJobData.hostname}
                                        hostip={cdsJobData.hostip}
                                    />)
                                }
                            )
                        }
                        </tbody>
                    </table>
                </div>
            </div>
        )
    }
}

export default OverviewTable;