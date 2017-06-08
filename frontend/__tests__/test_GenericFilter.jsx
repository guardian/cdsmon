import React from 'react';
import {shallow,mount} from 'enzyme';
import moxios from 'moxios';
import GenericFilter from '../app/GenericFilter.jsx';
import sinon from 'sinon';

describe("GenericFilter", ()=>{
    beforeEach(()=>moxios.install());
    afterEach(()=>moxios.uninstall());

    test("should render a select list from its state", (done)=>{
        const onChangedSpy = sinon.spy();

        const rendered = shallow(<GenericFilter onChanged={onChangedSpy}/>)

    });
});