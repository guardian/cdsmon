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
            'routeNames': []
        };
        this.nameFilterChanged = this.nameFilterChanged.bind(this);
    }

    componentWillMount(){
        axios.get('/jobs').then((response)=>{
            this.setState({jobsList: response.data})
        }).catch((error)=> {
            console.error(error);
            this.setState({error: error.toString});
        })
    }

    nameFilterChanged(newname){

    }

    render(){
        if(this.state.error) return(<span className="error">{this.state.error}</span>);

        return(
            <div>
                <div id="filterbar">
                    <RouteNamesFilter routeNames={this.state.routeNames} onChanged={this.nameFilterChanged}/>
                    <RouteStatusFilter/>
                </div>
                <div id="content">
                    <table className="dashboardpanel">
                        <thead>
                        <tr>
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