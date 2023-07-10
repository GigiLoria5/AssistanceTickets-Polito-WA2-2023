import {Col, Row, Tab, Tabs} from "react-bootstrap";
import ManagerDashboardTickets from "./ManagerDashboardTickets";
import ManagerDashboardExperts from "./ManagerDashboardExperts";
import ManagerDashboardAssignableTickets from "./ManagerDashboardAssignableTickets";
import React from "react";

function ManagerDashboard() {
    return (
        <>
            <Row className='pb-3'>
                <Col className="d-flex align-items-center">
                    <h1>Manager dashboard</h1>
                </Col>
            </Row>
            <Tabs
                defaultActiveKey="assignable-tickets"
                id="uncontrolled-tab-example"
                className="mb-3"
            >
                <Tab eventKey="assignable-tickets" title="Assignable Tickets">
                    <ManagerDashboardAssignableTickets/>
                </Tab>
                <Tab eventKey="tickets" title="All Tickets">
                    <ManagerDashboardTickets/>
                </Tab>
                <Tab eventKey="experts" title="Experts">
                    <ManagerDashboardExperts/>
                </Tab>
            </Tabs>
        </>
    );
}

export default ManagerDashboard;
