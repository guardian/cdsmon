import React from 'react';
import {shallow,mount} from 'enzyme';
import moxios from 'moxios';
import GenericFilter from '../app/GenericFilter.jsx';
import sinon from 'sinon';
import assert from 'assert';

describe("GenericFilter", ()=>{
    beforeEach(()=>moxios.install());
    afterEach(()=>moxios.uninstall());

    test("should render a select list from its state", ()=>{
        const onChangedSpy = sinon.spy();

        const rendered = shallow(<GenericFilter onChanged={onChangedSpy}/>);

        rendered.instance().setState({
            label: "test render control",
            jsid: "testcontrol",
            options: [{name: "one", label:"First one"},{name: "two", label:"Second one"}]
        });

        rendered.update();

        expect(rendered.find('.filter_label').text()).toEqual("test render control");
        expect(rendered.find('#testcontrol').childAt(0).props().value).toEqual("one");
        expect(rendered.find('#testcontrol').childAt(1).props().value).toEqual("two");
        expect(rendered.find('#testcontrol').childAt(0).text()).toEqual("First one");
        expect(rendered.find('#testcontrol').childAt(1).text()).toEqual("Second one");
    });

    test("should trigger a changed event in the parent", ()=>{
        const onChangedSpy = sinon.spy();
        const rendered = shallow(<GenericFilter onChanged={onChangedSpy}/>);

        rendered.instance().setState({
            label: "test render control",
            jsid: "testcontrol",
            options: [{name: "one", label:"First one"},{name: "two", label:"Second one"}]
        });

        rendered.update();
        rendered.find("select").simulate("change", {target: {value: "two"}});

        assert(onChangedSpy.calledOnce);
        expect(onChangedSpy.getCall(0).args[0]).toEqual("two");
    });
});