import React from 'react';
import {render} from 'react-dom';
import OverviewTable from './OverviewTable.jsx';

//for react-sortable-table
window.React = require('react');

class App extends React.Component {
    render () {
        return <OverviewTable/>;
    }
}

render(<App/>, document.getElementById('app'));