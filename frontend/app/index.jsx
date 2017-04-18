import React from 'react';
import {render} from 'react-dom';
import OverviewTable from './OverviewTable.jsx';
import LogView from './LogView.jsx';
// import { Router, Route } from 'react-router'
import {BrowserRouter, Route, Link, IndexRoute } from 'react-router-dom';

//for react-sortable-table
window.React = require('react');

class App extends React.Component {
    render () {
        return <div></div>;
    }
}

render(<BrowserRouter>
        <div>
            <Route path="/log/:externalid" component={LogView}/>
            <Route exact path="/" component={OverviewTable}/>
        </div>
    </BrowserRouter>, document.getElementById('app'));