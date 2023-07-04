import React, {useEffect, useState} from "react";
import {useStatusAlert} from "../../hooks/useStatusAlert";
import API from "../API";
import {handleApiError} from "../utils/utils";
import {Col, Container, Row, Spinner, Table} from "react-bootstrap";
import {Expertise} from "../../enums/Expertise";
import {Level} from "../../enums/Level";

function ManagerDashboardExperts() {
    const [experts, setExperts] = useState(null);
    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();

    useEffect(() => {
        API.getAllExperts()
            .then((response) => {
                    setExperts(response)
                    resetStatusAlert()
                }
            )
            .catch(err => handleApiError(err, showError))
    }, []);

    return (
        <Container className="h-100">
            <Row className="h-100">
                <Col className="d-flex flex-column align-items-center justify-content-center">
                    <StatusAlertComponent/>
                    {
                        experts ?
                            <>
                                <ExpertsTable experts={experts}/>
                            </>
                            : <Spinner animation="border" variant="primary"/>
                    }
                </Col>
            </Row>
        </Container>
    );
}

const ExpertsTable = ({experts}) => {

    return (
        <Table striped bordered hover>
            <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Country</th>
                <th>City</th>
                <th>Skills</th>
            </tr>
            </thead>
            <tbody>
            {experts.map((expert) => (
                <tr key={expert.expertId}>
                    <td>{expert.expertId}</td>
                    <td>{expert.name} {expert.surname}</td>
                    <td>{expert.email}</td>
                    <td>{expert.country}</td>
                    <td>{expert.city}</td>
                    <td>
                        {expert.skills.map((skill, index) => (
                            <tr key={index}>
                                {Expertise[skill.expertise]} {Level[skill.level]}
                            </tr>
                        ))}
                    </td>
                </tr>
            ))}
            </tbody>
        </Table>
    );
};

export default ManagerDashboardExperts;
