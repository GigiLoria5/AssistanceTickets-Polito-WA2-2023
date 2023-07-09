import {Tab, Tabs} from "react-bootstrap";
import ManagerDashboardTickets from "./ManagerDashboardTickets";
import ManagerDashboardExperts from "./ManagerDashboardExperts";
import ManagerDashboardAssignableTickets from "./ManagerDashboardAssignableTickets";

function ManagerDashboard() {
    return (
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
    );
}

export default ManagerDashboard;
