import React from 'react';
import {render} from 'react-dom';

class GenericFilter extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            options: []
        };
        this.selectorChanged = this.selectorChanged.bind(this);
    }

    selectorChanged(event) {
        this.props.onChanged(event.target.value)
    }

    componentWillMount() {

    }

    render() {
        return(<select onChange={this.selectorChanged}>
            {this.state.options.map((opt)=><option key={opt.name} name={opt.name}>{opt.label}</option>)}
        </select>)
    }
}

export default GenericFilter;