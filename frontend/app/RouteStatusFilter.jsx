import React from 'react';
import {render} from 'react-dom';
import GenericFilter from './GenericFilter.jsx';

class RouteStatusFilter extends GenericFilter {
    componentWillMount() {
        this.setState({
            options: [
                {name: "*", label: "(all)"},
                {name: "parsing", label: "Parsing"},
                {name: "processing", label: "Processing"},
                {name: "success", label: "Success"},
                {name: "error", label: "Error"}
            ],
            label: "Job Status",
            jsid: "id_job_status"
        })
    }
}

export default RouteStatusFilter;