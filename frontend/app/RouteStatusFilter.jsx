import React from 'react';
import {render} from 'react-dom';
import GenericFilter from './GenericFilter.jsx';

class RouteNamesFilter extends GenericFilter {
    constructor(props) {
        super(props);
    }

    componentWillMount() {
        this.setState({
            options: [
                {name: "*", label: "(all)"},
                {name: "parsing", label: "Parsing"},
                {name: "processing", label: "Processing"},
                {name: "success", label: "Success"},
                {name: "error", label: "Error"}
            ]
        })
    }
}

export default RouteNamesFilter;