import React from 'react';
import {shallow,mount} from 'enzyme';
import moxios from 'moxios';
import RouteStatusFilter from '../app/RouteStatusFilter.jsx';
import sinon from 'sinon';

describe("UserSelector", ()=>{
    beforeEach(()=>moxios.install());
    afterEach(()=>moxios.uninstall());

    test('should render a list of route statuses', (done)=>{
        const onChangedCallback = sinon.spy();
        const rendered = mount(<RouteStatusFilter onChanged={onChangedCallback}/>);

        expect(rendered.instance().state.users).toEqual([]);


    });
});