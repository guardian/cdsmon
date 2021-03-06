import React from 'react';
import {render} from 'react-dom';
import GenericFilter from './GenericFilter.jsx';
import axios from 'axios';

class RouteNamesFilter extends GenericFilter {
    componentWillMount() {
        this.setState({
            label: "Route Name",
            jsid: "id_routename_filter"
        });

        axios.get("/routenames").then((response)=>
            this.setState({
                options:
                    [{name: "(all)", label: "(all)"}].concat(response.data.map(
                        (entry)=>{return {name: entry, label: entry}})
                    )
            })
        ).catch((error)=>console.error(error))
    }
}

export default RouteNamesFilter;