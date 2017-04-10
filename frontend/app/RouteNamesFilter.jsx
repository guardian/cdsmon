import React from 'react';
import {render} from 'react-dom';
import GenericFilter from './GenericFilter.jsx';

class RouteNamesFilter extends GenericFilter {
    componentWillMount() {
        this.setState({
            options: [
                {name: "test_entry", label: "Test Entry"}
            ]
        })
    }
}

export default RouteNamesFilter;