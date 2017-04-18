import React from 'react';
import {render} from 'react-dom';

class GenericFilter extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            options: [],
            label: "",
            jsid: "",
            labelClass: "filter_label"
        };
        this.selectorChanged = this.selectorChanged.bind(this);
    }

    selectorChanged(event) {
        this.props.onChanged(event.target.value)
    }

    componentWillMount() {

    }

    render() {
        return(<span className="dashboard_component">
            <label htmlFor={this.state.jsid} className={this.state.labelClass}>{this.state.label}</label>
                <select id={this.state.jsid} onChange={this.selectorChanged}>
                    {this.state.options.map((opt)=><option key={opt.name} value={opt.name}>{opt.label}</option>)}
                </select>
            </span>)
    }
}

export default GenericFilter;