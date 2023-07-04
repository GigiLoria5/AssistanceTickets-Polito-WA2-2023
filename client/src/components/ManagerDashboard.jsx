import {Tab, Tabs} from "react-bootstrap";
import ManagerDashboardTickets from "./ManagerDashboardTickets";
import ManagerDashboardExperts from "./ManagerDashboardExperts";

function ManagerDashboard() {
    return (
        <Tabs
            defaultActiveKey="tickets"
            id="uncontrolled-tab-example"
            className="mb-3"
        >
            <Tab eventKey="tickets" title="Tickets">
                <ManagerDashboardTickets/>
            </Tab>
            <Tab eventKey="experts" title="Experts">
                <ManagerDashboardExperts/>
            </Tab>
        </Tabs>
    );
}

export default ManagerDashboard;
