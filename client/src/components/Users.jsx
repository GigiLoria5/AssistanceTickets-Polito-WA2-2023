import { Row, Col, Spinner, Button, } from "react-bootstrap";
import SearchUser from "./SearchUser";

function Users(props) {

    return (
        <Row>
            <Col>
                <SearchUser></SearchUser>
            </Col>
            <Col> form to insert user</Col>
        </Row>)
}

export default Users
